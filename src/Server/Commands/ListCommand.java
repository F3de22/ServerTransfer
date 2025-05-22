
package Server.Commands;

import Server.ClientHandler;
import Server.observers.FileLoggerObserver;
import Server.observers.LoggerObserver;
import Server.observers.UserActionObservable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

public class ListCommand implements Command {
    private File currentDir;
    private BufferedWriter out;

    public ListCommand(File currentDir, BufferedWriter out) {
        this.currentDir = currentDir;
        this.out = out;
    }

    @Override
    public File execute(ClientHandler handler, String[] args) {
        File[] files = currentDir.listFiles();
        try {
            if (files != null) {
                for (File file : files) {
                    out.write(file.getName() + (file.isDirectory() ? "/" : "") + "\n");
                }
            }
            out.flush();
            UserActionObservable observable = new UserActionObservable();
            observable.addObserver(new LoggerObserver());
            observable.addObserver(new FileLoggerObserver());
            observable.notifyObservers("User listed contents of: " + currentDir.getAbsolutePath());
        } catch (IOException ignored) {}
        return currentDir;
    }
}
