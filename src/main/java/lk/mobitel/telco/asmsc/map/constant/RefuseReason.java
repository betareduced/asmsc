package lk.mobitel.telco.asmsc.map.constant;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public enum RefuseReason {
  MAPRR_no_reason((byte) 0, "No reason given"),
  MAPRR_inv_dest_ref((byte) 1, "Invalid destination reference"),
  MAPRR_inv_orig_ref((byte) 2, "Invalid origination reference"),
  MAPRR_appl_context((byte) 3, "Application context not supported"),
  MAPRR_ver_incomp((byte) 4, "Potential version incompatibility"),
  MAPRR_node_notreach((byte) 5, "Remote node not reachable");

  private static final Map<Byte, RefuseReason> m = new HashMap<>();

  public static RefuseReason of(int value) {
    RefuseReason reason = m.get((byte) value);
    if (reason == null) {
      throw new NoSuchElementException("No Refuse reason for value: " + value);
    }
    return reason;
  }

  static {
    for (RefuseReason value : RefuseReason.values()) {
      m.put(value.value(), value);
    }
  }

  private final byte value;
  private final String desc;

  RefuseReason(byte value, String desc) {
    this.value = value;
    this.desc = desc;
  }

  public byte value() {
    return value;
  }

  public String description() {
    return desc;
  }
}
