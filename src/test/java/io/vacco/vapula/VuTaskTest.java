package io.vacco.vapula;

import com.google.gson.*;
import io.vacco.shax.logging.ShOption;
import io.vacco.vapula.task.*;
import io.vacco.vapula.trigger.*;
import j8spec.annotation.DefinedOrder;
import j8spec.junit.J8SpecRunner;
import org.junit.runner.RunWith;
import org.slf4j.*;

import static j8spec.J8Spec.*;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class VuTaskTest {

  static { ShOption.setSysProp(ShOption.IO_VACCO_SHAX_DEVMODE, "true"); }

  private static final Logger log = LoggerFactory.getLogger(VuTaskTest.class);
  private static final Gson g = new GsonBuilder().setPrettyPrinting().create();

  static {
    it("Loads task metadata", () -> {
      var nowUtc = System.currentTimeMillis();
      var nowUtcPlus30Sec = nowUtc + (30 * 1000);

      var vp = new SmidgeParams();
      vp.currentSmidges = 5;

      var vm = new VuTaskMeta()
          .withId("smidge-counter-ABCDEF0123")
          .withPeriod(new VuPeriod().withStartUtcMs(nowUtc).withEndUtcMs(nowUtcPlus30Sec).withSpanSeconds(10))
          .withTaskClass(SmidgeCounter.class)
          .withPayload(SmidgeParams.class, g.toJson(vp));

      var metaPacket = VuTaskIO.write(vm, g::toJson);
      var vm0 = VuTaskIO.read(metaPacket, g::fromJson);
      var t = VuTaskIO.newInstance(vm0, g::fromJson);

      t.run();
      log.info(g.toJson(vm0));
    });
  }
}
