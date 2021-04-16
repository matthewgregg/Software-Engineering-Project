package artemislite;

public class DuplicateNameException extends Exception {
    private static final long serialVersionID = 1L;

    public DuplicateNameException() {
    }

    public DuplicateNameException(String message) {
        super(message);
    }
}
