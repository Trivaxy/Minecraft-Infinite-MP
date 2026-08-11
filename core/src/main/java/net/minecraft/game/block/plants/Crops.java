package net.minecraft.game.block.plants;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class Crops extends Flower {
    private Icon[] icons;
    int maxMeta;

    public Crops(int id, int meta) {
        super(id);
        this.setTickable(true);
        float offset = 0.5F;
        this.setBounds(0.5F - offset, 0.0, 0.5F - offset, 0.5F + offset, 0.25, 0.5F + offset);
        this.maxMeta = meta;
    }

    @Override
    public boolean canGrowHere(int blockid) {
        return blockid == BlockList.tilledField.id;
    }

    @Override
    public void onTick(World world, int xCoord, int yCoord, int zCoord, Random rand) {
        if (!world.isOnline && world.getBlockBrightness(xCoord, yCoord + 1, zCoord) >= 9) {
            int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
            if (meta < this.maxMeta) {
                float tick = this.growthRate(world, xCoord, yCoord, zCoord);
                if (rand.nextInt((int)(100.0F / tick)) == 0) {
                    world.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, ++meta);
                }
            }
        }
    }

    public float growthRate(World world, int xCoord, int yCoord, int zCoord) {
        float returnClause = 1.0F;
        int left = world.getBlockId(xCoord, yCoord, zCoord - 1);
        int right = world.getBlockId(xCoord, yCoord, zCoord + 1);
        int front = world.getBlockId(xCoord - 1, yCoord, zCoord);
        int back = world.getBlockId(xCoord + 1, yCoord, zCoord);
        int diagUp = world.getBlockId(xCoord - 1, yCoord, zCoord - 1);
        int diagDown = world.getBlockId(xCoord + 1, yCoord, zCoord - 1);
        int diagLeft = world.getBlockId(xCoord + 1, yCoord, zCoord + 1);
        int diagRight = world.getBlockId(xCoord - 1, yCoord, zCoord + 1);
        boolean adjacentX = front == this.id || back == this.id;
        boolean adjacentZ = left == this.id || right == this.id;
        boolean diagonal = diagUp == this.id || diagDown == this.id || diagLeft == this.id || diagRight == this.id;

        for (int x = xCoord - 1; x <= xCoord + 1; x++) {
            for (int z = zCoord - 1; z <= zCoord + 1; z++) {
                int block = world.getBlockId(x, yCoord - 1, z);
                float offset = 0.0F;
                if (block == BlockList.tilledField.id) {
                    offset = 1.0F;
                    if (world.getBlockMetadata(x, yCoord - 1, z) > 0) {
                        offset = 3.0F;
                    }
                }

                if (x != xCoord || z != zCoord) {
                    offset /= 4.0F;
                }

                returnClause += offset;
            }
        }

        if (diagonal || adjacentX && adjacentZ) {
            returnClause /= 2.0F;
        }

        return returnClause;
    }

    @Override
    public Icon getIcon(int side, int meta) {
        if (meta < 0 || meta > this.maxMeta) {
            meta = this.maxMeta;
        }

        return this.icons[meta];
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.icons = new Icon[8];

        for (int i = 0; i <= this.maxMeta; i++) {
            this.icons[i] = ir.registerIcon("crops/" + this.name + "_" + i);
        }
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.CROPS;
    }

    @Override
    public void onBroken(World world, int xCoord, int yCoord, int zCoord, int meta) {
        Item drop = ItemList.seeds;
        int chance = 15;
        int max = 4;
        if (meta >= 3) {
            chance = 7;
        } else {
            max = 1;
        }

        if (this.id == BlockList.potatoes.id) {
            drop = ItemList.potato;
        } else if (this.id == BlockList.carrots.id) {
            drop = ItemList.carrot;
        } else if (this.id == BlockList.beetroots.id) {
            drop = ItemList.seedsBeetroot;
        } else if (this.id == BlockList.cotton.id) {
            drop = ItemList.seedsCotton;
        }

        if (!world.isOnline) {
            for (int i = 1; i < max; i++) {
                if (world.rand.nextInt(chance) <= meta) {
                    float offset = 0.7F;
                    float offsetX = world.rand.nextFloat() * offset + (1.0F - offset) * 0.5F;
                    float offsetY = world.rand.nextFloat() * offset + (1.0F - offset) * 0.5F;
                    float offsetZ = world.rand.nextFloat() * offset + (1.0F - offset) * 0.5F;
                    DroppedItem item = new DroppedItem(world, xCoord + offsetX, yCoord + offsetY, zCoord + offsetZ, new ItemStack(drop));
                    world.spawnEntity(item);
                }
            }
        }
    }

    @Override
    public int dropID(int meta, Random random) {
        if (this.id == BlockList.carrots.id) {
            return ItemList.carrot.id;
        } else if (this.id == BlockList.potatoes.id) {
            return ItemList.potato.id;
        } else if (this.id == BlockList.cotton.id) {
            return meta >= this.maxMeta ? ItemList.cotton.id : -1;
        } else if (this.id == BlockList.beetroots.id) {
            return meta >= this.maxMeta ? ItemList.beetroot.id : -1;
        } else {
            return meta >= this.maxMeta ? ItemList.wheat.id : -1;
        }
    }

    @Override
    public int pickID(int meta) {
        if (this.id == BlockList.carrots.id) {
            return ItemList.carrot.id;
        } else if (this.id == BlockList.potatoes.id) {
            return ItemList.potato.id;
        } else if (this.id == BlockList.cotton.id) {
            return ItemList.seedsCotton.id;
        } else {
            return this.id == BlockList.beetroots.id ? ItemList.seedsBeetroot.id : ItemList.seeds.id;
        }
    }

    @Override
    public int quantityDropped(Random random1) {
        return 1;
    }

    @Override
    public boolean fertilize(World world, int x, int y, int z, int meta) {
        if (meta >= 0 && meta <= this.maxMeta) {
            world.setBlockMetadataWithNotify(x, y, z, meta);
            return true;
        } else {
            world.setBlockMetadataWithNotify(x, y, z, this.maxMeta);
            return false;
        }
    }

    @Override
    public void setStateBounds(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z) & this.maxMeta;
        this.setBounds(0.0, 0.0, 0.0, 1.0, 2 * (1 + meta) / 16.0F, 1.0);
    }
}
