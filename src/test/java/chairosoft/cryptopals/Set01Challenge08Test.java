package chairosoft.cryptopals;

import org.junit.Test;

/**
 * https://cryptopals.com/sets/1/challenges/8
 */
public class Set01Challenge08Test extends TestBase {
    
    ////// Instance Methods - Tests //////
    @Test
    public void doTest() throws Exception {
        String inputFile = "src/test/resources/set01/8.txt";
        String expectedResultPrefix = "Line #0133: ";
        long expectedResultLineCount = 1;
        assertResultOutput(expectedResultPrefix, expectedResultLineCount, Set01Challenge08::main, inputFile);
    }
    
}
