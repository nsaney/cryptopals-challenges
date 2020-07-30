package chairosoft.cryptopals.set03;

import chairosoft.cryptopals.set03.Challenge21.MT19937Random;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

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
    
    public static long untemperMt19973Value(long z) {
        long u = MT19937Random.TEMPERING_SHIFT_U;
        long d = MT19937Random.TEMPERING_MASK_D;
        long s = MT19937Random.TEMPERING_SHIFT_S;
        long b = MT19937Random.TEMPERING_MASK_B;
        long t = MT19937Random.TEMPERING_SHIFT_T;
        long c = MT19937Random.TEMPERING_MASK_C;
        long ell = MT19937Random.TEMPERING_SHIFT_L;
        ////y1 = y0 ^ ((y0 >> u) & d);
        ////y2 = y1 ^ ((y1 << s) & b);
        ////y3 = y2 ^ ((y2 << t) & c);
        ////y  = y3 ^ (y3 >> ell);
        long z3 = z ^ (z << ell);
        long z2 = 0L; // TODO
        // a  b  a&b
        // 0  0  0
        // 0  1  0
        // 1  0  0
        // 1  1  1
        throw new UnsupportedOperationException();
    }
    
}
