package blackbox;

import Server.Commands.DownloadCommand;
import org.junit.jupiter.api.*;
import Server.User;
import Server.Commands.CommandFactory;
import Server.Commands.Command;
import Server.ClientHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.net.Socket;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileOperationsBlackBoxTest {
    private final File root = new File("server_files");
    private final User admin = new User("a","p",true);
    private final User user  = new User("u","p",false);

    @BeforeEach
    void cleanDownloadDir() throws Exception {
        // Svuota downloaded_files
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
    void downloadCommandSendsBase64ContentWithoutMockito() throws Exception {
        // 1) Assicuriamoci che il file esista
        Path src = Path.of("server_files/file.txt");
        assertTrue(Files.exists(src), "server_files/file.txt deve esistere");

        // 2) Recuperiamo il comando
        Command cmd = CommandFactory.getCommand("download file.txt", root, false);
        assertNotNull(cmd);
        assertTrue(cmd instanceof DownloadCommand,
                "getCommand su 'download' deve restituire DownloadCommand");

        // 3) Creiamo il nostro handler di test che registra i messaggi
        ClientHandlerForTesting handler = new ClientHandlerForTesting(root);

        // 4) Eseguiamo il comando
        File after = cmd.execute(handler);

        // 5) La directory corrente non cambia
        assertEquals(root.getCanonicalFile(), after.getCanonicalFile());

        // 6) Verifichiamo fra i messaggi registrati quello con FILE_CONTENT
        List<String> msgs = handler.getMessages();
        assertFalse(msgs.isEmpty(), "handler deve aver ricevuto almeno un messaggio");

        boolean found = msgs.stream()
                .anyMatch(m -> m.startsWith("FILE_CONTENT:file.txt:"));
        assertTrue(found, "Deve esserci un messaggio che inizia con 'FILE_CONTENT:file.txt:'");
    }


    @Test
    void uploadAddsFileOnlyForAdmin() throws Exception {
        // Assicuriamoci che esista il file di test da "caricare"
        Path up = Path.of("file_to_upload/upload_test.txt");
        assertTrue(Files.exists(up), "file_to_upload/upload_test.txt deve esistere");

        // Prepara i dati Base64 per quel file
        byte[] bytes = Files.readAllBytes(up);
        String base64 = Base64.getEncoder().encodeToString(bytes);

        // Costruiamo il comando upload
        Command cmdAdmin = CommandFactory.getCommand("upload upload_test.txt", root, true);
        assertNotNull(cmdAdmin, "Admin deve poter ottenere l'UploadCommand");

        // Creiamo il nostro handler di test, che risponde con la riga di upload
        class UploadTestHandler extends ClientHandler {
            UploadTestHandler(File rootDir) {
                super((Socket) null, rootDir);
                // preparo il reader con la riga "FILE_UPLOAD:upload_test.txt:<base64>"
                String line = "FILE_UPLOAD:upload_test.txt:" + base64 + "\n";
                this.in = new BufferedReader(new StringReader(line));
            }
            @Override
            public void sendMessage(String message) {
                // no-op: ignoriamo i prompt
            }
        }
        // Usa il handler per eseguire
        UploadTestHandler handler = new UploadTestHandler(root);
        File after = cmdAdmin.execute(handler);

        // Ora il file deve esistere in server_files
        assertTrue(Files.exists(Path.of("server_files/upload_test.txt")),
                "Il file deve finire in server_files");
        assertEquals(root.getCanonicalFile(), after.getCanonicalFile());

        // Puliamo per non lasciare tracce
        Files.deleteIfExists(Path.of("server_files/upload_test.txt"));
    }


    @Test
    void deleteRemovesFileOnlyForAdmin() throws Exception {
        Path toDelete = Path.of("server_files/tmp.txt");
        Files.writeString(toDelete, "temp");
        assertTrue(Files.exists(toDelete));

        // Utente normale non ottiene il comando
        assertNull(CommandFactory.getCommand("delete tmp.txt", root, false));

        // Comando delete per admin
        Command cmd = CommandFactory.getCommand("delete tmp.txt", root, true);
        assertNotNull(cmd);

        ClientHandlerForTesting adminHandler = new ClientHandlerForTesting(root);
        File afterDelete = cmd.execute(adminHandler);

        assertFalse(Files.exists(toDelete), "Il file deve essere rimosso da server_files");
        assertEquals(root.getCanonicalFile(), afterDelete.getCanonicalFile());
    }


    /**
     * Semplice estensione di ClientHandler per i test:
     * - Costruttore prende solo rootDir
     */
    private static class ClientHandlerForTesting extends ClientHandler {
        private final List<String> messages = new ArrayList<>();

        ClientHandlerForTesting(File rootDir) {
            super(null, rootDir);
        }

        @Override
        public void sendMessage(String message) {
            messages.add(message);
        }

        List<String> getMessages() {
            return messages;
        }
    }
}
