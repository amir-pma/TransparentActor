package transparentActor.network;

import transparentActor.actor.AbstractActor;
import transparentActor.composer.Composer;
import transparentActor.composer.ComposerItem;
import transparentActor.exception.ActorNotActivatedException;
import transparentActor.exception.DestinationActorNotFoundInNetworkException;
import transparentActor.utils.Buffer;
import transparentActor.utils.Message;


public abstract class AbstractNetwork extends ComposerItem {

    public AbstractNetwork(String identifier, Composer composer, Buffer buffer) {
        super(identifier, composer, buffer);
    }

    public AbstractNetwork(String identifier, Composer composer) {
        super(identifier, composer);
    }

    public final void handle() {
        Message message = takeMessageProtected();
        if(message != null) {
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
        Message taggedMessage = tag(message);
        if(taggedMessage != null)
            buffer.insert(taggedMessage);
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
        if(buffer.getMessages().size() > 0)
            return buffer.getMessages().get(0);
        else
            return null;
    }

}
