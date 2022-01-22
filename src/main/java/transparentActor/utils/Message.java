package transparentActor.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import transparentActor.actor.HandlerRef;


@Getter
@RequiredArgsConstructor
public class Message {

    private final HandlerRef handlerRef;

}
