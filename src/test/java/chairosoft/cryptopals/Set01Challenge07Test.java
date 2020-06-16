package chairosoft.cryptopals;

import org.junit.Test;

import static chairosoft.cryptopals.Common.COMMON_CHARSET;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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
        byte[] actualResultBytes = getStdOut(Set01Challenge07::main, inputFile, key);
        String actualResult = new String(actualResultBytes, COMMON_CHARSET);
        assertThat(actualResult, startsWith(expectedResultPrefix));
        long actualResultLineCount = actualResult.codePoints().filter(c -> c == '\n').count();
        assertThat("Line Count", actualResultLineCount, equalTo(expectedResultLineCount));
    }
    
}
