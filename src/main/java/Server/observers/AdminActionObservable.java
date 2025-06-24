package Server.observers;

/**
 * Notifiche di azioni per gli admin.
 */
public class AdminActionObservable extends AbstractObservable {
    public void notifyAdmin(String message) {
        notifyObservers(message);
    }
}
