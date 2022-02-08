package LF;

import lombok.Getter;
import lombok.Setter;
import transparentActor.utils.Message;

@Getter
@Setter
public class LFMessage extends Message {

    private String senderName;

    public LFMessage(String senderName, String actorIdentifier, String handlerName) {
        super(actorIdentifier, handlerName);
        this.senderName = senderName;
    }

}
