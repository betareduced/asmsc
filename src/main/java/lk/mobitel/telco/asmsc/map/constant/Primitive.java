package lk.mobitel.telco.asmsc.map.constant;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Declares the set of MAP Dialogue Primitives Types. This is as declared in <code>map_inc.h</code>
 *
 * @author NISALBA
 */
public enum Primitive {
  MAP_NULL_PRIMITIVE(0), /* Sentinel Value */

  // DIALOGUE PRIMITIVES

  MAPDT_OPEN_REQ(1, true, 130),
  MAPDT_OPEN_IND(2, true, 129),
  MAPDT_CLOSE_REQ(3, true, 0),
  MAPDT_CLOSE_IND(4, true, 0),
  MAPDT_DELIMITER_REQ(5, true, 0),
  MAPDT_DELIMITER_IND(6, true, 0),
  MAPDT_U_ABORT_REQ(7, true, 0),
  MAPDT_U_ABORT_IND(8, true, 0),
  MAPDT_P_ABORT_IND(9, true, 0),
  MAPDT_NOTICE_IND(10, true, 0),
  MAPDT_MAX_DLG_REQ_PRIM(-10, true, 0), /* Max dlg req primitive value */

  MAPDT_OPEN_RSP(129, true, 0),
  MAPDT_OPEN_CNF(130, true, 0),
  MAPDT_MAX_DLG_ACK_PRIM(-130, true, 0), /* Max dlg ack primitive value */

  // SERVICE PRIMITIVES

  MAPST_SND_RTISM_REQ(1, 130),
  MAPST_SND_RTISM_CNF(130, 0),
  MAPST_SND_RTISM_IND(2, 129),
  MAPST_SND_RTISM_RSP(129, 0),

  //MAPST_FWD_SM_REQ(3),
  //MAPST_FWD_SM_IND(4),
  //MAPST_FWD_SM_RSP(131),
  //MAPST_FWD_SM_CNF(132),

  MAPST_MO_FWD_SM_REQ(3, 131),
  MAPST_MO_FWD_SM_CNF(132, 0),
  MAPST_MO_FWD_SM_IND(4, 132),
  MAPST_MO_FWD_SM_RSP(131, 0),

  MAPST_SM_DELIV_STATUS_REQ(5, 134),
  MAPST_SM_DELIV_STATUS_CNF(134, 0),
  MAPST_SM_DELIV_STATUS_IND(6, 133),
  MAPST_SM_DELIV_STATUS_RSP(133, 0),

  MAPST_READY_FOR_SM_REQ(7, 136),
  MAPST_READY_FOR_SM_CNF(136, 0),
  MAPST_READY_FOR_SM_IND(8, 135),
  MAPST_READY_FOR_SM_RSP(135, 0),

  MAPST_ALERT_SRV_CENTRE_REQ(9, 138),
  MAPST_ALERT_SRV_CENTRE_CNF(138, 0),
  MAPST_ALERT_SRV_CENTRE_IND(10, 137),
  MAPST_ALERT_SRV_CENTRE_RSP(137, 0),

  MAPST_INFORM_SRV_CENTRE_REQ(11),
  MAPST_INFORM_SRV_CENTRE_IND(12),

  MAPST_SEND_INFO_FOR_MT_SMS_REQ(13, 140),
  MAPST_SEND_INFO_FOR_MT_SMS_CNF(140, 0),
  MAPST_SEND_INFO_FOR_MT_SMS_IND(14, 139),
  MAPST_SEND_INFO_FOR_MT_SMS_RSP(139, 0),

  MAPST_SEND_INFO_FOR_MO_SMS_REQ(15, 142),
  MAPST_SEND_INFO_FOR_MO_SMS_CNF(142, 0),
  MAPST_SEND_INFO_FOR_MO_SMS_IND(16, 141),
  MAPST_SEND_INFO_FOR_MO_SMS_RSP(141, 0),

  MAPST_ALERT_SC_WO_RESULT_REQ(17),
  MAPST_ALERT_SC_WO_RESULT_IND(18),

  MAPST_NOTE_SUB_PRESENT_REQ(19),
  MAPST_NOTE_SUB_PRESENT_IND(20),

  MAPST_SND_RI_REQ(21, 144),
  MAPST_SND_RI_CNF(144, 0),
  MAPST_SND_RI_IND(22, 143),
  MAPST_SND_RI_RSP(143, 0),

  MAPST_UNSTR_SS_REQ_REQ(23, 146),
  MAPST_UNSTR_SS_REQ_CNF(146, 0),
  MAPST_UNSTR_SS_REQ_IND(24, 145),
  MAPST_UNSTR_SS_REQ_RSP(145, 0),

  MAPST_UNSTR_SS_NOTIFY_REQ(25, 148),
  MAPST_UNSTR_SS_NOTIFY_CNF(148, 0),
  MAPST_UNSTR_SS_NOTIFY_IND(26, 147),
  MAPST_UNSTR_SS_NOTIFY_RSP(147, 0),

  MAPST_PRO_UNSTR_SS_REQ_REQ(27, 150),
  MAPST_PRO_UNSTR_SS_REQ_CNF(150, 0),
  MAPST_PRO_UNSTR_SS_REQ_IND(28, 149),
  MAPST_PRO_UNSTR_SS_REQ_RSP(149, 0),

  MAPST_ANYTIME_INT_REQ(29, 152),
  MAPST_ANYTIME_INT_CNF(152, 0),
  MAPST_ANYTIME_INT_IND(30, 151),
  MAPST_ANYTIME_INT_RSP(151, 0),

  MAPST_PROV_SUB_INFO_REQ(31, 154),
  MAPST_PROV_SUB_INFO_CNF(154, 0),
  MAPST_PROV_SUB_INFO_IND(32, 153),
  MAPST_PROV_SUB_INFO_RSP(153, 0),

  MAPST_SEND_IMSI_REQ(33, 156),
  MAPST_SEND_IMSI_CNF(156, 0),
  MAPST_SEND_IMSI_IND(34, 155),
  MAPST_SEND_IMSI_RSP(155, 0),

  MAPST_SND_RTIGPRS_REQ(35, 158),
  MAPST_SND_RTIGPRS_CNF(158, 0),
  MAPST_SND_RTIGPRS_IND(36, 157),
  MAPST_SND_RTIGPRS_RSP(157, 0),

  MAPST_PROV_ROAM_NUM_REQ(37, 160),
  MAPST_PROV_ROAM_NUM_CNF(160, 0),
  MAPST_PROV_ROAM_NUM_IND(38, 159),
  MAPST_PROV_ROAM_NUM_RSP(159, 0),

  MAPST_UPDATE_LOCATION_REQ(39, 162),
  MAPST_UPDATE_LOCATION_CNF(162, 0),
  MAPST_UPDATE_LOCATION_IND(40, 161),
  MAPST_UPDATE_LOCATION_RSP(161, 0),

  MAPST_CANCEL_LOCATION_REQ(41, 164),
  MAPST_CANCEL_LOCATION_CNF(164, 0),
  MAPST_CANCEL_LOCATION_IND(42, 163),
  MAPST_CANCEL_LOCATION_RSP(163, 0),

  MAPST_INS_SUBS_DATA_REQ(43, 166),
  MAPST_INS_SUBS_DATA_CNF(166, 0),
  MAPST_INS_SUBS_DATA_IND(44, 165),
  MAPST_INS_SUBS_DATA_RSP(165, 0),

  MAPST_ACTIV_TRACE_MODE_REQ(45, 168),
  MAPST_ACTIV_TRACE_MODE_CNF(168, 0),
  MAPST_ACTIV_TRACE_MODE_IND(46, 167),
  MAPST_ACTIV_TRACE_MODE_RSP(167, 0),

  MAPST_SEND_IDENT_REQ(47, 170),
  MAPST_SEND_IDENT_CNF(170, 0),
  MAPST_SEND_IDENT_IND(48, 169),
  MAPST_SEND_IDENT_RSP(169, 0),

  MAPST_DEL_SUBS_DATA_REQ(49, 172),
  MAPST_DEL_SUBS_DATA_CNF(172, 0),
  MAPST_DEL_SUBS_DATA_IND(50, 171),
  MAPST_DEL_SUBS_DATA_RSP(171, 0),

  MAPST_NOTE_MS_PRES_GPRS_REQ(51, 174),
  MAPST_NOTE_MS_PRES_GPRS_CNF(174, 0),
  MAPST_NOTE_MS_PRES_GPRS_IND(52, 173),
  MAPST_NOTE_MS_PRES_GPRS_RSP(173, 0),

  MAPST_FAIL_REPORT_REQ(53, 176),
  MAPST_FAIL_REPORT_CNF(176, 0),
  MAPST_FAIL_REPORT_IND(54, 175),
  MAPST_FAIL_REPORT_RSP(175, 0),

  MAPST_UPDATE_GPRS_LOC_REQ(55, 178),
  MAPST_UPDATE_GPRS_LOC_CNF(178, 0),
  MAPST_UPDATE_GPRS_LOC_IND(56, 177),
  MAPST_UPDATE_GPRS_LOC_RSP(177, 0),

  MAPST_PURGE_MS_REQ(57, 180),
  MAPST_PURGE_MS_CNF(180, 0),
  MAPST_PURGE_MS_IND(58, 179),
  MAPST_PURGE_MS_RSP(179, 0),

  MAPST_ROUT_INFO_LCS_REQ(59, 182),
  MAPST_ROUT_INFO_LCS_CNF(182, 0),
  MAPST_ROUT_INFO_LCS_IND(60, 181),
  MAPST_ROUT_INFO_LCS_RSP(181, 0),

  MAPST_PROVIDE_SUBS_LOC_REQ(61, 184),
  MAPST_PROVIDE_SUBS_LOC_CNF(184, 0),
  MAPST_PROVIDE_SUBS_LOC_IND(62, 183),
  MAPST_PROVIDE_SUBS_LOC_RSP(183, 0),

  MAPST_SUBS_LOC_REPORT_REQ(63, 186),
  MAPST_SUBS_LOC_REPORT_CNF(186, 0),
  MAPST_SUBS_LOC_REPORT_IND(64, 185),
  MAPST_SUBS_LOC_REPORT_RSP(185, 0),

  MAPST_NOTE_MM_EVENT_REQ(65, 188),
  MAPST_NOTE_MM_EVENT_CNF(188, 0),
  MAPST_NOTE_MM_EVENT_IND(66, 187),
  MAPST_NOTE_MM_EVENT_RSP(187, 0),

  MAPST_FWD_CHK_SS_INDICAT_REQ(67, 190),
  MAPST_FWD_CHK_SS_INDICAT_CNF(190, 0),
  MAPST_FWD_CHK_SS_INDICAT_IND(68, 189),
  MAPST_FWD_CHK_SS_INDICAT_RSP(189, 0),

  MAPST_MT_FWD_SM_REQ(69, 192),
  MAPST_MT_FWD_SM_CNF(192, 0),
  MAPST_MT_FWD_SM_IND(70, 191),
  MAPST_MT_FWD_SM_RSP(191, 0),

  MAPST_SEND_AUTH_INFO_REQ(71, 194),
  MAPST_SEND_AUTH_INFO_CNF(194, 0),
  MAPST_SEND_AUTH_INFO_IND(72, 193),
  MAPST_SEND_AUTH_INFO_RSP(193, 0),

  MAPST_INTERROGATE_SS_REQ(73, 196),
  MAPST_INTERROGATE_SS_CNF(196, 0),
  MAPST_INTERROGATE_SS_IND(74, 195),
  MAPST_INTERROGATE_SS_RSP(195, 0),

  MAPST_ACTIVATE_SS_REQ(75, 198),
  MAPST_ACTIVATE_SS_CNF(198, 0),
  MAPST_ACTIVATE_SS_IND(76, 197),
  MAPST_ACTIVATE_SS_RSP(197, 0),

  MAPST_DEACTIVATE_SS_REQ(77, 200),
  MAPST_DEACTIVATE_SS_CNF(200, 0),
  MAPST_DEACTIVATE_SS_IND(78, 199),
  MAPST_DEACTIVATE_SS_RSP(199, 0),

  MAPST_CHECK_IMEI_REQ(79, 202),
  MAPST_CHECK_IMEI_CNF(202, 0),
  MAPST_CHECK_IMEI_IND(80, 201),
  MAPST_CHECK_IMEI_RSP(201, 0),

  MAPST_ATSI_REQ(81, 204),
  MAPST_ATSI_CNF(204, 0),
  MAPST_ATSI_IND(82, 203),
  MAPST_ATSI_RSP(203, 0),

  MAPST_ERASE_SS_REQ(83, 206),
  MAPST_ERASE_SS_CNF(206, 0),
  MAPST_ERASE_SS_IND(84, 205),
  MAPST_ERASE_SS_RSP(205, 0),

  MAPST_REGISTER_SS_REQ(85, 208),
  MAPST_REGISTER_SS_CNF(208, 0),
  MAPST_REGISTER_SS_IND(86, 207),
  MAPST_REGISTER_SS_RSP(207, 0),

  MAPST_RES_CALL_REQ(87, 210),
  MAPST_RES_CALL_CNF(210, 0),
  MAPST_RES_CALL_IND(88, 209),
  MAPST_RES_CALL_RSP(209, 0),

  MAPST_GET_PASSWORD_REQ(89, 212),
  MAPST_GET_PASSWORD_CNF(212, 0),
  MAPST_GET_PASSWORD_IND(90, 211),
  MAPST_GET_PASSWORD_RSP(211, 0),

  MAPST_REGISTER_PASSWORD_REQ(91, 214),
  MAPST_REGISTER_PASSWORD_CNF(214, 0),
  MAPST_REGISTER_PASSWORD_IND(92, 213),
  MAPST_REGISTER_PASSWORD_RSP(213, 0),

  MAPST_RESTORE_DATA_REQ(93, 216),
  MAPST_RESTORE_DATA_CNF(216, 0),
  MAPST_RESTORE_DATA_IND(94, 215),
  MAPST_RESTORE_DATA_RSP(215, 0),

  MAPST_SEND_PARAMETERS_REQ(95, 218),
  MAPST_SEND_PARAMETERS_CNF(218, 0),
  MAPST_SEND_PARAMETERS_IND(96, 217),
  MAPST_SEND_PARAMETERS_RSP(217, 0),

  MAPST_DEACTIV_TRACE_MODE_REQ(97, 220),
  MAPST_DEACTIV_TRACE_MODE_CNF(220, 0),
  MAPST_DEACTIV_TRACE_MODE_IND(98, 219),
  MAPST_DEACTIV_TRACE_MODE_RSP(219, 0),

  MAPST_TRACE_SUB_ACTIV_REQ(99),
  MAPST_TRACE_SUB_ACTIV_IND(100),

  MAPST_FWDACCESS_SIG_REQ(101),
  MAPST_FWDACCESS_SIG_IND(102),

  MAPST_PREPAREHO_REQ(103, 222),
  MAPST_PREPAREHO_CNF(222, 0),
  MAPST_PREPAREHO_IND(104, 221),
  MAPST_PREPAREHO_RSP(221, 0),

  MAPST_PREPARESUBHO_REQ(105, 224),
  MAPST_PREPARESUBHO_CNF(224, 0),
  MAPST_PREPARESUBHO_IND(106, 223),
  MAPST_PREPARESUBHO_RSP(223, 0),

  MAPST_PROACCESS_SIG_REQ(107),
  MAPST_PROACCESS_SIG_IND(108),

  MAPST_SEND_ENDSIG_REQ(109, 226),
  MAPST_SEND_ENDSIG_CNF(226, 0),
  MAPST_SEND_ENDSIG_IND(110, 225),
  MAPST_SEND_ENDSIG_RSP(225, 0),

  MAPST_AUTHFAILREPORT_REQ(111, 228),
  MAPST_AUTHFAILREPORT_CNF(228, 0),
  MAPST_AUTHFAILREPORT_IND(112, 227),
  MAPST_AUTHFAILREPORT_RSP(227, 0),

  MAPST_EXT_BEGIN_SUB_ACTIV_REQ(113, 230),
  MAPST_EXT_BEGIN_SUB_ACTIV_CNF(230, 0),
  MAPST_EXT_BEGIN_SUB_ACTIV_IND(114, 229),
  MAPST_EXT_BEGIN_SUB_ACTIV_RSP(229, 0),

  MAPST_ANYTIME_MOD_REQ(115, 232),
  MAPST_ANYTIME_MOD_CNF(232, 0),
  MAPST_ANYTIME_MOD_IND(116, 231),
  MAPST_ANYTIME_MOD_RSP(231, 0),

  MAPST_MAX_SRV_REQ_PRIM(-116),
  /* Values above 127 are used for acknowledgements */
  MAPST_MAX_SRV_ACK_PRIM(-232),
  MAPST_EXTENDED_SERVICE_TYPE(-255, 255),

  /*
   * When MAPST_EXTENDED_SERVICE_TYPE is defined as the first byte in a user message,
   * a MAPPN_SERVICE_TYPE parameter must also be defined in the message to set
   * the actual primitive type value.
   * This will always be the case when primitive type values over 255 are used.
   */

  /*
   * Define MAP service primitive types greater than 255
   *
   * These primitive values require 2 octets and will always be sent to/received from the user
   * using the primitive type parameter MAPPN_SERVICE_TYPE.
   *
   * Each service defined here should always reserve 4 values,
   * even if there is no response defined for the service.
   */
  MAPST_MIN_SRV_REQ_U16_PRIM(-512, 515),

  /* MAP-RESET Service */
  MAPST_RESET_REQ(512, 515),
  MAPST_RESET_IND(513, 514),
  MAPST_RESET_RSP(514, 0),
  MAPST_RESET_CNF(515, 0),

  /* MAP-SET-REPORTING-STATE Service */
  MAPST_SET_REPORTING_STATE_REQ(516, 519),
  MAPST_SET_REPORTING_STATE_IND(517, 518),
  MAPST_SET_REPORTING_STATE_RSP(518, 0),
  MAPST_SET_REPORTING_STATE_CNF(519, 0),

  /* MAP-STATUS-REPORT Service */
  MAPST_STATUS_REPORT_REQ(520, 523),
  MAPST_STATUS_REPORT_IND(521, 522),
  MAPST_STATUS_REPORT_RSP(522, 0),
  MAPST_STATUS_REPORT_CNF(523, 0),

  /* MAP-REMOTE-USER-FREE Service */
  MAPST_REMOTE_USER_FREE_REQ(524, 527),
  MAPST_REMOTE_USER_FREE_IND(525, 526),
  MAPST_REMOTE_USER_FREE_RSP(526, 0),
  MAPST_REMOTE_USER_FREE_CNF(527, 0),

  /* MAP-REGISTER-CC-ENTRY Service */
  MAPST_REGISTER_CC_ENTRY_REQ(528, 531),
  MAPST_REGISTER_CC_ENTRY_IND(529, 530),
  MAPST_REGISTER_CC_ENTRY_RSP(530, 0),
  MAPST_REGISTER_CC_ENTRY_CNF(531, 0),

  /* MAP-ERASE-CC-ENTRY Service */
  MAPST_ERASE_CC_ENTRY_REQ(532, 535),
  MAPST_ERASE_CC_ENTRY_IND(533, 534),
  MAPST_ERASE_CC_ENTRY_RSP(534, 0),
  MAPST_ERASE_CC_ENTRY_CNF(535, 0),

  /* MAP-NOTIFY-SUBSCRIBER-DATA-MODIFIED Service */
  MAPST_NOTIFY_SUB_DATA_MOD_REQ(536, 539),
  MAPST_NOTIFY_SUB_DATA_MOD_IND(537, 538),
  MAPST_NOTIFY_SUB_DATA_MOD_RSP(538, 0),
  MAPST_NOTIFY_SUB_DATA_MOD_CNF(539, 0),

  MAPST_MAX_SRV_REQ_U16_PRIM(-539, 0);

  private static final Map<Integer, Primitive> mService = new HashMap<>();
  private static final Map<Integer, Primitive> mDialogue = new HashMap<>();

  /**
   * Gets the Dialogue Primitive for a value.
   *
   * @param val the value to match with existing Dialogue Primitives
   * @return the matching DialoguePrimitive.
   * @throws NoSuchElementException if there is no matching dialogue primitive for the given value
   */
  public static @NotNull Primitive ofDialogue(int val) throws NoSuchElementException {
    var prim = mDialogue.get(val);
    if (prim == null) {
      throw new NoSuchElementException(String.format("No such dialogue primitive: 0x%x", val));
    }
    return prim;
  }

  /**
   * Gets the Service Primitive for a value.
   *
   * @param val the value to match with existing Service Primitives
   * @return the matching ServicePrimitive.
   * @throws NoSuchElementException if there is no matching service primitive for the given value
   */
  public static @NotNull Primitive ofService(int val) throws NoSuchElementException {
    var prim = mService.get(val);
    if (prim == null) {
      throw new NoSuchElementException(String.format("No such service primitive: 0x%x", val));
    }
    return prim;
  }

  /**
   * Gets the primitive for a value depending on which type is required.
   *
   * @param val the value to look up
   * @param isDialogue If true the val is treated as a dialogue primitive, otherwise as a service
   *     primitive
   * @return A service primitive or dialogue primitive.
   * @throws NoSuchElementException if there is not matching primitive for the given value and
   *     criteria (is dialogue or not).
   */
  public static Primitive of(int val, boolean isDialogue) {
    if (isDialogue) return ofDialogue(val);
    else return ofService(val);
  }

  /**
   * Returns true if there is a valid service primitive for the given value
   *
   * @param val raw value to check
   * @return true if there is a service primitive for the value
   */
  public static boolean isValidServicePrimitive(int val) {
    return mService.containsKey(val);
  }

  /**
   * Returns true if there is a valid dialogue primitive for the given value
   *
   * @param val raw value to check
   * @return true if there is a dialogue primitive for the value
   */
  public static boolean isValidDialoguePrimitive(int val) {
    return mDialogue.containsKey(val);
  }

  static {
    for (Primitive primitive : Primitive.values()) {
      (primitive.isDialogue ? mDialogue : mService).put((int) primitive.value(), primitive);
    }
  }

  private final int value;
  private final boolean isDialogue;
  private final int ackValue;

  Primitive(int value) {
    this(value, false, -1);
  }

  Primitive(int value, int ackValue) {
    this(value, false, ackValue);
  }

  Primitive(int value, boolean isDialogue, int ackValue) {
    this.value = value;
    this.isDialogue = isDialogue;
    this.ackValue = ackValue;
  }

  /**
   * Gets the value of the primitive type.
   *
   * @return the value of the primitive type.
   */
  public short value() {
    return (short) ((value < 0) ? -value : value);
  }

  /**
   * Gets the mnemonic of the primitive type.
   *
   * @return the mnemonic of the primitive type.
   */
  @Contract(pure = true)
  public @NotNull String mnemonic() {
    return name();
  }

  /**
   * Returns true if this primitive is a dialogue primitive.
   *
   * @return true if this primitive is a dialogue primitive.
   */
  public boolean isDialogue() {
    return isDialogue;
  }

  /**
   * Gets the Acknowledgement type of this primitive. If this message type does
   * not have an acknowledgement (since it is an acknowledgement) this will
   * throw {@link java.util.NoSuchElementException}
   *
   * @return The acknowledgement primitive
   * @throws NoSuchElementException If this is an acknowledgement
   */
  public Primitive acknowledgement() throws NoSuchElementException {
    if (ackValue == 0 || (value > 128 && value < 256)) {
      // ackValue == 0: explicitly ack-less
      // (128 < value < 256): range for service primitive acks
      throw new NoSuchElementException(name() + " does not have an acknowledgement");
    }

    int lookupValue = (ackValue == -1) ? value + 128 : ackValue;
    return (isDialogue ? mDialogue : mService).get(lookupValue);
  }
}
