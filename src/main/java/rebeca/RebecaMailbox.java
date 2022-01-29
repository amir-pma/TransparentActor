package rebeca;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import transparentActor.utils.Buffer;

@Getter
@Setter
@AllArgsConstructor
public class RebecaMailbox extends Buffer {

    private Integer mailBoxSize;

    public Boolean contains(RebecaMessage message) {
        return getMessages().contains(message);
    }

    public Integer size() {
        return messages.size();
    }

}
