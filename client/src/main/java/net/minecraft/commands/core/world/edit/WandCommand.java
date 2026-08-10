package net.minecraft.commands.core.world.edit;

import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.localization.Translator;

public class WandCommand extends WorldEditCommand {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        Player thePlayer = ca.demandPlayer(src);
        World theWorld = ca.getWorld(thePlayer.dimension);
        float offset = 0.7F;
        double x = theWorld.rand.nextFloat() * offset + (1.0F - offset) * 0.5;
        double y = theWorld.rand.nextFloat() * offset + (1.0F - offset) * 0.5;
        double z = theWorld.rand.nextFloat() * offset + (1.0F - offset) * 0.5;
        DroppedItem wand = new DroppedItem(theWorld, thePlayer.posX + x, thePlayer.posY + y, thePlayer.posZ + z, new ItemStack(ItemList.debugStick));
        theWorld.spawnEntity(wand);
    }

    @Override
    public String name() {
        return "wand";
    }

    @Override
    public String help() {
        return Translator.getInstance().translateKey("commands.wand");
    }
}
