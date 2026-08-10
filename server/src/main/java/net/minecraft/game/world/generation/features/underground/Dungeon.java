package net.minecraft.game.world.generation.features.underground;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.tileentity.container.ChestEntity;
import net.minecraft.game.block.tileentity.other.SpawnerEntity;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.Feature;

public class Dungeon extends Feature {
    public static final Dungeon INSTANCE = new Dungeon();

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        byte height = 3;
        int sizeX = random.nextInt(2) + 2;
        int sizeZ = random.nextInt(2) + 2;
        int bounds = 0;

        for (int x0 = x - sizeX - 1; x0 <= x + sizeX + 1; x0++) {
            for (int y0 = y - 1; y0 <= y + height + 1; y0++) {
                for (int z0 = z - sizeZ - 1; z0 <= z + sizeZ + 1; z0++) {
                    Material mat = world.getBlockMaterial(x0, y0, z0);
                    if (y0 == y - 1 && !mat.isSolid()) {
                        return false;
                    }

                    if (y0 == y + height + 1 && !mat.isSolid()) {
                        return false;
                    }

                    if ((x0 == x - sizeX - 1 || x0 == x + sizeX + 1 || z0 == z - sizeZ - 1 || z0 == z + sizeZ + 1)
                        && y0 == y
                        && world.getBlockId(x0, y0, z0) == 0
                        && world.getBlockId(x0, y0 + 1, z0) == 0) {
                        bounds++;
                    }
                }
            }
        }

        if (bounds >= 1 && bounds <= 5) {
            for (int var18 = x - sizeX - 1; var18 <= x + sizeX + 1; var18++) {
                for (int y0 = y + height; y0 >= y - 1; y0--) {
                    for (int z0 = z - sizeZ - 1; z0 <= z + sizeZ + 1; z0++) {
                        if (var18 != x - sizeX - 1
                            && y0 != y - 1
                            && z0 != z - sizeZ - 1
                            && var18 != x + sizeX + 1
                            && y0 != y + height + 1
                            && z0 != z + sizeZ + 1) {
                            world.setBlock(var18, y0, z0, 0);
                        } else if (y0 >= 0 && !world.getBlockMaterial(var18, y0 - 1, z0).isSolid()) {
                            world.setBlock(var18, y0, z0, 0);
                        } else if (world.getBlockMaterial(var18, y0, z0).isSolid()) {
                            if (y0 == y - 1 && random.nextInt(4) != 0) {
                                world.setBlock(var18, y0, z0, BlockList.cobblestoneMossy.id);
                            } else {
                                world.setBlock(var18, y0, z0, BlockList.cobblestone.id);
                            }
                        }
                    }
                }
            }

            for (int var19 = 0; var19 < 2; var19++) {
                for (int y0 = 0; y0 < 3; y0++) {
                    int z0 = x + random.nextInt(sizeX * 2 + 1) - sizeX;
                    int chestOffset = z + random.nextInt(sizeZ * 2 + 1) - sizeZ;
                    if (world.getBlockId(z0, y, chestOffset) == 0) {
                        int i15 = 0;
                        if (world.getBlockMaterial(z0 - 1, y, chestOffset).isSolid()) {
                            i15++;
                        }

                        if (world.getBlockMaterial(z0 + 1, y, chestOffset).isSolid()) {
                            i15++;
                        }

                        if (world.getBlockMaterial(z0, y, chestOffset - 1).isSolid()) {
                            i15++;
                        }

                        if (world.getBlockMaterial(z0, y, chestOffset + 1).isSolid()) {
                            i15++;
                        }

                        if (i15 == 1) {
                            world.setBlock(z0, y, chestOffset, BlockList.chest.id);
                            ChestEntity tileEntityChest16 = (ChestEntity)world.getTileEntity(z0, y, chestOffset);

                            for (int i = 0; i < 8; i++) {
                                ItemStack stack = this.pickLootItem(random);
                                if (stack != null) {
                                    tileEntityChest16.setSlotContents(random.nextInt(tileEntityChest16.getSize()), stack);
                                }
                            }
                            break;
                        }
                    }
                }
            }

            world.setBlock(x, y, z, BlockList.mobSpawner.id);
            SpawnerEntity spawner = (SpawnerEntity)world.getTileEntity(x, y, z);
            spawner.mobID = this.pickMobSpawner(random);
            return true;
        } else {
            return false;
        }
    }

    private ItemStack pickLootItem(Random random) {
        int itm = random.nextInt(11);
        switch (itm) {
            case 0:
                return new ItemStack(ItemList.saddle);
            case 1:
                return new ItemStack(ItemList.ingotIron, random.nextInt(4) + 1);
            case 2:
                return new ItemStack(ItemList.carrot, random.nextInt(4) + 1);
            case 3:
                return new ItemStack(ItemList.ingotCopper, random.nextInt(4) + 1);
            case 4:
                return new ItemStack(ItemList.potato, random.nextInt(4) + 1);
            case 5:
                if (random.nextInt(100) == 0) {
                    return new ItemStack(ItemList.appleGold);
                }

                return null;
            case 6:
                int type = random.nextInt(11);
                switch (type) {
                    case 0:
                        return new ItemStack(ItemList.recordGold);
                    case 1:
                        return new ItemStack(ItemList.recordGreen);
                    case 2:
                        return new ItemStack(ItemList.recordRed);
                    case 3:
                        return new ItemStack(ItemList.recordPurple);
                    case 4:
                        return new ItemStack(ItemList.recordTeal);
                    case 5:
                        return new ItemStack(ItemList.recordOrange);
                    case 6:
                        return new ItemStack(ItemList.recordBlue);
                    case 7:
                        return new ItemStack(ItemList.recordWhite);
                    case 8:
                        return new ItemStack(ItemList.recordBlack);
                    case 9:
                        return new ItemStack(ItemList.recordBroken);
                    case 10:
                        return new ItemStack(ItemList.recordCactus);
                }
            case 7:
                return new ItemStack(ItemList.nameTag);
            case 8:
                return new ItemStack(ItemList.backpack);
            case 9:
                return new ItemStack(ItemList.crossbow);
            case 10:
                return new ItemStack(ItemList.pouch);
            default:
                return null;
        }
    }

    private String pickMobSpawner(Random random) {
        int mob = random.nextInt(3);
        return mob == 0 ? "Skeleton" : (mob == 1 ? "Zombie" : (mob == 2 ? "Spider" : ""));
    }
}
