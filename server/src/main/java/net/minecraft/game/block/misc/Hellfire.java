package net.minecraft.game.block.misc;

import java.util.Random;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class Hellfire extends Block {
    private Icon[] iconArray;

    public Hellfire(int blockid) {
        super(blockid, MaterialList.fire);
        this.setTickable(true);
    }

    @Override
    public BoundingBox getCollisionBox(World world, int xCoord, int yCoord, int zCoord) {
        return null;
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.FIRE;
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public int tickRate() {
        return 20;
    }

    @Override
    public void onTick(World world, int xCoord, int yCoord, int zCoord, Random random) {
        int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
        if (meta < 15) {
            world.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, meta + 1);
            world.forceTick(xCoord, yCoord, zCoord, this.id);
        }

        int chance = 3;
        Block belowBlock = BlockList.blocks[world.getBlockId(xCoord, yCoord - 1, zCoord)];
        if ((meta > chance || world.isRaining()) && belowBlock != BlockList.slowSand && belowBlock != BlockList.soulSoil) {
            world.setBlockWithNotify(xCoord, yCoord, zCoord, BlockList.ash.id);
        }
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    public boolean placeableAt(World world, int xCoord, int yCoord, int zCoord) {
        return world.isBlockFull(xCoord, yCoord - 1, zCoord);
    }

    @Override
    public void onNeighborChange(World world, int xCoord, int yCoord, int zCoord, int side) {
        if (!world.isBlockFull(xCoord, yCoord - 1, zCoord)) {
            world.setBlockWithNotify(xCoord, yCoord, zCoord, 0);
        }
    }

    @Override
    public void onBlockAdded(World world, int xCoord, int yCoord, int zCoord) {
        if (!world.isBlockFull(xCoord, yCoord - 1, zCoord)) {
            world.setBlockWithNotify(xCoord, yCoord, zCoord, 0);
        } else {
            world.forceTick(xCoord, yCoord, zCoord, this.id);
        }
    }

    @Override
    public void spawnParticles(World world, int xCoord, int yCoord, int zCoord, Random random) {
        if (random.nextInt(24) == 0) {
            world.playSoundEffect(xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F, "fire.fire", 1.0F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F);
        }

        for (int i = 0; i < 3; i++) {
            double xOffset = xCoord + random.nextDouble();
            double yOffset = yCoord + random.nextDouble() * 0.5 + 0.5;
            double zOffset = zCoord + random.nextDouble();
            world.spawnParticle(ParticleType.LARGESMOKE, xOffset, yOffset, zOffset, 0.0, 0.0, 0.0);
        }
    }

    public Icon getIcon(int meta) {
        return this.iconArray[meta];
    }

    @Override
    public void registerIcons(IconRegister r) {
        this.iconArray = new Icon[]{r.registerIcon("fire/hellfire_0"), r.registerIcon("fire/hellfire_1")};
    }

    @Override
    public Icon getIcon(int side, int meta) {
        return this.iconArray[0];
    }
}
