package fitnesse.wikitext.test;

import fitnesse.html.HtmlElement;
import fitnesse.wiki.WikiPage;
import fitnesse.wikitext.parser.HtmlTranslator;
import fitnesse.wikitext.parser.ParsingPage;
import fitnesse.wikitext.parser.Paths;
import fitnesse.wikitext.parser.WikiSourcePage;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import java.util.List;

public class PathTest {
    @Test
    public void scansPaths() {
        ParserTest.assertScansTokenType("!path stuff", "Path", true);
    }

    @Test
    public void translatesPaths() throws Exception {
        ParserTest.assertTranslatesTo("!path stuff", "<span class=\"meta\">classpath: stuff</span>");
        ParserTest.assertTranslatesTo("!path stuff\n",
                "<span class=\"meta\">classpath: stuff</span>" + ParserTest.newLineRendered);
    }

    @Test
    public void translatesVariableInPath() throws Exception {
        WikiPage page = new TestRoot().makePage("TestPage", "!define x {stuff}\n!path ${x}y\n");
        ParserTest.assertTranslatesTo(page, 
                "<span class=\"meta\">variable defined: x=stuff</span>" + HtmlElement.endl +
                ParserTest.newLineRendered + "<span class=\"meta\">classpath: stuffy</span>" + ParserTest.newLineRendered);
    }

    @Test
    public void findsDefinitions() throws Exception {
        WikiPage page = new TestRoot().makePage("TestPage", "!path stuff\n!note and\n!path nonsense");
        List<String> paths = new Paths(new HtmlTranslator(new WikiSourcePage(page), new ParsingPage(new WikiSourcePage(page)))).getPaths(ParserTest.parse(page));
        assertEquals(2, paths.size());
        assertEquals("stuff", paths.get(0));
        assertEquals("nonsense", paths.get(1));
    }
}