package rebeca;

import transparentActor.network.AbstractNetwork;

public class RebecaNetwork extends AbstractNetwork {

    public RebecaNetwork(String identifier, RebecaComposer composer) {
        super(identifier, composer, false, new RebecaNetworkMailbox());
    }

    public RebecaNetworkMailbox getBuff() {
        return (RebecaNetworkMailbox) buffer;
    }

    //Default tagging (no tagging)

    //Default Transfer Policy (FIFO)

}
