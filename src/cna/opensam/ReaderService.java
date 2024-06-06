package cna.opensam;

import org.eclipse.keyple.card.generic.GenericExtensionService;
import org.eclipse.keyple.core.service.Plugin;
import org.eclipse.keyple.core.service.SmartCardService;
import org.eclipse.keyple.core.service.SmartCardServiceProvider;
import org.eclipse.keyple.plugin.pcsc.PcscPluginFactoryBuilder;

public class ReaderService {
  private static final ReaderService INSTANCE = new ReaderService();
  private SmartCardService smartCardService;
  private Plugin plugin;
  private GenericExtensionService genericCardService;

  private ReaderService() {}

  public static ReaderService getInstance() {
    return INSTANCE;
  }

  public void initialize() {
    smartCardService = SmartCardServiceProvider.getService();
    plugin = smartCardService.registerPlugin(PcscPluginFactoryBuilder.builder().build());
    genericCardService = GenericExtensionService.getInstance();
    smartCardService.checkCardExtension(genericCardService);
  }
}
