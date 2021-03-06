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
	private static final int BANKRUPTCY_RISK = 100;
	private static final int MIN_BANKRUPTCY_DONATION = 100;

	private static final Random rand = new Random();
	// an unmodifiable list ensures the contents of the array cannot be modified. Declared as final so the variable cannot be reassigned
	private static final List<Square> squares = Collections.unmodifiableList(SetupGame.setupBoard());

	public static void main(String[] args) {
		clearScreen();
		System.out.print(introduction());
		loading(5, true);
		clearScreen();
		// System.in input stream cannot be closed and then reused, so must be passed as a variable. The input stream
		// is contained inside a Scanner object. This also allows a custom inputstream to be passed for testing
		final Scanner scanner = new Scanner(System.in);
		final List<Player> players = Collections.unmodifiableList(SetupGame.playerCreation(scanner));
		
		clearScreen();
		try {
			printLaunchStatusCheck();
		} catch (InterruptedException e) {
			System.out.println("Thread error");
		}
		clearScreen();
		printWelcomeMessage(players); loading(10, true);
		boolean quitGame = false;
		boolean bankruptcy = false;
		int playerCount = 0;
		boolean endGame;
		do {
			playerCount++;
			if (playerCount > players.size()) {
				// loop back to the start of the player array
				playerCount = 1;
			}
			Player player = players.get(playerCount - 1);
			try {
				// generatedOptionsMenu returns a boolean determining whether the players turn ended gracefully or not
				// if it did not end gracefully (false), then the player quit the game early and quitGame is true
				quitGame = !generateOptionsMenu(scanner, player, players);
			} catch (BankruptcyException e) {
				// player went bankrupt
				bankruptcy = true;
			}
			// check if all elements are developed
			endGame = players.stream().flatMap(p -> p.getOwnedSquares().stream())
					.filter(e -> e.getDevelopment() == e.getMaxDevelopment()).count() == 10;
		} while (!endGame && !quitGame && !bankruptcy);

		clearScreen();
		if (quitGame) {
			System.out.printf("Game is over! %s quit the game.\n", players.get(playerCount - 1).getName());
		} else if (bankruptcy) {
			System.out.printf("Game is over! %s went bankrupt.\n", players.get(playerCount - 1).getName());
		} else {
			epilogue(players);
		}
		scanner.close();
	}

	/**
	 * generate options for user
	 * 
	 * @param scanner the scanner object
	 * @param player  the current player
	 * @return a boolean for whether the user finished their turn or not. If false,
	 *         the player quit the game. If true, the player finished their turn
	 */
	public static boolean generateOptionsMenu(Scanner scanner, final Player player, final List<Player> players) throws BankruptcyException {
		// local vars
		int userOption;
		boolean turnFinished = false;
		boolean endGame = false;
		boolean rolled = false;
		boolean purchased = false;
		boolean paid = false;
		boolean auctioned = false;
		int menuNum;
		// create hashmap containing user-facing menu index paired with value for actual index
		HashMap<Integer, Integer> menuOptions = new HashMap<>();
		// initialise menu options
		String[] allMenu = new String[10];
		allMenu[0] = "Rules";
		allMenu[1] = "Display Board State";
		allMenu[2] = "Roll Dice";
		allMenu[3] = "Purchase Element";
		allMenu[4] = "Purchase Developments";
		allMenu[5] = "Sell Developments or Mortgage Element";
		allMenu[6] = "Trade with Player";
		allMenu[7] = "Donate to Player";
		allMenu[8] = "Finish Turn";
		allMenu[9] = "Quit Game";

		do {
			menuNum = 0;

			Square landedSquare = squares.get(player.getPosition());
			Triplet<SystemSquare, Boolean, Boolean> triplet = generateSquareStatus(scanner, player, landedSquare,
					players, rolled, paid, auctioned);
			// the square the player is on, null if not a systemsquare
			SystemSquare ss = triplet.getFirst();
			paid = triplet.getSecond();
			auctioned = triplet.getThird();

			System.out.println("\nMENU");

			// load options menu, with some skipped
			// the menu skips options when the condition is true
			for (int i = 0; i < allMenu.length; i++) {
				// skip roll dice
				if (i == 2 && rolled) {
					continue;
				}
				// skip purchase, auction, develop, finish turn when not rolled
				if (i > 2 && i < 9 && !rolled) {
					continue;
				}
				// skip roll dice, purchase, develop
				if (i == 3 && ss != null && ss.isOwned()) {
					continue;
				}
				// skip purchase
				if (i == 3 && (ss == null || player.getPlayerResources() < ss.getBaseCost())) {
					continue;
				}
				// skip buy developments
				if (i == 4 && (player.getOwnedSquares().size() == 0
						|| player.getMinimumOwnedDevCost() > player.getPlayerResources()
						|| player.getDevelopableSystems() == null)) {
					continue;
				}
				// skip deal with bank
				if (i == 5 && !player.hasDevelopments() && !player.hasMortgageableSquares() && !player.hasMortgagedSquares()) {
					continue;
				}
				// skip trade with player
				if (i == 6 && players.stream().noneMatch(p -> p.getOwnedSquares().size() > 0)) {
					continue;
				}
				// skip donate to player
				if (i == 7 && (players.stream().noneMatch(Player::goingBankrupt) || player.goingBankrupt())) {
					continue;
				}
				menuNum++;
				System.out.println(menuNum + ". " + allMenu[i]);
				// store user-facing menu index paired with actual index
				menuOptions.put(menuNum, i + 1);
			}
			System.out.println("Enter option");

			userOption = scanIntInput(scanner, 1, menuNum, false);

			clearScreen();

			// output options menu
			switch (menuOptions.get(userOption)) {
			case 1:
				// display rules
				displayGameRules(scanner);
				break;
			case 2:
				// display which squares are owned by who
				displayBoardState(players);
				scanner.nextLine();
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
					System.out.printf("Your budget increased! You get %d credits", GO_RESOURCES);
					player.addResources(GO_RESOURCES);
					loading(3, true);
				}
				break;
			case 4:
				// purchase unowned square
				assert ss != null;
				purchaseSquare(scanner, ss, player);
				purchased = true;
				break;
			case 5:
				// buy development
				buyDevelopments(scanner, player);
				break;
			case 6:
				// sell developments or mortgage
				bankMenu(scanner, player);
				break;
			case 7:
				// trade or sell square
				tradeWithPlayer(scanner, player, players);
				break;
			case 8:
				// donate to other player
				makeDonation(scanner, player, players);
				break;
			case 9:
				// end turn
				// if user is on a unowned system square and hasn't
				// purchased it yet, and the square is auctionable
				// and the auction hasn't occurred yet
				turnFinished = true;
				if (ss != null && !ss.isOwned() && !purchased && isAuctionable(ss, player, players) && !auctioned) {
					System.out.printf("%s didn't want to purchase %s.\nAuctioning element", player.getName(), ss.getSquareName());
					loading(5, true);
					auctionSquare(scanner, ss, player, players);
					auctioned = true;
					paid = true;
					loading(3, true);
				}
				break;
			case 10:
				// quit game
				clearScreen();
				System.out.print("WARNING\nAre you sure you want to quit? The game will end for all players in 10 seconds. Press enter to cancel.");
				if (inputTimer(10)) {
					endGame = true;
				} else {
					// consume new line char
					scanner.nextLine();
				}
				break;
			}
		} while (!turnFinished && !endGame);
		return turnFinished;
	}

	/**
	 * Scans menu input and allows user to enter '#' to cancel selection
	 *
	 * @param scanner     the scanner
	 * @param lowerLimit  the inclusive lower limit
	 * @param upperLimit  the inclusive upper limit
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
					// cancel input
					valid = true;
					userOption = -1;
				} else if (Integer.parseInt(option) >= lowerLimit && Integer.parseInt(option) <= upperLimit) {
					// valid input, return value
					valid = true;
					userOption = Integer.parseInt(option);
				} else {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException e) {
				// invalid input
				System.out.printf("Houston we have a problem! Please enter a number between %d and %d.\n", lowerLimit, upperLimit);
			}
		} while (!valid);
		return userOption;
	}

	public static StringBuilder introduction() {
		StringBuilder message = new StringBuilder("\t\t##################################\n"
				+ "\t\t###      ARTEMISLITE 2024      ###\n"
				+ "\t\t##################################");

		message.append("\n\nWelcome to the ArtemisLite Game!\n\n"
				+ "This virtual board game is inspired by Nasa's real life Artemis Mission...\n"
				+ "You can help send the first woman and next man to the moon.\n\n"
				+ "After that, next stop Mars!\n\n");
		return message;
	}

	public static void printLaunchStatusCheck() throws InterruptedException {
		System.out.print("Verify go/no-go for start all launch sequence.");
		Thread.sleep(1500);
		System.out.print("\n\tVerify go.");
		Thread.sleep(1000);
		System.out.print("\n\nMission director, CBTS 111.");
		Thread.sleep(1500);
		System.out.print("\n\tGo.");
		Thread.sleep(1000);
		System.out.print("\nVerify go for launch.");
		Thread.sleep(1500);
		System.out.print("\n\tGo for launch.");
		Thread.sleep(1500);
		System.out.print("\n\nFlight, go for launch.");
		Thread.sleep(1500);
		System.out.print("\n\nAll flight controllers, coming up on auto sequence.");
		Thread.sleep(5000);

		clearScreen();
		System.out.print("BOOSTER.");
		Thread.sleep(1000);
		System.out.print("\n\tWe're GO, Flight.");
		Thread.sleep(1000);
		System.out.print("\nEECOM.");
		Thread.sleep(1000);
		System.out.print("\n\tGO, Flight.");
		Thread.sleep(1000);
		System.out.print("\nGNC.");
		Thread.sleep(1000);
		System.out.print("\n\tGO, Flight.");
		Thread.sleep(1000);
		System.out.print("\nTELCOM.");
		Thread.sleep(1000);
		System.out.print("\n\tGO, Flight.");
		Thread.sleep(1000);
		System.out.print("\nCONTROL.");
		Thread.sleep(1000);
		System.out.print("\n\tGO.");
		Thread.sleep(1000);
		System.out.print("\nNETWORK.");
		Thread.sleep(1000);
		System.out.print("\n\tAffirmative, Flight.");
		Thread.sleep(3000);
		System.out.print("\n\nLift off.");
		Thread.sleep(3000);

		clearScreen();
		System.out.print("Staging...");
		Thread.sleep(2000);
		System.out.print("\n\nBOOSTER.");
		Thread.sleep(1000);
		System.out.print("\n\tGo.");
		Thread.sleep(1000);
		System.out.print("\nFIDO.");
		Thread.sleep(1000);
		System.out.print("\n\tGo.");
		Thread.sleep(1000);
		System.out.print("\nGUIDANCE.");
		Thread.sleep(1000);
		System.out.print("\n\tGo.");
		Thread.sleep(1000);
		System.out.print("\nGood for staging, CAPCOM.");
		Thread.sleep(3000);

		System.out.print("\n\nStaging...");
		Thread.sleep(1000);
		System.out.print(" and ignition.");
		Thread.sleep(5000);
		System.out.print("\n\nThrust is go, all engines.");
		Thread.sleep(5000);
	}

	/**
	 * Displays personalised welcome message
	 *
	 * @return the message
	 */
	public static void printWelcomeMessage(final List<Player> players) {
		String welcome = "Welcome "
				+ players.stream().limit(players.size() - 1).map(Player::getName).collect(Collectors.joining(", "))
				+ " and " + players.get(players.size() - 1).getName()
				+ ". Your mission as crew members of ArtemisLite is to procure all the systems required to launch "
				+ "and land on the moon! \nRemember you're not just playing against each other - if a crew member goes bankrupt, you all lose!\n";
		System.out.print(welcome);
	}

	/**
	 * prints user message
	 *
	 * @param player       the player
	 * @param square the square they've landed on
	 * @param rolled       whether the current user has rolled or not
	 * @return systemsquare if the square is a system square
	 */
	public static Triplet<SystemSquare, Boolean, Boolean> generateSquareStatus(Scanner scanner, final Player player,
			final Square square, final List<Player> players, boolean rolled, boolean paid, boolean auctioned) throws BankruptcyException {

		clearScreen();
		if (players.stream().anyMatch(p -> p.getPlayerResources() < BANKRUPTCY_RISK)) {
			// if a player is at risk of bankruptcy, notify them
			if (player.goingBankrupt()) {
				System.out.print("You are at risk of going bankrupt, which will end the game! Try and get the other players to donate credits to you.\n");
			} else {
				String names = getPlayersNearBankruptcy(player, players).stream().map(Player::getName)
						.collect(Collectors.joining(", "));
				System.out.printf("%s is at risk of bankruptcy, which will end the game! Consider donating credits to them.\n", names);
			}
		}

		System.out.printf("%s's turn [%d credits]", player.getName(), player.getPlayerResources());

		// Passing the landed square into the method was used in order to make the method more testable (instead of finding
		// the index of the user's position in the squares array)
		if (square instanceof SystemSquare) {
			SystemSquare ss = (SystemSquare) square;
			if (ss.isOwned()) {
				Player owner = players.stream().filter(p -> p.getOwnedSquares().contains(ss)).findAny().get();
				String squareName = ss.getSquareName();
				if (owner.equals(player)) {
					System.out.printf("\nYou are on %s. You own it.", squareName);
				} else {
					// owned by another player - subtract resources from player
					int cost = ss.getLandingCost();
					if (!paid && rolled && !ss.isMortgaged()) {
						System.out.printf("\n%s, do you want to charge %s %d credits?\n", owner.getName(), player.getName(), cost);
						System.out.println("1. Yes");
						System.out.println("2. No");
						int opt = scanIntInput(scanner, 1, 2, false);
						if (opt == 1) {
							player.addResources(-1 * cost);
							owner.addResources(cost);
							System.out.printf("Paid %s %d credits", owner.getName(), cost);
							loading(3, true);
						}
						return generateSquareStatus(scanner, player, square, players, true, true, auctioned);
					}
					System.out.printf("\nYou are on %s. It is owned by %s.", squareName, owner.getName());
				}
			} else if (player.getPlayerResources() >= ss.getBaseCost()) {
				// if player can purchase square
				String string = "\nYou are on " + square.getSquareName() + ", which is part of " + ss.getSystemNameString() + ". It is not owned.";
				if (rolled) {
					string += " You can purchase it for " + ss.getBaseCost() + " credits.";
				}
				System.out.print(string);
			} else if (isAuctionable(ss, player, players) && !auctioned && rolled) {
				// auction element if the player can't buy it
				System.out.printf("\nYou are on %s but don't have enough resources to purchase it.\nAuctioning element", ss.getSquareName());
				loading(5, true);
				auctionSquare(scanner, ss, player, players);
				loading(3, true);
				clearScreen();
				return generateSquareStatus(scanner, player, square, players, true, true, true);
			} else if (rolled) {
				// if player can't purchase square and it can't be auctioned
				System.out.printf("\nYou are on %s but don't have enough resources to purchase it.",
						ss.getSquareName());
			} else {
				System.out.printf("\nYou are on %s.", ss.getSquareName());
			}
			System.out.print("\n");
			return new Triplet<>(ss, paid, auctioned);
		} else {
			// if player lands on not ownable square
			System.out.print("\n" + square.getMessage());
			System.out.printf("\nYou are on %s. It can't be owned.\n", square.getSquareName());
			return new Triplet<>(null, paid, auctioned);
		}
	}

	/**
	 * Loading screen containing slight pause, can optionally display dots
	 *
	 * @param time the time to delay
	 */
	public static void loading(int time, boolean withDots) {
		try {
			for (int i = 0; i < time; i++) {
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
	 * Outputs game rules as requested by user via an options menu
	 *
	 * @param scanner the scanner
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
		basicGameRules.add("Basic Game Rules:");
		basicGameRules.add("The aim is to help NASA complete its mission by fully developing all mission-critical Systems");
		basicGameRules.add("When it's your go, pick what you'd like to do from the menu.");
		basicGameRules.add("e.g. Roll the dice to move along the board.");

		// buying and selling
		buyingSellingRules.add("Rules for Purchasing and Selling:");
		buyingSellingRules.add("You'll each be allotted some credits (the currency of the solar system) to start out.");
		buyingSellingRules.add("Use your credits to purchase a square that you land on or pay other players when you land on their square.");
		buyingSellingRules.add("If you don't want to purchase the square you land on, it will be auctioned to the other players.");

		// developing systems
		developmentRules.add("Rules for Developing Systems:");
		developmentRules.add("The board has 12 squares in total grouped into " + systemNames.size() + " systems: "
				+ Arrays.toString(systemNames.toArray()));
		developmentRules.add("Systems and their squares get more expensive the further you are along the board...");
		developmentRules.add("There's also bigger rewards should another player land on your square.");
		developmentRules.add("Once you own a whole system, you can pay to add a development, but only if you can pass a mini-challenge first!");

		// ending the game
		endingRules.add("Rules for Ending the Game:");
		endingRules.add("All systems must be developed to complete the mission and win the game.");
		endingRules.add("Should any player go 'Bankrupt' by running out of credits, the game ends and the mission has failed.");

		// join separate arrayLists into one
		List<String> combinedRuleSets = Stream.of(basicGameRules, buyingSellingRules, developmentRules, endingRules)
				.flatMap(Collection::stream).collect(Collectors.toList());

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
	 * Displays current state of the board (i.e. which squares are owned by which
	 * players)
	 */
	public static void displayBoardState(final List<Player> players) {
		System.out.printf("%-4s  %-28s  %-16s  %-20s\n", "[No]", "[Square Name]", "[Owner]", "[Player Position]");
		SystemName prevSystem = null;
		for (Square s : squares) {
			String owner;
			SystemSquare ss = null;
			if (s instanceof SystemSquare) {
				// find squares owned by player
				Player o = players.stream().filter(p -> p.getOwnedSquares().contains(s)).findAny().orElse(null);
				ss = (SystemSquare) s;
				owner = o != null ? o.getName() + " (Dev " + ss.getDevelopment() + ")" : "";
			} else {
				// if square is not ownable
				owner = "Can't be owned";
			}
			 // concatenate list of player names on square
			String landedPlayers = players.stream().filter(p -> p.getPosition() == s.getPosition()).map(Player::getName)
					.collect(Collectors.joining(", "));

			if (landedPlayers.length() - 3 > 20) {
				// truncate string if over 20 chars, as a player may use a long name or multiple players land on the same square
				landedPlayers = landedPlayers.substring(0, 17) + "...";
			}

			if (!(s instanceof SystemSquare) || !(ss.getSystemNameEnum().equals(prevSystem))) {
				System.out.println("----------------------------------------------------------------------------");
				// print out system name if system nam changes between printed squares (ie when new system is obout to be printed)
				if (ss != null && !(ss.getSystemNameEnum().equals(prevSystem))) {
					System.out.printf("%s\n", ss.getSystemNameString().toUpperCase());
				}
			}
			int pos = s.getPosition() + 1;
			System.out.printf("%-4s  %-28s  %-16s  %-20s\n", "[" + pos + "]", "[" + s.getSquareName() + "]",
					"[" + owner + "]", "[" + landedPlayers + "]");

			if (ss != null) {
				prevSystem = ss.getSystemNameEnum();
			}
		}
		System.out.print("\nPress enter to return to the main menu.");
	}

	/**
	 * Roll two virtual, weighted d-n dice and return two numbers
	 * The probabilities for rolling two fair dice approximate a normal distribution, making n+1 the most likely outcome
	 * For 2d6, the most likely roll is 7, for 2d3, it is 4.
	 * With these 'weighted' dice, any roll has equal probability
	 * i.e. if the argument is n, the probability of any roll is 1/n
	 * The probability is the same as if a single 2*d sided dice was rolled
	 *
	 * @return two-element integer array
	 */
	public static int[] rollDice(int d) {
		int lowest = 2;
		// generate sum of two rolls
		int rollSum = rand.nextInt(2 * d - lowest + 1) + lowest;
		// generate split index
		int split = rand.nextInt(rollSum + 1 - lowest) + lowest;
		int[] roll = new int[2];
		// calculate two random rolls from a single roll and split location
		roll[0] = split - (lowest - 1);
		roll[1] = (rollSum + 1) - split;
		return roll;
	}

	/**
	 * Handles purchasing of a square
	 * @param ss the square to be purchased
	 * @param player the current player
	 * @throws BankruptcyException throws whena  player runs out of resources
	 */
	public static void purchaseSquare(Scanner scanner, SystemSquare ss, Player player) throws BankruptcyException {
		// if player is purchasing their last square, load a quiz
		if (player.getOwnedSquares().stream().filter(s -> s.getSystemNameEnum().equals(ss.getSystemNameEnum())).count() == ss.getSystemType() - 1) {
			System.out.print("To purchase your last element, you have to pass a quiz. Loading");
			loading(5, true);
			// if player passes quiz, continue to purchase as normal
			if (Quiz.generateQuestions(scanner, ss.getQuizDifficulty())) {
				System.out.println("Well done! You passed the quiz.");
			} else {
				// if player fails quiz, return without purchasing
				System.out.print("You didn't pass the quiz. Better luck next time");
				loading(3, true);
				return;
			}
		}
		player.purchaseSquare(ss);
		System.out.printf("Purchasing %s for %d credits for %s", ss.getSquareName(), ss.getBaseCost(), player.getName());
		loading(3, true);
		System.out.printf("\nYour new balance is %d credits", player.getPlayerResources());
		loading(2, false);
	}

	/**
	 * Determines if a square is to be auctioned
	 *
	 * @param ss     the system square the current player is on
	 * @param player the current player
	 * @return whether the square can be auctioned or not
	 */
	public static boolean isAuctionable(final SystemSquare ss, final Player player, final List<Player> players) {
		return players.stream().filter(p -> !p.equals(player)).anyMatch(b -> b.getPlayerResources() > ss.getBaseCost());
	}

	/**
	 * Auctions a square to other players
	 *
	 * @param square the square to auction
	 */
	public static void auctionSquare(Scanner scanner, final SystemSquare square, final Player player,
			final List<Player> players) throws BankruptcyException {
		// copy players into new arraylist and remove player from bidders list
		ArrayList<Player> bidders = new ArrayList<>(players);
		bidders.remove(player);
		int highestBid = square.getBaseCost();
		boolean biddingEnded = false;
		int prevBid = 0;
		Player highestBidder = null;
		do {
			Iterator<Player> i = bidders.iterator();
			if (bidders.size() == 0) {
				// break outer while loop when bidding is over
				biddingEnded = true;
			}
			while (i.hasNext()) {
				Player bidder = i.next();
				if (bidder.getPlayerResources() < highestBid) {
					// remove bidder if they can't outbid the current bid
					i.remove();
				} else {
					String names = bidders.stream().map(Player::getName).collect(Collectors.joining(", "));
					clearScreen();
					// check if current bidder is the highest bidder (ie all other bidders have not outbid the highest bidder) - break if so
					if (bidder.equals(highestBidder)) {
						biddingEnded = true;
						break;
					}
					System.out.printf("Bidding on %s, starting at %d. (Eligible bidders: %s)\n", square.getSquareName(), square.getBaseCost(), names);
					if (highestBidder != null) {
						System.out.printf("%s is the highest bidder at %d credits\n", highestBidder.getName(), highestBid);
					}
					System.out.printf("%s, please enter your bid or # to skip.\n", bidder.getName());

					int bid = scanIntInput(scanner, square.getBaseCost() + (highestBid == square.getBaseCost() ? 0 : 1),
							bidder.getPlayerResources(), true);
					if (bid > prevBid) {
						// update bidder and bid
						highestBid = bid;
						highestBidder = bidder;
					} else if (bid == -1) {
						// remove bidder if they skip the bid
						i.remove();
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
	 * Allows Players to choose any of their owned squares to develop.
	 *
	 * @param scanner the scanner
	 * @param player  the current player
	 */
	public static void buyDevelopments(Scanner scanner, final Player player) {
		ArrayList<SystemSquare> squares = player.getOwnedSquares();
		ArrayList<SystemName> systems = player.getDevelopableSystems();

		// remove incomplete systems using predicate
		squares.removeIf(s -> !systems.contains(s.getSystemNameEnum()));

		System.out.printf("You have %d credits\n", player.getPlayerResources());
		System.out.println("Enter a square to develop. Enter # to cancel.");
		int count = 1;
		boolean valid = false;
		for (SystemSquare square : squares) {
			System.out.printf("%d. %s [%d] - %d credits per dev.\n", count++, square.getSquareName(), square.getDevelopment(), square.getCostPerDevelopment());
		}
		int squareNum = scanIntInput(scanner, 1, squares.size(), true);
		if (squareNum > 0) {
			SystemSquare chosenSquare = squares.get(squareNum - 1);
			System.out.println("Enter how many developments to add. Enter # to cancel.");
			do {
				try {
					int maxDevToAdd = chosenSquare.getMaxDevelopment() - chosenSquare.getDevelopment();
					int chosenDevToAdd = scanIntInput(scanner, 1, maxDevToAdd, true);
					if (chosenDevToAdd > 0) {
						// check if other squares in the chosen system are fully developed
						boolean otherSquaresFullyDev = player.getOwnedSquares().stream()
								.filter(s -> s.getSystemNameEnum().equals(chosenSquare.getSystemNameEnum()))
								.filter(s -> s.getDevelopment() == s.getMaxDevelopment())
								.count() == chosenSquare.getSystemType() - 1;
						// if player is attempting to fully develop a system, load a quiz
						if (otherSquaresFullyDev && chosenDevToAdd == maxDevToAdd) {
							System.out.print("To purchase your last development, you have to pass a quiz. Loading");
							loading(5, true);
							if (Quiz.generateQuestions(scanner, chosenSquare.getQuizDifficulty())) {
								// if player passes, continue
								System.out.println("Well done! You passed the quiz.");
							} else {
								// if player fails, break and don't add any developments
								System.out.print("You didn't pass the quiz. Better luck next time");
								loading(3, true);
								return;
							}
						}
						player.developSquare(chosenSquare, chosenDevToAdd);
						valid = true;
						System.out.printf("Developing %s with %d development(s) at %d each", chosenSquare.getSquareName(), chosenDevToAdd, chosenSquare.getCostPerDevelopment());
						loading(3, true);
						System.out.printf("\nYour new balance is %d credits", player.getPlayerResources());
						loading(2, false);
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
	 * Allows the user to choose to sell developments, mortgage a property or pay
	 * off the mortgage on a property
	 * 
	 * @param scanner the scanner
	 * @param player  the current player
	 */
	public static void bankMenu(Scanner scanner, final Player player) throws BankruptcyException {
		System.out.println("Welcome to the space bank. Here you can sell developments, mortgage an element or pay off a mortgage. Please select an option or enter # to cancel.");
		HashMap<Integer, Integer> bankOptions = new HashMap<>();
		String[] bankMenu = new String[3];
		bankMenu[0] = "Sell Developments";
		bankMenu[1] = "Mortgage Element";
		bankMenu[2] = "Pay Off Mortgage";

		int menuNum = 0;
		for (int i = 0; i < bankMenu.length; i++) {
			// if no developments, skip sell developments
			if (i == 0 && !player.hasDevelopments()) {
				continue;
			}
			// if no mortgageable squares, skip mortgage element
			if (i == 1 && !player.hasMortgageableSquares()) {
				continue;
			}
			// if no mortgaged squares or player can't pay off any mortgaged squares, skip pay off mortgage
			if (i == 2 && !player.hasMortgagedSquares()
					&& player.getOwnedSquares().stream().filter(SystemSquare::isMortgaged)
							.noneMatch(s -> player.getPlayerResources() > (int) (s.getBaseCost() * 1.1))) {
				continue;
			}
			menuNum++;
			System.out.println(menuNum + ". " + bankMenu[i]);
			bankOptions.put(menuNum, i + 1);
		}

		int userOption = scanIntInput(scanner, 1, menuNum, true);

		if (userOption < 0) {
			// break if user cancels
			return;
		}

		switch (bankOptions.get(userOption)) {
		case 1:
			sellDevelopments(scanner, player);
			break;
		case 2:
			mortgageSquare(scanner, player);
			break;
		case 3:
			payOffMortgage(scanner, player);
		}
	}

	/**
	 * generates a menu to allow a player to sell developments at half price
	 * 
	 * @param scanner scanner
	 * @param player  the current player
	 * @throws BankruptcyException thrown if a player goes bankrupt
	 */
	public static void sellDevelopments(Scanner scanner, final Player player) throws BankruptcyException {
		clearScreen();
		ArrayList<SystemSquare> developedSquares = new ArrayList<>(player.getOwnedSquares());
		// remove squares with no developments
		developedSquares.removeIf(s -> s.getDevelopment() == 0);
		int count = 1;
		System.out.print("Enter an element to sell development from. Enter # to cancel.");
		for (SystemSquare s : developedSquares) {
			System.out.printf("%d. %s (%d credits per development)\n", count++, s.getSquareName(), (int) (s.getCostPerDevelopment() * 0.5));
		}
		int option = scanIntInput(scanner, 1, developedSquares.size(), true);
		if (option > 0) {
			SystemSquare s = developedSquares.get(option - 1);
			System.out.print("Enter how many developments to sell. Enter # to cancel.\n");
			int dev = scanIntInput(scanner, 1, s.getDevelopment(), true);
			if (dev > 0) {
				// add resources to player at half the price of developments, and remove developments
				player.addResources((int) (0.5 * s.getCostPerDevelopment()));
				player.developSquare(s, -1 * dev);
				System.out.printf("You have sold %s developments for a total of %s", dev, dev * (int) (s.getCostPerDevelopment() * 0.5));
				loading(3, true);
			}
		}
	}

	/**
	 * allows a player to mortgage an undeveloped squares
	 * 
	 * @param scanner scanner
	 * @param player  player arraylist
	 */
	public static void mortgageSquare(Scanner scanner, final Player player) throws BankruptcyException {
		ArrayList<SystemSquare> undevelopedSquares = new ArrayList<>();
		int count = 1;
		System.out.println("Enter an element to mortgage. Enter # to cancel.");
		for (SystemSquare s : player.getOwnedSquares()) {
			if (s.getDevelopment() == 0) {
				System.out.printf("%d. %s (%d)\n", count++, s.getSquareName(), s.getBaseCost());
				undevelopedSquares.add(s);
			}
		}
		int option = scanIntInput(scanner, 1, undevelopedSquares.size(), true);
		if (option > 0) {
			// set mortgaged to true on chosen square, and update player resources
			SystemSquare s = undevelopedSquares.get(option - 1);
			s.setMortgaged(true);
			player.addResources(s.getBaseCost());
			System.out.printf("You have mortgaged %s for %d credits. You can buy it back for %d credits.",
					s.getSquareName(), s.getBaseCost(), (int) (1.1 * s.getBaseCost()));
			loading(3, true);
		}
	}

	/**
	 * allows a player to pay off a mortgaged square
	 * 
	 * @param scanner scanner
	 * @param player  the current player
	 * @throws BankruptcyException
	 */
	public static void payOffMortgage(Scanner scanner, final Player player) throws BankruptcyException {
		System.out.println("Which element would you like to pay off the mortgage on?");
		// get squares that are mortgaged and can be paid off by the player
		List<SystemSquare> mortgaged = player.getOwnedSquares().stream().filter(SystemSquare::isMortgaged)
				.filter(s -> player.getPlayerResources() > (int) (1.1 * s.getBaseCost())).collect(Collectors.toList());
		int count = 1;
		for (SystemSquare s : mortgaged) {
			System.out.printf("%d. %s\n", count, s.getSquareName());
		}
		int mortgagedOption = scanIntInput(scanner, 1, mortgaged.size(), true);
		if (mortgagedOption < 0) {
			// break if user cancels
			return;
		}
		// set square to not mortgaged and deduct players resources at 110% of base cost of square
		SystemSquare m = mortgaged.get(mortgagedOption - 1);
		player.addResources((int) (-1.1 * m.getBaseCost()));
		m.setMortgaged(false);
	}

	/**
	 * allows a player to sell an undeveloped square to another player for
	 * resources or squares
	 * 
	 * @param scanner the scanner
	 * @param player  the current player
	 */
	public static void tradeWithPlayer(Scanner scanner, final Player player, final List<Player> players) throws BankruptcyException {
		ArrayList<Player> buyers = new ArrayList<>(players);
		// remove current player from buyer
		buyers.remove(player);
		ArrayList<SystemSquare> sellerUndevelopedSquares = new ArrayList<>(player.getOwnedSquares());
		// remove developed squares from tradeable squares
		sellerUndevelopedSquares.removeIf(s -> s.getDevelopment() != 0);

		int count;
		int userOption;
		int optionNum = sellerUndevelopedSquares.size() + 1;
		int maxEntry = sellerUndevelopedSquares.size();
		ArrayList<SystemSquare> sellerSquares = new ArrayList<>();
		do {
			count = 1;
			System.out.println("Enter an undeveloped element(s) to sell. Select continue to finalise selection if there are multiple. Enter # to cancel at any time.");
			for (SystemSquare s : sellerUndevelopedSquares) {
				System.out.printf("%d. %s (%d credits)", count++, s.getSquareName(), s.getBaseCost());
				System.out.print(s.isMortgaged() ? " - mortgaged" : "" + "\n");
			}
			if (sellerSquares.size() > 0) {
				System.out.printf("%d. Continue\n", count);
				maxEntry++;
			}

			userOption = scanIntInput(scanner, 1, maxEntry, true);
			if (userOption < 0) {
				return;
			} else if (userOption > 0 && userOption < optionNum) {
				// add chosen square to seller array
				SystemSquare squareToTrade = sellerUndevelopedSquares.get(userOption - 1);
				sellerSquares.add(squareToTrade);
				sellerUndevelopedSquares.remove(squareToTrade);
				optionNum--;
			}
		} while (userOption < optionNum);

		int paymentMethod;
		// if no other player owns squares, skip option to trade for elements
		if (buyers.stream().anyMatch(s -> s.getOwnedSquares().size() > 0)) {
			clearScreen();
			System.out.print("What would you like to sell the element for?\n1. Credits\n2. An element(s)\n");

			paymentMethod = scanIntInput(scanner, 1, 2, true);
			if (paymentMethod < 0) {
				return;
			}
		} else {
			// set payment method to credits
			paymentMethod = 1;
		}

		if (paymentMethod == 2) {
			// remove buyers with no squares
			buyers.removeIf(b -> b.getOwnedSquares().size() == 0);
		}

		clearScreen();
		System.out.print("Who would you like to sell/trade the element to?\n");
		count = 1;
		for (Player buyer : buyers) {
			System.out.printf("%d. %s\n", count++, buyer.getName());
		}

		int buyOption = scanIntInput(scanner, 1, buyers.size(), true);
		if (buyOption < 0) {
			return;
		}

		Player buyer = buyers.get(buyOption - 1);

		if (paymentMethod == 1) {
			clearScreen();
			System.out.println("Enter your agreed price for the element.");
			int cost = scanIntInput(scanner, 0, buyer.getPlayerResources(), true);
			if (cost < 0) {
				return;
			}
			System.out.println("The trade will occur in 10 seconds. Press enter to cancel.");
			// if input timer returns true (completes without interruption)
			if (inputTimer(10)) {
				player.addResources(cost);
				for (SystemSquare ss : sellerSquares) {
					// loop through seller array and transfer
					player.removeSquare(ss);
					buyer.purchaseSquare(ss, cost);
				}
			} else {
				// consume new line char
				scanner.nextLine();
			}
		} else if (paymentMethod == 2) {
			int squareOption;
			optionNum = buyer.getOwnedSquares().size() + 1;
			ArrayList<SystemSquare> buyerSquares = new ArrayList<>();
			ArrayList<SystemSquare> buyerUndevelopedSquares = buyer.getOwnedSquares();
			do {
				clearScreen();
				count = 1;
				System.out.printf("Enter which element(s) %s will give to %s. Select continue to finalise selection if there are multiple..\n", buyer.getName(), player.getName());
				for (SystemSquare ss : buyerUndevelopedSquares) {
					System.out.printf("%d. %s (%d credits)", count++, ss.getSquareName(), ss.getBaseCost());
					System.out.print(ss.isMortgaged() ? " - mortgaged" : "" + "\n");
				}
				System.out.printf("%d. Continue\n", count);
				squareOption = scanIntInput(scanner, 1, optionNum, true);
				if (squareOption < 0) {
					return;
				} else if (squareOption != optionNum) {
					// add square to buyer array
					SystemSquare squareToTrade = buyerUndevelopedSquares.get(squareOption - 1);
					buyerSquares.add(squareToTrade);
					buyerUndevelopedSquares.remove(squareToTrade);
					optionNum--;
				}
			} while (squareOption < optionNum);
			System.out.println("The trade will occur in 10 seconds. Press enter to cancel.");
			// if input timer returns true (completes without interruption)
			if (inputTimer(10)) {
				// loop through seller array and transfer resources
				for (SystemSquare square : buyerSquares) {
					buyer.removeSquare(square);
					player.purchaseSquare(square, 0);
				}
				// loop through buyer array and transfer resources
				for (SystemSquare ss : sellerSquares) {
					buyer.purchaseSquare(ss, 0);
					player.removeSquare(ss);
				}
			} else {
				// consume new line char
				scanner.nextLine();
			}
		}
	}

	/**
	 * get the players (that aren't the current player) that have resources < {@value BANKRUPTCY_RISK}
	 *
	 * @param player  the current player
	 * @param players all players
	 * @return an arraylist of players that have resources <
	 *         {@value BANKRUPTCY_RISK}
	 */
	public static List<Player> getPlayersNearBankruptcy(final Player player, final List<Player> players) {
		List<Player> playersNearBankruptcy = new ArrayList<>(players);
		// remove current player
		playersNearBankruptcy.remove(player);
		// remove if greater than bankruptcy risk value
		playersNearBankruptcy.removeIf(s -> s.getPlayerResources() >= BANKRUPTCY_RISK);
		return playersNearBankruptcy;
	}

	/**
	 * allows a player to make a donation to another player
	 * 
	 * @param scanner scanner
	 * @param player  the current player
	 * @param players all players
	 * @throws BankruptcyException this exception will never be thrown as user input
	 *                             is limited
	 */
	public static void makeDonation(Scanner scanner, final Player player, final List<Player> players) throws BankruptcyException {
		List<Player> recipients = getPlayersNearBankruptcy(player, players);
		Player recipient;
		if (recipients.size() == 1) {
			// if only one player is going bankrupt
			recipient = recipients.get(0);
		} else {
			System.out.println("Which player would you like to donate to?");
			int count = 1;
			for (Player r : recipients) {
				System.out.printf("%d. %s\n", count++, r.getName());
			}
			int option = scanIntInput(scanner, 1, recipients.size(), true);
			if (option > 0) {
				recipient = recipients.get(option - 1);
			} else {
				return;
			}
		}
		System.out.printf("How much would you like to donate to %s\n", recipient.getName());
		int resources = scanIntInput(scanner, MIN_BANKRUPTCY_DONATION, player.getPlayerResources() - MIN_BANKRUPTCY_DONATION, true);
		if (resources > 0) {
			// update resources
			recipient.addResources(resources);
			player.addResources(-1 * resources);
			System.out.printf("You have donated %d credits to %s", resources, recipient.getName());
			loading(3, true);
		}
	}

	/**
	 * Handles successful completion of the game, shows all player in order of total net worth.
	 * If two player have the same net worth, they will be ranked by number of owned elements
	 * @param players all the players
	 */
	public static void epilogue(final List<Player> players) {
		//sort winners using CompareWinners comparator
		List<Player> winners = new ArrayList<>(players);
		winners.sort(new CompareWinners());

		System.out.print("Congratulations on completing the ArtemisLite mission!");
		loading(1, true);
		System.out.print("Space exploration is reaching new frontiers thanks to your crew's valiant efforts");
		loading(1, true);
		System.out.println("You're all winners but here's the final state of play:");
		loading(3, true);

		// loop through all players
		for (Player p : winners) {
			System.out.printf("\n%s ended the game with a total net value of %d credits:\n", p.getName(), calculateNetWorth(p));

			System.out.printf("\t--- Credits ---\n\t%d\n", p.getPlayerResources());

			System.out.println("\t--- Elements Owned ---\t");
			// iterate owned system squares
			if (p.getOwnedSquares().size() == 0) {
				System.out.printf("\t%s owned no elements\n", p.getName());
			} else {
				// concatenate owned system square names
				String owned = p.getOwnedSquares().stream().map(SystemSquare::getSquareName).collect(Collectors.joining(", "));
				System.out.println("\t" + owned);
			}
		}
		// Ending message
		System.out.println("\nThank you for playing Artemis Lite.");
	}

	/**
	 * Calculates net worth of all players (base cost of square + (cost_per_dev *playerResources))
	 *
	 * @return the total net worth of the player
	 */
	public static int calculateNetWorth(Player player) {
		int netWorth = player.getPlayerResources();
		// loop through each list of owned squares
		for (SystemSquare s : player.getOwnedSquares()) {
			// base cost + (cost_per_dev*4) + playerResources
			netWorth += s.isMortgaged() ? 0 : (s.getBaseCost() + s.getCostPerDevelopment() * s.getDevelopment());
		}
		return netWorth;
	}

	interface IExecCloseable extends AutoCloseable {
		void close();
	}

	/**
	 * starts a timer that can be interrupted by the user
	 * 
	 * @return whether the timer stopped gracefully (true) or was interrupted (false)
	 */
	public static boolean inputTimer(int time) {
		// create ExecutorService on single thread
		ExecutorService ex = Executors.newSingleThreadExecutor();
		// autoclose ExecutorService when complete using AutoClose interface
		try (IExecCloseable ignored = ex::shutdownNow) {
			// create input timer object using ExecutorService
			Future<Void> inputTimer = ex.submit(() -> {
				while (!new BufferedReader(new InputStreamReader(System.in)).ready()) {
					// check bufferedreader every 100ms
					Thread.sleep(100);
				}
				// return null as using runnable instead of callable due to ExecutorService
				// return value is unused but must be implemented
				return null;
			});
			try {
				// run input timer
				inputTimer.get(time, TimeUnit.SECONDS);
				// if inputTimer interrupted by user
				return false;
			} catch (TimeoutException e) {
				// if inputTimer times out (ie completes successfully)
				inputTimer.cancel(true);
				return true;
			} catch (InterruptedException | ExecutionException e) {
				return false;
			}
		}
	}

	/**
	 *
	 * @param en the enum class to be stringified
	 * @return array of stringified enums
	 */
	public static <E extends Enum<E>> ArrayList<String> stringifyEnum(Class<E> en) {
		// get all values in passed enum class
		EnumSet<E> enums = EnumSet.allOf(en);
		ArrayList<String> strEnums = new ArrayList<>();
		// pass each enum value to stringifyEnum and add to arrayList using iterator
		enums.iterator().forEachRemaining(s -> strEnums.add(stringifyEnum(s)));
		return strEnums;
	}

	/**
	 *
	 * @param en the enum to be stringified
	 * @return the enum as a user friendly string
	 */
	public static <E extends Enum<E>> String stringifyEnum(E en) {
		// overloaded method means either enum value or class can be stringified
		// split enum value on underscore, convert to lowercase and capitalise first letter
		return Arrays.stream(en.name().toLowerCase().split("_"))
				.map(s -> s.substring(0, 1).toUpperCase() + s.substring(1))
				.collect(Collectors.joining(" "));
	}

	/**
	 * Clears console of any text
	 */
	public static void clearScreen() {
		try {
			if (System.getProperty("os.name").contains("Windows")) {
				// enters cmd command to clear screen
				new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
			} else {
				// for unix devices, use ASCII escape code to clear screen
				// the clear command in a unix terminal uses the same escape code
				System.out.print("\033[H\033[2J");
				System.out.flush();
			}
		} catch (Exception e) {
			System.out.println("Error clearing console.");
		}
	}
}
