
package Client;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.Base64;

public class Client {
    public static void main(String[] args) {
        String serverAddress = "localhost"; // oppure l'indirizzo IP del server
        int port = 12345;
        try (Socket socket = new Socket(serverAddress, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("  ______                                                  ________                                        ______                     ");
            System.out.println(" /      \\                                                |        \\                                      /      \\                    ");
            System.out.println("|  $$$$$$\\  ______    ______  __     __   ______    ______\\$$$$$$$$______   ______   _______    _______ |  $$$$$$\\ ______    ______  ");
            System.out.println("| $$___\\$$ /      \\  /      \\|  \\   /  \\ /      \\  /      \\ | $$  /      \\ |      \\ |       \\  /       \\| $$_  \\$$/      \\  /      \\ ");
            System.out.println(" \\$$    \\ |  $$$$$$\\|  $$$$$$\\\\$$\\ /  $$|  $$$$$$\\|  $$$$$$\\| $$ |  $$$$$$\\ \\$$$$$$\\| $$$$$$$\\|  $$$$$$$| $$ \\   |  $$$$$$\\|  $$$$$$\\");
            System.out.println(" _\\$$$$$$\\| $$    $$| $$   \\$$ \\$$\\  $$ | $$    $$| $$   \\$$| $$ | $$   \\$$/      $$| $$  | $$ \\$$    \\ | $$$$   | $$    $$| $$   \\$$");
            System.out.println("|  \\__| $$| $$$$$$$$| $$        \\$$ $$  | $$$$$$$$| $$      | $$ | $$     |  $$$$$$$| $$  | $$ _\\$$$$$$\\| $$     | $$$$$$$$| $$      ");
            System.out.println(" \\$$    $$ \\$$     \\| $$         \\$$$    \\$$     \\| $$      | $$ | $$      \\$$    $$| $$  | $$|       $$| $$      \\$$     \\| $$      ");
            System.out.println("  \\$$$$$$   \\$$$$$$$ \\$$          \\$      \\$$$$$$$ \\$$       \\$$  \\$$       \\$$$$$$$ \\$$   \\$$ \\$$$$$$$  \\$$       \\$$$$$$$ \\$$      ");

            System.out.println("\n\nInserisci il percorso della cartella dove salvare i file scaricati (lascia vuoto per usare la cartella di default):");
            System.out.println("Cartella di default: ~\\ServerTransfer\\downloaded_files");
            String saveDirectory = stdIn.readLine();
            if (saveDirectory == null || saveDirectory.trim().isEmpty()) {
                Path path = Paths.get(".");
                saveDirectory = path.toAbsolutePath().normalize() + "\\downloaded_files";
            }
            final String finalSaveDirectory = saveDirectory;

            // Thread per ricevere i messaggi dal server
            Thread listener = new Thread(() -> {
                try {
                    // Dopo login riceve il ruolo utente
                    String welcomeMsg = in.readLine();
                    boolean isAdmin = welcomeMsg != null && welcomeMsg.contains("[ADMIN]");
                    System.out.println(welcomeMsg);

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

                    String response;
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
                            }
                        } else {
                            System.out.println("Server: " + response);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Connessione chiusa dal server.");
                }
            });
            listener.start();

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
