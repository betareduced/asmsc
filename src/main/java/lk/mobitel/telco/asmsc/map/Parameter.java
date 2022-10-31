package lk.mobitel.telco.asmsc.map;

import lk.mobitel.telco.asmsc.map.constant.ParameterName;

import java.nio.ByteBuffer;

/**
 * GctMsg defines a common format for all message parameters. They all have three components:
 *
 * <ol>
 *   <li>Name
 *   <li>Length
 *   <li>Data
 * </ol>
 *
 * However, this format is not much useful at higher levels. The <i>Data</i> field can be further
 * parsed into more descriptive values. Implementations of this interface can implement special
 * methods to retrieve these values.
 */
public interface Parameter {

  /**
   * Gets the <i>Name</i> of the parameter. <i>Name</i> refers to the byte value at the beginning of
   * the parameter.
   *
   * <p>Here the method returns an enum of type {@link ParameterName}. This includes both thw byte
   * value and a descriptive name for the parameter.
   *
   * @return The name value of the MAP parameter.
   */
  ParameterName getName();

  /**
   * Gets the length of the <i>Data</i> field.
   *
   * @return The length of the <i>Data</i> field.
   */
  int getLength();

  /**
   * Gets the raw <i>Data</i> field as a short-buffer.
   *
   * @return The raw <i>Data</i> field as a short-buffer
   */
  ByteBuffer getData();
}
