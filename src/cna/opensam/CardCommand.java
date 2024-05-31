package cna.opensam;

import javax.smartcardio.*;

public class CardCommand {
  private CardChannel channel;
  private ResponseAPDU response;

  public CardCommand(String readerName) throws CardException {
    // Use the singleton instance to get the channel for the specific reader name
    this.channel = CardContext.getInstance(readerName).getChannel();
  }

  public void sendApdu(String apduHex) throws CardException {
    // Convert the hex string to a byte array
    byte[] apduBytes = hexStringToByteArray(apduHex);

    // Send the APDU and receive the response
    response = channel.transmit(new CommandAPDU(apduBytes));
  }

  public void setApdu(String apduHex) throws CardException {
    sendApdu(apduHex);
  }

  public String getApduResponse() {
    // The response is already stored in 'response' after sendApdu
    return byteArrayToHexString(response.getBytes());
  }

  public String apduResponse() {
    return getApduResponse();
  }

  public String getApduData() {
    // The response is already stored in 'response' after sendApdu
    return byteArrayToHexString(response.getData());
  }

  public String apduData() {
    return getApduData();
  }

  // Utility methods for hex string <-> byte array conversion
  private static byte[] hexStringToByteArray(String s) {
    int len = s.length();

    // Check for even length
    if (len % 2 != 0) {
      throw new IllegalArgumentException("Invalid hex string: odd length");
    }

    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      int firstDigit = Character.digit(s.charAt(i), 16);
      int secondDigit = Character.digit(s.charAt(i + 1), 16);

      // Check for valid hex digits (0-9, A-F)
      if (firstDigit == -1 || secondDigit == -1) {
        throw new IllegalArgumentException("Invalid hex string: non-hex characters");
      }

      data[i / 2] = (byte) ((firstDigit << 4) + secondDigit);
    }
    return data;
  }

  private static String byteArrayToHexString(byte[] bytes) {
    StringBuilder sb = new StringBuilder(bytes.length * 2);
    for (byte b : bytes) {
      sb.append(String.format("%02X", b));
    }
    return sb.toString();
  }
}
