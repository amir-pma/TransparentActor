package LF;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import transparentActor.utils.Buffer;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
public class LFBuffer extends Buffer<LFMessage, ArrayList<LFMessage>> {

    private Integer mailBoxSize;

    @Override
    public void initializeMessages() {
        this.messages = new ArrayList<>();
    }

    public Integer size() {
        return messages.size();
    }

    public LFMessage peek() {
        return null;
    }

}
