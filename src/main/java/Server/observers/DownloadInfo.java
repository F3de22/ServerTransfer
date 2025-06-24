package Server.observers;

/**
 * Wrapper per username + fileName.
 */
public class DownloadInfo {
    private final String username;
    private final String fileName;

    public DownloadInfo(String username, String fileName) {
        this.username = username;
        this.fileName = fileName;
    }

    public String getUsername() { return username; }
    public String getFileName() { return fileName; }

    @Override
    public String toString() {
        return "DownloadInfo{username='" + username + "', fileName='" + fileName + "'}";
    }
}
