package artemislite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.naming.InvalidNameException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PlayerTest - test the Player class (and Actor as Player call's Actor's methods)
 */
class PlayerTest {
    SystemName systemName1, systemName2, systemName3, systemName4;
    int[] landingCost;
    int baseCost, costPerDev, difficulty, playerInitialRes, playerInitPos, resourcesValid, developmentValid, developmentInvalid;
    int bankruptTrue1, bankruptTrue2, bankruptFalse;
    int positionValid1, positionValid2, positionInvalid1, positionInvalid2, positionInvalid3;
    SystemSquare ss1, ss2, ss3, ss4, ss5, ss6, ss7, ss8, ss9, ss10;
    SortedSet<SystemSquare> squares;
    String nameValid, nameInvalid, nameInvalidNull;
    String INVALID_SQUARE_TO_DEVELOP, MAX_DEVELOPMENT_REACHED, BANKRUPTCY;
    int minPosition, maxPosition;
    Player player1, player2, player3, player4;
    String role1, role2, role3, role4;

    @BeforeEach
    void setUp() throws InvalidNameException {
        systemName1 = SystemName.EXPLORATION_GROUND_SYSTEM;
        systemName2 = SystemName.ORION_SPACECRAFT;
        systemName3 = SystemName.GATEWAY_OUTPOST;
        systemName4 = SystemName.LUNAR_LANDER;

        landingCost = new int[]{0, 0, 0, 0, 0};

        baseCost = 0;
        costPerDev = 0;
        difficulty = 0;
        playerInitialRes = 1500;
        playerInitPos = 0;
        resourcesValid = 100;
        developmentValid = 1;
        developmentInvalid = 5;

        bankruptTrue1 = 1;
        bankruptTrue2 = 200;
        bankruptFalse = 201;

        positionValid1 = 1;
        positionValid2 = 11;
        positionInvalid1 = 12;
        positionInvalid2 = 25;
        positionInvalid3 = -1;

        minPosition = 0;
        maxPosition = 11;

        nameValid = "Player";
        nameInvalid = "";
        nameInvalidNull = null;

        role1 = "Commander";
        role2 = "Command Module Pilot";
        role3 = "Lunar Module Pilot";
        role4 = "Docking Module Pilot";

        INVALID_SQUARE_TO_DEVELOP = "The player does not own this square.";
        MAX_DEVELOPMENT_REACHED = "Element fully developed";
        BANKRUPTCY = "You've gone bankrupt";

        ss1 = new SystemSquare("Square 1", 1, systemName1, difficulty, baseCost, costPerDev, landingCost);
        ss2 = new SystemSquare("Square 2", 2, systemName1, difficulty, baseCost, costPerDev, landingCost);
        ss3 = new SystemSquare("Square 3", 3, systemName2, difficulty, baseCost, costPerDev, landingCost);
        ss4 = new SystemSquare("Square 4", 4, systemName2, difficulty, baseCost, costPerDev, landingCost);
        ss5 = new SystemSquare("Square 5", 5, systemName2, difficulty, baseCost, costPerDev, landingCost);
        ss6 = new SystemSquare("Square 6", 7, systemName3, difficulty, baseCost, costPerDev, landingCost);
        ss7 = new SystemSquare("Square 7", 8, systemName3, difficulty, baseCost, costPerDev, landingCost);
        ss8 = new SystemSquare("Square 8", 9, systemName3, difficulty, baseCost, costPerDev, landingCost);
        ss9 = new SystemSquare("Square 9", 10, systemName4, difficulty, baseCost, costPerDev, landingCost);
        ss10 = new SystemSquare("Square 10", 11, systemName4, difficulty, baseCost, costPerDev, landingCost);

        squares = new TreeSet<>(new ComparePosition());

        player1 = new Player(nameValid);
        player2 = new Player(nameValid);
        player3 = new Player(nameValid);
        player4 = new Player(nameValid);

    }

    @Test
    void testConstructorValid() throws InvalidNameException {
        Player playerTest = new Player(nameValid);
        assertEquals(nameValid, playerTest.getName());
        assertEquals(playerInitialRes, playerTest.getPlayerResources());
        assertEquals(playerInitPos, playerTest.getPosition());
    }

    @Test
    void testConstructorInvalid() {
        assertThrows(InvalidNameException.class, () -> {
            new Player(nameInvalid);
        });

        assertThrows(InvalidNameException.class, () -> {
            new Player(nameInvalidNull);
        });
    }

    @Test
    void testGetPosition() {
        assertEquals(playerInitPos, player1.getPosition());
    }

    @Test
    void testUpdatePositionValid() {
        player1.updatePosition(positionValid1 - player1.getPosition());
        assertEquals(positionValid1, player1.getPosition());

        player1.updatePosition(positionValid2 - player1.getPosition());
        assertEquals(positionValid2, player1.getPosition());
    }

    @Test
    void testUpdatePositionInvalid() {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            player1.updatePosition(positionInvalid1 - player1.getPosition());
            assertTrue(player1.getPosition() >= minPosition);
            assertTrue(player1.getPosition() <= maxPosition);
        });

        assertThrows(IndexOutOfBoundsException.class, () -> {
            player1.updatePosition(positionInvalid2 - player1.getPosition());
            assertTrue(player1.getPosition() >= minPosition);
            assertTrue(player1.getPosition() <= maxPosition);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            player1.updatePosition(positionInvalid3);
        });
    }

    @Test
    void testGetOwnedSquares() throws BankruptcyException, InvalidNameException {
        player1.purchaseSquare(ss1);
        assertTrue(player1.getOwnedSquares().size() != 0);
        assertTrue(player1.getOwnedSquares().contains(ss1));
    }

    @Test
    void testAddResourcesValid() throws BankruptcyException {
        int res = player1.getPlayerResources();
        player1.addResources(resourcesValid);
        assertEquals(res + resourcesValid, player1.getPlayerResources());
    }

    @Test
    void testAddResourcesInvalid() {
        int res = player1.getPlayerResources();
        BankruptcyException e = assertThrows(BankruptcyException.class, () -> {
            player1.addResources(-1 * res - 1);
        });
        assertEquals(BANKRUPTCY, e.getMessage());
    }

    @Test
    void testDevelopSquareValid() throws BankruptcyException {
        player1.purchaseSquare(ss1);
        player1.developSquare(ss1, developmentValid);
        assertEquals(ss1.getDevelopment(), developmentValid);
    }

    @Test
    void testDevelopSquareInvalid() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            player1.purchaseSquare(ss1);
            player1.developSquare(ss1, developmentInvalid);
        });
        assertEquals(MAX_DEVELOPMENT_REACHED, e.getMessage());

        e = assertThrows(IllegalArgumentException.class, () -> {
            player1.developSquare(ss2, developmentInvalid);
        });
        assertEquals(INVALID_SQUARE_TO_DEVELOP, e.getMessage());
    }

    @Test
    void testPurchaseSquare() throws BankruptcyException {
        player1.purchaseSquare(ss1);
        assertTrue(player1.getOwnedSquares().contains(ss1));
        player1.purchaseSquare(ss2, resourcesValid);
        assertTrue(player1.getOwnedSquares().contains(ss2));
    }

    @Test
    void testRemoveSquare() {
        player1.removeSquare(ss1);
        assertFalse(player1.getOwnedSquares().contains(ss1));
    }

    @Test
    void testGetMinimumOwnedDevCost() throws BankruptcyException {
        player1.purchaseSquare(ss1);
        player1.purchaseSquare(ss2);
        player1.purchaseSquare(ss3);
        assertEquals(ss1.getBaseCost(), player1.getMinimumOwnedDevCost());
    }

    @Test
    void testGetDevelopableSystemValid() throws InvalidNameException, BankruptcyException {
        player1 = new Player(nameValid);

        Collections.addAll(squares, ss1, ss2, ss3, ss4, ss5, ss6, ss7, ss8, ss9, ss10);

        for (SystemSquare ss : squares) {
            player1.purchaseSquare(ss);
        }

        assertTrue(player1.getDevelopableSystems().contains(systemName1));
        assertTrue(player1.getDevelopableSystems().contains(systemName2));
        assertTrue(player1.getDevelopableSystems().contains(systemName3));
        assertTrue(player1.getDevelopableSystems().contains(systemName4));
        squares.clear();
    }

    @Test
    void testGetDevelopableSystemInvalid() throws InvalidNameException, BankruptcyException {
        player1 = new Player("Test Player");
        Collections.addAll(squares, ss1, ss3, ss4, ss6, ss7, ss9);

        assertNull(player1.getDevelopableSystems());

        for (SystemSquare ss : squares) {
            player1.purchaseSquare(ss);
            assertNull(player1.getDevelopableSystems());
        }

        ArrayList<SystemSquare> finalSystemSquare = new ArrayList<>();
        Collections.addAll(finalSystemSquare, ss2, ss5, ss8, ss10);
        squares.addAll(finalSystemSquare);

        Player player2 = null;
        for (SystemSquare ss : finalSystemSquare) {
            player2 = new Player("Player2");
            player2.purchaseSquare(ss);
            assertNull(player2.getDevelopableSystems());
            for (SystemSquare s : squares) {
                player2.purchaseSquare(s);
                s.setMortgaged(true);
                //set the finalSystemSquare to mortgaged if it matches the square name in squares (ie system completed)
                ss.setMortgaged(finalSystemSquare.stream().noneMatch(e -> e.getSquareName().equals(s.getSquareName())));
                assertNull(player2.getDevelopableSystems());
            }
        }

        player2 = new Player("Player 2");
        for (SystemSquare s : squares) {
            player2.purchaseSquare(s);
            s.setDevelopment(4);
        }
        assertNull(player2.getDevelopableSystems());
    }

    @Test
    void testHasMortgageableSystemsTrue() throws BankruptcyException {
        player1.purchaseSquare(ss1);
        assertTrue(player1.hasMortgageableSquares());
    }

    @Test
    void testHasMortgageableSystemsFalse() throws BankruptcyException {
        player1.purchaseSquare(ss1);
        player1.developSquare(ss1, developmentValid);
        assertFalse(player1.hasMortgageableSquares());

        player1.purchaseSquare(ss2);
        ss2.setMortgaged(true);
        assertFalse(player1.hasMortgageableSquares());
    }

    @Test
    void testHasMortgagedSquaresTrue() throws BankruptcyException {
        player1.purchaseSquare(ss1);
        ss1.setMortgaged(true);
        assertTrue(player1.hasMortgagedSquares());
    }

    @Test
    void testHasMortgagedSquaresFalse() throws BankruptcyException {
        player1.purchaseSquare(ss1);
        assertFalse(player1.hasMortgagedSquares());
    }

    @Test
    void testHasDevelopmentsTrue() throws BankruptcyException {
        player1.purchaseSquare(ss1);
        player1.developSquare(ss1, 1);
        assertTrue(player1.hasDevelopments());
    }

    @Test
    void testHasDevelopmentsFalse() throws BankruptcyException {
        assertFalse(player1.hasDevelopments());

        player1.purchaseSquare(ss1);
        assertFalse(player1.hasDevelopments());
    }

    @Test
    void testGoingBankruptTrue() throws BankruptcyException {
        int res = player1.getPlayerResources();
        player1.addResources(-1 * res + bankruptTrue1);
        assertTrue(player1.goingBankrupt());

        res = player1.getPlayerResources();
        player1.addResources(-1 * res + bankruptTrue2);
        assertTrue(player1.goingBankrupt());
    }

    @Test
    void testGoingBankruptFalse() throws BankruptcyException {
        int res = player1.getPlayerResources();
        player1.addResources(-1 * res + bankruptFalse);
        assertFalse(player1.goingBankrupt());

    }
}