package chairosoft.cryptopals.set03;

import chairosoft.cryptopals.TestBase;
import org.junit.Test;

import static chairosoft.cryptopals.Common.*;
import static org.hamcrest.Matchers.equalTo;

/**
 * https://cryptopals.com/sets/3/challenges/18
 */
public class Challenge18Test extends TestBase {
    
    ////// Instance Methods - Tests //////
    @Test
    public void doTest() throws Exception {
        byte[] key = fromUtf8("YELLOW SUBMARINE");
        byte[] nonce = new byte[8];
        boolean littleEndianBlockCount = true;
        byte[] input = fromBase64Text("L77na/nrFsKvynd6HzOoG7GHTLXsTVu9qvY/2syLXzhPweyyMTJULu/6/kXX0KSvoOLSFQ==");
        String expectedOutput = "Yo, VIP Let's kick it Ice, Ice, baby Ice, Ice, baby \n";
        assertResultOutput(
            equalTo(expectedOutput),
            1,
            Challenge18::main,
            key,
            nonce,
            littleEndianBlockCount,
            input
        );
    }
    
}
