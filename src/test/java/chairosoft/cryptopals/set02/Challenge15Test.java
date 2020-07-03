package chairosoft.cryptopals.set02;

import chairosoft.cryptopals.TestBase;
import org.junit.Test;

import static chairosoft.cryptopals.Common.*;
import static org.hamcrest.Matchers.equalTo;

/**
 * https://cryptopals.com/sets/2/challenges/15
 */
public class Challenge15Test extends TestBase {
    
    ////// Instance Methods - Tests //////
    @Test
    public void doTest() throws Exception {
        int blockSize = 16;
        String unpaddedText = "ICE ICE BABY";
        byte[] unpaddedBytes = fromUtf8(unpaddedText);
        // 01
        byte[] inputData01 = concatenate(unpaddedBytes, new byte[] { 4, 4, 4, 4 });
        assertResultOutput(equalTo(unpaddedText), 0, Challenge15::main, blockSize, inputData01);
        // 02
        byte[] inputData02 = concatenate(unpaddedBytes, new byte[] { 5, 5, 5, 5 });
        assertResultError(Throwable.class, Challenge15::main, blockSize, inputData02);
        // 03
        byte[] inputData03 = concatenate(unpaddedBytes, new byte[] { 1, 2, 3, 4 });
        assertResultError(Throwable.class, Challenge15::main, blockSize, inputData03);
    }
    
}
