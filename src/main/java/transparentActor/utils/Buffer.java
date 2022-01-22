package transparentActor.utils;


import lombok.Getter;

import java.util.ArrayList;

@Getter
public class Buffer {

    private final ArrayList<Message> messages = new ArrayList<>();

    public Boolean insert(Message message) {
        return messages.add(message);
    }

    public Boolean remove(Message message) {
        return messages.remove(message);
    }

    public Integer size() {
        return messages.size();
    }

    public void emptyBuffer() {
        messages.removeAll(messages);
    }

}