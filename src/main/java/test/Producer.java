package test;

import rebeca.RebecaActor;
import rebeca.RebecaComposer;
import rebeca.RebecaMessage;
import rebeca.RebecaNetwork;

public class Producer extends RebecaActor {

    public Integer count = 0;

    public Producer(String identifier, RebecaNetwork network, RebecaComposer composer, Integer mailBoxSize) {
        super(identifier, network, composer, mailBoxSize);
    }

    @Override
    public void initial() {
        handler_sendTwoPrintMessage(null);
    }

    public void handler_sendTwoPrintMessage(RebecaMessage message) {
        try {
            String messageText = "Current count: " + count++;
            Thread.sleep(3000);
            sendRebecaMessage(generateMessage("printer",
                    "handler_print", messageText));
            System.out.println("(PRODUCER) Sent message: " + messageText);
            messageText = "Current count: " + count++;
            sendRebecaMessage(generateMessage("printer",
                    "handler_print", messageText));
            System.out.println("(PRODUCER) Sent message: " + messageText);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void handler_getAck(RebecaMessage message) {
        System.out.println("(PRODUCER) Received ack");
        handler_sendTwoPrintMessage(null);
    }

    public PrintMessage generateMessage(String destinationActorName, String destinationHandlerName, String messageText) {
        PrintMessage message = new PrintMessage(self.identifier, destinationActorName, destinationHandlerName);
        message.setMessageText(messageText);
        return message;
    }

}
