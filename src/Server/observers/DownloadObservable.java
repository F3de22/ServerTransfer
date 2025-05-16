package Server.observers;

import java.util.ArrayList;
import java.util.List;

public abstract class DownloadObservable {
    private boolean changed = false;

    protected List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer o) {
        observers.add(o);
    }

    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    protected void setChanged() {
        changed = true;
    }

    protected void clearChanged() {
        changed = false;
    }

    public boolean hasChanged() {
        return changed;
    }

    protected void notifyObservers(String username, String fileName) {
        if (hasChanged()) {
            for (Observer o : observers) {
                o.onFileDownloaded(username, fileName);
            }
            clearChanged();
        }
    }
}
