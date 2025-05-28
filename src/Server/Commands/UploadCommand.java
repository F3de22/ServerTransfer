// UploadCommand.java
package Server.Commands;

import Server.ClientHandler;
import Server.observers.AdminActionObservable;
import Server.observers.FileLoggerObserver;
import Server.observers.LoggerObserver;

import java.io.File;

public class UploadCommand implements Command {
    private final File currentDir;
    private final String fileName;

    public UploadCommand(File currentDir, String fileName) {
        this.currentDir = currentDir;
        this.fileName = fileName;
    }

    @Override
    public File execute(ClientHandler handler, String[] args) {
        File file = new File(currentDir, fileName);
        if (file.exists()) {
            handler.sendMessage("Il file esiste già sul server.");
        } else {
            handler.sendMessage("Pronto per ricevere il file.");
            try {
                AdminActionObservable observable = new AdminActionObservable();
                observable.addObserver(new FileLoggerObserver());
                observable.addObserver(new LoggerObserver());
                observable.notifyObservers("Admin ha fatto l'upload del file: " + file.getAbsolutePath());
            } catch (Exception e) {
                System.err.println("Errore logging upload: " + e.getMessage());
            }
        }
        return currentDir;
    }
}
