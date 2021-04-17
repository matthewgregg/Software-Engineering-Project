package artemislite;

import javax.naming.InvalidNameException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * represents a player in the system
 */
public class Player extends Actor {
    private static final String[] INVALID_NAMES = new String[]{"", "#"};
    private static final String INVALID_SQUARE_TO_DEVELOP = "The player does not own this square.";
    private static final String INVALID_DEVELOPMENT = "The development is more than the maximum development.";
    private static final String BANKRUPTCY = "You've gone bankrupt";
    private static final String MAX_DEVELOPMENT_REACHED = "Element fully developed";
    private final String name;
    private final SortedSet<SystemSquare> ownedSquares = new TreeSet<>(new ComparePosition());
    private int playerResources;

    /**
     * constructor with arguments
     * @param name the player's name
     */
    public Player(String name) throws InvalidNameException {
        super(0);
        if (Arrays.asList(INVALID_NAMES).contains(name) || name == null) {
            throw new InvalidNameException();
        } else {
            this.name = name;
            this.playerResources = 1500;
        }
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
     * @return the ownedSquares
     */
    public ArrayList<SystemSquare> getOwnedSquares() {
        return new ArrayList<>(ownedSquares);
    }

    /**
     * updates a player's resources
     * @param delta the change in resources
     */
    public void addResources(int delta) throws BankruptcyException {
        int res = this.playerResources + delta;
        if (res >= 0) {
            this.playerResources += delta;
        } else {
            throw new BankruptcyException(BANKRUPTCY);
        }
    }

    /**
     * adds a number of developments to a square
     * @param square the square to be developed
     */
    public void developSquare(SystemSquare square, int devDelta) throws IllegalArgumentException, BankruptcyException {
        if (this.getOwnedSquares().contains(square)) {
            try {
                this.addResources(devDelta * square.getCostPerDevelopment() * -1 * (int) (devDelta > 0 ? 1 : 0.5));
                square.setDevelopment(devDelta+square.getDevelopment());
            } catch (IllegalArgumentException e) {
                //undo payment
                this.addResources(devDelta * square.getCostPerDevelopment());
                throw new IllegalArgumentException(MAX_DEVELOPMENT_REACHED);
            }
        } else {
            throw new IllegalArgumentException(INVALID_SQUARE_TO_DEVELOP);
        }
    }

    /**
     * purchases a square
     * @param square the square to be purchased
     */
    public void purchaseSquare(SystemSquare square) throws BankruptcyException {
        this.ownedSquares.add(square);
        this.addResources(-1 * square.getBaseCost());
    }

    /**
     * purchases a square
     * @param square the square to be purchased
     */
    public void purchaseSquare(SystemSquare square, int cost) throws BankruptcyException {
        this.ownedSquares.add(square);
        //this.setPlayerResources(this.playerResources - square.getBaseCost());
        this.addResources(-1 * cost);
    }

    /**
     * removes a square from a players collection
     * @param square the square to remove
     */
    public void removeSquare(SystemSquare square) {
        this.ownedSquares.remove(square);
        square.setOwned(false);
    }

    /**
     * get the cost of the cheapest development
     * @return the cost of the cheapest development
     */
    public int getMinimumOwnedDevCost() {
        //the first square will always have the lowest development cost
        return this.ownedSquares.first().getCostPerDevelopment();
    }

    /**
     * checks if a player has at least one entire system that isn't completely developed
     * @return the systems or null
     */
    public ArrayList<SystemName> getDevelopableSystems() {
        Map<SystemName, List<SystemSquare>> systems = this.ownedSquares.stream()
                .filter(s -> !s.isMortgaged())
                .collect(Collectors.groupingBy(SystemSquare::getSystemNameEnum, Collectors.toList()));
        //remove if incomplete system
        systems.entrySet().removeIf(s -> s.getValue().size() != s.getValue().get(0).getSystemType());
        //remove if fully developed system
        systems.entrySet().removeIf(s -> s.getValue().stream().allMatch(t -> t.getDevelopment() == 4));
        return systems.size() == 0 ? null : new ArrayList<>(systems.keySet());
    }

    /**
     * find out whether the user has any mortgagable squares
     * @return whether the player has at least one squares that can be mortgaged
     */
    public boolean hasMortgageableSquares() {
        return this.getOwnedSquares().stream().filter(s -> s.getDevelopment() == 0).anyMatch(s -> !s.isMortgaged());
    }

    public boolean hasMortgagedSquares() {
        return this.getOwnedSquares().stream().anyMatch(SystemSquare::isMortgaged);
    }

    /**
     * find out whether the user has any developments
     * @return whether the player has at least one development
     */
    public boolean hasDevelopments() {
        return this.getOwnedSquares().stream().anyMatch(s -> s.getDevelopment() > 0);
    }

    /**
     * whether the player is close to going bankrupt or not (less than or equal to 200 credits)
     * @return player resources <= 200
     */
    public boolean goingBankrupt() {
        return this.getPlayerResources() <= 200;
    }
}
