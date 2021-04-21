package artemislite;

/**
 * represents a square on the board that belongs to a system
 */
public class SystemSquare extends Square {
    private static final int MIN_DEVELOPMENT = 0;
    private static final int MAX_DEVELOPMENT = 4;
    private static final String INVALID_SYSTEM_NAME = "Invalid system name";
    private static final String MAX_DEVELOPMENT_REACHED = "Element fully developed";
    private static final String INVALID_LANDING_COST = "Invalid landing cost.";

    private SystemName systemNameEnum;
    private String systemNameString;
    private int systemType;
    private final int quizDifficulty;
    private final int baseCost;
    private final int costPerDevelopment;
    private int development;
    private int[] landingCost;
    private boolean isMortgaged;
    private boolean isOwned;

    /**
     * constructor with arguments
     * @param systemName the name of the square's system
     * @param quizDifficulty the quiz difficulty
     * @param baseCost the base cost of the square
     * @param costPerDevelopment the cost per development
     */
    public SystemSquare(String squareName,
                        int position,
                        SystemName systemName,
                        int quizDifficulty,
                        int baseCost,
                        int costPerDevelopment,
                        int[] landingCost) throws IllegalArgumentException {
        super(squareName, position, null);
        setSystemName(systemName);
        this.quizDifficulty = quizDifficulty;
        this.baseCost = baseCost;
        this.costPerDevelopment = costPerDevelopment;
        this.development = 0;
        setLandingCost(landingCost);
        this.isMortgaged = false;
        this.isOwned = false;
        setSystemType();
    }

    /**
     * @return the systemName
     */
    public String getSystemNameString() {
        return systemNameString;
    }

    public SystemName getSystemNameEnum() {
        return systemNameEnum;
    }

    /**
     * Sets both enum value and string value for systemName
     * @param systemNameString the systemName to set
     */
    private void setSystemName(SystemName systemNameString) {
        this.systemNameEnum = systemNameString;
        this.systemNameString = Game.stringifyEnum(systemNameString);
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
    private void setSystemType() throws IllegalArgumentException {
        switch (systemNameEnum) {
            case EXPLORATION_GROUND_SYSTEM:
            case LUNAR_LANDER:
                this.systemType = 2; break;
            case ORION_SPACECRAFT:
            case GATEWAY_OUTPOST:
                this.systemType = 3; break;
            default: throw new IllegalArgumentException(INVALID_SYSTEM_NAME);
        }
    }

    /**
     * @return the minigameDifficulty
     */
    public int getQuizDifficulty() {
        return quizDifficulty;
    }

    /**
     * @return the baseCost
     */
    public int getBaseCost() {
        return baseCost;
    }

    /**
     * @return the costPerDevelopment
     */
    public int getCostPerDevelopment() {
        return costPerDevelopment;
    }

    /**
     * @return the development
     */
    public int getDevelopment() {
        return development;
    }

    /**
     * @param development the development to set
     * @throws IllegalArgumentException if maximum development reached
     */
    public void setDevelopment(int development) throws IllegalArgumentException {
        if (development >= MIN_DEVELOPMENT && development <= MAX_DEVELOPMENT) {
            this.development = development;
        } else {
            throw new IllegalArgumentException(MAX_DEVELOPMENT_REACHED);
        }
    }

    /**
     * @param landingCost the landingCost to set
     * @throws IllegalArgumentException if the landing costs do not match the number of developments
     */
    private void setLandingCost(int[] landingCost) throws IllegalArgumentException {
        if (landingCost.length == MAX_DEVELOPMENT + 1) {
            this.landingCost = landingCost;
        } else {
            throw new IllegalArgumentException(INVALID_LANDING_COST);
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
     * @return the maximum development
     */
    public int getMaxDevelopment() {
        return MAX_DEVELOPMENT;
    }

    /**
     * @return the isMortgaged
     */
    public boolean isMortgaged() {
        return isMortgaged;
    }

    /**
     * @param mortgaged the isMortgaged to set
     */
    public void setMortgaged(boolean mortgaged) {
        this.isMortgaged = mortgaged;
    }

    /**
     * @return the isOwned
     */
    public boolean isOwned() {
        return isOwned;
    }

    /**
     * @param owned the isOwned to set
     */
    public void setOwned(boolean owned) {
        this.isOwned = owned;
    }
}
