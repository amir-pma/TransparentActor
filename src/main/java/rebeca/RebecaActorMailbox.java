package rebeca;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import transparentActor.utils.Buffer;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
public class RebecaActorMailbox extends Buffer<RebecaMessage, ArrayList<RebecaMessage>> {

    private Integer mailBoxSize;

    @Override
    public void initializeMessages() {
        this.messages = new ArrayList<>();
    }

    public Boolean contains(RebecaMessage message) {
        return getMessages().contains(message);
    }

    public Integer size() {
        return messages.size();
    }

}
