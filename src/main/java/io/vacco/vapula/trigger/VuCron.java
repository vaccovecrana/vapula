package io.vacco.vapula.trigger;

import java.util.Objects;

public class VuCron {

  public String expression;

  public VuCron withExpression(String expression) {
    this.expression = Objects.requireNonNull(expression);
    return this;
  }

}
