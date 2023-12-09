package es.danirod.aoc.aoc23;

import es.danirod.aoc.aoc23.support.GridFile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Stream;

public class AocUtils {

    public static List<String> fileAsLines(String path) throws IOException {
        try (InputStream is = AocUtils.class.getClassLoader().getResourceAsStream(path);
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr)) {
            // Don't let this line fool you: the stream is lazy; therefore,
            // returning just the output of .lines() will give you a Stream
            // that will not work once the InputStream is closed. You have
            // to collect the lines to have them available after closing.
            return br.lines().toList();
        }
    }

    public static Stream<String> fileAsLineStream(String path) throws IOException {
        return fileAsLines(path).stream();
    }

    public static GridFile gridFile(String path) throws IOException {
        return new GridFile(fileAsLines(path));
    }

    public static long mcd(long a, long b) {
        while (b > 0) {
            var c = b;
            b = a % b;
            a = c;
        }
        return a;
    }

    public static long mcm(long a, long b) {
        return a * (b / mcd(a, b));
    }
}
