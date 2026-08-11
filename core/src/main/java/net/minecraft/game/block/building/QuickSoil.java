package net.minecraft.game.block.building;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class QuickSoil extends Block {
    public QuickSoil(int blockid, Material material) {
        super(blockid, material);
    }

    @Override
    public int getRotation(int x, int y, int z, int side) {
        return this.rotationRand(x, y, z, side);
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("sands/" + this.name);
    }

    @Override
    public BoundingBox getCollisionBox(World world, int x, int y, int z) {
        return BoundingBox.getBoundingBox(x, y, z, x + 1, y + 1 - 0.125, z + 1);
    }

    @Override
    public void onEntityCollision(Entity entity, World world, int x, int y, int z) {
        entity.motionX *= 1.15;
        entity.motionZ *= 1.15;
    }
}
