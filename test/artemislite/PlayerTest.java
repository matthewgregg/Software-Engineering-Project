package artemislite;

import org.junit.jupiter.api.Test;

import javax.naming.InvalidNameException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    SystemName systemName1 = SystemName.EXPLORATION_GROUND_SYSTEM;
    SystemName systemName2 = SystemName.ORION_SPACECRAFT;
    SystemName systemName3 = SystemName.GATEWAY_OUTPOST;
    SystemName systemName4 = SystemName.LUNAR_LANDER;
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

    SystemSquare ss4 = new SystemSquare("Square 4",
            4,
            "System 1",
            systemName2,
            difficulty,
            baseCost,
            costPerDev,
            devCost);

    SystemSquare ss5 = new SystemSquare("Square 5",
            5,
            "System 1",
            systemName2,
            difficulty,
            baseCost,
            costPerDev,
            devCost);

    SystemSquare ss6 = new SystemSquare("Square 6",
            7,
            "System 1",
            systemName3,
            difficulty,
            baseCost,
            costPerDev,
            devCost);

    SystemSquare ss7 = new SystemSquare("Square 7",
            8,
            "System 1",
            systemName3,
            difficulty,
            baseCost,
            costPerDev,
            devCost);

    SystemSquare ss8 = new SystemSquare("Square 8",
            9,
            "System 1",
            systemName3,
            difficulty,
            baseCost,
            costPerDev,
            devCost);

    SystemSquare ss9 = new SystemSquare("Square 9",
            10,
            "System 1",
            systemName4,
            difficulty,
            baseCost,
            costPerDev,
            devCost);

    SystemSquare ss10 = new SystemSquare("Square 10",
            11,
            "System 1",
            systemName4,
            difficulty,
            baseCost,
            costPerDev,
            devCost);

    SortedSet<SystemSquare> squares = new TreeSet<>(new ComparePosition());

    Player player;

    @Test
    void testGetCompletedSystemValid() throws InvalidNameException, BankruptcyException {
        player = new Player("Test Player");
        Collections.addAll(squares, ss1, ss2, ss3, ss4, ss5, ss6, ss7, ss8, ss9, ss10);

        for (SystemSquare ss : squares) {
            player.purchaseSquare(ss);
        }

        assertTrue(player.getCompletedSystems().contains(systemName1));
        assertTrue(player.getCompletedSystems().contains(systemName2));
        assertTrue(player.getCompletedSystems().contains(systemName3));
        assertTrue(player.getCompletedSystems().contains(systemName4));
        squares.clear();
    }

    @Test
    void testGetCompletedSystemInvalid() throws InvalidNameException, BankruptcyException {
        player = new Player("Test Player");
        Collections.addAll(squares, ss1, ss3, ss4, ss6, ss7, ss9);

        assertNull(player.getCompletedSystems());

        for (SystemSquare ss : squares) {
            player.purchaseSquare(ss);
            assertNull(player.getCompletedSystems());
        }

        ArrayList<SystemSquare> squares2 = new ArrayList<>();
        Collections.addAll(squares2, ss2, ss5, ss8, ss10);
        squares.addAll(squares2);

        for (SystemSquare ss : squares2) {
            Player player2 = player;
            player2.purchaseSquare(ss);
            for (SystemSquare s : squares) {
                s.setMortgaged(true);
                assertNull(player2.getCompletedSystems());
                if (!squares2.contains(s)) {
                    s.setMortgaged(false);
                }
            }
        }
    }
}