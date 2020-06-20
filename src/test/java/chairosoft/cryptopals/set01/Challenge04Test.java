package chairosoft.cryptopals.set01;

import chairosoft.cryptopals.TestBase;
import org.junit.Test;

/**
 * https://cryptopals.com/sets/1/challenges/4
 */
public class Challenge04Test extends TestBase {
    
    ////// Instance Methods - Tests //////
    @Test
    public void doTest() throws Exception {
        String inputFile = "src/test/resources/challenge-data/set01/04.txt";
        String expectedResultPrefix = "[35]: ";
        long expectedResultLineCount = 1;
        assertResultOutputStartsWith(expectedResultPrefix, expectedResultLineCount, Challenge04::main, inputFile);
    }
    
}
