package artemislite;

public class BankruptcyException extends Exception {
    private static final long serialVersionID = 1L;

    public BankruptcyException() {
    }

    public BankruptcyException(String message) {
        super(message);
    }
}


