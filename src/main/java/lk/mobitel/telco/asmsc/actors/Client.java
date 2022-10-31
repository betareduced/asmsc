package lk.mobitel.telco.asmsc.actors;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class Client extends AbstractBehavior<Client.Command> {

  public interface Command {}

  public static Behavior<Command> create() {
    return Behaviors.setup(Client::new);
  }

  private Client(ActorContext<Command> context) {
    super(context);
    context.getLog().info("Gct Client Started");
  }

  @Override
  public Receive<Command> createReceive() {
    return null;
  }
}
