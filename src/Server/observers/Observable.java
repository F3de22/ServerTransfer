package Server.observers;

import Server.observers.Observer;
import java.util.List;

public interface Observable {
    void addObserver(Observer observer);
    void notifyObservers(String message);
}
