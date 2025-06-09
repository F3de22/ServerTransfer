package blackbox;

import org.junit.jupiter.api.*;
import Server.Commands.*;
import Server.CommandFactory;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class CommandFactoryBlackBoxTest {
    private final File root = new File("server_files");

    @Test
    void regularUserCanUseOnlyListCdDownload() {
        assertTrue(CommandFactory.getCommand("list", root, false) instanceof ListCommand);
        assertTrue(CommandFactory.getCommand("cd subdir", root, false) instanceof CdCommand);
        assertTrue(CommandFactory.getCommand("download file.txt", root, false) instanceof DownloadCommand);
    }

    @Test
    void regularUserCannotUploadOrDelete() {
        assertNull(CommandFactory.getCommand("upload file.txt", root, false));
        assertNull(CommandFactory.getCommand("delete file.txt", root, false));
    }

    @Test
    void adminCanUseUploadAndDelete() {
        assertTrue(CommandFactory.getCommand("upload new.txt", root, true) instanceof UploadCommand);
        assertTrue(CommandFactory.getCommand("delete old.txt", root, true) instanceof DeleteCommand);
    }

    @Test
    void unknownCommandsReturnNull() {
        assertNull(CommandFactory.getCommand("foobar", root, true));
        assertNull(CommandFactory.getCommand("", root, false));
    }
}
