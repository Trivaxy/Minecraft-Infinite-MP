package net.minecraft.game.block.utility;

import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class ObsidianChest extends LockedChest {
    public ObsidianChest(int blockid) {
        super(blockid);
    }

    @Override
    public void registerIcons(IconRegister r) {
        this.side = r.registerIcon("workstations/obsidianChestSide");
        this.top = r.registerIcon("workstations/obsidianChestTop");
        this.front = r.registerIcon("workstations/obsidianChestFront");
        this.blockIcon = r.registerIcon("workstations/obsidianChestSide");
    }

    @Override
    public boolean blockActivated(World world, int x, int y, int z, Player player) {
        if (world.isBlockFull(x, y + 1, z)) {
            return true;
        } else if (!world.isOnline) {
            player.displayGUIEnderChest(player.inventoryChest, (Inventory)world.getTileEntity(x, y, z));
            return true;
        } else {
            return true;
        }
    }
}
