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
        String expectedFirstLine = "  1  i have met them at close of day";
        long expectedResultLineCount = 40;
        assertResultOutput(
            nestedMatcher(
                String.format("A string starting with, case-insensitive, \"%s\"", expectedFirstLine),
                x -> x != null && x.toLowerCase().startsWith(expectedFirstLine.toLowerCase())
            ),
            expectedResultLineCount,
            Challenge19::main,
            key,
            nonce,
            littleEndianBlockCount,
            inputsFile
        );
    }
    
}
