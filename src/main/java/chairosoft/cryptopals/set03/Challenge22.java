package chairosoft.cryptopals.set03;

import chairosoft.cryptopals.set03.Challenge21.MT19937Random;

/**
 * https://cryptopals.com/sets/3/challenges/22
 */
public class Challenge22 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        long currentTimeSeconds = System.currentTimeMillis() / 1000;
        long firstValue = Long.parseLong(args[0]);
        int minAdjustment = Integer.parseInt(args[1]);
        int maxAdjustment = Integer.parseInt(args[2]);
        long seed = crackMt19937TimeBasedSeed(currentTimeSeconds, firstValue, minAdjustment, maxAdjustment);
        System.out.println(seed);
    }
    
    
    ////// Static Methods //////
    public static long crackMt19937TimeBasedSeed(
        long callTimeSeconds,
        long firstValue,
        int minAdjustment,
        int maxAdjustment
    )
        throws Exception
    {
        int range = maxAdjustment - minAdjustment;
        int extendedMaxAdjustment = maxAdjustment + range;
        for (int i = minAdjustment; i < extendedMaxAdjustment; ++i) {
            long potentialSeed = callTimeSeconds - i;
            long potentialFirstValue = firstValueForMt19937Seed(potentialSeed);
            if (firstValue == potentialFirstValue) {
                return potentialSeed;
            }
        }
        throw new RuntimeException(String.format(
            "Unable to crack seed from value [%s] in range from [%s - %s] to [%s - %s].",
            firstValue,
            callTimeSeconds,
            extendedMaxAdjustment,
            callTimeSeconds,
            minAdjustment
        ));
    }
    
    public static long firstValueForMt19937Seed(long seed) {
        MT19937Random mt19937Random = new MT19937Random(seed);
        return mt19937Random.extractNumber();
    }
    
}
