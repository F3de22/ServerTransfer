package blackbox;

import org.junit.jupiter.api.*;
import Server.User;
import Server.UserAuthenticator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class AuthBlackBoxTest {

    private static final String TEST_CRED_FILE = "test_credentials_bb.txt"; // Test credenziali per blackbox
    private UserAuthenticator auth;

    @BeforeEach
    void setUp() throws IOException {
        // Assicuriamoci di partire sempre da un file vuoto
        Files.deleteIfExists(Path.of(TEST_CRED_FILE));
        auth = new UserAuthenticator(TEST_CRED_FILE);
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Path.of(TEST_CRED_FILE));
    }

    @Test
    void registerNewUserAndLoginSuccess() {
        // black‑box: uso solo l’API pubblica
        assertTrue(auth.register("aaa", "pwd123", false));
        User u = auth.authenticate("aaa", "pwd123");
        assertNotNull(u);
        assertEquals("aaa", u.getUsername());
        assertFalse(u.isAdmin());
    }

    @Test
    void cannotRegisterSameUserTwice() {
        assertTrue(auth.register("alice", "secret", true));
        assertFalse(auth.register("alice", "secret", true));
    }

    @Test
    void loginWithWrongPasswordFails() {
        auth.register("testLogin", "pwd", false);
        assertNull(auth.authenticate("testLogin", "wrongpwd"));
    }

    @Test
    void loginNonexistentUserFails() {
        assertNull(auth.authenticate("pippo", "nopass"));
    }
}
