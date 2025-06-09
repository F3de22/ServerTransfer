package Server.observers;

import java.util.ArrayList;
import java.util.List;

public class DownloadObservable {
    private final List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void notifyObservers(String username, String fileName) {
        for (Observer observer : observers) {
            observer.onFileDownloaded(username, fileName);
        }
    }
}
