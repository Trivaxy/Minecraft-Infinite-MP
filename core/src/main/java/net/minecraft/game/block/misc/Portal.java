package net.minecraft.game.block.misc;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.tileentity.other.PortalEntity;
import net.minecraft.game.block.utility.ContainerBlock;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;

public class Portal extends ContainerBlock {
    public static boolean breakPortal = true;

    public Portal(int id, Material mat) {
        super(id, mat);
        this.setLightValue(1.0F);
    }

    @Override
    public void modifyOutline(WorldSource world, int x, int y, int z) {
        this.setBounds(0.0, 0.0, 0.0, 1.0, 0.8625F, 1.0);
    }

    @Override
    public boolean renderFace(WorldSource world, int x, int y, int z, int side) {
        return side != 1 ? false : super.renderFace(world, x, y, z, side);
    }

    @Override
    public void getCollisionBox(World world, int x, int y, int z, BoundingBox bb, ArrayList<BoundingBox> bounds) {
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.NONE;
    }

    @Override
    public boolean canCollideWith(int i, boolean z) {
        return false;
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public void spawnParticles(World world, int x, int y, int z, Random rand) {
        double xOffset = x + rand.nextFloat();
        double yOffset = y + 0.8F;
        double zOffset = z + rand.nextFloat();
        world.spawnParticle(ParticleType.SMOKE, xOffset, yOffset, zOffset, 0.0, 0.0, 0.0);
    }

    @Override
    public void onNeighborChange(World world, int x, int y, int z, int id) {
        if (breakPortal) {
            world.setBlockWithNotify(x, y, z, 0);
        }
    }

    @Override
    public TileEntity getBlockEntity() {
        return new PortalEntity();
    }
}
