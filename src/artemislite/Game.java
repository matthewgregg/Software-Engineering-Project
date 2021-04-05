package artemislite;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Game {

	private static final Random rand = new Random();
	private static List<Player> players;
	private static final Scanner scanner = new Scanner(System.in);
	//scanner cannot be closed and then reused

	SetupGame gameSetup = new SetupGame();
	
	public static void main(String[] args) {
		boolean isGameOver = false;
		boolean quitGame;

		players = new ArrayList<>(SetupGame.playerCreation(scanner));

		clearScreen();

		System.out.println(welcomeMessage(players));
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			System.out.println("Thread error");
		}
		int playerCount = 0;
		do {
			clearScreen();
			playerCount++;
			if (playerCount > players.size()) {
				playerCount = 1;
			}
			System.out.printf("%s's turn\n", players.get(playerCount - 1).getName());
			quitGame = !currentPlayerMenu(scanner);
		} while(!isGameOver && !quitGame);

		if (quitGame) {
			clearScreen();
			System.out.printf("Game is over! %s quit the game\n", players.get(playerCount - 1).getName());
		}
	}

	public static boolean currentPlayerMenu(Scanner scanner) {
		String option = "";
		int userOption = 0;
		boolean turnFinished = false;

		System.out.println("Menu");
		System.out.println("1. Display Resources");
		System.out.println("2. Roll Dice");
		System.out.println("3. Purchase Square");
		System.out.println("4. Purchase Element");
		System.out.println("5. Develop Element");
		System.out.println("6. Finish Turn");
		System.out.println("7. Quit Game");
		System.out.println("Enter option ");

		do {
			boolean valid = false;
			//TODO remove nested while loop
			do {
				try {
					option = scanner.nextLine();
					if (Integer.parseInt(option) >= 1 && Integer.parseInt(option) <= 7) {
						valid = true;
						userOption = Integer.parseInt(option);
					} else {
						throw new NumberFormatException();
					}
				} catch (NumberFormatException e) {
					System.out.println("Please enter a number between 1 and 7.");
				}
			} while (!valid);

			switch (userOption) {
				case 1:

					break;
				case 2:
					int[] roll = rollDice();
					System.out.printf("You rolled a %d and a %d\n", roll[0], roll[1]);
					break;
				case 3:

					break;
				case 4:

					break;
				case 5:

					break;
				case 6:
					turnFinished = true;
					break;
				case 7:
					System.out.println("Quitting");
					break;
			}
		} while (!turnFinished && userOption != 7);
		return turnFinished;
	}
	
	
	public static int[] rollDice() {
		int[] roll = new int[2];
		roll[0] = rand.nextInt(6) + 1;
		roll[1] = rand.nextInt(6) + 1;
		
		return roll;
	}

	public static StringBuilder welcomeMessage(List<Player> players) {
		StringBuilder welcome = new StringBuilder();
		for(int i = players.size(); i > 0; i--) {
			welcome.insert(0, players.get(i-1).getName());
			if (i == players.size()) {
				welcome.insert(0, " and ");
			} else if (i > 1) {
				welcome.insert(0,", ");
			}
		}
		welcome.append(".");
		welcome.insert(0, "Welcome to ArtemisLite, ");
		return welcome;
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
		}
		catch (final Exception e) {
			System.out.println("Error clearing console.");
		}
	}
}
