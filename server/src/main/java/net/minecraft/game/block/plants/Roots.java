package net.minecraft.game.block.plants;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;

public class Roots extends Flower {
    public Roots(int blockid) {
        super(blockid);
        this.material = MaterialList.rope;
        float offset = 0.375F;
        this.setBounds(0.5F - offset, 0.0, 0.5F - offset, 0.5F + offset, 1.0, 0.5F + offset);
        this.setTickable(true);
    }

    @Override
    public void onTick(World world, int x, int y, int z, Random rand) {
        if (world.isAir(x, y + 1, z) && rand.nextInt(25) == 0) {
            int maxHeight = 1;

            while (world.getBlockId(x, y - maxHeight, z) == this.id) {
                maxHeight++;
            }

            if (maxHeight < 8) {
                world.setBlockAndMetadataWithNotify(x, y + 1, z, this.id, world.getBlockMetadata(x, y, z));
            }
        }
    }

    @Override
    public final boolean canGrowHere(int blockID) {
        return blockID == this.id || blockID == BlockList.ember.id || blockID == BlockList.grass.id || blockID == BlockList.dirt.id;
    }

    @Override
    public boolean canStay(World world, int x, int y, int z) {
        return this.canGrowHere(world.getBlockId(x, y - 1, z));
    }

    @Override
    public boolean placeableAt(World world, int x, int y, int z) {
        return this.canGrowHere(world.getBlockId(x, y - 1, z));
    }

    @Override
    public int dropID(int id, Random rand) {
        return this.id == BlockList.glowRoots.id ? ItemList.wax.id : 0;
    }

    @Override
    public int pickID(int id) {
        return this.id == BlockList.glowRoots.id ? ItemList.wax.id : this.id;
    }
}
