package Server;

import Server.Commands.*;
import org.junit.jupiter.api.*;

import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

class CommandFactoryTest {
    private File root = new File("server_files");
    private User regular = new User("u","p",false);
    private User admin   = new User("a","p",true);

    @Test
    void testValidCommandsForRegular() {
        assertTrue(CommandFactory.getCommand("list", root, false) instanceof ListCommand);
        assertTrue(CommandFactory.getCommand("cd ..", root, false)  instanceof CdCommand);
        assertTrue(CommandFactory.getCommand("download file.txt", root, false) instanceof DownloadCommand);
    }

    @Test
    void testInvalidForRegular() {
        // upload e delete non permessi
        assertNull(CommandFactory.getCommand("upload x", root, false));
        assertNull(CommandFactory.getCommand("delete x", root, false));
    }

    @Test
    void testAdminCommands() {
        assertTrue(CommandFactory.getCommand("upload file", root, true) instanceof UploadCommand);
        assertTrue(CommandFactory.getCommand("delete file", root, true) instanceof DeleteCommand);
    }

    @Test
    void testUnknownCommand() {
        assertNull(CommandFactory.getCommand("foobar", root, true));
    }
}
