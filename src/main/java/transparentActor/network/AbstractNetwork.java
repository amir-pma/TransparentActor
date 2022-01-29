package transparentActor.network;

import transparentActor.actor.AbstractActor;
import transparentActor.composer.Composer;
import transparentActor.composer.ComposerItem;
import transparentActor.exception.ActorNotActivatedException;
import transparentActor.exception.DestinationActorNotFoundInNetworkException;
import transparentActor.exception.DestinationActorNotSpecifiedInMessageException;
import transparentActor.utils.Buffer;
import transparentActor.utils.Message;


public abstract class AbstractNetwork extends ComposerItem {

    private final Boolean needScheduleInComposer;

    public AbstractNetwork(String identifier, Composer composer, Boolean needScheduleInComposer, Buffer buffer) {
        super(identifier, composer, buffer);
        this.needScheduleInComposer = needScheduleInComposer;
    }

    public AbstractNetwork(String identifier, Composer composer, Boolean needScheduleInComposer) {
        super(identifier, composer);
        this.needScheduleInComposer = needScheduleInComposer;
    }

    public final void handle() {
        Message message = takeMessageProtected();
        if(message != null) {
            if(message.getActorIdentifier() == null)
                throw new DestinationActorNotSpecifiedInMessageException();
            AbstractActor destinationActor = (AbstractActor) composer.findItem(message.getActorIdentifier());
            if (destinationActor != null) {
                if(!needScheduleInComposer || this.requestComposerSchedule()) {
                    if (!needScheduleInComposer)
                        composer.changeItemStatusToRunning(this);
                    transfer(destinationActor, message);
                    if(!needScheduleInComposer)
                        composer.changeItemStatusToIdle(this);
                }
            }
            else {
                throw new DestinationActorNotFoundInNetworkException();
            }
        }
    }

    private void transfer(AbstractActor destinationActor, Message message) {
        Boolean received = destinationActor.receiveMessageProtected(message);
        if(!received)
            throw new ActorNotActivatedException();
    }

    public final void receiveMessage(Message message) {
        Message taggedMessage = tag(message);
        if(taggedMessage != null)
            buffer.insert(taggedMessage);
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
