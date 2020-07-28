package chairosoft.cryptopals;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runners.model.MultipleFailureException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static chairosoft.cryptopals.Common.*;
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
    
    public static class NestedMatcher<T> extends TypeSafeMatcher<T> {
        //// Instance Fields ////
        public final Predicate<T> matchFn;
        public final String descriptionText;
        //// Constructors ////
        public NestedMatcher(Predicate<T> _matchFn, String _descriptionText) {
            this.matchFn = _matchFn;
            this.descriptionText = _descriptionText;
        }
        //// Instance Methods ////
        @Override
        protected boolean matchesSafely(T actualValue) {
            return this.matchFn.test(actualValue);
        }
        @Override
        public void describeTo(Description description) {
            description.appendText(this.descriptionText);
        }
    }
    
    
    ////// Static Methods //////
    public static byte[] getStdOut(MainMethod mainMethod, Object... argObjects) throws Exception {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] result;
        try (PrintStream tempOut = new PrintStream(baos)) {
            String[] args = new String[argObjects.length];
            for (int i = 0; i < args.length; ++i) {
                Object obj = argObjects[i];
                if (obj == null) {
                    args[i] = null;
                }
                else if (obj instanceof byte[]) {
                    args[i] = toBase64Text((byte[])obj);
                }
                else {
                    args[i] = String.valueOf(obj);
                }
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
    
    public static <T> NestedMatcher<T> nestedMatcher(String description, Predicate<T> matchFn) {
        return new NestedMatcher<>(matchFn, description);
    }
    
    public static void assertResultOutputStartsWith(
        String expectedResultText,
        long expectedResultLineCount,
        MainMethod mainMethod,
        Object... argObjects
    )
        throws Exception
    {
        assertResultOutput(startsWith(expectedResultText), expectedResultLineCount, mainMethod, argObjects);
    }
    
    public static void assertResultOutputContains(
        String expectedResultContains,
        long expectedResultLineCount,
        MainMethod mainMethod,
        Object... argObjects
    )
        throws Exception
    {
        assertResultOutput(containsString(expectedResultContains), expectedResultLineCount, mainMethod, argObjects);
    }
    
    public static void assertResultOutput(
        Matcher<String> resultMatcher,
        long expectedResultLineCount,
        MainMethod mainMethod,
        Object... argObjects
    )
        throws Exception
    {
        byte[] actualResultBytes = getStdOut(mainMethod, argObjects);
        String actualResult = toUtf8(actualResultBytes);
        assertThat("Result Output", actualResult, resultMatcher);
        long actualResultLineCount = actualResult.codePoints().filter(c -> c == '\n').count();
        assertThat("Line Count", actualResultLineCount, equalTo(expectedResultLineCount));
    }
    
    public static void assertResultOutputEquals(
        String expectedResult,
        MainMethod mainMethod,
        Object... argObjects
    )
        throws Exception
    {
        byte[] actualResultBytes = getStdOut(mainMethod, argObjects);
        String actualResult = toUtf8(actualResultBytes);
        String[] expectedResultLines = expectedResult.split("\\n");
        String[] actualResultLines = actualResult.split("\\n");
        assertThat("Line Count", actualResultLines.length, equalTo(expectedResultLines.length));
        List<Throwable> assertionErrors = new ArrayList<>();
        for (int i = 0; i < expectedResultLines.length; ++i) {
            String expectedLine = expectedResultLines[i];
            String actualLine = actualResultLines[i];
            try {
                assertThat(
                    String.format("Line %s", i + 1),
                    actualLine,
                    equalTo(expectedLine)
                );
            }
            catch (AssertionError ex) {
                assertionErrors.add(ex);
            }
        }
        try {
            MultipleFailureException.assertEmpty(assertionErrors);
        }
        catch (Error | Exception ex) {
            throw ex;
        }
        catch (Throwable th) {
            throw new Error("", th);
        }
    }
    
    public static void assertResultError(
        Class<? extends Throwable> errorType,
        MainMethod mainMethod,
        Object... argObjects
    )
        throws Exception
    {
        assertResultError(errorType, null, mainMethod, argObjects);
    }
    
    public static void assertResultError(
        Class<? extends Throwable> errorType,
        Matcher<String> errorMessageMatcher,
        MainMethod mainMethod,
        Object... argObjects
    )
        throws Exception
    {
        Throwable error = null;
        try {
            getStdOut(mainMethod, argObjects);
        }
        catch (Throwable ex) {
            error = ex;
        }
        assertThat("Error", error, notNullValue());
        System.err.printf("%s: %s\n", error.getClass(), error.getMessage());
        assertThat("Error Type", error.getClass(), typeCompatibleWith(errorType));
        if (errorMessageMatcher != null) {
            String errorMessage = error.getMessage();
            assertThat("Error Message", errorMessage, errorMessageMatcher);
        }
    }
    
}
