package net.minecraft.game.item.block;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.stepsound.StepSound;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;
import net.minecraft.util.math.MathHelper;

public class FlagItem extends Item {
    public static List<Icon> icons = new ArrayList<>();
    public static final String[] colors = new String[]{
        "flagWhite",
        "flagLightGray",
        "flagDarkGray",
        "flagRose",
        "flagMagenta",
        "flagPurple",
        "flagViolet",
        "flagUltramarine",
        "flagCapri",
        "flagCyan",
        "flagSpringGreen",
        "flagGreen",
        "flagChartreuse",
        "flagYellow",
        "flagOrange",
        "flagRed",
        "flagBlack",
        "flagBrown"
    };

    public FlagItem(int itemid) {
        super(itemid);
        this.priorityItem = true;
        this.setHasSubtypes(true);
    }

    @Override
    public int getMaxDamage() {
        return 18;
    }

    @Override
    public boolean useOnBlock(ItemStack stack, Player player, World world, int xCoord, int yCoord, int zCoord, int side) {
        if (!world.getBlockMaterial(xCoord, yCoord, zCoord).isSolid()) {
            return false;
        }

        if (side == 0) {
            yCoord--;
        }

        if (side == 1) {
            yCoord++;
        }

        if (side == 2) {
            zCoord--;
        }

        if (side == 3) {
            zCoord++;
        }

        if (side == 4) {
            xCoord--;
        }

        if (side == 5) {
            xCoord++;
        }

        if (!BlockList.flag.placeableAt(world, xCoord, yCoord, zCoord)) {
            return false;
        }

        world.setBlock(xCoord, yCoord, zCoord, BlockList.flag.id);
        world.setBlock(xCoord, yCoord + 1, zCoord, BlockList.flag.id);
        int facing = MathHelper.floor_double(player.yaw * 4.0F / 360.0F + 0.5) & 3;
        if (facing == 0) {
            world.setBlockMetadataWithNotify(xCoord, yCoord + 1, zCoord, stack.itemDmg * 4 + 1);
        }

        if (facing == 1) {
            world.setBlockMetadataWithNotify(xCoord, yCoord + 1, zCoord, stack.itemDmg * 4 + 4);
        }

        if (facing == 2) {
            world.setBlockMetadataWithNotify(xCoord, yCoord + 1, zCoord, stack.itemDmg * 4 + 2);
        }

        if (facing == 3) {
            world.setBlockMetadataWithNotify(xCoord, yCoord + 1, zCoord, stack.itemDmg * 4 + 3);
        }

        if (player.properties.gamemode != 1) {
            stack.stackSize--;
        }

        StepSound snd = BlockList.flag.getStepSound(0);
        world.playBlockSound(player, xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F, snd.getSound(), (snd.getVolume() + 1.0F) / 2.0F, snd.getPitch() * 0.8F);
        player.addStat(StatList.useStats[this.id], 1);
        return true;
    }

    @Override
    public void getSubItems(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i < this.getMaxDamage(); i++) {
            ls.add(new ItemStack(id, 1, i));
        }
    }

    @Override
    public String getNameFromMeta(int damage) {
        return "item." + colors[damage];
    }

    @Override
    public Icon getIconFromMeta(int meta) {
        return icons.get(meta);
    }

    @Override
    public void registerIcons(IconRegister ir) {
        for (int i = 0; i < this.getMaxDamage(); i++) {
            icons.add(ir.registerIcon("flags/" + colors[i]));
        }
    }
}
