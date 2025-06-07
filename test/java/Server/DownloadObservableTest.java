package Server;

import Server.observers.LoggerObserver;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class DownloadObservableTest {
    static class DummyObserver extends LoggerObserver {
        List<String> events = new ArrayList<>();
        @Override
        public void onFileDownloaded(String user, String fname) {
            events.add(user + ":" + fname);
        }
    }

    @Test
    void testNotifyDownload() {
        Server srv = Server.getInstance();
        DummyObserver dob = new DummyObserver();
        srv.addObserver(dob);

        srv.notifyDownload("alice", "file.txt");
        assertEquals(1, dob.events.size());
        assertEquals("alice:file.txt", dob.events.get(0));
    }
}
