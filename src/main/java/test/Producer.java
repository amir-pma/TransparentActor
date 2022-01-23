package test;

import rebeca.RebecaActor;
import rebeca.RebecaComposer;
import rebeca.RebecaMessage;
import rebeca.RebecaNetwork;
import transparentActor.actor.HandlerRef;

public class Producer extends RebecaActor {

    public Integer count = 0;

    public Producer(String identifier, RebecaNetwork network, RebecaComposer composer, Integer mailBoxSize) {
        super(identifier, network, composer, mailBoxSize);
    }

    @Override
    public void initial() {
        handle_sendPrintMessage(null);
    }

    public void handle_sendPrintMessage(RebecaMessage message) {
        while(true) {
            String messageText = "Current count: " + count++;
            sendMessage(generateMessage("printer", "handler_print", messageText));

            System.out.println("Sent message: " + messageText + "    " + currentThread().getId());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public PrintMessage generateMessage(String destinationActorName, String destinationHandlerName, String messageText) {
        PrintMessage message = new PrintMessage(self.identifier, new HandlerRef(destinationActorName, destinationHandlerName));
        message.setMessageText(messageText);
        return message;
    }

}
