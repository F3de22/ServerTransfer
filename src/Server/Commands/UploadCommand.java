
package Server.Commands;

import Server.ClientHandler;
import Server.observers.AdminActionObservable;
import Server.observers.FileLoggerObserver;

import java.io.*;

public class UploadCommand implements Command {
    private File currentDir;
    private BufferedWriter out;
    private String fileName;

    public UploadCommand(File currentDir, BufferedWriter out, String fileName) {
        this.currentDir = currentDir;
        this.out = out;
        this.fileName = fileName;
    }

    @Override
    public File execute(ClientHandler handler, String[] args) {
        try {
            File file = new File(currentDir, fileName);
            if (file.exists()) {
                out.write("Il File esiste già sul server.\n");
            } else {
                out.write("Pronto per ricevere il file.\n");
                out.flush();
                // Logging dell'azione admin
                AdminActionObservable observable = new AdminActionObservable();
                observable.addObserver(new FileLoggerObserver());
                observable.notifyObservers("Admin ha fatto l'upload del file: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            try {
                out.write("Errore: " + e.getMessage() + "\n");
            } catch (IOException ignored) {}
        }
        return currentDir;
    }
}
