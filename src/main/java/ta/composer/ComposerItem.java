package ta.composer;

import ta.actor.AbstractActor;
import ta.exception.AlreadyActivatedException;
import ta.exception.AlreadyDeactivatedException;
import ta.exception.ComposerNotActiveException;
import ta.network.AbstractNetwork;
import ta.utils.Buffer;
import ta.utils.Message;

import java.time.LocalDateTime;
import java.util.Comparator;


public abstract class ComposerItem extends Thread {

    public enum StatusType {
        IDLE, WAITING, RUNNING
    }

    public final String identifier;
    private volatile Boolean isActive = false, isDeactivating = false;
    private volatile int priority = 0;
    private LocalDateTime requestTime;
    protected final Composer composer;
    protected final Buffer buffer;
    public final static int MIN_PRIORITY = 0;
    public final static int MAX_PRIORITY = 100;


    public ComposerItem(String identifier, Composer composer, Buffer buffer) {
        this.identifier = identifier;
        this.composer = composer;
        this.buffer = buffer;
        this.setDaemon(true);
    }

    public ComposerItem(String identifier, Composer composer) {
        this.identifier = identifier;
        this.composer = composer;
        this.buffer = new Buffer();
        this.setDaemon(true);
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public final void activate() {
        if(composer.findItem(identifier) != null)
            throw new AlreadyActivatedException();
        isDeactivating = false;
        Boolean isActivated = composer.register(this);
        if(!isActivated)
            throw new ComposerNotActiveException();
        this.start();
    }

    public final void deactivate() {
        if(composer.findItem(identifier) == null)
            throw new AlreadyDeactivatedException();
        isDeactivating = true;
        buffer.emptyBuffer();
        this.interrupt();
        while (isActive)
            Thread.onSpinWait();
        composer.deregister(this);
    }

    @Override
    public final void run() {
        isActive = true;
        while(!Thread.interrupted()) {
            try {
                handle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        isActive = false;
    }

    public final Boolean requestComposerSchedule() {
        requestTime = LocalDateTime.now();
        return composer.requestSchedule(this);
    }

    public final void setItemPriority(int newPriority) {
        if (newPriority > MAX_PRIORITY || newPriority < MIN_PRIORITY) {
            throw new IllegalArgumentException();
        }
        this.priority = newPriority;
        composer.updateComposerItemPriorityInWaitingList(this);
    }

    public final Boolean isActor() {
        return this instanceof AbstractActor;
    }

    public final Boolean isNetwork() {
        return this instanceof AbstractNetwork;
    }

    public final Boolean receiveMessageProtected(Message message) {
        if(!isActive || isDeactivating)
            return false;
        synchronized (buffer) {
            receiveMessage(message);
        }
        return true;
    }

    public final Message takeMessageProtected() {
        if(!isActive)
            return null;
        synchronized (buffer) {
            return takeMessage();
        }
    }

    public abstract void handle();

    public abstract void receiveMessage(Message message);

    public abstract Message takeMessage();

    static class ComposerItemComparator implements Comparator<ComposerItem> {
        @Override
        public int compare(ComposerItem item1, ComposerItem item2) {
            if(item1.priority < item2.priority)
                return 1;
            else if(item1.priority > item2.priority)
                return -1;
            else if(item2.requestTime.isBefore(item1.requestTime))
                return 1;
            else if(item1.requestTime.isBefore(item2.requestTime))
                return -1;
            else
                return 0;
        }
    }

}
