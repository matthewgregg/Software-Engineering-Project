package artemislite;

import java.util.*;

/**
 * Represents the game , including setup of players and board, rolling dice,
 * player movement on board, options menu functionality,
 * purchasing/developing/auctioning squares and updating resources
 *
 * @author 40084448 (Darragh Kieran)
 * @author 40151114 (Matthew Gregg)
 * @author 40246739 (Mark Graham)
 * @author 40315922 (Amit Jayaprakash)
 *
 */
public class Game {
	// global constants
	private static final int GO_RESOURCES = 200;

	private static final Random rand = new Random();
	private static List<Player> players;
	private static final Scanner scanner = new Scanner(System.in);
	// scanner cannot be closed and then reused
	private static ArrayList<Square> unownedSquares = null;
	// TODO if second player lands on same square as first player and buys it, the first player pays the second on their turn
	private static boolean paid = false;
	private static boolean auctioned = false;
	
	private static ArrayList<SystemName> systemNames;

	/**
	 * Main method ArtemisLite.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		clearScreen();
		boolean isGameOver = false;
		boolean quitGame;
		SetupGame gameSetup = new SetupGame();

		players = new ArrayList<>(SetupGame.playerCreation(scanner));

		// squares will be moved to a player's array when purchased and replaced with
		// null in this array
		unownedSquares = new ArrayList<>(SetupGame.setupBoard());

		clearScreen();

		System.out.println(welcomeMessage(players));
		try {
			//Thread.sleep(5500);
			//TODO Would displayGameRules be better as a menu option so they can review rules at any time?
			//TODO yes probably
			gameSetup.displayGameRules(systemNames);
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.out.println("Thread error");
		}
		int playerCount = 0;
		do {
			playerCount++;
			if (playerCount > players.size()) {
				playerCount = 1;
			}
			Player player = players.get(playerCount - 1);
			quitGame = !generateOptionsMenu(scanner, player);
			//reset global vars on new turn
			paid = false;
			auctioned = false;
		} while (!isGameOver && !quitGame);
		// TODO is there a different ending if a player goes bankrupt

		if (quitGame) {
			clearScreen();
			System.out.printf("Game is over! %s ended the game\n", players.get(playerCount - 1).getName());
		}
	}

	/**
	 * generate options for user
	 * 
	 * @param scanner the scanner object
	 * @param player  the current player
	 * @return a boolean for whether the user finished their turn or not. If false,
	 *         the player quit the game. If true, the player finished their turn
	 */
	public static boolean generateOptionsMenu(Scanner scanner, Player player) throws IndexOutOfBoundsException {
		// TODO split this method up as it's getting too large
		// local vars
		int userOption = 0;
		boolean turnFinished = false;
		boolean rolled = false;
		boolean purchased = false;
		int menuNum;
		HashMap<Integer, Integer> menuOptions = new HashMap<>();
		// initialise menu options
		String[] allMenu = new String[7];
		allMenu[0] = "Display Board State";
		allMenu[1] = "Roll Dice";
		allMenu[2] = "Purchase Square";
		allMenu[3] = "Auction Element";
		allMenu[4] = "Develop Element";
		allMenu[5] = "Finish Turn";
		allMenu[6] = "Quit Game";

		do {
			menuNum = 0;
			clearScreen();
			// decide if player is on a system square(i.e any square except position 0 or 6)
			boolean onSysSq = true;
			if (player.getPosition() == 0 || player.getPosition() == 6) {
				onSysSq = false;
			}
			Square landedSquare = unownedSquares.get(player.getPosition());
			SystemSquare ss = userStatus(player, landedSquare, onSysSq);

			System.out.println("Menu");

			// load options menu, with some skipped
			for (int i = 0; i < allMenu.length; i++) {
				//skip roll dice
				if (i == 1 && rolled) {
					continue;
				}
				//skip purchase, auction, develop, finish turn
				if (i > 1 && i < 6 && !rolled) {
					continue;
				}
				//skip roll dice, purchase, auction, develop
				if (i > 1 && i < 4 && (landedSquare == null || !onSysSq)) {
					continue;
				}
				//skip purchase
				if (i == 2 && ss!=null && (player.getPlayerResources() < ss.getBaseCost())) {
					continue;
				}
				//skip auction
				if (i == 3 && ss!=null && (auctioned || !isAuctionable(ss, player))) {
					continue;
				}
				//skip develop
				if (i == 4 && (player.getOwnedElements().size() == 0
						|| player.getMinimumOwnedDevCost() >= player.getPlayerResources()
						|| player.getCompletedSystems() == null)) {
					continue;
				}
				//skip finish turn
				//if user is on a unowned system square and has enough resources and haven't purchased it yet, and the square is auctionable
				// and the auction hasn't occurred yet
				if (i == 5 && onSysSq && landedSquare != null && ss!=null && player.getPlayerResources() >= ss.getBaseCost() && !purchased && isAuctionable(ss, player) && !auctioned) {
					continue;
				}
				menuNum++;
				System.out.println(menuNum + ". " + allMenu[i]);
				menuOptions.put(menuNum, i + 1);
			}
			System.out.println("Enter option");

			userOption = scanIntInput(scanner, 1, menuNum, false);

			clearScreen();
			// output options menu
			switch (menuOptions.get(userOption)) {
			case 1:
				// display which elements are owned by who
				displayBoardState();
				loading(5);
				break;
			case 2:
				// roll dice and move player
				rolled = true;
				int[] roll = rollDice();
				System.out.printf("You rolled a %d and a %d.\nMoving %d spaces", roll[0], roll[1], roll[0] + roll[1]);
				loading(3);
				try {
					player.updatePosition(roll[0] + roll[1]);
					//TODO sometimes exception occurs as the player is moved to a position > 11
				} catch (IndexOutOfBoundsException e) {
					System.out.print("You passed Go! Updating resources");
					player.addResources(GO_RESOURCES);
					loading(3);
				}
				break;
			case 3:
				// purchase unowned square
				if (ss != null) {
					try {
						player.purchaseSquare(ss);
						// replace square with null
						unownedSquares.set(player.getPosition(), null);
						System.out.print("Purchasing " + ss.getSquareName());
						purchased = true;
						loading(3);
						break;
					} catch (IndexOutOfBoundsException e) {
						System.out.print("You cannot purchase this element. It will be auctioned");
						loading(3);
						// don't break to allow auction case to be executed. This block shouldn't
						// normally be executed
					}
				}
			case 4:
				// auction unowned square
				if (ss != null) {
					auctionSquare(scanner, players, ss, player);
					auctioned = true;
					// so the user doesn't have to pay the winner
					paid = true;
					loading(3);
				}
				break;
			case 5:
				// develop player's square
				developMenu(scanner, player);
				break;
			case 6:
				// end turn
				turnFinished = true;
				break;
			case 7:
				// quit game
				break;
			default:
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

	/**
	 * Loading screen containing slight pause
	 * 
	 * @param time the time to delay
	 */
	public static void loading(int time) {
		try {
			for (int i = 0; i <= time; i++) {
				Thread.sleep(1000);
				System.out.print(".");
			}
		} catch (InterruptedException e) {
			System.out.println("Thread error");
		}
		System.out.println();
	}

	/**
	 * Displays personalised welcome message
	 * 
	 * @param players the players
	 * @return the message
	 */
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
		welcome.insert(0, "Welcome to ArtemisLite, ");
		welcome.append(
				". \nThis virtual board game is inspired by Nasa's real life Artemis Mission...\n"
				+ "You can help send the first woman and next man to the moon.\n\n"
				+ "After that, next stop Mars!");
		return welcome;
	}

	/**
	 * Displays current state of the board(i.e. which elements are owned by which
	 * players)
	 */
	public static void displayBoardState() {
		for (Player player : players) {
			System.out.printf("%s (pos. %d) ", player.getName(), player.getPosition() + 1);
			if (player.getOwnedElements().size() == 0) {
				System.out.print("[No owned elements]");
			} else {
				System.out.print("[");
				for (int i = 0; i < player.getOwnedElements().size(); i++) {
					SystemSquare e = player.getOwnedElements().get(i);
					System.out.print(e.getSquareName());
					System.out.printf(" (pos. %d, dev. %d)", e.getPosition() + 1, e.getDevelopment());
					if (i < player.getOwnedElements().size() - 1) {
						System.out.print(", ");
					}
				}
				System.out.print("]");
			}
			System.out.println();
		}
	}

	/**
	 * Gets a square and its owner.
	 * 
	 * @param position - current position of player
	 * @return Pair - player and system square
	 */
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

	/**
	 * prints user message
 	 * @param player
	 * @param landedSquare
	 * @param onSysSq
	 * @return systemsquare if the square is a system square
	 */
	public static SystemSquare userStatus(Player player, Square landedSquare, boolean onSysSq) {
		System.out.printf("%s's turn [%d units]\n", player.getName(), player.getPlayerResources());

		// decide if square landed on is owned by current player or another player
		if (landedSquare == null) {
			Pair<Player, SystemSquare> squareAndOwner = getSquareAndOwner(player.getPosition());
			String squareName = squareAndOwner.getSecond().getSquareName();
			if (squareAndOwner.getFirst().equals(player)) {
				System.out.printf("You are on %s. You own it.\n", squareName);
			} else {
				// owned by another player -- update resources to show fine deduction
				int cost = squareAndOwner.getSecond().getLandingCost();
				try {
					if (!paid) {
						player.addResources(-1 * cost);
						paid = true;
						clearScreen();
						System.out.printf("%s's turn [%d units] (Paid %s %d units)\n", player.getName(),
								player.getPlayerResources(), squareAndOwner.getFirst().getName(), cost);
					}
				} catch (IndexOutOfBoundsException e) {
					throw new IndexOutOfBoundsException();
					// TODO handle player going bankrupt (throw exception?)
				}
				System.out.printf("You are on %s. It is owned by %s.\n", squareName,
						squareAndOwner.getFirst().getName());
			}
			return squareAndOwner.getSecond();
		} else if (!onSysSq) {
			System.out.printf("You are on %s. It can't be owned.\n", landedSquare.getSquareName());
		} else {
			Square square = unownedSquares.get(player.getPosition());
			if (square instanceof SystemSquare) {
				SystemSquare ss = (SystemSquare) square;
				if (player.getPlayerResources() >= ss.getBaseCost()) {
					System.out.printf("You are on %s. It is not owned. You can buy it for %d units.\n",
							square.getSquareName(), ss.getBaseCost());
				} else if (isAuctionable(ss, player) && !auctioned) {
					System.out.printf("You are on %s but don't have enough resources to buy it.\nAuctioning element",
							ss.getSquareName());
					loading(5);
					auctionSquare(scanner, players, ss, player);
					auctioned = true;
					paid = true;
					loading(3);
					clearScreen();
					userStatus(player, landedSquare, onSysSq);
				} else {
					System.out.printf("You are on %s but don't have enough resources to buy it.\n",
							ss.getSquareName());
				}
				return ss;
			}
		}
		return null;
	}

	/**
	 * Allows Players to choose any of their owned elements to develop.
	 * 
	 * @param scanner the scanner
	 * @param player  the current player
	 */
	public static void developMenu(Scanner scanner, Player player) {
		ArrayList<SystemSquare> squares = player.getOwnedElements();
		ArrayList<SystemName> systems = player.getCompletedSystems();

		//remove incomplete systems using predicate
		squares.removeIf(s -> !systems.contains(s.getSystemName()));

		System.out.printf("You have %d units\n", player.getPlayerResources());
		System.out.println("Please enter a square to develop. Type # to cancel.");
		int count = 1;
		boolean valid = false;
		for (SystemSquare square : squares) {
			System.out.printf("%d. %s [%d] - %d units per dev.\n", count++, square.getSquareName(),
					square.getDevelopment(), square.getCostPerDevelopment());
		}
		int squareNum = scanIntInput(scanner, 1, squares.size(), true);
		if (squareNum != -1) {
			SystemSquare chosenSquare = squares.get(squareNum - 1);
			System.out.println("Please enter how many developments to add. Type # to cancel.");
			do {
				try {
					int dev = scanIntInput(scanner, 1, chosenSquare.getMaxDevelopment(), true);
					if (dev != -1) {
						player.developElement(chosenSquare, dev);
						valid = true;
						System.out.printf("Developing %s with %d development(s)", chosenSquare.getSquareName(), dev);
						loading(3);
					} else {
						break;
					}
				} catch (IndexOutOfBoundsException e) {
					System.out.println("You don't have enough resources to do that. Enter a different number.\n");
				}
			} while (!valid);
		}
	}

	/**
	 * Scans menu input and allows user to enter '#' to cancel selection
	 * 
	 * @param scanner     the scanner
	 * @param lowerLimit  the lower limit
	 * @param upperLimit  the upper limit
	 * @param cancellable alternative input for the user, can be used to return to
	 *                    previous screen or as non-input
	 * @return the user's input
	 */
	public static int scanIntInput(Scanner scanner, int lowerLimit, int upperLimit, boolean cancellable) {
		boolean valid = false;
		int userOption = 0;
		do {
			String option;
			try {
				option = scanner.nextLine();
				if (cancellable && option.equals("#")) {
					valid = true;
					userOption = -1;
				} else if (Integer.parseInt(option) >= lowerLimit && Integer.parseInt(option) <= upperLimit) {
					valid = true;
					userOption = Integer.parseInt(option);
				} else {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException e) {
				System.out.printf("Please enter a number between %d and %d.\n", lowerLimit, upperLimit);
			}
		} while (!valid);
		return userOption;
	}

	/**
	 * Auctions a square to other players
	 * 
	 * @param players all players
	 * @param square  the square to auction
	 */
	public static boolean auctionSquare(Scanner scanner, List<Player> players, SystemSquare square, Player player) {
		// create vars and remove player from bidders list
		ArrayList<Player> bidders = new ArrayList<>(players);
		bidders.remove(player);
		int highestBid = square.getBaseCost();
		boolean biddingEnded = false;
		int prevBid = 0;
		Player highestBidder = null;
		int rejectedCount = 0;
		do {
			StringBuilder names = new StringBuilder();
			ArrayList<Player> removedBidders = new ArrayList<>();
			for (Player bidder : bidders) {
				// remove anyone with too few resources
				if (bidder.getPlayerResources() < highestBid) {
					removedBidders.add(bidder);
				} else {
					names.append(bidder.getName()).append(", ");
				}
			}
			bidders.removeAll(removedBidders);
			String strNames = names.deleteCharAt(names.length() - 2).toString().trim();

			for (Player bidder : bidders) {
				clearScreen();
				System.out.printf("Bidding on %s, starting at %d. (Eligible bidders: %s)\n", square.getSquareName(),
						square.getBaseCost(), strNames);
				// check if current bidder is the highest bidder - break if so
				if (bidder.getPlayerResources() > highestBid) {
					if (bidder.equals(highestBidder) || rejectedCount == bidders.size()) {
						biddingEnded = true;
						break;
					}
					if (highestBidder != null) {
						System.out.printf("%s is the highest bidder at %d units\n", highestBidder.getName(),
								highestBid);
					}
					System.out.printf("%s, please enter your bid or # to skip.\n", bidder.getName());

					int bid = Game.scanIntInput(scanner,
							square.getBaseCost() + (highestBid == square.getBaseCost() ? 0 : 1),
							bidder.getPlayerResources(), true);
					if (bid > prevBid) {
						highestBid = bid;
						highestBidder = bidder;
						// increment rejected count to prevent infinite loop if no one bids
					} else if (bid == -1) {
						rejectedCount++;
					}
					// set current bid to previous user's bid for next iteration
					prevBid = bid;
				}
			}
		} while (!biddingEnded);

		// purchase square and update resources for highest bidder
		if (highestBidder != null) {
			System.out.printf("%s has won %s", highestBidder.getName(), square.getSquareName());
			highestBidder.addResources(square.getBaseCost() - highestBid);
			highestBidder.purchaseSquare(square);
			unownedSquares.set(player.getPosition(), null);
			return true;
		} else {
			System.out.printf("Nobody wanted %s", square.getSquareName());
			return false;
		}
	}

	/**
	 * Determines if a square is to be auctioned
	 * 
	 * @param ss
	 * @param player
	 * @return
	 */
	public static boolean isAuctionable(SystemSquare ss, Player player) {
		ArrayList<Player> bidders = new ArrayList<>(players);
		bidders.remove(player);
		int playersTooExpensive = 0;
		for (Player bidder : bidders) {
			if (bidder.getPlayerResources() < ss.getBaseCost()) {
				playersTooExpensive++;
			}
		}
		return playersTooExpensive < bidders.size();
	}

	/**
	 * Clears console of any text
	 */
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
