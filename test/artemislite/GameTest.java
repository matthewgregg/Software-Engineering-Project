package artemislite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import static java.lang.System.lineSeparator;
import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    List<Player> players;
    Player player1, player2;
    SystemSquare ss1, ss2, ss3;
    SystemName systemName1, systemName2;
    int[] devCost;
    int baseCost, costPerDev, difficulty;

    @BeforeEach
    void setUp() throws Exception {
        players = new ArrayList<>();
        player1 = new Player("Player 1");
        player2 = new Player("Player 2");
        players.add(player1);
        players.add(player2);

        systemName1 = SystemName.EXPLORATION_GROUND_SYSTEM;
        systemName2 = SystemName.ORION_SPACECRAFT;
        devCost = new int[]{0, 0, 0, 0, 0};
        baseCost = 0;
        costPerDev = 0;
        difficulty = 0;

        ss1 = new SystemSquare("Square 1",
                1,
                systemName1,
                difficulty,
                baseCost,
                costPerDev,
                devCost);

        ss2 = new SystemSquare("Square 2",
                2,
                systemName1,
                difficulty,
                baseCost,
                costPerDev,
                devCost);

        ss3 = new SystemSquare("Square 3",
                3,
                systemName2,
                difficulty,
                baseCost,
                costPerDev,
                devCost);
    }

    @Test
    void testSellElementValid() throws BankruptcyException {
        int cost = 100;

        ByteArrayInputStream inResources = new ByteArrayInputStream(("1" + lineSeparator() + "2" + lineSeparator() + "1" + lineSeparator() + cost + lineSeparator()).getBytes());
        System.setIn(inResources);
        Scanner scanner = new Scanner(inResources);
        int res = player1.getPlayerResources();

        player1.purchaseSquare(ss1);
        player1.purchaseSquare(ss2);
        Game.tradeWithPlayer(scanner, player1, players);
        assertEquals(res + cost, player1.getPlayerResources());
        assertTrue(player2.getOwnedSquares().contains(ss1));

        ByteArrayInputStream inElement = new ByteArrayInputStream(("1" + lineSeparator() + "2" + lineSeparator() + "1" + lineSeparator() + "1" + lineSeparator() + "2").getBytes());
        System.setIn(inElement);
        scanner = new Scanner(inElement);

        Game.tradeWithPlayer(scanner, player2, players);
        assertTrue(player1.getOwnedSquares().contains(ss1));
        assertFalse(player1.getOwnedSquares().contains(ss2));
        assertTrue(player2.getOwnedSquares().contains(ss2));
        assertFalse(player2.getOwnedSquares().contains(ss1));
    }

    @Test
    void testInputTimerValid() {
        assertTrue(Game.inputTimer(0));
    }

    @Test
    void testInputTimerInvalid() {
        ByteArrayInputStream in = new ByteArrayInputStream(("1" + lineSeparator()).getBytes());
        System.setIn(in);
        new Scanner(in);
        assertFalse(Game.inputTimer(1));
    }

    @Test
    void testRollDice() {
        ArrayList<Integer> rolls = new ArrayList<>();
        // as the loop value increases, the delta required will decrease as the results tends towards a normal dist.
        int loop = 1000000;
        int n = 3;
        int averageFrequency = loop / (2 * n - 2 + 1);
        for (int i = 0; i < loop; i++) {
            int[] roll = Game.rollDice(n);
            rolls.add(roll[0] + roll[1]);
        }
        Map<Integer, Long> map = rolls.stream().collect(Collectors.groupingBy(i -> i, Collectors.counting()));
        for (Long l : map.values()) {
            assertEquals(l.intValue(), averageFrequency, averageFrequency * 0.01);
        }
    }
}