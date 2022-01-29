package transparentActor.utils;


import lombok.Getter;

import java.util.ArrayList;

@Getter
public class Buffer {

    protected final ArrayList<Message> messages = new ArrayList<>();

    public Boolean insert(Message message) {
        return messages.add(message);
    }

    public Boolean remove(Message message) {
        return messages.remove(message);
    }

}
