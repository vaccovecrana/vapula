package io.vacco.vapula.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MakerOfOnes extends VuTask<Void> {

  private static final Logger log = LoggerFactory.getLogger(MakerOfOnes.class);

  @Override public void run() {
    log.info("Making a lot of ones...");
  }

}
