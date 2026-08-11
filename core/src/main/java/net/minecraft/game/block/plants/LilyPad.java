package net.minecraft.game.block.plants;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.world.World;

public class LilyPad extends Flower {
    public LilyPad(int id) {
        super(id);
        float offset = 0.5F;
        this.setBounds(0.5F - offset, 0.0, 0.5F - offset, 0.5F + offset, 0.015625, 0.5F + offset);
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.LILYPAD;
    }

    @Override
    public boolean canGrowHere(int id) {
        return id == BlockList.waterStill.id;
    }

    @Override
    public boolean canStay(World world, int x, int y, int z) {
        return y >= 0 && y < world.getWorldHeight()
            ? world.getBlockMaterial(x, y - 1, z) == MaterialList.water && world.getBlockMetadata(x, y - 1, z) == 0
            : false;
    }

    @Override
    public BoundingBox getCollisionBox(World world, int xCoord, int yCoord, int zCoord) {
        return new BoundingBox(xCoord + this.minX, yCoord + this.minY, zCoord + this.minZ, xCoord + this.maxX, yCoord + this.maxY, zCoord + this.maxZ);
    }

    @Override
    public boolean placeableInWater() {
        return true;
    }
}
