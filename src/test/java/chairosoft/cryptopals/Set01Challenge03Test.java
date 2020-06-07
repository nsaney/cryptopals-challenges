package chairosoft.cryptopals;

import org.junit.Test;

import static chairosoft.cryptopals.Common.COMMON_CHARSET;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * https://cryptopals.com/sets/1/challenges/3
 */
public class Set01Challenge03Test extends TestBase {
    
    ////// Instance Methods - Tests //////
    @Test
    public void doTest() throws Exception {
        String input = "1b37373331363f78151b7f2b783431333d78397828372d363c78373e783a393b3736";
        String expectedResult = "[58]: Cooking MC's like a pound of bacon\n";
        byte[] actualResultBytes = getStdOut(Set01Challenge03::main, input);
        String actualResult = new String(actualResultBytes, COMMON_CHARSET);
        assertThat(actualResult, equalTo(expectedResult));
    }
    
}
