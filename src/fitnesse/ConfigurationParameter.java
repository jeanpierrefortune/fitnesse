package fitnesse;

import util.FileUtil;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Parameters used to configure FitNesse.
 */
public enum ConfigurationParameter {

  CONFIG_FILE("ConfigFile"),
  LOG_LEVEL("LogLevel"),
  LOG_DIRECTORY("LogDirectory"),
  CREDENTIALS("Credentials"),
  ROOT_PATH("RootPath"),
  ROOT_DIRECTORY("FitNesseRoot"),
  PORT("Port"),
  OUTPUT("RedirectOutput"),
  OMITTING_UPDATES("OmittingUpdates"),
  INSTALL_ONLY("InstallOnly"),
  COMMAND("Command"),
  WIKI_PAGE_FACTORY_CLASS("WikiPageFactory"),
  WIKI_PAGE_FACTORIES("WikiPageFactories"),
  PLUGINS("Plugins"),
  RESPONDERS("Responders"),
  TEST_SYSTEMS("TestSystems"),
  FORMATTERS("Formatters"),
  SYMBOL_TYPES("SymbolTypes"),
  SLIM_TABLES("SlimTables"),
  AUTHENTICATOR("Authenticator"),
  CUSTOM_COMPARATORS("CustomComparators"),
  CONTENT_FILTER("ContentFilter"),
  VERSIONS_CONTROLLER_CLASS("VersionsController"),
  VERSIONS_CONTROLLER_DAYS("VersionsController.days"),
  RECENT_CHANGES_CLASS("RecentChanges"),
  CONTEXT_ROOT("ContextRoot"),
  LOCALHOST_ONLY("LocalhostOnly"),
  MAXIMUM_WORKERS("MaximumWorkers"),
  THEME("Theme"),
  PURGE_OPTIONS("TestHistory.purgeOptions");

  private static final Logger LOG = Logger.getLogger(ConfigurationParameter.class.getName());

  private final String name;

  private ConfigurationParameter(String key) {
    this.name = key;
  }

  public String getKey() {
    return name;
  }

  public static Properties makeProperties(Object... keyValuePairs) {
    if (keyValuePairs.length % 2 != 0) {
      throw new IllegalArgumentException("Number of arguments should be even (name, value)");
    }

    Properties properties = new Properties();
    for (int i = 0; i < keyValuePairs.length; i += 2) {
      String key = keyValuePairs[i] instanceof ConfigurationParameter ? ((ConfigurationParameter) keyValuePairs[i]).getKey() : keyValuePairs[i].toString();
      String value = keyValuePairs[i+1].toString();
      properties.setProperty(key, value);
    }
    return properties;
  }

  public static Properties loadProperties(File propertiesFile) {
    Properties properties = new Properties();
    String resourcePath = convertFilePathToResourcePath(propertiesFile.getPath());

    try (InputStream in = ConfigurationParameter.class.getResourceAsStream(resourcePath)) {
      if (in != null) {
        properties.load(in);
      } else {
        System.out.println("Configuration file not found within resources (" + resourcePath + ")");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return properties;
  }

  private static String convertFilePathToResourcePath(String filePath) {
    // Replace system-specific file separators with '/'
    String resourcePath = filePath.replace(File.separatorChar, '/');

    // Remove any leading "./" or ".\" from the path
    if (resourcePath.startsWith("./")) {
      resourcePath = resourcePath.substring(2);
    } else if (resourcePath.startsWith(".\\")) {
      resourcePath = resourcePath.substring(2);
    }

    // Ensure the path starts with a leading slash
    if (!resourcePath.startsWith("/")) {
      resourcePath = "/" + resourcePath;
    }

    return resourcePath;
  }

  private static String getCanonicalPath(File propertiesFile) {
    try {
      return propertiesFile.getCanonicalPath();
    } catch (IOException e) {
      return propertiesFile.toString();
    }
  }

  public static ConfigurationParameter byKey(String key) {
    for (ConfigurationParameter parameter : ConfigurationParameter.values()) {
      if (parameter.getKey().equals(key)) {
        return parameter;
      }
    }
    return null;
  }
}
