package chairosoft.cryptopals.set01;

import chairosoft.cryptopals.TestBase;
import org.junit.Test;

/**
 * https://cryptopals.com/sets/1/challenges/3
 */
public class Challenge03Test extends TestBase {
    
    ////// Instance Methods - Tests //////
    @Test
    public void doTest() throws Exception {
        String input = "1b37373331363f78151b7f2b783431333d78397828372d363c78373e783a393b3736";
        String expectedResultPrefix = "[58]: ";
        long expectedResultLineCount = 1;
        assertResultOutput(expectedResultPrefix, expectedResultLineCount, Challenge03::main, input);
    }
    
}
