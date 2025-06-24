package Server.observers;

/**
 * Notifiche di azioni per lo user.
 * E' giusto che sia public e non protected sia qui che su AdminAction
 * perchè altrimenti tutte le chiamate da ListCommand, DownloadCommand, ecc ecc
 * effettuate dal ClientHandler non compilerebbero.
 * Infatti protected in Java permette l’accesso solo alle sottoclassi
 * o alle classi nello stesso package di UserActionObservable
 */
public class UserActionObservable extends AbstractObservable {
    public void notifyUser(String message) {
        notifyObservers(message);
    }
}
