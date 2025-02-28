package Server;

import Server.Commands.Command;
import Server.Commands.CommandFactory;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private Server server;
    private BufferedReader in;
    private PrintWriter out;
    private String username;
    private File currentDir;

    public ClientHandler(Socket socket, Server server) {
        this.clientSocket = socket;
        this.server = server;
    }

    // Metodo per inviare messaggi al client
    public void sendMessage(String message) {
        out.println(message);
    }

    public File getCurrentDir() {
        return currentDir;
    }

    public void setCurrentDir(File dir) {
        this.currentDir = dir;
    }

    public String getUsername() {
        return username;
    }

    public Server getServer() {
        return server;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            // CICLO DI AUTENTICAZIONE
            boolean authenticated = false;
            while (!authenticated) {
                out.println("Sei registrato? (si/no):");
                String risposta = in.readLine();
                if (risposta == null) break;
                if (risposta.equalsIgnoreCase("si")) {
                    out.println("Inserisci username:");
                    String user = in.readLine();
                    out.println("Inserisci password:");
                    String pass = in.readLine();
                    if (UserAuthenticator.login(user, pass, server.getCredentialsFile())) {
                        username = user;
                        authenticated = true;
                        out.println("Login effettuato con successo!");
                    } else {
                        out.println("Credenziali errate.");
                    }
                } else {
                    out.println("Registrazione - Inserisci username:");
                    String user = in.readLine();
                    out.println("Inserisci password:");
                    String pass = in.readLine();
                    if (UserAuthenticator.register(user, pass, server.getCredentialsFile())) {
                        username = user;
                        authenticated = true;
                        out.println("Registrazione effettuata e login effettuato con successo!");
                    } else {
                        out.println("Errore nella registrazione. Username già esistente?");
                    }
                }
            }

            // Imposta la directory iniziale (assicurarsi che la cartella "server_files" esista)
            currentDir = new File("server_files");
            if (!currentDir.exists()) {
                currentDir.mkdirs();
            }
            out.println("Directory corrente: " + currentDir.getAbsolutePath());

            // CICLO DI GESTIONE DEI COMANDI
            out.println("Scegli un comando: ");
            String commandLine;
            while ((commandLine = in.readLine()) != null) {
                String[] parts = commandLine.split(" ");
                String commandName = parts[0];
                if (commandName.equalsIgnoreCase("exit")) {
                    out.println("Disconnessione...");
                    break;
                }
                // Usa il CommandFactory per ottenere il comando appropriato
                Command command = CommandFactory.getCommand(commandName);
                if (command != null) {
                    command.execute(this, parts);
                } else {
                    out.println("Comando non riconosciuto.");
                }
            }
        } catch (IOException e) {
            System.out.println("Errore con client " + clientSocket.getInetAddress() + ": " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) { }
        }
    }
}
