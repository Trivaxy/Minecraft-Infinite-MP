package net.minecraft.server.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import net.minecraft.SharedConstants;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandSource;
import net.minecraft.server.MinecraftServer;

public class ServerGUI extends JComponent implements CommandSource {
    private static final long serialVersionUID = 0L;
    public static Logger logger = Logger.getLogger("Minecraft");
    private MinecraftServer mcServer;

    public static void initGui(final MinecraftServer minecraftServer0) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception var5) {
        }

        ServerGUI serverGUI1 = new ServerGUI(minecraftServer0);
        JFrame jFrame2 = new JFrame("Minecraft server");

        try {
            jFrame2.setIconImages(SharedConstants.loadAWTIcons(ServerGUI.class.getResource("/icon16.png"), ServerGUI.class.getResource("/icon32.png")));
        } catch (Exception var4) {
        }

        jFrame2.add(serverGUI1);
        jFrame2.pack();
        jFrame2.setLocationRelativeTo((Component)null);
        jFrame2.setVisible(true);
        jFrame2.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent1) {
                minecraftServer0.initiateShutdown();

                while (!minecraftServer0.serverStopped) {
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException interruptedException3) {
                        interruptedException3.printStackTrace();
                    }
                }

                System.exit(0);
            }
        });
    }

    public ServerGUI(MinecraftServer minecraftServer1) {
        this.mcServer = minecraftServer1;
        this.setPreferredSize(new Dimension(854, 480));
        this.setLayout(new BorderLayout());

        try {
            this.add(this.getLogComponent(), "Center");
            this.add(this.getStatsComponent(), "West");
        } catch (Exception exception3) {
            exception3.printStackTrace();
        }
    }

    private JComponent getStatsComponent() {
        JPanel jPanel1 = new JPanel(new BorderLayout());
        jPanel1.add(new GuiStatsComponent(), "North");
        jPanel1.add(this.getPlayerListComponent(), "Center");
        jPanel1.setBorder(new TitledBorder(new EtchedBorder(), "Stats"));
        return jPanel1;
    }

    private JComponent getPlayerListComponent() {
        PlayerListBox playerListBox1 = new PlayerListBox(this.mcServer);
        JScrollPane jScrollPane2 = new JScrollPane(playerListBox1, 22, 30);
        jScrollPane2.setBorder(new TitledBorder(new EtchedBorder(), "Players"));
        return jScrollPane2;
    }

    private JComponent getLogComponent() {
        JPanel jPanel1 = new JPanel(new BorderLayout());
        JTextArea jTextArea2 = new JTextArea();
        logger.addHandler(new GuiLogOutputHandler(jTextArea2));
        JScrollPane jScrollPane3 = new JScrollPane(jTextArea2, 22, 30);
        jTextArea2.setEditable(false);
        final JTextField jTextField4 = new JTextField();
        jTextField4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String string2 = jTextField4.getText().trim();
                if (string2.length() > 0) {
                    if (string2.startsWith("/")) {
                        string2 = string2.substring(1);
                    }

                    ServerGUI.this.mcServer.addCommand(string2, ServerGUI.this);
                }

                jTextField4.setText("");
            }
        });
        jTextArea2.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent focusEvent1) {
            }
        });
        jPanel1.add(jScrollPane3, "Center");
        jPanel1.add(jTextField4, "South");
        jPanel1.setBorder(new TitledBorder(new EtchedBorder(), "Log and chat"));
        return jPanel1;
    }

    @Override
    public void print(String string1) {
        logger.info(SharedConstants.deleteColorCodes(string1));
    }

    @Override
    public String getUsername() {
        return "CONSOLE";
    }

    @Override
    public Command.UserType getType() {
        return Command.UserType.ADMINISTRATOR;
    }

    @Override
    public boolean canRunCommand(Command c) {
        return true;
    }
}
