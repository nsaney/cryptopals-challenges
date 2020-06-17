package chairosoft.cryptopals.set02;

import chairosoft.cryptopals.TestBase;
import chairosoft.cryptopals.set01.Challenge08;
import org.junit.Test;

import static chairosoft.cryptopals.Common.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * https://cryptopals.com/sets/2/challenges/11
 */
public class Challenge11Test extends TestBase {
    
    ////// Instance Methods - Tests //////
    @Test
    public void doTest() throws Exception {
        THREAD_LOCAL_RANDOM.get().setSeed(211);
        String plainTextBlock = "YellowSubmarine-";
        String plainText = String.format("%s%<s%<s%<s%<s%<s%<s%<s%<s%<s%<s%<s%<s%<s%<s%<s", plainTextBlock);
        byte[] input = fromUtf8(plainText);
        int trialCount = 30;
        for (int i = 0; i < trialCount; ++i) {
            Challenge11.RandomEncryptionResult result = Challenge11.randomlyEncrypt(input);
            boolean hasRepeatBlocks = Challenge08.hasRepeatBlocks(result.encryptedData, 16);
            String detectedAlgorithmMode = hasRepeatBlocks ? "ECB" : "CBC";
            System.out.printf(
                "%s%02d of %02d. Detected/Actual: '%s'/'%s'",
                (i % 3) == 0 ? "\n" : "  ",
                i + 1,
                trialCount,
                detectedAlgorithmMode,
                result.algorithmMode
            );
            assertThat("Detected Algorithm Mode", detectedAlgorithmMode, equalTo(result.algorithmMode));
        }
        System.out.println();
    }
    
}
