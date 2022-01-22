package rebeca;

import transparentActor.network.AbstractNetwork;

public class RebecaNetwork extends AbstractNetwork {

    public RebecaNetwork(String identifier, RebecaComposer composer) {
        super(identifier, composer, new RebecaNetworkMailbox());
        this.setItemPriority(1);
    }

    public RebecaNetworkMailbox getBuff() {
        return (RebecaNetworkMailbox) buffer;
    }

    //Default tagging (no tagging)

    //Default Transfer Policy (FIFO)

}
