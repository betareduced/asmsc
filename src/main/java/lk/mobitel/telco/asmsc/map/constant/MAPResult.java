package lk.mobitel.telco.asmsc.map.constant;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public enum MAPResult {
  MAPRS_DLG_ACC(0), /* Dialogue accepted */
  MAPRS_DLG_REF(1), /* Dialogue refused */
  MAPRS_DLG_ERR(2); /* NON-STANDARD: Failed to open dialogue */

  private static final Map<Integer, MAPResult> m = new HashMap<>();

  public static @NotNull MAPResult of(int value) throws NoSuchElementException {
    MAPResult result = m.get(value);
    if (result == null) {
      throw new NoSuchElementException("No MAP-Result for value: " + value);
    }
    return result;
  }

  static {
    for (MAPResult value : MAPResult.values()) {
      m.put(value.value(), value);
    }
  }

  private final int value;

  MAPResult(int value) {
    this.value = value;
  }

  public int value() {
    return value;
  }
}
