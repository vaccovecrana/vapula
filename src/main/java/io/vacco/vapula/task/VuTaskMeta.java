package io.vacco.vapula.task;

import io.vacco.vapula.trigger.*;
import java.util.Objects;

public class VuTaskMeta {

  public String id;

  public VuOnce once;
  public VuPeriod period;
  public VuCron cron;

  public String taskClass;
  public String payloadClass;
  public String payloadPacket;

  public VuTaskMeta withId(String id) {
    this.id = Objects.requireNonNull(id);
    return this;
  }

  public VuTaskMeta withOnce(VuOnce once) {
    this.once = Objects.requireNonNull(once);
    this.period = null;
    this.cron = null;
    return this;
  }

  public VuTaskMeta withPeriod(VuPeriod period) {
    this.once = null;
    this.period = Objects.requireNonNull(period);
    this.cron = null;
    return this;
  }

  public VuTaskMeta withCron(VuCron cron) {
    this.once = null;
    this.period = null;
    this.cron = Objects.requireNonNull(cron);
    return this;
  }

  public VuTaskMeta withTaskClass(Class<? extends VuTask<?>> tc) {
    this.taskClass = tc.getCanonicalName();
    return this;
  }

  public VuTaskMeta withPayload(Class<?> pc, String pp) {
    this.payloadClass = Objects.requireNonNull(pc.getCanonicalName());
    this.payloadPacket = Objects.requireNonNull(pp);
    return this;
  }

  public void validate() {
    if (id == null || id.trim().length() == 0) {
      throw new IllegalStateException("Missing task ID");
    }
    if (once == null && period == null && cron == null) {
      throw new IllegalStateException(String.format("Task [%s] has no trigger specification", id));
    }
    if (taskClass == null) {
      throw new IllegalStateException(String.format("Task [%s] has no implementation class defined", id));
    }
    if (payloadClass != null && payloadPacket == null) {
      throw new IllegalStateException(String.format("Task [%s] has payload of type [%s] but is missing payload data", id, payloadClass));
    }
    if (payloadClass == null && payloadPacket != null) {
      throw new IllegalStateException(String.format("Task [%s] has payload data but no payload type", id));
    }
  }

}
