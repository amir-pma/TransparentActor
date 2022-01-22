package test;

import lombok.Getter;
import lombok.Setter;
import rebeca.RebecaMessage;
import transparentActor.actor.HandlerRef;


@Getter
@Setter
public class PrintMessage extends RebecaMessage {

    private String messageText;

    public PrintMessage(String senderName, HandlerRef handlerRef) {
        super(senderName, handlerRef);
    }
}
