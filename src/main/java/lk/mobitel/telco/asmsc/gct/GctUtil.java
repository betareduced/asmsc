package lk.mobitel.telco.asmsc.gct;

import com.dialogic.signaling.gct.BBUtil;
import com.dialogic.signaling.gct.GctException;
import com.dialogic.signaling.gct.GctLib;
import com.dialogic.signaling.gct.GctMsg;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lk.mobitel.telco.asmsc.config.RuntimeConfiguration;
import lk.mobitel.telco.asmsc.map.constant.MessageType;
import lk.mobitel.telco.asmsc.map.constant.ParameterName;
import lk.mobitel.telco.asmsc.map.constant.Primitive;
import lk.mobitel.telco.asmsc.map.constant.UserReason;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
public class GctUtil {

  /**
   * This is a rough estimation on how many parameters would appear in one GctMsg. It reduces
   * unnecessary memory allocations.
   */
  private static final int NORMAL_PARAMETER_COUNT = 3;

  /**
   * Empty buffer can be used safely to indicate empty parameter area in the GctMsg. It keeps the
   * parsing algorithm happy.
   */
  private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.allocate(2);

  static RuntimeConfiguration config;

  /**
   * Initializes the static data for Gct internals
   *
   * @param config configuration object
   */
  public static void init(RuntimeConfiguration config) {
    GctUtil.config = config;
  }

  /**
   * Gets the global configuration object. Throws {@link IllegalStateException} if GctUtil is not
   * initialized properly;
   *
   * @return The associated {@link RuntimeConfiguration} object.
   *
   * @throws IllegalStateException if {@link #init(RuntimeConfiguration)} function is not called
   *                               already.
   */
  public static RuntimeConfiguration config() throws IllegalStateException {
    if (config == null) {
      throw new IllegalStateException("GctUtil is not initialized properly. This is a bug...");
    }
    return config;
  }

  /**
   * Gets value suited for rspReq field in native dialogic messages. This value is calculated as
   * follows:
   *
   * <blockquote>
   * <p>
   * If the sending module requires confirmation, it sets a bit in the rsp_req field prior to
   * sending the message. Which bit to set is determined by the value of the least significant
   * nibble of the module's own module id (as written in the src field) For example, if the module
   * id is 0x36 and message confirmation is required, the least significant nibble value of 0x6
   * indicates that the user would set bit 6 in the rsp_req field, so rsp_req would equal 0x0040.
   *
   * </blockquote>
   *
   * @return The value for rspReq field in native messages
   */
  public static short rspReqValue() {
    return (short) (1 << (config.localModuleId() & 15));
  }

  /**
   * Copies the given byte-buffer to given GctMsg's parameter buffer. GctMsg does not provide a
   * direct method to set parameter buffer.
   *
   * @param msg    GctMsg to copy the buffer to.
   * @param buffer buffer to copy
   */
  public static void copyParameterBuffer(@NotNull GctMsg msg, @NotNull ByteBuffer buffer) {
    int id = -1;
    try {
      id = msg.getId();
      ByteBuffer msgBuffer = msg.getParam();
      msgBuffer.put(buffer);
    } catch (GctException e) {
      if (id == -1) {
        log.error("Unable to read Message data: {}", e.getMessage());
      } else {
        log.error("Unable to copy parameter buffer to Message #{}", id);
      }
    }
  }

  /**
   * Parses a byte-buffer as specified in Dialogic MAP Programmer's Manual.
   *
   * @param buffer      The byte buffer to parse.
   * @param messageType messageType Type of the message from which this buffer is extracted. This is
   *                    necessary since dialogue and service primitive type values are overlapping.
   *
   * @return a tuple of two elements, namely 1) Primitive type, 2) A list of parameters
   *
   * @throws BufferUnderflowException if reading from buffer failed.
   */
  public static @NotNull Tuple2<Primitive, List<GctParameter>> decodeParameterBuffer(
      @NotNull ByteBuffer buffer, @NotNull MessageType messageType)
      throws BufferUnderflowException {
    return decodeParameterBuffer(new GctByteBuffer(buffer), messageType);
  }

  /**
   * Parses a GctByteBuffer as specified in Dialogic MAP Programmer's Manual.
   *
   * @param buffer      The byte buffer to parse.
   * @param messageType Type of the message from which this buffer is extracted. This is necessary
   *                    since dialogue and service primitive type values are overlapping.
   *
   * @return a tuple of two elements, namely 1) Primitive type, 2) A list of parameters
   *
   * @throws BufferUnderflowException if reading from buffer failed.
   * @throws IllegalStateException    if the primitive in parameter area is unknown.
   */
  public static @NotNull Tuple2<Primitive, List<GctParameter>> decodeParameterBuffer(
      @NotNull GctByteBuffer buffer, @NotNull MessageType messageType)
      throws BufferUnderflowException, IllegalStateException {
    Primitive primitive;
    ArrayList<GctParameter> params = new ArrayList<>(NORMAL_PARAMETER_COUNT);

    int primitiveVal;
    try {
      primitiveVal = buffer.next8();
    } catch (BufferUnderflowException ex) {
      log.warn("Parameter buffer is empty");
      throw ex;
    }

    boolean codeShift = false;
    while (buffer.hasRemaining()) {

      // read name tag
      int nameVal = buffer.next8();
      if (nameVal == ParameterName.MAPPN_LONG_PARAM_CODE_EXTENSION.value()) {
        if (codeShift) {
          buffer.next16();
        } else {
          buffer.next8();
        }
        nameVal = buffer.next16();
      }

      if (nameVal == ParameterName.MAPPN_null.value()) {
        break;
      }

      // read length field
      int length;
      if (codeShift) {
        length = buffer.next16();
      } else {
        length = buffer.next8();
      }

      // read data field
      ByteBuffer data = ByteBuffer.allocate(length);
      if (nameVal == ParameterName.MAPPN_CODE_SHIFT.value()) {
        short c = buffer.next8();
        codeShift = (c == 0x01);
        data.put((byte) c);
      } else if (nameVal == ParameterName.MAPPN_SERVICE_TYPE.value()) {
        primitiveVal = buffer.next16();
        data.putShort((short) primitiveVal);
      } else {
        for (int i = 0; i < length; i++) {
          data.put((byte) buffer.next8());
        }
      }
      data.rewind();

      try {
        ParameterName name = ParameterName.of(nameVal);
        params.add(new GctParameter(name, data));
      } catch (NoSuchElementException ex) {
        log.warn(
            String.format(
                "No parameter for value '0x%x'(%d). Ignoring this parameter...", nameVal, nameVal));
      }
    }

    try {
      primitive = Primitive.of(primitiveVal, messageType.isDialogue());
    } catch (NoSuchElementException ex) {
      log.warn(
          String.format(
              "Unable to parse buffer, unknown primitive value '0x%x'(%d). Default primitive is applied",
              primitiveVal, primitiveVal));
      throw new IllegalStateException(String.format("bad primitive: 0x%x", primitiveVal));
    }

    params.trimToSize();

    return Tuple.of(primitive, params);
  }

  /**
   * Converts a <code>GctMsg</code> to a {@link GctMessage}.
   *
   * @param msg The <code>GctMsg</code> object to convert
   *
   * @return A {@link GctMessage} object.
   */
  public static GctMessage parseMsg(@NotNull GctMsg msg) {
    ByteBuffer params = null;
    int id = 0;
    int src = 0;
    int dst = 0;
    int type = 0;
    long instance = 0;
    boolean rspReq = false;

    try {
      id = msg.getId();
      src = msg.getSrc();
      dst = msg.getDst();
      type = msg.getType();
      instance = msg.getInstance();
      rspReq = msg.getRspReq();
      params = msg.getParam();
    } catch (GctException ex) {
      log.warn("Parameter buffer retrieval failed for Message #{}", id);
      params = EMPTY_BUFFER;
    } finally {
      if (params == null) {
        params = EMPTY_BUFFER;
      }
    }

    try {
      MessageType mType = MessageType.of(type);
      Tuple2<Primitive, List<GctParameter>> t = decodeParameterBuffer(params, mType);

      Primitive primitive = t._1;
      List<GctParameter> parameters = t._2;

      return GctMessage.builder()
          .id(id)
          .source(src)
          .destination(dst)
          .instance(instance)
          .type(mType)
          .primitive(primitive)
          .parameters(parameters)
          .responseRequired(rspReq)
          .build();
    } catch (BufferUnderflowException ex) {
      log.error("Unable to parse parameter area: {}", ex.getMessage());
      throw new IllegalStateException("bad parameter area");
    } catch (NoSuchElementException ex) {
      log.error("Unknown message type for Message #{}", id);
      throw new IllegalStateException("bad message type: " + ex.getMessage());
    } catch (IllegalStateException ex) {
      log.error("Unknown primitive type: {}", ex.getMessage());
      throw new IllegalStateException("bad primitive: " + ex.getMessage());
    }
  }

  /**
   * Encodes a parameter buffer according to Dialogic MAP Programmer's manual
   *
   * @param primitive  Primitive of the message. (Message primitive is found in the parameter area).
   * @param parameters The list of parameters to encode after primitive byte.
   *
   * @return The encoded bytes are a byte buffer.
   */
  public static @NotNull ByteBuffer encodeParameterBuffer(
      @NotNull Primitive primitive, @NotNull List<GctParameter> parameters) {

    // (1) Use extended primitive type for values > 255
    if (primitive.value() > 0xff) {
      parameters.add(
          GctParameter.of16BitValue(ParameterName.MAPPN_SERVICE_TYPE, primitive.value()));
      primitive = Primitive.MAPST_EXTENDED_SERVICE_TYPE;
    }

    // (2) Re-organize parameters
    var organizedResult = reorganizeParameters(parameters);
    int bufferLength = organizedResult._2 + 1; // +1 for primitive
    List<GctParameter> newParameters = organizedResult._1;
    ByteBuffer result = ByteBuffer.allocate(bufferLength);

    // (4) Put primitive. It is guaranteed to be an 8-bit value at (1)
    result.put((byte) (primitive.value() & 0xFF));

    // (5) Insert parameters
    boolean codeShifted = false;
    for (GctParameter parameter : newParameters) {
      BBUtil.putU8(result, parameter.getName().value());
      if (codeShifted) {
        BBUtil.putU16(result, parameter.getLength());
      } else {
        BBUtil.putU8(result, parameter.getLength());
      }
      result.put(parameter.getData());

      if (parameter.getName() == ParameterName.MAPPN_CODE_SHIFT) {
        codeShifted = GctDecode.asBoolean(parameter);
      }
    }

    return result.rewind();
  }

  /**
   * Encodes a {@link GctMessage} into a <code>GctMsg</code>. A GctMsg is allocated in this method
   * and caller must release it.
   *
   * @param message The {@link GctMessage} to encode
   *
   * @return An allocated GctMsg, populated with data from {@code message}. Caller must release
   * this.
   *
   * @throws IllegalStateException if {@code com.dialogic.signaling.gct.GctException} is thrown
   *                               while setting data.
   */
  public static @NotNull GctMsg encodeMsg(@NotNull GctMessage message)
      throws IllegalStateException {
    try {
      ByteBuffer buffer = encodeParameterBuffer(message.getPrimitive(), message.parameterList());

      GctMsg nativeMsg = GctLib.getm(buffer.limit());
      nativeMsg.setId(message.getId());
      nativeMsg.setSrc((short) message.getSource());
      nativeMsg.setDst((short) message.getDestination());
      nativeMsg.setInstance(message.getInstance());
      nativeMsg.setRspReq(message.isResponseRequired());
      nativeMsg.setType(message.getType().value());
      copyParameterBuffer(nativeMsg, buffer);
      return nativeMsg;
    } catch (GctException e) {
      log.error("Unable to set values for GctMsg. GctException occurred: {}", e.getMessage());
      throw new IllegalStateException("Unable to set values for GctMsg: " + e.getMessage());
    }
  }

  /**
   * This is a simplification for message encoding algorithm.
   *
   * <p>If we were to encode parameter list as it is, the algorithm becomes complex, in order to
   * handle long parameters, extended parameters in the mix of normal parameters. There will be
   * multiple code shifts, and it is harder to manage actual parameter lengths when they are
   * extended.
   *
   * <p>Whenever an extended parameter occurs, the actual parameter's (<code>
   * MAPPN_LONG_PARAM_CODE_EXTENSION</code>) length is +3/+4 of the given length. If not code
   * shifted, this length can exceed the range (00-ff) and there needs to be additional code
   * shifting to handle it. This process is too cumbersome and implementations are much prone to
   * bugs.
   *
   * <p>This function reorganize parameters so that, only one <code>MAPPN_CODE_SHIFT</code> is used
   * and lengths of extended parameters are safely handled. There is four categories of parameters
   * regarded in this reorganization.
   *
   * <ol>
   *   <li>Short parameters (length <= 255) <b>"S"</b>
   *   <li>Short extended parameters (length+3 <= 255 BUT name > 255) <b>"SE"</b>
   *   <li>Long parameters (length > 255) <b>"L"</b>
   *   <li>Long extended parameters (length+3 > 255 AND name > 255) <b>"LE"</b>
   * </ol>
   *
   * <p>These parameters are reorganized into a list of following order, <b>*</b> indicated zero or
   * more parameters:
   *
   * <pre>
   *   [S]*...[SE]*...[<b>MAPPN_CODE_SHIFT</b>][L]*...[LE]*...
   * </pre>
   *
   * <p><b>SE, LE</b> parameters lose their form and becomes extended parameters (<code>
   * MAPPN_LONG_PARAM_CODE_EXTENSION</code>)
   *
   * @param parameters the list of original parameters.
   *
   * @return a tuple of 2 items; 1) list of organized parameters, 2) length of the buffer required
   * for (1)
   *
   * @implNote This could be a possible bottleneck when comes to memory or time efficiency.
   */
  static @NotNull Tuple2<List<GctParameter>, Integer> reorganizeParameters(
      @NotNull List<GctParameter> parameters) {
    int bufferLength = 0;
    List<GctParameter> paramsS = new ArrayList<>();
    List<GctParameter> paramsSE = new ArrayList<>();
    List<GctParameter> paramsL = new ArrayList<>();
    List<GctParameter> paramsLE = new ArrayList<>();

    for (GctParameter parameter : parameters) {
      if (parameter.getName().value() > 255) {
        // Extended parameters
        if (parameter.getLength() + 3 > 255) {
          // Extended Long parameters
          int l = 2 + 2 + parameter.getLength();
          ByteBuffer b = ByteBuffer.allocate(l);
          BBUtil.putU16(b, parameter.getName().value());
          BBUtil.putU16(b, parameter.getLength());
          b.put(parameter.getData());
          paramsLE.add(new GctParameter(ParameterName.MAPPN_LONG_PARAM_CODE_EXTENSION, b));
          bufferLength += 1 + 2 + l;
        } else {
          // Extended Short parameters
          int l = 2 + 1 + parameter.getLength();
          ByteBuffer b = ByteBuffer.allocate(l);
          BBUtil.putU16(b, parameter.getName().value());
          BBUtil.putU8(b, parameter.getLength());
          b.put(parameter.getData());
          paramsSE.add(new GctParameter(ParameterName.MAPPN_LONG_PARAM_CODE_EXTENSION, b));
          bufferLength += 1 + 1 + l;
        }
      } else {
        // Normal parameters
        bufferLength += 1 + 1 + parameter.getLength();
        if (parameter.getLength() > 255) {
          // Normal Long parameters
          paramsL.add(parameter);
          bufferLength++;
        } else {
          // Normal Short parameters
          paramsS.add(parameter);
        }
      }
    }

    List<GctParameter> organized =
        new ArrayList<>(paramsS.size() + paramsL.size() + paramsSE.size() + paramsLE.size() + 1);
    organized.addAll(paramsS);
    organized.addAll(paramsSE);

    if (paramsSE.size() > 0 || paramsLE.size() > 0) {
      bufferLength += 3; // for MAPPN_CODE_SHIFT
      organized.add(GctParameter.of8BitValue(ParameterName.MAPPN_CODE_SHIFT, (byte) 1));
      organized.addAll(paramsL);
      organized.addAll(paramsLE);
    }

    return Tuple.of(organized, bufferLength);
  }

  /**
   * Gets the invoke-id of a message. When the MAPPN_invoke_id is missing from the message, this
   * returns -1.
   *
   * @param message Message to retrieve the invoke-id from.
   *
   * @return The invoke-id in the message. -1 if invoke-id parameter is absent.
   */
  public static int getInvokeId(@NotNull GctMessage message) {
    int invokeId = -1;
    if (message.hasParameter(ParameterName.MAPPN_invoke_id)) {
      GctParameter invokeIdParam = message.getParameter(ParameterName.MAPPN_invoke_id);
      invokeId = GctDecode.asInt(invokeIdParam);
    }
    return invokeId;
  }

  /**
   * Builds the acknowledgement message for the given message.
   *
   * @param message Message to build ack for. This must not be an ack already.
   * @param params  List of parameters to include in the resultant message.
   *
   * @return The ack message of the given req message.
   *
   * @throws NoSuchElementException if the given message is already an ack message.
   */
  public static GctMessage getAckFor(@NotNull GctMessage message,
                                     List<GctParameter> params)
      throws NoSuchElementException {
    return GctMessage.builder()
        .id(message.getId())
        .instance(message.getInstance())
        .type(message.getType().acknowledgement())
        .primitive(message.getPrimitive().acknowledgement())
        .parameters(params)
        .build();
  }

  /**
   * Builds a generic abort request with a user reason
   *
   * @param dialogueId Relevant dialogue id for the abort request.
   * @param instance   Instance ID of the resultant message
   *
   * @return A User Abort Request ready to send
   */
  public static GctMessage getGenericAbortRequest(final int dialogueId,
                                                  final long instance) {
    return GctMessage.builder()
        .id(dialogueId)
        .type(MessageType.MAP_MSG_DLG_REQ)
        .primitive(Primitive.MAPDT_U_ABORT_REQ)
        .instance(instance)
        .parameter(
            GctParameter.of8BitValue(
                ParameterName.MAPPN_user_rsn, UserReason.MAPUR_procedure_error.value()))
        .build();
  }

  /**
   * Checks if the message is an incoming message.
   *
   * @param message Message to check
   *
   * @return true if the message is an incoming message, otherwise false.
   */
  public static boolean isIncomingMessage(@NotNull GctMessage message) {
    return message.getId() >= config().incomingDialogueIdBase()
        && message.getId() < config().incomingDialogueCount();
  }

  /**
   * Checks if the message is an outgoing message.
   *
   * @param message Message to check
   *
   * @return true if the message is an outgoing message, otherwise false.
   */
  public static boolean isOutgoingMessage(@NotNull GctMessage message) {
    return message.getId() >= config().outgoingDialogueIdBase()
        && message.getId() < config().outgoingDialogueCount();
  }

  static {
    EMPTY_BUFFER.put((byte) 0); // null primitive
    EMPTY_BUFFER.put((byte) 0); // parameter sentinel
  }
}
