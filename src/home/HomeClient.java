import erostamas.common.udp_messenger.UdpSender;
import erostamas.common.udp_messenger.UdpReceiver;
import erostamas.common.command_processor.CommandProcessor;

public class HomeClient {

    public static void main (String args[]) {
        UdpReceiver udpReceiver = new UdpReceiver(50003);
        udpReceiver.start();
        HeartbeatSender heartbeatSender = new HeartbeatSender();
        heartbeatSender.start();
        StringCommandAdapter adapter = new StringCommandAdapter(heartbeatSender);
        adapter.registerMessageReceiver(udpReceiver);
        CommandProcessor commandProcessor = new CommandProcessor();
        commandProcessor.registerCommandAdapter(adapter);
        
        while(true) {
            commandProcessor.processCommands();
            try {
                Thread.sleep(1000);
            } catch (Exception e) {

            }
        }
    }
}