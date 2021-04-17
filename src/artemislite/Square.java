package artemislite;

/**
 * represents a square on the board
 */
public class Square {
    private final String squareName;
    private final int position;
    private final String message;

    public Square(String squareName, int position, String message) {
        this.squareName = squareName;
        this.position = position;
        this.message = message;
    }

    /**
     * @return the squareName
     */
    public String getSquareName() {
        return squareName;
    }

    /**
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }
}
