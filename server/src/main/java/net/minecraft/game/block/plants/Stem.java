package net.minecraft.game.block.plants;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;

public class Stem extends Flower {
    private Block fruitType;
    public Icon bentStem;

    public Stem(int id, Block block) {
        super(id);
        this.fruitType = block;
        this.setTickable(true);
        float offset = 0.125F;
        this.setBounds(0.5F - offset, 0.0, 0.5F - offset, 0.5F + offset, 0.25, 0.5F + offset);
    }

    @Override
    public void registerIcons(IconRegister icon) {
        super.registerIcons(icon);
        this.bentStem = icon.registerIcon("plants/" + this.name + "Active");
    }

    @Override
    public final boolean canGrowHere(int blockID) {
        return blockID == BlockList.tilledField.id;
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.STEM;
    }

    @Override
    public void onTick(World world, int x, int y, int z, Random rand) {
        if (world.getBlockBrightness(x, y + 1, z) >= 9) {
            float growthRate = ((Crops)BlockList.crops).growthRate(world, x, y, z);
            if (rand.nextInt((int)(100.0F / growthRate)) == 0) {
                int meta = world.getBlockMetadata(x, y, z);
                if (meta < 7) {
                    world.setBlockMetadataWithNotify(x, y, z, ++meta);
                } else {
                    if (world.getBlockId(x - 1, y, z) == this.fruitType.id) {
                        return;
                    }

                    if (world.getBlockId(x + 1, y, z) == this.fruitType.id) {
                        return;
                    }

                    if (world.getBlockId(x, y, z - 1) == this.fruitType.id) {
                        return;
                    }

                    if (world.getBlockId(x, y, z + 1) == this.fruitType.id) {
                        return;
                    }

                    int side = rand.nextInt(4);
                    int newX = x;
                    int newZ = z;
                    if (side == 0) {
                        newX = x - 1;
                    }

                    if (side == 1) {
                        newX++;
                    }

                    if (side == 2) {
                        newZ = z - 1;
                    }

                    if (side == 3) {
                        newZ++;
                    }

                    if (world.getBlockId(newX, y, newZ) == 0
                        && (world.getBlockMaterial(newX, y - 1, newZ) == MaterialList.ground || world.getBlockMaterial(newX, y - 1, newZ) == MaterialList.grass)
                        )
                     {
                        world.setBlockWithNotify(newX, y, newZ, this.fruitType.id);
                    }
                }
            }
        }
    }

    @Override
    public boolean fertilize(World world, int x, int y, int z, int meta) {
        if (meta >= 0 && meta <= 7) {
            world.setBlockMetadataWithNotify(x, y, z, meta);
            return true;
        } else {
            world.setBlockMetadataWithNotify(x, y, z, 7);
            return false;
        }
    }

    @Override
    public int getMaxMeta() {
        return 7;
    }

    @Override
    public void setStateBounds(World world, int x, int y, int z) {
        this.maxY = (world.getBlockMetadata(x, y, z) * 2 + 2) / 16.0F;
        float offset = 0.125F;
        this.setBounds(0.5F - offset, 0.0, 0.5F - offset, 0.5F + offset, (float)this.maxY, 0.5F + offset);
    }

    public int getState(WorldSource world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        return meta < 7
            ? -1
            : (
                world.getBlockId(x - 1, y, z) == this.fruitType.id
                    ? 0
                    : (
                        world.getBlockId(x + 1, y, z) == this.fruitType.id
                            ? 1
                            : (world.getBlockId(x, y, z - 1) == this.fruitType.id ? 2 : (world.getBlockId(x, y, z + 1) == this.fruitType.id ? 3 : -1))
                    )
            );
    }

    @Override
    public void dropBlockItem(World world, int x, int y, int z, int meta, float force, boolean fortune) {
        if (!world.isOnline) {
            int drop = this.dropID(meta, world.rand);
            if (world.rand.nextInt(15) <= meta && drop > 0) {
                float offset = 0.7F;
                float xOffset = world.rand.nextFloat() * offset + (1.0F - offset) * 0.5F;
                float yOffset = world.rand.nextFloat() * offset + (1.0F - offset) * 0.5F;
                float zOffset = world.rand.nextFloat() * offset + (1.0F - offset) * 0.5F;
                DroppedItem item = new DroppedItem(world, x + xOffset, y + yOffset, z + zOffset, new ItemStack(drop));
                world.spawnEntity(item);
            }
        }
    }

    @Override
    public int dropID(int meta, Random rand) {
        Item item = ItemList.seeds;
        if (this.fruitType == BlockList.pumpkin) {
            item = ItemList.seedsPumpkin;
        } else if (this.fruitType == BlockList.melon) {
            item = ItemList.seedsMelon;
        }

        return item.id;
    }
}
