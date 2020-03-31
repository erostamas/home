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
                    try {
                        sender.sendMessage(new Message("heartbeat:" + InetAddress.getLocalHost().getHostName(), entry.getKey(), 50004));
                    }  catch (Exception e) {
                        System.out.println("[HeartbeatSender] EXCEPTION");
                    }
                }
            } catch (InterruptedException e) {
                System.err.println("[HeartbeatSender] Lock interrupted");
            } finally {
                _serversMutex.release();
            }
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                System.out.println("[HeartbeatSender] EXCEPTION SLEEP");
            }
            
        }
    }

    public void addServer(InetAddress serverAddress) {
        System.out.println("[HeartbeatSender] Trying to add server: " + serverAddress);
        try {
            _serversMutex.acquire();
            _servers.put(serverAddress, System.currentTimeMillis());
            System.out.println("[HeartbeatSender] Added server: " + serverAddress);
        } catch (InterruptedException e) {
            System.err.println("[HeartbeatSender] Lock interrupted");
        } finally {
            _serversMutex.release();
        }
    }
};