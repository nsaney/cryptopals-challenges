package chairosoft.cryptopals;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static chairosoft.cryptopals.Common.COMMON_CHARSET;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public abstract class TestBase {
    
    ////// Setup/Teardown Methods //////
    @BeforeClass
    public static void beforeClass() throws Exception {
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }
    
    @AfterClass
    public static void afterClass() throws Exception {
        System.out.println("________________________________________________________________________________");
    }
    
    
    ////// Static Inner Classes //////
    @FunctionalInterface
    public interface MainMethod {
        void doMain(String... args) throws Exception;
    }
    
    
    ////// Static Methods //////
    public static byte[] getStdOut(MainMethod mainMethod, Object... argObjects) throws Exception {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] result;
        try (PrintStream tempOut = new PrintStream(baos)) {
            String[] args = new String[argObjects.length];
            for (int i = 0; i < args.length; ++i) {
                args[i] = argObjects[i] == null ? null : String.valueOf(argObjects[i]);
            }
            System.setOut(tempOut);
            mainMethod.doMain(args);
        }
        finally {
            result = baos.toByteArray();
            System.setOut(originalOut);
            System.out.write(result);
            System.out.println();
        }
        return result;
    }
    
    public static void assertResultOutput(
        String expectedResultPrefix,
        long expectedResultLineCount,
        MainMethod mainMethod,
        Object... argObjects
    )
        throws Exception
    {
        byte[] actualResultBytes = getStdOut(mainMethod, argObjects);
        String actualResult = new String(actualResultBytes, COMMON_CHARSET);
        assertThat("Result Output", actualResult, startsWith(expectedResultPrefix));
        long actualResultLineCount = actualResult.codePoints().filter(c -> c == '\n').count();
        assertThat("Line Count", actualResultLineCount, equalTo(expectedResultLineCount));
    }
    
}
