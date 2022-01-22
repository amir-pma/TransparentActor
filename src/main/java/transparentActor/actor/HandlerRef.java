package transparentActor.actor;

import lombok.Getter;

@Getter
public class HandlerRef {

    private final String handlerName;
    private final String actorIdentifier;

    public HandlerRef(String actorIdentifier, String handlerName) {
        this.actorIdentifier = actorIdentifier;
        this.handlerName = handlerName;
    }
}
