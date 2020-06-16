package chairosoft.cryptopals;

import org.junit.Test;

import static chairosoft.cryptopals.Common.COMMON_CHARSET;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * https://cryptopals.com/sets/2/challenges/9
 */
public class Set02Challenge09Test extends TestBase {
    
    ////// Instance Methods - Tests //////
    @Test
    public void doTest() throws Exception {
        String inputText = "YELLOW SUBMARINE";
        int blockSize = 20;
        String expectedOutputText = "YELLOW SUBMARINE\\x[04040404]\n";
        byte[] actualOutput = getStdOut(Set02Challenge09::main, inputText, blockSize);
        String actualOutputText = new String(actualOutput, COMMON_CHARSET);
        assertThat("Output Text", actualOutputText, equalTo(expectedOutputText));
    }
    
}
