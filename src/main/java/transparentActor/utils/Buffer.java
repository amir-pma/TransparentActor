package transparentActor.utils;


import lombok.Getter;

import java.util.Collection;

@Getter
public abstract class Buffer<M extends Message, C extends Collection<M>> {

    protected C messages;

    public Buffer() {
        initializeMessages();
    }

    public abstract void initializeMessages();

    public Boolean insert(M message) {
        return messages.add(message);
    }

    public Boolean remove(M message) {
        return messages.remove(message);
    }

}
