
package Server;

import Server.Commands.Command;
import Server.Commands.CommandFactory;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private File currentDir;
    private User user;

    public ClientHandler(Socket clientSocket, File rootDir) {
        this.clientSocket = clientSocket;
        this.currentDir = rootDir;
    }

	@Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))
        ) {
        	
        	UserAuthenticator authenticator = new UserAuthenticator("credentials.txt");

        	boolean authenticated = false;
        	
        	while (!authenticated) {
        		String risposta;
        		while (true) {
        		    out.write("Sei registrato? (si/no):\n");
        		    out.flush();
        		    risposta = in.readLine();

        		    if (risposta == null || risposta.trim().isEmpty()) {
        		        continue; // premi solo INVIO o invio vuoto allora ripeti
        		    }

        		    if (risposta.equalsIgnoreCase("si") || risposta.equalsIgnoreCase("no")) {
        		        break; // le uniche risposte valide
        		    }
        		}

        	    if (risposta.equalsIgnoreCase("si")) {
        	        out.write("Inserisci username:\n");
        	        out.flush();
        	        String usernameInput = in.readLine();

        	        out.write("Inserisci password:\n");
        	        out.flush();
        	        String passwordInput = in.readLine();

        	        user = authenticator.authenticate(usernameInput, passwordInput);
        	        if (user != null) {
        	            authenticated = true;
        	            this.user = user;
        	            out.write("Login effettuato con successo!\n");
        	            out.write("Benvenuto " + user.getUsername() + (user.isAdmin() ? " [ADMIN]" : "") + "\n");
        	            out.flush();
        	        } else {
        	            out.write("Credenziali errate.\n");
        	            out.flush();
        	        }
        	    } else {
        	        out.write("Registrazione - Inserisci username:\n");
        	        out.flush();
        	        String newUser = in.readLine();

        	        out.write("Inserisci password:\n");
        	        out.flush();
        	        String newPass = in.readLine();

        	        if (authenticator.register(newUser, newPass, false)) { // false → non admin
        	            user = authenticator.authenticate(newUser, newPass);
        	            this.user = user;
        	            authenticated = true;
        	            out.write("Registrazione completata con successo!\n");
        	            out.write("Benvenuto " + user.getUsername() + "\n");
        	            out.flush();
        	        } else {
        	            out.write("Errore: username già esistente.\n");
        	            out.flush();
        	        }
        	    }
        	}

            String commandLine;
            while ((commandLine = in.readLine()) != null) {
                String[] parts = commandLine.trim().split("\\s+"); // divide per spazio
                String cmdName = parts[0];
                Command command = CommandFactory.getCommand(cmdName, currentDir, out, user.isAdmin());
                if (command != null) {
                    currentDir = command.execute(this, parts);
                } else {
                    out.write("Invalid or unauthorized command.\n");
                    out.flush();
                }
            }


        } catch (IOException e) {
            System.out.println("Client connection error: " + e.getMessage());
        }
    }
}
