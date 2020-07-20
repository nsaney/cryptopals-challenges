package chairosoft.cryptopals.set03;

import chairosoft.cryptopals.TestBase;
import org.junit.Test;

import java.io.File;

import static chairosoft.cryptopals.Common.randomBytes;

/**
 * https://cryptopals.com/sets/3/challenges/20
 */
public class Challenge20Test extends TestBase {
    
    ////// Instance Methods - Tests //////
    @Test
    public void doTest() throws Exception {
        int blockSize = 16;
        byte[] key = randomBytes(blockSize);
        byte[] nonce = new byte[8];
        boolean littleEndianBlockCount = true;
        File inputsFile = new File("src/test/resources/challenge-data/set03/20.txt");
        String expectedResultText = "  1  I'm rated \"R\"...this is a warning";
        long expectedResultLineCount = 60;
        assertResultOutputStartsWith(
            expectedResultText,
            expectedResultLineCount,
            Challenge20::main,
            key,
            nonce,
            littleEndianBlockCount,
            inputsFile
        );
    }
    
}
