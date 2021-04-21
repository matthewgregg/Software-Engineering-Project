package artemislite;

/**
 * custom BankruptcyException, thrown when player goes bankrupt
 */
public class BankruptcyException extends Exception {
    private static final long serialVersionID = 1L;

    /**
     * custom BankruptcyException
     */
    public BankruptcyException() {
    }

    /**
     * custom BankruptcyException with message
     * @param message
     */
    public BankruptcyException(String message) {
        super(message);
    }
}


