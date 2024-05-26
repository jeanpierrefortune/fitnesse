package opensam;

import junit.framework.TestCase;

public class ReaderManagerTest extends TestCase {

  public void testEnumerateReaders() throws Exception {
    ReaderManager rm = new ReaderManager();
    rm.enumerateReaders(true);
  }

  public void testSetActiveReader() {}
}
