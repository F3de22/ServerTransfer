package Server.observers;

/**
 * Notifiche di azioni per lo user.
 */
public class UserActionObservable extends AbstractObservable {
    public void notifyUser(String message) {
        notifyObservers(message);
    }
}
