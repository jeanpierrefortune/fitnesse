package opensam;

import fitnesse.ContextConfigurator;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;

public class ReaderManager {
  private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(ContextConfigurator.class.getName());
  static {
    try {
      FileHandler fileHandler = new FileHandler("fitnesse.log", true);
      fileHandler.setFormatter(new SimpleFormatter());
      LOG.addHandler(fileHandler);
      LOG.setLevel(java.util.logging.Level.ALL);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  public ReaderManager() {
    LOG.log(Level.WARNING, "ReaderManager instantiated");
  }

  public boolean enumerateReaders(boolean flag) {
    try {
      LOG.log(Level.WARNING, "Enumerating readers - start");
      enumerateAndWriteReaders("FitNesseRoot\\OpenSam\\ApplicationSettings.wiki");
      LOG.log(Level.WARNING, "Enumerating readers - end");
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  public boolean setActiveReader(String readerName) {
    try (FileWriter writer = new FileWriter("FitNesseRoot\\OpenSam\\ActiveReader.wiki")) {
      writer.write("Active reader: '''" + readerName + "'''");
    } catch (IOException e) {
      return false;
    }
    return true;
  }

  public String getShowValue() {
    return "show value returned from Java";
  }

  private void enumerateAndWriteReaders(String fileName) throws Exception {
    TerminalFactory terminalFactory = TerminalFactory.getDefault();
    List<CardTerminal> terminals = new ArrayList<>();

    try {
      terminals = terminalFactory.terminals().list();
    } catch (Exception e) {
      if (!e.getCause().getMessage().contains("SCARD_E_NO_READERS_AVAILABLE")) {
        throw e;
      }
      // Log the exception if needed
      e.printStackTrace();
    }

    // Initialize Velocity
    VelocityEngine velocityEngine = new VelocityEngine();
    velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
    velocityEngine.setProperty("classpath.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    velocityEngine.init();

    // Load the template
    Template template = velocityEngine.getTemplate("fitnesse/resources/templates/selectReader.vm");
    // Create context and add data
    VelocityContext context = new VelocityContext();
    context.put("terminals", terminals);

    // Render template to a StringWriter
    StringWriter writer = new StringWriter();
    template.merge(context, writer);

    // Write the final HTML to the file
    try (FileWriter fileWriter = new FileWriter(fileName)) {
      fileWriter.write(writer.toString());
    }
  }
}
