package lk.mobitel.telco.asmsc.map.constant;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public enum ApplicationContext {
  CONTEXT_MISSING((byte) 0, (byte) 0),
  SHORTMSG_MTRELAY_CONTEXT((byte) 25, (byte) 3),
  SHORTMSG_GATEWAY_CONTEXT((byte) 20, (byte) 3),
  IMSI_RETRIEVAL_CONTEXT((byte) 26, (byte) 2);

  private static final Map<Integer, ApplicationContext> m = new HashMap<>();

  private static int keyCode(int a, int v) {
    return (a & 0xFF) << 8 | (v & 0xFF);
  }

  public static ApplicationContext of(int mapAc, int version) {
    ApplicationContext ac = m.get(keyCode(mapAc, version));
    if (ac == null) throw new NoSuchElementException("Unsupported application context");
    return ac;
  }

  static {
    for (ApplicationContext ac : ApplicationContext.values()) {
      m.put(keyCode(ac.acv, ac.ver), ac);
    }
  }

  private final byte[] value;
  private final byte acv;
  private final byte ver;

  ApplicationContext(byte mapAc, byte version) {
    acv = mapAc;
    ver = version;
    this.value =
        new byte[] {
          0x06, /* = Object identifier */ 0x07, /* = Length */ 0x04, /* = CCITT */
          0x00, /* = ETSI */ 0x00, /* = Mobile Domain */ 0x01, /* = GSM Network */
          0x00, /* = Application context */ mapAc, /* = ID of application context */
          version, /* = version */
        };
  }

  public ByteBuffer asByteBuffer() {
    return ByteBuffer.wrap(value);
  }
}
