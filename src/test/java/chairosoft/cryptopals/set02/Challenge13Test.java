package chairosoft.cryptopals.set02;

import chairosoft.cryptopals.TestBase;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static chairosoft.cryptopals.Common.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * https://cryptopals.com/sets/2/challenges/13
 */
public class Challenge13Test extends TestBase {
    
    ////// Instance Methods - Tests //////
    @Test
    public void doTest() throws Exception {
        // test kv parse routine
        String kvText = "foo=bar&baz=qux&zap=zazzle";
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("foo", "bar");
        expectedMap.put("baz", "qux");
        expectedMap.put("zap", "zazzle");
        Map<String, String> actualMap = Challenge13.parseKv(kvText);
        System.out.println("Actual KV Map: " + actualMap);
        assertThat("KV Map", actualMap, equalTo(expectedMap));
    }
    
}
