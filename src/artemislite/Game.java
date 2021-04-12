package artemislite;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
	private static final Scanner scanner = new Scanner(System.in);
	static final List<Player> players = Collections.unmodifiableList(SetupGame.playerCreation(scanner));
	static final List<Square> squares = Collections.unmodifiableList(SetupGame.setupBoard());

	// scanner cannot be closed and then reused

	public static void main(String[] args) {
		clearScreen();
		System.out.print(welcomeMessage());
		loading(5, true);

		boolean quitGame = false;
		boolean bankruptcy = false;
		int playerCount = 0;
		boolean endGame;
		do {
			playerCount++;
			if (playerCount > players.size()) {
				playerCount = 1;
			}
			Player player = players.get(playerCount - 1);
			try {
				quitGame = !generateOptionsMenu(scanner, player);
			} catch (BankruptcyException e) {
				// player went bankrupt
				bankruptcy = true;
			}
			endGame = players.stream()
					.flatMap(p -> p.getOwnedElements().stream())
					.filter(e -> e.getDevelopment() == 4).count() == 10;
		} while (!endGame && !quitGame && !bankruptcy);

		if (quitGame) {
			System.out.printf("Game is over! %s quit the game.\n", players.get(playerCount - 1).getName());
		} else if (bankruptcy) {
			System.out.printf("Game is over! %s went bankrupt.\n", players.get(playerCount - 1).getName());
		} else {
			System.out.println("Epilogue...");
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
	public static boolean generateOptionsMenu(Scanner scanner, Player player) throws BankruptcyException {
		// local vars
		int userOption;
		boolean turnFinished = false;
		boolean rolled = false;
		boolean purchased = false;
		boolean paid = false;
		boolean auctioned = false;
		int menuNum;
		HashMap<Integer, Integer> menuOptions = new HashMap<>();
		// initialise menu options
		String[] allMenu = new String[10];
		allMenu[0] = "Rules";
		allMenu[1] = "Display Board State";
		allMenu[2] = "Roll Dice";
		allMenu[3] = "Purchase Element";
		allMenu[4] = "Auction Element";
		allMenu[5] = "Buy Developments";
		allMenu[6] = "Mortgage Element";
		allMenu[7] = "Sell Developments";
		//sell to another player
		allMenu[8] = "Finish Turn";
		allMenu[9] = "Quit Game";

		do {
			menuNum = 0;
			clearScreen();

			Square landedSquare = squares.get(player.getPosition());
			Triplet<SystemSquare, boolean, boolean> triplet = generateSquareStatus(player, landedSquare, rolled, paid, auctioned);
			SystemSquare ss = triplet.getFirst();
			paid = triplet.getSecond();
			auctioned = triplet.getThird();

			System.out.println("\nMENU");

			// load options menu, with some skipped
			for (int i = 0; i < allMenu.length; i++) {
				// skip roll dice
				if (i == 2 && rolled) {
					continue;
				}
				// skip purchase, auction, develop, finish turn
				if (i > 2 && i < 9 && !rolled) {
					continue;
				}
				// skip roll dice, purchase, auction, develop
				if (i > 2 && i < 5 && ss != null && ss.isOwned()) {
					continue;
				}
				// skip purchase
				if (i == 3 && ss != null && (player.getPlayerResources() < ss.getBaseCost())) {
					continue;
				}
				// skip auction
				if (i == 4 && ss != null && (auctioned || !isAuctionable(ss, player))) {
					continue;
				}
				// skip develop
				if (i == 5 && (player.getOwnedElements().size() == 0
						|| player.getMinimumOwnedDevCost() > player.getPlayerResources()
						|| player.getCompletedSystems() == null)) {
					continue;
				}
				//skip mortgage
				if (i == 6 && !player.hasMortgagableElements()) {
					continue;
				}
				//skip sell developments
				if (i == 7 && !player.hasDevelopments()) {
					continue;
				}
				// skip finish turn
				// if user is on a unowned system square and has enough resources and haven't
				// purchased it yet, and the square is auctionable
				// and the auction hasn't occurred yet
				if (i == 8 && ss != null && !ss.isOwned()
						&& player.getPlayerResources() >= ss.getBaseCost() && !purchased && isAuctionable(ss, player)
						&& !auctioned) {
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
				displayGameRules(scanner);
				break;
			case 2:
				// display which elements are owned by who
				displayBoardState();
				loading(5, true);
				break;
			case 3:
				// roll dice and move player
				rolled = true;
				int[] roll = rollDice(3);
				System.out.printf("You rolled a %d and a %d.\nMoving %d spaces", roll[0], roll[1], roll[0] + roll[1]);
				loading(3, true);
				try {
					player.updatePosition(roll[0] + roll[1]);
				} catch (IndexOutOfBoundsException e) {
					System.out.print("You passed Go! Updating resources");
					player.addResources(GO_RESOURCES);
					loading(3, true);
				}
				break;
			case 4:
				// purchase unowned square
				assert ss != null;
				player.purchaseSquare(ss);
				System.out.print("Purchasing " + ss.getSquareName());
				purchased = true;
				loading(3, true);
				break;
			case 5:
				// auction unowned square
				assert ss != null;
				auctionSquare(scanner, ss, player);
				auctioned = true;
				// so the user doesn't have to pay the winner
				paid = true;
				loading(3, true);
				break;
			case 6:
				// develop player's square
				developMenu(scanner, player);
				break;
			case 7:
				// mortgage element
				mortgageMenu(scanner, player);
				break;
			case 8:
				//sell developments
				sellDevelopmentsMenu(scanner, player);
			case 9:
				// end turn
				turnFinished = true;
				break;
			case 10:
				// quit game
				clearScreen();
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
	public static int[] rollDice(int d) {
		int[] roll = new int[2];
		roll[0] = rand.nextInt(d) + 1;
		roll[1] = rand.nextInt(d) + 1;
		return roll;
	}

	/**
	 * Loading screen containing slight pause
	 * 
	 * @param time the time to delay
	 */
	public static void loading(int time, boolean withDots) {
		try {
			for (int i = 0; i <= time; i++) {
				Thread.sleep(1000);
				if (withDots) {
					System.out.print(".");
				}
			}
		} catch (InterruptedException e) {
			System.out.println("Thread error");
		}
		System.out.println();
	}

	/**
	 * Displays personalised welcome message
	 *
	 * @return the message
	 */
	public static StringBuilder welcomeMessage() {
		StringBuilder welcome = new StringBuilder("Welcome to ArtemisLite, ");
		welcome.append(players.stream().limit(players.size() - 1).map(Player::getName).collect(Collectors.joining(", ")));
		welcome.append(" and ").append(players.get(players.size() - 1).getName());
		welcome.append("""
				.\s
				This virtual board game is inspired by Nasa's real life Artemis Mission...
				You can help send the first woman and next man to the moon.

				After that, next stop Mars""");
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
				String separator = "";
				for (SystemSquare s : player.getOwnedElements()) {
					System.out.println(s.getSquareName());
					System.out.printf(" (pos. %d, dev. %d)%s", s.getPosition() + 1, s.getDevelopment(), separator);
					separator = ", ";
				}
				System.out.print("]");
			}
			System.out.println();
		}
	}

	/**
	 * prints user message
	 * 
	 * @param player       	the player
	 * @param landedSquare 	the square they've landed on
	 * @param rolled		whether the current user has rolled or not
	 * @return systemsquare if the square is a system square
	 */
	public static Triplet<SystemSquare, boolean, boolean> generateSquareStatus(Player player, Square landedSquare, boolean rolled, boolean paid, boolean auctioned) throws BankruptcyException {
		System.out.printf("%s's turn [%d units]\n", player.getName(), player.getPlayerResources());

		Square square = squares.get(player.getPosition());
		if (square instanceof SystemSquare) {
			SystemSquare ss = (SystemSquare) square;
			if (ss.isOwned()) {
				Player owner = players.stream().filter(p -> p.getOwnedElements().contains(ss)).findAny().get();
				String squareName = ss.getSquareName();
				if (owner.equals(player)) {
					System.out.printf("You are on %s. You own it.\n", squareName);
				} else {
					// owned by another player - subtract resources from player
					int cost = ss.getLandingCost();
					if (!paid && rolled && !ss.isMortgaged()) {
						player.addResources(-1 * cost);
						paid = true;
						clearScreen();
						System.out.printf("%s's turn [%d units] (Paid %s %d units)\n", player.getName(),
								player.getPlayerResources(), owner.getName(), cost);
					}
					System.out.printf("You are on %s. It is owned by %s.\n", squareName, owner.getName());
				}
			} else if (player.getPlayerResources() >= ss.getBaseCost()) {
				String string = "You are on " + square.getSquareName() + ". It is not owned.";
				if (rolled) {
					string += " You can buy it for " + ss.getBaseCost() + " units.";
				}
				System.out.print(string + "\n");
			} else if (isAuctionable(ss, player) && !auctioned && rolled) {
				System.out.printf("You are on %s but don't have enough resources to buy it.\nAuctioning element", ss.getSquareName());
				loading(5, true);
				auctionSquare(scanner, ss, player);
				auctioned = true;
				paid = true;
				loading(3, true);
				clearScreen();
				generateSquareStatus(player, landedSquare, true, true, true);
			} else if (rolled) {
				System.out.printf("You are on %s but don't have enough resources to buy it.\n", ss.getSquareName());
			} else {
				System.out.printf("You are on %s.\n", ss.getSquareName());
			}
			return new Triplet<SystemSquare, boolean, boolean>(ss, paid, auctioned);
		} else {
			System.out.printf("You are on %s. It can't be owned.\n", landedSquare.getSquareName());
			return new Triplet<SystemSquare, boolean, boolean>(null, paid, auctioned);
		}
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

		// remove incomplete systems using predicate
		squares.removeIf(s -> !systems.contains(s.getSystemNameEnum()));

		System.out.printf("You have %d units\n", player.getPlayerResources());
		System.out.println("Enter a square to develop. Enter # to cancel.");
		int count = 1;
		boolean valid = false;
		for (SystemSquare square : squares) {
			System.out.printf("%d. %s [%d] - %d units per dev.\n", count++, square.getSquareName(),
					square.getDevelopment(), square.getCostPerDevelopment());
		}
		int squareNum = scanIntInput(scanner, 1, squares.size(), true);
		if (squareNum > 0) {
			SystemSquare chosenSquare = squares.get(squareNum - 1);
			System.out.println("Enter how many developments to add. Enter # to cancel.");
			do {
				try {
					int dev = scanIntInput(scanner, 1, chosenSquare.getMaxDevelopment(), true);
					if (dev > 0) {
						player.developElement(chosenSquare, dev);
						valid = true;
						System.out.printf("Developing %s with %d development(s)", chosenSquare.getSquareName(), dev);
						loading(3, true);
					} else {
						break;
					}
				} catch (BankruptcyException e) {
					System.out.println("You don't have enough resources to do that. Enter a different number.");
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
	 * @param square  the square to auction
	 */
	public static void auctionSquare(Scanner scanner, SystemSquare square, Player player) throws BankruptcyException {
		// copy players into new arraylist and remove player from bidders list
		ArrayList<Player> bidders = new ArrayList<>(players);
		bidders.remove(player);
		int highestBid = square.getBaseCost();
		boolean biddingEnded = false;
		int prevBid = 0;
		Player highestBidder = null;
		int rejectedCount = 0;
		do {
			for (Player bidder : bidders) {
				int finalHighestBid = highestBid;
				bidders.removeIf(b -> b.getPlayerResources() < finalHighestBid);
				String names = bidders.stream().map(Player::getName).collect(Collectors.joining(", "));
				clearScreen();
				System.out.printf("Bidding on %s, starting at %d. (Eligible bidders: %s)\n", square.getSquareName(),
						square.getBaseCost(), names);
				// check if current bidder is the highest bidder - break if so
				if (bidder.getPlayerResources() >= highestBid) {
					if (bidder.equals(highestBidder) || rejectedCount == bidders.size()) {
						biddingEnded = true;
						break;
					}
					if (highestBidder != null) {
						System.out.printf("%s is the highest bidder at %d units\n", highestBidder.getName(),
								highestBid);
					}
					System.out.printf("%s, please enter your bid or # to skip.\n", bidder.getName());

					int bid = scanIntInput(scanner,
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
			highestBidder.purchaseSquare(square, highestBid);
		} else {
			System.out.printf("Nobody wanted %s", square.getSquareName());
		}
	}

	/**
	 * Outputs game rules as requested by user via an options menu
	 * 
	 * @param scanner
	 */
	public static void displayGameRules(Scanner scanner) {

		System.out.println("Rules\nEnter a number or # to return to the main menu.\n");
		// initialise menu options
		String[] rulesMenu = new String[5];
		rulesMenu[0] = "All Game Rules";
		rulesMenu[1] = "Just the Basics";
		rulesMenu[2] = "Buying and Selling";
		rulesMenu[3] = "Development Rules";
		rulesMenu[4] = "Ending the Game Rules";

		// output menu
		int counter = 1;
		for (String option : rulesMenu) {
			System.out.printf("%d. %s\n", counter++, option);
		}
		System.out.println("Enter option");

		// instantiate arrayLists
		ArrayList<String> systemNames = stringifyEnum(SystemName.class);

		ArrayList<String> basicGameRules = new ArrayList<>();
		ArrayList<String> buyingSellingRules = new ArrayList<>();
		ArrayList<String> developmentRules = new ArrayList<>();
		ArrayList<String> endingRules = new ArrayList<>();

		// basic game structure rules
		//TODO implement this somewhere
//		basicGameRules.add("Roll dice to decide who goes first");
		basicGameRules.add("Basic Game Rules:");
		basicGameRules.add("The aim is to help NASA complete its mission by fully developing all mission-critical Systems");
		basicGameRules.add("When it's your go, pick what you'd like to do from the menu.");
		basicGameRules.add("e.g. Roll the dice to move along the board.");

		// buying and selling
		buyingSellingRules.add("Rules for Buying and Selling:");
		buyingSellingRules.add("You'll each be allotted some Space Points (the currency of the solar system) to start out.");
		buyingSellingRules.add("Use your points to purchase a square that you land on or pay other players when you land on their square.");
		buyingSellingRules.add("If you don't want to buy the square you land on, it will be auctioned to the other players.");

		// developing systems
		developmentRules.add("Rules for Developing Systems:");
		developmentRules.add("The board has 12 squares in total grouped into " + systemNames.size() + " systems: " + Arrays.toString(systemNames.toArray()));
		developmentRules.add("Systems and their squares get more expensive the further you are along the board...");
		developmentRules.add("There's also bigger rewards should another player land on your square.");
		developmentRules.add("Once you own a whole system, you can pay to add a development, but only if you can pass a mini-challenge first!");

		// ending the game
		endingRules.add("Rules for Ending the Game:");
		endingRules.add("All systems must be developed to complete the mission and win the game.");
		endingRules.add("Should any player go 'Bankrupt' by running out of Space Points, the game ends and the mission has failed.");

		// join separate arrayLists into one
		List<String> combinedRuleSets = Stream.of(basicGameRules,
				buyingSellingRules,
				developmentRules,
				endingRules).flatMap(Collection::stream).collect(Collectors.toList());

		// create hashmap of user input and corresponding list 
		HashMap<Integer, List<String>> getList = new HashMap<>();
		getList.put(1, combinedRuleSets);
		getList.put(2, basicGameRules);
		getList.put(3, buyingSellingRules);
		getList.put(4, developmentRules);
		getList.put(5, endingRules);

		int option = scanIntInput(scanner, 1, rulesMenu.length, true);
		clearScreen();

		if (option > 0) {
			for (String s : getList.get(option)) {
				System.out.print(s);
				loading(2, false);
			}
			System.out.print("\nPress enter to return to main menu");
			scanner.nextLine();
			clearScreen();
		}
	}

	/**
	 * allows a player to mortgage an undeveloped element
	 * @param scanner
	 * @param player
	 */
	public static void mortgageMenu(Scanner scanner, Player player) throws BankruptcyException {
		ArrayList<SystemSquare> undevelopedSquares = new ArrayList<>();
		int count = 1;
		System.out.println("Enter an element to mortgage. Enter # to cancel.");
		for (SystemSquare s : player.getOwnedElements()) {
			if (s.getDevelopment() == 0) {
				System.out.printf("%d. %s (%d)\n", count++, s.getSquareName(), s.getBaseCost());
				undevelopedSquares.add(s);
			}
		}
		int option = scanIntInput(scanner, 1, undevelopedSquares.size(), true);
		if (option > 0) {
			SystemSquare s = undevelopedSquares.get(option-1);
			s.setMortgaged(true);
			player.addResources(s.getBaseCost());
			System.out.printf("You have mortgaged %s for %d units. You can buy it back for %d units", s.getSquareName(), s.getBaseCost(), (int) (1.1 * s.getBaseCost()));
			loading(3, true);
		}
	}

	/**
	 * generates a menu to allow a player to sell developments at half price
	 * @param scanner scanner
	 * @param player the curremt player
	 * @throws BankruptcyException
	 */
	public static void sellDevelopmentsMenu(Scanner scanner, Player player) throws BankruptcyException {
		ArrayList<SystemSquare> developedSquares = new ArrayList<>(player.getOwnedElements());
		developedSquares.removeIf(s -> s.getDevelopment() == 0);
		int count = 1;
		System.out.print("Enter an element to sell development from. Enter # to cancel.");
		for (SystemSquare s : developedSquares) {
			System.out.printf("%d. %s (%d units per development)\n", count++, s.getSquareName(), (int) (s.getCostPerDevelopment() * 0.5));
		}
		int option = scanIntInput(scanner, 1, developedSquares.size(), true);
		if (option > 0) {
			SystemSquare s = developedSquares.get(option - 1);
			System.out.print("Enter how many developments to sell. Enter # to cancel.\n");
			int dev = scanIntInput(scanner, 1, s.getDevelopment(), true);
			if (dev > 0) {
				player.developElement(s, -1 * dev);
				System.out.printf("You have sold %s developments for a total of %s", dev, dev * s.getCostPerDevelopment());
				loading(3, true);
			}
		}
	}

	/**
	 * allows a player to sell an undeveloped element to another player for resources or elements
	 * @param scanner the scanner
	 * @param player the current player
	 */
	public static void sellElement(Scanner scanner, Player player) throws BankruptcyException {
		//TODO check if buyer has enough resources or has elements
		ArrayList<Player> buyers = new ArrayList<>(players);
		ArrayList<SystemSquare> undevelopedSquares = new ArrayList<>(player.getOwnedElements());
		undevelopedSquares.removeIf(s -> s.getDevelopment() != 0);
		int count = 1;
		System.out.println("Enter an element(s) to sell. Select continue to finalise selection. Enter # to cancel at any time.");
		for (SystemSquare s : undevelopedSquares) {
			System.out.printf("%d. %s (%d)\n", count++, s.getSquareName(), s.getBaseCost());
		}
		int option = scanIntInput(scanner, 1, undevelopedSquares.size(), true);
		if (option > 0) {
			SystemSquare ss = undevelopedSquares.get(option - 1);
			clearScreen();
			System.out.print("Who would you like to sell the element to?\n");
			buyers.remove(player);
			count = 1;
			for (Player buyer : buyers) {
				System.out.printf("%d. %s\n", count++, buyer.getName());
			}
			int buyOption = scanIntInput(scanner, 1, buyers.size(), true);
			if (buyOption > 0) {
				Player buyer = buyers.get(buyOption - 1);
				clearScreen();
				System.out.print("What would you like to sell the element for?\n1. Space Points\n2. An element(s)\n");
				int paymentMethod = scanIntInput(scanner, 1, 2, true);
				if (count > 0) {
					if (paymentMethod == 1) {
						clearScreen();
						System.out.println("Enter your agreed price for the element.");
						int cost = scanIntInput(scanner, 0, buyer.getPlayerResources(), true);
						System.out.println("The trade will occur in 10 seconds. Press enter to cancel.");
						if (inputTimer(10)) {
							player.addResources(cost);
							player.removeSquare(ss);
							buyer.purchaseSquare(ss, cost);
						}
					} else if (paymentMethod == 2) {
						int squareOption;
						int max = buyer.getOwnedElements().size()+1;
						ArrayList<SystemSquare> squaresToTrade = new ArrayList<>();
						ArrayList<SystemSquare> buyerSquares = buyer.getOwnedElements();
						do {
							clearScreen();
							count = 1;
							System.out.printf("Enter which element(s) to trade for %s. Select continue to finalise selection.\n", ss.getSquareName());
							for (SystemSquare ss2 : buyerSquares) {
								System.out.printf("%d. %s (%d)\n", count++, ss2.getSquareName(), ss2.getBaseCost());
							}
							System.out.printf("%d. Continue\n", count);
							squareOption = scanIntInput(scanner, 1, max, true);
							if (squareOption != max) {
								SystemSquare squareToTrade = buyerSquares.get(squareOption - 1);
								squaresToTrade.add(squareToTrade);
								buyerSquares.remove(squareToTrade);
								max--;
							}
						} while (squareOption < max);
						System.out.println("The trade will occur in 10 seconds. Press enter to cancel.");
						if (inputTimer(10)) {
							for (SystemSquare square : squaresToTrade) {
								buyer.removeSquare(square);
								player.purchaseSquare(square, 0);
							}
							buyer.purchaseSquare(ss, 0);
							player.removeSquare(ss);
						}
					}
				}
			}
		}
	}

	interface IExecCloseable extends AutoCloseable {
		void close();
	}

	/**
	 * starts a timer that can be interrupted by the user
	 * @return whether the timer stopped gracefully or was interrupted
	 */
	public static boolean inputTimer(int time) {
		ExecutorService ex = Executors.newSingleThreadExecutor();
		try (IExecCloseable ignored = ex::shutdownNow) {
			Future<Void> inputTimer = ex.submit(() -> {
				while (!new BufferedReader(new InputStreamReader(System.in)).ready()) {
					Thread.sleep(100);
				}
				return null;
			});
			try {
				inputTimer.get(time, TimeUnit.SECONDS);
				return false;
			} catch (TimeoutException e) {
				inputTimer.cancel(true);
				return true;
			} catch (InterruptedException | ExecutionException e) {
				return false;
			}
			/*either use autocloseable interface or finally block
		} finally {
			ex.shutdownNow();*/
		}
	}

	/**
	 *
	 * @param en the enum class to be stringified
	 * @return array of stringified enums
	 */
	public static <E extends Enum<E>> ArrayList<String> stringifyEnum(Class<E> en) {
		EnumSet<E> enums = EnumSet.allOf(en);
		ArrayList<String> strEnums = new ArrayList<>();
		enums.iterator().forEachRemaining(s -> strEnums.add(stringifyEnum(s)));
		return strEnums;
	}

	/**
	 *
	 * @param en the enum to be stringified
	 * @return the enum as a user friendly string
	 */
	public static <E extends Enum<E>> String stringifyEnum(E en) {
		String[] words = en.name().toLowerCase().split("_");
		StringBuilder stringify = new StringBuilder();

		for (String w : words) {
			String first = w.substring(0, 1);
			String rest = w.substring(1);
			stringify.append(first.toUpperCase()).append(rest.toLowerCase()).append(" ");
		}
		return stringify.toString().trim();
	}

	/**
	 * Determines if a square is to be auctioned
	 * 
	 * @param ss     the system square
	 * @param player the current player
	 * @return whether the square can be auctioned or not
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
