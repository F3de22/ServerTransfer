
package Server;

import Server.Commands.Command;
import Server.Commands.CommandFactory;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private final File rootDir;      // la cartella fissa dei file sul server per l'upload
    private File currentDir;
    private User user;
	private BufferedReader in;
	private BufferedWriter out;

    public ClientHandler(Socket clientSocket, File rootDir) {
        this.clientSocket = clientSocket;
        this.currentDir = rootDir;
        this.rootDir = rootDir;
    }

	public void sendMessage(String message) {
		try {
			out.write(message + "\n");
			out.flush();
		} catch (IOException e) {
			System.err.println("Errore nell'invio al client: " + e.getMessage());
		}
	}
	
	public File getRootDir() {
        return rootDir;
    }

	public File getCurrentDir() {
		return currentDir;
	}

	public void setCurrentDir(File dir) {
		this.currentDir = dir;
	}

	public String getUsername() {
		return user != null ? user.getUsername() : "User non valido";
	}
	
	public BufferedReader getIn() {
	    return in;
	}
	
	public String readLine() throws IOException {
	    return in.readLine();
	}

	@Override
    public void run() {
        try {
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

			UserAuthenticator authenticator = new UserAuthenticator("credentials.txt");

        	boolean authenticated = false;
        	
        	while (!authenticated) {
        		String risposta;
				while (true) {
					sendMessage("Sei registrato? (si/no):");
					risposta = in.readLine();

					if (risposta == null || risposta.trim().isEmpty()) continue;

					if (risposta.equalsIgnoreCase("si") || risposta.equalsIgnoreCase("no")) {
						break;
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
				String trimmed = commandLine.trim();

				if (trimmed.equalsIgnoreCase("exit")) {
					sendMessage("Chiusura connessione. Arrivederci!");
					break;
				}

				Command command = CommandFactory.getCommand(trimmed, currentDir, user.isAdmin());
				if (command != null) {
					// Dividi la riga in due: comando + argomento
					String[] parts = commandLine.trim().split("\\s+", 2);
					currentDir = command.execute(this, parts);
				} else {
					sendMessage("Comando non valido oppure non hai l'autorizzazione.");
				}
			}

		} catch (IOException e) {
            System.out.println("Errore di connessione al client: " + e.getMessage());
        }finally {
			// 3) Chiudi risorse al termine del loop
			try { clientSocket.close(); } catch (IOException ignored) {}
		}
    }
}
