package net.minecraft.client.gamemode;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.GuiScreen;
import net.minecraft.client.gui.screens.container.inventory.AdventureScreen;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.item.ItemStack;

public class AdventureMode extends SurvivalMode {
    public AdventureMode(Minecraft minecraft) {
        super(minecraft);
    }

    @Override
    public void updateBreakTime(int x, int y, int z, int side) {
        int id = this.mc.world.getBlockId(x, y, z);
        int meta = this.mc.world.getBlockMetadata(x, y, z);
        if (id != 0 && this.canHarvestBlock(BlockList.blocks[id], meta)) {
            super.updateBreakTime(x, y, z, side);
        }
    }

    @Override
    public GuiScreen displayInventory() {
        return new AdventureScreen(this.mc.player);
    }

    public boolean canHarvestBlock(Block block, int meta) {
        if (block.breakStrength(this.mc.player, meta) <= 0.0F) {
            return true;
        }

        ItemStack stack = this.mc.player.inventory.getStackInSlot(this.mc.player.inventory.currentSlot);
        if (this.mc.player.getUsing()) {
            stack = this.mc.player.inventory.getStackInSlot(44);
        }

        return stack != null ? stack.canHarvestBlock(block) : false;
    }
}
