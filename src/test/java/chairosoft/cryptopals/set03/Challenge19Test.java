package chairosoft.cryptopals.set03;

import chairosoft.cryptopals.TestBase;
import org.junit.Test;

import java.io.File;

import static chairosoft.cryptopals.Common.*;

/**
 * https://cryptopals.com/sets/3/challenges/19
 */
public class Challenge19Test extends TestBase {
    
    ////// Instance Methods - Tests //////
    @Test
    public void doTest() throws Exception {
        int blockSize = 16;
        byte[] key = randomBytes(blockSize);
        byte[] nonce = new byte[8];
        boolean littleEndianBlockCount = true;
        File inputsFile = new File("src/test/resources/challenge-data/set03/19--inputs.txt");
        String expectedResultText = "__unknown__";
        long expectedResultLineCount = 40;
        assertResultOutputStartsWith(
            expectedResultText,
            expectedResultLineCount,
            Challenge19::main,
            key,
            nonce,
            littleEndianBlockCount,
            inputsFile
        );
    }
    
}
