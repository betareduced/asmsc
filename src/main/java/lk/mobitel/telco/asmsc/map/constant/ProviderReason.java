package lk.mobitel.telco.asmsc.map.constant;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public enum ProviderReason {
  MAPPR_prov_malfct((byte) 0, "Provider malfunction"),
  MAPPR_dlg_rlsd((byte) 1, "Supporting dialogue/transaction released"),
  MAPPR_rsrc_limit((byte) 2, "Resource limitation"),
  MAPPR_mnt_act((byte) 3, "Maintenance activity"),
  MAPPR_ver_incomp((byte) 4, "version incompatibility"),
  MAPPR_ab_dlg((byte) 5, "Abnormal MAP dialogue"),
  MAPPR_invalid_PDU((byte) 6, "Invalid PDU (obsolete - no longer used)"),
  MAPPR_idle_timeout((byte) 7, "Idle Timeout");

  private static final Map<Byte, ProviderReason> m = new HashMap<>();

  public static @NotNull ProviderReason of(int value) {
    ProviderReason reason = m.get((byte) value);
    if (reason == null) {
      throw new NoSuchElementException("No such provider reason for value: " + value);
    }
    return reason;
  }

  static {
    for (ProviderReason reason : ProviderReason.values()) {
      m.put(reason.value, reason);
    }
  }

  private final byte value;
  private final String desc;

  ProviderReason(byte value, String desc) {
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
