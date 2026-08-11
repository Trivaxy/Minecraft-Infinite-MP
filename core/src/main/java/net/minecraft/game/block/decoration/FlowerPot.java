package net.minecraft.game.block.decoration;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.building.Slab;
import net.minecraft.game.block.building.Stairs;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.plants.Flower;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.block.BlockItem;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class FlowerPot extends Block {
    public FlowerPot(int id) {
        super(id, MaterialList.circuits);
        float height = 0.375F;
        float offset = height / 2.0F;
        this.setBounds(0.5F - offset, 0.0, 0.5F - offset, 0.5F + offset, height, 0.5F + offset);
        this.enableStats = false;
        this.silkTouch = false;
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.FLOWER_POT;
    }

    @Override
    public boolean blockActivated(World world, int x, int y, int z, Player player) {
        ItemStack heldItem = player.inventory.getCurrentItem();
        ItemStack offhand = player.inventory.getOffHandItem();
        ItemStack stack = getPlantFromPot(world.getBlockMetadata(x, y, z));
        if (offhand != null && offhand.getItem() instanceof BlockItem && ((BlockItem)offhand.getItem()).block instanceof Flower) {
            heldItem = offhand;
        }

        if (heldItem != null && world.getBlockMetadata(x, y, z) == 0) {
            int meta = getMetaFromPlant(heldItem);
            if (meta > 0) {
                world.setBlockMetadataWithNotify(x, y, z, meta);
                if (player.properties.gamemode != 1) {
                    heldItem.stackSize--;
                }

                player.addStat(StatList.potUse, 1);
                return true;
            } else {
                return false;
            }
        } else {
            if (stack != null && world.getBlockMetadata(x, y, z) != 0) {
                this.dropItem(world, x, y, z, stack);
            }

            world.setBlockMetadataWithNotify(x, y, z, 0);
            return true;
        }
    }

    @Override
    public int dropID(int id, Random rand) {
        return ItemList.flowerPot.id;
    }

    @Override
    public int pickID(int id) {
        return ItemList.flowerPot.id;
    }

    @Override
    public boolean particles3D() {
        return false;
    }

    @Override
    public boolean placeableAt(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y - 1, z);
        int meta = world.getBlockMetadata(x, y - 1, z);
        return block == BlockList.glass
            ? true
            : (
                block == BlockList.table
                    ? true
                    : (
                        block == BlockList.detector
                            ? true
                            : (
                                block instanceof Stairs && (meta & 4) != 0
                                    ? true
                                    : (
                                        block instanceof Slab && meta >= 128
                                            ? true
                                            : (
                                                world.getBlockMaterial(x, y, z) == MaterialList.water
                                                    ? false
                                                    : (!world.isBlockFull(x, y - 1, z) ? false : super.placeableAt(world, x, y, z))
                                            )
                                    )
                            )
                    )
            );
    }

    @Override
    public void onNeighborChange(World world, int x, int y, int z, int id) {
        if (!this.placeableAt(world, x, y, z)) {
            this.dropBlock(world, x, y, z, world.getBlockMetadata(x, y, z));
            world.playEffect(2001, x, y, z, world.getBlockMetadata(x, y, z) << 12 | this.id);
            world.setBlockWithNotify(x, y, z, 0);
        }
    }

    @Override
    public boolean canStay(World world, int xCoord, int yCoord, int zCoord) {
        return world.getBlockMaterial(xCoord, yCoord - 1, zCoord).isSolid();
    }

    @Override
    public void dropBlockItem(World world, int x, int y, int z, int meta, float force, boolean fortune) {
        super.dropBlockItem(world, x, y, z, meta, force, fortune);
        if (!world.isOnline && meta > 0) {
            ItemStack flower = getPlantFromPot(meta);
            if (flower != null) {
                this.dropItem(world, x, y, z, flower);
            }
        }
    }

    protected void dropItem(World world, int x, int y, int z, ItemStack stack) {
        if (!world.isOnline) {
            float offset = 0.7F;
            float offsetX = world.rand.nextFloat() * offset + (1.0F - offset) * 0.5F;
            float offsetY = world.rand.nextFloat() * offset + (1.0F - offset) * 0.5F;
            float offsetZ = world.rand.nextFloat() * offset + (1.0F - offset) * 0.5F;
            DroppedItem drop = new DroppedItem(world, x + offsetX, y + offsetY, z + offsetZ, stack);
            world.spawnEntity(drop);
        }
    }

    public static ItemStack getPlantFromPot(int meta) {
        switch (meta) {
            case 1:
                return new ItemStack(BlockList.rose);
            case 2:
                return new ItemStack(BlockList.dandelion);
            case 3:
                return new ItemStack(BlockList.lilac);
            case 4:
                return new ItemStack(BlockList.petunia);
            case 5:
                return new ItemStack(BlockList.mushroomRed);
            case 6:
                return new ItemStack(BlockList.mushroomBrown);
            case 7:
                return new ItemStack(BlockList.mushroomGlowing);
            case 8:
                return new ItemStack(BlockList.berryBush, 1, 0);
            case 9:
                return new ItemStack(BlockList.berryBush, 1, 1);
            case 10:
                return new ItemStack(BlockList.witherRose);
            case 11:
                return new ItemStack(BlockList.fireFlower);
            case 12:
                return new ItemStack(BlockList.mushroomCrimson);
            case 13:
                return new ItemStack(BlockList.mushroomNylium);
            case 14:
                return new ItemStack(BlockList.tulip);
            case 15:
                return new ItemStack(BlockList.daisy);
            case 16:
                return new ItemStack(BlockList.pinkTulip);
            case 17:
                return new ItemStack(BlockList.whiteTulip);
            case 18:
                return new ItemStack(BlockList.paeonia);
            case 19:
                return new ItemStack(BlockList.cactus);
            case 20:
                return new ItemStack(ItemList.bamboo);
            case 21:
                return new ItemStack(BlockList.deadBush);
            case 22:
                return new ItemStack(BlockList.tallGrass, 1, 7);
            case 23:
                return new ItemStack(BlockList.tallGrass, 1, 9);
            case 24:
                return new ItemStack(BlockList.tallGrass, 1, 11);
            case 25:
                return new ItemStack(BlockList.glowGrass, 1, 1);
            default:
                return new ItemStack(BlockList.sapling, 1, meta - 128);
        }
    }

    public static int getMetaFromPlant(ItemStack stack) {
        int block = stack.getItem().id;
        if (block == BlockList.rose.id) {
            return 1;
        }

        if (block == BlockList.dandelion.id) {
            return 2;
        }

        if (block == BlockList.lilac.id) {
            return 3;
        }

        if (block == BlockList.petunia.id) {
            return 4;
        }

        if (block == BlockList.mushroomRed.id) {
            return 5;
        }

        if (block == BlockList.mushroomBrown.id) {
            return 6;
        }

        if (block == BlockList.mushroomGlowing.id) {
            return 7;
        }

        if (block == BlockList.berryBush.id) {
            switch (stack.getItemDamage()) {
                case 0:
                    return 8;
                case 1:
                    return 9;
            }
        } else {
            if (block == BlockList.witherRose.id) {
                return 10;
            }

            if (block == BlockList.fireFlower.id) {
                return 11;
            }

            if (block == BlockList.mushroomCrimson.id) {
                return 12;
            }

            if (block == BlockList.mushroomNylium.id) {
                return 13;
            }

            if (block == BlockList.tulip.id) {
                return 14;
            }

            if (block == BlockList.daisy.id) {
                return 15;
            }

            if (block == BlockList.pinkTulip.id) {
                return 16;
            }

            if (block == BlockList.whiteTulip.id) {
                return 17;
            }

            if (block == BlockList.paeonia.id) {
                return 18;
            }

            if (block == BlockList.cactus.id) {
                return 19;
            }

            if (block == ItemList.bamboo.id) {
                return 20;
            }

            if (block == BlockList.deadBush.id) {
                return 21;
            }

            if (block == BlockList.tallGrass.id) {
                switch (stack.getItemDamage()) {
                    case 7:
                        return 22;
                    case 8:
                    case 10:
                    default:
                        break;
                    case 9:
                        return 23;
                    case 11:
                        return 24;
                }
            } else {
                if (block == BlockList.glowGrass.id && stack.getItemDamage() == 1) {
                    return 25;
                }

                if (block == BlockList.sapling.id) {
                    return stack.getItemDamage() + 128;
                }
            }
        }

        return 0;
    }
}
