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

        try {
            Socket socket = new Socket(serverAddress, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("\nClient connesso al server.\n\n");

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

            /*System.out.println("\nInserisci il percorso della cartella dove salvare i file scaricati (lascia vuoto per usare la cartella di default):");
            System.out.println("Cartella di default: ~\\ServerTransfer\\downloaded_files");
            String saveDirectory = stdIn.readLine();
            if (saveDirectory == null || saveDirectory.trim().isEmpty()) {
                Path defaultPath = Paths.get(System.getProperty("user.home"), "Documenti", "GitHub", "ServerTransfer", "downloaded_files");
                saveDirectory = defaultPath.toString();
            }
            final Path finalSaveDirectory = Paths.get(saveDirectory).toAbsolutePath();*/
            
            Path finalSaveDirectory;

            while (true) {
                System.out.println("\nInserisci il percorso della cartella dove salvare i file scaricati (lascia vuoto per usare la cartella di default):");
                System.out.println("Cartella di default: ~\\ServerTransfer\\downloaded_files");
                String saveDirectory = stdIn.readLine();

                if (saveDirectory == null || saveDirectory.trim().isEmpty()) {
                    Path defaultPath = Paths.get(System.getProperty("user.home"), "GitHub", "ServerTransfer", "downloaded_files");
                    finalSaveDirectory = defaultPath.toAbsolutePath();
                } else {
                    finalSaveDirectory = Paths.get(saveDirectory).toAbsolutePath();
                }

                if (Files.exists(finalSaveDirectory) && Files.isDirectory(finalSaveDirectory)) {
                    break; // directory valida
                } else {
                    System.out.println("La directory specificata non esiste. Riprova.");
                }
            }
            
            // Assegno finalSaveDirectory cosi che creo una variabile final per usarla nella lambda del listener
            final Path saveDir = finalSaveDirectory;  

            System.out.println("Esegui comando: "); // prompt iniziale
            
            final StringBuilder lastCommand = new StringBuilder();
            
            // Listener asincrono per messaggi e file
            Thread listener = new Thread(() -> {
                try {
                    String response;
                    while (!Thread.currentThread().isInterrupted() && (response = in.readLine()) != null) {
                        if (response.startsWith("FILE_CONTENT:")) {
                            String[] parts = response.split(":", 3);
                            if (parts.length == 3) {
                                String fileName = parts[1];
                                String encoded = parts[2];
                                byte[] fileBytes = Base64.getDecoder().decode(encoded);
                                Path outputPath = saveDir.resolve(fileName);
                                Files.write(outputPath, fileBytes);
                                System.out.println("File " + fileName + " salvato localmente. Dimensione: " + fileBytes.length + " bytes.");
                                System.out.println("Esegui comando:");
                            }
                        } else if (response.equalsIgnoreCase("Pronto per ricevere il file.")) {
                            String[] parts = lastCommand.toString().split("\\s+", 2);
                            if (parts.length == 2) {
                                File fileToSend = new File(parts[1]);
                                if (fileToSend.exists()) {
                                    String fileName = fileToSend.getName();
                                    byte[] fileBytes = Files.readAllBytes(fileToSend.toPath());
                                    String encoded = Base64.getEncoder().encodeToString(fileBytes);
                                    out.println("FILE_UPLOAD:" + fileName + ":" + encoded);
                                    System.out.println("Esegui comando:");
                                } else {
                                    System.out.println("File non trovato: " + fileToSend.getAbsolutePath());
                                }
                            } else {
                                System.out.println("Percorso file non specificato.");
                            }
                        } else {
                            if (!response.trim().isEmpty()) {
                                System.out.println("Server: " + response);
                            }
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

            // Loop dei comandi di input
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
            	lastCommand.setLength(0); // Ripulisce dopo ogni comando             
                lastCommand.append(userInput);
                out.println(userInput);
                if (userInput.equalsIgnoreCase("exit")) {
                    listener.interrupt();
                    socket.close();
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }
}
