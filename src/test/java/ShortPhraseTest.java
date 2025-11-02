import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ShortPhraseTest {
    @Test
    public void shortPhrase() {

        String phrase = "Hello, world! Nice to meet you";
        Assertions.assertTrue(phrase.length() > 15,"Length is less than 15 symbols");
    }
}
