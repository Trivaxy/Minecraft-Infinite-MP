package net.minecraft.game.item.misc;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.monster.boss.Guardian;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class Key extends Item {
    public static List<Icon> icons = new ArrayList<>();
    public static final String[] signs = new String[]{"key", "keyBronze", "keySilver", "keyGold", "keyDiamond"};

    public Key(int itemid) {
        super(itemid);
        this.maxStackSize = 1;
        this.priorityItem = true;
        this.setHasSubtypes(true);
    }

    @Override
    public int getMaxDamage() {
        return 5;
    }

    @Override
    public void getSubItems(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i < this.getMaxDamage(); i++) {
            ls.add(new ItemStack(id, 1, i));
        }
    }

    @Override
    public String getNameFromMeta(int damage) {
        return "item." + signs[damage];
    }

    @Override
    public Icon getIconFromMeta(int meta) {
        return icons.get(meta);
    }

    @Override
    public void registerIcons(IconRegister ir) {
        for (int i = 0; i < this.getMaxDamage(); i++) {
            icons.add(ir.registerIcon("keys/" + signs[i]));
        }
    }

    @Override
    public boolean useOnBlock(ItemStack stack, Player player, World world, int xCoord, int yCoord, int zCoord, int side) {
        int id = world.getBlockId(xCoord, yCoord, zCoord);
        int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
        if (id == BlockList.lockedChest.id && meta == 1) {
            world.setBlockMetadata(xCoord, yCoord, zCoord, 0);
            world.playSoundEffect(xCoord, yCoord, zCoord, "random.unlocked", 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
            if (player.properties.gamemode != 1) {
                stack.stackSize--;
            }

            List<Entity> entities = world.entities;
            if (!entities.isEmpty()) {
                for (int i = 0; i < entities.size(); i++) {
                    if (entities.get(i) instanceof Guardian) {
                        ((Guardian)entities.get(i)).setDead();
                    }
                }
            }

            player.addStat(StatList.useStats[this.id], 1);
            return true;
        } else {
            return false;
        }
    }
}
