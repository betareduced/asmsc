package lk.mobitel.telco.asmsc.actors;

import akka.actor.typed.ActorRef;
import com.dialogic.signaling.gct.GctException;
import com.dialogic.signaling.gct.GctLib;
import com.dialogic.signaling.gct.GctMsg;
import lk.mobitel.telco.asmsc.gct.GctMessage;
import lk.mobitel.telco.asmsc.gct.GctUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Receives messages directly from the GCT Stack. Received messages are then parsed into {@link
 * GctMessage} objects and sent to the {@link MAPSystem} to be processed.
 */
@Slf4j
public class Collector {
  private final short gctLocalModuleId;
  private final ActorRef<MAPSystem.Command> gct;

  /**
   * @param gctLocalModuleId ID of the Septel module to receive messages from.
   * @param gct              Reference to the {@link MAPSystem} actor.
   */
  public Collector(short gctLocalModuleId, ActorRef<MAPSystem.Command> gct) {
    this.gctLocalModuleId = gctLocalModuleId;
    this.gct = gct;
  }

  /** Starts the receiving process. */
  public void start() {
    log.info("Collector started on thread: {}", Thread.currentThread());

    //noinspection InfiniteLoopStatement
    for (; ; ) {
      try {
        GctMsg encoded = GctLib.receive(gctLocalModuleId);
        GctMessage message = GctUtil.parseMsg(encoded);
        gct.tell(new MAPSystem.ProcessMessage(message));
        GctLib.relm(encoded);
      } catch (RuntimeException e) {
        log.warn("Runtime exception from GctLib is ignored. This is unusual. Cause is unknown.");
      } catch (GctException e) {
        log.warn("Unable to release GctMsg at Ingress processor", e);
      }
    }
  }
}
