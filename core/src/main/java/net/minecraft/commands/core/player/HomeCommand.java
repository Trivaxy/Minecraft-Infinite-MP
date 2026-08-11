package net.minecraft.commands.core.player;

import java.util.Arrays;
import java.util.List;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.World;
import net.minecraft.game.world.util.Position;
import net.minecraft.localization.Translator;

public class HomeCommand extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        Player player = ca.demandPlayer(src);
        World w = ca.getWorld(0);
        int x = w.getSettings().getSpawnX();
        int z = w.getSettings().getSpawnZ();
        int y = w.getSettings().getSpawnY();
        if (args.length == 1) {
            player = ca.getPlayer(args[0]);
        }

        if (player != null) {
            Position playerSpawn = player.getSpawn(player.dimension);
            if (playerSpawn != null) {
                x = playerSpawn.x;
                z = playerSpawn.z;
                y = playerSpawn.y;
            }

            player.setPosition(x + 0.5, y + 2.62, z + 0.5);
            sendNoticeToOps(ca, src, Translator.getInstance().translateKey("commands.home.return"));
        } else {
            src.print(Translator.getInstance().translateKey("commands.inv.noplayer"));
        }
    }

    @Override
    public String name() {
        return "home";
    }

    @Override
    public Command.UserType requiredUserType() {
        return Command.UserType.OPERATOR;
    }

    @Override
    public Command.EnvType envType() {
        return Command.EnvType.BOTH;
    }

    @Override
    public String help() {
        return Translator.getInstance().translateKey("commands.home");
    }

    @Override
    public List<String> additionalHelp() {
        return Arrays.asList("<player> " + Translator.getInstance().translateKey("commands.home.extra"));
    }
}
