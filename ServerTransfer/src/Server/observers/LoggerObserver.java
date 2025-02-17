package Server.observers;

public class LoggerObserver implements FileDownloadObserver {
    @Override
    public void onFileDownloaded(String username, String fileName) {
        System.out.println("L'utente " + username + " ha scaricato il file: " + fileName);
    }
}
