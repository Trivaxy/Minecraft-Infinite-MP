package net.minecraft.game.item.misc;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.building.Cloth;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.animal.Sheep;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class Dye extends Item {
    public static List<Icon> icons = new ArrayList<>();

    public Dye(int itemid) {
        super(itemid);
        this.setHasSubtypes(true);
        this.maxDamage = 0;
        this.priorityItem = true;
    }

    @Override
    public int getMaxDamage() {
        return 16;
    }

    @Override
    public String getNameFromMeta(int damage) {
        return "item.dye_" + damage;
    }

    @Override
    public Icon getIconFromMeta(int meta) {
        if (meta >= this.getMaxDamage()) {
            meta = 0;
        }

        return icons.get(meta);
    }

    @Override
    public void registerIcons(IconRegister ir) {
        for (int i = 0; i < this.getMaxDamage(); i++) {
            icons.add(ir.registerIcon("dyes/dye_" + i));
        }
    }

    @Override
    public void getSubItems(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i < this.getMaxDamage(); i++) {
            ls.add(new ItemStack(id, 1, i));
        }
    }

    @Override
    public boolean useOnBlock(ItemStack stack, Player player, World world, int x, int y, int z, int side) {
        int id = world.getBlockId(x, y, z);
        int count = 15;
        if (id == BlockList.brick.id || id == BlockList.hardenedClay.id) {
            count = 16;
        }

        boolean isWorldSurvival = player.properties.gamemode != 1;
        boolean condition = false;
        if ((
                id == BlockList.cloth.id
                    || id == BlockList.carpet.id
                    || id == BlockList.glassStained.id
                    || id == BlockList.glass.id
                    || id == BlockList.coloredPane.id
                    || id == BlockList.hardenedClay.id
                    || id == BlockList.brick.id
            )
            && world.getBlockMetadata(x, y, z) != count - stack.getItemDamage()
            && !world.isOnline) {
            world.setBlockMetadataWithNotify(x, y, z, count - stack.getItemDamage());
            if (isWorldSurvival) {
                stack.stackSize--;
            }

            condition = true;
        }

        return condition;
    }

    @Override
    public void useOnEntity(ItemStack stack, Mob target, Player player) {
        if (target instanceof Sheep) {
            Sheep sheeple = (Sheep)target;
            int fleeceColor = Cloth.getClothColor(stack.getItemDamage());
            if (!sheeple.getSheared() && sheeple.getFleeceColor() != fleeceColor) {
                sheeple.setFleeceColor(fleeceColor);
                if (player.properties.gamemode != 1) {
                    stack.stackSize--;
                }
            }
        }
    }
}
