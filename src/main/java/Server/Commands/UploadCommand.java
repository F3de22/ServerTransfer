package Server.Commands;

import Server.ClientHandler;
import Server.observers.AdminActionObservable;
import Server.observers.LoggerObserver;

import java.io.*;
import java.nio.file.Files;
import java.util.Base64;

public class UploadCommand implements Command {
    private final String clientFilePath;

    public UploadCommand(String clientFilePath) {
        this.clientFilePath = clientFilePath;
    }

    @Override
    public File execute(ClientHandler handler) {
        // Estrai solo il nome del file (Windows o Linux)
        String fileName = new File(clientFilePath).getName();

        // Cartella di destinazione fissa
        File dest = new File(handler.getRootDir(), fileName);

        // Se già esiste sul server
        if (dest.exists()) {
            handler.sendMessage("Il file '" + fileName + "' esiste già sul server.");
            return handler.getCurrentDir();
        }

        // Prompt al client per inviare i dati
        handler.sendMessage("Pronto per ricevere il file.");

        try {
            // Leggi la riga intera inviata dal client
            String uploadLine = handler.getIn().readLine();
            if (uploadLine == null || !uploadLine.startsWith("FILE_UPLOAD:")) {
                handler.sendMessage("Formato di upload non valido.");
                return handler.getCurrentDir();
            }

            // Splitta in tre parti: [0]="FILE_UPLOAD", [1]=nomeFile, [2]=base64Data
            String[] parts = uploadLine.split(":", 3);
            if (parts.length < 3) {
                handler.sendMessage("Dati di upload corrotti.");
                return handler.getCurrentDir();
            }
            String base64Data = parts[2];

            // Decodifica e salva
            byte[] fileBytes = Base64.getDecoder().decode(base64Data);
            Files.write(dest.toPath(), fileBytes);

            handler.sendMessage("Upload completato con successo: " + fileName);

            // Logging osservatori
            AdminActionObservable obs = new AdminActionObservable();
            obs.addObserver(new LoggerObserver());
            obs.notifyObservers("Admin ha caricato: " + dest.getAbsolutePath());

        } catch (Exception e) {
            handler.sendMessage("Errore durante l'upload: " + e.getMessage());
            System.err.println("Errore in UploadCommand: " + e.getMessage());
        }

        return handler.getCurrentDir();
    }
}
