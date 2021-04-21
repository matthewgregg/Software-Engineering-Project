package artemislite;

/**
 * custom DuplicateNameException, thrown when player enters a duplicate name
 */
public class DuplicateNameException extends Exception {
    private static final long serialVersionID = 1L;

    /**
     * custom DuplicateNameException
     */
    public DuplicateNameException() {
    }

    /**
     * Custom DuplicateNameException with message
     * @param message the exception's message
     */
    public DuplicateNameException(String message) {
        super(message);
    }
}
