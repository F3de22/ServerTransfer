package whitebox;

import org.junit.jupiter.api.*;
import Server.Server;

import static org.assertj.core.api.Assertions.*;

class ServerWhiteBoxTest {

    @Test
    void singletonBehaviorAndCredentialsFilePath() {
        Server s1 = Server.getInstance();
        Server s2 = Server.getInstance();
        assertThat(s1).isSameAs(s2);

        String credPath = s1.getCredentialsFile().getPath();
        assertThat(credPath)
                .as("Il server deve avere un file di credenziali configurato")
                .isNotEmpty();
    }
}
