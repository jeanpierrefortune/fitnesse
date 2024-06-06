package cna.settings;

import cna.opensam.ReaderManager;
import junit.framework.TestCase;

public class SettingsPageBuilderTest extends TestCase {
  public void testBuild() throws Exception {
    SettingsPageBuilder settingsPageBuilder = new SettingsPageBuilder();
    settingsPageBuilder.build("abc");
  }
}
