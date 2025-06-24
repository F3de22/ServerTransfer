package Server;

import Server.observers.AbstractObservable;
import Server.observers.DownloadInfo;
import Server.observers.LoggerObserver;

import java.io.*;
import java.net.*;

// Nel nostro progetto il Server è il nostro Observable (Quindi il soggetto osservato)
public class Server extends AbstractObservable {
    private static Server instance;
    private ServerSocket serverSocket;
    private int port = 12345;
    private File credentialsFile;

    private Server() {
        credentialsFile = new File("credentials.txt");
        // Se il file delle credenziali non esiste, lo crea
        try {
            if (!credentialsFile.exists()) {
                credentialsFile.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("Errore nella creazione del file delle credenziali: " + e.getMessage());
        }
    }

    // Metodo per ottenere l'unica istanza del Server
    public static Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }

    public File getCredentialsFile() {
        return credentialsFile;
    }

    /** Metodo per notificare il completamento di un download
     * Viene usata solo per i Test
     * */
    public void notifyDownload(String username, String fileName) {
        notifyObservers(new DownloadInfo(username, fileName));
    }

    // Avvia il server in ascolto sulla porta definita
    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server avviato sulla porta " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept(); // <--- riceve la connessione da Client.java
                System.out.println("Nuovo client connesso: " + clientSocket.getInetAddress());
                File rootDir = new File("server_files"); // Directory di base
                ClientHandler handler = new ClientHandler(clientSocket, rootDir); // Per ogni client viene creato un ClientHandler in un nuovo thread
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.out.println("Errore nel server: " + e.getMessage());
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException e) { }
            }
        }
    }

    public static void main(String[] args) {
        Server server = Server.getInstance();
        
        server.addObserver(new LoggerObserver()); // Aggiungo un observer per il logging dei download
        server.start();
    }
}
