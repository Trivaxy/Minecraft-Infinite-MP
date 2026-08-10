package net.minecraft.client.gamemode;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.GuiScreen;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;

public class SpectatorMode extends GameMode {
    public SpectatorMode(Minecraft minecraft) {
        super(minecraft);
        if (minecraft.player != null) {
            minecraft.player.noClip = true;
            minecraft.player.flying = true;
        }
    }

    @Override
    public void onRespawn(Player player) {
        player.noClip = true;
        player.flying = true;
    }

    @Override
    public GuiScreen displayInventory() {
        return null;
    }

    @Override
    public boolean shouldDrawHUD() {
        return false;
    }

    @Override
    public boolean useItemOnBlock(Player player, World world, ItemStack item, int x, int y, int z, int side) {
        return false;
    }

    @Override
    public void interactWithEntity(Player player, Entity entity) {
        if (entity instanceof Mob) {
            this.mc.gameRenderer.cameraMob = (Mob)entity;
        }
    }

    @Override
    public void attackEntity(Player player, Entity entity) {
        if (entity instanceof Mob) {
            this.mc.gameRenderer.cameraMob = (Mob)entity;
        }
    }

    @Override
    public boolean useItem(Player player, World world, ItemStack item) {
        return false;
    }

    @Override
    public boolean removeBlock(int x, int y, int z, int side) {
        return false;
    }
}
