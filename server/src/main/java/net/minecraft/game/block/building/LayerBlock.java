package net.minecraft.game.block.building;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.tools.Shears;
import net.minecraft.game.item.tools.Shovel;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;
import net.minecraft.game.world.util.LightLayer;

public class LayerBlock extends Block {
    public LayerBlock(int id, Material mat) {
        super(id, mat);
        this.setBounds(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);
        this.setTickable(true);
    }

    @Override
    public boolean placeableAt(World world, int x, int y, int z) {
        Block below = world.getBlock(x, y - 1, z);
        if (this.id == BlockList.snowLayer.id && below == BlockList.powderSnow) {
            return false;
        }

        if (this.id == BlockList.sandLayer.id && below == BlockList.quickSand) {
            return false;
        }

        int meta = world.getBlockMetadata(x, y - 1, z);
        return below == BlockList.glass || below == BlockList.leaves || below == BlockList.table || below == BlockList.detector
            ? true
            : (
                below instanceof Stairs && (meta & 4) != 0
                    ? true
                    : (
                        below instanceof Slab && meta >= 128
                            ? true
                            : (
                                world.getBlockMaterial(x, y, z) == MaterialList.water
                                    ? false
                                    : (below == this && meta < 7 ? true : (!world.isBlockFull(x, y - 1, z) ? false : super.placeableAt(world, x, y, z)))
                            )
                    )
            );
    }

    @Override
    public boolean renderFace(WorldSource world, int xCoord, int yCoord, int zCoord, int side) {
        return this.maxY <= 0.125 && world.getBlock(xCoord, yCoord, zCoord) == this ? false : super.renderFace(world, xCoord, yCoord, zCoord, side);
    }

    @Override
    public boolean blocksPathfinder(WorldSource world, int x, int y, int z) {
        return false;
    }

    @Override
    public void forceBounds(int meta) {
        this.setBounds(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);
    }

    @Override
    public int dropMeta(int id) {
        return 0;
    }

    @Override
    protected int guaranteedMeta(int meta) {
        return this == BlockList.leafPile ? (meta >> 3) * 8 : 0;
    }

    @Override
    public void onNeighborChange(World world, int x, int y, int z, int id) {
        if (!world.getBlockMaterial(x, y - 1, z).isSolid()) {
            world.playEffect(2001, x, y, z, world.getBlockMetadata(x, y, z) << 12 | this.id);
            world.setBlockWithNotify(x, y, z, 0);
        }
    }

    @Override
    public boolean canStay(World world, int xCoord, int yCoord, int zCoord) {
        Block below = world.getBlock(xCoord, yCoord - 1, zCoord);
        return below != this
            && (
                world.isBlockFull(xCoord, yCoord - 1, zCoord)
                    || below == BlockList.leaves
                    || below == BlockList.glass
                    || below == BlockList.slabCobble
                    || below == BlockList.doubleSlabCobble
                    || below == BlockList.detector
            );
    }

    @Override
    public void onTick(World world, int x, int y, int z, Random random) {
        if (world.getBlock(x, y, z) == BlockList.snowLayer && world.getBrightness(LightLayer.Block, x, y, z) > 11) {
            if (world.getBlockMetadata(x, y, z) > 0) {
                world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) - 1);
            } else {
                world.setBlockWithNotify(x, y, z, 0);
            }
        }
    }

    @Override
    public void onBlockAdded(World world, int xCoord, int yCoord, int zCoord) {
        int meta = world.getBlockMetadata(xCoord, yCoord - 1, zCoord);
        if (this.id == world.getBlockId(xCoord, yCoord - 1, zCoord) && meta < 7) {
            world.setBlockMetadata(xCoord, yCoord - 1, zCoord, meta + 1);
            world.setBlock(xCoord, yCoord, zCoord, 0);
        }
    }

    @Override
    public BoundingBox getCollisionBox(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z) & 7;
        return meta >= 3 && meta < 7
            ? new BoundingBox(x + this.minX, y + this.minY, z + this.minZ, x + this.maxX, y + 0.5, z + this.maxZ)
            : (meta >= 7 ? new BoundingBox(x + this.minX, y + this.minY, z + this.minZ, x + this.maxX, y + 1.0, z + this.maxZ) : null);
    }

    @Override
    public void modifyOutline(WorldSource world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z) & 7;
        float height = 2 * (1 + meta) / 16.0F;
        this.setBounds(0.0, 0.0, 0.0, 1.0, height, 1.0);
    }

    @Override
    public void setStateBounds(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z) & 7;
        float height = 2 * (1 + meta) / 16.0F;
        this.setBounds(0.0, 0.0, 0.0, 1.0, height, 1.0);
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public int dropID(int i, Random random) {
        if (this.id == BlockList.snowLayer.id) {
            return ItemList.snowball.id;
        } else if (this.id == BlockList.ash.id) {
            return ItemList.ash.id;
        } else {
            return this.id == BlockList.sandLayer.id ? ItemList.sandball.id : 0;
        }
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    public void dropBlockItem(World world, int x, int y, int z, int id, float f, boolean fortune) {
        if (!world.isOnline) {
            int quantity = this.quantityDropped(world.rand) + world.getBlockMetadata(x, y, z) / 2;

            for (int i = 0; i < quantity; i++) {
                int drop = this.dropID(id, world.rand);
                if (drop > 0) {
                    float xOffset = world.rand.nextFloat() * 0.7F + 0.15F;
                    float yOffset = world.rand.nextFloat() * 0.7F + 0.15F;
                    float zOffset = world.rand.nextFloat() * 0.7F + 0.15F;
                    DroppedItem item = new DroppedItem(world, x + xOffset, y + yOffset, z + zOffset, new ItemStack(drop, 1, this.dropMeta(id)));
                    world.spawnEntity(item);
                }
            }
        }
    }

    @Override
    public void dropItem(World world, Player player, int x, int y, int z, int meta) {
        if (this == BlockList.leafPile) {
            if (!world.isOnline && player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof Shears) {
                this.dropExact(world, x, y, z, meta, 1.0F, this.id, true);
            }
        } else if (player.inventory.getCurrentItem() != null
                && (player.inventory.getCurrentItem().getItem() instanceof Shovel || player.inventory.getCurrentItem().getItem() == ItemList.paxel)
            || player.inventory.canUseItem()
                && (player.inventory.getOffHandItem().getItem() instanceof Shovel || player.inventory.getOffHandItem().getItem() == ItemList.paxel)) {
            this.dropBlockItem(world, x, y, z, meta, 1.0F, false);
        }
    }
}
