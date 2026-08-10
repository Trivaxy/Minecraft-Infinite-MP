package net.minecraft.game.block.utility;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class Stonecutter extends Block {
    private Icon top;
    private Icon rot;
    private Icon bottom;
    public static Icon saw;

    public Stonecutter(int blockid) {
        super(blockid, MaterialList.rock);
        this.setBounds(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
        this.setLightOpacity(0);
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
    public Block.RenderType getRenderType() {
        return Block.RenderType.SAW;
    }

    @Override
    public Icon getIcon(int side, int meta) {
        if (side == 0) {
            return this.bottom;
        } else if (side == 1) {
            return meta != 4 && meta != 5 ? this.top : this.rot;
        } else {
            return this.blockIcon;
        }
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
    }

    @Override
    public void registerIcons(IconRegister r) {
        this.blockIcon = r.registerIcon("workstations/stonecutter");
        this.top = r.registerIcon("workstations/stonecutter_top");
        this.bottom = r.registerIcon("workstations/stonecutter_bottom");
        this.rot = r.registerIcon("workstations/stonecutter_rotated");
        saw = r.registerIcon("workstations/stonecutter_saw");
    }

    @Override
    public boolean blockActivated(World world, int xCoord, int yCoord, int zCoord, Player entityPlayer) {
        if (!world.isOnline) {
            entityPlayer.displayStonecutterGUI(xCoord, yCoord, zCoord);
            return true;
        } else {
            return true;
        }
    }
}
