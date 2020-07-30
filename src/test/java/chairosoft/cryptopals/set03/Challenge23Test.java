package chairosoft.cryptopals.set03;

import chairosoft.cryptopals.Common;
import chairosoft.cryptopals.TestBase;
import chairosoft.cryptopals.set03.Challenge21.MT19937Random;
import org.junit.Test;

import java.io.*;
import java.util.Random;

/**
 * https://cryptopals.com/sets/3/challenges/23
 */
public class Challenge23Test extends TestBase {
    
    ////// Instance Methods - Tests //////
    @Test
    public void doTest() throws Exception {
        long seed = System.currentTimeMillis();
        int count = 20;
        File testFile = new File("target/test-values/23--inputs.txt");
        if (!testFile.getParentFile().mkdirs()) {
            throw new FileNotFoundException("Unable to create file: " + testFile);
        }
        MT19937Random mt19937Random = new MT19937Random(seed);
        try (FileOutputStream fout = new FileOutputStream(testFile)
            ; PrintStream fps = new PrintStream(fout, true, Common.COMMON_CHARSET.name())
        ) {
            for (int i = 0; i < MT19937Random.RECURRENCE_DEGREE; ++i) {
                long num = mt19937Random.extractNumber();
                fps.println(num);
            }
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        for (int i = 0; i < count; ++i) {
            long num = mt19937Random.extractNumber();
            pw.println(num);
        }
        String expectedResultText = sw.toString();
        assertResultOutputEquals(
            expectedResultText,
            Challenge23::main,
            count,
            testFile
        );
    }
    
}
