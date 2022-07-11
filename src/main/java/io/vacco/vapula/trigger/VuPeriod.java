package io.vacco.vapula.trigger;

import java.time.ZonedDateTime;

public class VuPeriod {

  public long startUtcMs, endUtcMs;
  public int spanSeconds;

  public VuPeriod withStartUtcMs(long startUtcMs) {
    this.startUtcMs = startUtcMs;
    return this;
  }

  public VuPeriod withStartTime(ZonedDateTime t) {
    this.startUtcMs = t.toInstant().toEpochMilli();
    return this;
  }

  public VuPeriod withEndUtcMs(long endUtcMs) {
    this.endUtcMs = endUtcMs;
    return this;
  }

  public VuPeriod withEndTime(ZonedDateTime t) {
    this.endUtcMs = t.toInstant().toEpochMilli();
    return this;
  }

  public VuPeriod withSpanSeconds(int spanSeconds) {
    this.spanSeconds = spanSeconds;
    return this;
  }

}
