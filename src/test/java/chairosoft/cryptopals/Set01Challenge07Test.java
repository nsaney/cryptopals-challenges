package chairosoft.cryptopals;

import org.junit.Test;

/**
 * https://cryptopals.com/sets/1/challenges/7
 */
public class Set01Challenge07Test extends TestBase {
    
    ////// Instance Methods - Tests //////
    @Test
    public void doTest() throws Exception {
        String inputFile = "src/test/resources/set01/7.txt";
        String key = "YELLOW SUBMARINE";
        String expectedResultPrefix = "I'm back and I'm ringin' the bell";
        long expectedResultLineCount = 80;
        assertResultOutput(expectedResultPrefix, expectedResultLineCount, Set01Challenge07::main, inputFile, key);
    }
    
}
