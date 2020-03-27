import erostamas.common.udp_messenger.UdpSender;

public class Home {
    public static void main (String args[]) {
        if (args.length < 2) {
            System.err.println("Usage: Home <target_ip_address> <target_port>");
            System.exit(1);
        }

        int port = 0;
        try {
            port = Integer.parseInt(args[1]);
        } catch (Exception e) {
            System.err.println("Cannot convert port to integer: " + args[1]);
            System.exit(1);
        }
        UdpSender udpSender = new UdpSender(args[0], port);
        while(true) {
            udpSender.sendMessage(Long.toString(System.currentTimeMillis()/1000));
            try {
                Thread.sleep(1000);
            } catch (Exception e) {

            }
        }
    }
}