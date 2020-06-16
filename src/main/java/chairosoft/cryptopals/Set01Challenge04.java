package chairosoft.cryptopals;

import chairosoft.cryptopals.Set01Challenge03.SingleCharXorCipherResult;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static chairosoft.cryptopals.Common.readFileLinesHex;

/**
 * https://cryptopals.com/sets/1/challenges/4
 */
public class Set01Challenge04 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        List<byte[]> dataLines = readFileLinesHex(args[0]);
        List<SingleCharXorCipherResult> cipherResults = dataLines
            .stream()
            .map(Set01Challenge03::getMostLikelyEnglishCleartext)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        cipherResults.forEach(System.out::print);
    }
    
}
