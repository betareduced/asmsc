package lk.mobitel.telco.asmsc.map.constant;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public enum ProviderError {
  MAPPE_duplicated_invoke_id((byte) 1, "duplicated invoke id"),
  MAPPE_not_supported_service((byte) 2, "not supported service"),
  MAPPE_mistyped_parameter((byte) 3, "mistyped parameter"),
  MAPPE_resource_limitation((byte) 4, "resource limitation"),
  MAPPE_initiating_release((byte) 5, "initiating release"),
  MAPPE_unexpected_response_from_peer((byte) 6, "unexpected response from peer"),
  MAPPE_service_completion_failure((byte) 7, "service completion failure"),
  MAPPE_no_response_from_peer((byte) 8, "no response from peer"),
  MAPPE_invalid_response_received((byte) 9, "invalid response received");

  private static final Map<Byte, ProviderError> m = new HashMap<>();

  public static @NotNull ProviderError of(int value) {
    ProviderError ProviderError = m.get((byte) value);
    if (ProviderError == null) {
      throw new NoSuchElementException("No such provider error for value: " + value);
    }
    return ProviderError;
  }

  static {
    for (ProviderError providerError : ProviderError.values()) {
      m.put(providerError.value(), providerError);
    }
  }

  private final byte value;
  private final String desc;

  ProviderError(byte value, String desc) {
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
