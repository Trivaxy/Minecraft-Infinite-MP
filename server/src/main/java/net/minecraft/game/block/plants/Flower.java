package net.minecraft.game.block.plants;

import java.util.Random;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.trees.bigflower.BigFlower;
import net.minecraft.statistics.StatList;

public class Flower extends Block {
    public Flower(int blockid) {
        super(blockid, MaterialList.plants);
        float offset = 0.2F;
        this.setBounds(0.5F - offset, 0.0, 0.5F - offset, 0.5F + offset, offset * 3.0F, 0.5F + offset);
        this.particleGravity = 0.6F;
        this.compostable = true;
    }

    @Override
    public boolean placeableAt(World world, int xCoord, int yCoord, int zCoord) {
        return this.canGrowHere(world.getBlockId(xCoord, yCoord - 1, zCoord));
    }

    public boolean canGrowHere(int blockid) {
        return blockid == BlockList.moss.id || blockid == BlockList.grass.id || blockid == BlockList.dirt.id || blockid == BlockList.tilledField.id;
    }

    @Override
    public void onNeighborChange(World world, int xCoord, int yCoord, int zCoord, int id) {
        if (!this.canStay(world, xCoord, yCoord, zCoord)) {
            this.dropBlock(world, xCoord, yCoord, zCoord, world.getBlockMetadata(xCoord, yCoord, zCoord));
            world.playEffect(2001, xCoord, yCoord, zCoord, world.getBlockMetadata(xCoord, yCoord, zCoord) << 12 | this.id);
            world.setBlockWithNotify(xCoord, yCoord, zCoord, 0);
        }
    }

    @Override
    public boolean canStay(World world, int xCoord, int yCoord, int zCoord) {
        if (world.getBlockId(xCoord, yCoord - 1, zCoord) == BlockList.grass.id && world.getBlockMetadata(xCoord, yCoord - 1, zCoord) == 4) {
            return false;
        } else {
            return world.getBlockId(xCoord, yCoord - 1, zCoord) == BlockList.dirt.id && world.getBlockMetadata(xCoord, yCoord - 1, zCoord) == 6
                ? false
                : (world.getBlockBrightness(xCoord, yCoord, zCoord) >= 8 || world.isBlockExposed(xCoord, yCoord, zCoord))
                    && this.canGrowHere(world.getBlockId(xCoord, yCoord - 1, zCoord));
        }
    }

    @Override
    public void onEntityCollision(Entity entity, World world, int x, int y, int z) {
        if (this.id == BlockList.witherRose.id && entity instanceof Mob) {
            entity.damageEntity(DamageSource.generic, 1);
        }
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
        return Block.RenderType.CROSS;
    }

    @Override
    public boolean particles3D() {
        return false;
    }

    @Override
    public void spawnParticles(World world, int xCoord, int yCoord, int zCoord, Random rand) {
        if (this.id == BlockList.fireFlower.id) {
            world.spawnParticle(ParticleType.SMOKE, xCoord + 0.52, yCoord + 0.7, zCoord + 0.52, 0.0, 0.0, 0.0);
            world.spawnParticle(ParticleType.FLAME, xCoord + 0.52, yCoord + 0.7, zCoord + 0.52, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("plants/" + this.name);
    }

    public void growTree(World w, Player player, int x, int y, int z, Random r) {
        BigFlower grower = BigFlower.FLOWERS.get(w.getBlockId(x, y, z));
        if (grower != null && grower.place(w, r, x, y, z)) {
            player.addStat(StatList.flowerGrowth, 1);
        }
    }
}
