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

	public static List<Player> playerCreation(Scanner scanner) {
		System.out.println("How many crew members would like to play?\nEnter a number between " + MIN_PLAYERS + " and " + MAX_PLAYERS + ".");
		int numPlayers = Game.scanIntInput(scanner, MIN_PLAYERS, MAX_PLAYERS, false);

		Game.clearScreen();

		System.out.println("Enter the names of the " + numPlayers + " crew members.\nPress return after entering a name.");

		ArrayList<Player> players = new ArrayList<>();
		boolean validPlayerName = false;
		int count = 1;

		do {
			do {
				String name = null;
				try {
					System.out.printf("%d. ", count);
					name = scanner.nextLine();
					// add new player to players if the name is unique
					String finalName = name;
					if (players.stream().noneMatch(p -> p.getName().equals(finalName))) {
						players.add(new Player(name));
						validPlayerName = true;
						count++;
					} else {
						throw new InvalidNameException();
					}
				} catch (InvalidNameException e) {
					System.out.printf("%s is not a valid name. Try again.\n", name);
				}
			} while (!validPlayerName);
		} while (players.size() < numPlayers);
		return players;
	}

	public static ArrayList<Square> setupBoard() {
		ArrayList<Square> squares = new ArrayList<>();
		Square sq1 = new Square("Mission Control", 0, "Pass Go, Collect 200! or something ...");

		SystemSquare ss1 = new SystemSquare("Moon Gateway Station",
				1,
				SystemName.EXPLORATION_GROUND_SYSTEM,
				2,
				80,
				200,
				new int[] { 10, 20, 30, 90, 150 });

		SystemSquare ss2 = new SystemSquare("Lunar Payload Services",
				2,
				SystemName.EXPLORATION_GROUND_SYSTEM,
				2,
				100,
				200,
				new int[] { 10, 20, 30, 90, 150 });

		SystemSquare ss3 = new SystemSquare("Onward to Lunar Surface",
				3,
				SystemName.ORION_SPACECRAFT,
				2,
				120,
				200,
				new int[] { 10, 20, 30, 90, 150 });

		SystemSquare ss4 = new SystemSquare("Navigation Control",
				4,
				SystemName.ORION_SPACECRAFT,
				2,
				120,
				200,
				new int[] { 10, 20, 30, 90, 150 });

		SystemSquare ss5 = new SystemSquare("Abort Launch",
				5,
				SystemName.ORION_SPACECRAFT,
				2,
				140,
				200,
				new int[] { 10, 20, 30, 90, 150 });

		Square sq2 = new Square("Nothing", 6, "Missed launch window");

		SystemSquare ss6 = new SystemSquare("Core Stage Depleted",
				7,
				SystemName.GATEWAY_OUTPOST,
				2,
				160,
				200,
				new int[] { 10, 20, 30, 90, 150 });

		SystemSquare ss7 = new SystemSquare("Spun Stage",
				8,
				SystemName.GATEWAY_OUTPOST,
				2,
				160,
				200,
				new int[] { 10, 20, 30, 90, 150 });

		SystemSquare ss8 = new SystemSquare("SLS Booster Testing",
				9,
				SystemName.GATEWAY_OUTPOST,
				2,
				180,
				200,
				new int[] { 10, 20, 30, 90, 150 });

		SystemSquare ss9 = new SystemSquare("Astronaut Training",
				10,
				SystemName.LUNAR_LANDER,
				2,
				200,
				200,
				new int[] { 10, 20, 30, 90, 150 });

		SystemSquare ss10 = new SystemSquare("Launch Facilities Upgrades",
				11,
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
