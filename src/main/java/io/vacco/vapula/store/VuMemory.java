package io.vacco.vapula.store;

import io.vacco.vapula.task.VuTaskMeta;
import io.vacco.vapula.trigger.VuUtc;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class VuMemory implements VuTaskStore {

  private final ReentrantLock taskUtcIdxLock = new ReentrantLock();
  private final Map<String, Long> taskUtcIdx = new TreeMap<>();
  private final Map<String, VuTaskMeta> tasks = new ConcurrentHashMap<>();

  private long version = 0;

  @Override public Map<String, VuTaskMeta> index() {
    return tasks;
  }

  @Override public long indexVersion() {
    return version;
  }

  @Override public void add(VuTaskMeta task) {
    tasks.put(task.id, task);
    this.version = version + 1;
  }

  @Override public void remove(String taskId) {
    tasks.remove(taskId);
    this.version = version + 1;
  }

  @Override public boolean isDefined(String taskId) {
    return tasks.containsKey(taskId);
  }

  @Override public boolean lock(VuTaskMeta task, int lockSeconds) {
    taskUtcIdxLock.lock();
    try {
      var nowUtc = VuUtc.utcNow();
      var releaseUtcMs = taskUtcIdx.get(task.id);
      if (releaseUtcMs == null || releaseUtcMs <= nowUtc.toInstant().toEpochMilli()) {
        taskUtcIdx.put(task.id, nowUtc.plus(lockSeconds, ChronoUnit.SECONDS).toInstant().toEpochMilli());
        return true;
      }
      return false;
    } finally {
      taskUtcIdxLock.unlock();
    }
  }

  @Override public void close() {
    tasks.clear();
  }
}
