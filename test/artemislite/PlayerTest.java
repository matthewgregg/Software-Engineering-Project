package artemislite;

import org.junit.jupiter.api.Test;

import javax.naming.InvalidNameException;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    SystemName systemName1 = SystemName.SYSTEM_NAME_1;
    SystemName systemName2 = SystemName.SYSTEM_NAME_2;
    SystemName systemName3 = SystemName.SYSTEM_NAME_3;
    SystemName systemName4 = SystemName.SYSTEM_NAME_4;
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

    ArrayList<SystemSquare> squares = new ArrayList<>();

    Player player;

    @Test
    void testGetCompletedSystemValid() throws InvalidNameException {
        player = new Player(1, "Test Player");
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
    void testGetCompletedSystemInvalid() throws InvalidNameException {
        player = new Player(1, "Test Player");
        //test for player with single square
        player.purchaseSquare(ss1);
        assertNull(player.getCompletedSystems());

        //test for player with two squares
        player.purchaseSquare(ss3);
        assertNull(player.getCompletedSystems());

        //three squares
        player.purchaseSquare(ss6);
        assertNull(player.getCompletedSystems());

        //single square of each type
        player.purchaseSquare(ss9);
        assertNull(player.getCompletedSystems());

        //test for player with one less than complete system
        player.purchaseSquare(ss4);
        assertNull(player.getCompletedSystems());
        player.purchaseSquare(ss7);
        assertNull(player.getCompletedSystems());
    }
}