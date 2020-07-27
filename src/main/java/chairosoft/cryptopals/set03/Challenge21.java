package chairosoft.cryptopals.set03;

import java.util.Random;

/**
 * https://cryptopals.com/sets/3/challenges/21
 */
public class Challenge21 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        long seed = Long.parseLong(args[0]);
        int count = Integer.parseInt(args[1]);
        long[] values = getValuesFromMt19937(seed, count);
        for (int i = 0; i < values.length; ++i) {
            System.out.printf("%s %s\n", (i + 1), values[i]);
        }
    }
    
    
    ////// Static Methods //////
    public static long[] getValuesFromMt19937(long seed, int count) {
        MT19937Random rand = new MT19937Random(seed);
        long[] result = new long[count];
        for (int i = 0; i < count; ++i) {
            result[i] = rand.extractNumber();
        }
        return result;
    }
    
    
    ////// Static Inner Classes //////
    /**
     * See https://en.wikipedia.org/wiki/Mersenne_Twister
     */
    public static class MersenneTwisterRandom extends Random {
        //// Instance Fields ////
        private final int w, n, m, r, s, t, u, ell;
        private final long a, b, c, d, f;
        private final long[] MT;
        private int index;
        private final long wordMask;
        private final long loMask;
        private final long hiMask;
        //// Constructor ////
        public MersenneTwisterRandom(
            int _wordSize,
            int _recurrenceDegree,
            int _middleWord,
            int _lowMaskSize,
            long _twistMatrixCoefficientBits,
            int _temperingShiftU,
            long _temperingMaskD,
            int _temperingShiftS,
            long _temperingMaskB,
            int _temperingShiftT,
            long _temperingMaskC,
            int _temperingShiftL,
            long _seedInitMultiplier
        ) {
            if (_wordSize < 1 || Long.SIZE < _wordSize) {
                throw new IllegalArgumentException(String.format(
                    "Word size [%s] outside of range [1, %s].",
                    _wordSize,
                    Long.SIZE
                ));
            }
            if (_middleWord < 1 || _recurrenceDegree <= _middleWord) {
                throw new IllegalArgumentException(String.format(
                    "Middle word [%s] outside of range [1, %s).",
                    _middleWord,
                    _recurrenceDegree
                ));
            }
            if (_lowMaskSize < 0 || _wordSize <= _lowMaskSize) {
                throw new IllegalArgumentException(String.format(
                    "Lo mask size [%s] outside of range [0, %s).",
                    _lowMaskSize,
                    _wordSize
                ));
            }
            this.w = _wordSize;
            this.wordMask = (1L << this.w) - 1;
            this.n = _recurrenceDegree;
            this.m = _middleWord;
            this.r = _lowMaskSize;
            this.a = _twistMatrixCoefficientBits;
            this.u = _temperingShiftU;
            this.d = _temperingMaskD & this.wordMask;
            this.s = _temperingShiftS;
            this.b = _temperingMaskB & this.wordMask;
            this.t = _temperingShiftT;
            this.c = _temperingMaskC & this.wordMask;
            this.ell = _temperingShiftL;
            this.f = _seedInitMultiplier;
            this.MT = new long[this.n];
            this.index = this.n + 1;
            this.loMask = (1L << this.r) - 1;
            this.hiMask = (~this.loMask) & this.wordMask;
        }
        //// Instance Methods ////
        @Override
        public synchronized void setSeed(long seed) {
            if (this.MT == null) {
                return;
            }
            this.index = this.n;
            this.MT[0] = seed;
            long downShift = this.w - 2;
            for (int i = 1; i < this.n; ++i) {
                long MT_prev = MT[i - 1];
                long MT_i = MT_prev;
                MT_i >>= downShift;
                MT_i ^= MT_prev;
                MT_i *= this.f;
                MT_i += i;
                this.MT[i] = MT_i & this.wordMask;
            }
        }
        @Override
        protected int next(int bits) {
            long num = this.extractNumber();
            long mask = (1L << bits) - 1;
            return (int)(num & mask);
        }
        public long extractNumber() {
            if (index >= n) {
                if (index > n) {
                    throw new IllegalStateException("Generator was never seeded.");
                }
                this.twist();
            }
            long y = MT[index];
            ++index;
            y = y ^ ((y >> u) & d);
            y = y ^ ((y << s) & b);
            y = y ^ ((y << t) & c);
            y = y ^ (y >> ell);
            y &= wordMask;
            if (y < 0) {
                y += 1;
                y += wordMask;
            }
            return y;
        }
        protected void twist() {
            for (int i = 0; i < n; ++i) {
                int j = (i + 1) % n;
                long x = (MT[i] & hiMask) + (MT[j] & loMask);
                long xA = x >> 1;
                if (x % 2 != 0) {
                    xA ^= a;
                }
                int k = (i + m) % n;
                MT[i] = MT[k] ^ xA;
            }
            index = 0;
        }
    }
    
    public static class MT19937Random extends MersenneTwisterRandom {
        //// Constructors ////
        public MT19937Random() {
            this(5489);
        }
        public MT19937Random(long seed) {
            super(
                32, 624, 397, 31,
                0x9908b0df,
                11, 0xFFFFFFFF,
                7, 0x9d2c5680,
                15, 0xefc60000,
                18,
                1812433253
            );
            this.setSeed(seed);
        }
    }
}
