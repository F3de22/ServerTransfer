package Server.Commands;

import Server.ClientHandler;
import Server.observers.LoggerObserver;
import Server.observers.UserActionObservable;

import java.io.File;

public class ListCommand implements Command {
    private final File currentDir;

    public ListCommand(File currentDir) {
        this.currentDir = currentDir;
    }

    @Override
    public File execute(ClientHandler handler) {
        File[] files = currentDir.listFiles();
        if (files == null || files.length == 0) {
            handler.sendMessage("La directory è vuota.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (File file : files) {
                sb.append(file.getName()).append(file.isDirectory() ? "/" : "").append("\n");
            }
            String list = sb.toString().trim(); // rimuove l’ultimo \n
            handler.sendMessage(list);
        }

        try {
            UserActionObservable observable = new UserActionObservable();
            observable.addObserver(new LoggerObserver());
            observable.notifyObservers("Utente ha visualizzato il contenuto di: " + currentDir.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Errore logging list: " + e.getMessage());
        }

        return currentDir;
    }
}