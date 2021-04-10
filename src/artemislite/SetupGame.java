package artemislite;

import javax.naming.InvalidNameException;
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
				/*
				 * don't mix nextLine and nextInt as nextInt doesn't read the following new line
				 * character which results ina empty string
				 */
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
					// check previous names
					for (Player player : players) {
						if (player.getName().equalsIgnoreCase(name)) {
							uniqueName = false;
							break;
						}
					}
					if (uniqueName) {
						// TODO (random) id should be generated by player class
						Player player = new Player(playerID, name);
						playerID++;
						players.add(player);
						validPlayerName = true;
					} else {
						throw new InvalidNameException();
					}
				} catch (InvalidNameException e) {
					System.out.printf("%s is not a valid name. Try again.\n", name);
				}
			} while (!validPlayerName);
		}
		return players;
	}

	public static ArrayList<Square> setupBoard() {
		ArrayList<Square> squares = new ArrayList<>();
		Square sq1 = new Square("Mission Control", 0, "Pass Go, Collect 200! or something ...");

		SystemSquare ss1 = new SystemSquare("Moon Gateway Station",
				1,
				"System 1",
				SystemName.EXPLORATION_GROUND_SYSTEM,
				2,
				80,
				200,
				new int[] { 10, 20, 30, 90, 150 });

		SystemSquare ss2 = new SystemSquare("Lunar Payload Services",
				2,
				"System 1",
				SystemName.EXPLORATION_GROUND_SYSTEM,
				2,
				100,
				200,
				new int[] { 10, 20, 30, 90, 150 });

		SystemSquare ss3 = new SystemSquare("Onward to Lunar Surface",
				3,
				"System 1",
				SystemName.ORION_SPACECRAFT,
				2,
				120,
				200,
				new int[] { 10, 20, 30, 90, 150 });

		SystemSquare ss4 = new SystemSquare("Navigation Control",
				4,
				"System 1",
				SystemName.ORION_SPACECRAFT,
				2,
				120,
				200,
				new int[] { 10, 20, 30, 90, 150 });

		SystemSquare ss5 = new SystemSquare("Abort Launch",
				5,
				"System 1",
				SystemName.ORION_SPACECRAFT,
				2,
				140,
				200,
				new int[] { 10, 20, 30, 90, 150 });

		Square sq2 = new Square("Nothing", 6, "Missed launch window");

		SystemSquare ss6 = new SystemSquare("Core Stage Depleted",
				7,
				"System 1",
				SystemName.GATEWAY_OUTPOST,
				2,
				160,
				200,
				new int[] { 10, 20, 30, 90, 150 });

		SystemSquare ss7 = new SystemSquare("Spun Stage",
				8,
				"System 1",
				SystemName.GATEWAY_OUTPOST,
				2,
				160,
				200,
				new int[] { 10, 20, 30, 90, 150 });

		SystemSquare ss8 = new SystemSquare("SLS Booster Testing",
				9,
				"System 1",
				SystemName.GATEWAY_OUTPOST,
				2,
				180,
				200,
				new int[] { 10, 20, 30, 90, 150 });

		SystemSquare ss9 = new SystemSquare("Astronaut Training",
				10,
				"System 1",
				SystemName.LUNAR_LANDER,
				2,
				200,
				200,
				new int[] { 10, 20, 30, 90, 150 });

		SystemSquare ss10 = new SystemSquare("Launch Facilities Upgrades",
				11,
				"System 1",
				SystemName.LUNAR_LANDER,
				2,
				220,
				200,
				new int[] { 10, 20, 30, 90, 150 });
		
	

		Collections.addAll(squares, sq1, ss1, ss2, ss3, ss4, ss5, sq2, ss6, ss7, ss8, ss9, ss10);
		squares.sort(new ComparePosition());
		return squares;
	}
}
