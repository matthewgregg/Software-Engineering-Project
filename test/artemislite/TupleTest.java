package artemislite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.naming.InvalidNameException;

import static org.junit.jupiter.api.Assertions.*;

class TupleTest {

    Integer testInt;
    String testString;
    Player testPlayer;
    Triplet<Integer, String, Player> testTuple;

    @BeforeEach
    void setUp() throws InvalidNameException {
        testInt = 1;
        testString = "String";
        testPlayer = new Player(testString);
        testTuple = new Triplet<>(testInt, testString, testPlayer);
    }

    @Test
    void testConstructor() {
        Triplet<Integer, String, Player> testTupleConstructor = new Triplet<>(testInt, testString, testPlayer);
        assertEquals(testInt, testTupleConstructor.getFirst());
        assertEquals(testString, testTupleConstructor.getSecond());
        assertEquals(testPlayer, testTupleConstructor.getThird());
    }

    @Test
    void testGetFirst() {
        assertEquals(testInt, testTuple.getFirst());
    }

    @Test
    void testGetSecond() {
        assertEquals(testString, testTuple.getSecond());
    }

    @Test
    void testGetThird() {
        assertEquals(testPlayer, testTuple.getThird());
    }
}