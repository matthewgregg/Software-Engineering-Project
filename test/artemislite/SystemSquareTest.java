package artemislite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SystemSquareTest {

    SystemName systemName1, systemName2;
    int[] landingCostValid, landingCostInvalidLower, landingCostInvalidUpper;
    SystemSquare ss1, ss2, ss3;
    boolean initialMortgaged, initialOwned;
    int baseCost, costPerDev, difficulty, pos1, pos2, pos3, initialDev;
    int systemTypeSystemName2;
    String name;
    String INVALID_SYSTEM_NAME, MAX_DEVELOPMENT_REACHED, INVALID_LANDING_COST;

    @BeforeEach
    void setUp() {

        name = "Square";
        systemName1 = SystemName.EXPLORATION_GROUND_SYSTEM;
        systemName2 = SystemName.ORION_SPACECRAFT;
        pos1 = 1;
        pos2 = 2;
        pos3 = 3;
        baseCost = 0;
        costPerDev = 0;
        difficulty = 0;
        landingCostValid = new int[]{0, 0, 0, 0, 0};
        landingCostInvalidLower = new int[]{0};
        landingCostInvalidUpper = new int[]{0, 0, 0, 0, 0, 0};
        initialDev = 0;
        initialMortgaged = false;
        initialOwned = false;
        systemTypeSystemName2 = 3;

        INVALID_SYSTEM_NAME = "Invalid system name";
        MAX_DEVELOPMENT_REACHED = "Element fully developed";
        INVALID_LANDING_COST = "Invalid landing cost.";

        ss1 = new SystemSquare(name, pos1, systemName1, difficulty, baseCost, costPerDev, landingCostValid);
        ss2 = new SystemSquare(name, pos2, systemName1, difficulty, baseCost, costPerDev, landingCostValid);

    }

    @Test
    void testConstructorValid() {
        ss3 = new SystemSquare(name, pos3, systemName2, difficulty, baseCost, costPerDev, landingCostValid);
        assertEquals(name, ss3.getSquareName());
        assertEquals(pos3, ss3.getPosition());
        assertNull(ss3.getMessage());
        assertEquals(systemName2, ss3.getSystemNameEnum());
        assertEquals(Game.stringifyEnum(systemName2), ss3.getSystemNameString());
        assertEquals(difficulty, ss3.getQuizDifficulty());
        assertEquals(baseCost, ss3.getBaseCost());
        assertEquals(costPerDev, ss3.getCostPerDevelopment());
        assertEquals(initialDev, ss3.getDevelopment());
        assertEquals(landingCostValid[ss3.getDevelopment()], ss3.getLandingCost());
        assertEquals(initialMortgaged, ss3.isMortgaged());
        assertEquals(initialOwned, ss3.isOwned());
        assertEquals(systemTypeSystemName2, ss3.getSystemType());
    }

    @Test
    void testConstructorInvalid() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
           new SystemSquare(name, pos3, systemName1, difficulty, baseCost, costPerDev, landingCostInvalidLower);
        });
        assertEquals(INVALID_LANDING_COST, e.getMessage());

        e = assertThrows(IllegalArgumentException.class, () -> {
            new SystemSquare(name, pos3, systemName1, difficulty, baseCost, costPerDev, landingCostInvalidUpper);
        });
        assertEquals(INVALID_LANDING_COST, e.getMessage());
    }

    @Test
    void testGetSystemNameString() {

    }

    @Test
    void testGetSystemNameEnum() {

    }

    @Test
    void testGetSystemType() {

    }

    @Test
    void testGetQuizDifficulty() {

    }

    @Test
    void testGetBaseCost() {

    }

    @Test
    void testGetCostPerDevelopment() {

    }

    @Test
    void testGetSetDevelopment() {

    }

    @Test
    void testGetLandingCost() {

    }

    @Test
    void testGetMaxDevelopment() {

    }

    @Test
    void testGetSetIsMortgaged() {

    }

    @Test
    void testGetSetIsOwned() {

    }

}