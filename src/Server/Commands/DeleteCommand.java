package Server.Commands;

import Server.ClientHandler;
import Server.observers.AdminActionObservable;
import Server.observers.FileLoggerObserver;
import Server.observers.LoggerObserver;

import java.io.File;

public class DeleteCommand implements Command {
    private final File currentDir;
    private final String fileName;

    public DeleteCommand(File currentDir, String fileName) {
        this.currentDir = currentDir;
        this.fileName = fileName;
    }

    @Override
    public File execute(ClientHandler handler, String[] args) {
        File file = new File(currentDir, fileName);
        if (!file.exists()) {
            handler.sendMessage("File non trovato.");
        } else if (file.delete()) {
            handler.sendMessage("File eliminato.");
            try {
                AdminActionObservable observable = new AdminActionObservable();
                observable.addObserver(new FileLoggerObserver());
                observable.addObserver(new LoggerObserver());
                observable.notifyObservers("Admin ha eliminato il file: " + file.getAbsolutePath());
            } catch (Exception e) {
                System.err.println("Errore logging delete: " + e.getMessage());
            }
        } else {
            handler.sendMessage("Tentativo di eliminazione fallito.");
        }
        return currentDir;
    }
}