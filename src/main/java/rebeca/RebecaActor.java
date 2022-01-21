package rebeca;

import ta.actor.AbstractActor;
import ta.composer.Composer;
import ta.network.AbstractNetwork;
import ta.utils.Buffer;

public class RebecaActor extends AbstractActor {


    public RebecaActor(String identifier, Buffer buffer, AbstractNetwork abstractNetwork, Composer composer) {
        super(identifier, buffer, abstractNetwork, composer);
    }

    public RebecaActor(String identifier, AbstractNetwork abstractNetwork, Composer composer) {
        super(identifier, abstractNetwork, composer);
    }
}
