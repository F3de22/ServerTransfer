package Server.observers;

public interface Observable {
    void addObserver(Observer observer);
    void notifyObservers(String message);
}
