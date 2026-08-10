package net.minecraft.client.gamemode;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.GuiScreen;
import net.minecraft.client.gui.screens.container.inventory.CreativeScreen;
import net.minecraft.game.item.weapons.Sword;

public class CreativeMode extends GameMode {
    public CreativeMode(Minecraft minecraft) {
        super(minecraft);
        this.entityDist = 6.0;
        this.blockDist = 5.0F;
    }

    @Override
    public void clickBlock(int x, int y, int z, int side) {
        if (this.mc.player.getCurrentEquippedItem() != null) {
            if (!(this.mc.player.getCurrentEquippedItem().getItem() instanceof Sword)) {
                super.clickBlock(x, y, z, side);
            }
        } else {
            super.clickBlock(x, y, z, side);
        }
    }

    @Override
    public void onUpdate() {
        this.mc.sndManager.playRandomMusic(4);
    }

    @Override
    public void swapItem(int id, int meta) {
        this.mc.player.inventory.swapItem(id, meta);
    }

    @Override
    public boolean shouldDrawHUD() {
        return false;
    }

    @Override
    public GuiScreen displayInventory() {
        return new CreativeScreen(this.mc.player);
    }
}
