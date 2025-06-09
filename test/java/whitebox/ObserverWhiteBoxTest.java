package whitebox;

import org.junit.jupiter.api.*;
import Server.Server;
import Server.observers.LoggerObserver;

import static org.assertj.core.api.Assertions.*;

class ObserverWhiteBoxTest {
    private static class SpyObserver extends LoggerObserver {
        boolean notified = false;
        @Override
        public void onFileDownloaded(String user, String fname) {
            notified = true;
        }
    }

    @Test
    void notifyDownloadInvokesObserverMethod() {
        Server srv = Server.getInstance();
        SpyObserver spy = new SpyObserver();
        srv.addObserver(spy);

        srv.notifyDownload("greg", "report.pdf");
        assertThat(spy.notified)
                .as("L’Observer deve essere notificato in caso di download")
                .isTrue();
    }
}
