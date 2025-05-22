
package Server.Commands;

import Server.ClientHandler;

import Server.observers.FileLoggerObserver;
import Server.observers.LoggerObserver;
import Server.observers.UserActionObservable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

public class CdCommand implements Command {
    private File currentDir;
    private BufferedWriter out;
    private String dirName;

    public CdCommand(File currentDir, BufferedWriter out, String dirName) {
        this.currentDir = currentDir;
        this.out = out;
        this.dirName = dirName;
    }

    @Override
    public File execute(ClientHandler handler, String[] args) {
        File newDir = new File(currentDir, dirName);
        if (newDir.exists() && newDir.isDirectory()) {
            try {
                out.write("Directory cambiata in: " + newDir.getName() + "\n");
                out.flush();
                UserActionObservable observable = new UserActionObservable();
                observable.addObserver(new LoggerObserver());
                observable.addObserver(new FileLoggerObserver());
                observable.notifyObservers("L'utente ha cambiato la directory in: " + newDir.getAbsolutePath());
                return newDir;
            } catch (IOException ignored) {}
        } else {
            try {
                out.write("Directory non trovata.\n");
                out.flush();
            } catch (IOException ignored) {}
        }
        return currentDir;
    }
}
