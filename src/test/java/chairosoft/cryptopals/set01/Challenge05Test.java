package chairosoft.cryptopals.set01;

import chairosoft.cryptopals.TestBase;
import org.junit.Test;

import static chairosoft.cryptopals.Common.toUtf8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * https://cryptopals.com/sets/1/challenges/5
 */
public class Challenge05Test extends TestBase {
    
    ////// Instance Methods - Tests //////
    @Test
    public void doTest() throws Exception {
        String dataText = "Burning 'em, if you ain't quick and nimble"
                        + "\nI go crazy when I hear a cymbal";
        String keyText = "ICE";
        String expectedOutput = "0b3637272a2b2e63622c2e69692a23693a2a3c6324202d623d63343c2a26226324272765272"
                              + "a282b2f20430a652e2c652a3124333a653e2b2027630c692b20283165286326302e27282f";
        byte[] actualOutputBytes = getStdOut(Challenge05::main, dataText, keyText);
        String actualOutput = toUtf8(actualOutputBytes);
        assertThat(actualOutput, equalTo(expectedOutput));
    }
}
