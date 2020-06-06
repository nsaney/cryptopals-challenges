package chairosoft.cryptopals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public final class TestUtils {
    
    ////// Constructor //////
    private TestUtils() { throw new UnsupportedOperationException(); }
    
    
    ////// Static Inner Classes //////
    @FunctionalInterface
    public interface MainMethod {
        void doMain(String... args) throws Exception;
    }
    
    
    ////// Static Methods //////
    public static byte[] getStdOut(MainMethod mainMethod, String... args) throws Exception {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PrintStream tempOut = new PrintStream(baos)) {
            System.setOut(tempOut);
            mainMethod.doMain(args);
        }
        finally {
            System.setOut(originalOut);
        }
        return baos.toByteArray();
    }
    
}
