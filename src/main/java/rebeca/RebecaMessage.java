package rebeca;

import lombok.Getter;
import lombok.Setter;
import transparentActor.actor.HandlerRef;
import transparentActor.utils.Message;

@Getter
@Setter
public class RebecaMessage extends Message {

    private String senderName;

    public RebecaMessage(String senderName, HandlerRef handlerRef) {
        super(handlerRef);
        this.senderName = senderName;
    }

}
