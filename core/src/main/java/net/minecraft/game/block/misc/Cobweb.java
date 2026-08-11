package net.minecraft.game.block.misc;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.world.World;

public class Cobweb extends Block {
    public Cobweb(int blockID, Material material) {
        super(blockID, material);
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
    public final int dropID(int id, Random random) {
        return ItemList.silk.id;
    }

    @Override
    public boolean particles3D() {
        return false;
    }

    @Override
    public void onEntityCollision(Entity entity, World world, int x, int y, int z) {
        if (entity.canBeSlowed()) {
            entity.motionX *= 0.2;
            entity.motionY *= 0.2;
            entity.motionZ *= 0.2;
        }

        entity.fallDistance = 0.0F;
    }
}
