package net.minecraft.server.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.Timer;
import net.minecraft.network.NetworkManager;

public class GuiStatsComponent extends JComponent {
    private static final long serialVersionUID = 0L;
    private int[] memoryUse = new int[256];
    private int updateCounter = 0;
    private String[] displayStrings = new String[10];

    public GuiStatsComponent() {
        this.setPreferredSize(new Dimension(256, 196));
        this.setMinimumSize(new Dimension(256, 196));
        this.setMaximumSize(new Dimension(256, 196));
        new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiStatsComponent.this.updateStats();
            }
        }).start();
        this.setBackground(Color.BLACK);
    }

    private void updateStats() {
        long j1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.gc();
        this.displayStrings[0] = "Memory use: "
            + j1 / 1024L / 1024L
            + " mb ("
            + Runtime.getRuntime().freeMemory() * 100L / Runtime.getRuntime().maxMemory()
            + "% free)";
        this.displayStrings[1] = "Threads: " + NetworkManager.numReadThreads + " + " + NetworkManager.numWriteThreads;
        this.memoryUse[this.updateCounter++ & 0xFF] = (int)(j1 * 100L / Runtime.getRuntime().maxMemory());
        this.repaint();
    }

    @Override
    public void paint(Graphics graphics1) {
        graphics1.setColor(new Color(16777215));
        graphics1.fillRect(0, 0, 256, 192);

        for (int i2 = 0; i2 < 256; i2++) {
            int i3 = this.memoryUse[i2 + this.updateCounter & 0xFF];
            graphics1.setColor(new Color(i3 + 28 << 16));
            graphics1.fillRect(i2, 100 - i3, 1, i3);
        }

        graphics1.setColor(Color.BLACK);

        for (int var4 = 0; var4 < this.displayStrings.length; var4++) {
            String string4 = this.displayStrings[var4];
            if (string4 != null) {
                graphics1.drawString(string4, 32, 116 + var4 * 16);
            }
        }
    }
}
