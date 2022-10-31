package lk.mobitel.telco.asmsc.map.constant;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public enum MessageType {
  MAP_MSG_NULL(0),
  MAP_MSG_SRV_REQ(0xc7e0, false, 0),
  MAP_MSG_SRV_IND(0x87e1, false, 0xc7e0),
  MAP_MSG_DLG_REQ(0xc7e2, true, 0),
  MAP_MSG_DLG_IND(0x87e3, true, 0xc7e2);

  private static final Map<Integer, MessageType> mDialogue = new HashMap<>();
  private static final Map<Integer, MessageType> mService = new HashMap<>();

  /**
   * Gets the MessageType enum for a value.
   *
   * @param val the value to match with existing Message types
   * @return the matching MessageType
   * @throws NoSuchElementException if there is no matching message type for the given value
   */
  public static @NotNull MessageType of(int val) throws NoSuchElementException {
    var type = mDialogue.get(val);
    if (type == null) {
      type = mService.get(val);
    }
    if (type == null) {
      throw new NoSuchElementException(String.format("No such message type: 0x%x", val));
    }
    return type;
  }

  /**
   * Returns true if the given value corresponds to a dialogue type.
   *
   * @param val the value to lookup up
   * @return true if the given value corresponds to a dialogue type.
   */
  public static boolean isDialogue(int val) {
    return mDialogue.containsKey(val);
  }

  /**
   * Returns true if the given value corresponds to a service type.
   *
   * @param val the value to lookup up
   * @return true if the given value corresponds to a service type.
   */
  public static boolean isService(int val) {
    return mService.containsKey(val);
  }

  static {
    for (MessageType type : MessageType.values()) {
      (type.isDialogue ? mDialogue : mService).put(type.value(), type);
    }
  }

  private final int value;
  private final boolean isDialogue;
  private final int ackValue;

  MessageType(int value) {
    this(value, false, 0);
  }

  MessageType(int value, boolean isDialogue, int ackValue) {
    this.value = value;
    this.isDialogue = isDialogue;
    this.ackValue = ackValue;
  }

  /**
   * Gets the value of the message type.
   *
   * @return the value of the message type.
   */
  public int value() {
    return value;
  }

  /**
   * Gets the mnemonic of the message type.
   *
   * @return the mnemonic of the message type.
   */
  @Contract(pure = true)
  public @NotNull String mnemonic() {
    return name();
  }

  /**
   * Returns true if this message type is dialogue related.
   *
   * @return True if this message type has CATEGORY_DIALOGUE
   */
  public boolean isDialogue() {
    return isDialogue;
  }

  /**
   * Gets the Acknowledgement type of this message type. If this message type does not have an
   * acknowledgement (since it is an acknowledgement) this will throw {@link
   * java.util.NoSuchElementException}
   *
   * @return The acknowledgement message type
   * @throws NoSuchElementException If this is an acknowledgement
   */
  public MessageType acknowledgement() throws NoSuchElementException {
    if (ackValue == 0) {
      throw new NoSuchElementException(name() + " does not have an acknowledgement");
    }
    return (isDialogue ? mDialogue : mService).get(ackValue);
  }
}
