package chairosoft.cryptopals.set03;

import chairosoft.cryptopals.TestBase;
import chairosoft.cryptopals.set03.Challenge21.MT19937Random;
import org.junit.Test;

import java.util.Random;

/**
 * https://cryptopals.com/sets/3/challenges/22
 */
public class Challenge22Test extends TestBase {
    
    ////// Instance Methods - Tests //////
    @Test
    public void doTest() throws Exception {
        Random rand = new Random();
        long currentTimeSeconds = System.currentTimeMillis() / 1000;
        int minAdjustment = 40;
        int maxAdjustment = 1000;
        long adjustment = rand.nextInt(maxAdjustment - minAdjustment) + minAdjustment;
        long seed = currentTimeSeconds - adjustment;
        MT19937Random mt19937Random = new MT19937Random(seed);
        long firstValue = mt19937Random.extractNumber();
        String expectedResultText = String.valueOf(seed);
        System.err.printf("%s was the seed; calling with first value [%s].\n", seed, firstValue);
        assertResultOutputEquals(
            expectedResultText,
            Challenge22::main,
            firstValue,
            minAdjustment,
            maxAdjustment
        );
    }
    
}
