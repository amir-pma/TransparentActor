package transparentActor.actor;


import transparentActor.composer.Composer;
import transparentActor.composer.ComposerItem;
import transparentActor.exception.ActorHandlerNotFoundException;
import transparentActor.exception.NetworkNotActiveException;
import transparentActor.network.AbstractNetwork;
import transparentActor.utils.Buffer;
import transparentActor.utils.Message;

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

    protected final AbstractNetwork network;
    public final static String HANDLER_PREFIX = "handler_";


    public AbstractActor(String identifier, AbstractNetwork network, Composer composer, Buffer buffer) {
        super(identifier, composer, buffer);
        this.network = network;
    }

    public AbstractActor(String identifier, AbstractNetwork network, Composer composer) {
        super(identifier, composer);
        this.network = network;
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

    protected final void sendMessage(Message message) {
        Boolean received = network.receiveMessageProtected(message);
        if(!received)
            throw new NetworkNotActiveException();
    }

    //Receive Policy: Default Accept All
    public void receiveMessage(Message message) {
        buffer.insert(message);
    }

    //Take Policy: Default FIFO
    public Message takeMessage() {
        if(buffer.getMessages().size() > 0) {
            return buffer.getMessages().get(0);
        }
        else {
            return null;
        }
    }

}
