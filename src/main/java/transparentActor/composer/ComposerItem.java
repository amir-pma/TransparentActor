package transparentActor.composer;

import transparentActor.actor.AbstractActor;
import transparentActor.exception.AlreadyRegisteredException;
import transparentActor.exception.AlreadyDeactivatedException;
import transparentActor.exception.CantDeregisterWhileRunningException;
import transparentActor.network.AbstractNetwork;
import transparentActor.utils.Buffer;
import transparentActor.utils.Message;

import java.util.Comparator;
import java.util.concurrent.locks.ReentrantLock;


public abstract class ComposerItem extends Thread {

    public enum StatusType {
        IDLE, WAITING, RUNNING
    }

    public final String identifier;
    private volatile Boolean isActive = false, isDeactivating = false;
    private volatile Integer priority = 0;
    protected final Composer composer;
    protected final Buffer buffer;
    protected final ReentrantLock bufferLock;
    private final Boolean[] runLock;
    public final static Integer MIN_PRIORITY = 0;


    public ComposerItem(String identifier, Composer composer, Buffer buffer) {
        this.identifier = identifier;
        this.composer = composer;
        this.buffer = buffer;
        this.bufferLock = new ReentrantLock();
        this.setDaemon(true);
        runLock = new Boolean[] {false};
        if (composer.findItem(identifier) != null)
            throw new AlreadyRegisteredException();
        composer.register(this, runLock);
    }

    public ComposerItem(String identifier, Composer composer) {
        this(identifier, composer, new Buffer());
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public final void activate() {
        isDeactivating = false;
        this.start();
    }

    public final void deactivate() {
        if (composer.findItem(identifier) == null)
            throw new AlreadyDeactivatedException();
        if(!composer.composerItems.get(this).equals(StatusType.IDLE))
            throw new CantDeregisterWhileRunningException();
        isDeactivating = true;
        buffer.emptyBuffer();
        this.interrupt();
    }

    @Override
    public final void run() {
        isActive = true;
        while (!Thread.interrupted()) {
            try {
//                if(Objects.equals(identifier, "network"))
//                    System.out.println("============  "+currentThread().getId());
//                if(Objects.equals(identifier, "printer"))
//                    System.out.println("+++++++++++   "+currentThread().getId());
//                if(Objects.equals(identifier, "producer"))
//                    System.out.println("*************   "+currentThread().getId());
                handle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        isActive = false;
    }

    public final Boolean requestComposerSchedule() {
        runLock[0] = false;
        Boolean canSchedule = composer.requestSchedule(this);
        if (canSchedule) {
            try {
                synchronized (runLock) {
                    while(!runLock[0])
                        runLock.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            return false;
        }
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
        if (!isActive || isDeactivating)
            return false;
        this.bufferLock.lock();
        receiveMessage(message);
        this.bufferLock.unlock();
        return true;
    }

    public final Message takeMessageProtected() {
        if (!isActive)
            return null;
        this.bufferLock.lock();
        Message message = takeMessage();
        buffer.remove(message);
        this.bufferLock.unlock();
        return message;
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
