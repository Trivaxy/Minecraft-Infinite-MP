package net.minecraft.game.item.block;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.tileentity.other.SignEntity;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;
import net.minecraft.util.math.MathHelper;

public class SignItem extends Item {
    public static List<Icon> icons = new ArrayList<>();
    public static final String[] signs = new String[]{
        "sign",
        "signBirch",
        "signSpruce",
        "signPalm",
        "signAcacia",
        "signJungle",
        "signMangrove",
        "signBaobab",
        "signScorched",
        "signMaple",
        "signCherry",
        "signMahogany",
        "signOldenOak",
        "signCrimson",
        "signNylium",
        "signPurpur",
        "signWillow",
        "signLime"
    };

    public SignItem(int id) {
        super(id);
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

        if (!BlockList.signStanding.placeableAt(world, xCoord, yCoord, zCoord)) {
            return false;
        }

        int meta = stack.itemDmg * 16;
        int standingID = BlockList.signStanding.id;
        int hangingID = BlockList.signHanging.id;
        int wallID = BlockList.signWall.id;
        if (stack.itemDmg >= 16) {
            meta = (stack.itemDmg - 16) * 16;
            standingID = BlockList.signStanding2.id;
            hangingID = BlockList.signHanging2.id;
            wallID = BlockList.signWall2.id;
        }

        switch (side) {
            case 0:
                world.setBlockAndMetadataWithNotify(xCoord, yCoord, zCoord, hangingID, MathHelper.floor_double((player.yaw + 180.0) * 16.0 / 360.0 + 0.5) & 15);
                world.setBlockMetadata(xCoord, yCoord, zCoord, world.getBlockMetadata(xCoord, yCoord, zCoord) + meta);
                break;
            case 1:
                world.setBlockAndMetadataWithNotify(xCoord, yCoord, zCoord, standingID, MathHelper.floor_double((player.yaw + 180.0) * 16.0 / 360.0 + 0.5) & 15);
                world.setBlockMetadata(xCoord, yCoord, zCoord, world.getBlockMetadata(xCoord, yCoord, zCoord) + meta);
                break;
            default:
                world.setBlockAndMetadataWithNotify(xCoord, yCoord, zCoord, wallID, side);
                world.setBlockMetadata(xCoord, yCoord, zCoord, world.getBlockMetadata(xCoord, yCoord, zCoord) + meta);
        }

        if (player.properties.gamemode != 1) {
            stack.stackSize--;
        }

        player.displayGUIEditSign((SignEntity)world.getTileEntity(xCoord, yCoord, zCoord));
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
        return "item." + signs[damage];
    }

    @Override
    public Icon getIconFromMeta(int meta) {
        return icons.get(meta);
    }

    @Override
    public void registerIcons(IconRegister ir) {
        for (int i = 0; i < this.getMaxDamage(); i++) {
            icons.add(ir.registerIcon("signs/" + signs[i]));
        }
    }
}
