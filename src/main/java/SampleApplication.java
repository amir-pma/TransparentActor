import rebeca.RebecaComposer;
import rebeca.RebecaNetwork;
import test.Printer;
import test.Producer;

import static java.lang.Thread.currentThread;


public class SampleApplication {

    public static void main(String[] args) {
        RebecaComposer composer = new RebecaComposer();
        RebecaNetwork network = new RebecaNetwork("network", composer);
        Producer producer = new Producer("producer", network, composer, 1);
        Printer printer = new Printer("printer", network, composer, 5);

        producer.addKnownRebec(printer);

//        System.out.println("Thread: " + currentThread().getId());

        composer.activate();
        network.activate();
        producer.activate();
        printer.activate();

        producer.initial();

    }


}
