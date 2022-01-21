package ta.actor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class HandlerRef {

    private final String handlerName;
    private final String actorIdentifier;

}
