package net.minecraft.game.block.misc;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.misc.PrimedTNT;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class TNT extends Block {
    private Icon top;
    private Icon bottom;

    public TNT(int blockid) {
        super(blockid, MaterialList.tnt);
    }

    @Override
    public Icon getIcon(int side, int meta) {
        return side == 0 ? this.bottom : (side == 1 ? this.top : this.blockIcon);
    }

    @Override
    public void onExplode(World world, int xCoord, int yCoord, int zCoord) {
        if (!world.isOnline) {
            PrimedTNT tnt = new PrimedTNT(world, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
            tnt.fuse = world.rand.nextInt(tnt.fuse / 4) + tnt.fuse / 8;
            world.spawnEntity(tnt);
        }
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("tnt_side");
        this.top = ir.registerIcon("tnt_top");
        this.bottom = ir.registerIcon("tnt_bottom");
    }

    @Override
    public boolean blockActivated(World world, int x, int y, int z, Player player) {
        boolean lighterMain = player.getCurrentEquippedItem() != null
            && (
                player.getCurrentEquippedItem().itemID == ItemList.striker.id
                    || player.getCurrentEquippedItem().itemID == ItemList.axePheonix.id
                    || player.getCurrentEquippedItem().itemID == ItemList.shovelPheonix.id
                    || player.getCurrentEquippedItem().itemID == ItemList.pickaxePheonix.id
            );
        boolean lighterOff = player.inventory.getOffHandItem() != null
            && (
                player.inventory.getOffHandItem().itemID == ItemList.striker.id
                    || player.inventory.getOffHandItem().itemID == ItemList.axePheonix.id
                    || player.inventory.getOffHandItem().itemID == ItemList.shovelPheonix.id
                    || player.inventory.getOffHandItem().itemID == ItemList.pickaxePheonix.id
            );
        if (!lighterMain && !lighterOff) {
            return super.blockActivated(world, x, y, z, player);
        }

        PrimedTNT tnt = new PrimedTNT(world, x + 0.5, y + 0.5, z + 0.5);
        world.spawnEntity(tnt);
        world.playEntitySound(tnt, "random.fuse", 1.0F, 1.0F);
        world.setBlockWithNotify(x, y, z, 0);
        if (lighterMain) {
            player.getCurrentEquippedItem().damageItem(1, player);
        } else if (lighterOff) {
            player.inventory.getOffHandItem().damageItem(1, player);
        }

        player.addStat(StatList.tntUse, 1);
        return true;
    }
}
