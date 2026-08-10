package net.minecraft.game.item.crafting.special;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.container.inventory.CraftingInventory;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.crafting.ShapedRecipe;
import net.minecraft.game.world.World;
import net.minecraft.game.world.mapdata.MapData;

public class MapExpansionRecipes extends ShapedRecipe {
    public MapExpansionRecipes() {
        super(
            3,
            3,
            new ItemStack[]{
                new ItemStack(ItemList.paper),
                new ItemStack(ItemList.paper),
                new ItemStack(ItemList.paper),
                new ItemStack(ItemList.paper),
                new ItemStack(ItemList.map, 1, 32767),
                new ItemStack(ItemList.paper),
                new ItemStack(ItemList.paper),
                new ItemStack(ItemList.paper),
                new ItemStack(ItemList.paper)
            },
            new ItemStack(ItemList.mapEmpty, 1, 0)
        );
    }

    @Override
    public boolean matches(CraftingInventory inv, World world) {
        if (!super.matches(inv, world)) {
            return false;
        }

        ItemStack stack = null;

        for (int slot = 0; slot < inv.getSize() && stack == null; slot++) {
            ItemStack slotStack = inv.getStackInSlot(slot);
            if (slotStack != null && slotStack.itemID == ItemList.map.id) {
                stack = slotStack;
            }
        }

        if (stack == null) {
            return false;
        }

        MapData mapData = ItemList.map.getMapData(stack, world);
        return mapData == null ? false : mapData.scale < 4;
    }

    @Override
    public ItemStack getCraftResult(CraftingInventory inv) {
        ItemStack stack = null;

        for (int slot = 0; slot < inv.getSize() && stack == null; slot++) {
            ItemStack slotStack = inv.getStackInSlot(slot);
            if (slotStack != null && slotStack.itemID == ItemList.map.id) {
                stack = slotStack;
            }
        }

        stack = stack.copy();
        stack.stackSize = 1;
        if (stack.getTag() == null) {
            stack.setTag(new CompoundTag());
        }

        stack.getTag().setBoolean("map_is_scaling", true);
        return stack;
    }
}
