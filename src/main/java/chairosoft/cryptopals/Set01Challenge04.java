package chairosoft.cryptopals;

import chairosoft.cryptopals.Set01Challenge03.SingleCharXorCipherResult;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * https://cryptopals.com/sets/1/challenges/4
 */
public class Set01Challenge04 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        File inputFile = new File(args[0]);
        List<String> textLines = Files.readAllLines(inputFile.toPath());
        List<byte[]> dataLines = textLines.stream().map(Common::fromHex).collect(Collectors.toList());
        List<SingleCharXorCipherResult> cipherResults = dataLines
            .stream()
            .map(Set01Challenge03::getEnglishCleartext)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        cipherResults.forEach(System.out::println);
    }
    
}
