package cna.settings;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

public class SettingsPageBuilder {

  public SettingsPageBuilder() {}

  public boolean build(String profileName) throws IOException {
    Map<String, String> variables =
        extractTableData("FitNesseRoot/FitNesse/ApplicationSettings/Profiles/Profile1.wiki");
    String fileName = "FitNesseRoot/FitNesse/ApplicationSettings/Settings.wiki";
    // Initialize Velocity
    VelocityEngine velocityEngine = new VelocityEngine();
    velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
    velocityEngine.setProperty(
        RuntimeConstants.FILE_RESOURCE_LOADER_PATH, "FitNesseRoot/files/fitnesse/templates/");
    velocityEngine.init();

    // Load the template
    Template template = velocityEngine.getTemplate("settings.vm");
    // Create context and add data
    VelocityContext context = new VelocityContext();
    context.put("variables", variables);

    // Render template to a StringWriter
    StringWriter writer = new StringWriter();
    template.merge(context, writer);

    // Write the final HTML to the file
    try (FileWriter fileWriter = new FileWriter(fileName)) {
      fileWriter.write(writer.toString());
    }
    return true;
  }

  private Map<String, String> extractTableData(String fileName) throws IOException {
    Map<String, String> tableMap = new HashMap<>();
    String text = new String(Files.readAllBytes(Paths.get(fileName)));

    // Regular expression to match table rows
    Pattern pattern = Pattern.compile("\\|(.*?)\\|(.*?)\\|");
    Matcher matcher = pattern.matcher(text);

    while (matcher.find()) {
      String key = matcher.group(1).trim();
      String value = matcher.group(2).trim();
      tableMap.put(key, value);
    }

    return tableMap;
  }
}
