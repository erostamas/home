import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.HashMap;
import java.util.concurrent.Semaphore;
import javax.swing.DefaultListModel;

import erostamas.common.udp_messenger.UdpSender;
import erostamas.common.udp_messenger.UdpReceiver;
import erostamas.common.Message;

public class HeartbeatHandler extends Thread {
    static final long HB_TIMEOUT_MSEC = 5000;

    private UdpReceiver _udpReceiver = new UdpReceiver(50004);
    private HashMap<String, Long> _activeServers = new HashMap<String, Long>();
    private Semaphore _activeServersMutex = new Semaphore(1);
    private DefaultListModel<String> _serverList = new DefaultListModel<>();  

    private class RequestHeartbeat extends TimerTask {
        public void run() {
            UdpSender _udpSender = new UdpSender();
            try {
                _udpSender.sendMessage(new Message("request_heartbeat", InetAddress.getByName("192.168.1.255"), 50003));
            } catch (Exception e) {
                // TODO
            } 
        }
    }

    public void run() {
        while (true) {
            try {
                _activeServersMutex.acquire();
                for (Message message : _udpReceiver.getIncomingMessages()) {
                    String msg = message.getMessageContent();
                    String[] split = msg.split(":");
                    if (split.length > 1) {
                        _activeServers.put(split[1], System.currentTimeMillis());
                    }
                }
                _activeServers.entrySet().removeIf(entry -> entry.getValue() < System.currentTimeMillis() - HB_TIMEOUT_MSEC);
                _serverList.clear();
                for (String server : _activeServers.keySet()) {
                    _serverList.addElement(server);
                }
            } catch (InterruptedException e) {
                System.err.println("[HeartbeatSender] Lock interrupted");
            } finally {
                _activeServersMutex.release();
            }
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                System.out.println("[HeartbeatSender] EXCEPTION SLEEP");
            }
            
        }
    }

    private ArrayList<String> _activeClients;

    public HeartbeatHandler(DefaultListModel<String> serverList) {
        _serverList = serverList;
        Timer timer = new Timer();
        timer.schedule(new RequestHeartbeat(), 0, 50000);
        _udpReceiver.start();
    }

    public ArrayList<String> getActiveServers() {
        ArrayList<String> ret = new ArrayList<String>();
        for (String server : _activeServers.keySet()) {
            ret.add(server);
        }
        return ret;
    }
};