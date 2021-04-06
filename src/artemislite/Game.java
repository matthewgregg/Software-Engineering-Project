package artemislite;

import java.util.*;
import java.util.List;

public class Game {

	private static final Random rand = new Random();
	private static List<Player> players;
	private static final Scanner scanner = new Scanner(System.in);
	// scanner cannot be closed and then reused

	SetupGame gameSetup = new SetupGame();

	public static void main(String[] args) {
		clearScreen();
		boolean isGameOver = false;
		boolean quitGame;

		players = new ArrayList<>(SetupGame.playerCreation(scanner));

		// squares will be moved to a player's array when purchased and replaced with
		// null in this array
		ArrayList<Square> squares = new ArrayList<>(SetupGame.setupBoard());

		clearScreen();

		System.out.println(welcomeMessage(players));
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			System.out.println("Thread error");
		}
		int playerCount = 0;
		do {
			playerCount++;
			Player player = players.get(playerCount - 1);
			if (playerCount > players.size()) {
				playerCount = 1;
			}
			quitGame = !generateOptionsMenu(scanner, player, squares);
		} while (!isGameOver && !quitGame);

		if (quitGame) {
			clearScreen();
			System.out.printf("Game is over! %s quit the game\n", players.get(playerCount - 1).getName());
		}
	}

	public static boolean generateOptionsMenu(Scanner scanner, Player player, ArrayList<Square> unownedSquares) {
		int userOption = 0;
		boolean turnFinished = false;
		boolean rolled = false;
		int menuNum;
		HashMap<Integer, Integer> menuOptions = new HashMap<>();
		String[] allMenu = new String[7];
		allMenu[0] = "Display Board State";
		allMenu[1] = "Roll Dice";
		allMenu[2] = "Purchase Square";
		allMenu[3] = "Offer Element";
		allMenu[4] = "Develop Element";
		allMenu[5] = "Finish Turn";
		allMenu[6] = "Quit Game";

		do {
			menuNum = 0;
			clearScreen();
			System.out.printf("%s's turn [%d units]\n", player.getName(), player.getPlayerResources());
			Square landed = unownedSquares.get(player.getPosition());

			boolean onSysSq = true;
			if (player.getPosition() == 0 || player.getPosition() == 6) {
				onSysSq = false;
			}
			if (landed == null) {
				Pair<Player, SystemSquare> ownedSquare = getSquareAndOwner(player.getPosition());
				String squareName = ownedSquare.getSecond().getSquareName();
				if (ownedSquare.getFirst().equals(player)) {
					System.out.printf("You are on %s. You own it.", squareName);
				} else {
					System.out.printf("You are on %s. It is owned by %s.\n", squareName,
							ownedSquare.getFirst().getName());
				}
			} else if (!onSysSq) {
				System.out.printf("You are on %s. It can't be owned.\n", landed.getSquareName());
			} else {
				System.out.printf("You are on %s. It is not owned.\n", landed.getSquareName());
			}

			System.out.println("Menu");

			for (int i = 0; i < allMenu.length; i++) {
				if (i == 1 && rolled) {
					continue;
				}
				if (i > 1 && i < 6 && !rolled) {
					continue;
				}
				if (i > 1 && i < 4 && (landed == null || !onSysSq)) {
					continue;
				}
				menuNum++;
				System.out.println(menuNum + ". " + allMenu[i]);
				menuOptions.put(menuNum, i + 1);
			}
			System.out.println("Enter option");

			boolean valid = false;

			do {
				String option;
				try {
					option = scanner.nextLine();
					if (Integer.parseInt(option) >= 1 && Integer.parseInt(option) <= menuNum) {
						valid = true;
						userOption = Integer.parseInt(option);
					} else {
						throw new NumberFormatException();
					}
				} catch (NumberFormatException e) {
					System.out.printf("Please enter a number between 1 and %d.\n", menuNum);
				}
			} while (!valid);

			clearScreen();

			switch (menuOptions.get(userOption)) {
			case 1:
				displayBoardState();
				loading();
				break;
			case 2:
				rolled = true;
				int[] roll = rollDiceToMove(player);
				System.out.printf("You rolled a %d and a %d.\nMoving %d spaces", roll[0], roll[1], roll[0] + roll[1]);
				System.out.printf("You are now sitting at Square %s, what would you like to do?", player.getPosition());
				loading();
				break;
			case 3:
				Square square = unownedSquares.get(player.getPosition());
				if (square instanceof SystemSquare) {
					SystemSquare ss = (SystemSquare) square;
					ss.purchaseSquare(player);
					System.out.print("Purchasing " + ss.getSquareName());
				} else {
					System.out.println("That square can't be purchased.");
				}
				loading();
				break;
			case 4:
				System.out.print("Auction");
				// this should happen automatically if player can't buy development
				loading();
				break;
			case 5:
				System.out.print("Develop");
				loading();
				break;
			case 6:
				turnFinished = true;
				break;
			case 7:
				break;
			}
		} while (!turnFinished && userOption != menuNum);
		return turnFinished;
	}

	/**
	 * Roll dice to see who goes first, second, third etc
	 * 
	 * @return
	 */
	public static int[] rollDiceForPlayerOrder() {
		int[] roll = new int[2];
		roll[0] = rand.nextInt(6) + 1;
		roll[1] = rand.nextInt(6) + 1;

		return roll;
	}

	/**
	 * Roll dice to enable players to move on board
	 * 
	 * @return
	 */
	public static int[] rollDiceToMove(Player player) {
		int[] roll = new int[2];
		roll[0] = rand.nextInt(6) + 1;
		roll[1] = rand.nextInt(6) + 1;

		traverseBoard(roll, player);
		return roll;
	}

	/**
	 * moves players' position on board based on result of dice roll
	 */
	public static void traverseBoard(int[] rolledNumber, Player player) {
		
		switch (rolledNumber[0]+rolledNumber[1]) {
		case 0:
			player.setPosition(0);
			break;
		case 1:
			player.setPosition(1);
			break;
		case 2:
			player.setPosition(2);
			break;
		case 3:
			player.setPosition(3);
			break;
		case 4:
			player.setPosition(4);
			break;
		case 5:
			player.setPosition(5);
			break;
		case 6:
			player.setPosition(6);
			break;
		case 7:
			player.setPosition(7);
			break;
		case 8:
			player.setPosition(8);
			break;
		case 9:
			player.setPosition(9);
			break;
		case 10:
			player.setPosition(10);
			break;
		case 11:
			player.setPosition(11);
			break;
		case 12:
			player.setPosition(12);
			break;
		}
		
	}

	public static void loading() {
		try {
			for (int i = 0; i <= 3; i++) {
				Thread.sleep(1000);
				System.out.print(".");
			}
		} catch (InterruptedException e) {
			System.out.println("Thread error");
		}
		System.out.println();
	}

	public static StringBuilder welcomeMessage(List<Player> players) {
		StringBuilder welcome = new StringBuilder();
		for (int i = players.size(); i > 0; i--) {
			welcome.insert(0, players.get(i - 1).getName());
			if (i == players.size()) {
				welcome.insert(0, " and ");
			} else if (i > 1) {
				welcome.insert(0, ", ");
			}
		}
		welcome.append(".");
		welcome.insert(0, "Welcome to ArtemisLite, ");
		return welcome;
	}

	public static void displayBoardState() {
		for (Player player : players) {
			System.out.print(player.getName() + " ");
			if (player.getOwnedElements().size() == 0) {
				System.out.print("[No owned elements]");
			} else {
				System.out.print("[");
				for (int i = 0; i < player.getOwnedElements().size(); i++) {
					System.out.print(player.getOwnedElements().get(i).getSquareName());
					if (i > 1) {
						System.out.print(", ");
					}
				}
				System.out.print("]");
			}
			System.out.println();
		}
	}

	static Pair<Player, SystemSquare> getSquareAndOwner(int position) {
		SystemSquare squareMatch = null;
		Player playerMatch = null;
		for (Player player : players) {
			for (SystemSquare square : player.getOwnedElements()) {
				if (square.getPosition() == position) {
					squareMatch = square;
					playerMatch = player;
					break;
				}
			}
		}
		return new Pair<Player, SystemSquare>(playerMatch, squareMatch);
	}

	public static void clearScreen() {
		try {
			final String os = System.getProperty("os.name");

			if (os.contains("Windows")) {
				new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
			} else {
				System.out.print("\033[H\033[2J");
				System.out.flush();
			}
		} catch (final Exception e) {
			System.out.println("Error clearing console.");
		}
	}
}
