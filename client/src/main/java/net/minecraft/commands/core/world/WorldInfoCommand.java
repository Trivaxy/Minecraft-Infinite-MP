package net.minecraft.commands.core.world;

import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.World;
import net.minecraft.localization.Translator;

public class WorldInfoCommand extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        Player p = ca.getConfigManager().getPlayer(src.getUsername());
        if (p != null) {
            World w = ca.getWorld(p.dimension);
            if (w != null) {
                src.print(Translator.getInstance().translateKey("commands.world.name") + ": " + w.getSettings().getWorldName());
                src.print(Translator.getInstance().translateKey("commands.world.seed") + ": " + w.getSettings().getRandomSeed());
                src.printf(
                    Translator.getInstance().translateKey("commands.world.spawn"),
                    w.getSettings().getSpawnX(),
                    w.getSettings().getSpawnY(),
                    w.getSettings().getSpawnZ()
                );
                src.print(Translator.getInstance().translateKey("commands.world.height") + ": " + w.getSettings().getMaxSections() * 16);
                src.print(Translator.getInstance().translateKey("commands.world.type") + ": " + w.getSettings().getType());
                src.print(Translator.getInstance().translateKey("commands.world.dimension") + ": " + w.currDim.dimension);
                src.print(Translator.getInstance().translateKey("commands.world.difficulty") + ": " + w.getSettings().getDifficulty());
                src.print(Translator.getInstance().translateKey("commands.world.cheats") + ": " + w.getSettings().getCheats());
                src.print(Translator.getInstance().translateKey("commands.world.structures") + ": " + w.getSettings().generateStructures());
                src.print(Translator.getInstance().translateKey("commands.world.amplified") + ": " + w.getSettings().amplified());
                src.print(Translator.getInstance().translateKey("commands.world.weather") + ": " + w.getSettings().getWeather());
                src.print(Translator.getInstance().translateKey("commands.world.permaday") + ": " + w.getSettings().isPermaDay());
                src.print(
                    Translator.getInstance().translateKey("commands.world.size")
                        + ": "
                        + (
                            w.getSettings().border == 0
                                ? Translator.getInstance().translateKey("commands.world.infinite")
                                : w.getSettings().border * 128 + "x" + w.getSettings().border * 128
                        )
                );
                src.print(Translator.getInstance().translateKey("commands.world.time") + ": " + w.getSettings().getWorldTime());
                src.print(Translator.getInstance().translateKey("commands.world.day") + ": " + (int)w.getSettings().dayLength / 20 / 60);
                src.print(Translator.getInstance().translateKey("commands.world.mobdrop") + ": " + w.getSettings().mobDrops);
                src.print(Translator.getInstance().translateKey("commands.world.blockdrop") + ": " + w.getSettings().blockDrops);
                src.print(Translator.getInstance().translateKey("commands.world.spawning") + ": " + w.getSettings().spawning);
                src.print(Translator.getInstance().translateKey("commands.world.ai") + ": " + w.getSettings().isAiDisabled());
                src.print(Translator.getInstance().translateKey("commands.world.phys") + ": " + w.getSettings().isPhysicsDisabled());
            } else {
                src.print(Translator.getInstance().translateKey("commands.world.error"));
            }
        }
    }

    @Override
    public String name() {
        return "worldinfo";
    }

    @Override
    public Command.UserType requiredUserType() {
        return Command.UserType.USER;
    }

    @Override
    public Command.EnvType envType() {
        return Command.EnvType.BOTH;
    }

    @Override
    public String help() {
        return Translator.getInstance().translateKey("commands.world.info");
    }
}
