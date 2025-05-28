package Client;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.Base64;

public class Client {
    public static void main(String[] args) {
        String serverAddress = "localhost";
        int port = 12345;

        System.out.println("  ______                                                  ________                                        ______                     ");
        System.out.println(" /      \\                                                |        \\                                      /      \\                    ");
        System.out.println("|  $$$$$$\\  ______    ______  __     __   ______    ______\\$$$$$$$$______   ______   _______    _______ |  $$$$$$\\ ______    ______  ");
        System.out.println("| $$___\\$$ /      \\  /      \\|  \\   /  \\ /      \\  /      \\ | $$  /      \\ |      \\ |       \\  /       \\| $$_  \\$$/      \\  /      \\ ");
        System.out.println(" \\$$    \\ |  $$$$$$\\|  $$$$$$\\\\$$\\ /  $$|  $$$$$$\\|  $$$$$$\\| $$ |  $$$$$$\\ \\$$$$$$\\| $$$$$$$\\|  $$$$$$$| $$ \\   |  $$$$$$\\|  $$$$$$\\");
        System.out.println(" _\\$$$$$$\\| $$    $$| $$   \\$$ \\$$\\  $$ | $$    $$| $$   \\$$| $$ | $$   \\$$/      $$| $$  | $$ \\$$    \\ | $$$$   | $$    $$| $$   \\$$");
        System.out.println("|  \\__| $$| $$$$$$$$| $$        \\$$ $$  | $$$$$$$$| $$      | $$ | $$     |  $$$$$$$| $$  | $$ _\\$$$$$$\\| $$     | $$$$$$$$| $$      ");
        System.out.println(" \\$$    $$ \\$$     \\| $$         \\$$$    \\$$     \\| $$      | $$ | $$      \\$$    $$| $$  | $$|       $$| $$      \\$$     \\| $$      ");
        System.out.println("  \\$$$$$$   \\$$$$$$$ \\$$          \\$      \\$$$$$$$ \\$$       \\$$  \\$$       \\$$$$$$$ \\$$   \\$$ \\$$$$$$$  \\$$       \\$$$$$$$ \\$$      ");

        try (Socket socket = new Socket(serverAddress, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Client connesso al server.\n\n");

            // Gestione login iniziale (sincrona)
            boolean isAdmin = false;
            String serverMsg;

            while ((serverMsg = in.readLine()) != null) {
                System.out.println("Server: " + serverMsg);
                if (serverMsg.trim().endsWith(":") || serverMsg.trim().endsWith("?")) {
                    // È un prompt -> l’utente deve rispondere
                    String userResponse = stdIn.readLine();
                    out.println(userResponse);
                }

                // Verifica ruolo admin
                if (serverMsg.startsWith("Benvenuto")) {
                    if (serverMsg.contains("[ADMIN]")) {
                        isAdmin = true;
                    }
                    break;
                }
            }

            System.out.println("  __  __              _      _     _                             _ _ ");
            System.out.println(" |  \\/  |___ _ _ _  _( )  __| |___(_)  __ ___ _ __  __ _ _ _  __| (_)");
            System.out.println(" | |\\/| / -_| ' | || |/  / _` / -_| | / _/ _ | '  \\/ _` | ' \\/ _` | |");
            System.out.println(" |_|  |_\\___|_||_\\_,_|   \\__,_\\___|_| \\__\\___|_|_|_\\__,_|_||_\\__,_|_|");
            System.out.println(" |------------------------------------------------------------------|");
            System.out.println(" |- list: Visualizza tutti i file presenti nella cartella scelta;   |");
            System.out.println(" |- cd <directory>: Comando per spostarsi tra le directory;         |");
            System.out.println(" |- download <nomefile>: Comando per scaricare un file;             |");
            if (isAdmin) {
                System.out.println(" |- upload <percorso_file>: Carica un file sul server (solo admin);|");
                System.out.println(" |- delete <nomefile>: Elimina un file dal server (solo admin);    |");
            }
            System.out.println(" |- exit: Comando per chiudere la sessione del client;              |");
            System.out.println(" |------------------------------------------------------------------|");

            System.out.println("\nInserisci il percorso della cartella dove salvare i file scaricati (lascia vuoto per usare la cartella di default):");
            System.out.println("Cartella di default: ~\\ServerTransfer\\downloaded_files");
            String saveDirectory = stdIn.readLine();
            if (saveDirectory == null || saveDirectory.trim().isEmpty()) {
                Path path = Paths.get(".");
                saveDirectory = path.toAbsolutePath().normalize() + "\\downloaded_files";
            }
            final String finalSaveDirectory = saveDirectory;

            System.out.println("Esegui comando: "); // prompt iniziale dopo setup

            // Listener asincrono per messaggi e file
            Thread listener = new Thread(() -> {
                String response;
                try {
                    while ((response = in.readLine()) != null) {
                        if (response.startsWith("FILE_CONTENT:")) {
                            String[] parts = response.split(":", 3);
                            if (parts.length == 3) {
                                String fileName = parts[1];
                                String encoded = parts[2];
                                byte[] fileBytes = Base64.getDecoder().decode(encoded);
                                Path outputPath = Paths.get(finalSaveDirectory, fileName);
                                Files.write(outputPath, fileBytes);
                                System.out.println("File " + fileName + " salvato localmente. Dimensione: " + fileBytes.length + " bytes.");
                                System.out.println("Esegui comando:");
                            }
                        } else {
                            if (!response.trim().isEmpty()) {
                                System.out.println("Server: " + response);
                            }
                            // Solo se non ci sono altre righe in arrivo, mostra il prompt
                            if (!in.ready()) {
                                System.out.println("\nEsegui comando:");
                            }
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Connessione chiusa dal server.");
                }
            });


            listener.start();

            // Loop dei comandi da tastiera
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                if (userInput.equalsIgnoreCase("exit")) break;
            }

        } catch (IOException e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }
}
