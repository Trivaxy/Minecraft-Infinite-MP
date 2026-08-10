package net.minecraft.client.commands.client;

import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.entity.misc.PrimedTNT;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.World;
import net.minecraft.localization.Translator;

public class ImagineCommand extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        Player player = ca.getConfigManager().getPlayer(src.getUsername());
        if (player != null) {
            World level = ca.getWorld(player.dimension);
            PrimedTNT tnt = new PrimedTNT(level);
            tnt.fuse = 30;
            tnt.setPosition(player.posX, player.posY + 5.0, player.posZ);
            level.spawnEntity(tnt);
            src.print("§5Imagine §f" + Translator.getInstance().translateKey("commands.imagine"));
        } else {
            src.print(Translator.getInstance().translateKey("commands.inv.noplayer"));
        }
    }

    @Override
    public String name() {
        return "imagine";
    }

    @Override
    public Command.UserType requiredUserType() {
        return Command.UserType.OPERATOR;
    }

    @Override
    public Command.EnvType envType() {
        return Command.EnvType.CLIENT;
    }

    @Override
    public String help() {
        return null;
    }
}
