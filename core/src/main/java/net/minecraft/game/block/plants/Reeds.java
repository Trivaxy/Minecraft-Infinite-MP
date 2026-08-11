package net.minecraft.game.block.plants;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class Reeds extends Block {
    public Reeds(int blockid) {
        super(blockid, MaterialList.plants);
        float offset = 0.375F;
        this.setBounds(0.5F - offset, 0.0, 0.5F - offset, 0.5F + offset, 1.0, 0.5F + offset);
        this.setTickable(true);
        this.particleGravity = 0.6F;
        this.compostable = true;
    }

    @Override
    public void onTick(World world, int x, int y, int z, Random rand) {
        if (world.isAir(x, y + 1, z)) {
            int maxHeight = 1;

            while (world.getBlockId(x, y - maxHeight, z) == this.id) {
                maxHeight++;
            }

            if (maxHeight < 3) {
                int meta = world.getBlockMetadata(x, y, z);
                if (meta == 15) {
                    world.setBlockWithNotify(x, y + 1, z, this.id);
                    world.setBlockMetadataWithNotify(x, y, z, 0);
                } else {
                    world.setBlockMetadataWithNotify(x, y, z, meta + 1);
                }
            }
        }
    }

    @Override
    public boolean placeableAt(World world, int x, int y, int z) {
        int belowID = world.getBlockId(x, y - 1, z);
        return belowID == this.id
            ? true
            : (
                belowID != BlockList.grass.id && belowID != BlockList.dirt.id
                    ? false
                    : (
                        world.getBlockMaterial(x - 1, y - 1, z) == MaterialList.water
                            ? true
                            : (
                                world.getBlockMaterial(x + 1, y - 1, z) == MaterialList.water
                                    ? true
                                    : (
                                        world.getBlockMaterial(x, y - 1, z - 1) == MaterialList.water
                                            ? true
                                            : world.getBlockMaterial(x, y - 1, z + 1) == MaterialList.water
                                    )
                            )
                    )
            );
    }

    @Override
    public void onNeighborChange(World world, int x, int y, int z, int id) {
        if (!this.canStay(world, x, y, z)) {
            this.dropBlock(world, x, y, z, world.getBlockMetadata(x, y, z));
            world.setBlockWithNotify(x, y, z, 0);
            world.playEffect(2001, x, y, z, world.getBlockMetadata(x, y, z) << 12 | this.id);
        }
    }

    @Override
    public boolean canStay(World world, int x, int y, int z) {
        return this.placeableAt(world, x, y, z);
    }

    @Override
    public BoundingBox getCollisionBox(World world, int x, int y, int z) {
        return null;
    }

    @Override
    public int dropID(int id, Random rand) {
        return ItemList.reeds.id;
    }

    @Override
    public int pickID(int id) {
        return ItemList.reeds.id;
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.CROPS;
    }

    @Override
    public boolean particles3D() {
        return false;
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir.registerIcon("plants/" + this.name);
    }
}
