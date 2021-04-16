package artemislite;

/**
 * represents an actor in the system
 */
public abstract class Actor {
    private static final int MIN_POSITION = 0;
    private static final int MAX_POSITION = 11;
    private static final String INVALID_POSITION = "Position is invalid";

    private int position;

    public Actor(int position) {
        this.position = position;
    }

    /**
     * @return the position
     */
    public int getPosition() {
        return this.position;
    }

    /**
     * @param position the position to set
     * @throws IllegalArgumentException if outside bounds
     */
    private void setPosition(int position) throws IllegalArgumentException {
        this.position = position;
    }

    /**
     * updates the position of a player
     * @param delta - the change in position of a player
     */
    public void updatePosition(int delta) {
        if (delta > 0) {
            if (this.position + delta <= 11) {
                setPosition(this.position + delta);
            } else {
                setPosition(((this.position + delta + 1) % 12) - 1);
                throw new IndexOutOfBoundsException();
            }
        } else {
            throw new IllegalArgumentException(INVALID_POSITION);
        }
    }
}
