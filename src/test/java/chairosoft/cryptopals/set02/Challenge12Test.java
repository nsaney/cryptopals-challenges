package chairosoft.cryptopals.set02;

import chairosoft.cryptopals.TestBase;
import org.junit.Test;

import static chairosoft.cryptopals.Common.*;

/**
 * https://cryptopals.com/sets/2/challenges/12
 */
public class Challenge12Test extends TestBase {
    
    ////// Instance Methods - Tests //////
    @Test
    public void doTest() throws Exception {
        String unknownBase64Text = "Um9sbGluJyBpbiBteSA1LjAKV2l0aCBteSByYWctdG9wIGRvd24gc28gbXkg"
                                 + "aGFpciBjYW4gYmxvdwpUaGUgZ2lybGllcyBvbiBzdGFuZGJ5IHdhdmluZyBq"
                                 + "dXN0IHRvIHNheSBoaQpEaWQgeW91IHN0b3A/IE5vLCBJIGp1c3QgZHJvdmUg"
                                 + "YnkK";
        byte[] key = Challenge11.generateRandomAesKey();
        String keyBase64Text = toBase64Text(key);
        byte[] unknownBase64 = fromBase64Text(unknownBase64Text);
        String expectedResultPrefix = "[blockSize=16][ECB=true]: " + toDisplayableText(unknownBase64);
        long expectedResultLineCount = count((byte)'\n', unknownBase64) + 1;
        assertResultOutputStartsWith(expectedResultPrefix, expectedResultLineCount, Challenge12::main, unknownBase64Text, keyBase64Text);
    }
    
}
