package chairosoft.cryptopals.set01;

import chairosoft.cryptopals.TestBase;
import org.junit.Test;

import static chairosoft.cryptopals.Common.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * https://cryptopals.com/sets/1/challenges/1
 */
public class Challenge01Test extends TestBase {
    
    ////// Instance Methods - Tests //////
    @Test
    public void doTest() throws Exception {
        String input = "49276d206b696c6c696e6720796f757220627261696e206c696b65206120706f69736f6e6f7573206d757368726f6f6d";
        String expectedOutput = "SSdtIGtpbGxpbmcgeW91ciBicmFpbiBsaWtlIGEgcG9pc29ub3VzIG11c2hyb29t";
        byte[] actualOutputBytes = getStdOut(Challenge01::main, input);
        String actualOutput = toUtf8(actualOutputBytes);
        assertThat(actualOutput, equalTo(expectedOutput));
        String inputText = parseFromHex(input);
        System.out.printf("S1C1 input: %s\n", inputText);
    }
}
