package Server;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class ServerTest {
    @Test
    void testOnlyOneInstance() {
        Server s1 = Server.getInstance();
        Server s2 = Server.getInstance();
        assertSame(s1, s2);
        assertNotNull(s1.getCredentialsFile());
    }
}
