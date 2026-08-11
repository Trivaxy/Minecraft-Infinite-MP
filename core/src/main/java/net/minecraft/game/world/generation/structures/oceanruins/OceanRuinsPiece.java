package net.minecraft.game.world.generation.structures.oceanruins;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.structures.StructurePiece;

public abstract class OceanRuinsPiece extends StructurePiece {
    protected Random rand;

    public OceanRuinsPiece(World world, boolean rotated) {
        super(world, rotated);
        this.rand = new Random(world.getSeed());
    }

    @Override
    protected ItemStack getLoot(int level, Random rand) {
        switch (rand.nextInt(9)) {
            case 1:
                return new ItemStack(BlockList.cobweb.id, rand.nextInt(5) + 2);
            case 2:
                return new ItemStack(ItemList.bone.id, rand.nextInt(4) + 1);
            case 3:
                return new ItemStack(ItemList.ash.id, rand.nextInt(4) + 2);
            case 4:
                return new ItemStack(ItemList.rottenFlesh.id, rand.nextInt(4) + 1);
            case 5:
                return new ItemStack(ItemList.nuggetGold.id, rand.nextInt(4) + 1);
            case 6:
                return new ItemStack(ItemList.ruby.id, rand.nextInt(3) + 1);
            case 7:
                return new ItemStack(ItemList.sapphire.id, rand.nextInt(3) + 1);
            case 8:
                return new ItemStack(ItemList.emerald.id, rand.nextInt(3) + 1);
            default:
                return new ItemStack(ItemList.nuggetIron.id, rand.nextInt(8) + 3);
        }
    }

    @Override
    protected ItemStack getTreasure(int level, Random rand) {
        if (level < 4) {
            switch (rand.nextInt(6)) {
                case 0:
                    return new ItemStack(ItemList.ingotCopper, rand.nextInt(4) + 1);
                case 1:
                    return new ItemStack(ItemList.nameTag);
                case 2:
                    return new ItemStack(ItemList.bread);
                case 3:
                    return new ItemStack(ItemList.seaPickle, rand.nextInt(2) + 1);
                case 4:
                    return new ItemStack(ItemList.wheat, rand.nextInt(3) + 1);
                default:
                    return new ItemStack(ItemList.stick, rand.nextInt(16) + 1);
            }
        } else if (level < 9) {
            switch (rand.nextInt(10)) {
                case 0:
                    return new ItemStack(ItemList.ingotIron, rand.nextInt(4) + 1);
                case 1:
                    return new ItemStack(ItemList.seaPickle, rand.nextInt(4) + 2);
                case 2:
                    return new ItemStack(ItemList.crossbow);
                case 3:
                    return new ItemStack(ItemList.potion, 1, rand.nextInt(19));
                case 4:
                default:
                    return new ItemStack(ItemList.bowlSussy);
                case 5:
                    return new ItemStack(ItemList.fishingRod);
                case 6:
                    return new ItemStack(ItemList.swordSteel);
                case 7:
                    return new ItemStack(ItemList.backpack);
                case 8:
                    return new ItemStack(ItemList.pouch);
            }
        } else {
            switch (rand.nextInt(8)) {
                case 0:
                    return new ItemStack(ItemList.swordGold);
                case 1:
                    return new ItemStack(ItemList.seaPickle, rand.nextInt(6) + 3);
                case 2:
                    return this.getTreasure(2, rand);
                case 3:
                    return new ItemStack(ItemList.potion, 1, rand.nextInt(19));
                case 4:
                    return new ItemStack(ItemList.plateSteel);
                case 5:
                    return new ItemStack(ItemList.diamond, rand.nextInt(2) + 1);
                case 6:
                    return rand.nextInt(10) == 0 ? new ItemStack(ItemList.appleGold) : new ItemStack(ItemList.seaPickle);
                default:
                    return new ItemStack(ItemList.diamond);
            }
        }
    }

    @Override
    protected int getChestNumberOfItems() {
        return this.world.rand.nextInt(3) + 2;
    }

    @Override
    public boolean buildingFitsCheck() {
        return true;
    }
}
