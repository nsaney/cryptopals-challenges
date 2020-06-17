package chairosoft.cryptopals.set02;

import chairosoft.cryptopals.TestBase;
import org.junit.Test;

import static chairosoft.cryptopals.Common.toUtf8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * https://cryptopals.com/sets/2/challenges/9
 */
public class Challenge09Test extends TestBase {
    
    ////// Instance Methods - Tests //////
    @Test
    public void doTest() throws Exception {
        String inputText = "YELLOW SUBMARINE";
        int blockSize = 20;
        String expectedOutput = "YELLOW SUBMARINE\\x[04040404]\n";
        byte[] actualOutputBytes = getStdOut(Challenge09::main, inputText, blockSize);
        String actualOutput = toUtf8(actualOutputBytes);
        assertThat("Output Text", actualOutput, equalTo(expectedOutput));
    }
    
}
