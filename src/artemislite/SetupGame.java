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

	public List<Player> playerCreation() {
		List<Player> players = new ArrayList<>();

		try (Scanner scanner = new Scanner(System.in)) {
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
