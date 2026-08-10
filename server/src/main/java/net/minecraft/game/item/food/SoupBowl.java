package net.minecraft.game.item.food;

import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.potion.EffectData;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;

public class SoupBowl extends FoodItem {
    public SoupBowl(int id, int heal, float sat) {
        super(id, heal, sat);
        this.setMaxStackSize(1);
    }

    @Override
    public Item.Action getItemUseAction(ItemStack stack) {
        return Item.Action.drink;
    }

    @Override
    public ItemStack onFinishedUsing(ItemStack stack, World world, Player player) {
        super.onFinishedUsing(stack, world, player);
        if (this.id == ItemList.bowlSussy.id) {
            int id = rand.nextInt(19) + 1;
            if (!EffectList.effects[id].isInstant()) {
                player.addEffect(new EffectData(id, 300, world.rand.nextInt(3)));
            }
        } else {
            player.addEffect(new EffectData(EffectList.stamina.id, 300, 0));
            player.addEffect(new EffectData(EffectList.resistance.id, 150, 1));
        }

        player.addStat(StatList.useStats[this.id], 1);
        return new ItemStack(ItemList.bowlEmpty);
    }
}
