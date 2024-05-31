package cna.opensam;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.smartcardio.*;

public class CardContext {
  private static final Map<String, CardContext> instances = new ConcurrentHashMap<>();
  private CardChannel channel;

  private CardContext(String readerName) throws CardException {
    // 1. Obtenir la liste des terminaux disponibles
    TerminalFactory factory = TerminalFactory.getDefault();
    List<CardTerminal> terminals = factory.terminals().list();

    // 2. Trouver le terminal correspondant au nom donné
    CardTerminal terminal =
        terminals.stream()
            .filter(t -> t.getName().contains(readerName))
            .findFirst()
            .orElseThrow(() -> new CardException("Lecteur non trouvé : " + readerName));

    // 3. Établir une connexion avec la carte dans le terminal
    Card card = terminal.connect("*");
    channel = card.getBasicChannel();
  }

  public static CardContext getInstance(String readerName) throws CardException {
    return instances.computeIfAbsent(
        readerName,
        name -> {
          try {
            return new CardContext(name);
          } catch (CardException e) {
            throw new RuntimeException(e);
          }
        });
  }

  public CardChannel getChannel() {
    return channel;
  }
}
