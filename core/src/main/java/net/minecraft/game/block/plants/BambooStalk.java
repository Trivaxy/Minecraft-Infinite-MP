package net.minecraft.game.block.plants;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class BambooStalk extends Flower {
    public BambooStalk(int blockID) {
        super(blockID);
        this.material = MaterialList.wood;
        this.setTickable(true);
        this.setBounds(0.375, 0.0, 0.375, 0.625, 1.0, 0.625);
    }

    @Override
    public final boolean canGrowHere(int blockID) {
        return blockID == BlockList.dirt.id || blockID == BlockList.grass.id || blockID == this.id;
    }

    @Override
    public void onTick(World world, int xCoord, int yCoord, int zCoord, Random random) {
        if (world.getBlockMaterial(xCoord, yCoord + 1, zCoord) == MaterialList.air) {
            int yOffset = 1;

            while (world.getBlockId(xCoord, yCoord - yOffset, zCoord) == this.id) {
                yOffset++;
            }

            if (yOffset < 16 && random.nextInt(10) == 0) {
                world.setBlockWithNotify(xCoord, yCoord + 1, zCoord, this.id);
            }
        }
    }

    @Override
    public int dropID(int blockid, Random random2) {
        return ItemList.bamboo.id;
    }

    @Override
    public int pickID(int i1) {
        return ItemList.bamboo.id;
    }

    @Override
    public boolean canStay(World world, int x, int y, int z) {
        return this.canGrowHere(world.getBlockId(x, y - 1, z));
    }

    @Override
    public boolean placeableAt(World world, int xCoord, int yCoord, int zCoord) {
        return this.canStay(world, xCoord, yCoord, zCoord) && world.getBlockMaterial(xCoord, yCoord, zCoord) == MaterialList.air;
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.NORMAL;
    }

    @Override
    public BoundingBox getCollisionBox(World world, int xCoord, int yCoord, int zCoord) {
        return BoundingBox.getBoundingBox(
            xCoord + this.minX, yCoord + this.minY, zCoord + this.minZ, xCoord + this.maxX, yCoord + this.maxY, zCoord + this.maxZ
        );
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("plants/bambooStalk");
    }
}
