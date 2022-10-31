package lk.mobitel.telco.asmsc.map.constant;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public enum ProblemDiagnostic {
  MAPPD_abnormal_event_detected_by_peer((byte) 0, "Abnormal event detected by peer"),
  MAPPD_response_rejected_by_peer((byte) 1, "Response rejected by peer"),
  MAPPD_abnormal_event_rx_from_peer((byte) 2, "Abnormal event received from peer"),
  MAPPD_message_not_delivered((byte) 3, "Message not delivered");

  private static final Map<Byte, ProblemDiagnostic> m = new HashMap<>();

  public static @NotNull ProblemDiagnostic of(int value) throws NoSuchElementException {
    ProblemDiagnostic p = m.get((byte) value);
    if (p == null) {
      throw new NoSuchElementException("No such problem diagnostic for value: " + value);
    }
    return p;
  }

  static {
    for (ProblemDiagnostic problemDiagnostic : ProblemDiagnostic.values()) {
      m.put(problemDiagnostic.value(), problemDiagnostic);
    }
  }

  private final byte value;
  private final String desc;

  ProblemDiagnostic(byte value, String desc) {
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
