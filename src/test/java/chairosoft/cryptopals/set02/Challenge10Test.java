package chairosoft.cryptopals.set02;

import chairosoft.cryptopals.TestBase;
import org.junit.Test;

import static chairosoft.cryptopals.Common.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * https://cryptopals.com/sets/2/challenges/10
 */
public class Challenge10Test extends TestBase {
    
    ////// Instance Methods - Tests //////
    @Test
    public void doTest() throws Exception {
        String inputFile = "src/test/resources/challenge-data/set02/10.txt";
        String key = "YELLOW SUBMARINE";
        String iv = "\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0";
        // test round trip
        byte[] data00 = readFileBase64(inputFile);
        byte[] keyData = fromUtf8(key);
        byte[] ivData = fromUtf8(iv);
        byte[] data01 = Challenge10.decryptAesCbc(data00, keyData, ivData);
        byte[] data02 = Challenge10.encryptAesCbc(data01, keyData, ivData);
        assertThat("Round Trip #1", toDisplayableText(data02), equalTo(toDisplayableText(data00)));
        byte[] data03 = Challenge10.decryptAesCbc(data02, keyData, ivData);
        byte[] data04 = Challenge10.encryptAesCbc(data03, keyData, ivData);
        assertThat("Round Trip #2", toDisplayableText(data04), equalTo(toDisplayableText(data00)));
        // test challenge data
        String expectedResultPrefix = "I'm back and I'm ringin' the bell";
        long expectedResultLineCount = 80;
        assertResultOutputStartsWith(expectedResultPrefix, expectedResultLineCount, Challenge10::main, inputFile, key, iv);
    }
    
}
