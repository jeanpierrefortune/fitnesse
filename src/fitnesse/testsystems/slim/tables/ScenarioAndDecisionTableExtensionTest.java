// Copyright (C) 2003-2009 by Object Mentor, Inc. All rights reserved.
// Released under the terms of the CPL Common Public License version 1.0.
package fitnesse.testsystems.slim.tables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fitnesse.testsystems.slim.SlimCommandRunningClient;
import fitnesse.slim.instructions.CallInstruction;
import fitnesse.slim.instructions.Instruction;
import fitnesse.testsystems.slim.HtmlTableScanner;
import fitnesse.testsystems.slim.SlimTestContext;
import fitnesse.testsystems.slim.SlimTestContextImpl;
import fitnesse.testsystems.slim.Table;
import fitnesse.testsystems.slim.TableScanner;
import fitnesse.wiki.WikiPage;
import fitnesse.wiki.WikiPageUtil;
import fitnesse.wiki.mem.InMemoryPage;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static util.ListUtility.list;

public class ScenarioAndDecisionTableExtensionTest extends SlimTestContextImpl {
  private static final String SCEN_EXTENSION_NAME = "diffScriptScenario";
  private static final String SCRIPT_EXTENSION_NAME = "diffScript";

  private WikiPage root;
  private List<SlimAssertion> assertions;
  private ScenarioTable st;
  private DecisionTable dt;

  private List<Instruction> instructions() {
    return SlimAssertion.getInstructions(assertions);
  }

  @Before
  public void setUp() throws Exception {
    SlimTableFactory.addTableType(SCEN_EXTENSION_NAME, ScenarioTableWithDifferentScript.class);
    SlimTableFactory.addTableType(SCRIPT_EXTENSION_NAME, DiffScriptTable.class);
    root = InMemoryPage.makeRoot("root");
    assertions = new ArrayList<SlimAssertion>();
    clearTestSummary();
  }

  private void makeTables(String scenarioText, String scriptText) throws Exception {
    String tableText = "!|" + SCEN_EXTENSION_NAME + "|" + scenarioText + "|\n"
            + "\n"
            + "!|DT:" + scriptText + "|\n";
    WikiPageUtil.setPageContents(root, tableText);
    TableScanner ts = new HtmlTableScanner(root.getData().getHtml());
    Table t = ts.getTable(0);
    st = new ScenarioTableWithDifferentScript(t, "s_id", this);
    t = ts.getTable(1);
    dt = new DecisionTable(t, "did", this);
    assertions.addAll(st.getAssertions());
    assertions.addAll(dt.getAssertions());
  }

  @Test
  public void bracesAroundArgumentInTable() throws Exception {
    makeTables(
      "echo|user|giving|user_old|\n" +
        "|check|echo|@{user}|@{user_old}",
      "EchoGiving|\n" +
        "|user|user_old|\n" +
        "|7|7"
    );
    Map<String, Object> pseudoResults = SlimCommandRunningClient.resultToMap(
            list(
                    list("decisionTable_did_0/diffScriptTable_s_id_0", "7")
            )
    );
    SlimAssertion.evaluateExpectations(assertions, pseudoResults);

    String scriptTable = dt.getChildren().get(0).getTable().toString();
    String expectedScript =
      "[[diffScriptScenario, echo, user, giving, user_old], [check, echo, 7, pass(7)]]";
    assertEquals(expectedScript, scriptTable);
    String dtHtml = dt.getTable().toString();
    assertEquals(1, getTestSummary().getRight());
    assertEquals(0, getTestSummary().getWrong());
    assertEquals(0, getTestSummary().getIgnores());
    assertEquals(0, getTestSummary().getExceptions());
  }

  @Test
  public void oneInput() throws Exception {
    makeTables(
      "myScenario|input|\n" +
        "|function|@input",
      "myScenario|\n" +
        "|input|\n" +
        "|7"
    );
    List<CallInstruction> expectedInstructions =
      list(
              new CallInstruction("decisionTable_did_0/diffScriptTable_s_id_0", "diffScriptTableActor", "function", new Object[]{"7"})
      );
    assertEquals(expectedInstructions, instructions());
  }

  @Test
  public void manyInputsAndRows() throws Exception {
    makeTables(
      "login|user name|password|password|pin|pin|\n" +
        "|login|@userName|with password|@password|and pin|@pin|\n" +
        "|show|currentUserProfileUrl",
      "LoginPasswordPin|\n" +
        "|user name|password|pin|\n" +
        "|bob|xyzzy|7734|\n" +
        "|bill|yabba|8892"
    );
    List<CallInstruction> expectedInstructions =
      list(
              new CallInstruction("decisionTable_did_0/diffScriptTable_s_id_0", "diffScriptTableActor", "loginWithPasswordAndPin", new Object[]{"bob", "xyzzy", "7734"}),
              new CallInstruction("decisionTable_did_0/diffScriptTable_s_id_1", "diffScriptTableActor", "currentUserProfileUrl", new Object[0]),
              new CallInstruction("decisionTable_did_1/diffScriptTable_s_id_0", "diffScriptTableActor", "loginWithPasswordAndPin", new Object[]{"bill", "yabba", "8892"}),
              new CallInstruction("decisionTable_did_1/diffScriptTable_s_id_1", "diffScriptTableActor", "currentUserProfileUrl", new Object[0])
      );
    assertEquals(expectedInstructions, instructions());
  }

  @Test
  public void simpleInputAndOutputPassing() throws Exception {
    makeTables(
      "echo|input|giving|output|\n" +
        "|check|echo|@input|@output",
      "EchoGiving|\n" +
        "|input|output|\n" +
        "|7|7"
    );
    Map<String, Object> pseudoResults = SlimCommandRunningClient.resultToMap(
            list(
                    list("decisionTable_did_0/diffScriptTable_s_id_0", "7")
            )
    );
    SlimAssertion.evaluateExpectations(assertions, pseudoResults);

    String scriptTable = dt.getChildren().get(0).getTable().toString();
    String expectedScript =
      "[[diffScriptScenario, echo, input, giving, output], [check, echo, 7, pass(7)]]";
    assertEquals(expectedScript, scriptTable);
    String dtHtml = dt.getTable().toString();
    assertEquals(1, getTestSummary().getRight());
    assertEquals(0, getTestSummary().getWrong());
    assertEquals(0, getTestSummary().getIgnores());
    assertEquals(0, getTestSummary().getExceptions());
  }

  @Test
  public void simpleInputAndOutputFailing() throws Exception {
    makeTables(
      "echo|input|giving|output|\n" +
        "|check|echo|@input|@output",
      "EchoGiving|\n" +
        "|input|output|\n" +
        "|7|8"
    );
    Map<String, Object> pseudoResults = SlimCommandRunningClient.resultToMap(
            list(
                    list("decisionTable_did_0/diffScriptTable_s_id_0", "7")
            )
    );
    SlimAssertion.evaluateExpectations(assertions, pseudoResults);

    String scriptTable = dt.getChildren().get(0).getTable().toString();
    String expectedScript =
      "[[diffScriptScenario, echo, input, giving, output], [check, echo, 7, fail(a=7;e=8)]]";
    assertEquals(expectedScript, scriptTable);
    String dtHtml = dt.getTable().toString();
    assertEquals(0, getTestSummary().getRight());
    assertEquals(1, getTestSummary().getWrong());
    assertEquals(0, getTestSummary().getIgnores());
    assertEquals(0, getTestSummary().getExceptions());
  }

  @Test(expected=SyntaxError.class)
  public void scenarioHasTooFewArguments() throws Exception {
    makeTables(
      "echo|input|giving|\n" +
        "|check|echo|@input|@output",
      "EchoGiving|\n" +
        "|input|output|\n" +
        "|7|8"
    );
  }

  @Test
  public void scenarioHasExtraArgumentsThatAreIgnored() throws Exception {
    makeTables(
      "echo|input|giving|output||output2|\n" +
        "|check|echo|@input|@output",
      "EchoGiving|\n" +
        "|input|output|\n" +
        "|7|7"
    );
    Map<String, Object> pseudoResults = SlimCommandRunningClient.resultToMap(
            list(
                    list("decisionTable_did_0/diffScriptTable_s_id_0", "7")
            )
    );
    SlimAssertion.evaluateExpectations(assertions, pseudoResults);

    String scriptTable = dt.getChildren().get(0).getTable().toString();
    String expectedScript =
      "[[diffScriptScenario, echo, input, giving, output, , output2], [check, echo, 7, pass(7)]]";
    assertEquals(expectedScript, scriptTable);
    String dtHtml = dt.getTable().toString();
  }

  /**
   * ScenarioTable that does not make ScriptTables, but DiffScriptTables.
   */
  public static class ScenarioTableWithDifferentScript extends ScenarioTable {

    public ScenarioTableWithDifferentScript(Table table, String tableId, SlimTestContext testContext) {
      super(table, tableId, testContext);
    }
    @Override
    protected ScriptTable createChild(ScenarioTestContext testContext, Table newTable) {
      return new DiffScriptTable(newTable, id, testContext);
    }
  }

  /**
   * Special script table.
   */
  public static class DiffScriptTable extends ScriptTable {

    public DiffScriptTable(Table table, String tableId, SlimTestContext context) {
      super(table, tableId, context);
    }
    protected String getTableType() {
      return "diffScriptTable";
    }

  }
}
