package whitebox;

import org.junit.jupiter.api.*;
import Server.UserAuthenticator;
import Server.User;

import java.io.IOException;
import java.nio.file.*;
import static org.assertj.core.api.Assertions.*;

class UserAuthenticatorWhiteBoxTest {
    private static final String CRED_FILE = "test_credentials_wb.txt";
    private UserAuthenticator auth;

    @BeforeEach
    void init() throws IOException {
        Files.deleteIfExists(Path.of(CRED_FILE));
        auth = new UserAuthenticator(CRED_FILE);
    }

    @AfterEach
    void cleanup() throws IOException {
        Files.deleteIfExists(Path.of(CRED_FILE));
    }

    @Test
    void constructorCreatesFileIfMissing() {
        assertThat(Files.exists(Path.of(CRED_FILE)))
                .as("Il costruttore deve creare il file delle credenziali")
                .isTrue();
    }

    @Test
    void registerAppendsCorrectLine() throws IOException {
        auth.register("dave", "pass", true);
        String content = Files.readString(Path.of(CRED_FILE));
        assertThat(content).contains("dave,pass,true");
    }

    @Test
    void authenticateReturnsCorrectUserObject() {
        auth.register("eve", "pw", false);
        User u = auth.authenticate("eve", "pw");
        assertThat(u)
                .satisfies(user -> {
                    assertThat(user.getUsername()).isEqualTo("eve");
                    assertThat(user.isAdmin()).isFalse();
                });
    }

    @Test
    void authenticateReturnsNullOnBadPwd() {
        auth.register("frank", "pwd", false);
        assertThat(auth.authenticate("frank", "wrong")).isNull();
    }
}
