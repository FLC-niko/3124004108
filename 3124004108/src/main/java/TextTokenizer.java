import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Converts text into comparable tokens without requiring a third-party
 * dictionary. Chinese characters are treated as single-character tokens;
 * Latin letters and digits are grouped into words.
 */
public final class TextTokenizer {

    public List<String> tokenize(String text) {
        Objects.requireNonNull(text, "text");

        List<String> tokens = new ArrayList<>();
        StringBuilder word = new StringBuilder();

        for (int offset = 0; offset < text.length();) {
            int codePoint = text.codePointAt(offset);
            offset += Character.charCount(codePoint);

            if (isChineseCharacter(codePoint)) {
                flushWord(word, tokens);
                tokens.add(new String(Character.toChars(codePoint)));
            } else if (Character.isLetterOrDigit(codePoint)) {
                word.appendCodePoint(Character.toLowerCase(codePoint));
            } else {
                flushWord(word, tokens);
            }
        }

        flushWord(word, tokens);
        return tokens;
    }

    private static boolean isChineseCharacter(int codePoint) {
        return Character.UnicodeScript.of(codePoint) == Character.UnicodeScript.HAN;
    }

    private static void flushWord(StringBuilder word, List<String> tokens) {
        if (word.length() > 0) {
            tokens.add(word.toString());
            word.setLength(0);
        }
    }
}
