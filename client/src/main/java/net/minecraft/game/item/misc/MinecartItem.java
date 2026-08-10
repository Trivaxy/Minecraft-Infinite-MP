package net.minecraft.game.item.misc;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.misc.MinecartTrack;
import net.minecraft.game.entity.minecart.EngineMinecart;
import net.minecraft.game.entity.minecart.ExplosiveMinecart;
import net.minecraft.game.entity.minecart.Minecart;
import net.minecraft.game.entity.minecart.StorageMinecart;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class MinecartItem extends Item {
    private int minecartType;

    public MinecartItem(int id, int typ) {
        super(id);
        this.maxStackSize = 1;
        this.minecartType = typ;
        this.priorityItem = true;
    }

    @Override
    public boolean useOnBlock(ItemStack stack, Player player, World world, int x, int y, int z, int side) {
        Block block = world.getBlock(x, y, z);
        if (block instanceof MinecartTrack) {
            if (!world.isOnline) {
                Minecart cart = new Minecart(world, x + 0.5F, y + 0.5F, z + 0.5F);
                switch (this.minecartType) {
                    case 1:
                        cart = new StorageMinecart(world, x + 0.5F, y + 0.5F, z + 0.5F);
                        break;
                    case 2:
                        cart = new EngineMinecart(world, x + 0.5F, y + 0.5F, z + 0.5F);
                        break;
                    case 3:
                        cart = new ExplosiveMinecart(world, x + 0.5F, y + 0.5F, z + 0.5F);
                }

                world.spawnEntity(cart);
            }

            if (player.properties.gamemode != 1) {
                stack.stackSize--;
            }

            player.addStat(StatList.useStats[this.id], 1);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.itemIcon = ir.registerIcon("minecarts/" + this.internalName);
    }
}
