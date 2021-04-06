package artemislite;

import javax.naming.ConfigurationException;
import java.util.ArrayList;

/**
 * represents a player in the system
 */
public class Player extends Actor {
    private static final int MIN_PLAYER_ID = 1;
    private static final int MAX_PLAYER_ID = 4;
    private static final String INVALID_PLAYER_ID = "Invalid player ID";
    private final int playerID;
    private final String name;
    private ArrayList<SystemSquare> ownedElements = new ArrayList<>();
    private int playerResources;

    /**
     * constructor with arguments
     * @param playerID the playerID
     * @param name the player's name
     */
    public Player(int playerID, String name) throws ConfigurationException {
        if (name.equalsIgnoreCase("quit")) {
            throw new ConfigurationException();
        } else {
            this.playerID = playerID;
        }
        this.name = name;
        this.playerResources = 500;
    }
    
    /**
	 * @return the playerID
	 */
	public int getPlayerID() {
		return playerID;
	}

	/**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the playerResources
     */
    public int getPlayerResources() {
        return playerResources;
    }

    /**
     * @param playerResources the playerResources to set
     */
    public void setPlayerResources(int playerResources) {
        this.playerResources = playerResources;
    }

    /**
     * @return the ownedElements
     */
    public ArrayList<SystemSquare> getOwnedElements() {
        return ownedElements;
    }

    /**
     * gets the player role based on playerID
     * @return the player role
     * @throws IllegalArgumentException if playerID outside bounds
     */
    public String getPlayerRole() throws IllegalArgumentException {
        if (this.playerID >= MIN_PLAYER_ID && this.playerID <= MAX_PLAYER_ID) {
            switch(this.playerID) {
                case 1:
                    return "One";
                case 2:
                    return "Two";
                case 3:
                    return "Three";
                case 4:
                    return "Four";
            }
        } else {
            throw new IllegalArgumentException(INVALID_PLAYER_ID);
        }
        return null;
    }

    /**
     * adds a SystemSquare to the player's element array
     * @param square the square to be added
     */
    public void updateOwnedElements(SystemSquare square) {
        ownedElements.add(square);
    }
}
