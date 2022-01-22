package ta.composer;

import ta.exception.AlreadyActivatedException;
import ta.exception.AlreadyDeactivatedException;
import ta.exception.CantDeregisterWhileRunningException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;


public class Composer extends Thread {

    private volatile Boolean isActive = false, isDeactivating = false, ageWaitingComposerItems = false;
    private PriorityBlockingQueue<ComposerItem> waitingItems;
    protected final HashMap<ComposerItem, ComposerItem.StatusType> composerItems = new HashMap<>();
    private final HashMap<ComposerItem, ReentrantLock> composerItemLocks = new HashMap<>();


    public Composer() {
        this.setDaemon(true);
        waitingItems = new PriorityBlockingQueue<>(10, new ComposerItem.ComposerItemComparator());
    }

    public Composer(Boolean ageWaitingComposerItems) {
        this();
        this.ageWaitingComposerItems = ageWaitingComposerItems;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public final void activate() {
        if(isActive)
            throw new AlreadyActivatedException();
        isDeactivating = false;
        this.start();
    }

    public final void deactivate() {
        if(!isActive)
            throw new AlreadyDeactivatedException();
        isDeactivating = true;
        HashSet<ComposerItem> networks = new HashSet<>();
        for(ComposerItem composerItem : composerItems.keySet()) {
            if (!composerItem.isNetwork())
                composerItem.deactivate();
            else
                networks.add(composerItem);
        }
        for(ComposerItem network : networks)
            network.deactivate();
        this.interrupt();
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


    public final Boolean register(ComposerItem composerItem) {
        if(!isActive || isDeactivating)
            return false;
        composerItems.put(composerItem, ComposerItem.StatusType.IDLE);
        ReentrantLock newLock = new ReentrantLock();
        newLock.lock();
        composerItemLocks.put(composerItem, newLock);
        return true;
    }

    public final void deregister(ComposerItem composerItem) {
        if(!composerItems.get(composerItem).equals(ComposerItem.StatusType.IDLE))
            throw new CantDeregisterWhileRunningException();
        composerItems.remove(composerItem);
        composerItemLocks.remove(composerItem);
    }
    
    public final Boolean requestSchedule(ComposerItem composerItem) {
        if(!isActive || isDeactivating)
            return false;
        synchronized (this) {
            if(ageWaitingComposerItems) {
                updatePrioritiesForAging();
                waitingItems = new PriorityBlockingQueue<>(waitingItems);
            }
            composerItems.put(composerItem, ComposerItem.StatusType.WAITING);
            waitingItems.add(composerItem);
            composerItemLocks.get(composerItem).lock();
            return true;
        }
    }

    public final void changeItemStatusToIdle(ComposerItem composerItem) {
        synchronized (this) {
            composerItems.put(composerItem, ComposerItem.StatusType.IDLE);
            composerItemLocks.get(composerItem).lock();
        }
    }

    public final ComposerItem findItem(String identifier) {
        return composerItems.keySet().stream()
                .filter(composerItem -> composerItem.identifier.equals(identifier)).findFirst()
                .orElse(null);
    }

    public final void updateComposerItemPriorityInWaitingList(ComposerItem composerItem) {
        synchronized (this) {
            if (waitingItems.contains(composerItem)) {
                waitingItems.remove(composerItem);
                waitingItems.add(composerItem);
            }
        }
    }

    private void handle() {
        if(waitingItems.isEmpty())
            return;
        synchronized (this) {
            preScheduleTask();
            ComposerItem composerItem = waitingItems.poll();
            composerItems.put(composerItem, ComposerItem.StatusType.RUNNING);
            composerItemLocks.get(composerItem).unlock();
        }
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
