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
        	
        	System.out.println("  ______                                                  ________                                        ______                     \n"
        			+ " /      \\                                                |        \\                                      /      \\                    \n"
        			+ "|  $$$$$$\\  ______    ______  __     __   ______    ______\\$$$$$$$$______   ______   _______    _______ |  $$$$$$\\ ______    ______  \n"
        			+ "| $$___\\$$ /      \\  /      \\|  \\   /  \\ /      \\  /      \\ | $$  /      \\ |      \\ |       \\  /       \\| $$_  \\$$/      \\  /      \\ \n"
        			+ " \\$$    \\ |  $$$$$$\\|  $$$$$$\\\\$$\\ /  $$|  $$$$$$\\|  $$$$$$\\| $$ |  $$$$$$\\ \\$$$$$$\\| $$$$$$$\\|  $$$$$$$| $$ \\   |  $$$$$$\\|  $$$$$$\\\n"
        			+ " _\\$$$$$$\\| $$    $$| $$   \\$$ \\$$\\  $$ | $$    $$| $$   \\$$| $$ | $$   \\$$/      $$| $$  | $$ \\$$    \\ | $$$$   | $$    $$| $$   \\$$\n"
        			+ "|  \\__| $$| $$$$$$$$| $$        \\$$ $$  | $$$$$$$$| $$      | $$ | $$     |  $$$$$$$| $$  | $$ _\\$$$$$$\\| $$     | $$$$$$$$| $$      \n"
        			+ " \\$$    $$ \\$$     \\| $$         \\$$$    \\$$     \\| $$      | $$ | $$      \\$$    $$| $$  | $$|       $$| $$      \\$$     \\| $$      \n"
        			+ "  \\$$$$$$   \\$$$$$$$ \\$$          \\$      \\$$$$$$$ \\$$       \\$$  \\$$       \\$$$$$$$ \\$$   \\$$ \\$$$$$$$  \\$$       \\$$$$$$$ \\$$      \n"
        			+ "                                                                                                                                     \n"
        			+ "                                                                                                                                     \n"
        			+ "                                                                                                                                     ");

            System.out.println("Inserisci il percorso della cartella dove salvare i file scaricati (lascia vuoto per usare la cartella di default):");
            System.out.println("Cartella di default: ~\\ServerTransfer\\downloaded_files");
            String saveDirectory = stdIn.readLine();
            if (saveDirectory == null || saveDirectory.trim().isEmpty()) {
                Path path = Paths.get(".");
                saveDirectory = path.toAbsolutePath().normalize() + "\\downloaded_files";
            }
            final String finalSaveDirectory = saveDirectory;

            // Thread per ricevere i messaggi dal server
            Thread listener = new Thread(() -> {
            	System.out.println("  __  __              _      _     _                             _ _ \n"
            			+ " |  \\/  |___ _ _ _  _( )  __| |___(_)  __ ___ _ __  __ _ _ _  __| (_)\n"
            			+ " | |\\/| / -_| ' | || |/  / _` / -_| | / _/ _ | '  \\/ _` | ' \\/ _` | |\n"
            			+ " |_|  |_\\___|_||_\\_,_|   \\__,_\\___|_| \\__\\___|_|_|_\\__,_|_||_\\__,_|_|\n"
            			+ "                                                                     ");
            	System.out.println(" |------------------------------------------------------------------|\n"
            			+ " |- list: Visualizza tutti i file presenti nella cartella scelta;   |\n"
            			+ " |- cd <directory>: Comando per spostarsi tra le directory;         |\n"
            			+ " |- download <nomefile>: Comando per scaricare un file;             |\n"
            			+ " |- exit: Comando per chiudere la sessione del client;              |\n"
            			+ " |------------------------------------------------------------------|\n");

                String response;
                try {
                    while ((response = in.readLine()) != null) {
                        // Se il messaggio contiene il file (download)
                        if (response.startsWith("FILE_CONTENT:")) {
                            // Formato: FILE_CONTENT:nomeFile:contenutoCodificato
                            String[] parts = response.split(":", 3);
                            if (parts.length == 3) {
                                String fileName = parts[1];
                                String encoded = parts[2];
                                byte[] fileBytes = Base64.getDecoder().decode(encoded);
                                Path outputPath = Paths.get(finalSaveDirectory, fileName);
                                Files.write(outputPath, fileBytes);
                                System.out.println("File " + fileName + " salvato localmente.");
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

            // lettura dei comandi
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                if (userInput.equalsIgnoreCase("exit")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Errore: " + e.getMessage());
        }
    }
}
