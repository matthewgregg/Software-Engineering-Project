package artemislite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.naming.InvalidNameException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Duration;
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
    SystemSquare ss1, ss2, ss3, ss4;
    SystemName systemName1, systemName2;
    int[] devCost;
    int baseCost, costPerDev, difficulty;
    int scanLowerLimit, scanUpperLimit, scanValidMid, scanInvalidLower, scanInvalidUpper, scanValidCancelResult;
    String scanValidCancel, name;
    SystemName systemName;
    String systemNameString;
    int systemNameEnumPos;

    // methods not tested - printLaunchStatusCheck, printWelcomeMessage, displayGameRules, displayBoardState, epilogue, clearScreen

    @BeforeEach
    void setUp() throws Exception {
        players = new ArrayList<>();
        player1 = new Player("Player 1");
        player2 = new Player("Player 2");
        players.add(player1);
        players.add(player2);
        name = "Player";

        systemName1 = SystemName.EXPLORATION_GROUND_SYSTEM;
        systemName2 = SystemName.ORION_SPACECRAFT;
        devCost = new int[]{0, 0, 0, 0, 0};
        baseCost = 50;
        costPerDev = 10;
        difficulty = 1;
        scanLowerLimit = 1;
        scanUpperLimit = 5;
        scanValidMid = 3;
        scanValidCancel = "#";
        scanValidCancelResult = -1;
        scanInvalidLower = 0;
        scanInvalidUpper = 6;
        systemName = SystemName.LUNAR_LANDER;
        systemNameString = "Lunar Lander";
        systemNameEnumPos = 3;

        ss1 = new SystemSquare("Square 1", 1, systemName1, difficulty, baseCost, costPerDev, devCost);
        ss2 = new SystemSquare("Square 2", 2, systemName1, difficulty, baseCost, costPerDev, devCost);
        ss3 = new SystemSquare("Square 3", 3, systemName2, difficulty, baseCost, costPerDev, devCost);
        ss4 = new SystemSquare("Square 4", 4, systemName2, difficulty, baseCost, costPerDev, devCost);
    }

    @Test
    void testGenerateOptionsMenu() throws BankruptcyException {
        String rules = "Rules";
        String board = "Display Board State";
        String dice = "Roll Dice";
        String purchaseEle = "Purchase Element";
        String purchaseDev = "Purchase Developments";
        String mortgage = "Sell Developments or Mortgage Element";
        String trade = "Trade with Player";
        String donate = "Donate to Player";
        String finish = "Finish Turn";
        String quit = "Quit Game";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        ByteArrayInputStream in = new ByteArrayInputStream(("4" + lineSeparator()).getBytes());
        System.setIn(in);
        Scanner scanner = new Scanner(in);
        Game.generateOptionsMenu(scanner, player1, players);
        assertTrue(out.toString().contains("1. " + rules));
        assertTrue(out.toString().contains("2. " + board));
        assertTrue(out.toString().contains("3. " + dice));
        assertTrue(out.toString().contains("4. " + quit));
        assertFalse(out.toString().contains(purchaseEle));
        assertFalse(out.toString().contains(purchaseDev));
        assertFalse(out.toString().contains(mortgage));
        assertFalse(out.toString().contains(trade));
        assertFalse(out.toString().contains(donate));
        assertFalse(out.toString().contains(finish));
    }

    @Test
    void testScanIntInput() {
        ByteArrayInputStream in = new ByteArrayInputStream((scanLowerLimit + lineSeparator()).getBytes());
        System.setIn(in);
        Scanner scanner = new Scanner(in);
        int res = Game.scanIntInput(scanner, scanLowerLimit, scanUpperLimit, false);
        assertEquals(scanLowerLimit, res);

        in = new ByteArrayInputStream((scanUpperLimit + lineSeparator()).getBytes());
        System.setIn(in);
        scanner = new Scanner(in);
        res = Game.scanIntInput(scanner, scanLowerLimit, scanUpperLimit, false);
        assertEquals(scanUpperLimit, res);

        in = new ByteArrayInputStream((scanValidCancel + lineSeparator()).getBytes());
        System.setIn(in);
        scanner = new Scanner(in);
        res = Game.scanIntInput(scanner, scanLowerLimit, scanUpperLimit, true);
        assertEquals(scanValidCancelResult, res);
    }

    @Test
    void testScanIntInputInvalid() {
        ByteArrayInputStream in = new ByteArrayInputStream((scanValidCancel + lineSeparator() + scanLowerLimit + lineSeparator()).getBytes());
        System.setIn(in);
        Scanner scanner = new Scanner(in);
        int res = Game.scanIntInput(scanner, scanLowerLimit, scanUpperLimit, true);
        assertEquals(scanValidCancelResult, res);

        in = new ByteArrayInputStream((scanInvalidLower + lineSeparator() + scanLowerLimit + lineSeparator()).getBytes());
        System.setIn(in);
        scanner = new Scanner(in);
        res = Game.scanIntInput(scanner, scanLowerLimit, scanUpperLimit, false);
        assertEquals(scanLowerLimit, res);

        in = new ByteArrayInputStream((scanInvalidUpper + lineSeparator() + scanLowerLimit + lineSeparator()).getBytes());
        System.setIn(in);
        scanner = new Scanner(in);
        res = Game.scanIntInput(scanner, scanLowerLimit, scanUpperLimit, false);
        assertEquals(scanLowerLimit, res);
    }

    @Test
    void testIntroduction() {
        assertNotNull(Game.introduction());
    }

    @Test
    void testGenerateSquareStatus() {

    }

    @Test
    void testLoading() {
        assertTimeout(Duration.ofMillis(5050), () -> {
            Game.loading(5, false);
        });

        assertTimeout(Duration.ofMillis(5050), () -> {
            Game.loading(5, true);
        });
    }

    @Test
    void testRollDice() {
        ArrayList<Integer> rolls = new ArrayList<>();
        // as the loop value increases, the delta required will decrease as the results tend towards a discrete uniform dist.
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

    @Test
    void testPurchaseSquare() throws BankruptcyException {
        Scanner scanner = new Scanner(System.in);
        int res = player1.getPlayerResources();
        Game.purchaseSquare(scanner, ss1, player1);
        assertTrue(player1.getOwnedSquares().contains(ss1));
        assertEquals(res - ss1.getBaseCost(), player1.getPlayerResources());

        ByteArrayInputStream in = new ByteArrayInputStream(("1" + lineSeparator() + "1" + lineSeparator() + "1" + lineSeparator() + "1" + lineSeparator()).getBytes());
        System.setIn(in);
        scanner = new Scanner(in);
        Game.purchaseSquare(scanner, ss2, player1);
    }

    @Test
    void testIsAuctionable() throws BankruptcyException {
        int value = ss1.getBaseCost() + 1;
        player1.addResources(-1 * player1.getPlayerResources() + value);
        assertTrue(Game.isAuctionable(ss1, player2, players));
        player1.addResources(-1 * player1.getPlayerResources());
        assertFalse(Game.isAuctionable(ss1, player2, players));
    }

    @Test
    void testAuctionSquare() throws BankruptcyException, InvalidNameException {
        Player player3 = new Player("Player 3");
        players.add(player3);
        player2.addResources(-1 * player2.getPlayerResources() + 300);
        int bid1 = ss1.getBaseCost() + 1;
        int bid2 = bid1 + 300;
        ByteArrayInputStream in = new ByteArrayInputStream((bid1 + lineSeparator() + bid2 + lineSeparator() + "#" + lineSeparator()).getBytes());
        System.setIn(in);
        Scanner scanner = new Scanner(System.in);
        int res2 = player2.getPlayerResources();
        int res3 = player3.getPlayerResources();
        Game.auctionSquare(scanner, ss1, player1, players);
        assertEquals(res3 - bid2, player3.getPlayerResources());
        assertTrue(player3.getOwnedSquares().contains(ss1));
        assertEquals(res2, player2.getPlayerResources());
        assertFalse(player2.getOwnedSquares().contains(ss1));

    }

    @Test
    void testAuctionSquareCancel() throws BankruptcyException, InvalidNameException {
        Player player3 = new Player("Player 3");
        int res2 = player2.getPlayerResources();
        int res3 = player3.getPlayerResources();
        ByteArrayInputStream in = new ByteArrayInputStream(("#" + lineSeparator() + "#" + lineSeparator()).getBytes());
        System.setIn(in);
        Scanner scanner = new Scanner(System.in);
        Game.auctionSquare(scanner, ss1, player1, players);
        assertEquals(res3, player3.getPlayerResources());
        assertFalse(player3.getOwnedSquares().contains(ss1));
        assertEquals(res2, player2.getPlayerResources());
        assertFalse(player2.getOwnedSquares().contains(ss1));
    }

    @Test
    void testBuyDevelopments() throws BankruptcyException {
        player1.purchaseSquare(ss1);
        player1.purchaseSquare(ss2);
        int res = player1.getPlayerResources();

        ByteArrayInputStream in = new ByteArrayInputStream(("1" + lineSeparator() + "1" + lineSeparator()).getBytes());
        System.setIn(in);
        Scanner scanner = new Scanner(in);
        Game.buyDevelopments(scanner, player1);
        assertEquals(res - ss1.getCostPerDevelopment(), player1.getPlayerResources());
    }

    @Test
    void testBuyDevelopmentsCancel() {
        int res = player1.getPlayerResources();
        ByteArrayInputStream in = new ByteArrayInputStream(("#" + lineSeparator()).getBytes());
        System.setIn(in);
        Scanner scanner = new Scanner(in);
        Game.buyDevelopments(scanner, player1);
        assertEquals(res, player1.getPlayerResources());

        in = new ByteArrayInputStream(("1" + lineSeparator() + "#" + lineSeparator()).getBytes());
        System.setIn(in);
        scanner = new Scanner(in);
        Game.buyDevelopments(scanner, player1);
        assertEquals(res, player1.getPlayerResources());
    }

    @Test
    void testBankMenu() throws BankruptcyException {
        player1.purchaseSquare(ss1);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        ByteArrayInputStream in = new ByteArrayInputStream(("1" + lineSeparator() + "#" + lineSeparator()).getBytes());
        System.setIn(in);
        Scanner scanner = new Scanner(in);
        Game.bankMenu(scanner, player1);
        assertTrue(out.toString().contains("1. Mortgage Element"));
        assertFalse(out.toString().contains("1. Sell Developments"));
        assertFalse(out.toString().contains("1. Pay Off Mortgage"));


        player1.developSquare(ss1, 1);
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        in = new ByteArrayInputStream(("1" + lineSeparator() + "#" + lineSeparator()).getBytes());
        System.setIn(in);
        scanner = new Scanner(in);
        Game.bankMenu(scanner, player1);
        assertTrue(out.toString().contains("1. Sell Developments"));
        assertFalse(out.toString().contains("1. Mortgage Element"));
        assertFalse(out.toString().contains("1. Pay Off Mortgage"));

        player2.purchaseSquare(ss2);
        ss2.setMortgaged(true);
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        in = new ByteArrayInputStream(("1" + lineSeparator() + "#" + lineSeparator()).getBytes());
        System.setIn(in);
        scanner = new Scanner(in);
        Game.bankMenu(scanner, player2);
        assertTrue(out.toString().contains("1. Pay Off Mortgage"));
        assertFalse(out.toString().contains("1. Sell Developments"));
        assertFalse(out.toString().contains("1. Mortgage Element"));
    }

    @Test
    void testBankMenuCancel() {
        //check if system is waiting for input with timeout (if cancel fails)
        assertTimeout(Duration.ofMillis(5000), () -> {
            player1.purchaseSquare(ss1);
            ByteArrayInputStream in = new ByteArrayInputStream(("#" + lineSeparator()).getBytes());
            System.setIn(in);
            Scanner scanner = new Scanner(in);
            Game.bankMenu(scanner, player1);
        });
    }

    @Test
    void testSellDevelopments() throws BankruptcyException {
        player1.purchaseSquare(ss1);
        player1.purchaseSquare(ss2);
        player1.developSquare(ss2, 1);
        int res = player1.getPlayerResources();

        ByteArrayInputStream in = new ByteArrayInputStream(("1" + lineSeparator() + "1" + lineSeparator()).getBytes());
        System.setIn(in);
        Scanner scanner = new Scanner(in);
        Game.sellDevelopments(scanner, player1);
        assertEquals(res + (int) (0.5 * ss1.getCostPerDevelopment()), player1.getPlayerResources());
        assertEquals(0, ss1.getDevelopment());
    }

    @Test
    void testSellDevelopmentsCancel() throws BankruptcyException {
        int res = player1.getPlayerResources();
        ByteArrayInputStream in = new ByteArrayInputStream(("#" + lineSeparator()).getBytes());
        System.setIn(in);
        Scanner scanner = new Scanner(in);
        Game.sellDevelopments(scanner, player1);
        assertEquals(res, player1.getPlayerResources());
    }

    @Test
    void testMortgageSquare() throws BankruptcyException {
        player1.purchaseSquare(ss1);
        player1.purchaseSquare(ss2);
        player1.developSquare(ss2, 1);
        int res = player1.getPlayerResources();

        ByteArrayInputStream in = new ByteArrayInputStream(("1" + lineSeparator()).getBytes());
        System.setIn(in);
        Scanner scanner = new Scanner(in);
        Game.mortgageSquare(scanner, player1);
        assertEquals(res+ss1.getBaseCost(), player1.getPlayerResources());
    }

    @Test
    void testMortgageSquareCancel() throws BankruptcyException {
        int res = player1.getPlayerResources();
        ByteArrayInputStream in = new ByteArrayInputStream(("#" + lineSeparator()).getBytes());
        System.setIn(in);
        Scanner scanner = new Scanner(in);
        Game.mortgageSquare(scanner, player1);
        assertEquals(res, player1.getPlayerResources());
    }

    @Test
    void testPayOffMortgage() throws BankruptcyException {
        player1.purchaseSquare(ss1);
        ss1.setMortgaged(true);
        int res = player1.getPlayerResources();

        ByteArrayInputStream in = new ByteArrayInputStream(("1" + lineSeparator()).getBytes());
        System.setIn(in);
        Scanner scanner = new Scanner(in);
        Game.payOffMortgage(scanner, player1);
        assertEquals(res - (int)(ss1.getBaseCost() * 1.1), player1.getPlayerResources());
    }

    @Test
    void testPayOffMortgageCancel() throws BankruptcyException {
        ByteArrayInputStream in = new ByteArrayInputStream(("#" + lineSeparator()).getBytes());
        System.setIn(in);
        Scanner scanner = new Scanner(in);
        int res = player1.getPlayerResources();
        Game.payOffMortgage(scanner, player1);
        assertEquals(res, player1.getPlayerResources());
    }

    @Test
    void testTradeWithPlayer() throws BankruptcyException {
        int cost = 100;

        ByteArrayInputStream in = new ByteArrayInputStream(("1" + lineSeparator() + "2" + lineSeparator() + "1" + lineSeparator() + cost + lineSeparator()).getBytes());
        System.setIn(in);
        Scanner scanner = new Scanner(in);

        player1.purchaseSquare(ss1);
        player1.purchaseSquare(ss2);
        int res = player1.getPlayerResources();
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
    void testTradeWithPlayerCancel() throws BankruptcyException {
        player1.purchaseSquare(ss1);
        player1.purchaseSquare(ss2);
        player2.purchaseSquare(ss3);
        player2.purchaseSquare(ss4);

        ByteArrayInputStream in = new ByteArrayInputStream(("#" + lineSeparator()).getBytes());
        System.setIn(in);
        Scanner scanner = new Scanner(in);
        int res = player1.getPlayerResources();
        Game.tradeWithPlayer(scanner, player1, players);
        assertEquals(res, player1.getPlayerResources());

        in = new ByteArrayInputStream(("1" + lineSeparator() + "#" + lineSeparator()).getBytes());
        System.setIn(in);
        scanner = new Scanner(in);
        res = player1.getPlayerResources();
        Game.tradeWithPlayer(scanner, player1, players);
        assertEquals(res, player1.getPlayerResources());

        in = new ByteArrayInputStream(("1" + lineSeparator() + "2" + lineSeparator() + "#" + lineSeparator()).getBytes());
        System.setIn(in);
        scanner = new Scanner(in);
        res = player1.getPlayerResources();
        Game.tradeWithPlayer(scanner, player1, players);
        assertEquals(res, player1.getPlayerResources());

        in = new ByteArrayInputStream(("1" + lineSeparator() + "2" + lineSeparator() + "1" + lineSeparator() + "#").getBytes());
        System.setIn(in);
        scanner = new Scanner(in);
        res = player1.getPlayerResources();
        Game.tradeWithPlayer(scanner, player1, players);
        assertEquals(res, player1.getPlayerResources());

        in = new ByteArrayInputStream(("1" + lineSeparator() + "2" + lineSeparator() + "1" + lineSeparator() + "1" + lineSeparator() + "#" + lineSeparator()).getBytes());
        System.setIn(in);
        scanner = new Scanner(in);
        res = player1.getPlayerResources();
        Game.tradeWithPlayer(scanner, player1, players);
        assertEquals(res, player1.getPlayerResources());

        in = new ByteArrayInputStream(("1" + lineSeparator() + "2" + lineSeparator() + "2" + lineSeparator() + "#" + lineSeparator()).getBytes());
        System.setIn(in);
        scanner = new Scanner(in);
        res = player1.getPlayerResources();
        Game.tradeWithPlayer(scanner, player1, players);
        assertEquals(res, player1.getPlayerResources());

        in = new ByteArrayInputStream(("1" + lineSeparator() + "2" + lineSeparator() + "2" + lineSeparator() + "1" + lineSeparator() + "#" + lineSeparator()).getBytes());
        System.setIn(in);
        scanner = new Scanner(in);
        res = player1.getPlayerResources();
        Game.tradeWithPlayer(scanner, player1, players);
        assertEquals(res, player1.getPlayerResources());

        in = new ByteArrayInputStream(("1" + lineSeparator() + "2" + lineSeparator() + "2" + lineSeparator() + "1" + lineSeparator() + "1" + lineSeparator() + "#" + lineSeparator()).getBytes());
        System.setIn(in);
        scanner = new Scanner(in);
        res = player1.getPlayerResources();
        Game.tradeWithPlayer(scanner, player1, players);
        assertEquals(res, player1.getPlayerResources());
    }

    @Test
    void testGetPlayersNearBankruptcy() throws BankruptcyException {
        int playerToDonateToAmount = 90;
        player2.addResources(-1 * player2.getPlayerResources() + playerToDonateToAmount);
        assertTrue(Game.getPlayersNearBankruptcy(player1, players).contains(player2));
        assertFalse(Game.getPlayersNearBankruptcy(player1, players).contains(player1));
    }

    @Test
    void testMakeDonation() throws BankruptcyException, InvalidNameException {
        int donation = 100;
        int playerToDonateToAmount = 90;
        ByteArrayInputStream in = new ByteArrayInputStream((donation + lineSeparator()).getBytes());
        System.setIn(in);
        Scanner scanner = new Scanner(in);
        int res1 = player1.getPlayerResources();
        player2.addResources(-1 * player2.getPlayerResources() + playerToDonateToAmount);

        Game.makeDonation(scanner, player1, players);
        assertEquals(res1-donation, player1.getPlayerResources());
        assertEquals(playerToDonateToAmount+donation, player2.getPlayerResources());

        Player player3 = new Player("Player 3");
        player2.addResources(-1 * player2.getPlayerResources() + playerToDonateToAmount);
        player3.addResources(-1 * player3.getPlayerResources() + playerToDonateToAmount);
        players.add(player3);

        in = new ByteArrayInputStream(("2" + lineSeparator() + donation + lineSeparator()).getBytes());
        System.setIn(in);
        scanner = new Scanner(in);
        Game.makeDonation(scanner, player1, players);
        assertEquals(res1-2*donation, player1.getPlayerResources());
        assertEquals(playerToDonateToAmount+donation, player3.getPlayerResources());
    }

    @Test
    void testMakeDonationCancel() throws BankruptcyException {
        int res1 = player1.getPlayerResources();
        ByteArrayInputStream in = new ByteArrayInputStream(("#" + lineSeparator()).getBytes());
        System.setIn(in);
        Scanner scanner = new Scanner(in);
        Game.makeDonation(scanner, player1, players);
        assertEquals(res1, player1.getPlayerResources());
    }

    @Test
    void testCalculateNetWorth() throws InvalidNameException, BankruptcyException {
        Player playerNetWorthTest = new Player(name);
        int res = playerNetWorthTest.getPlayerResources();
        playerNetWorthTest.purchaseSquare(ss1);
        playerNetWorthTest.developSquare(ss1, 4);
        assertEquals(res, Game.calculateNetWorth(playerNetWorthTest));
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
    void testStringifyEnum() {
        assertEquals(systemNameString,  Game.stringifyEnum(systemName));
        ArrayList<String> sysNamesString = Game.stringifyEnum(SystemName.class);
        assertEquals(systemNameString, sysNamesString.get(systemNameEnumPos));
    }
}