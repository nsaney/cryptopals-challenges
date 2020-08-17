package chairosoft.cryptopals.set03;

import chairosoft.cryptopals.Common;
import chairosoft.cryptopals.set03.Challenge21.MT19937Random;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.function.LongBinaryOperator;

/**
 * https://cryptopals.com/sets/3/challenges/23
 */
public class Challenge23 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        int count = Integer.parseInt(args[0]);
        File inputFile = new File(args[1]);
        List<String> inputLines = Files.readAllLines(inputFile.toPath());
        long[] inputValues = inputLines.stream().mapToLong(Long::parseLong).toArray();
        MT19937Random mt19937Random = getMt19973RandomForValues(inputValues);
        for (int i = 0; i < count; ++i) {
            long num = mt19937Random.extractNumber();
            System.out.println(num);
        }
    }
    
    
    ////// Static Methods //////
    public static MT19937Random getMt19973RandomForValues(long[] inputValues) {
        long[] untemperedValues = new long[inputValues.length];
        for (int i = 0; i < untemperedValues.length; ++i) {
            untemperedValues[i] = untemperMt19973Value(inputValues[i]);
        }
        MT19937Random mt19937Random = new MT19937Random();
        mt19937Random.setState(untemperedValues.length, untemperedValues);
        return mt19937Random;
    }
    
    private final static long wordMask = (1L << MT19937Random.WORD_SIZE) - 1;
    private final static long u = MT19937Random.TEMPERING_SHIFT_U;
    private final static long d = MT19937Random.TEMPERING_MASK_D & wordMask;
    private final static long s = MT19937Random.TEMPERING_SHIFT_S;
    private final static long b = MT19937Random.TEMPERING_MASK_B & wordMask;
    private final static long t = MT19937Random.TEMPERING_SHIFT_T;
    private final static long c = MT19937Random.TEMPERING_MASK_C & wordMask;
    private final static long ell = MT19937Random.TEMPERING_SHIFT_L;
    private final static LongBinaryOperator f4 = (x0, x) -> x0 ^ ((x >> ell));
    private final static LongBinaryOperator f3 = (x0, x) -> x0 ^ ((x << t) & c);
    private final static LongBinaryOperator f2 = (x0, x) -> x0 ^ ((x << s) & b);
    private final static LongBinaryOperator f1 = (x0, x) -> x0 ^ ((x >> u) & d);
    public static long applyUntilStable(LongBinaryOperator op, long x0, int maxIters) {
        long xf = x0;
        for (int i = 0; i < maxIters; ++i) {
            long prev = xf;
            xf = op.applyAsLong(x0, prev);
            if (prev == xf) {
                return xf;
            }
        }
        throw new IllegalStateException("Did not converge.");
    }
    
    public static long untemperMt19973Value(long z) {
        long z4 = z & wordMask;
        long z3 = applyUntilStable(f4, z4, 10);
        long z2 = applyUntilStable(f3, z3, 10);
        long z1 = applyUntilStable(f2, z2, 10);
        long z0 = applyUntilStable(f1, z1, 10);
        long y = z0 & wordMask;
        if (Common.IS_DEBUG) {
            long y1 = f1.applyAsLong(y, y);
            long y2 = f2.applyAsLong(y1, y1);
            long y3 = f3.applyAsLong(y2, y2);
            long y4 = f4.applyAsLong(y3, y3);
            if (y4 != z4) {
                String message = String.format("Could not untemper [%08x] ; got [%08x] instead.", z4, y4);
                throw new IllegalStateException(message);
            }
        }
        return y;
    }
    
}
