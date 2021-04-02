package artemislite;

import java.util.ArrayList;

/**
 * represents a square on the board that belongs to a system
 */
public class SystemSquare extends Square {
    private static final int MIN_DEVELOPMENT = 0;
    private static final int MAX_DEVELOPMENT = 4;
    private static final String INVALID_SYSTEM_NAME = "Invalid system name";
    private static final String MAX_DEVELOPMENT_REACHED = "Element fully developed";

    private SystemName systemName;
    private int systemType;
    private int minigameDifficulty;
    private int baseCost;
    private int costPerDevelopment;
    private boolean squareOwned;
    private int development;

    /**
     * constructor with arguments
     * @param systemName the name of the square's system
     * @param minigameDifficulty the minigame difficulty
     * @param baseCost the base cost of the square
     * @param costPerDevelopment the cost per development
     * @param squareOwned the square's ownership status
     */
    public SystemSquare(SystemName systemName,
                        int minigameDifficulty,
                        int baseCost,
                        int costPerDevelopment,
                        boolean squareOwned) throws IllegalArgumentException {
        this.systemName = systemName;
        this.minigameDifficulty = minigameDifficulty;
        this.baseCost = baseCost;
        this.costPerDevelopment = costPerDevelopment;
        this.squareOwned = squareOwned;
        this.development = 0;
        setSystemType();
    }

    /**
     * @return the systemName
     */
    public SystemName getSystemName() {
        return systemName;
    }

    /**
     * @param systemName the systemName to set
     */
    public void setSystemName(SystemName systemName) {
        this.systemName = systemName;
    }

    /**
     * @return the systemType
     */
    public int getSystemType() {
        return systemType;
    }

    /**
     * sets the system type
     */
    public void setSystemType() throws IllegalArgumentException {
        switch (systemName) {
            case SYSTEM_NAME_1: this.systemType = 2;
            case SYSTEM_NAME_2: this.systemType = 2;
            case SYSTEM_NAME_3: this.systemType = 3;
            case SYSTEM_NAME_4: this.systemType = 3;
            default: throw new IllegalArgumentException(INVALID_SYSTEM_NAME);
        }
    }

    /**
     * @return the minigameDifficulty
     */
    public int getMinigameDifficulty() {
        return minigameDifficulty;
    }

    /**
     * @param minigameDifficulty the minigameDifficulty to set
     */
    public void setMinigameDifficulty(int minigameDifficulty) {
        this.minigameDifficulty = minigameDifficulty;
    }

    /**
     * @return the baseCost
     */
    public int getBaseCost() {
        return baseCost;
    }

    /**
     * @param baseCost the baseCost to set
     */
    public void setBaseCost(int baseCost) {
        this.baseCost = baseCost;
    }

    /**
     * @return the costPerDevelopment
     */
    public int getCostPerDevelopment() {
        return costPerDevelopment;
    }

    /**
     * @param costPerDevelopment the costPerDevelopment to set
     */
    public void setCostPerDevelopment(int costPerDevelopment) {
        this.costPerDevelopment = costPerDevelopment;
    }

    /**
     * @return the squareOwned
     */
    public boolean isSquareOwned() {
        return squareOwned;
    }

    /**
     * @param squareOwned the squareOwned to set
     */
    public void setSquareOwned(boolean squareOwned) {
        this.squareOwned = squareOwned;
    }

    /**
     * @return the development
     */
    public int getDevelopment() {
        return development;
    }

    /**
     * @param development the development to set
     */
    public void setDevelopment(int development) throws IllegalArgumentException {
        if (this.development >= MIN_DEVELOPMENT && this.development <= MAX_DEVELOPMENT) {
            this.development = development;
        } else {
            throw new IllegalArgumentException(MAX_DEVELOPMENT_REACHED);
        }
    }

    /**
     * develops a square
     * @param player the current player
     */
    public void developElement(Player player) {
        // TODO develop square
    }

    /**
     * purchases a square
     * @param player the current player
     */
    public void purchaseSquare(Player player) {
        // TODO purchase square
    }

    /**
     * auctions a square
     * @param players all players
     */
    public void auctionSquare(ArrayList<Player> players) {
        // TODO auction square
    }
}
