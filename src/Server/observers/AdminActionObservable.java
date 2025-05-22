
package Server.observers;

import java.util.ArrayList;
import java.util.List;

public class AdminActionObservable {
    private List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void notifyObservers(String message) {
        for (Observer obs : observers) {
            obs.update(message);
        }
    }
}
