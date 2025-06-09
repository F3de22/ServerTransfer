package Server.observers;

// Questo rappresenta l'interfaccia per poter istanziare un Observer
public interface Observer {
	void update(String message); // <- aggiunto per UserActionObservable e AdminActionObservable
    void onFileDownloaded(String username, String fileName);
}
