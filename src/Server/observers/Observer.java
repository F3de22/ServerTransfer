package Server.observers;

// Questo rappresenta l'interfaccia per poter istanziare un Observer
public interface Observer {
    void onFileDownloaded(String username, String fileName);
}
