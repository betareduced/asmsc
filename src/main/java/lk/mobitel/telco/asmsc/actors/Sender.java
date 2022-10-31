package lk.mobitel.telco.asmsc.actors;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.dialogic.signaling.gct.GctException;
import lk.mobitel.telco.asmsc.gct.GctMessage;
import lombok.Value;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Handles the message emitting through {@link Emitter}. Rationale for this actor is to keep the
 * {@link Server} safe from errors at message delivery. In such cases, emitter is failed
 * without disrupting IncomingManager. Subjected to restarts.
 */
public class Sender extends AbstractBehavior<Sender.Command> {

  /** Generic EmitterProxy command */
  public interface Command {}

  /**
   * Command to emit a message through emitter.
   *
   * @sentby Any Actor
   * @receivedby {@link Sender}
   * @pattern Request-response
   */
  @Value
  public static class Send implements Command {
    GctMessage message;
  }

  /**
   * Creates an EmitterProxy actor
   *
   * @return An EmitterProxy actor
   */
  public static Behavior<Command> create() {
    return Behaviors.setup(context -> new Sender(context, Emitter.getInstance()));
  }

  private final Emitter _emitter;

  private Sender(ActorContext<Command> context, Emitter emitter) {
    super(context);
    this._emitter = emitter;
  }

  @Override
  public Receive<Command> createReceive() {
    return newReceiveBuilder()
        .onMessage(Send.class, this::sendMessage)
        .build();
  }

  /**
   * Handles the {@link Send} command.
   *
   * <p>Sends the wrapped {@link GctMessage} to {@link Emitter} instance. If
   * any exception occurred, it is logged and rethrown as a RuntimeException.
   * </p>
   *
   * @param emit the command object
   *
   * @return Same behavior
   *
   * @handles {@link Send}
   */
  @Contract("_ -> this")
  private Behavior<Command> sendMessage(@NotNull Sender.Send emit) {
    try {
      _emitter.send(emit.message());
    } catch (GctException ex) {
      getContext()
          .getLog()
          .error(
              "Sender failed to send the message: {}, Exception occurred: {}.",
              emit.message(),
              ex.getMessage());
      throw new RuntimeException(ex);
    }
    return Behaviors.stopped();
  }
}
