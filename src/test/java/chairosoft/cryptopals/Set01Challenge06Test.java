package chairosoft.cryptopals;

import org.junit.Test;

import static chairosoft.cryptopals.Common.COMMON_CHARSET;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;

/**
 * https://cryptopals.com/sets/1/challenges/6
 */
public class Set01Challenge06Test extends TestBase {
    
    ////// Instance Methods - Tests //////
    @Test
    public void doTest() throws Exception {
        // test hamming
        String hammingX = "this is a test";
        String hammingY = "wokka wokka!!!";
        int expectedHammingDistance = 37;
        int actualHammingDistance = Set01Challenge06.hammingDistance(
            hammingX.getBytes(COMMON_CHARSET),
            hammingY.getBytes(COMMON_CHARSET)
        );
        System.out.println("Hamming Distance Check: " + actualHammingDistance);
        assertThat("Hamming Distance", actualHammingDistance, equalTo(expectedHammingDistance));
        // test break repeating xor
        String inputFile = "src/test/resources/set01/6.txt";
        String keySizeMin = "2";
        String keySizeMax = "40";
        String expectedResultPrefix = "[Terminator X: Bring the noise]";
        long expectedResultLineCount = 79;
        byte[] actualResultBytes = getStdOut(Set01Challenge06::main, inputFile, keySizeMin, keySizeMax);
        String actualResult = new String(actualResultBytes, COMMON_CHARSET);
        assertThat(actualResult, startsWith(expectedResultPrefix));
        long actualResultLineCount = actualResult.codePoints().filter(c -> c == '\n').count();
        assertThat("Line Count", actualResultLineCount, equalTo(expectedResultLineCount));
    }
    
}
