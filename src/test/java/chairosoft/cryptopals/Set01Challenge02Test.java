package chairosoft.cryptopals;

import org.junit.Test;

import static chairosoft.cryptopals.Common.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * https://cryptopals.com/sets/1/challenges/2
 */
public class Set01Challenge02Test extends TestBase {
    
    ////// Instance Methods - Tests //////
    @Test
    public void testSet01Challenge01() throws Exception {
        String x = "1c0111001f010100061a024b53535009181c";
        String y = "686974207468652062756c6c277320657965";
        String expectedOutput = "746865206b696420646f6e277420706c6179";
        byte[] actualOutputBytes = getStdOut(Set01Challenge02::main, x, y);
        String actualOutput = new String(actualOutputBytes, COMMON_CHARSET);
        assertThat(actualOutput, equalTo(expectedOutput));
        String xText = parseFromHex(x);
        String yText = parseFromHex(y);
        String outText = parseFromHex(actualOutput);
        System.out.printf("S1C2   x: %s\n", xText);
        System.out.printf("S1C2   y: %s\n", yText);
        System.out.printf("S1C2 out: %s\n", outText);
    }
}
