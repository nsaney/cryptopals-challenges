package chairosoft.cryptopals.set03;

import chairosoft.cryptopals.TestBase;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import static chairosoft.cryptopals.Common.randomBytes;
import static org.hamcrest.Matchers.equalTo;

/**
 * https://cryptopals.com/sets/3/challenges/21
 */
public class Challenge21Test extends TestBase {
    
    ////// Instance Methods - Tests //////
    @Test
    public void doTest() throws Exception {
        // see https://oeis.org/A221557
        long seed = 5489;
        int count = 50;
        File referenceFile = new File("src/test/resources/challenge-data/set03/21-b221557.txt");
        List<String> referenceLines = Files.readAllLines(referenceFile.toPath()).subList(0, count);
        String expectedOutput = String.join("\n", referenceLines);
        assertResultOutputEquals(
            expectedOutput,
            Challenge21::main,
            seed,
            count
        );
    }
    
}
