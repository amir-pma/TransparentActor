package LF;

import lombok.Getter;
import lombok.Setter;
import rebeca.RebecaMessage;
import transparentActor.utils.Buffer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class LFNetworkBuffer extends Buffer<LFMessage, ArrayList<LFMessage>> {

    public Boolean contains(LFMessage message) {
        return getMessages().contains(message);
    }

    @Override
    public void initializeMessages() {
        this.messages = new ArrayList<>();
    }
}
