package artemislite;

import javax.naming.ConfigurationException;
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
    //this may not be required
    private boolean squareOwned;
    private int development;
    private int[] landingCost;

    /**
     * constructor with arguments
     * @param systemName the name of the square's system
     * @param minigameDifficulty the minigame difficulty
     * @param baseCost the base cost of the square
     * @param costPerDevelopment the cost per development
     */
    public SystemSquare(String squareName,
                        int position,
                        String message,
                        SystemName systemName,
                        int minigameDifficulty,
                        int baseCost,
                        int costPerDevelopment,
                        int[] landingCost) throws IllegalArgumentException {
        super(squareName, position, message);
        this.systemName = systemName;
        this.minigameDifficulty = minigameDifficulty;
        this.baseCost = baseCost;
        this.costPerDevelopment = costPerDevelopment;
        setLandingCost(landingCost);
        this.squareOwned = false;
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
            case SYSTEM_NAME_1:
            case SYSTEM_NAME_4:
                this.systemType = 2; break;
            case SYSTEM_NAME_2:
            case SYSTEM_NAME_3:
                this.systemType = 3; break;
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
     * @return the landingCost
     */
    public int getLandingCost() {
        return landingCost[this.development];
    }

    /**
     *
     * @return the minimum development
     */
    public int getMinDevelopment() {
        return MIN_DEVELOPMENT;
    }

    /**
     *
     * @return the maximum development
     */
    public int getMaxDevelopment() {
        return MAX_DEVELOPMENT;
    }

    /**
     * @param landingCost the landingCost to set
     */
    public void setLandingCost(int[] landingCost) throws IllegalArgumentException {
        if (landingCost.length >= MIN_DEVELOPMENT && landingCost.length <= MAX_DEVELOPMENT + 1) {
            this.landingCost = landingCost;
        } else {
            throw new IllegalArgumentException();
        }
    }
    
    // TODO  - to fix the System Square enum
	public static String capitaliseWord(String str) {
		
		String words[] = str.split("\\s");
		String formattedWords = "";
		
		for(String w : words) {
			String first = w.substring(0, 1);
			String afterfirst = w.substring(1);
			formattedWords += first.toUpperCase()+afterfirst.toLowerCase()+" ";
		}
		return formattedWords.trim();
	}
}
