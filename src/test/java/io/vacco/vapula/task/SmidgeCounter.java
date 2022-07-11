package io.vacco.vapula.task;

import org.slf4j.*;

public class SmidgeCounter extends VuTask<SmidgeParams> {

  private static final Logger log = LoggerFactory.getLogger(SmidgeCounter.class);

  @Override public void run() {
    log.info("Counting them smidges...");
    for (int i = 0; i < payload.currentSmidges; i++) {
      log.info("Smidge [{}]", i);
    }
    log.info("Done counting them smidges...");
  }
}
