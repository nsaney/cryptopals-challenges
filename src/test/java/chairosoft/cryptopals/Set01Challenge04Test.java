package chairosoft.cryptopals;

import org.junit.Test;

import static chairosoft.cryptopals.Common.COMMON_CHARSET;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;

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
        byte[] actualResultBytes = getStdOut(Set01Challenge04::main, inputFile);
        String actualResult = new String(actualResultBytes, COMMON_CHARSET);
        assertThat(actualResult, startsWith(expectedResultPrefix));
        long actualResultLineCount = actualResult.codePoints().filter(c -> c == '\n').count();
        assertThat("Line Count", actualResultLineCount, equalTo(expectedResultLineCount));
    }
    
}
