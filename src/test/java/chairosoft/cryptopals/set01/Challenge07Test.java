package chairosoft.cryptopals.set01;

import chairosoft.cryptopals.TestBase;
import org.junit.Test;

/**
 * https://cryptopals.com/sets/1/challenges/7
 */
public class Challenge07Test extends TestBase {
    
    ////// Instance Methods - Tests //////
    @Test
    public void doTest() throws Exception {
        String inputFile = "src/test/resources/challenge-data/set01/07.txt";
        String key = "YELLOW SUBMARINE";
        String expectedResultPrefix = "I'm back and I'm ringin' the bell";
        long expectedResultLineCount = 80;
        assertResultOutput(expectedResultPrefix, expectedResultLineCount, Challenge07::main, inputFile, key);
    }
    
}
