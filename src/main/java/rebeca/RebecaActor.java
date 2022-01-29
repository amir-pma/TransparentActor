package rebeca;

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
public abstract class RebecaActor extends AbstractActor {

    protected RebecaActor self;
    protected ArrayList<RebecaActor> knownRebecs = new ArrayList<>();

    public RebecaActor(String identifier, RebecaNetwork network, RebecaComposer composer, Integer mailBoxSize) {
        super(identifier, network, composer, new RebecaActorMailbox(mailBoxSize));
        self = this;
    }

    public abstract void initial();

    public Boolean addKnownRebec(RebecaActor rebecaActor) {
        return knownRebecs.add(rebecaActor);
    }

    public RebecaActor getKnownRebec(String identifier) {
        return knownRebecs.stream()
                .filter(rebecaActor -> rebecaActor.identifier.equals(identifier))
                .findFirst().orElseThrow(KnownRebecNotFoundException::new);
    }

    public RebecaActorMailbox getBuff() {
        return (RebecaActorMailbox) buffer;
    }

    public RebecaMessage generateMessage(String destinationActorName, String destinationHandlerName) {
        return new RebecaMessage(self.identifier, destinationActorName, destinationHandlerName);
    }

    public void sendRebecaMessage(Message message) {
        RebecaMessage rebecaMessage = (RebecaMessage) message;
        if(knownRebecs.stream().anyMatch(rebecaActor ->
                rebecaActor.identifier.equals(rebecaMessage.getActorIdentifier())))
            sendMessage(rebecaMessage);
        else
            throw new DestinationNotInKnownRebecsException();
    }

    @Override
    public void receiveMessage(Message message) {
        if (Objects.equals(((RebecaActorMailbox)buffer).size(), ((RebecaActorMailbox) buffer).getMailBoxSize()))
            return;
        buffer.insert(message);
    }

    //Default Take Policy (FIFO)

}
