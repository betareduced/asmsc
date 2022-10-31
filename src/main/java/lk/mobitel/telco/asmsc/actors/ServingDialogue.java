package lk.mobitel.telco.asmsc.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import lk.mobitel.telco.asmsc.gct.GctDecode;
import lk.mobitel.telco.asmsc.gct.GctMessage;
import lk.mobitel.telco.asmsc.gct.GctParameter;
import lk.mobitel.telco.asmsc.gct.GctUtil;
import lk.mobitel.telco.asmsc.map.constant.ApplicationContext;
import lk.mobitel.telco.asmsc.map.constant.MAPResult;
import lk.mobitel.telco.asmsc.map.constant.ParameterName;
import lombok.Value;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents an incoming dialogue in MAP protocol. An incoming dialogue is a dialogue opened by the
 * MAP-Provider, as opposed to {@link RequestingDialogue}, which is opened by the MAP-User (i.e. this
 * application). Spawned by {@link Server} as required. This supports multiple service
 * invocations simultaneously.
 */
public class ServingDialogue extends AbstractBehavior<ServingDialogue.Command> {

  /**
   * Generic IncomingDialogue command
   */
  public interface Command {}

  /**
   * Generic type for service responses
   */
  public interface ServiceResponse {}

  /**
   * Indicates the service failure
   */
  public enum ServiceFailure implements ServiceResponse {
    /**
     * Marker Instance
     */

    INSTANCE
  }

  private enum State {
    CREATED,
    OPENED,
    RECEIVED,
    DEPLOYED
  }

  /**
   * Opens the dialogue. Dialogue will accept service indications only after opening. Note that
   * dialogue creation is not the opening.
   *
   * @sentby {@link Server}
   * @receivedby {@link ServingDialogue}
   * @pattern Request-Response
   */
  @Value
  public static class OpenDialogue implements Command {
    int dialogueId;
    GctMessage message;
    ActorRef<Server.DialogueOpenStatus> replyTo;
  }

  /**
   * This event indicates a service requirement to the dialogue
   */
  @Value
  public static class ServiceIndication implements Command {
    int dialogueId;
    GctMessage message;
  }

  /**
   * Executes services indicated to the dialogue.
   */
  @Value
  public static class Execute implements Command {
    GctMessage message;
  }

  /**
   * Creates a new incoming dialogue.
   *
   * @param dialogueId ID of the dialogue to be created
   * @param gct        Reference to {@link MAPSystem} actor
   *
   * @return An IncomingDialogue actor
   */
  public static Behavior<Command> create(final int dialogueId,
                                         final ActorRef<MAPSystem.Command> gct) {
    return Behaviors.setup(context -> new ServingDialogue(context, dialogueId, gct));
  }

  private final int id;
  private final ActorRef<MAPSystem.Command> gct;

  private State state;
  private long instance;
  //private List<ServiceIndication> indications;
  //private Map<Short, ActorRef<Service.Command>> activeServices;

  private ServingDialogue(
      ActorContext<Command> context, int dialogueId, ActorRef<MAPSystem.Command> gct) {
    super(context);
    this.id = dialogueId;
    this.gct = gct;
    this.state = State.CREATED;
    context.getLog().info("IncomingDialogue started for ID #{}", this.id);
  }

  @Override
  public Receive<Command> createReceive() {
    return newReceiveBuilder()
        .onMessage(OpenDialogue.class, this::onDialogueOpenIndication)
        .onMessage(ServingDialogue.ServiceIndication.class, this::onServiceIndication)
        .onSignal(PostStop.class, signal -> postStop())
        .build();
  }

  @Contract("_ -> this")
  private Behavior<Command> onDialogueOpenIndication(@NotNull OpenDialogue indication) {
    if (this.id == indication.dialogueId() && state == State.CREATED) {
      if (indication.message().hasParameter(ParameterName.MAPPN_applic_context)) {
        ApplicationContext applicationContext = GctDecode
            .asMobileDomainApplicationContext(
                indication.message().getParameter(
                    ParameterName.MAPPN_applic_context));
        state = State.OPENED;
        instance = indication.message().getInstance();
        getContext()
            .spawnAnonymous(Sender.create())
            .tell(
                new Sender.Send(
                    GctUtil.getAckFor(
                        indication.message(),
                        List.of(
                            GctParameter.of8BitValue(
                                ParameterName.MAPPN_result, (byte) MAPResult.MAPRS_DLG_ACC.value()),
                            GctParameter.ofBytes(
                                ParameterName.MAPPN_applic_context,
                                applicationContext.asByteBuffer())))));
        indication.replyTo().tell(Server.DialogueOpenStatus.success(id));
      } else {
        // application context is missing
        getContext()
            .spawnAnonymous(Sender.create())
            .tell(
                new Sender.Send(
                    GctUtil.getGenericAbortRequest(
                        indication.dialogueId(), indication.message().getInstance())));
        indication
            .replyTo()
            .tell(Server.DialogueOpenStatus.error(id, "Application Context missing"));
        return Behaviors.stopped();
      }
    } else {
      // This path is highly unlikely
      if (state != State.CREATED) {
        // Another open indication received for busy dialogue. This indication should be aborted.
        getContext().getLog().warn("Ignoring open indication for already opened dialogue");
      } else {
        getContext()
            .getLog()
            .warn(
                "Ignoring dialogue open indication with wrong ID. Expected = #{}, Received = #{}",
                this.id,
                indication.message().getId());
      }
    }
    return this;
  }

  @Contract("_ -> this")
  private Behavior<Command> onServiceIndication(ServingDialogue.@NotNull ServiceIndication creation) {
    return this;
  }

  private Behavior<Command> postStop() {
    getContext().getLog().info("IncomingDialogue #{} is ended.", id);
    return this;
  }
}
