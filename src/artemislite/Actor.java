package artemislite;

/**
 * represents an actor in the system
 */
public abstract class Actor {
    private static final int MIN_POSITION = 1;
    private static final int MAX_POSITION = 12;
    private static final String INVALID_POSITION = "Position is invalid";

    private static int position;

    /**
     * @return the position
     */
    public static int getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     * @throws IllegalArgumentException if outside bounds
     */
    public static void setPosition(int position) throws IllegalArgumentException {
        if (position >= MIN_POSITION && position <= MAX_POSITION) {
            Actor.position = position;
        } else {
            throw new IllegalArgumentException(INVALID_POSITION);
        }
    }

    /**
     * updates the position of a player
     * @param delta - the change in position of a player
     */
    public static void updatePosition(int delta) {
        setPosition(position + delta);
    }
}
