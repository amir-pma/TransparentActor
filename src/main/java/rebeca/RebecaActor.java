package rebeca;

import lombok.Getter;
import lombok.Setter;
import rebeca.exception.DestinationNotInKnownRebecsException;
import rebeca.exception.KnownRebecNotFoundException;
import transparentActor.actor.AbstractActor;
import transparentActor.actor.HandlerRef;

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

    public RebecaMessage generateMessage(String destinationActorName, String destinationHandlerName) {
        return new RebecaMessage(self.identifier, new HandlerRef(destinationActorName, destinationHandlerName));
    }

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

    public void receiveMessage(RebecaMessage message) {
        if (Objects.equals(buffer.size(), ((RebecaActorMailbox) buffer).getMailBoxSize()))
            return;
        buffer.insert(message);
    }

    public void sendRebecaMessage(RebecaMessage message) {
        if(knownRebecs.stream().anyMatch(rebecaActor -> rebecaActor.identifier.equals(message.getSenderName())))
            sendMessage(message);
        else
            throw new DestinationNotInKnownRebecsException();
    }

    //Default Take Policy (FIFO)

}
