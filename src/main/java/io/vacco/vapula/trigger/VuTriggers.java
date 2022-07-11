package io.vacco.vapula.trigger;

import io.vacco.cron.CronExpression;
import io.vacco.vapula.task.VuTaskMeta;
import java.time.temporal.ChronoUnit;

public class VuTriggers {

  public static boolean ready(VuOnce o) {
    return VuUtc.utcNowMs() >= o.startUtcMs;
  }

  public static boolean ready(VuPeriod p) {
    var nowUtcMs = VuUtc.utcNowMs();
    var sLtn = p.startUtcMs <= nowUtcMs;
    var eGtn = p.endUtcMs >= nowUtcMs;
    return sLtn && eGtn;
  }

  public static VuCronInterval cronLengthOf(String expr) {
    var pattern = CronExpression.createWithoutSeconds(expr);
    var nx0 = pattern.nextTimeAfter(VuUtc.utcNow());
    var nx1 = pattern.nextTimeAfter(nx0);
    return VuCronInterval.of(nx0, nx1);
  }

  public static long cronSecondsUntilNext(String expr) {
    var utc = VuUtc.utcNow().truncatedTo(ChronoUnit.MINUTES);
    var nx0 = CronExpression.createWithoutSeconds(expr).nextTimeAfter(utc);
    return VuUtc.dateTimeDifference(utc, nx0, ChronoUnit.SECONDS);
  }

  public static boolean ready(VuCron c) {
    var utcMin = VuUtc.utcNow().truncatedTo(ChronoUnit.MINUTES);
    var cl = cronLengthOf(c.expression);
    var clMins = VuUtc.dateTimeDifference(cl.nx0, cl.nx1, ChronoUnit.MINUTES);
    var diffNowNx0Mins = VuUtc.dateTimeDifference(utcMin, cl.nx0, ChronoUnit.MINUTES);
    return clMins - diffNowNx0Mins == 0;
  }

  public static boolean ready(VuTaskMeta task) {
    if (task.once != null) return ready(task.once);
    if (task.period != null) return ready(task.period);
    return ready(task.cron);
  }

  public static boolean ended(VuPeriod p) {
    return VuUtc.utcNowMs() >= p.endUtcMs;
  }

}
