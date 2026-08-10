package net.minecraft.game.item.misc;

import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.game.world.mapdata.MapData;
import net.minecraft.statistics.StatList;

public class EmptyMap extends Item {
    public EmptyMap(int id) {
        super(id);
        this.priorityItem = true;
    }

    @Override
    public ItemStack use(ItemStack stack, World world, Player player) {
        ItemStack newStack = new ItemStack(ItemList.map, 1, world.getMapId("map"));
        String mapID = "map_" + newStack.getItemDamage();
        MapData data = new MapData(mapID);
        world.setMapData(mapID, data);
        data.scale = 0;
        data.calculateMapCenter(player.posX, player.posZ, data.scale);
        data.dimension = (byte)world.currDim.dimension;
        data.markDirty();
        world.playEntitySound(player, "random.map", 1.0F, rand.nextFloat() / 0.4F + 0.8F);
        player.addStat(StatList.useStats[this.id], 1);
        stack.stackSize--;
        if (stack.stackSize <= 0) {
            return newStack;
        }

        if (!player.inventory.addItemToInventory(newStack.copy())) {
            player.dropPlayerItem(newStack);
        }

        return stack;
    }
}
