package io.vacco.vapula.task;

/**
 * @param <P> a task payload, defines task parameters.
 */
public abstract class VuTask<P> implements Runnable {
  public P payload;
  public VuTaskMeta meta;
}
