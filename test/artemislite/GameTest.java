package artemislite;

import org.junit.jupiter.api.Test;

import javax.naming.InvalidNameException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.lineSeparator;
import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    Player player1 = new Player("1");
    Player player2 = new Player("2");
    Player player3 = new Player("3");

    ArrayList<Player> playerArr = new ArrayList<>();

    SystemName systemName1 = SystemName.EXPLORATION_GROUND_SYSTEM;
    SystemName systemName2 = SystemName.ORION_SPACECRAFT;
    int[] devCost = new int[]{0,0,0,0,0};
    int baseCost = 0;
    int costPerDev = 0;
    int difficulty = 0;

    SystemSquare ss1 = new SystemSquare("Square 1",
            1,
            "System 1",
            systemName1,
            difficulty,
            baseCost,
            costPerDev,
            devCost);

    SystemSquare ss2 = new SystemSquare("Square 2",
            2,
            "System 1",
            systemName1,
            difficulty,
            baseCost,
            costPerDev,
            devCost);

    SystemSquare ss3 = new SystemSquare("Square 3",
            3,
            "System 1",
            systemName2,
            difficulty,
            baseCost,
            costPerDev,
            devCost);


    GameTest() throws InvalidNameException {
    }

    @Test
    void testSellElementValid() throws BankruptcyException {
        int cost = 100;

        Collections.addAll(playerArr, player1, player2, player3);
        List<Player> players = Collections.unmodifiableList(playerArr);

        InputStream inBackup = System.in;
        ByteArrayInputStream inResources = new ByteArrayInputStream(("1" + lineSeparator() + "1" + lineSeparator() + "1" + lineSeparator() + cost + lineSeparator()).getBytes());
        System.setIn(inResources);
        Scanner scanner = new Scanner(inResources);
        int res = player1.getPlayerResources();

        player1.purchaseSquare(ss1);
        player1.purchaseSquare(ss2);
        Game.sellElement(scanner, player1);
        assertEquals(res + cost, player1.getPlayerResources());
        assertTrue(player2.getOwnedElements().contains(ss1));

        ByteArrayInputStream inElement = new ByteArrayInputStream(("1" + lineSeparator() + "1" + lineSeparator() + "2" + lineSeparator() + "1" + lineSeparator() + "2").getBytes());
        System.setIn(inElement);
        scanner = new Scanner(inElement);

        Game.sellElement(scanner, player2);
        assertTrue(player1.getOwnedElements().contains(ss1));
        assertFalse(player1.getOwnedElements().contains(ss2));
        assertTrue(player2.getOwnedElements().contains(ss2));
        assertFalse(player2.getOwnedElements().contains(ss1));
        System.setIn(inBackup);
    }

    @Test
    void testInputTimerValid() {
        assertTrue(Game.inputTimer(0));
    }

    @Test
    void testInputTimerInvalid() {
        InputStream inBackup = System.in;
        ByteArrayInputStream in = new ByteArrayInputStream(("1" + lineSeparator()).getBytes());
        System.setIn(in);
        new Scanner(in);
        assertFalse(Game.inputTimer(1));
    }
}