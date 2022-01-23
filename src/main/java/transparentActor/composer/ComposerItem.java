package transparentActor.composer;

import transparentActor.actor.AbstractActor;
import transparentActor.exception.AlreadyActivatedException;
import transparentActor.exception.AlreadyDeactivatedException;
import transparentActor.exception.ComposerNotActiveException;
import transparentActor.network.AbstractNetwork;
import transparentActor.utils.Buffer;
import transparentActor.utils.Message;

import java.util.Comparator;
import java.util.Objects;


public abstract class ComposerItem extends Thread {

    public enum StatusType {
        IDLE, WAITING, RUNNING
    }

    public final String identifier;
    private volatile Boolean isActive = false, isDeactivating = false;
    private volatile Integer priority = 0;
    protected final Composer composer;
    protected final Buffer buffer;
    public final static Integer MIN_PRIORITY = 0;


    public ComposerItem(String identifier, Composer composer, Buffer buffer) {
        this.identifier = identifier;
        this.composer = composer;
        this.buffer = buffer;
        this.setDaemon(true);
    }

    public ComposerItem(String identifier, Composer composer) {
        this(identifier, composer, new Buffer());
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
        return composer.requestSchedule(this);
    }

    public final Integer getItemPriority() {
        return priority;
    }

    public final void setItemPriority(Integer newPriority) {
        if (newPriority < MIN_PRIORITY) {
            throw new IllegalArgumentException();
        }
        this.priority = newPriority;
    }

    public final void setItemPriorityAndUpdateComposer(Integer newPriority) {
        setItemPriority(newPriority);
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
            Message message = takeMessage();
            buffer.remove(message);
            return message;
        }
    }

    public abstract void handle();

    public abstract void receiveMessage(Message message);

    public abstract Message takeMessage();

    static final class ComposerItemComparator implements Comparator<ComposerItem> {
        @Override
        public int compare(ComposerItem item1, ComposerItem item2) {
            return Integer.compare(item2.priority, item1.priority);
        }
    }

}
