package artemislite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.naming.InvalidNameException;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionTest {

    String message;

    @BeforeEach
    void setUp() {
        message = "Message";
    }

    @Test
    void testBankruptcyException() {
        BankruptcyException e = assertThrows(BankruptcyException.class, () -> {
            throw new BankruptcyException();
        });

        e = assertThrows(BankruptcyException.class, () -> {
            throw new BankruptcyException(message);
        });
        assertEquals(message, e.getMessage());
    }

    @Test
    void testInvalidNameException() {
        DuplicateNameException e = assertThrows(DuplicateNameException.class, () -> {
            throw new DuplicateNameException();
        });

        e = assertThrows(DuplicateNameException.class, () -> {
            throw new DuplicateNameException(message);
        });
        assertEquals(message, e.getMessage());
    }
}