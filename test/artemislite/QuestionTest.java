package artemislite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuestionTest {

    String question, answerValid, answerInvalid;
    String[] choices;
    int difficultyValidLower, difficultyValidUpper, difficultyInvalidLower, difficultyInvalidUpper;
    String INVALID_ANS, INVALID_DIFF;
    Question q;

    @BeforeEach
    void setUp() {
        question = "Question";
        choices = new String[] { "1", "2", "3"};
        answerValid = "1";
        answerInvalid = "5";
        difficultyValidLower = 1;
        difficultyValidUpper = 4;
        difficultyInvalidLower = 0;
        difficultyInvalidUpper = 5;
        INVALID_ANS = "The answer you entered doesn't match any of the choices";
        INVALID_DIFF = "The difficulty must be between 1 and 4;";
        q = new Question(question, choices, answerValid, difficultyValidLower);
    }

    @Test
    void testConstructorValid() {
        Question q1 = new Question(question, choices, answerValid, difficultyValidLower);
        assertEquals(question, q1.getQuestion());
        assertEquals(choices, q1.getChoices());
        assertEquals(answerValid, q1.getAnswer());
        assertEquals(difficultyValidLower, q1.getDifficulty());
    }

    @Test
    void testConstructorInvalid() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new Question(question, choices, answerInvalid, difficultyValidLower);
        });
        assertEquals(INVALID_ANS, e.getMessage());

        e = assertThrows(IllegalArgumentException.class, () -> {
            new Question(question, choices, answerValid, difficultyInvalidLower);
        });
        assertEquals(INVALID_DIFF, e.getMessage());

        e = assertThrows(IllegalArgumentException.class, () -> {
            new Question(question, choices, answerValid, difficultyInvalidUpper);
        });
        assertEquals(INVALID_DIFF, e.getMessage());
    }

    @Test
    void testGetQuestion() {
        assertEquals(question, q.getQuestion());
    }

    @Test
    void testGetChoices() {
        assertEquals(choices, q.getChoices());
    }

    @Test
    void testGetAnswer() {
        assertEquals(answerValid, q.getAnswer());
    }

    @Test
    void testGetDifficulty() {
        assertEquals(difficultyValidLower, q.getDifficulty());
    }
}