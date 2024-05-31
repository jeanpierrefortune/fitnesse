package cna.crypto;

import java.security.SecureRandom;
import org.eclipse.keyple.core.util.HexUtil;

public class Crypto {
  public Crypto() {}

  public String generateAlea(int length) {
    SecureRandom sr = new SecureRandom();
    byte[] randomBytes = new byte[length];
    sr.nextBytes(randomBytes);

    return HexUtil.toHex(randomBytes);
  }

  public String shiftLeft(String hexString, int shift) {
    // Convert hex string to byte array
    byte[] byteArray = HexUtil.toByteArray(hexString);

    // Determine the number of whole bytes and remaining bits to shift
    int byteShift = shift / 8;
    int bitShift = shift % 8;

    // Create a new array to store the shifted result
    byte[] shiftedArray = new byte[byteArray.length];

    for (int i = 0; i < byteArray.length; i++) {
      // Calculate the source index adjusted by the byte shift
      int srcIndex = i + byteShift;

      if (srcIndex < byteArray.length) {
        // Shift the current byte
        shiftedArray[i] = (byte) (byteArray[srcIndex] << bitShift);

        // If there's a remaining bit shift, carry bits from the next byte
        if (srcIndex + 1 < byteArray.length && bitShift != 0) {
          shiftedArray[i] |= (byteArray[srcIndex + 1] & 0xFF) >>> (8 - bitShift);
        }
      } else {
        // Beyond the length of the array, just add zeros
        shiftedArray[i] = 0;
      }
    }

    // Convert the shifted byte array back to a hex string
    return HexUtil.toHex(shiftedArray);
  }
}
