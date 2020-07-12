package chairosoft.cryptopals.set03;

import chairosoft.cryptopals.TestBase;
import org.junit.Test;

import static chairosoft.cryptopals.Common.*;
import static org.hamcrest.Matchers.equalTo;

/**
 * https://cryptopals.com/sets/3/challenges/17
 */
public class Challenge17Test extends TestBase {
    
    ////// Constants //////
    private static final byte[][] CLEAR_TEXT_OPTIONS = {
        fromBase64Text("dGVzdCB3aXRoIHBhZD0x"),
        fromBase64Text("MDAwMDAwTm93IHRoYXQgdGhlIHBhcnR5IGlzIGp1bXBpbmc="),
        fromBase64Text("MDAwMDAxV2l0aCB0aGUgYmFzcyBraWNrZWQgaW4gYW5kIHRoZSBWZWdhJ3MgYXJlIHB1bXBpbic="),
        fromBase64Text("MDAwMDAyUXVpY2sgdG8gdGhlIHBvaW50LCB0byB0aGUgcG9pbnQsIG5vIGZha2luZw=="),
        fromBase64Text("MDAwMDAzQ29va2luZyBNQydzIGxpa2UgYSBwb3VuZCBvZiBiYWNvbg=="),
        fromBase64Text("MDAwMDA0QnVybmluZyAnZW0sIGlmIHlvdSBhaW4ndCBxdWljayBhbmQgbmltYmxl"),
        fromBase64Text("MDAwMDA1SSBnbyBjcmF6eSB3aGVuIEkgaGVhciBhIGN5bWJhbA=="),
        fromBase64Text("MDAwMDA2QW5kIGEgaGlnaCBoYXQgd2l0aCBhIHNvdXBlZCB1cCB0ZW1wbw=="),
        fromBase64Text("MDAwMDA3SSdtIG9uIGEgcm9sbCwgaXQncyB0aW1lIHRvIGdvIHNvbG8="),
        fromBase64Text("MDAwMDA4b2xsaW4nIGluIG15IGZpdmUgcG9pbnQgb2g="),
        fromBase64Text("MDAwMDA5aXRoIG15IHJhZy10b3AgZG93biBzbyBteSBoYWlyIGNhbiBibG93")
    };
    
    ////// Instance Methods - Tests //////
    @Test
    public void doTest() throws Exception {
        int blockSize = 16;
        byte[] key = randomBytes(blockSize);
        byte[] iv = randomBytes(blockSize);
        for (byte[] data : CLEAR_TEXT_OPTIONS) {
            String expectedOutput = toDisplayableText(data) + "\n";
            byte[] input = Challenge17.encrypt(data, key, iv);
            assertResultOutput(equalTo(expectedOutput), 1, Challenge17::main, key, iv, input);
        }
    }
    
}
