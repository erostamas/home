import erostamas.common.udp_messenger.UdpSender;
import erostamas.common.udp_messenger.UdpReceiver;
import erostamas.common.command_processor.CommandProcessor;
import erostamas.common.Message;

import javax.swing.*;
import java.awt.*;

public class Home extends JPanel {

    private JPanel _serversPanel;
    private static DefaultListModel<String> _serverList = new DefaultListModel<>();   

    public Home() {
        initializeUI();
    }

    private void initializeUI() {
        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel _serversPanel = new JPanel();
        _serversPanel.setLayout(new GridLayout());
        _serversPanel.add(new JLabel("SERVERS"));

        JList<String> list = new JList<>(_serverList);  
        _serversPanel.add(list);

        // Add Dashboard Tab
        tabbedPane.addTab("Dashboard", _serversPanel);

        JPanel transactionPanel = new JPanel();
        transactionPanel.add(new JLabel("Transaction"));

        // Add Transactions Tab
        tabbedPane.addTab("Transaction", transactionPanel);

        JPanel accountPanel = new JPanel();
        accountPanel.add(new JLabel("Account"));

        // Add Account Tab
        tabbedPane.addTab("Account", accountPanel);

        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(500, 200));
        this.add(tabbedPane, BorderLayout.CENTER);
    }

    public static void showFrame() {
        JPanel panel = new Home();
        panel.setOpaque(true);

        JFrame frame = new JFrame("Simple Tabbed Pane Demo");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main (String args[]) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Home.showFrame();
            }
        });

        UdpReceiver udpReceiver = new UdpReceiver(50003);
        udpReceiver.start();
        HeartbeatSender heartbeatSender = new HeartbeatSender();
        heartbeatSender.start();
        StringCommandAdapter adapter = new StringCommandAdapter(heartbeatSender);
        adapter.registerMessageReceiver(udpReceiver);
        CommandProcessor commandProcessor = new CommandProcessor();
        commandProcessor.registerCommandAdapter(adapter);
        HeartbeatHandler heartbeatHandler = new HeartbeatHandler(_serverList);
        heartbeatHandler.start();
        
        while(true) {
            commandProcessor.processCommands();
            for (String server : heartbeatHandler.getActiveServers()) {
                System.out.println("ACTIVE SERVER : '" + server + "'");
            }
            try {
                Thread.sleep(1000);
            } catch (Exception e) {

            }
        }
    }

    public static JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }
}