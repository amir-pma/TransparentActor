package transparentActor.utils;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
public class Message {

    private String handlerName;
    private String actorIdentifier;

    public Message(String actorIdentifier, String handlerName) {
        this.handlerName = handlerName;
        this.actorIdentifier = actorIdentifier;
    }
}
