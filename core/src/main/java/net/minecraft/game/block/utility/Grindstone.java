package net.minecraft.game.block.utility;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;
import net.minecraft.util.math.MathHelper;

public class Grindstone extends Block {
    private Icon rot;
    private Icon rot2;

    public Grindstone(int id, Material mat) {
        super(id, mat);
    }

    @Override
    public boolean placeableAt(World world, int x, int y, int z) {
        return world.getBlockMaterial(x, y - 1, z).isSolid();
    }

    @Override
    public boolean canStay(World world, int xCoord, int yCoord, int zCoord) {
        return !world.getBlockMaterial(xCoord, yCoord - 1, zCoord).isSolid();
    }

    @Override
    public void onNeighborChange(World world, int x, int y, int z, int id) {
        if (!world.getBlockMaterial(x, y - 1, z).isSolid()) {
            this.dropBlock(world, x, y, z, 0);
            world.setBlockWithNotify(x, y, z, 0);
        }
    }

    @Override
    public void setStateBounds(World world, int x, int y, int z) {
        if (world.getBlockMetadata(x, y, z) != 4 && world.getBlockMetadata(x, y, z) != 5) {
            this.setBounds(0.25, 0.15F, 0.15F, 0.75, 0.85F, 0.85F);
        } else {
            this.setBounds(0.15F, 0.15F, 0.25, 0.85F, 0.85F, 0.75);
        }
    }

    @Override
    public Icon getIcon(int side, int meta) {
        if (side != 0 && side != 1) {
            if (meta != 4 && meta != 5 && meta != 0 || side != 4 && side != 5) {
                return meta == 4 || meta == 5 || meta == 0 || side != 2 && side != 3 ? this.blockIcon : this.rot;
            } else {
                return this.rot;
            }
        } else {
            return meta != 4 && meta != 5 && meta != 0 ? this.rot : this.rot2;
        }
    }

    @Override
    public boolean particles3D() {
        return false;
    }

    @Override
    public void registerIcons(IconRegister r) {
        this.blockIcon = r.registerIcon("workstations/grindstone");
        this.rot = r.registerIcon("workstations/grindstone_round");
        this.rot2 = r.registerIcon("workstations/grindstone_rot");
    }

    @Override
    public void onPlaced(World world, Player player, int x, int y, int z, int side) {
        super.onPlaced(world, player, x, y, z, side);
        int angle = MathHelper.floor_double(player.yaw * 4.0F / 360.0F + 0.5) & 3;
        if (angle == 0) {
            world.setBlockMetadataWithNotify(x, y, z, 2);
        }

        if (angle == 1) {
            world.setBlockMetadataWithNotify(x, y, z, 5);
        }

        if (angle == 2) {
            world.setBlockMetadataWithNotify(x, y, z, 3);
        }

        if (angle == 3) {
            world.setBlockMetadataWithNotify(x, y, z, 4);
        }
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public boolean renderFace(WorldSource world, int x, int y, int z, int side) {
        return true;
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.GRINDSTONE;
    }

    @Override
    public final boolean blockActivated(World world, int x, int y, int z, Player player) {
        if (!world.isOnline) {
            player.displayGUIGrindstone(x, y, z);
        }

        return true;
    }
}
