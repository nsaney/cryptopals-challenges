package chairosoft.cryptopals;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

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
    public static byte[] getStdOut(MainMethod mainMethod, String... args) throws Exception {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] result;
        try (PrintStream tempOut = new PrintStream(baos)) {
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
    
}
