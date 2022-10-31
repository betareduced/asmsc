package lk.mobitel.telco.asmsc.map.constant;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public enum UserError {
  MAPUE_unknown_subscriber((byte) 1, "unknown subscriber"),
  MAPUE_unknown_MSC((byte) 3, "unknown MSC"),
  MAPUE_unidentified_subscriber((byte) 5, "unidentified subscriber"),
  MAPUE_absentsubscriber_SM((byte) 6, "absent subscriber SM"),
  MAPUE_unknown_equipment((byte) 7, "unknown equipment"),
  MAPUE_roaming_not_allowed((byte) 8, "roaming not allowed"),
  MAPUE_illegal_subscriber((byte) 9, "illegal subscriber"),
  MAPUE_bearer_service_not_provisioned((byte) 10, "bearer service not provisioned"),
  MAPUE_teleservice_not_provisioned((byte) 11, "teleservice not provisioned"),
  MAPUE_illegal_equipment((byte) 12, "illegal equipment"),
  MAPUE_call_barred((byte) 13, "call barred"),
  MAPUE_forwarding_violation((byte) 14, "forwarding violation"),
  MAPUE_cug_reject((byte) 15, "cug reject"),
  MAPUE_illegal_ss_operation((byte) 16, "illegal ss operation"),
  MAPUE_ss_error_status((byte) 17, "ss error status"),
  MAPUE_ss_not_available((byte) 18, "ss not available"),
  MAPUE_ss_subscription_violation((byte) 19, "ss subscription violation"),
  MAPUE_ss_incompatibility((byte) 20, "ss incompatibility"),
  MAPUE_facility_not_supported((byte) 21, "facility not supported"),
  MAPUE_pw_registration_failure((byte) 23, "pw registration failure"),
  MAPUE_negative_pw_check((byte) 24, "negative pw check"),
  MAPUE_no_handover_number_available((byte) 25, "no handover number available"),
  MAPUE_subsequent_handover_failure((byte) 26, "subsequent handover failure"),
  MAPUE_absent_subscriber((byte) 27, "absent subscriber"),
  MAPUE_subscriber_busy_for_MT_SMS((byte) 31, "subscriber busy for MT SMS"),
  MAPUE_SM_delivery_failure((byte) 32, "SM delivery failure"),
  MAPUE_message_waiting_list_full((byte) 33, "message waiting list full"),
  MAPUE_system_failure((byte) 34, "system failure"),
  MAPUE_data_missing((byte) 35, "data missing"),
  MAPUE_unexpected_data_value((byte) 36, "unexpected data value"),
  MAPUE_resource_limitation((byte) 37, "resource limitation"),
  MAPUE_initiating_release((byte) 38, "initiating release"),
  MAPUE_no_roaming_number_available((byte) 39, "no roaming number available"),
  MAPUE_tracing_buffer_full((byte) 40, "tracing buffer full"),
  MAPUE_number_of_pw_attempts_violation((byte) 43, "number of pw attempts violation"),
  MAPUE_number_changed((byte) 44, "number changed"),
  MAPUE_busy_subscriber((byte) 45, "busy subscriber"),
  MAPUE_no_subscriber_reply((byte) 46, "no subscriber reply"),
  MAPUE_forwarding_failed((byte) 47, "forwarding failed"),
  MAPUE_or_not_allowed((byte) 48, "or not allowed"),
  MAPUE_ATI_not_allowed((byte) 49, "ATI not allowed"),
  MAPUE_unauthorised_requesting_network((byte) 52, "unauthorised requesting network"),
  MAPUE_unauthorised_LCS_client((byte) 53, "unauthorised LCS client"),
  MAPUE_position_method_failure((byte) 54, "position method failure"),
  MAPUE_unknown_or_unreachable_LCS_client((byte) 58, "unknown or unreachable LCS client"),
  MAPUE_mm_event_not_supported((byte) 59, "mm event not supported"),
  MAPUE_atsi_not_allowed((byte) 60, "atsi not allowed"),
  MAPUE_atm_not_allowed((byte) 61, "atm not allowed"),
  MAPUE_information_not_available((byte) 62, "information not available"),
  MAPUE_unknown_alphabet((byte) 71, "unknown alphabet"),
  MAPUE_ussd_busy((byte) 72, "ussd busy");

  private static final Map<Byte, UserError> m = new HashMap<>();

  public static @NotNull UserError of(int value) {
    UserError userError = m.get((byte) value);
    if (userError == null) {
      throw new NoSuchElementException("No such user error for value: " + value);
    }
    return userError;
  }

  static {
    for (UserError userError : UserError.values()) {
      m.put(userError.value(), userError);
    }
  }

  private final byte value;
  private final String desc;

  UserError(byte value, String desc) {
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
