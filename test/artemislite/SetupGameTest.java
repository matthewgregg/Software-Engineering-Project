package artemislite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static java.lang.System.lineSeparator;

class SetupGameTest {

    int numPlayersValidLower, numPlayersValidUpper, numPlayersInvalidLower, numPlayersInvalidUpper;
    String playerNameValid1, playerNameValid2, playerNameValid3, playerNameValid4, playerNameInvalidDuplicate, playerNameInvalidBlank ,playerNameInvalidNull;
    int NUM_SQUARES, NUM_SYSTEM_SQUARES;
    int playerRoleValidLower, playerRoleValidUpper, playerRoleValidMidLower, playerRoleValidMidUpper, playerRoleInvalidLower, playerRoleInvalidUpper;
    String playerRoleValidLowerOut, playerRoleValidUpperOut, playerRoleValidMidLowerOut, playerRoleValidMidUpperOut;
    String INVALID_PLAYER_ID;

    @BeforeEach
    void setUp() {
        numPlayersValidLower = 2;
        numPlayersValidUpper = 4;
        numPlayersInvalidLower = 1;
        numPlayersInvalidUpper = 5;
        playerNameValid1 = "Player1";
        playerNameValid2 = "Player2";
        playerNameValid3 = "Player3";
        playerNameValid4 = "Player4";
        playerNameInvalidDuplicate = "Player1";
        playerNameInvalidBlank = "";
        NUM_SQUARES = 12;
        NUM_SYSTEM_SQUARES = 10;
        playerRoleValidLower = 1;
        playerRoleValidUpper = 4;
        playerRoleValidMidLower = 2;
        playerRoleValidMidUpper = 3;
        playerRoleInvalidLower = 0;
        playerRoleInvalidUpper = 5;
        playerRoleValidLowerOut = "Commander";
        playerRoleValidUpperOut = "Docking Module Pilot";
        playerRoleValidMidLowerOut = "Command Module Pilot";
        playerRoleValidMidUpperOut = "Lunar Module Pilot";
        INVALID_PLAYER_ID = "Invalid player ID";
    }

    @Test
    void testPlayerCreationValid() {
        ByteArrayInputStream in = new ByteArrayInputStream((numPlayersValidLower + lineSeparator() + playerNameValid1 + lineSeparator() + playerNameValid2 + lineSeparator()).getBytes());
        System.setIn(in);
        Scanner scanner = new Scanner(in);
        List<Player> players = SetupGame.playerCreation(scanner);
        assertEquals(playerNameValid1, players.get(0).getName());
        assertEquals(playerNameValid2, players.get(1).getName());

        in = new ByteArrayInputStream((numPlayersValidUpper + lineSeparator() + playerNameValid1 + lineSeparator() + playerNameValid2 + lineSeparator() + playerNameValid3 + lineSeparator() + playerNameValid4 + lineSeparator()).getBytes());
        System.setIn(in);
        scanner = new Scanner(in);
        players = SetupGame.playerCreation(scanner);
        assertEquals(playerNameValid1, players.get(0).getName());
        assertEquals(playerNameValid2, players.get(1).getName());
        assertEquals(playerNameValid3, players.get(2).getName());
        assertEquals(playerNameValid4, players.get(3).getName());
    }

    @Test
    void testPlayerCreationInvalid() {
        String validInput = numPlayersValidLower + lineSeparator() + playerNameValid1 + lineSeparator() + playerNameValid2 + lineSeparator();
        ByteArrayInputStream in = new ByteArrayInputStream((numPlayersInvalidLower + lineSeparator() + validInput).getBytes());
        System.setIn(in);
        Scanner scanner = new Scanner(in);
        List<Player> players = SetupGame.playerCreation(scanner);
        assertNotEquals(numPlayersInvalidLower, players.size());
        assertEquals(playerNameValid1, players.get(0).getName());
        assertEquals(playerNameValid2, players.get(1).getName());

        in = new ByteArrayInputStream((numPlayersInvalidUpper + lineSeparator() + validInput).getBytes());
        System.setIn(in);
        scanner = new Scanner(in);
        players = SetupGame.playerCreation(scanner);
        assertNotEquals(numPlayersInvalidUpper, players.size());
        assertEquals(playerNameValid1, players.get(0).getName());
        assertEquals(playerNameValid2, players.get(1).getName());

        in = new ByteArrayInputStream((numPlayersValidLower + lineSeparator() + playerNameValid1 + lineSeparator() + playerNameInvalidDuplicate + lineSeparator() + playerNameValid2 + lineSeparator()).getBytes());
        System.setIn(in);
        scanner = new Scanner(in);
        players = SetupGame.playerCreation(scanner);
        assertFalse(players.stream().allMatch(s -> s.getName().equals(playerNameInvalidDuplicate)));
        assertEquals(playerNameValid1, players.get(0).getName());
        assertEquals(playerNameValid2, players.get(1).getName());

        in = new ByteArrayInputStream((numPlayersValidLower + lineSeparator() + playerNameValid1 + lineSeparator() + playerNameInvalidBlank + lineSeparator() + playerNameValid2 + lineSeparator()).getBytes());
        System.setIn(in);
        scanner = new Scanner(in);
        players = SetupGame.playerCreation(scanner);
        assertFalse(players.stream().anyMatch(s -> s.getName().equals(playerNameInvalidBlank)));
        assertEquals(playerNameValid1, players.get(0).getName());
        assertEquals(playerNameValid2, players.get(1).getName());
    }

    @Test
    void testSetupBoard() {
        List<Square> squares = SetupGame.setupBoard();
        assertEquals(NUM_SQUARES, squares.size());
        assertEquals(NUM_SYSTEM_SQUARES, squares.stream().filter(s -> s instanceof SystemSquare).count());
    }

    @Test
    void testGetPlayerRoleValid() {
        assertEquals(playerRoleValidLowerOut, SetupGame.getPlayerRole(playerRoleValidLower));
        assertEquals(playerRoleValidUpperOut, SetupGame.getPlayerRole(playerRoleValidUpper));
        assertEquals(playerRoleValidMidLowerOut, SetupGame.getPlayerRole(playerRoleValidMidLower));
        assertEquals(playerRoleValidMidUpperOut, SetupGame.getPlayerRole(playerRoleValidMidUpper));
    }

    @Test
    void testGetPlayerRoleInvalid() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            SetupGame.getPlayerRole(playerRoleInvalidLower);
        });
        assertEquals(INVALID_PLAYER_ID, e.getMessage());

        e = assertThrows(IllegalArgumentException.class, () -> {
            SetupGame.getPlayerRole(playerRoleInvalidUpper);
        });
        assertEquals(INVALID_PLAYER_ID, e.getMessage());
    }
}