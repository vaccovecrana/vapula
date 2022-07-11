package io.vacco.vapula.trigger;

import java.time.ZonedDateTime;
import java.util.Objects;

public class VuCronInterval {

  public ZonedDateTime nx0, nx1;

  public static VuCronInterval of(ZonedDateTime nx0, ZonedDateTime nx1) {
    var ci = new VuCronInterval();
    ci.nx0 = Objects.requireNonNull(nx0);
    ci.nx1 = Objects.requireNonNull(nx1);
    return ci;
  }

}
