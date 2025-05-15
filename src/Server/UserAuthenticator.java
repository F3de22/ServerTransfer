package Server;

import java.io.*;

public class UserAuthenticator {
    // Controlla se le credenziali sono corrette
    public static synchronized boolean login(String username, String password, File credentialsFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(credentialsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    if (parts[0].equals(username) && parts[1].equals(password)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            // Nessun utente trovato
            // TODO: gestire l'eccezione in qualche modo
        }
        return false;
    }

    // Registra un nuovo utente, se non esiste già
    public static synchronized boolean register(String username, String password, File credentialsFile) {
        // Controlla se l'username esiste già
        if (credentialsFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(credentialsFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(":");
                    if (parts.length == 2 && parts[0].equals(username)) {
                        return false; // Utente già esistente
                    }
                }
            } catch (IOException e) { }
        }
        // Aggiunge le nuove credenziali al file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(credentialsFile, true))) {
            writer.write(username + ":" + password);
            writer.newLine();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
