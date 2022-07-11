package io.vacco.vapula.store;

public class VuRedisKeys {

  public static final String Tasks = "tasks";
  public static final String Version = "version";
  public static final String Lock = "lock";

  public static String keyOf2(String arg0, String arg1) {
    return String.format("%s-%s", arg0, arg1);
  }

  public static String keyOf3(String arg0, String arg1, String arg2) {
    return String.format("%s-%s-%s", arg0, arg1, arg2);
  }

  public static String versionKeyOf(String taskGroup) {
    return keyOf2(taskGroup, Version);
  }

  public static String taskKeyOf(String taskGroup, String taskId) {
    return keyOf3(taskGroup,Tasks , taskId);
  }

  public static String taskPrefixKeyOf(String taskGroup) {
    return keyOf3(taskGroup, Tasks, "*");
  }

  public static String taskLockKeyOf(String taskGroup, String taskId) {
    return keyOf3(taskGroup, taskId, Lock);
  }

}
