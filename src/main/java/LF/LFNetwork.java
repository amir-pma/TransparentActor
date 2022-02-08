package LF;

import transparentActor.network.AbstractNetwork;

public class LFNetwork extends AbstractNetwork {

    public LFNetwork(String identifier, LFComposer composer) {
        super(identifier, composer, false, new LFNetworkBuffer());
    }

    public LFNetworkBuffer getBuff() {
        return (LFNetworkBuffer) buffer;
    }

    //Default tagging (no tagging)

    //Default Transfer Policy (FIFO)

}
