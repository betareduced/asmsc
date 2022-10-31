package lk.mobitel.telco.asmsc.map.constant;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public enum UserReason {
  MAPUR_user_specific((byte) 0, "User specific reason"),
  MAPUR_user_resource_limitation((byte) 1, "User resource limitation"),
  MAPUR_resource_unavail((byte) 2, "resource unavailable"),
  MAPUR_app_proc_cancelled((byte) 3, "application procedure cancelled"),
  MAPUR_procedure_error((byte) 4, "Procedure Error"),
  MAPUR_unspecified_reason((byte) 5, "Unspecified Reason"),
  MAPUR_version_not_supported((byte) 6, "Version Not Supported");

  private static final Map<Byte, UserReason> m = new HashMap<>();

  public static @NotNull UserReason of(int value) {
    UserReason reason = m.get((byte) value);
    if (reason == null) {
      throw new NoSuchElementException("No such user reason for value: " + value);
    }
    return reason;
  }

  static {
    for (UserReason reason : UserReason.values()) {
      m.put(reason.value, reason);
    }
  }

  private final byte value;
  private final String desc;

  UserReason(byte value, String desc) {
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
