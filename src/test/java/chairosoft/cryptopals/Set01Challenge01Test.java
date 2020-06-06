package chairosoft.cryptopals;

import org.junit.Test;

import static chairosoft.cryptopals.Common.COMMON_CHARSET;
import static chairosoft.cryptopals.TestUtils.getStdOut;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * https://cryptopals.com/sets/1/challenges/1
 */
public class Set01Challenge01Test {
    
    ////// Instance Methods - Tests //////
    @Test
    public void testSet01Challenge01() throws Exception {
        String input = "49276d206b696c6c696e6720796f757220627261696e206c696b65206120706f69736f6e6f7573206d757368726f6f6d";
        String expectedOutput = "SSdtIGtpbGxpbmcgeW91ciBicmFpbiBsaWtlIGEgcG9pc29ub3VzIG11c2hyb29t";
        byte[] actualOutputBytes = getStdOut(Set01Challenge01::main, input);
        String actualOutput = new String(actualOutputBytes, COMMON_CHARSET);
        assertThat(actualOutput, equalTo(expectedOutput));
    }
}
