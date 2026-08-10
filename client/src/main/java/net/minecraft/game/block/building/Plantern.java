package net.minecraft.game.block.building;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;
import net.minecraft.util.math.MathHelper;

public class Plantern extends Block {
    private Icon top;
    private Icon front;

    public Plantern(int blockid, Material material) {
        super(blockid, material);
    }

    @Override
    public boolean isRotatable(int id) {
        return true;
    }

    @Override
    public void onPlaced(World world, Player player, int x, int y, int z, int side) {
        super.onPlaced(world, player, x, y, z, side);
        int meta = MathHelper.floor_double(player.yaw * 4.0F / 360.0F + 0.5) & 3;
        if (meta == 0) {
            world.setBlockMetadataWithNotify(x, y, z, 2);
        }

        if (meta == 1) {
            world.setBlockMetadataWithNotify(x, y, z, 5);
        }

        if (meta == 2) {
            world.setBlockMetadataWithNotify(x, y, z, 3);
        }

        if (meta == 3) {
            world.setBlockMetadataWithNotify(x, y, z, 4);
        }
    }

    @Override
    public Icon getTexture(WorldSource world, int xCoord, int yCoord, int zCoord, int side) {
        if (side != 1 && side != 0) {
            return side != world.getBlockMetadata(xCoord, yCoord, zCoord) ? this.blockIcon : this.front;
        } else {
            return this.top;
        }
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("fruits/pumpkinSide");
        this.front = ir.registerIcon("fruits/plantern");
        this.top = ir.registerIcon("fruits/pumpkinTop");
    }

    @Override
    public Icon getIcon(int side, int meta) {
        return side == 1 ? this.top : (side == 0 ? this.top : (side == 3 ? this.front : this.blockIcon));
    }
}
