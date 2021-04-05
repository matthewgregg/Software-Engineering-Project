package artemislite;

import javax.naming.ConfigurationException;
import java.util.*;

/**
 * @author mark
 *
 */
public class SetupGame {

	private static final int MIN_PLAYERS = 2;
	private static final int MAX_PLAYERS = 4;
	private static final int NUMBER_OF_SQUARES = 12;
	private ArrayList<Square> squares;

	public static List<Player> playerCreation(Scanner scanner) {
		ArrayList<Player> players = new ArrayList<>();
		String userOption = "";
		int numPlayers = 0;
		boolean validPlayerNum = false;

		System.out.println("How many players? Enter a number between " + MIN_PLAYERS + " and " + MAX_PLAYERS + ".");

		do {
			try {
				/* don't mix nextLine and nextInt as nextInt doesn't read the following new line character
				which results ina empty string */
				userOption = scanner.nextLine();
				if (Integer.parseInt(userOption) >= MIN_PLAYERS && Integer.parseInt(userOption) <= MAX_PLAYERS) {
					validPlayerNum = true;
					numPlayers = Integer.parseInt(userOption);
				} else {
					System.out.println("Please enter a number between " + MIN_PLAYERS + " and " + MAX_PLAYERS + ".");
				}
			} catch (NumberFormatException e) {
				System.out.println("Error! Try again.");
			}
		} while (!validPlayerNum);

		System.out.println("Enter the names of the " + userOption + " players. Press return after entering a name.");

		int playerID = 0;
		boolean validPlayerName = false;
		while (players.size() < numPlayers) {
			do {
				String name = "";
				boolean uniqueName = true;
				try {
					name = scanner.nextLine();
					//check previous names
					for (Player player : players) {
						if (player.getName().equalsIgnoreCase(name)) {
							uniqueName = false;
							break;
						}
					}
					if (uniqueName) {
						//TODO should (random) id be generated by player class?
						Player player = new Player(playerID, name);
						playerID++;
						players.add(player);
						validPlayerName = true;
					} else {
						throw new ConfigurationException();
					}
				} catch (ConfigurationException e) {
					System.out.printf("%s is not a valid name. Try again.\n", name);
				}
			} while (!validPlayerName);
		}
		// Added the playerNameCheck before adding the new player to the array!
		// we could probably incorporate new Player Constructor into it like this
		// playerNameCheck(new Player(playerID, name)), but I haven't investigated that approach!
		return players;
	}
	/*
	// Just have a play with handling the name check needs work!
	// I only spent 15 minutes to break up my revision - if anyone wants to progress it feel free!
	public void playerNameCheck(String name, int playerID) {
		//

		for(Player player: players) {
			if(player.getName().equalsIgnoreCase(name) || player.getPlayerID() == playerID) {
				System.out.println("Do something with this Duplicate " + name);
				System.out.println("Do something with this Duplicate " + playerID);
			}
		}

	}
	 */

	public void setupBoard() {
		Square sq1 = new Square("Start", 1, "Pass Go, Collect 200! or something ...");
		Square sq2 = new Square("System 1", 2, "System 1");
		Square sq3 = new Square("System 1", 3, "System 1");
		Square sq4 = new Square("System 1", 4, "System 1");
		Square sq12 = new Square("Empty Space", 12, "To go were no man has gone before ...");
		
		SystemSquare ss1 = new SystemSquare(SystemName.SYSTEM_NAME_1, 2, 300, 200, false);
		ss1.setSquareName("System 1");
		ss1.setPosition(2);
		ss1.setMessage("This is a message for this Square");
		
		
	}

	public void displayGameRules() {

	}

	public ArrayList<Square> getSquares() {
		return squares;
	}

	public void setSquares(ArrayList<Square> squares) {
		this.squares = squares;
	}
}
