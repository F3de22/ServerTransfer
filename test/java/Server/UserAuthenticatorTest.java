package Server;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

class UserAuthenticatorTest {
    private static final String CRED_FILE = "test_credentials.txt";
    private UserAuthenticator auth;

    @BeforeEach
    void setup() throws IOException {
        // Prepara un file fittizio delle credenziali
        Files.deleteIfExists(Path.of(CRED_FILE));
        auth = new UserAuthenticator(CRED_FILE);
        assertTrue(Files.exists(Path.of(CRED_FILE))); // creato nel costruttore
    }

    @Test
    void testRegisterAndAuthenticateUser() {
        assertTrue(auth.register("alice", "pwd123", false));
        // autenticazione con credenziali corrette
        User u = auth.authenticate("alice", "pwd123");
        assertNotNull(u);
        assertEquals("alice", u.getUsername());
        assertFalse(u.isAdmin());

        // non registra due volte lo stesso username
        assertFalse(auth.register("alice", "pwd123", false));
    }

    @Test
    void testAuthenticateInvalid() {
        // nessun utente esistente
        assertNull(auth.authenticate("bob", "nopwd"));
        // registra un admin e test
        assertTrue(auth.register("admin1", "secret", true));
        User u2 = auth.authenticate("admin1", "wrong");
        assertNull(u2);
    }
}
