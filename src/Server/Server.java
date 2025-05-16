package Server;

import Server.observers.DownloadObservable;
import Server.observers.Observer;
import Server.observers.LoggerObserver;

import java.io.*;
import java.net.*;
import java.util.*;

// Nel nostro progetto il Server è il nostro Observable (Quindi il soggetto osservato)
public class Server extends DownloadObservable {
    private static Server instance;
    private ServerSocket serverSocket;
    private int port = 12345;
    private List<Observer> observers;
    private File credentialsFile;

    private Server() {
        observers = new ArrayList<>();
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

    // Metodo per notficare il download
    public void notifyDownload(String username, String fileName) {
        setChanged(); // notifica solo se è cambiato
        notifyObservers(username, fileName);
    }

    // Avvia il server in ascolto sulla porta definita
    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server avviato sulla porta " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nuovo client connesso: " + clientSocket.getInetAddress());
                // Per ogni client viene creato un ClientHandler in un nuovo thread
                ClientHandler handler = new ClientHandler(clientSocket, this);
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
        // Aggiungo un observer per il logging dei download
        server.addObserver(new LoggerObserver());
        server.start();
    }
}
