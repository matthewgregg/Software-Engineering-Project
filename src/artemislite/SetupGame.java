package artemislite;

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

	public List<Player> playerCreation(Scanner scanner) {
		List<Player> players = new ArrayList<>();

		try {
			String userOption = "";
			int numPlayers = 0;
			boolean valid = false;
			System.out.println("How many players? Enter a number between " + MIN_PLAYERS + " and " + MAX_PLAYERS + ".");
			do {
				try {
					/* don't mix nextLine and nextInt as nextInt doesn't read the following new line character
					which results ina empty string */
					userOption = scanner.nextLine();
					if (Integer.parseInt(userOption) >= MIN_PLAYERS && Integer.parseInt(userOption) <= MAX_PLAYERS) {
						valid = true;
						numPlayers = Integer.parseInt(userOption);
					} else {
						System.out.println("Please enter a number between " + MIN_PLAYERS + " and " + MAX_PLAYERS + ".");
					}
				} catch (NumberFormatException e) {
					System.out.println("Error! Try again.");
				}
			} while (!valid);

			System.out.println("Enter the names of the " + userOption + " players. Press return after entering a name.");
			int playerID = 1;
			while (players.size() < numPlayers) {
				String name = scanner.nextLine();
				//TODO mix up playerIDs?
				Player player = new Player(playerID, name);
				players.add(player);
				playerID++;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return players;
	}

	public void playerNameCheck() {

	}

	public void setupBoard() {
		Square sq1 = new Square("Start", 1, "Pass Go, Collect 200! or something ...");
		Square sq2 = new Square("System 1", 2, "System 1");
		Square sq3 = new Square("System 1", 3, "System 1");
		Square sq4 = new Square("System 1", 4, "System 1");
		Square sq12 = new Square("Empty Space", 12, "To go were no man has gone before ...");
		
		SystemSquare ss1 = new SystemSquare(SystemName.SYSTEM_NAME_1, 2, 300, 200, false);
		ss1.setSquareName("System 1");
		ss1.setPosition(2);
		ss1.setMessage("This is a message for this Sqaure");
		
		
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
