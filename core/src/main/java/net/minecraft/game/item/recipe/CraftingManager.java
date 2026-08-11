package net.minecraft.game.item.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import net.minecraft.game.block.Block;
import net.minecraft.game.container.inventory.CraftingInventory;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.recipe.blocks.BarrierRecipes;
import net.minecraft.game.item.recipe.blocks.BlockRecipes;
import net.minecraft.game.item.recipe.blocks.FurnitureRecipes;
import net.minecraft.game.item.recipe.blocks.MachineRecipes;
import net.minecraft.game.item.recipe.blocks.SlabRecipes;
import net.minecraft.game.item.recipe.blocks.StairRecipes;
import net.minecraft.game.item.recipe.misc.DyeRecipes;
import net.minecraft.game.item.recipe.misc.FoodRecipes;
import net.minecraft.game.item.recipe.misc.ItemRecipes;
import net.minecraft.game.item.recipe.special.DyedArmorRecipes;
import net.minecraft.game.item.recipe.special.DyedItemRecipes;
import net.minecraft.game.item.recipe.special.MapCaveRecipes;
import net.minecraft.game.item.recipe.special.MapCloningRecipes;
import net.minecraft.game.item.recipe.special.MapExpansionRecipes;
import net.minecraft.game.item.recipe.tool.ArmorRecipes;
import net.minecraft.game.item.recipe.tool.RecordRecipes;
import net.minecraft.game.item.recipe.tool.ToolRecipes;
import net.minecraft.game.item.recipe.tool.TreasureRecipes;
import net.minecraft.game.item.recipe.tool.WeaponRecipes;
import net.minecraft.game.world.World;

public class CraftingManager {
    private static final CraftingManager instance = new CraftingManager();
    private final List<Recipe> recipes = new ArrayList<>();

    public static CraftingManager getInstance() {
        return instance;
    }

    private CraftingManager() {
        new ToolRecipes().addRecipes(this);
        new WeaponRecipes().addRecipes(this);
        new ArmorRecipes().addRecipes(this);
        new TreasureRecipes().addRecipes(this);
        new BlockRecipes().addRecipes(this);
        new DyeRecipes().addRecipes(this);
        new BarrierRecipes().addRecipes(this);
        new SlabRecipes().addRecipes(this);
        new StairRecipes().addRecipes(this);
        new FurnitureRecipes().addRecipes(this);
        new MachineRecipes().addRecipes(this);
        new FoodRecipes().addRecipes(this);
        new ItemRecipes().addRecipes(this);
        new RecordRecipes().addRecipes(this);
        this.recipes.add(new DyedArmorRecipes());
        this.recipes.add(new DyedItemRecipes());
        this.recipes.add(new MapCloningRecipes());
        this.recipes.add(new MapExpansionRecipes());
        this.recipes.add(new MapCaveRecipes());
        this.recipes
            .sort(
                (recipe1, recipe2) -> recipe1 instanceof ShapelessRecipe && recipe2 instanceof ShapedRecipe
                    ? 1
                    : (
                        recipe2 instanceof ShapelessRecipe && recipe1 instanceof ShapedRecipe
                            ? -1
                            : Integer.compare(recipe2.getRecipeSize(), recipe1.getRecipeSize())
                    )
            );
        Logger.getLogger("Minecraft").info(this.recipes.size() + " recipes");
    }

    public final void addRecipe(ItemStack output, Object... inputs) {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        int length = 0;
        int next = 0;
        if (inputs[index] instanceof String[]) {
            String[] in1 = (String[])inputs[index++];

            for (String in : in1) {
                next++;
                length = in.length();
                sb.append(in);
            }
        } else {
            while (inputs[index] instanceof String) {
                String in = (String)inputs[index++];
                next++;
                length = in.length();
                sb.append(in);
            }
        }

        HashMap<Character, ItemStack> recipeMap = new HashMap<>();

        while (index < inputs.length) {
            Character chara = (Character)inputs[index];
            ItemStack stack = null;
            if (inputs[index + 1] instanceof Item) {
                stack = new ItemStack((Item)inputs[index + 1]);
            } else if (inputs[index + 1] instanceof Block) {
                stack = new ItemStack((Block)inputs[index + 1], 1, -1);
            } else if (inputs[index + 1] instanceof ItemStack) {
                stack = (ItemStack)inputs[index + 1];
            }

            recipeMap.put(chara, stack);
            index += 2;
        }

        ItemStack[] stacks = new ItemStack[length * next];

        for (int i = 0; i < length * next; i++) {
            char chara = sb.charAt(i);
            if (recipeMap.containsKey(chara)) {
                stacks[i] = recipeMap.get(chara).copy();
            } else {
                stacks[i] = null;
            }
        }

        this.recipes.add(new ShapedRecipe(length, next, stacks, output));
    }

    public final void addShapelessRecipe(ItemStack output, Object... inputs) {
        ArrayList<ItemStack> stacks = new ArrayList<>();

        for (Object input : inputs) {
            if (input instanceof ItemStack) {
                stacks.add(((ItemStack)input).copy());
            } else if (input instanceof Item) {
                stacks.add(new ItemStack((Item)input));
            } else {
                if (!(input instanceof Block)) {
                    throw new RuntimeException("Invalid shapeless recipe!");
                }

                stacks.add(new ItemStack((Block)input));
            }
        }

        this.recipes.add(new ShapelessRecipe(output, stacks));
    }

    public ItemStack findMatchingRecipe(CraftingInventory inv, World world) {
        for (Recipe recipe : this.recipes) {
            if (recipe.matches(inv, world)) {
                return recipe.getCraftResult(inv);
            }
        }

        return null;
    }

    public List<Recipe> getRecipeList() {
        return this.recipes;
    }
}
