package ta.actor;


import ta.composer.Composer;
import ta.composer.ComposerItem;
import ta.exception.ActorHandlerNotFoundException;
import ta.exception.NetworkNotActiveException;
import ta.network.AbstractNetwork;
import ta.utils.Buffer;
import ta.utils.Message;

import java.lang.reflect.Method;


/**
 *
 * Handler format:
 *      access: public
 *      name: 'handler_' + id
 *      return type: void
 *      args type: (Message)
 *
 * Example:
 *      public void handler_myNewHandler(Message m) {}
 *
 */

public abstract class AbstractActor extends ComposerItem {

    protected final AbstractNetwork abstractNetwork;
    public final static String ACTOR_PREFIX = "actor_";
    public final static String HANDLER_PREFIX = "handler_";


    public AbstractActor(String identifier, Buffer buffer, AbstractNetwork abstractNetwork, Composer composer) {
        super(ACTOR_PREFIX.concat(identifier), composer, buffer);
        this.abstractNetwork = abstractNetwork;
    }

    public AbstractActor(String identifier, AbstractNetwork abstractNetwork, Composer composer) {
        super(ACTOR_PREFIX.concat(identifier), composer);
        this.abstractNetwork = abstractNetwork;
    }

    public final void handle() {
        Message message = takeMessageProtected();
        if(message != null) {
            if (!message.getHandlerRef().getHandlerName().startsWith(HANDLER_PREFIX)) {
                throw new ActorHandlerNotFoundException();
            }
            Method handler;
            try {
                handler = this.getClass().getMethod(message.getHandlerRef().getHandlerName(), Message.class);
            } catch (NoSuchMethodException e) {
                throw new ActorHandlerNotFoundException();
            }
            if(this.requestComposerSchedule()) {
                try {
                    handler.invoke(this, message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                composer.changeItemStatusToIdle(this);
            }
        }
    }

    private void sendMessage(Message message) {
        Boolean received = abstractNetwork.receiveMessageProtected(message);
        if(!received)
            throw new NetworkNotActiveException();
    }

    //Receive Policy: Default Accept All
    public void receiveMessage(Message message) {
        buffer.add(message);
    }

    //Take Policy: Default FIFO
    public Message takeMessage() {
        Message message = buffer.getMessages().get(0);
        buffer.remove(message);
        return message;
    }

}
