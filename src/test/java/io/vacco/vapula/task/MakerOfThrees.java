package io.vacco.vapula.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MakerOfThrees extends VuTask<Void> {

  private static final Logger log = LoggerFactory.getLogger(MakerOfThrees.class);

  @Override public void run() {
    log.info("Making a lot of threes...");
  }

}