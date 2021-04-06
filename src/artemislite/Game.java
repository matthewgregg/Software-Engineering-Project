package artemislite;

import java.util.*;
import java.util.List;

public class Game {
	private static final int GO_RESOURCES = 200;

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

	/**
	 * generate options for user
	 * @param scanner the scanner object
	 * @param player the current player
	 * @param unownedSquares the list of unowned squares
	 * @return a boolean for whether the user finished their turn or not. If false, the player quit the game. If true, the player won the game
	 */
	public static boolean generateOptionsMenu(Scanner scanner, Player player, ArrayList<Square> unownedSquares) {
		//TODO split this method up as it's getting too large
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

			boolean onSysSq = true;
			if (player.getPosition() == 0 || player.getPosition() == 5) {
				onSysSq = false;
			}
			SystemSquare ss = null;
			System.out.printf("%s's turn [%d units]\n", player.getName(), player.getPlayerResources());
			Square landed = unownedSquares.get(player.getPosition());

			if (landed == null) {
				Pair<Player, SystemSquare> ownedSquare = getSquareAndOwner(player.getPosition());
				String squareName = ownedSquare.getSecond().getSquareName();
				if (ownedSquare.getFirst().equals(player)) {
					System.out.printf("You are on %s. You own it.\n", squareName);
					ss = ownedSquare.getSecond();
				} else {
					int cost = ownedSquare.getSecond().getLandingCost();
					try {
						player.updateResources(-1 * cost);
						clearScreen();
						System.out.printf("%s's turn [%d units]\n", player.getName(), player.getPlayerResources());
						System.out.printf("Paid %s %d units", ownedSquare.getFirst().getName(), cost);
					} catch (IndexOutOfBoundsException e) {
						System.out.println(e.getMessage());
						//TODO handle player going bankrupt
					}
					System.out.printf("You are on %s. It is owned by %s.\n", squareName, ownedSquare.getFirst().getName());
				}
			} else if (!onSysSq) {
				System.out.printf("You are on %s. It can't be owned.\n", landed.getSquareName());
			} else {
				System.out.printf("You are on %s. It is not owned.\n", landed.getSquareName());
				Square square = unownedSquares.get(player.getPosition());
				if (square instanceof SystemSquare) {
					ss = (SystemSquare) square;
				}
			}

			System.out.println("Menu");

			//load options menu, with some skipped
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
				int[] roll = rollDice();
				System.out.printf("You rolled a %d and a %d.\nMoving %d spaces", roll[0], roll[1], roll[0] + roll[1]);
				loading();
				try {
					player.updatePosition(roll[0]+roll[1]);
				} catch (IndexOutOfBoundsException e) {
					System.out.print("You passed Go! Updating resources");
					player.updateResources(GO_RESOURCES);
					loading();
				}
				break;
			case 3:
				if (ss != null) {
					try {
						player.purchaseSquare(ss);
						//replace square with null
						unownedSquares.set(player.getPosition(), null);
						System.out.print("Purchasing " + ss.getSquareName());
						loading();
						break;
					} catch (IndexOutOfBoundsException e) {
						System.out.print("You cannot purchase this element. It will be auctioned");
						loading();
						//don't break to allow auction case to be executed
						//TODO could change to hide purchase option when resources are too low
					}
				}
			case 4:
				if (ss != null) {
					player.auctionSquare(players, ss);
					//replace square with null
					unownedSquares.set(player.getPosition(), null);
					loading();
				}
				//TODO automatically auction when resources low?
				break;
			case 5:
				if (ss != null) {
					//TODO currently only develops the element the player is on
					player.developElement(ss);
					System.out.print("Developing");
					loading();
				}
				//TODO hide this options when resources too low
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
	 * Roll two virtual dice and return two numbers
	 * 
	 * @return two-element integer array
	 */
	public static int[] rollDice() {
		int[] roll = new int[2];
		roll[0] = rand.nextInt(6) + 1;
		roll[1] = rand.nextInt(6) + 1;
		return roll;
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
			System.out.printf("%s (pos. %d) ", player.getName(), player.getPosition()+1);
			if (player.getOwnedElements().size() == 0) {
				System.out.print("[No owned elements]");
			} else {
				System.out.print("[");
				for (int i = 0; i < player.getOwnedElements().size(); i++) {
					SystemSquare e = player.getOwnedElements().get(i);
					System.out.print(e.getSquareName());
					System.out.printf(" (pos. %d, dev. %d)",e.getPosition()+1, e.getDevelopment());
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
		return new Pair<>(playerMatch, squareMatch);
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
