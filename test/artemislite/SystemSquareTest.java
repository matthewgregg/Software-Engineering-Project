package artemislite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SystemSquareTest {

    SystemName systemName1, systemName2;
    int[] landingCostValid, landingCostInvalidLower, landingCostInvalidUpper;
    SystemSquare ss1, ss2, ss3;
    boolean initialMortgaged, initialOwned;
    int baseCost, costPerDev, difficulty, pos1, pos2, pos3, initDev, devValidUpper;
    int systemTypeSystemName2, devInvalidLower, devInvalidUpper;
    String name;
    String INVALID_SYSTEM_NAME, MAX_DEVELOPMENT_REACHED, INVALID_LANDING_COST;
    int MAX_DEVELOPMENT;

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
        initDev = 0;
        devValidUpper = 4;
        devInvalidLower = -1;
        devInvalidUpper = 5;
        initialMortgaged = false;
        initialOwned = false;
        systemTypeSystemName2 = 3;

        INVALID_SYSTEM_NAME = "Invalid system name";
        MAX_DEVELOPMENT_REACHED = "Element fully developed";
        INVALID_LANDING_COST = "Invalid landing cost.";
        MAX_DEVELOPMENT = 4;

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
        assertEquals(initDev, ss3.getDevelopment());
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
        assertEquals(name, ss1.getSquareName());
    }

    @Test
    void testGetSystemNameEnum() {
        assertEquals(systemName1, ss1.getSystemNameEnum());
    }

    @Test
    void testGetSystemType() {
        assertEquals(Game.stringifyEnum(systemName1), ss1.getSystemNameString());
    }

    @Test
    void testGetQuizDifficulty() {
        assertEquals(difficulty, ss1.getQuizDifficulty());
    }

    @Test
    void testGetBaseCost() {
        assertEquals(baseCost, ss1.getBaseCost());
    }

    @Test
    void testGetCostPerDevelopment() {
        assertEquals(costPerDev, ss1.getCostPerDevelopment());
    }

    @Test
    void testGetSetDevelopmentValid() {
        ss2.setDevelopment(initDev);
        assertEquals(initDev, ss2.getDevelopment());

        ss2.setDevelopment(devValidUpper);
        assertEquals(devValidUpper, ss2.getDevelopment());
    }

    @Test
    void testGetSetDevelopmentInvalid() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
           ss2.setDevelopment(devInvalidLower);
        });
        assertEquals(MAX_DEVELOPMENT_REACHED, e.getMessage());

        e = assertThrows(IllegalArgumentException.class, () -> {
            ss2.setDevelopment(devInvalidUpper);
        });
        assertEquals(MAX_DEVELOPMENT_REACHED, e.getMessage());
    }

    @Test
    void testGetLandingCost() {
        assertEquals(landingCostValid[ss1.getDevelopment()], ss1.getLandingCost());
    }

    @Test
    void testGetMaxDevelopment() {
        assertEquals(MAX_DEVELOPMENT, ss1.getMaxDevelopment());
    }

    @Test
    void testGetSetIsMortgaged() {
        ss1.setMortgaged(true);
        assertTrue(ss1.isMortgaged());

        ss1.setMortgaged(false);
        assertFalse(ss1.isMortgaged());
    }

    @Test
    void testGetSetIsOwned() {
        ss1.setOwned(true);
        assertTrue(ss1.isOwned());

        ss1.setOwned(false);
        assertFalse(ss1.isOwned());
    }

}