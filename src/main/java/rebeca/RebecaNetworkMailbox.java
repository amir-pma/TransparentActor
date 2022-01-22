package rebeca;

import lombok.Getter;
import lombok.Setter;
import transparentActor.utils.Buffer;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class RebecaNetworkMailbox extends Buffer {

    public Boolean contains(RebecaMessage message) {
        return getMessages().contains(message);
    }

    public List<RebecaMessage> getBuff(String senderName) {
        return getMessages().stream()
                .filter(message -> ((RebecaMessage)message).getSenderName().equals(senderName))
                .map(message -> (RebecaMessage)message)
                .collect(Collectors.toList());
    }

}
