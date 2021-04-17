package artemislite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SquareTest {

    Square s1;
    String name, message;
    int pos;

    @BeforeEach
    void setUp() {
        name = "Square";
        pos = 0;
        message = "Message";
        s1 = new Square(name, pos, message);
    }

    @Test
    void testGetSquareName() {
        assertEquals(name, s1.getSquareName());
    }

    @Test
    void testGetSquarePosition() {
        assertEquals(pos, s1.getPosition());
    }

    @Test
    void testGetMessage() {
        assertEquals(message, s1.getMessage());
    }
}