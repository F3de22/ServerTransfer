package Server.Commands;

import Server.ClientHandler;
import Server.observers.LoggerObserver;
import Server.observers.UserActionObservable;

import java.io.File;

public class CdCommand implements Command {
    private final File currentDir;
    private final String dirName;

    public CdCommand(File currentDir, String dirName) {
        this.currentDir = currentDir;
        this.dirName = dirName;
    }

    @Override
    public File execute(ClientHandler handler) {
        File newDir = new File(currentDir, dirName);
        if (newDir.exists() && newDir.isDirectory()) {
            handler.sendMessage("Directory cambiata in: " + newDir.getName());
            try {
                UserActionObservable observable = new UserActionObservable();
                observable.addObserver(new LoggerObserver());
                observable.notifyUser("L'utente ha cambiato la directory in: " + newDir.getAbsolutePath());
            } catch (Exception e) {
                System.err.println("Errore logging cd: " + e.getMessage());
            }
            return newDir;
        } else {
            handler.sendMessage("Directory non trovata.");
            return currentDir;
        }
    }
}
