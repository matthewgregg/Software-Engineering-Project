package artemislite;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static java.lang.System.lineSeparator;
import static org.junit.jupiter.api.Assertions.*;

class QuizTest {

    @Test
    void testGenerateQuestions() {
        ByteArrayInputStream in = new ByteArrayInputStream(("1" + lineSeparator() + "1" + lineSeparator() + "1" + lineSeparator() + "1" + lineSeparator()).getBytes());
        System.setIn(in);
        Scanner scanner = new Scanner(in);
        Quiz.generateQuestions(scanner, 3);

        in = new ByteArrayInputStream(("1" + lineSeparator() + "a" + lineSeparator() + "1" + lineSeparator() + "1" + lineSeparator() + "1" + lineSeparator()).getBytes());
        System.setIn(in);
        scanner = new Scanner(in);
        Quiz.generateQuestions(scanner, 3);
    }
}