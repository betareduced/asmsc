package lk.mobitel.telco.asmsc.actors;

import com.dialogic.signaling.gct.GctException;
import com.dialogic.signaling.gct.GctLib;
import com.dialogic.signaling.gct.GctMsg;
import lk.mobitel.telco.asmsc.gct.GctMessage;
import lk.mobitel.telco.asmsc.gct.GctUtil;

/**
 * Sends out messages to a GCT module. This is a singleton object and must be initialized before
 * starting the actor system.
 */
public class Emitter {

  private static Emitter INSTANCE = null;

  /**
   * Returns the singleton instance of the Emitter
   *
   * @return Emitter instance
   */
  public static Emitter getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new Emitter(GctUtil.config().remoteModuleId());
    }
    return INSTANCE;
  }

  private final short gctRemoteModuleId;

  private Emitter(short gctRemoteModuleId) {
    this.gctRemoteModuleId = gctRemoteModuleId;
  }

  /**
   * Sends the given message to configured remote module.
   *
   * @param message Message to send.
   * @throws GctException if Sending is failed at GctLib
   * @throws IllegalStateException if failed to parse the message into a GctMsg object
   */
  public void send(GctMessage message) throws GctException, IllegalStateException {
    GctMsg encoded = GctUtil.encodeMsg(message);
    try {
      GctLib.send(gctRemoteModuleId, encoded);
    } catch (GctException ex) {
      GctLib.relm(encoded);
      throw ex;
    }
  }
}
