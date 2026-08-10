package net.minecraft.game.block.plants;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;

public class Shrub extends Flower {
    public Shrub(int blockID) {
        super(blockID);
        float offset = 0.4F;
        this.setBounds(0.5F - offset, 0.0, 0.5F - offset, 0.5F + offset, 0.8F, 0.5F + offset);
    }

    @Override
    public boolean canGrowHere(int id) {
        return id == BlockList.hardenedClay.id
            || id == BlockList.sand.id
            || id == BlockList.dirt.id
            || id == BlockList.redSand.id
            || id == BlockList.silverSand.id
            || id == BlockList.susSand.id;
    }

    @Override
    public boolean canStay(World world, int xCoord, int yCoord, int zCoord) {
        return this.canGrowHere(world.getBlockId(xCoord, yCoord - 1, zCoord));
    }

    @Override
    public int quantityDropped(Random random) {
        return random.nextInt(3);
    }

    @Override
    public int dropID(int id, Random random) {
        return ItemList.stick.id;
    }
}
