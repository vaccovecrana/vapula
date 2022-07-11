package io.vacco.vapula;

import com.google.gson.Gson;
import io.vacco.vapula.scheduler.VuScheduler;
import io.vacco.shax.logging.ShOption;
import io.vacco.vapula.store.*;
import io.vacco.vapula.task.*;
import io.vacco.vapula.trigger.*;
import j8spec.annotation.DefinedOrder;
import j8spec.junit.J8SpecRunner;
import org.junit.runner.RunWith;
import org.slf4j.*;
import java.awt.*;
import java.time.temporal.ChronoUnit;

import static j8spec.J8Spec.*;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class VuSchedulerTest {

  static {
    ShOption.setSysProp(ShOption.IO_VACCO_SHAX_DEVMODE, "true");
    ShOption.setSysProp(ShOption.IO_VACCO_SHAX_LOGLEVEL, "debug");
  }

  private static final Logger log = LoggerFactory.getLogger(VuSchedulerTest.class);
  private static final Gson g = new Gson();

  static {
    it("Schedules 3 task types", () -> {
      var taskStore = GraphicsEnvironment.isHeadless()
          ? new VuMemory()
          : new VuRedis(g::fromJson, g::toJson, "vap_test", "redis://localhost:6379/0");
      var scheduler = new VuScheduler(taskStore, g::fromJson);
      var nowUtcMs = System.currentTimeMillis();

      scheduler.add(
          new VuTaskMeta()
              .withId("smidge-counter-0000")
              .withTaskClass(SmidgeCounter.class)
              .withPayload(SmidgeParams.class, g.toJson(new SmidgeParams().withCurrentSmidges(5)))
              .withOnce(new VuOnce().withStartTime(VuUtc.utcNow().plus(50, ChronoUnit.SECONDS)))
      ).add(
          new VuTaskMeta()
              .withId("smidge-counter-0001")
              .withTaskClass(SmidgeCounter.class)
              .withPayload(SmidgeParams.class, g.toJson(new SmidgeParams().withCurrentSmidges(10)))
              .withOnce(new VuOnce().withStartUtcMs(VuUtc.utcNowMs()))
      ).add(
          new VuTaskMeta()
              .withId("maker-ones")
              .withTaskClass(MakerOfOnes.class)
              .withPeriod(
                  new VuPeriod()
                      .withStartTime(VuUtc.utcNow().plus(2, ChronoUnit.SECONDS))
                      .withEndTime(VuUtc.utcNow().plus(20, ChronoUnit.SECONDS))
                      .withSpanSeconds(2)
              )
      ).add(
          new VuTaskMeta()
              .withId("maker-twos")
              .withTaskClass(MakerOfTwos.class)
              .withPeriod(
                  new VuPeriod()
                      .withStartUtcMs(nowUtcMs + 2000)
                      .withEndUtcMs(nowUtcMs + 40000)
                      .withSpanSeconds(4)
              )
      ).add(
          new VuTaskMeta()
              .withId("maker-threes")
              .withTaskClass(MakerOfThrees.class)
              .withCron(new VuCron().withExpression("* * * * ?"))
      );

      scheduler.start();

      Thread.sleep(3 * 60 * 1000);
      log.info("Done");
    });
  }
}
