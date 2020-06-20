package chairosoft.cryptopals.set01;

import chairosoft.cryptopals.TestBase;
import org.junit.Test;

/**
 * https://cryptopals.com/sets/1/challenges/8
 */
public class Challenge08Test extends TestBase {
    
    ////// Instance Methods - Tests //////
    @Test
    public void doTest() throws Exception {
        String inputFile = "src/test/resources/challenge-data/set01/08.txt";
        String expectedResultPrefix = "Line #0133: ";
        long expectedResultLineCount = 1;
        assertResultOutputStartsWith(expectedResultPrefix, expectedResultLineCount, Challenge08::main, inputFile);
    }
    
}
