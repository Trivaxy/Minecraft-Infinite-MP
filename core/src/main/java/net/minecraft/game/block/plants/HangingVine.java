package net.minecraft.game.block.plants;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class HangingVine extends Flower {
    private Icon base;

    public HangingVine(int blockID) {
        super(blockID);
        this.material = MaterialList.rope;
        this.setTickable(true);
        this.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
    }

    @Override
    public final boolean canGrowHere(int blockID) {
        return blockID == BlockList.wood.id
            || blockID == BlockList.dirt.id
            || blockID == BlockList.moss.id
            || blockID == this.id
            || blockID == BlockList.mushroomCap.id;
    }

    @Override
    public final void onNeighborChange(World world, int xCoord, int yCoord, int zCoord, int blockID) {
        super.onNeighborChange(world, xCoord, yCoord, zCoord, blockID);
        this.onBlockAdded(world, xCoord, yCoord, zCoord);
    }

    @Override
    public void onTick(World world, int xCoord, int yCoord, int zCoord, Random random) {
        if (world.getBlockId(xCoord, yCoord - 1, zCoord) == 0) {
            int max = 1;

            while (world.getBlockId(xCoord, yCoord + max, zCoord) == this.id) {
                max++;
            }

            if (max <= 8 && random.nextInt(25) == 0) {
                world.setBlockWithNotify(xCoord, yCoord - 1, zCoord, this.id);
            }
        }
    }

    @Override
    public final void onBlockAdded(World world, int xCoord, int yCoord, int zCoord) {
        if (world.getBlockId(xCoord, yCoord - 1, zCoord) == this.id) {
            world.setBlockMetadata(xCoord, yCoord, zCoord, 15);
        } else {
            world.setBlockMetadata(xCoord, yCoord, zCoord, 0);
        }
    }

    @Override
    public void registerIcons(IconRegister r) {
        super.registerIcons(r);
        this.base = r.registerIcon("plants/" + this.name + "End");
    }

    @Override
    public Icon getIcon(int side, int meta) {
        return meta == 0 ? this.base : this.blockIcon;
    }

    @Override
    public boolean canStay(World world, int x, int y, int z) {
        return this.canGrowHere(world.getBlockId(x, y + 1, z));
    }

    @Override
    public boolean placeableAt(World world, int x, int y, int z) {
        return this.canGrowHere(world.getBlockId(x, y + 1, z));
    }

    @Override
    public int dropID(int id, Random rand) {
        return 0;
    }

    @Override
    public int tickRate() {
        return 20;
    }
}
