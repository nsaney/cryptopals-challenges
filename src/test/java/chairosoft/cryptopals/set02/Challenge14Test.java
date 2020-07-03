package chairosoft.cryptopals.set02;

import chairosoft.cryptopals.TestBase;
import org.junit.Test;

import static chairosoft.cryptopals.Common.*;

/**
 * https://cryptopals.com/sets/2/challenges/14
 */
public class Challenge14Test extends TestBase {
    
    ////// Instance Methods - Tests //////
    @Test
    public void doTest() throws Exception {
        String unknownBase64Text = "Um9sbGluJyBpbiBteSA1LjAKV2l0aCBteSByYWctdG9wIGRvd24gc28gbXkg"
                                 + "aGFpciBjYW4gYmxvdwpUaGUgZ2lybGllcyBvbiBzdGFuZGJ5IHdhdmluZyBq"
                                 + "dXN0IHRvIHNheSBoaQpEaWQgeW91IHN0b3A/IE5vLCBJIGp1c3QgZHJvdmUg"
                                 + "YnkK";
        int prefixSize = THREAD_LOCAL_RANDOM.get().nextInt(31) + 17;
        byte[] prefix = randomBytes(prefixSize);
        byte[] key = Challenge11.generateRandomAesKey();
        byte[] unknown = fromBase64Text(unknownBase64Text);
        System.err.println("prefix.length:  " + prefix.length);
        System.err.println("unknown.length: " + unknown.length);
        String expectedResultPrefix = "[blockSize=16][ECB=true]: " + toDisplayableText(unknown);
        long expectedResultLineCount = count((byte)'\n', unknown) + 1;
        assertResultOutputStartsWith(
            expectedResultPrefix,
            expectedResultLineCount,
            Challenge14::main,
            prefix,
            unknown,
            key
        );
    }
    
}
