package Server.observers;

// Nel nostro progetto LoggerObserver è il nostro Observer (Quidi il nostro osservatore)
public class LoggerObserver implements Observer {
	@Override
	public void update(String message) {
	    System.out.println("[LOG] " + message);
	}

	@Override
	public void onFileDownloaded(String username, String fileName) {
	    System.out.println("[DOWNLOAD] L'utente " + username + " ha scaricato il file: " + fileName);
	}

}
