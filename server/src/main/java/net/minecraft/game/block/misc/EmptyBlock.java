package net.minecraft.game.block.misc;

import java.util.Random;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;

public class EmptyBlock extends Block {
    public EmptyBlock(int blockid, Material material) {
        super(blockid, material);
    }

    @Override
    public boolean particles3D() {
        return false;
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.NONE;
    }

    @Override
    public boolean renderFace(WorldSource blockAccess, int xCoord, int yCoord, int zCoord, int id) {
        return false;
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public BoundingBox getCollisionBox(World world, int xCoord, int yCoord, int zCoord) {
        return this.id == BlockList.light.id
            ? null
            : BoundingBox.getBoundingBox(xCoord + this.minX, yCoord + this.minY, zCoord + this.minZ, xCoord + this.maxX, yCoord + this.maxY, zCoord + this.maxZ);
    }

    @Override
    public int dropID(int blockid, Random random) {
        return 0;
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public final void spawnParticles(World world, int xCoord, int yCoord, int zCoord, Random random) {
        if (world.getClosestPlayer(xCoord, yCoord, zCoord, 16.0) != null) {
            Player activePlayer = world.getClosestPlayer(xCoord, yCoord, zCoord, 16.0);
            ItemStack mainHand = activePlayer.getCurrentEquippedItem();
            if (this.id == BlockList.barrier.id && mainHand != null && mainHand.itemID == BlockList.barrier.id) {
                world.spawnParticle(ParticleType.BARRIER, xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F, 0.0, 0.0, 0.0);
            } else if (this.id == BlockList.light.id && mainHand != null && mainHand.itemID == BlockList.light.id) {
                world.spawnParticle(ParticleType.LIGHT, xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F, 0.0, 0.0, 0.0);
            }
        }
    }
}
