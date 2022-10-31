package lk.mobitel.telco.asmsc.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import io.vavr.control.Option;
import lk.mobitel.telco.asmsc.gct.GctMessage;
import lk.mobitel.telco.asmsc.gct.GctUtil;
import lk.mobitel.telco.asmsc.map.constant.MessageType;
import lombok.Value;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Incoming Dialogue Manager. Responsible for spawning {@link ServingDialogue} actors, convert
 * GctMessages into Actor Protocol commands and relay responses from dialogues.
 */
public class Server extends AbstractBehavior<Server.Command> {

  /**
   * This dialogue actor ignores any message directed at it.
   */
  private static final int IGNORING_DIALOGUE = -1;

  /**
   * Generic Incoming Manager command
   */
  public interface Command {}

  /**
   * Watches the {@link Sender} termination
   *
   * @sentby ActorSystem
   * @receivedby {@link Server}
   * @pattern Event Notification
   */
  public enum EmitterStopped implements Command {
    /**
     * Placeholder Instance
     */
    INSTANCE
  }

  /**
   * Process the incoming message.
   *
   * @sentby {@link MAPSystem}
   * @receivedby {@link Server}
   */
  @Value
  public static class ProcessIncomingMessage implements Command {
    GctMessage message;
  }

  /**
   * Watches a dialogue termination
   *
   * @sentby ActorSystem
   * @receivedby {@link Server}
   */
  @Value
  public static class DialogueEnded implements Command {
    int dialogueId;
  }


  /**
   * Result of {@link ServingDialogue.OpenDialogue} command.
   *
   * @sentby {@link ServingDialogue}
   * @receivedby the issuer of {@link ServingDialogue.OpenDialogue}
   */
  @Value
  public static class DialogueOpenStatus implements Server.Command {
    @Contract("_ -> new")
    public static @NotNull DialogueOpenStatus success(final int dialogueId) {
      return new DialogueOpenStatus(dialogueId, true, Option.none());
    }

    @Contract("_, _ -> new")
    public static @NotNull DialogueOpenStatus error(final int dialogueId, String error) {
      return new DialogueOpenStatus(dialogueId, false, Option.of(error));
    }

    int dialogueId;
    boolean isSuccess;
    Option<String> errorReason;
  }


  /**
   * Creates a new IncomingManager actor
   *
   * @param gct Reference to {@link MAPSystem}
   *
   * @return A new IncomingManager actor
   */
  public static Behavior<Command> create(final ActorRef<MAPSystem.Command> gct) {
    return Behaviors.setup(context -> new Server(context, gct));
  }

  /**
   * Reference to {@link MAPSystem} actor.
   */
  private final ActorRef<MAPSystem.Command> gct;

  /**
   * Registry of currently opened dialogues. Dialogue IDs are mapped to {@link ServingDialogue} actor
   * references.
   *
   * <p>In this map, key NULL_DIALOGUE is mapped to a special actor: Which ignores all messages.
   */
  private final Map<Integer, ActorRef<ServingDialogue.Command>> dialogueRegistry = new HashMap<>();

  private Server(ActorContext<Command> context, ActorRef<MAPSystem.Command> gct) {
    super(context);
    this.gct = gct;
    this.dialogueRegistry.put(
        IGNORING_DIALOGUE, getContext().spawn(Behaviors.ignore(), "all-ignoring-dialogue"));

    context.getLog().info("Gct Server started");
  }

  @Override
  public Receive<Command> createReceive() {
    return newReceiveBuilder()
        .onMessage(ProcessIncomingMessage.class, this::processIncomingMessage)
        .onMessage(DialogueEnded.class, this::onDialogueEnded)
        .onMessage(DialogueOpenStatus.class, this::onDialogueOpenResponse)
        .onSignal(PostStop.class, signal -> postStop())
        .build();
  }

  /**
   * Handles an incoming message. Takes action regarding the type and primitive of the message. This
   * is the *entry* point for new messages.
   *
   * @param incoming The incoming message command
   *
   * @return the current behaviour
   */
  @Contract("_ -> this")
  private Behavior<Command> processIncomingMessage(@NotNull Server.ProcessIncomingMessage incoming) {
    GctMessage msg = incoming.message();
    getContext().getLog().info("Received message: {}", msg);

    // Defense against unexpected message types.
    // We don't know how to handle them.
    if (msg.getType() == MessageType.MAP_MSG_DLG_REQ
        || msg.getType() == MessageType.MAP_MSG_SRV_REQ) {
      getContext()
          .getLog()
          .warn(
              "Unexpected dialogue received for IncomingManager. Message: {}", msg);
      return this;
    }

    int dialogueId = msg.getId();

    // Convert the MAP protocol to Actor protocol
    switch (msg.getType()) {
      case MAP_MSG_DLG_IND:
        // Dialogue indication
        switch (msg.getPrimitive()) {
          case MAPDT_OPEN_IND:
            boolean spawned = newDialogue(dialogueId, msg);
            if (spawned) {
              getContext().getLog()
                  .info("Dialogue spawned for #{}", dialogueId);
            } else {
              getContext().getLog()
                  .error("Dialogue #{} is unavailable", dialogueId);
            }
            break;
          case MAPDT_DELIMITER_IND:
            getDialogueOrElseIgnore(dialogueId, msg)
                .tell(new ServingDialogue.Execute(msg));
            break;
          case MAPDT_CLOSE_IND:
            // TODO: Close indication
            break;
          case MAPDT_NOTICE_IND:
            // TODO: Notice indication
            break;
          default:
            getContext().getLog().warn("Unsupported dialogue indication: {}", msg);
            break;
        }
        break;
      case MAP_MSG_SRV_IND:
        getDialogueOrElseIgnore(dialogueId, msg)
            .tell(new ServingDialogue.ServiceIndication(dialogueId, msg));
        break;
      default:
        getContext().getLog().info("Unsupported message");
    }

    return this;
  }

  /**
   * Handles dialogue termination event.
   *
   * @param ended Signal of termination
   *
   * @return the current behaviour
   */
  @Contract("_ -> this")
  private Behavior<Command> onDialogueEnded(@NotNull DialogueEnded ended) {
    getContext().getLog().info("Incoming Dialogue #{} has been ended.", ended.dialogueId());
    dialogueRegistry.remove(ended.dialogueId());
    return this;
  }

  /**
   * Handles the response of OpenDialogue command issued for InDialogue.
   *
   * @param status status reply from InDialogue
   *
   * @return the current behaviour
   */
  @Contract("_ -> this")
  private Behavior<Command> onDialogueOpenResponse(@NotNull DialogueOpenStatus status) {

    if (!status.isSuccess()) {
      getContext()
          .getLog()
          .error(
              "Incoming dialogue opening failed for #{}: {}",
              status.dialogueId(),
              status.errorReason().getOrElse("Unknown error"));

      // Since we are watching the dialogue with DialogueEnded, when the
      // dialogue actor is stopped, it will be automatically removed. But this
      // will make it immediate.
      dialogueRegistry.remove(status.dialogueId());
    } else {
      getContext().getLog().info("Incoming dialogue opened successfully #{}", status.dialogueId());
    }
    return this;
  }

  /**
   * Performs post-stopping actions
   *
   * @return the current behaviour
   */
  private Behavior<Command> postStop() {
    getContext().getLog().info("Incoming Manager has been stopped.");
    return this;
  }

  /**
   * Spawns a new {@link ServingDialogue} and try opening it.
   *
   * <p>If the dialogue is already opened, this will emit an abort request
   * to the open indication instance. Otherwise new {@link ServingDialogue}
   * actor is spawned and the {@link ServingDialogue.OpenDialogue} message
   * is sent to it, wrapping the given {@code message}</p>
   *
   * @param dialogueId Dialogue ID
   * @param message    An open indication message to open the dialogue
   *
   * @return True if new dialogue actor is spawned, False if an actor is
   * already spawned for the {@code dialogueId}
   */
  private boolean newDialogue(int dialogueId, @NotNull GctMessage message) {
    if (dialogueRegistry.containsKey(dialogueId)) {
      getContext()
          .spawnAnonymous(Sender.create())
          .tell(
              new Sender.Send(
                  GctUtil.getGenericAbortRequest(
                      dialogueId, message.getInstance())));
      return false;
    }

    getContext().getLog().info("Creating IncomingDialogue actor for ID #{}", dialogueId);
    ActorRef<ServingDialogue.Command> dialogueRef =
        getContext()
            .spawn(ServingDialogue.create(dialogueId, this.gct), "IncomingDialogue-" + dialogueId);
    getContext().watchWith(dialogueRef, new DialogueEnded(dialogueId));
    dialogueRegistry.put(dialogueId, dialogueRef);
    dialogueRef.tell(new ServingDialogue.OpenDialogue(
        dialogueId, message, getContext().getSelf().narrow()));
    return true;
  }

  /**
   * Gets a dialogue actor from the registry. If there is no such actor for the given dialogue id,
   * the generic abort request is issued to the MAP-Provider, and returns NULL_DIALOGUE.
   *
   * @param dialogueId dialogue id to lookup
   * @param message    message for which this dialogue is used.
   *
   * @return a reference to dialogue actor if dialogueId is present. if not the NULL_DIALOGUE.
   * Hence, the return value of this method is always valid
   */
  private @NotNull ActorRef<ServingDialogue.Command> getDialogueOrElseIgnore(
      int dialogueId, GctMessage message) {
    ActorRef<ServingDialogue.Command> ref = dialogueRegistry.get(dialogueId);
    if (ref == null) {
      getContext()
          .getLog()
          .error(
              "Received unexpected message for unopened dialogue #{}. This "
                  + "will be ignored"
                  + "(message: {})",
              dialogueId,
              message);
      getContext()
          .spawnAnonymous(Sender.create())
          .tell(
              new Sender.Send(
                  GctUtil.getGenericAbortRequest(dialogueId, message.getInstance())));
      return dialogueRegistry.get(IGNORING_DIALOGUE);
    }
    return ref;
  }
}
