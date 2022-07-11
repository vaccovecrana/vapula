package io.vacco.vapula.trigger;

import java.time.ZonedDateTime;

public class VuOnce {

  public long startUtcMs;

  public VuOnce withStartUtcMs(long startUtcMs) {
    this.startUtcMs = startUtcMs;
    return this;
  }

  public VuOnce withStartTime(ZonedDateTime t) {
    this.startUtcMs = t.toInstant().toEpochMilli();
    return this;
  }

}
