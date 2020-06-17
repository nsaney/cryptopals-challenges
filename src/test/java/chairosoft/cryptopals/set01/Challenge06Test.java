package chairosoft.cryptopals.set01;

import chairosoft.cryptopals.TestBase;
import org.junit.Test;

import static chairosoft.cryptopals.Common.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * https://cryptopals.com/sets/1/challenges/6
 */
public class Challenge06Test extends TestBase {
    
    ////// Instance Methods - Tests //////
    @Test
    public void doTest() throws Exception {
        // test hamming
        byte[] hammingX = fromUtf8("this is a test");
        byte[] hammingY = fromUtf8("wokka wokka!!!");
        int expectedHammingDistance = 37;
        int actualHammingDistance = hammingDistance(hammingX, hammingY);
        System.out.println("Hamming Distance Check: " + actualHammingDistance);
        assertThat("Hamming Distance", actualHammingDistance, equalTo(expectedHammingDistance));
        // test break repeating xor
        String inputFile = "src/test/resources/challenge-data/set01/06.txt";
        int keySizeMin = 2;
        int keySizeMax = 40;
        String expectedResultPrefix = "[Terminator X: Bring the noise]";
        long expectedResultLineCount = 79;
        assertResultOutput(
            expectedResultPrefix,
            expectedResultLineCount,
            Challenge06::main,
            inputFile,
            keySizeMin,
            keySizeMax
        );
    }
    
}
