package test;

import lombok.Getter;
import lombok.Setter;
import rebeca.RebecaMessage;


@Getter
@Setter
public class PrintMessage extends RebecaMessage {

    private String messageText;

    public PrintMessage(String senderName, String actorIdentifier, String handlerName) {
        super(senderName, actorIdentifier, handlerName);
    }
}
