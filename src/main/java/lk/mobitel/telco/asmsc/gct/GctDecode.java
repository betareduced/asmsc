package lk.mobitel.telco.asmsc.gct;

import com.dialogic.signaling.gct.BBUtil;
import lk.mobitel.telco.asmsc.map.constant.*;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.NoSuchElementException;

/**
 * Objects of {@link GctParameter} does not provide methods to decode their data in further formats.
 * This is to limit the complexity of the class. As there are many MAP specific data types. It is
 * not feasible for {@link GctParameter} to provide algorithms for all these data types.
 *
 * <p>This class provides these algorithms as static functions. This separate the algorithms
 * development from Parameter logic.
 *
 * @author NISALBA
 */
public class GctDecode {
  public static boolean asBoolean(@NotNull GctParameter parameter) {
    short value = BBUtil.getU8(parameter.getData());
    return value == 1;
  }

  public static short asByte(@NotNull GctParameter parameter) {
    return BBUtil.getU8(parameter.getData());
  }

  public static int asInt(@NotNull GctParameter parameter) {
    ByteBuffer buffer = parameter.getData();
    switch (buffer.capacity()) {
      case 1:
        return BBUtil.getU8(buffer);
      case 2:
        return BBUtil.getU16(buffer);
      default:
        /* consider upto 3 bytes only */
        return BBUtil.getU24(buffer);
    }
  }

  public static long asLong(@NotNull GctParameter parameter) {
    ByteBuffer buffer = parameter.getData();
    switch (buffer.capacity()) {
      case 1:
        return BBUtil.getU8(buffer);
      case 2:
        return BBUtil.getU16(buffer);
      case 3:
        return BBUtil.getU24(buffer);
      default:
        /* consider upto 4 bytes only */
        return BBUtil.getU32(buffer);
    }
  }

  public static @NotNull ApplicationContext asMobileDomainApplicationContext(
      @NotNull GctParameter parameter) throws IllegalArgumentException, NoSuchElementException {
    ByteBuffer buffer = parameter.getData();

    // assumed at lease 9 bytes in the parameter area
    if (buffer.capacity() < 9)
      throw new IllegalArgumentException(
          "Mobile Domain Application Context is expected to have at least 9 octets");

    byte mapAc = (byte) BBUtil.getU8(buffer, 7);
    byte version = (byte) BBUtil.getU8(buffer, 8);

    return ApplicationContext.of(mapAc, version);
  }

  public static @NotNull UserReason asUserReason(@NotNull GctParameter parameter)
      throws NoSuchElementException {
    return UserReason.of(asByte(parameter));
  }

  public static @NotNull ProviderReason asProviderReason(@NotNull GctParameter parameter)
      throws NoSuchElementException {
    return ProviderReason.of(asByte(parameter));
  }

  public static @NotNull MAPResult asMAPResult(@NotNull GctParameter parameter)
      throws NoSuchElementException {
    return MAPResult.of(asByte(parameter));
  }

  public static @NotNull RefuseReason asRefuseReason(@NotNull GctParameter parameter)
      throws NoSuchElementException {
    return RefuseReason.of(asByte(parameter));
  }

  public static @NotNull ProblemDiagnostic asProblemDiagnostic(@NotNull GctParameter parameter)
      throws NoSuchElementException {
    return ProblemDiagnostic.of(asByte(parameter));
  }

  public static @NotNull UserError asUserError(@NotNull GctParameter parameter)
      throws NoSuchElementException {
    return UserError.of(asByte(parameter));
  }

  public static @NotNull ProviderError asProviderError(@NotNull GctParameter parameter)
      throws NoSuchElementException {
    return ProviderError.of(asByte(parameter));
  }
}
