package net.minecraft.server.gui;

import java.util.Vector;
import javax.swing.JList;
import net.minecraft.server.MinecraftServer;

public class PlayerListBox extends JList<String> implements IUpdatePlayerListBox {
    private static final long serialVersionUID = 0L;
    private MinecraftServer mcServer;
    private int updateCounter = 0;

    public PlayerListBox(MinecraftServer minecraftServer1) {
        this.mcServer = minecraftServer1;
        minecraftServer1.addPlayerToList(this);
    }

    @Override
    public void update() {
        if (this.updateCounter++ % 20 == 0) {
            Vector<String> vector1 = new Vector<>();

            for (int i2 = 0; i2 < this.mcServer.configManager.playerEntities.size(); i2++) {
                vector1.add(this.mcServer.configManager.playerEntities.get(i2).username);
            }

            this.setListData(vector1);
        }
    }
}
