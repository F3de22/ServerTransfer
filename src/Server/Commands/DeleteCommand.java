
package Server.Commands;

import Server.ClientHandler;
import Server.observers.AdminActionObservable;
import Server.observers.FileLoggerObserver;

import java.io.*;

public class DeleteCommand implements Command {
    private File currentDir;
    private BufferedWriter out;
    private String fileName;

    public DeleteCommand(File currentDir, BufferedWriter out, String fileName) {
        this.currentDir = currentDir;
        this.out = out;
        this.fileName = fileName;
    }

    @Override
    public File execute(ClientHandler handler, String[] args) {
        try {
            File file = new File(currentDir, fileName);
            if (!file.exists()) {
                out.write("File non trovato.\n");
            } else if (file.delete()) {
                out.write("File eliminato.\n");
                AdminActionObservable observable = new AdminActionObservable();
                observable.addObserver(new FileLoggerObserver());
                observable.notifyObservers("Admin ha eliminato il file: " + file.getAbsolutePath());
            } else {
                out.write("Tentativo di eliminazione del file fallito.\n");
            }
            out.flush();
        } catch (IOException e) {
            try {
                out.write("Errore: " + e.getMessage() + "\n");
            } catch (IOException ignored) {}
        }
        return currentDir;
    }
}
