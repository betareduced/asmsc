package lk.mobitel.telco.asmsc.map;

import lk.mobitel.telco.asmsc.map.constant.MessageType;
import lk.mobitel.telco.asmsc.map.constant.ParameterName;
import lk.mobitel.telco.asmsc.map.constant.Primitive;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * High-level interface for MAP Messages.
 *
 * <p>ASMSC Core delivers objects of this interface to Processors from Adapters. Messages are
 * immutable, so there are no setters in this interface. Implementations can provide means to set
 * values.
 *
 * @author NISALBA
 */
public interface Message {

  /**
   * Gets the type of the message. Type of the message indicates what kind of message is this.
   *
   * @return the type of the message.
   */
  MessageType getType();

  /**
   * Gets the primitive type of the message.
   *
   * @return The primitive of the message.
   */
  Primitive getPrimitive();

  /**
   * Gets the id of the message. This id is used to distinguish the internal dialogue entity for
   * which the message is destined.
   *
   * @return The id of the message.
   */
  int getId();

  /**
   * Gets the source of the message.
   *
   * @return The source id of the message
   */
  int getSource();

  /**
   * Gets the destination of the message.
   *
   * @return The destination id of the message.
   */
  int getDestination();

  /**
   * Gets the origin of the message. The origin denotes the actual originating place of the message.
   * Meanwhile, source {@link #getSource()} denotes from where the message is received to this
   * application. That could be an intermediate processor, relaying the message to this application,
   * but origin is where the message originally emitted.
   *
   * @return The origin id of the message
   */
  long getInstance();

  /**
   * Gets the response requirement of the message. This information is used by the originator of a
   * message to indicate whether it requires confirmation from the receiving module that the message
   * has been received.
   *
   * @return True is response is required for this message.
   */
  boolean isResponseRequired();

  /**
   * Gets a parameter by its name.
   *
   * @param name The parameter name to lookup
   * @return The parameter object.
   * @throws NoSuchElementException in case of the given name could not be found in this message.
   */
  Parameter getParameter(ParameterName name) throws NoSuchElementException;

  /**
   * True if a parameter of the specified name is present in this message.
   *
   * @param name The parameter name to lookup.
   * @return True if the parameter is present.
   */
  boolean hasParameter(ParameterName name);

  /**
   * Gets the number of parameters in this message.
   *
   * @return The number of parameters in this message.
   */
  int parameterCount();

  /**
   * Gets all parameters as a list.
   *
   * @return The list of parameters in this message.
   */
  List<? extends Parameter> parameterList();
}
