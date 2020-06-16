package chairosoft.cryptopals;

import org.junit.Test;

import static chairosoft.cryptopals.Common.COMMON_CHARSET;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * https://cryptopals.com/sets/2/challenges/10
 */
public class Set02Challenge10Test extends TestBase {
    
    ////// Instance Methods - Tests //////
    @Test
    public void doTest() throws Exception {
        String inputFile = "src/test/resources/challenge-data/10.txt";
        String key = "YELLOW SUBMARINE";
        String iv = "\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0";
        String expectedResultPrefix = "I'm back and I'm ringin' the bell";
        long expectedResultLineCount = 80;
        assertResultOutput(expectedResultPrefix, expectedResultLineCount, Set02Challenge10::main, inputFile, key, iv);
    }
    
}
