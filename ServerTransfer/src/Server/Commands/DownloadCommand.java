package Server.Commands;

import Server.ClientHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class DownloadCommand implements Command {
    public void execute(ClientHandler handler, String[] args) {
        if (args.length < 2) {
            handler.sendMessage("Utilizzo: download <nome_file>");
            return;
        }
        String fileName = args[1];
        File file = new File(handler.getCurrentDir(), fileName);
        if (!file.exists() || !file.isFile()) {
            handler.sendMessage("File non trovato o non è un file.");
            return;
        }
        try {
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            String encoded = Base64.getEncoder().encodeToString(fileBytes);
            // Il messaggio inviato contiene: FILE_CONTENT:nomeFile:contenutoBase64
            handler.sendMessage("FILE_CONTENT:" + file.getName() + ":" + encoded);
            // Notifica l'observer del download
            handler.getServer().notifyDownload(handler.getUsername(), file.getAbsolutePath());
        } catch (IOException e) {
            handler.sendMessage("Errore nel download del file: " + e.getMessage());
        }
    }
}
