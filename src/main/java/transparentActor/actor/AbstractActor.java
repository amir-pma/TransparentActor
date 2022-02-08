package transparentActor.actor;


import transparentActor.composer.Composer;
import transparentActor.composer.ComposerItem;
import transparentActor.exception.ActorHandlerNotFoundException;
import transparentActor.exception.ActorHandlerNotSpecifiedInMessageException;
import transparentActor.exception.NetworkNotActiveException;
import transparentActor.network.AbstractNetwork;
import transparentActor.utils.Buffer;
import transparentActor.utils.Message;

import java.lang.reflect.Method;
import java.util.Arrays;


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
    private final String actorName;
    public final static String HANDLER_PREFIX = "handler_";


    public AbstractActor(String identifier, AbstractNetwork network, Composer composer, Buffer buffer) {
        super(identifier, composer, buffer);
        this.network = network;
        this.actorName = this.getClass().getName();
    }

    public final void handle() {
        Message message = takeMessageProtected();
        if(message != null) {
            if(message.getHandlerName() == null)
                throw new ActorHandlerNotSpecifiedInMessageException();
            if (!message.getHandlerName().startsWith(HANDLER_PREFIX)) {
                throw new ActorHandlerNotFoundException();
            }
            Method handler = null;
            try {
                for(Method method : Class.forName(actorName).getDeclaredMethods()) {
                    if(method.getParameterCount() == 1 && method.getName().equals(message.getHandlerName()) &&
                            (Message.class.isAssignableFrom(Arrays.stream(method.getParameterTypes()).findFirst().orElseThrow()))) {
                        handler = method;
                        break;
                    }
                }
            } catch (ClassNotFoundException e) {
                throw new ActorHandlerNotFoundException();
            }
            if(handler == null)
                throw new ActorHandlerNotFoundException();
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
        if(buffer.getMessages().size() > 0)
            return (Message) buffer.getMessages().stream().findFirst().orElse(null);
        else
            return null;
    }

}
