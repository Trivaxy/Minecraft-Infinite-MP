package net.minecraft.game.block.machines;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;
import net.minecraft.util.math.MathHelper;

public class Fan extends Block {
    private Icon front;
    private Icon animation;

    public Fan(int blockID) {
        super(blockID, MaterialList.metal);
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("machines/fan_side");
        this.front = ir.registerIcon("machines/fan_front");
        this.animation = ir.registerIcon("machines/fan");
    }

    @Override
    public Icon getTexture(WorldSource world, int xCoord, int yCoord, int zCoord, int side) {
        int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
        int newMeta = meta;
        if (meta > 5) {
            newMeta -= 4;
        }

        if (side != newMeta) {
            return this.blockIcon;
        } else {
            return meta > 5 ? this.animation : this.front;
        }
    }

    @Override
    public Icon getIcon(int side, int meta) {
        return side != 1 && side != 0 ? (side == 3 ? this.front : this.blockIcon) : this.blockIcon;
    }

    @Override
    public void onPlaced(World world, Player player, int x, int y, int z, int side) {
        super.onPlaced(world, player, x, y, z, side);
        int facing = MathHelper.floor_double(player.yaw * 4.0F / 360.0F + 0.5) & 3;
        if (facing == 0) {
            world.setBlockMetadataWithNotify(x, y, z, 2);
        }

        if (facing == 1) {
            world.setBlockMetadataWithNotify(x, y, z, 5);
        }

        if (facing == 2) {
            world.setBlockMetadataWithNotify(x, y, z, 3);
        }

        if (facing == 3) {
            world.setBlockMetadataWithNotify(x, y, z, 4);
        }

        if (!world.isOnline) {
            this.updateState(world, x, y, z);
        }
    }

    @Override
    public void onNeighborChange(World world, int x, int y, int z, int id) {
        if (!world.isOnline) {
            this.updateState(world, x, y, z);
        }
    }

    private void updateState(World world, int x, int y, int z) {
        int metadata = world.getBlockMetadata(x, y, z);
        if (world.hasNeighborSignal(x, y, z) && metadata < 6) {
            world.setBlockMetadata(x, y, z, metadata + 4);
        } else if (!world.hasNeighborSignal(x, y, z) && metadata >= 6) {
            world.setBlockMetadata(x, y, z, metadata - 4);
        }
    }
}
