package Server.observers;

public interface FileDownloadObserver {
    void onFileDownloaded(String username, String fileName);
}
