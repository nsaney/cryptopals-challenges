package chairosoft.cryptopals.set02;

import chairosoft.cryptopals.Common.*;
import chairosoft.cryptopals.TestBase;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static chairosoft.cryptopals.Common.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * https://cryptopals.com/sets/2/challenges/13
 */
public class Challenge13Test extends TestBase {
    
    ////// Instance Methods - Tests //////
    @Test
    public void doTest() throws Exception {
        // test overlap check
        List<OverlapDetails> overlaps = getOverlaps(new byte[] { 0, 1, 2, 3, 4, 5 }, new byte[] { 0, 1, 0, 3, 0, 5 });
        System.out.println("Overlaps: " + overlaps);
        assertThat("Overlaps", overlaps, notNullValue());
        assertThat("Overlaps", overlaps, equalTo(Arrays.asList(
            new OverlapDetails(0, 2),
            new OverlapDetails(3, 1),
            new OverlapDetails(5, 1)
        )));
        // test kv parse routine
        String kvText = "foo=bar&baz=qux&zap=zazzle";
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("foo", "bar");
        expectedMap.put("baz", "qux");
        expectedMap.put("zap", "zazzle");
        Map<String, String> actualMap = Challenge13.parseKv(kvText);
        System.out.println("Actual KV Map: " + actualMap);
        assertThat("KV Map", actualMap, equalTo(expectedMap));
        // test kv format routine
        String expectedReformattedKvText = "baz=qux&foo=bar&zap=zazzle";
        String actualReformattedKvText = Challenge13.formatKv(expectedMap);
        System.out.println("Actual Reformatted KV Map: " + actualReformattedKvText);
        assertThat("Reformatted KV Map", actualReformattedKvText, equalTo(expectedReformattedKvText));
        // test plaintext profile routine
        assertProfile("#1", "atless", "atless");
        assertProfile("#2", "atta@example.com", "atta@example.com");
        assertProfile("#3", "atta@example.com=3", "atta@example.com3");
        assertProfile("#4", "atta@example.com&blah=4", "atta@example.comblah4");
        // test making a role=admin profile
        byte[] key = randomBytes(16);
        String keyText = toHex(key);
        Matcher<String> expectedResultMatcher = nestedMatcher(
            "a serialized map with entry {role=admin}",
            text -> {
                String trimmed = text.trim();
                Map<String, String> serialized = Challenge13.parseKv(trimmed);
                return "admin".equals(serialized.get("role"));
            }
        );
        long expectedResultLineCount = 1;
        assertResultOutput(expectedResultMatcher, expectedResultLineCount, Challenge13::main, keyText);
    }
    
    
    ////// Helper Methods //////
    public static void assertProfile(String profileSuffix, String inputEmail, String expectedEmail) throws Exception {
        String actualProfileText = Challenge13.profileFor(inputEmail);
        String expectedProfileText = String.format(
            "email=%s&role=user&uid=10",
            expectedEmail
        );
        System.out.println("Actual Profile " + profileSuffix + ": " + actualProfileText);
        assertThat("Profile " + profileSuffix, actualProfileText, equalTo(expectedProfileText));
    }
    
}
