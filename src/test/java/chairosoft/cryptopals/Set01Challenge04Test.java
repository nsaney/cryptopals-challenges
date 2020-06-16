package chairosoft.cryptopals;

import org.junit.Test;

/**
 * https://cryptopals.com/sets/1/challenges/4
 */
public class Set01Challenge04Test extends TestBase {
    
    ////// Instance Methods - Tests //////
    @Test
    public void doTest() throws Exception {
        String inputFile = "src/test/resources/set01/4.txt";
        String expectedResultPrefix = "[35]: ";
        long expectedResultLineCount = 1;
        assertResultOutput(expectedResultPrefix, expectedResultLineCount, Set01Challenge04::main, inputFile);
    }
    
}
