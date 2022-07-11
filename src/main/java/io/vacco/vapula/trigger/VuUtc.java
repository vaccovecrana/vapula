package io.vacco.vapula.trigger;

import java.time.*;
import java.time.temporal.*;

public class VuUtc {

  public static final ZoneId UTC = ZoneId.of("UTC");

  public static ZonedDateTime utcNow() {
    return ZonedDateTime.now(UTC);
  }

  public static long utcNowMs() {
    return utcNow().toInstant().toEpochMilli();
  }

  public static long dateTimeDifference(Temporal d0, Temporal d1, ChronoUnit unit) {
    return unit.between(d0, d1);
  }

}
