package Server.observers;

// Questo rappresenta l'interfaccia per poter istanziare un Observer
public interface Observer {
	void update(Object obj); // <- aggiunto per UserActionObservable e AdminActionObservable
    void onFileDownloaded(Object obj);
}
