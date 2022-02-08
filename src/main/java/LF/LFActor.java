package LF;

import lombok.Getter;
import lombok.Setter;
import rebeca.exception.DestinationNotInKnownRebecsException;
import rebeca.exception.KnownRebecNotFoundException;
import transparentActor.actor.AbstractActor;
import transparentActor.utils.Message;

import java.util.ArrayList;
import java.util.Objects;


@Getter
@Setter
public abstract class LFActor extends AbstractActor {

    protected LFActor self;
    protected ArrayList<LFActor> knownRebecs = new ArrayList<>();

    public LFActor(String identifier, LFNetwork network, LFComposer composer, Integer mailBoxSize) {
        super(identifier, network, composer, new LFBuffer(mailBoxSize));
        self = this;
    }

    public abstract void initial();

    public Boolean addKnownRebec(LFActor LFActor) {
        return knownRebecs.add(LFActor);
    }

    public LFActor getKnownRebec(String identifier) {
        return knownRebecs.stream()
                .filter(LFActor -> LFActor.identifier.equals(identifier))
                .findFirst().orElseThrow(KnownRebecNotFoundException::new);
    }

    public LFBuffer getBuff() {
        return (LFBuffer) buffer;
    }

    public LFMessage generateMessage(String destinationActorName, String destinationHandlerName) {
        return new LFMessage(self.identifier, destinationActorName, destinationHandlerName);
    }

    public void sendRebecaMessage(Message message) {
        LFMessage LFMessage = (LFMessage) message;
        if(knownRebecs.stream().anyMatch(LFActor ->
                LFActor.identifier.equals(LFMessage.getActorIdentifier())))
            sendMessage(LFMessage);
        else
            throw new DestinationNotInKnownRebecsException();
    }

    @Override
    public void receiveMessage(Message message) {
        if (Objects.equals(((LFBuffer)buffer).size(), ((LFBuffer) buffer).getMailBoxSize()))
            return;
        buffer.insert(message);
    }

    //Default Take Policy (FIFO)

}
