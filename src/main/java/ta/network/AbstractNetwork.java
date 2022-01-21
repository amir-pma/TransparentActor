package ta.network;

import ta.actor.AbstractActor;
import ta.composer.Composer;
import ta.composer.ComposerItem;
import ta.exception.ActorNotActivatedException;
import ta.exception.DestinationActorNotFoundInNetworkException;
import ta.exception.ReceiverNotAnActorException;
import ta.utils.Buffer;
import ta.utils.Message;


public abstract class AbstractNetwork extends ComposerItem {

    public final static String NETWORK_PREFIX = "network_";

    public AbstractNetwork(String identifier, Buffer buffer, Composer composer) {
        super(NETWORK_PREFIX.concat(identifier), composer, buffer);
    }

    public AbstractNetwork(String identifier, Composer composer) {
        super(NETWORK_PREFIX.concat(identifier), composer);
    }

    public final void handle() {
        Message message = takeMessageProtected();
        if(message != null) {
            if(!identifier.startsWith(AbstractActor.ACTOR_PREFIX))
                throw new ReceiverNotAnActorException();
            AbstractActor destinationActor = (AbstractActor) composer.findItem(message.getHandlerRef().getActorIdentifier());
            if (destinationActor != null) {
                if(this.requestComposerSchedule()) {
                    transfer(destinationActor, message);
                    composer.changeItemStatusToIdle(this);
                }
            }
            else {
                throw new DestinationActorNotFoundInNetworkException();
            }
        }
    }

    public final void receiveMessage(Message message) {
        buffer.add(tag(message));
    }

    private void transfer(AbstractActor destinationActor, Message message) {
        Boolean received = destinationActor.receiveMessageProtected(message);
        if(!received)
            throw new ActorNotActivatedException();
    }

    //Tagging: Default No Extra Tag
    public Message tag(Message message) {
        return message;
    }

    //Take Message For Transfer Policy: Default FIFO
    public Message takeMessage() {
        Message message = buffer.getMessages().get(0);
        buffer.remove(message);
        return message;
    }

}
