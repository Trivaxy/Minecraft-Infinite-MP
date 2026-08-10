package net.minecraft.game.item.crafting.special;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;

public class FurnaceRecipes {
    private static final FurnaceRecipes smeltingBase = new FurnaceRecipes();
    private Map<Integer, ItemStack> smeltingList = new HashMap<>();

    public static final FurnaceRecipes smelting() {
        return smeltingBase;
    }

    private FurnaceRecipes() {
        this.addSmelting(BlockList.oreCopper.id, new ItemStack(ItemList.ingotCopper));
        this.addSmelting(BlockList.oreIron.id, new ItemStack(ItemList.ingotIron));
        this.addSmelting(BlockList.oreGold.id, new ItemStack(ItemList.ingotGold));
        this.addSmelting(BlockList.oreDiamond.id, new ItemStack(ItemList.diamond));
        this.addSmelting(BlockList.oreRuby.id, new ItemStack(ItemList.ruby));
        this.addSmelting(BlockList.oreSapphire.id, new ItemStack(ItemList.sapphire));
        this.addSmelting(BlockList.oreEmerald.id, new ItemStack(ItemList.emerald));
        this.addSmelting(BlockList.oreAmethyst.id, new ItemStack(ItemList.amethyst));
        this.addSmelting(BlockList.oreTopaz.id, new ItemStack(ItemList.topaz));
        this.addSmelting(BlockList.oreCoal.id, new ItemStack(ItemList.coal, 1, 0));
        this.addSmelting(BlockList.netherGold.id, new ItemStack(ItemList.ingotGold));
        this.addSmelting(BlockList.aetherIron.id, new ItemStack(ItemList.ingotIron));
        this.addSmelting(ItemList.porkRaw.id, new ItemStack(ItemList.porkCooked));
        this.addSmelting(ItemList.muttonRaw.id, new ItemStack(ItemList.muttonCooked));
        this.addSmelting(ItemList.chimkenRaw.id, new ItemStack(ItemList.chimkenCooked));
        this.addSmelting(ItemList.rabbitRaw.id, new ItemStack(ItemList.rabbitCooked));
        this.addSmelting(ItemList.fishRaw.id, new ItemStack(ItemList.fishCooked));
        this.addSmelting(ItemList.potato.id, new ItemStack(ItemList.bakedPotato));
        this.addSmelting(ItemList.egg.id, new ItemStack(ItemList.eggBoiled));
        this.addSmelting(BlockList.cobblestone.id, new ItemStack(BlockList.stone));
        this.addSmelting(BlockList.clay.id, new ItemStack(BlockList.hardenedClay, 1, 255));
        this.addSmelting(BlockList.sand.id, new ItemStack(BlockList.glass));
        this.addSmelting(BlockList.redSand.id, new ItemStack(BlockList.glass));
        this.addSmelting(BlockList.silverSand.id, new ItemStack(BlockList.glass));
        this.addSmelting(ItemList.clay.id, new ItemStack(ItemList.brick));
        this.addSmelting(ItemList.rawCopper.id, new ItemStack(ItemList.ingotCopper));
        this.addSmelting(ItemList.rawIron.id, new ItemStack(ItemList.ingotIron));
        this.addSmelting(ItemList.rawGold.id, new ItemStack(ItemList.ingotGold));
        this.addSmelting(BlockList.wood.id, new ItemStack(ItemList.coal, 1, 1));
        this.addSmelting(BlockList.log.id, new ItemStack(ItemList.coal, 1, 1));
        this.addSmelting(BlockList.coral.id, new ItemStack(BlockList.sponge, 1));
        this.addSmelting(BlockList.dirt.id, new ItemStack(BlockList.dirt, 1, 0));
    }

    public void addSmelting(int input, ItemStack output) {
        this.smeltingList.put(input, output);
    }

    public ItemStack getSmeltingResult(int input, int meta) {
        if (input == BlockList.cobblestone.id) {
            return new ItemStack(BlockList.stone.id, 1, meta);
        }

        if (input == BlockList.dirt.id) {
            switch (meta) {
                case 0:
                    return new ItemStack(BlockList.dirt.id, 1, 3);
                case 3:
                    return new ItemStack(BlockList.dirt.id, 1, 6);
                default:
                    return new ItemStack(BlockList.dirt.id, 1);
            }
        } else {
            return input == BlockList.stoneBrick.id && meta == 0 ? new ItemStack(BlockList.stoneBrick.id, 1, 2) : this.smeltingList.get(input);
        }
    }

    public Map<Integer, ItemStack> getSmeltingList() {
        return this.smeltingList;
    }
}
