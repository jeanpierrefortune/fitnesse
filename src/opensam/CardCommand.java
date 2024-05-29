package opensam;

import javax.smartcardio.*;
import java.util.List;

public class CardCommand {
  private CardChannel channel;
  private ResponseAPDU response;

  public CardCommand(String readerName) throws CardException {
    // 1. Obtenir la liste des terminaux disponibles
    TerminalFactory factory = TerminalFactory.getDefault();
    List<CardTerminal> terminals = factory.terminals().list();

    // 2. Trouver le terminal correspondant au nom donné
    CardTerminal terminal = terminals.stream()
      .filter(t -> t.getName().contains(readerName))
      .findFirst()
      .orElseThrow(() -> new CardException("Lecteur non trouvé : " + readerName));

    // 3. Établir une connexion avec la carte dans le terminal
    Card card = terminal.connect("*");
    channel = card.getBasicChannel();
  }

  public void sendApdu(String apduHex) throws CardException {
    // 4. Convertir la chaîne hexadécimale en tableau d'octets
    byte[] apduBytes = hexStringToByteArray(apduHex);

    // 5. Envoyer l'APDU et recevoir la réponse
    response = channel.transmit(new CommandAPDU(apduBytes));

    // 6. Vérifier le statut de la réponse (SW1-SW2)
    if (response.getSW() != 0x9000) {
      throw new CardException("Erreur APDU : " + String.format("%04X", response.getSW()));
    }
  }

  public String getApduResponse() {
    // (La réponse est déjà stockée dans 'response' après sendApdu)
    return byteArrayToHexString(response.getBytes());
  }

  public String getApduData() {
    // (La réponse est déjà stockée dans 'response' après sendApdu)
    return byteArrayToHexString(response.getData());
  }

  // Méthodes utilitaires pour la conversion hexadécimal <-> tableau d'octets
  private static byte[] hexStringToByteArray(String s) {
    int len = s.length();

    // Vérification de la longueur paire
    if (len % 2 != 0) {
      throw new IllegalArgumentException("Chaîne hexadécimale invalide : longueur impaire");
    }

    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      int firstDigit = Character.digit(s.charAt(i), 16);
      int secondDigit = Character.digit(s.charAt(i + 1), 16);

      // Vérification des chiffres hexadécimaux valides (0-9, A-F)
      if (firstDigit == -1 || secondDigit == -1) {
        throw new IllegalArgumentException("Chaîne hexadécimale invalide : caractères non hexadécimaux");
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

