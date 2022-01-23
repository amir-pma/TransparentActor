package transparentActor.composer;

import transparentActor.exception.AlreadyRegisteredException;
import transparentActor.exception.AlreadyDeactivatedException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;


public class Composer extends Thread {

    private volatile Boolean isActive = false, isDeactivating = false, ageWaitingComposerItems = false;
    private PriorityBlockingQueue<ComposerItem> waitingItems;
    protected final HashMap<ComposerItem, ComposerItem.StatusType> composerItems = new HashMap<>();
    private final HashMap<ComposerItem, Boolean[]> composerItemLocks = new HashMap<>();
    private final long composerThreadId;
    private final ReentrantLock composerLock;


    public Composer() {
        this.setDaemon(true);
        waitingItems = new PriorityBlockingQueue<>(10, new ComposerItem.ComposerItemComparator());
        composerThreadId = currentThread().getId();
        composerLock = new ReentrantLock();
    }

    public Composer(Boolean ageWaitingComposerItems) {
        this();
        this.ageWaitingComposerItems = ageWaitingComposerItems;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public final void activate() {
        if (isActive)
            throw new AlreadyRegisteredException();
        isDeactivating = false;
        this.start();
    }

    public final void deactivate() {
        if (!isActive)
            throw new AlreadyDeactivatedException();
        isDeactivating = true;
        HashSet<ComposerItem> networks = new HashSet<>();
        for (ComposerItem composerItem : composerItems.keySet()) {
            if (!composerItem.isNetwork())
                composerItem.deactivate();
            else
                networks.add(composerItem);
        }
        for (ComposerItem network : networks)
            network.deactivate();
        this.interrupt();
    }

    @Override
    public final void run() {
        isActive = true;
        while (!Thread.interrupted()) {
            try {
                handle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        isActive = false;
    }


    public final void register(ComposerItem composerItem, Boolean[] runLock) {
        composerItems.put(composerItem, ComposerItem.StatusType.IDLE);
        composerItemLocks.put(composerItem, runLock);
    }

    public final Boolean requestSchedule(ComposerItem composerItem) {
        if (!isActive || isDeactivating || currentThread().getId() == composerThreadId)
            return false;
        composerLock.lock();
        if (ageWaitingComposerItems) {
            updatePrioritiesForAging();
            waitingItems = new PriorityBlockingQueue<>(waitingItems);
        }
        waitingItems.add(composerItem);
        composerItems.put(composerItem, ComposerItem.StatusType.WAITING);
        composerLock.unlock();
        return true;

    }

    public final Boolean changeItemStatusToIdle(ComposerItem composerItem) {
        if (currentThread().getId() == composerThreadId)
            return false;
        composerItems.put(composerItem, ComposerItem.StatusType.IDLE);
        return true;
    }

    public final ComposerItem findItem(String identifier) {
        return composerItems.keySet().stream()
                .filter(composerItem -> composerItem.identifier.equals(identifier)).findFirst()
                .orElse(null);
    }

    public final void updateComposerItemPriorityInWaitingList(ComposerItem composerItem) {
        if (currentThread().getId() == composerThreadId)
            composerLock.lock();
        if (waitingItems.contains(composerItem)) {
            waitingItems.remove(composerItem);
            waitingItems.add(composerItem);
        }
        if (currentThread().getId() == composerThreadId)
            composerLock.unlock();
    }

    private void handle() {
        if (waitingItems.isEmpty())
            return;
        composerLock.lock();
        preScheduleTask();
        ComposerItem composerItem = waitingItems.poll();
        synchronized (composerItemLocks.get(composerItem)) {
            composerItemLocks.get(composerItem)[0] = true;
            composerItemLocks.get(composerItem).notify();
        }
        composerItems.put(composerItem, ComposerItem.StatusType.RUNNING);
        composerLock.unlock();
    }

    //Aging Method: Default increment priority of every item
    public void updatePrioritiesForAging() {
        waitingItems.forEach(item -> item.setItemPriority(item.getItemPriority() + 1));
    }

    //Schedule Policy: Default continue with default priorities (Non-Deterministic)
    public void preScheduleTask() {
        //You can change priorities or wait based on composer items
    }

}
