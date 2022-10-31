package lk.mobitel.telco.asmsc.gct;

import com.dialogic.signaling.gct.BBUtil;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

/**
 * This class combines the use of {@code com.dialogic.signaling.gct.BBUtil} with Java ByteBuffers.
 * While {@code BBUtil} class provides correct methods to extract values from ByteBuffers, those
 * methods are static and stateless. Which is not pleasant when parsing byte buffers.
 *
 * @author NISALBA
 */
public class GctByteBuffer {
  private final ByteBuffer buffer;

  public GctByteBuffer(@NotNull ByteBuffer buffer) {
    this.buffer = buffer.rewind();
  }

  public boolean hasRemaining() {
    return buffer.hasRemaining();
  }

  public GctByteBuffer rewind() {
    buffer.rewind();
    return this;
  }

  public short next8() {
    return BBUtil.getU8(buffer);
  }

  public short at8(int offset) {
    return BBUtil.getU8(buffer, offset);
  }

  public int next16() {
    return BBUtil.getU16(buffer);
  }

  public int at16(int offset) {
    return BBUtil.getU16(buffer, offset);
  }

  public int next24() {
    return BBUtil.getU24(buffer);
  }

  public int at24(int offset) {
    return BBUtil.getU24(buffer, offset);
  }

  public long next32() {
    return BBUtil.getU32(buffer);
  }

  public long at32(int offset) {
    return BBUtil.getU32(buffer, offset);
  }

  public void put8(int value) {
    BBUtil.putU8(buffer, value);
  }

  public void put8(int value, int offset) {
    BBUtil.putU8(buffer, offset, value);
  }

  public void put16(int value) {
    BBUtil.putU16(buffer, value);
  }

  public void put16(int value, int offset) {
    BBUtil.putU16(buffer, offset, value);
  }

  public void put24(int value) {
    BBUtil.putU24(buffer, value);
  }

  public void put24(int value, int offset) {
    BBUtil.putU24(buffer, offset, value);
  }

  public void put32(long value) {
    BBUtil.putU32(buffer, value);
  }

  public void put32(long value, int offset) {
    BBUtil.putU32(buffer, offset, value);
  }
}
