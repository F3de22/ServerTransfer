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

            System.out.println("Inserisci il percorso della cartella dove salvare i file scaricati (lascia vuoto per usare la cartella corrente):");
            String saveDirectory = stdIn.readLine();
            if (saveDirectory == null || saveDirectory.trim().isEmpty()) {
                saveDirectory = ".";
            }
            final String finalSaveDirectory = saveDirectory; // variabile finale per usarla nell'inner class

            // Thread per ricevere i messaggi dal server
            Thread listener = new Thread(() -> {
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

            // Ciclo di lettura dei comandi da console
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
