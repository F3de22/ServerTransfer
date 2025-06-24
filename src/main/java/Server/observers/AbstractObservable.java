package Server.observers;

import java.util.ArrayList;
import java.util.List;

/**
 * Base astratta che incapsula la lista di observer
 */
public abstract class AbstractObservable implements Observable {
    private final List<Observer> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer o) {
        if (o != null && !observers.contains(o)) {
            observers.add(o);
        }
    }

    /**
     * Il metodo notify è protetto: solo le sottoclassi dentro il package possono invocarlo.
     * Se un evento invocato è di tipo DownloadInfo allora chiama onFileDownloaded
     * altrimenti per qualsiasi altro evento chiama o.update(event...)
     * E' stato spostato in AbstractObservable (dove prima era in Observable) in modo
     * da restare nascosto a chiunque abbia soltanto un riferimento di tipo Observable.
     */
    protected void notifyObservers(Object event) {
        for (Observer o : observers) {
            if (event instanceof DownloadInfo) {
                o.onFileDownloaded(event);
            } else {
                o.update(event);
            }
        }
    }

}
