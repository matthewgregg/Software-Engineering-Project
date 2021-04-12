package artemislite;

import javax.naming.InvalidNameException;
import java.util.*;

/**
 * represents a player in the system
 */
public class Player extends Actor {
    private static final int MIN_PLAYER_ID = 1;
    private static final int MAX_PLAYER_ID = 4;
    private static final String INVALID_PLAYER_ID = "Invalid player ID";
    private final int playerID;
    private final String name;
    private final SortedSet<SystemSquare> ownedElements = new TreeSet<>(new ComparePosition());
    private int playerResources;

    /**
     * constructor with arguments
     * @param playerID the playerID
     * @param name the player's name
     */
    public Player(int playerID, String name) throws InvalidNameException {
        super(0);
        final String[] INVALID_NAMES = new String[]{"", "quit"};
        if (Arrays.asList(INVALID_NAMES).contains(name)) {
            throw new InvalidNameException();
        } else {
            this.playerID = playerID;
            this.name = name;
            this.playerResources = 500;
        }
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
        return new ArrayList<>(ownedElements);
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
                    return "Commander";
                case 2:
                    return "Command Module Pilot";
                case 3:
                    return "Lunar Module Pilot";
                case 4:
                    return "Docking Module Pilot";
            }
        } else {
            throw new IllegalArgumentException(INVALID_PLAYER_ID);
        }
        return null;
    }

    /**
     * updates a player's resources
     * @param delta the change in resources
     */
    public void addResources(int delta) throws BankruptcyException {
        int res = this.playerResources + delta;
        if (res >= 0) {
            setPlayerResources(this.playerResources + delta);
        } else {
            throw new BankruptcyException("You've gone bankrupt");
        }
    }

    /**
     * develops a square
     * @param square the square to be developed
     */
    public void developElement(SystemSquare square, int devDelta) throws IllegalArgumentException, BankruptcyException {
        try {
            this.addResources(devDelta * square.getCostPerDevelopment() * -1 * (int) (devDelta > 0 ? 1 : 0.5));
            square.setDevelopment(devDelta+square.getDevelopment());
        } catch (IllegalArgumentException e) {
            //undo payment
            this.addResources(devDelta * square.getCostPerDevelopment());
            throw new IllegalArgumentException();
        }
    }

    /**
     * purchases a square
     * @param square the square to be purchased
     */
    public void purchaseSquare(SystemSquare square) throws BankruptcyException {
        this.ownedElements.add(square);
        square.setOwned(true);
        //this.setPlayerResources(this.playerResources - square.getBaseCost());
        if (!square.isMortgaged()) {
            this.addResources(-1 * square.getBaseCost());
        } else {
            this.addResources((int) -1.1 * square.getBaseCost());
            square.setMortgaged(false);
        }
    }

    /**
     * purchases a square
     * @param square the square to be purchased
     */
    public void purchaseSquare(SystemSquare square, int cost) throws BankruptcyException {
        this.ownedElements.add(square);
        //this.setPlayerResources(this.playerResources - square.getBaseCost());
        this.addResources(-1 * cost);
    }

    /**
     * removes a square from a players collection
     * @param square the square to remove
     */
    public void removeSquare(SystemSquare square) {
        this.ownedElements.remove(square);
        square.setOwned(false);
    }

    /**
     * get the cost of the cheapest development
     * @return the cost of the cheapest development
     */
    public int getMinimumOwnedDevCost() {
        /*
        int lowest = this.ownedElements.get(0).getCostPerDevelopment();
        for (SystemSquare ss : this.ownedElements) {
            if (ss.getCostPerDevelopment() < lowest) {
                lowest = ss.getCostPerDevelopment();
            }
        }
         */
        //the first square will always have the lowest development cost
        return this.ownedElements.first().getCostPerDevelopment();
    }

    /**
     * checks if a player has at least one entire system
     * @return the systems or null
     */
    public ArrayList<SystemName> getCompletedSystems() {
        //only allow unique values
        Set<SystemName> ownedSystems = new HashSet<>();

        for (int i = 0; i < getOwnedElements().size(); i++) {
            int squaresInSys = getOwnedElements().get(i).getSystemType();
            SystemSquare s = getOwnedElements().get(i);
            SystemName initSys = s.getSystemNameEnum();
            ownedSystems.add(initSys);

            //this is 1 larger than the number of squares to check
            int squaresToCheckLimit = Math.min(i+squaresInSys, getOwnedElements().size());

            //skip check if single square or if there are fewer squares left to check than are in the system
            if (getOwnedElements().size() == 1 || squaresInSys > getOwnedElements().size() - i || s.isMortgaged()) {
                ownedSystems.remove(initSys);
                squaresToCheckLimit = i + 1;
            }

            for (int j = i + 1; j < squaresToCheckLimit; j++) {
                SystemSquare s2 = getOwnedElements().get(j);
                SystemName sys = s2.getSystemNameEnum();
                if (sys.equals(initSys) && !s2.isMortgaged()) {
                    i++;
                } else {
                    ownedSystems.remove(initSys);
                    //updates i to skip uncompleted system
                    i = j - 1;
                    //ends inner loop
                    j = i + squaresInSys;
                }
            }
        }
        if (ownedSystems.size() == 0) {
            return null;
        } else {
            return new ArrayList<>(ownedSystems);
        }
    }

    /**
     * find out whether the user has any mortgagable elements
     * @return whether the player has at least one element that can be mortgaged
     */
    public boolean hasMortgagableElements() {
        for (SystemSquare s : this.getOwnedElements()) {
            if (s.getDevelopment() == 0 && !s.isMortgaged()) {
                return true;
            }
        }
        return false;
    }

    /**
     * find out whether the user has any developments
     * @return whether the player has at least one development
     */
    public boolean hasDevelopments() {
        for (SystemSquare s : this.getOwnedElements()) {
            if (s.getDevelopment() > 0) {
                return true;
            }
        }
        return false;
    }
}
