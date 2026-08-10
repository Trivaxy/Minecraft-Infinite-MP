package net.minecraft.game.block.building;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class Pillar extends Block {
    private Icon top;
    private Icon front;

    public Pillar(int blockID, Material mat) {
        super(blockID, mat);
    }

    @Override
    public void onPlaced(World world, Player player, int x, int y, int z, int side) {
        super.onPlaced(world, player, x, y, z, side);
        ItemStack stack = player.inventory.getCurrentItem();
        if (player.inventory.canUseItem()) {
            stack = player.inventory.getOffHandItem();
        }

        int meta = stack.itemDmg & 63;
        switch (side) {
            case 0:
            case 1:
            default:
                break;
            case 2:
            case 3:
                meta += 128;
                break;
            case 4:
            case 5:
                meta += 64;
        }

        world.setBlockMetadataWithNotify(x, y, z, meta);
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.LOG;
    }

    @Override
    public void registerIcons(IconRegister r) {
        this.top = r.registerIcon("logs/" + this.name + "_top");
        this.front = r.registerIcon("logs/" + this.name);
    }

    @Override
    public Icon getIcon(int side, int state) {
        int meta = state & 240;
        if (meta < 64) {
            if (side == 1 || side == 0) {
                return this.top;
            }
        } else if (meta < 128) {
            if (side == 5 || side == 4) {
                return this.top;
            }
        } else if (side == 2 || side == 3) {
            return this.top;
        }

        return this.front;
    }
}
