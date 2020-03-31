import java.net.InetAddress;
import java.util.concurrent.Semaphore;
import java.util.ArrayList;
import java.util.HashMap;

import erostamas.common.udp_messenger.UdpSender;
import erostamas.common.Message;

public class HeartbeatSender extends Thread {
    static final long REQUEST_TIMEOUT_MSEC = 60000;

    HashMap<InetAddress, Long> _servers = new HashMap<InetAddress, Long>();
    private Semaphore _serversMutex = new Semaphore(1);

    public void run() {
        UdpSender sender = new UdpSender();
        while (true) {
            try {
                _serversMutex.acquire();
                _servers.entrySet().removeIf(entry -> entry.getValue() < System.currentTimeMillis() - REQUEST_TIMEOUT_MSEC);
        
                for (HashMap.Entry<InetAddress, Long> entry : _servers.entrySet()) {
                    sender.sendMessage(new Message(Long.toString(System.currentTimeMillis()/1000), entry.getKey(), 50004));
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {}
            } catch (InterruptedException e) {
                System.err.println("[HeartbeatSender] Lock interrupted");
            } finally {
                _serversMutex.release();
            }
            
        }
    }

    public void addServer(InetAddress serverAddress) {
        try {
            _serversMutex.acquire();
            _servers.put(serverAddress, System.currentTimeMillis());
        } catch (InterruptedException e) {
            System.err.println("[HeartbeatSender] Lock interrupted");
        } finally {
            _serversMutex.release();
        }
    }
};