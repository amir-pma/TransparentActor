package rebeca;

import lombok.Getter;
import lombok.Setter;
import transparentActor.utils.Message;

@Getter
@Setter
public class RebecaMessage extends Message {

    private String senderName;

    public RebecaMessage(String senderName, String actorIdentifier, String handlerName) {
        super(actorIdentifier, handlerName);
        this.senderName = senderName;
    }

}
