package Server;

import java.io.*;

public class UserAuthenticator {
    private final String credentialsPath;

    public UserAuthenticator(String credentialsPath) {
        this.credentialsPath = credentialsPath;
    }

    public User authenticate(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(credentialsPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length >= 3 &&
                    parts[0].trim().equals(username.trim()) &&
                    parts[1].trim().equals(password.trim())) {
                    boolean isAdmin = parts[2].trim().equalsIgnoreCase("admin");
                    return new User(username.trim(), password.trim(), isAdmin);
                }
            }
        } catch (IOException e) {
            System.out.println("Errore durante l'autenticazione: " + e.getMessage());
        }
        return null;
    }

    public boolean register(String username, String password, boolean isAdmin) {
        username = username.trim();
        password = password.trim();

        File file = new File(credentialsPath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Impossibile creare il file credentials.txt: " + e.getMessage());
                return false;
            }
        }

        // Controlla se l'username esiste già
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length >= 1 && parts[0].trim().equals(username)) {
                    return false; // esiste già
                }
            }
        } catch (IOException e) {
            System.out.println("Errore durante la lettura per la registrazione: " + e.getMessage());
            return false;
        }

        // Aggiunge il nuovo utente
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            String role = isAdmin ? "admin" : "user";
            writer.write(username + " " + password + " " + role);
            writer.newLine();
            return true;
        } catch (IOException e) {
            System.out.println("Errore durante la scrittura della registrazione: " + e.getMessage());
            return false;
        }
    }
}
