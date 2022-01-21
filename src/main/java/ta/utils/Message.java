package ta.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ta.actor.HandlerRef;


@Getter
@RequiredArgsConstructor
public class Message {

    private final HandlerRef handlerRef;

}
