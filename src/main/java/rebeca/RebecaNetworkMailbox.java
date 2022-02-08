package rebeca;

import lombok.Getter;
import lombok.Setter;
import transparentActor.utils.Buffer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class RebecaNetworkMailbox extends Buffer<RebecaMessage, ArrayList<RebecaMessage>> {

    @Override
    public void initializeMessages() {
        this.messages = new ArrayList<>();
    }

    public Boolean contains(RebecaMessage message) {
        return getMessages().contains(message);
    }

    public List<RebecaMessage> getBuff(String senderName) {
        return getMessages().stream()
                .filter(message -> message.getSenderName().equals(senderName))
                .collect(Collectors.toList());
    }

}
