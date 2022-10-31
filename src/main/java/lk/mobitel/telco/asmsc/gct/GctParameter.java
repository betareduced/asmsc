package lk.mobitel.telco.asmsc.gct;

import com.dialogic.signaling.gct.BBUtil;
import lk.mobitel.telco.asmsc.map.Parameter;
import lk.mobitel.telco.asmsc.map.constant.ParameterName;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

public class GctParameter implements Parameter {
  public static GctParameter of(Parameter parameter) {
    return (GctParameter) parameter;
  }

  @Contract("_, _ -> new")
  public static @NotNull GctParameter ofBytes(ParameterName name, byte[] data) {
    return new GctParameter(name, ByteBuffer.wrap(data));
  }

  @Contract("_, _ -> new")
  public static @NotNull GctParameter ofBytes(ParameterName name, ByteBuffer data) {
    return new GctParameter(name, data);
  }

  @Contract("_, _ -> new")
  public static @NotNull GctParameter of8BitValue(ParameterName name, byte value) {
    var b = ByteBuffer.allocate(1);
    BBUtil.putU8(b, value);
    return new GctParameter(name, b.rewind());
  }

  @Contract("_, _ -> new")
  public static @NotNull GctParameter of16BitValue(ParameterName name, short value) {
    var b = ByteBuffer.allocate(2);
    BBUtil.putU16(b, value);
    return new GctParameter(name, b.rewind());
  }

  @Contract("_, _ -> new")
  public static @NotNull GctParameter of24BitValue(ParameterName name, int value) {
    var b = ByteBuffer.allocate(3);
    BBUtil.putU24(b, value);
    return new GctParameter(name, b.rewind());
  }

  @Contract("_, _ -> new")
  public static @NotNull GctParameter of32BitValue(ParameterName name, int value) {
    var b = ByteBuffer.allocate(4);
    BBUtil.putU32(b, value);
    return new GctParameter(name, b.rewind());
  }

  private final ParameterName name;
  private final ByteBuffer data;

  public GctParameter(ParameterName name, @NotNull ByteBuffer data) {
    this.name = name;
    this.data = data.rewind();
  }

  @Override
  public ParameterName getName() {
    return name;
  }

  @Override
  public int getLength() {
    return data.capacity();
  }

  @Override
  public ByteBuffer getData() {
    return data.asReadOnlyBuffer();
  }

  @Override
  public String toString() {
    StringBuilder bytes = new StringBuilder();

    bytes.append("[");
    ByteBuffer buf = getData();
    while (buf.hasRemaining()) {
      bytes.append(String.format("%02x,", buf.get() & 0xFF));
    }
    bytes.replace(bytes.length() - 1, bytes.length(), "]");

    return String.format(
        "{ \"name\": \"%s\", \"length\": %d, \"data\": %s }", name.mnemonic(), getLength(), bytes);
  }
}
