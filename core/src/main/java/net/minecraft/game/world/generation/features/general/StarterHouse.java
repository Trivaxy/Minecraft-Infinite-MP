package net.minecraft.game.world.generation.features.general;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.tileentity.container.ChestEntity;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.Feature;

public class StarterHouse extends Feature {
    public static final StarterHouse INSTANCE = new StarterHouse();

    @Override
    public boolean place(World world, Random random, int x, int y, int z) {
        for (int x0 = x - 3; x0 <= x + 3; x0++) {
            for (int y0 = y - 2; y0 <= y + 2; y0++) {
                for (int z0 = z - 3; z0 <= z + 3; z0++) {
                    int id = 0;
                    if (x0 == x - 3 || z0 == z - 3 || x0 == x + 3 || z0 == z + 3 || y0 == y - 2 || y0 == y + 2) {
                        id = BlockList.stone.id;
                        if (y0 >= y - 1) {
                            id = BlockList.planks.id;
                        }
                    }

                    if (z0 == z - 3 && x0 == x && y0 >= y - 1 && y0 <= y) {
                        id = 0;
                    }

                    world.setBlock(x0, y0, z0, id);
                }
            }
        }

        int offX = random.nextInt(2) + 2;
        int offZ = random.nextInt(2) + 2;

        for (int attempts = 0; attempts < 6; attempts++) {
            int x0 = x + random.nextInt(offX * 2 + 1) - offX;
            int z0 = z + random.nextInt(offZ * 2 + 1) - offZ;
            if (world.getBlockId(x0, y, z0) == 0) {
                boolean canGenerateChest = false;
                if (world.getBlockMaterial(x0 - 1, y, z0).isSolid()) {
                    canGenerateChest = true;
                }

                if (world.getBlockMaterial(x0 + 1, y, z0).isSolid()) {
                    canGenerateChest = true;
                }

                if (world.getBlockMaterial(x0, y, z0 - 1).isSolid()) {
                    canGenerateChest = true;
                }

                if (world.getBlockMaterial(x0, y, z0 + 1).isSolid()) {
                    canGenerateChest = true;
                }

                if (canGenerateChest) {
                    world.setBlock(x0, y - 1, z0, BlockList.chest.id);
                    ChestEntity chest = (ChestEntity)world.getTileEntity(x0, y - 1, z0);

                    for (int itms = 0; itms < 8; itms++) {
                        ItemStack stack = this.pickStarterGear(random);
                        if (stack != null) {
                            chest.setSlotContents(random.nextInt(chest.getSize()), stack);
                        }
                    }
                    break;
                }
            }
        }

        world.setBlockWithNotify(x - 3 + 1, y, z, BlockList.torch.id);
        world.setBlockWithNotify(x + 3 - 1, y, z, BlockList.torch.id);
        return true;
    }

    public ItemStack pickStarterGear(Random random) {
        int id = random.nextInt(13);
        switch (id) {
            case 0:
                return new ItemStack(ItemList.axeStone, 1);
            case 1:
                return new ItemStack(ItemList.pickaxeStone, 1);
            case 2:
                return new ItemStack(ItemList.bread, random.nextInt(2) + 1);
            case 3:
                return new ItemStack(BlockList.wood, random.nextInt(4) + 1, random.nextInt(13));
            case 4:
                return new ItemStack(BlockList.planks, random.nextInt(4) + 1, random.nextInt(13));
            case 5:
                return new ItemStack(ItemList.stick, random.nextInt(8) + 2);
            case 6:
                return new ItemStack(ItemList.appleRed, random.nextInt(4) + 1);
            case 7:
                return new ItemStack(ItemList.fishRaw, random.nextInt(2) + 1);
            case 8:
                return new ItemStack(ItemList.bootsLeather);
            case 9:
                return new ItemStack(ItemList.helmetLeather);
            case 10:
                return new ItemStack(BlockList.torch, random.nextInt(6) + 2);
            case 11:
                return new ItemStack(ItemList.shovelStone, 1);
            case 12:
                return new ItemStack(ItemList.swordStone, 1);
            default:
                return null;
        }
    }
}
