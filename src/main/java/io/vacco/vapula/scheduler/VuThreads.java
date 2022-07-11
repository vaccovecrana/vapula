package io.vacco.vapula.scheduler;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadLocalRandom;

public class VuThreads {

  public static final ThreadFactory schedulerFactory = r -> new Thread(r, String.format(
      "va-scheduler-%s",
      Integer.toHexString(ThreadLocalRandom.current().nextInt())
  ));

  public static final ThreadFactory taskRunnerFactory = r -> new Thread(r, String.format(
      "va-task-runner-%s",
      Integer.toHexString(ThreadLocalRandom.current().nextInt())
  ));

}
