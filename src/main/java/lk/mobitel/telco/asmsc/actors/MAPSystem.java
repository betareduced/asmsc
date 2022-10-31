package lk.mobitel.telco.asmsc.actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import lk.mobitel.telco.asmsc.gct.GctMessage;
import lk.mobitel.telco.asmsc.gct.GctUtil;
import lk.mobitel.telco.asmsc.map.Parameter;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

/**
 * Switch is the main guardian of the ASMSC actor hierarchy. It deploys the
 * {@link Server} and {@link Client} actors. Then routes messages to
 * those actors.
 */
public class MAPSystem extends AbstractBehavior<MAPSystem.Command> {

  /**
   * Generic command for Switch
   */
  public interface Command {}

  /**
   * Command to handle
   */
  @Value
  public static class ProcessMessage implements Command {
    GctMessage message;
  }

  /**
   * Request routing information from an external entity
   */
  @Value
  public static class RequestRoutingInfo implements Command {
    Parameter msisdn;
    Parameter scAddr;
  }

  private final ActorRef<Server.Command> server;
  private final ActorRef<Client.Command> client;

  private MAPSystem(ActorContext<Command> context) {
    super(context);

    server = getContext().spawn(
        Behaviors
            .supervise(Server.create(context.getSelf()))
            .onFailure(SupervisorStrategy.restart()),
        "server");

    client = getContext().spawn(
        Behaviors
            .supervise(Client.create())
            .onFailure(SupervisorStrategy.restart()),
        "client");
  }


  @Override
  public Receive<Command> createReceive() {
    return newReceiveBuilder()
        .onMessage(ProcessMessage.class, this::onProcessMessage)
        .build();
  }

  private Behavior<Command> onProcessMessage(@NotNull ProcessMessage handle) {
    if (GctUtil.isIncomingMessage(handle.message())) {
      server.tell(new Server.ProcessIncomingMessage(handle.message()));
    }

    return this;
  }
}
