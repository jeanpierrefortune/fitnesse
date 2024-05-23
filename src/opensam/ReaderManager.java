package opensam;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;

public class ReaderManager {

  public ReaderManager() {
  }

  public boolean enumerateReaders(boolean flag) throws IOException, CardException {
    try {
      enumerateAndWriteReaders("FitNesseRoot\\OpenSAM\\ApplicationSettings.wiki");
    } catch (IOException | CardException e) {
      throw e;
    }
    return true;
  }

  public boolean setActiveReader(String readerName) {
    try (FileWriter writer = new FileWriter("FitNesseRoot\\OpenSAM\\ActiveReader.wiki")) {
      writer.write("Active reader: '''" + readerName + "'''");
    } catch (IOException e) {
      return false;
    }
    return true;
  }

  public String getShowValue() { return "show value returned from Java"; }

  private void enumerateAndWriteReaders(String fileName) throws IOException, CardException {
    TerminalFactory terminalFactory = TerminalFactory.getDefault();
    List<CardTerminal> terminals = null;

    try {
      terminals = terminalFactory.terminals().list();
    } catch (CardException e) {
    }

    try (FileWriter writer = new FileWriter(fileName)) {
      writer.write("!-");
      writer.write("<!DOCTYPE html>\n");
      writer.write("<html lang=\"en\">\n");
      writer.write("<head>\n");
      writer.write("<meta charset=\"UTF-8\">\n");
      writer.write("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
      writer.write("<style>\n");
      writer.write("body { font-family: Arial, sans-serif; margin: 20px; }\n");
      writer.write(".container { max-width: 600px; padding: 20px; border: 1px solid #ccc; border-radius: 8px; }\n");
      writer.write("h1 { font-size: 24px; margin-bottom: 20px; text-align: center; }\n");
      writer.write(".form-check { display: flex; align-items: center; margin-bottom: 10px; }\n");
      writer.write(".form-check input { margin-right: 10px; }\n");
      writer.write(".form-check-label { white-space: nowrap; text-align: left; }\n");
      writer.write(".btn { display: inline-block; padding: 10px 20px; font-size: 16px; color: #fff; background-color: #007bff; border: none; border-radius: 4px; cursor: pointer; text-align: center; }\n");
      writer.write(".btn:hover { background-color: #0056b3; }\n");
      writer.write(".alert { padding: 15px; background-color: #f44336; color: white; margin-bottom: 20px; border-radius: 4px; }\n");
      writer.write("</style>\n");
      writer.write("<title>PC/SC Readers</title>\n");
      writer.write("<script>\n");
      writer.write("function selectReader() {\n");
      writer.write("  const selectedReader = document.querySelector('input[name=\"reader\"]:checked');\n");
      writer.write("  if (!selectedReader) {\n");
      writer.write("    alert('Please select a reader.');\n");
      writer.write("    return;\n");
      writer.write("  }\n");
      writer.write("  const readerName = selectedReader.value;\n");
      writer.write("  fetch(`/OpenSAM.SetReader?responder=test&readerName=${encodeURIComponent(readerName)}`)\n");
      writer.write("    .then(response => response.json())\n");
      writer.write("    .then(data => {\n");
      writer.write("      // Process the JSON response and update the page content\n");
      writer.write("      if (data.results && data.results.length > 0) {\n");
      writer.write("        const resultContainer = document.createElement('div');\n");
      writer.write("        resultContainer.id = 'resultContainer';\n");
      writer.write("        document.body.appendChild(resultContainer);\n");
      writer.write("        resultContainer.innerHTML = '';\n");
      writer.write("        data.results.forEach(result => {\n");
      writer.write("          const resultElement = document.createElement('div');\n");
      writer.write("          resultElement.textContent = `Test: ${result.name}, Status: ${result.status}`;\n");
      writer.write("          resultContainer.appendChild(resultElement);\n");
      writer.write("        });\n");
      writer.write("      }\n");
      writer.write("    })\n");
      writer.write("    .catch(error => console.error('Error:', error));\n");
      writer.write("}\n");
      writer.write("</script>\n");
      writer.write("</head>\n");
      writer.write("<body>\n");
      writer.write("<div class=\"container\">\n");
      writer.write("<h1>Available PC/SC Readers</h1>\n");

      if (terminals == null || terminals.isEmpty()) {
        writer.write("<div class=\"alert\">No card terminals found.</div>\n");
      } else {
        writer.write("<form onsubmit=\"event.preventDefault(); selectReader();\">\n");
        for (int i = 0; i < terminals.size(); i++) {
          CardTerminal terminal = terminals.get(i);
          writer.write("<div class=\"form-check\">\n");
          writer.write(String.format(
            "<input type=\"radio\" id=\"reader%d\" name=\"reader\" value=\"%s\">\n",
            i, terminal.getName()));
          writer.write(String.format(
            "<label class=\"form-check-label\" for=\"reader%d\">%s</label>\n",
            i, terminal.getName()));
          writer.write("</div>\n");
        }
        writer.write("<button type=\"submit\">Select Reader</button>\n");
        writer.write("</form>\n");
      }

      writer.write("</div>\n");
      writer.write("</body>\n");
      writer.write("</html>\n");
      writer.write("-!");
    }
  }
}
