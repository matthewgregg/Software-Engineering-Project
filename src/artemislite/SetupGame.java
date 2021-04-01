/**
 * 
 */
package artemislite;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import com.sun.tools.javac.comp.Todo;

/**
 * @author mark
 *
 */
public class SetupGame {

	private static final int MIN_PLAYERS = 2;
	private static final int MAX_PLAYERS = 4;
	private static final int NUMBER_OF_SQUARES = 12;

	protected List<Player> players = new ArrayList<Player>();
	private ArrayList<Square> squares;

	public void playerCreation() {

		// Todo - Extra player getting added to the Array?!?! :-(
		// Todo - If you type a String it throws an exception - 
		//        the try catch continues the game without entering names
		
		Scanner scanner = new Scanner(System.in);
		Random rand = new Random();
		boolean playerSelect = true;

		System.out.println("How many players between 2-4 players");
		try {

			while (playerSelect) {

				int userOption = scanner.nextInt();

				if (userOption == 2) {
					System.out.println("Enter the name's of the 2 Player's");

					while (players.size() <= MIN_PLAYERS) {

						String name = scanner.nextLine();
						int playerID = name.length() + rand.nextInt(24) + 1;
						Player player = new Player(playerID, name);

						// System.out.println(player.getName());

						addPlayer(player);

					}
					break;
				} else if (userOption == 3) {
					System.out.println("Enter the name's of the 3 Player's");

					while (players.size() <= MAX_PLAYERS - 1) {

						String name = scanner.nextLine();
						int playerID = rand.nextInt(24) + 1;
						Player player = new Player(playerID, name);

						// System.out.println(player.getName());

						addPlayer(player);

					}
					break;
				} else if (userOption == 4) {
					System.out.println("Enter the name's of the 4 Player's");

					while (players.size() <= MAX_PLAYERS) {

						String name = scanner.nextLine();
						int playerID = rand.nextInt(24) + 1;
						Player player = new Player(playerID, name);

						// System.out.println(player.getName());

						addPlayer(player);

					}
					break;
				} else {
					System.out.println("Please select between 2-4 Players");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		// scanner.close();

	}

	public void addPlayer(Player player) {
		players.add(player);
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
