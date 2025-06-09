package blackbox;

import org.junit.jupiter.api.*;
import Server.User;
import Server.Commands.*;
import Server.CommandFactory;

import java.io.File;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

class FileOperationsBlackBoxTest {
    private final File root = new File("server_files");
    private final User admin = new User("a","p",true);
    private final User user  = new User("u","p",false);

    @BeforeEach
    void cleanDownloadDir() throws Exception {
        // svuota downloaded_files
        Path dl = Path.of("downloaded_files");
        if (Files.exists(dl)) {
            Files.walk(dl)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
        Files.createDirectories(dl);
    }

    @Test
    void downloadCopiesFileForAnyUser() throws Exception {
        // assicuriamoci che server_files/prova1.txt esista
        Path src = Path.of("server_files/prova1.txt");
        assertTrue(Files.exists(src), "server_files/prova1.txt deve esistere per il test");

        Command cmd = CommandFactory.getCommand("download prova1.txt", root, false);
        assertNotNull(cmd);
        cmd.execute(user);

        Path dest = Path.of("downloaded_files/prova1.txt");
        assertTrue(Files.exists(dest), "Il file dovrebbe essere copiato in downloaded_files");
    }

    @Test
    void uploadAddsFileOnlyForAdmin() throws Exception {
        // prendi un file di test in file_to_upload/prova2.txt
        Path up = Path.of("file_to_upload/prova2.txt");
        assertTrue(Files.exists(up), "file_to_upload/prova2.txt deve esistere");

        // upload come admin
        Command cmdAdmin = CommandFactory.getCommand("upload prova2.txt", root, true);
        assertNotNull(cmdAdmin);
        cmdAdmin.execute(admin);
        assertTrue(Files.exists(Path.of("server_files/prova2.txt")), "Il file deve finire in server_files");

        // pulisco e ricarico
        Files.deleteIfExists(Path.of("server_files/prova2.txt"));

        // upload come utente normale → comando null
        Command cmdUser = CommandFactory.getCommand("upload prova2.txt", root, false);
        assertNull(cmdUser);
    }

    @Test
    void deleteRemovesFileOnlyForAdmin() throws Exception {
        // crea un file temporaneo da cancellare
        Path toDelete = Path.of("server_files/tmp.txt");
        Files.writeString(toDelete, "temp");
        assertTrue(Files.exists(toDelete));

        // delete come utente normale → comando null
        assertNull(CommandFactory.getCommand("delete tmp.txt", root, false));

        // delete come admin
        Command cmd = CommandFactory.getCommand("delete tmp.txt", root, true);
        assertNotNull(cmd);
        cmd.execute(admin);
        assertFalse(Files.exists(toDelete), "Il file deve essere rimosso da server_files");
    }
}
