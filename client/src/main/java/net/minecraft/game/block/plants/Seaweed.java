package net.minecraft.game.block.plants;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class Seaweed extends Flower {
    private Icon base;

    public Seaweed(int blockID) {
        super(blockID);
        this.material = MaterialList.water;
        this.setTickable(true);
        this.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
    }

    @Override
    public final boolean canGrowHere(int blockID) {
        return blockID == BlockList.dirt.id || blockID == BlockList.sand.id || blockID == BlockList.gravel.id || blockID == this.id;
    }

    @Override
    public final void onNeighborChange(World world, int xCoord, int yCoord, int zCoord, int blockID) {
        if (!this.canStay(world, xCoord, yCoord, zCoord)) {
            this.dropBlock(world, xCoord, yCoord, zCoord, world.getBlockMetadata(xCoord, yCoord, zCoord));
            world.setBlockWithNotify(xCoord, yCoord, zCoord, BlockList.waterStill.id);
            world.playEffect(2001, xCoord, yCoord, zCoord, world.getBlockMetadata(xCoord, yCoord, zCoord) << 12 | this.id);
        }

        if (world.getBlockId(xCoord, yCoord + 1, zCoord) == this.id) {
            world.setBlockMetadata(xCoord, yCoord, zCoord, 15);
        } else {
            world.setBlockMetadata(xCoord, yCoord, zCoord, 0);
        }
    }

    @Override
    public void onTick(World world, int xCoord, int yCoord, int zCoord, Random random) {
        if (world.getBlockMaterial(xCoord, yCoord + 1, zCoord) == MaterialList.water
            && world.getBlockMaterial(xCoord, yCoord + 2, zCoord) == MaterialList.water) {
            int maxHeight = 1;

            while (world.getBlockId(xCoord, yCoord - maxHeight, zCoord) == this.id) {
                maxHeight++;
            }

            if (maxHeight < 6 && random.nextInt(25) == 0) {
                world.setBlockWithNotify(xCoord, yCoord + 1, zCoord, this.id);
                world.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 15);
            }
        }
    }

    @Override
    public final void onBlockAdded(World world, int xCoord, int yCoord, int zCoord) {
        if (world.getBlockId(xCoord, yCoord - 1, zCoord) == this.id) {
            world.setBlockMetadataWithNotify(xCoord, yCoord - 1, zCoord, 15);
        }
    }

    @Override
    public void registerIcons(IconRegister r) {
        super.registerIcons(r);
        this.base = r.registerIcon("plants/" + this.name + "Base");
    }

    @Override
    public Icon getIcon(int side, int meta) {
        return meta == 15 ? this.base : this.blockIcon;
    }

    @Override
    public boolean canStay(World world, int x, int y, int z) {
        int blockOnTop = world.getBlockId(x, y + 1, z);
        return blockOnTop != this.id && blockOnTop != BlockList.waterStill.id && blockOnTop != BlockList.waterMoving.id
            ? false
            : this.canGrowHere(world.getBlockId(x, y - 1, z));
    }

    @Override
    public boolean placeableAt(World world, int xCoord, int yCoord, int zCoord) {
        return this.canStay(world, xCoord, yCoord, zCoord) && !world.isSolid(xCoord, yCoord, zCoord);
    }

    @Override
    public BoundingBox getCollisionBox(World world, int xCoord, int yCoord, int zCoord) {
        return null;
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.CROPS;
    }
}
