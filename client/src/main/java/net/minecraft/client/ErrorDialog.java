package net.minecraft.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import net.minecraft.util.logging.Logging;
import org.lwjgl.Sys;

public class ErrorDialog extends JFrame {
    private static final long serialVersionUID = 1L;
    static final int MAGIC_FONT_SIZE = 22;
    private final JPanel contentPanel = new JPanel();
    private String title = "Minecraft has crashed!";
    private String logFileName = Logging.currentFileDate + ".log";

    public static void showDlg(String crashDescription, Throwable exception) {
        ErrorDialog dialog = new ErrorDialog(crashDescription, exception);
        dialog.setDefaultCloseOperation(3);
        dialog.setVisible(true);
    }

    private String throwableAsString(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        pw.close();
        String out = sw.toString();
        out = out.replaceAll("\t", "  ");
        return out.replaceAll("\r", "");
    }

    private ErrorDialog(String crashDescription, Throwable exception) {
        this.setTitle("Minecraft - Fatal Error!");
        int fontsize = 12;
        String stacktrace = "";
        stacktrace = stacktrace + "---- D E S C R I P T I O N ----\n";
        stacktrace = stacktrace + crashDescription + "\n";
        stacktrace = stacktrace + "\n";
        stacktrace = stacktrace + "---- S T A C K   T R A C E ----\n";
        stacktrace = stacktrace + this.throwableAsString(exception) + "\n";
        stacktrace = stacktrace + "---- S Y S T E M   I N F O ----\n";
        stacktrace = stacktrace + "Version: Minecraft Infinite v1.0\n";
        stacktrace = stacktrace + "OS: " + System.getProperty("os.name") + " " + System.getProperty("os.arch") + "\n";

        try {
            stacktrace = stacktrace + "LWJGL: " + Sys.getVersion() + "\n";
        } catch (Throwable e) {
            e.printStackTrace();
        }

        stacktrace = stacktrace + "Java: " + System.getProperty("java.version") + ", Vendor: " + System.getProperty("java.vendor") + "\n";

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            this.setIconImage(ImageIO.read(this.getClass().getResource("/icon_error.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.setResizable(false);
        int ww = 854;
        int hh = 480;

        int xpos;
        int ypos;
        try {
            DisplayMode dm = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
            xpos = dm.getWidth() / 4;
            ypos = dm.getHeight() / 4;
        } catch (Throwable e) {
            System.err.println("Failed to set window relative position");
            xpos = 100;
            ypos = 100;
        }

        this.setBounds(xpos, ypos, 854, 480);
        this.getContentPane().setLayout(new BorderLayout());
        this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.getContentPane().add(this.contentPanel, "Center");
        this.contentPanel.setLayout(new BorderLayout(0, 0));
        JPanel titlePanel = new JPanel();
        this.contentPanel.add(titlePanel, "North");
        titlePanel.setLayout(new BorderLayout(0, 0));
        JLabel titleLabel = new JLabel(this.title);
        titleLabel.setHorizontalAlignment(2);
        titleLabel.setFont(UIManager.getFont("Label.font").deriveFont(1, 16.0F));
        titlePanel.add(titleLabel, "North");
        JPanel descPane = new JPanel();
        titlePanel.add(descPane, "West");
        descPane.setLayout(new GridLayout(0, 1, 0, 0));
        JLabel line1;
        if (this.logFileName == null) {
            line1 = new JLabel("More debug information unavailable.");
        } else {
            line1 = new JLabel("More debug information can be found in the '" + this.logFileName + "' file.");
        }

        descPane.add(line1);
        line1 = new JLabel("Click the 'Copy details to clipboard' button to copy full stacktrace to clipboard.");
        descPane.add(line1);
        line1 = new JLabel("It is highly recommended that you restart the game to prevent further issues.");
        descPane.add(line1);
        Component rigidArea = Box.createRigidArea(new Dimension(20, 8));
        titlePanel.add(rigidArea, "South");
        titlePanel = new JPanel();
        titlePanel.setBorder(new TitledBorder(null, "Exception Details:", 4, 2, null, new Color(0, 0, 0)));
        this.contentPanel.add(titlePanel, "Center");
        titlePanel.setLayout(new BorderLayout(0, 0));
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(22);
        titlePanel.add(scrollPane, "Center");
        JTextArea vAREA = new JTextArea(stacktrace);
        vAREA.setBackground(Color.WHITE);
        int i = 0;
        if (fontsize == 22) {
            i = 1;
        }

        vAREA.setFont(new Font("Monospaced", i, fontsize));
        vAREA.setEditable(false);
        scrollPane.setViewportView(vAREA);
        titlePanel = new JPanel();
        titlePanel.setBorder(new BevelBorder(1, null, null, null, null));
        titlePanel.setLayout(new FlowLayout(2));
        this.getContentPane().add(titlePanel, "South");
        JButton copyButton = new JButton("Copy details to clipboard");
        final String copy = stacktrace;
        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (copy != null) {
                    ErrorDialog.setClipboardString(copy);
                }
            }
        });
        titlePanel.add(copyButton);
        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        titlePanel.add(quitButton);
        this.getRootPane().setDefaultButton(quitButton);
    }

    static void setClipboardString(String tring) {
        try {
            StringSelection strSel = new StringSelection(tring);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(strSel, null);
        } catch (Exception var2) {
        }
    }
}
