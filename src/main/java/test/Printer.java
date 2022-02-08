package test;

import rebeca.RebecaActor;
import rebeca.RebecaComposer;
import rebeca.RebecaNetwork;

public class Printer extends RebecaActor {
    public Printer(String identifier, RebecaNetwork network, RebecaComposer composer, Integer mailBoxSize) {
        super(identifier, network, composer, mailBoxSize);
    }

    @Override
    public void initial() {
    }

    public void handler_print(PrintMessage message) {
        try {
            System.out.println("(PRINTER) Received message sender: " + message.getSenderName());
            Thread.sleep(3000);
            sendRebecaMessage(generateMessage("producer", "handler_getAck"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
