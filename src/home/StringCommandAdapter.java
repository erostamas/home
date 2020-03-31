import erostamas.common.ICommandAdapter;
import erostamas.common.ICommand;
import erostamas.common.Message;

public class StringCommandAdapter extends ICommandAdapter {

    private HeartbeatSender _heartbeatSender;

    StringCommandAdapter(HeartbeatSender heartbeatSender) {
        _heartbeatSender = heartbeatSender;
    }

    @Override
    public ICommand convertMessage(Message message) {
        System.out.println("Received command: " + message.getMessageContent());
        if (message.getMessageContent().equals("request_heartbeat")) {
            return new RequestHeartbeatCommand(_heartbeatSender, message.getEndpoint());
        }
        return null;
    }
};