package artemislite;

import java.util.Random;
import java.util.Scanner;

public class Game {

	protected static boolean isGameOver = false;
	protected static boolean quitGame = false;
	private static Random rand = new Random();
	private static Scanner scanner = new Scanner(System.in);
	
	SetupGame gameSetup = new SetupGame();
	
	public static void main(String[] args) {

		new Game();
		
		System.out.println("Welcome to Artemislite");
		
		while(!isGameOver) {
			while(!quitGame) {
				currentPlayerMenu();
				
			
			}
			System.out.println("I've Quit the Game");
			
			isGameOver = true;
		}
		System.out.println("Game is Over! There is a winner");
		
		
	}
		
	public Game() {
		
		gameSetup.playerCreation();
		
		System.out.println("Players Added");
		for(Player play : gameSetup.players) {
			System.out.print("[ Name: " + play.getName() + " - ID: " + play.getPlayerID() + " ] ");
		}
	}
	
	public static void currentPlayerMenu() {
		int userOption;
		
		do {
			System.out.println("Menu");
			System.out.println("1. Display Resources");
			System.out.println("2. Roll Dice");
			System.out.println("3. Purchase Square");
			System.out.println("4. Purchase Element");
			System.out.println("5. Develop Element");
			System.out.println("6. Finish Turn");
			System.out.println("7. Quit Game");
			System.out.println("Enter option ");
			
			userOption = scanner.nextInt();

			switch (userOption) {
			case 1:
				
				break;
			case 2:
				int[] roll = rollDice();
				System.out.println("Dice 1 : " +roll[0]);
				System.out.println("Dice 2 : " +roll[1]);
				break;
			case 3:
				
				break;
			case 4:
				
				break;
			case 5:
				
				break;
			case 6:
				
				break;
			case 7:
				quitGame = true;
				
				System.out.println("Quitting");
				break;
			default:
				System.out.println("Done");
			}

		} while (userOption != 7);
		//scanner.close();
	}
	
	
	public static int[] rollDice() {
		int[] roll = new int[2];
		roll[0] = rand.nextInt(6) + 1;
		roll[1] = rand.nextInt(6) + 1;
		
		return roll;
	}
}
