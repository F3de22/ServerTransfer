package Server.Commands;

import Server.ClientHandler;
import Server.observers.DownloadObservable;
import Server.observers.LoggerObserver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class DownloadCommand implements Command {
    private final File currentDir;
    private final String fileName;

    public DownloadCommand(File currentDir, String fileName) {
        this.currentDir = currentDir;
        this.fileName = fileName;
    }

    @Override
    public File execute(ClientHandler handler) {
        File file = new File(currentDir, fileName);

        try {
            if (!file.exists() || !file.isFile()) {
                handler.sendMessage("File non trovato.");
                return currentDir;
            }

            byte[] fileBytes = Files.readAllBytes(file.toPath());
            String encoded = Base64.getEncoder().encodeToString(fileBytes);

            handler.sendMessage("FILE_CONTENT:" + file.getName() + ":" + encoded);

            try {
                DownloadObservable observable = new DownloadObservable();
                observable.addObserver(new LoggerObserver());
                observable.notifyObservers(handler.getUsername(), file.getName());
            } catch (Exception e) {
                System.err.println("Errore nel logging degli observers: " + e.getMessage());
            }

        } catch (IOException e) {
            handler.sendMessage("Errore nel download del file: " + e.getMessage());
            e.printStackTrace(); // stampa lato server
        }

        return currentDir;
    }
}
