package io.vacco.vapula.store;

import io.vacco.vapula.task.VuTaskMeta;
import java.io.Closeable;
import java.util.Map;

public interface VuTaskStore extends Closeable {

  Map<String, VuTaskMeta> index();
  long indexVersion();

  void add(VuTaskMeta task);
  void remove(String taskId);
  boolean isDefined(String taskId);
  boolean lock(VuTaskMeta task, int lockSeconds);

}
