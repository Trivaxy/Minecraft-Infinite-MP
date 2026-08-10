package net.minecraft.game.world.generation.structures.labyrinth;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.structures.StructurePiece;

public abstract class LabyrinthPiece extends StructurePiece {
    Item[] records = new Item[]{
        ItemList.recordGold,
        ItemList.recordGreen,
        ItemList.recordOrange,
        ItemList.recordPurple,
        ItemList.recordRed,
        ItemList.recordTeal,
        ItemList.recordBlue,
        ItemList.recordWhite,
        ItemList.recordBlack,
        ItemList.recordCactus,
        ItemList.recordBroken
    };

    public LabyrinthPiece(World world, boolean rotated) {
        super(world, rotated);
    }

    @Override
    public boolean buildingFitsCheck() {
        return true;
    }

    @Override
    protected ItemStack getTreasure(int level, Random rand) {
        if (level < 5) {
            switch (rand.nextInt(15)) {
                case 0:
                    return new ItemStack(ItemList.ingotIron, 2 + rand.nextInt(4));
                case 1:
                    return new ItemStack(ItemList.diamond, rand.nextInt(2) + 1);
                case 2:
                    return new ItemStack(ItemList.saddle);
                case 3:
                    return new ItemStack(ItemList.swordSteel);
                case 4:
                    return new ItemStack(ItemList.pickaxeSteel);
                case 5:
                    return new ItemStack(ItemList.crossbow);
                case 6:
                    return new ItemStack(ItemList.ingotCopper, 4 + rand.nextInt(4));
                case 7:
                    if (rand.nextInt(10) == 0) {
                        return new ItemStack(ItemList.appleGold);
                    }
                case 8:
                    return new ItemStack(this.records[rand.nextInt(this.records.length)]);
                case 9:
                    return new ItemStack(ItemList.backpack);
                case 10:
                    return new ItemStack(ItemList.potion, 1, rand.nextInt(19));
                case 11:
                    return new ItemStack(ItemList.arrowEffect, 1 + rand.nextInt(4));
                case 12:
                    return new ItemStack(ItemList.scroll, 1, rand.nextInt(6));
                case 13:
                    return new ItemStack(ItemList.pouch);
                default:
                    return new ItemStack(ItemList.arrowExplosive, 1 + rand.nextInt(4));
            }
        } else {
            switch (rand.nextInt(10)) {
                case 0:
                    return new ItemStack(ItemList.appleRed, rand.nextInt(2) + 1);
                case 1:
                    return new ItemStack(BlockList.torch, 4 + rand.nextInt(4));
                case 2:
                    return new ItemStack(ItemList.arrow, 4 + rand.nextInt(8));
                case 3:
                    return new ItemStack(ItemList.wheat, 4 + rand.nextInt(8));
                case 4:
                    return new ItemStack(ItemList.bone, 4 + rand.nextInt(4));
                case 5:
                    return new ItemStack(ItemList.ingotIron, 2 + rand.nextInt(4));
                case 6:
                    return new ItemStack(ItemList.silk, 4 + rand.nextInt(4));
                case 7:
                    return new ItemStack(ItemList.gunpowder, 4 + rand.nextInt(4));
                case 8:
                    return new ItemStack(ItemList.bowlSussy, 1);
                default:
                    return new ItemStack(ItemList.coal, 4 + rand.nextInt(4));
            }
        }
    }

    @Override
    protected int getChestNumberOfItems() {
        return this.world.rand.nextInt(4) + 2;
    }

    public String getRandomMob() {
        switch (this.world.rand.nextInt(3)) {
            case 1:
                return "Spider";
            case 2:
                return "Skeleton";
            default:
                return "Zombie";
        }
    }
}
