package io.vacco.vapula.store;

import io.vacco.vapula.task.*;
import redis.clients.jedis.*;
import redis.clients.jedis.params.SetParams;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.*;
import java.util.stream.Collectors;

import static io.vacco.vapula.store.VuRedisKeys.*;

public class VuRedis implements VuTaskStore {

  private final List<JedisPool> pools;
  private final String taskGroup;
  private final VuTaskIO.VuJsonIn jsonIn;
  private final VuTaskIO.VuJsonOut jsonOut;

  public VuRedis(VuTaskIO.VuJsonIn jsonIn, VuTaskIO.VuJsonOut jsonOut,
                 String taskGroup, String ... uris) {
    try {
      if (uris == null || uris.length == 0) {
        throw new IllegalArgumentException("Missing Redis connection URI(s)");
      }
      if (taskGroup == null || taskGroup.trim().length() == 0) {
        throw new IllegalArgumentException("Missing task group name");
      }
      this.jsonIn = Objects.requireNonNull(jsonIn);
      this.jsonOut = Objects.requireNonNull(jsonOut);
      this.taskGroup = taskGroup;
      this.pools = new ArrayList<>(uris.length);
      for (String uriSt : uris) {
        URI uri = new URI(uriSt);
        JedisPool pool = new JedisPool(uri, 5000);
        pools.add(pool);
      }
    } catch (Exception e) {
      throw new IllegalStateException(e.getMessage(), e);
    }
  }

  public <T> T mapConn(Function<Jedis, T> fn) {
    ThreadLocalRandom random = ThreadLocalRandom.current();
    int index = random.nextInt(pools.size());
    JedisPool pool = pools.get(index);
    try (Jedis jedis = pool.getResource()) {
      return fn.apply(jedis);
    }
  }

  public void withConn(Consumer<Jedis> c) {
    mapConn(j -> {
      c.accept(j);
      return null;
    });
  }

  public void withPipeline(Consumer<Pipeline> fn) {
    withConn(jedis -> {
      var p = jedis.pipelined();
      p.multi();
      fn.accept(p);
      p.exec();
    });
  }

  @Override public Map<String, VuTaskMeta> index() {
    return mapConn(jedis -> {
      var pfx = taskPrefixKeyOf(taskGroup);
      var keys = jedis.keys(pfx);
      if (!keys.isEmpty()) {
        var metas = jedis.mget(keys.toArray(String[]::new));
        return metas.stream()
            .map(j -> VuTaskIO.read(j, jsonIn))
            .collect(Collectors.toMap(t -> t.id, Function.identity()));
      }
      return Collections.emptyMap();
    });
  }

  @Override public long indexVersion() {
    return mapConn(jedis -> jedis.incrBy(versionKeyOf(taskGroup), 0));
  }

  @Override public void add(VuTaskMeta task) {
    var taskKey = taskKeyOf(taskGroup, task.id);
    withPipeline(p -> {
      p.set(taskKey, jsonOut.toJson(task));
      p.incr(versionKeyOf(taskGroup));
    });
  }

  @Override public void remove(String taskId) {
    var key = taskKeyOf(taskGroup, taskId);
    withPipeline(p -> {
      p.del(key);
      p.incr(versionKeyOf(taskGroup));
    });
  }

  @Override public boolean isDefined(String taskId) {
    var taskKey = taskKeyOf(taskGroup, taskId);
    return mapConn(jedis -> jedis.exists(taskKey));
  }

  @Override public boolean lock(VuTaskMeta task, int lockSeconds) {
    return mapConn(jedis -> {
      var lockKey = taskLockKeyOf(taskGroup, task.id);
      var params = SetParams.setParams().nx().ex(lockSeconds);
      var ok = jedis.set(lockKey, Boolean.TRUE.toString(), params);
      return ok != null;
    });
  }

  public void close() {
    for (JedisPool pool : pools) {
      pool.close();
    }
  }

}
