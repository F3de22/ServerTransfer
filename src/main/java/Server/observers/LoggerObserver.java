package Server.observers;

// Nel nostro progetto LoggerObserver è il nostro Observer (Quidi il nostro osservatore)
public class LoggerObserver implements Observer {
	@Override
	public void update(Object message) {
	    System.out.println("[LOG] " + (String) message);
	}

	@Override
	public void onFileDownloaded(Object obj) {
		DownloadInfo info = (DownloadInfo) obj;
		System.out.printf("[DOWNLOAD] Utente %s ha scaricato il file %s\n", info.getUsername(), info.getFileName());
	}


}
