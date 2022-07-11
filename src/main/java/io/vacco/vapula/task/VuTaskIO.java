package io.vacco.vapula.task;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;

public class VuTaskIO {

  public interface VuJsonIn {
    <T> T fromJson(Reader r, Type knownType);

    default <T> T fromJson(String s, Type knownType) {
      return fromJson(new StringReader(s), knownType);
    }

    default <T> T fromJson(InputStream in, Type knownType) {
      return fromJson(new BufferedReader(new InputStreamReader(in)), knownType);
    }

    default <T> T fromJson(URL url, Type knownType) throws IOException {
      return fromJson(url.openStream(), knownType);
    }
  }

  public interface VuJsonOut {
    <T> String toJson(T t);
  }

  public static String write(VuTaskMeta m, VuJsonOut out) {
    return out.toJson(m);
  }

  public static VuTaskMeta read(String metaPacket, VuJsonIn in) {
    return in.fromJson(metaPacket, VuTaskMeta.class);
  }

  public static VuTask<?> newInstance(VuTaskMeta tm, VuJsonIn in) {
    try {
      var vt0 = Class.forName(tm.taskClass).getConstructor().newInstance();
      if (tm.payloadClass != null) {
        var vp0 = in.fromJson(tm.payloadPacket, Class.forName(tm.payloadClass));
        vt0.getClass().getField("payload").set(vt0, vp0);
      }
      vt0.getClass().getField("meta").set(vt0, tm);
      return (VuTask<?>) vt0;
    } catch (Exception e) {
      throw new IllegalStateException(e.getMessage(), e);
    }
  }

}
