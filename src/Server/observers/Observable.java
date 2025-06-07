package Server.observers;

import java.util.ArrayList;
import java.util.List;

public interface Observable {
    List<Observer> observers = new ArrayList<>();
    void addObserver(Observer observer);
    void notifyObservers(String message);
}
