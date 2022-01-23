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
        System.out.println("Received message: " + message.getMessageText());
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
