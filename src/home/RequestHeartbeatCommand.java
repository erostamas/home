import java.net.InetAddress;

import erostamas.common.ICommand;

public class RequestHeartbeatCommand implements ICommand {

    private HeartbeatSender _heartbeatSender;
    private InetAddress _serverAddress;

    RequestHeartbeatCommand(HeartbeatSender heartbeatSender, InetAddress serverAddress) {
        _heartbeatSender = heartbeatSender;
        _serverAddress = serverAddress;
    }

    @Override
    public void execute() {
        _heartbeatSender.addServer(_serverAddress);
    }
};