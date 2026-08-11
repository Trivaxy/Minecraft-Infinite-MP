package net.minecraft.commands.core.general;

import java.util.Arrays;
import java.util.List;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.localization.Translator;

public class SoundCommand extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        demandArgCountOf(1, args);
        Player player = ca.demandPlayer(src);
        switch (args.length) {
            case 1:
                ca.getWorld(0).playSoundEffect(player.posX, player.posY, player.posZ, args[0], 1.0F, 1.0F);
                src.printf(Translator.getInstance().translateKey("commands.sound.play"), args[0], player.posX, player.posY, player.posZ, 1.0F, 1.0F);
            case 2:
            case 5:
            default:
                break;
            case 3: {
                float vol = parseFloat(args[1]);
                float pitch = parseFloat(args[2]);
                ca.getWorld(0).playSoundEffect(player.posX, player.posY, player.posZ, args[0], vol, pitch);
                src.printf(Translator.getInstance().translateKey("commands.sound.play"), args[0], player.posX, player.posY, player.posZ, vol, pitch);
                break;
            }
            case 4: {
                double x = parseRelativeCoords(args[1], player.posX);
                double y = parseRelativeCoords(args[2], player.posY);
                double z = parseRelativeCoords(args[3], player.posZ);
                float vol = 1.0F;
                float pitch = 1.0F;
                ca.getWorld(0).playSoundEffect(x, y, z, args[0], vol, pitch);
                src.printf(Translator.getInstance().translateKey("commands.sound.play"), args[0], x, y, z, vol, pitch);
                break;
            }
            case 6: {
                double x = parseRelativeCoords(args[1], player.posX);
                double y = parseRelativeCoords(args[2], player.posY);
                double z = parseRelativeCoords(args[3], player.posZ);
                float vol = parseFloat(args[3]);
                float pitch = parseFloat(args[4]);
                ca.getWorld(0).playSoundEffect(x, y, z, args[0], vol, pitch);
                src.printf(Translator.getInstance().translateKey("commands.sound.play"), args[0], x, y, z, vol, pitch);
            }
        }
    }

    @Override
    public String name() {
        return "sound";
    }

    @Override
    public Command.UserType requiredUserType() {
        return Command.UserType.USER;
    }

    @Override
    public Command.EnvType envType() {
        return Command.EnvType.CLIENT;
    }

    @Override
    public String help() {
        return "<name> " + Translator.getInstance().translateKey("commands.sound");
    }

    @Override
    public List<String> additionalHelp() {
        return Arrays.asList(
            "<volume> <pitch> " + Translator.getInstance().translateKey("commands.sound.full"),
            "<x> <y> <z> " + Translator.getInstance().translateKey("commands.sound.loc"),
            "<x> <y> <z> <volume> <pitch> " + Translator.getInstance().translateKey("commands.give.loc")
        );
    }
}
