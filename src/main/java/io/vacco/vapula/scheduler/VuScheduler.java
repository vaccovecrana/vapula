package io.vacco.vapula.scheduler;

import io.vacco.vapula.store.VuTaskStore;
import io.vacco.vapula.task.*;
import io.vacco.vapula.trigger.VuTriggers;
import org.slf4j.*;
import java.util.*;
import java.util.concurrent.*;

public class VuScheduler {

  private static final Logger log = LoggerFactory.getLogger(VuScheduler.class);

  private final VuTaskStore taskStore;

  private Map<String, VuTaskMeta> index = new HashMap<>();
  private long indexVersion = -1;

  private final VuTaskIO.VuJsonIn jsonIn;
  private final ScheduledExecutorService scheduler;
  private final ExecutorService executor;

  public VuScheduler(VuTaskStore taskStore, VuTaskIO.VuJsonIn in, int threads) {
    this.jsonIn = Objects.requireNonNull(in);
    this.taskStore = Objects.requireNonNull(taskStore);
    this.scheduler = Executors.newSingleThreadScheduledExecutor(VuThreads.schedulerFactory);
    this.executor = Executors.newFixedThreadPool(threads, VuThreads.taskRunnerFactory);
  }

  public VuScheduler(VuTaskStore taskStore, VuTaskIO.VuJsonIn in) {
    this(taskStore, in, Runtime.getRuntime().availableProcessors() * 2);
  }

  public VuScheduler add(VuTaskMeta taskMeta) {
    taskMeta.validate();
    if (taskStore.isDefined(taskMeta.id)) {
      log.warn("Overwriting definition of task [{}].", taskMeta.id);
    }
    taskStore.add(taskMeta);
    return this;
  }

  private int lockTimeOf(VuTaskMeta tm) {
    if (tm.once != null) {
      return 5;
    } else if (tm.period != null) {
      return tm.period.spanSeconds;
    }
    return (int) VuTriggers.cronSecondsUntilNext(tm.cron.expression);
  }

  public void start() {
    this.scheduler.scheduleWithFixedDelay(() -> {
      if (log.isDebugEnabled()) {
        log.trace("Tick...");
      }
      try {
        var remoteVersion = taskStore.indexVersion();

        if (remoteVersion != indexVersion) {
          this.indexVersion = remoteVersion;
          this.index = taskStore.index();
          log.info("New task list version [{}] with [{}] tasks", remoteVersion, index.size());
        }

        for (VuTaskMeta tm : index.values()) {
          if (VuTriggers.ready(tm)) {
            if (taskStore.lock(tm, lockTimeOf(tm))) {
              log.info("Running: [{}]", tm.id);
              var t = VuTaskIO.newInstance(tm, jsonIn);
              if (tm.once != null || (tm.period != null && VuTriggers.ended(tm.period))) {
                taskStore.remove(tm.id);
              }
              executor.submit(t);
            }
          }
        }
      } catch (Exception e) {
        log.error("Task reloading error", e);
      }
    }, 0, 500, TimeUnit.MILLISECONDS);
  }

}
