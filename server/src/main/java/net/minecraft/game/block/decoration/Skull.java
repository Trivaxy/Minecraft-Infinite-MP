package net.minecraft.game.block.decoration;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;
import net.minecraft.util.math.MathHelper;

public class Skull extends Block {
    private Icon top;
    private Icon side;
    private Icon bottom;

    public Skull(int var1) {
        super(var1, MaterialList.circuits);
        this.setBounds(0.25, 0.0, 0.25, 0.75, 0.5, 0.75);
    }

    @Override
    public boolean particles3D() {
        return false;
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public void onPlaced(World world, Player player, int x, int y, int z, int side) {
        super.onPlaced(world, player, x, y, z, side);
        int facingAngle = MathHelper.floor_double(player.yaw * 4.0F / 360.0F + 0.5) & 3;
        if (facingAngle == 0) {
            world.setBlockMetadataWithNotify(x, y, z, 2);
        }

        if (facingAngle == 1) {
            world.setBlockMetadataWithNotify(x, y, z, 5);
        }

        if (facingAngle == 2) {
            world.setBlockMetadataWithNotify(x, y, z, 3);
        }

        if (facingAngle == 3) {
            world.setBlockMetadataWithNotify(x, y, z, 4);
        }
    }

    @Override
    public void registerIcons(IconRegister ir) {
        super.registerIcons(ir);
        this.top = ir.registerIcon("skull_top");
        this.bottom = ir.registerIcon("skull_bottom");
        this.side = ir.registerIcon("skull_side");
    }

    @Override
    public Icon getIcon(int side, int meta) {
        return side == 1 ? this.top : (side == 0 ? this.bottom : (side == 3 ? this.blockIcon : this.side));
    }

    @Override
    public Icon getTexture(WorldSource world, int xCoord, int yCoord, int zCoord, int side) {
        if (side == 1) {
            return this.top;
        }

        if (side == 0) {
            return this.bottom;
        }

        int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
        return side != meta ? this.side : this.blockIcon;
    }
}
