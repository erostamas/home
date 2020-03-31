import erostamas.common.udp_messenger.UdpSender;
import erostamas.common.udp_messenger.UdpReceiver;
import erostamas.common.command_processor.CommandProcessor;

import javax.swing.*;
import java.awt.*;

public class Home extends JPanel {

    public Home() {
        initializeUI();
    }

    private void initializeUI() {
        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel dashboardPanel = new JPanel();
        dashboardPanel.add(new JLabel("Dashboard"));
        dashboardPanel.add(new JScrollPane());

        // Add Dashboard Tab
        tabbedPane.addTab("Dashboard", dashboardPanel);

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
        if (args.length < 2) {
            System.err.println("Usage: Home <target_ip_address> <target_port>");
            System.exit(1);
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Home.showFrame();
            }
        });


        int port = 0;
        try {
            port = Integer.parseInt(args[1]);
        } catch (Exception e) {
            System.err.println("Cannot convert port to integer: " + args[1]);
            System.exit(1);
        }
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

    public static JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }
}