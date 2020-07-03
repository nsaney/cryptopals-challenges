package chairosoft.cryptopals.set02;

import chairosoft.cryptopals.TestBase;
import org.junit.Test;

import static chairosoft.cryptopals.Common.*;
import static chairosoft.cryptopals.set02.Challenge16.encryptUserData;
import static chairosoft.cryptopals.set02.Challenge16.isAdminProfile;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * https://cryptopals.com/sets/2/challenges/16
 */
public class Challenge16Test extends TestBase {
    
    ////// Instance Methods - Tests //////
    @Test
    public void doTest() throws Exception {
        int blockSize = 16;
        byte[] key = randomBytes(blockSize);
        byte[] iv = randomBytes(blockSize);
        // 01 empty
        byte[] userData01 = new byte[0];
        byte[] encrypted01 = encryptUserData(userData01, key, iv);
        boolean isAdmin01 = isAdminProfile(encrypted01, key, iv);
        System.err.println("isAdmin01: " + isAdmin01);
        assertThat("isAdmin #01", isAdmin01, equalTo(false));
        // 02 injection attempt
        byte[] userData02 = fromUtf8(";admin=true");
        byte[] encrypted02 = encryptUserData(userData02, key, iv);
        boolean isAdmin02 = isAdminProfile(encrypted02, key, iv);
        System.err.println("isAdmin02: " + isAdmin02);
        assertThat("isAdmin #02", isAdmin02, equalTo(false));
        // 03 encrypt directly
        byte[] userData03 = fromUtf8("admin=true");
        byte[] encrypted03 = Challenge10.encryptAesCbc(userData03, key, iv);
        boolean isAdmin03 = isAdminProfile(encrypted03, key, iv);
        System.err.println("isAdmin03: " + isAdmin03);
        assertThat("isAdmin #03", isAdmin03, equalTo(true));
        // test breaking of CBC
        String expectedResultContains = "isAdmin = true";
        long expectedResultLineCount = 2;
        assertResultOutputContains(expectedResultContains, expectedResultLineCount, Challenge16::main, key, iv);
    }
    
}
