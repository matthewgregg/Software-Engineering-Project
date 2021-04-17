package artemislite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.naming.InvalidNameException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ComparatorTest {

    Square s1, s2;
    SystemSquare s3;
    String name;
    int pos1, pos2;
    String message;
    SystemName systemName;
    int quizDifficulty, baseCost, costPerDev;
    private final ComparePosition comparePos = new ComparePosition();
    Player p1, p2, p3;
    String namePlayer;
    int[] landingCost;
    private final CompareWinners compareWin = new CompareWinners();

    @BeforeEach
    void setUp() throws InvalidNameException {
        name = "Square";
        pos1 = 1;
        pos2 = 0;
        message = "Message";
        systemName = SystemName.EXPLORATION_GROUND_SYSTEM;
        quizDifficulty = 1;
        baseCost = 100;
        costPerDev = 10;
        landingCost = new int[] {1,2,3,4,5};
        s1 = new Square(name, pos1, message);
        s2 = new Square(name,pos2, message);
        s3 = new SystemSquare(name, pos1, systemName, quizDifficulty, baseCost, costPerDev, landingCost);

        namePlayer = "Player";
        p1 = new Player(namePlayer);
        p2 = new Player(namePlayer);
        p3 = new Player(namePlayer);
    }

    @Test
    void testComparePosition() {
        assertTrue(comparePos.compare(s1, s2) > 0);
        assertEquals(0, comparePos.compare(s1, s3));
    }

    @Test
    void testCompareWinners() throws BankruptcyException {
        p1.addResources(200);
        p2.addResources(100);
        p3.addResources(100);
        p3.purchaseSquare(s3);
        p3.developSquare(s3, 1);

        assertTrue(compareWin.compare(p1, p2) > 0);
        assertTrue(compareWin.compare(p3, p2) > 0);
    }
}