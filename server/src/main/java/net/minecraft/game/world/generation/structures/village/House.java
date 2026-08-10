package net.minecraft.game.world.generation.structures.village;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.structures.StructurePiece;

public abstract class House extends StructurePiece {
    public House(World world, boolean rotated) {
        super(world, rotated);
    }

    @Override
    public boolean buildingFitsCheck() {
        return true;
    }

    @Override
    protected ItemStack getTreasure(int level, Random rand) {
        if (level < 5) {
            switch (rand.nextInt(10)) {
                case 0:
                    return new ItemStack(ItemList.bread, rand.nextInt(2) + 1);
                case 1:
                    return new ItemStack(ItemList.axeCopper);
                case 2:
                    return new ItemStack(ItemList.shovelCopper);
                case 3:
                    return new ItemStack(ItemList.swordCopper);
                case 4:
                    return new ItemStack(ItemList.pickaxeCopper);
                case 5:
                    return new ItemStack(ItemList.ingotIron, 2 + rand.nextInt(4));
                case 6:
                    return new ItemStack(ItemList.carrot, rand.nextInt(2) + 1);
                case 7:
                    return new ItemStack(ItemList.potato, rand.nextInt(2) + 1);
                case 8:
                    return new ItemStack(ItemList.nuggetGold, rand.nextInt(4) + 2);
                default:
                    return new ItemStack(ItemList.coal, 4 + rand.nextInt(4));
            }
        } else {
            switch (rand.nextInt(10)) {
                case 0:
                    return new ItemStack(ItemList.appleRed, rand.nextInt(2) + 1);
                case 1:
                    return new ItemStack(BlockList.torch, 4 + rand.nextInt(4));
                case 2:
                    return new ItemStack(ItemList.seeds, 4 + rand.nextInt(8));
                case 3:
                    return new ItemStack(ItemList.wheat, 4 + rand.nextInt(4));
                case 4:
                    return new ItemStack(ItemList.bone, 4 + rand.nextInt(4));
                case 5:
                    return new ItemStack(ItemList.feather, 4 + rand.nextInt(4));
                case 6:
                    return new ItemStack(BlockList.wood, 4 + rand.nextInt(4));
                case 7:
                    return new ItemStack(ItemList.stick, 4 + rand.nextInt(8));
                case 8:
                    return new ItemStack(BlockList.sapling, 4 + rand.nextInt(8));
                default:
                    return new ItemStack(ItemList.coal, 2 + rand.nextInt(2));
            }
        }
    }

    @Override
    protected int getChestNumberOfItems() {
        return this.world.rand.nextInt(4) + 2;
    }

    protected void generatePillars(int x, int z) {
        for (int y = this.y0 - 1;
            y >= 1 && (this.getBlockRelative(x, y - this.y0, z) == 0 || this.getBlockRelative(x, y - this.y0, z) == BlockList.waterStill.id);
            y--
        ) {
            this.drawClippedBoxRelative(x, y - this.y0, z, x, y - this.y0, z, BlockList.wood.id, 0, true);
        }
    }
}
