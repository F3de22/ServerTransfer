package Server.observers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

// Questo è un Observer del Client -> E' leggermente diverso da LoggerObserver perchè aggiunge il Log scrivendolo all'interno di un file
// TODO: Eventualmernte si può eliminare LoggerObserver così da integrare LoggerObserver qua dentro, per ora rimane separato
public class FileLoggerObserver implements Observer {
    @Override
    public void onFileDownloaded(String username, String fileName) {
        // Prima era scritto su LoggerObserver; così scriviamo sia su console che su file
        //System.out.println("[DOWNLOAD] L'utente " + username + " ha scaricato il file: " + fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("download_log.txt", true))) {
            writer.write("Utente " + username + " ha scaricato il file: " + fileName);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Errore nel salvataggio del log su file: " + e.getMessage());
        }
    }
    
    @Override
    public void update(String message) {
        //System.out.println("[LOG] " + message);
    }

}
