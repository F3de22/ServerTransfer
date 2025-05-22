
package Server.Commands;

import Server.ClientHandler;
import Server.observers.DownloadObservable;
import Server.observers.FileLoggerObserver;
import Server.observers.LoggerObserver;

import java.io.*;

public class DownloadCommand implements Command {
    private File currentDir;
    private BufferedWriter out;
    private String fileName;

    public DownloadCommand(File currentDir, BufferedWriter out, String fileName) {
        this.currentDir = currentDir;
        this.out = out;
        this.fileName = fileName;
    }

    @Override
    public File execute(ClientHandler handler, String[] args) {
        File file = new File(currentDir, fileName);
        try {
            if (!file.exists()) {
                out.write("File non trovato.\n");
                out.flush();
            } else {
                out.write("Pronto per il download.\n");
                out.flush();

                DownloadObservable observable = new DownloadObservable();
                observable.addObserver(new LoggerObserver());
                observable.addObserver(new FileLoggerObserver());
                observable.notifyObservers("User", file.getName());
            }
        } catch (IOException ignored) {}
        return currentDir;
    }
}
