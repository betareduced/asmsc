package lk.mobitel.telco.asmsc.config;

/**
 * This interface declares the configuration items accepted by the Advanced SMSC Application.
 * Implementations can provide various methods to retrieve configurations.
 *
 * @author NISALBA
 */
public interface RuntimeConfiguration {
  /**
   * Local Module ID is the configured gctload module id for this application, as in
   * Septel/system.txt
   *
   * @return the module id of the application
   */
  short localModuleId();

  /**
   * Remote Module ID is the configured module id of the MAP task in Septel stack. This is where the
   * messages are sent to and received from.
   *
   * @return the (remote) module id of the MAP task
   */
  short remoteModuleId();

  /**
   * The first dialogue ID for outgoing dialogues that the user wishes to be handled by the MAP
   * module. The subsequent ({@link #outgoingDialogueCount()} - 1) dialogue IDs will also be handled
   * by the module.
   *
   * <p>The <b>user</b> must ensure that the values used in the dialogue ID field of all protocol
   * messages pertaining to outgoing dialogues lie within the correct range.
   *
   * <p>see Dialogic MAP Programmer's Manual, 5.1 MAP Configuration Request.
   *
   * @return the smallest dialog id for outgoing dialogues
   * @see #outgoingDialogueCount
   */
  int outgoingDialogueIdBase();

  /**
   * The maximum number of simultaneous outgoing dialogues that the module is required to support.
   *
   * <p>This and the {@link #outgoingDialogueIdBase()} makes up the range of outgoing dialogue ids
   * handled by the system, namely <code>[base, base + count)</code>
   *
   * @return the number of simultaneous outgoing dialogues allowed in the system.
   * @see #outgoingDialogueIdBase()
   */
  int outgoingDialogueCount();

  /**
   * The first dialogue ID for incoming dialogues that the user wishes to be handled by the MAP
   * module. The subsequent ({@link #outgoingDialogueCount()} - 1) dialogue IDs will also be handled
   * by the module.
   *
   * <p>The MAP module allocates the dialogue ID for each incoming dialogue. It uses values in the
   * range <code>[base, base + count)</code> for this purpose.
   *
   * @return the smallest dialog id for incoming dialogues
   * @see #incomingDialogueCount()
   */
  int incomingDialogueIdBase();

  /**
   * The maximum number of simultaneous incoming dialogues that the module is required to support.
   *
   * @return the number of simultaneous incoming dialogues allowed in the system.
   * @see #incomingDialogueIdBase()
   */
  int incomingDialogueCount();

  /**
   * Global Title (GT) of this node.
   *
   * @return the GT address of the node.
   */
  String gtAddress();
}
