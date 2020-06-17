package chairosoft.cryptopals.set01;

import chairosoft.cryptopals.set01.Challenge03.SingleCharXorCipherResult;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static chairosoft.cryptopals.Common.readFileLinesHex;

/**
 * https://cryptopals.com/sets/1/challenges/4
 */
public class Challenge04 {
    
    ////// Main Method //////
    public static void main(String... args) throws Exception {
        List<byte[]> dataLines = readFileLinesHex(args[0]);
        List<SingleCharXorCipherResult> cipherResults = dataLines
            .stream()
            .map(Challenge03::getMostLikelyEnglishCleartext)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        cipherResults.forEach(System.out::print);
    }
    
}
