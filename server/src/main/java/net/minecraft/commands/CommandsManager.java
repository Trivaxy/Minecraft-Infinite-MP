package net.minecraft.commands;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.commands.core.AliasCommand;
import net.minecraft.commands.core.general.CountCommand;
import net.minecraft.commands.core.general.GameRuleCommand;
import net.minecraft.commands.core.general.HelpCommand;
import net.minecraft.commands.core.general.KillCommand;
import net.minecraft.commands.core.general.ParticleCommand;
import net.minecraft.commands.core.general.SoundCommand;
import net.minecraft.commands.core.general.SummonCommand;
import net.minecraft.commands.core.player.AchievementCommand;
import net.minecraft.commands.core.player.ClearInvCommand;
import net.minecraft.commands.core.player.EffectCommand;
import net.minecraft.commands.core.player.EnchantCommand;
import net.minecraft.commands.core.player.GiveCommand;
import net.minecraft.commands.core.player.HomeCommand;
import net.minecraft.commands.core.player.MeCommand;
import net.minecraft.commands.core.player.ReplaceItemCommand;
import net.minecraft.commands.core.player.ResetSleepCommand;
import net.minecraft.commands.core.player.ScoreCommand;
import net.minecraft.commands.core.player.SetSpawnCommand;
import net.minecraft.commands.core.player.TeleportCommand;
import net.minecraft.commands.core.world.ChunkInfoCommand;
import net.minecraft.commands.core.world.DifficultyCommand;
import net.minecraft.commands.core.world.DimensionCommand;
import net.minecraft.commands.core.world.EventCommand;
import net.minecraft.commands.core.world.ExplodeCommand;
import net.minecraft.commands.core.world.LocateCommand;
import net.minecraft.commands.core.world.SeedCommand;
import net.minecraft.commands.core.world.SetBlockCommand;
import net.minecraft.commands.core.world.TimeCommand;
import net.minecraft.commands.core.world.WeatherCommand;
import net.minecraft.commands.core.world.WorldInfoCommand;
import net.minecraft.commands.core.world.WorldSpawnCommand;
import net.minecraft.commands.core.world.edit.ClearCommand;
import net.minecraft.commands.core.world.edit.CopyCommand;
import net.minecraft.commands.core.world.edit.CutCommand;
import net.minecraft.commands.core.world.edit.ExportCommand;
import net.minecraft.commands.core.world.edit.FillCommand;
import net.minecraft.commands.core.world.edit.PasteCommand;
import net.minecraft.commands.core.world.edit.PointCommand;
import net.minecraft.commands.core.world.edit.ReplaceCommand;
import net.minecraft.commands.core.world.edit.RotateCommand;
import net.minecraft.commands.core.world.edit.UndoCommand;
import net.minecraft.commands.core.world.edit.WandCommand;
import net.minecraft.localization.Translator;

public abstract class CommandsManager {
    public static Logger logger = Logger.getLogger("Minecraft");
    public static final int MAX_COMMANDS = 256;
    private static int cmdNext = 0;
    public static final Command[] COMMANDS = new Command[256];

    protected CommandsManager() {
        registerCommand(new HelpCommand());
        registerCommand(new GameRuleCommand());
        registerCommand(new SummonCommand());
        registerCommand(new ParticleCommand());
        registerCommand(new SoundCommand());
        registerCommand(new KillCommand());
        registerCommand(new CountCommand());
        registerCommand(new GiveCommand());
        registerCommand(new TeleportCommand());
        registerCommand(new EffectCommand());
        registerCommand(new EnchantCommand());
        registerCommand(new ScoreCommand());
        registerCommand(new AchievementCommand());
        registerCommand(new ReplaceItemCommand());
        registerCommand(new ClearInvCommand());
        registerCommand(new SetSpawnCommand());
        registerCommand(new ResetSleepCommand());
        registerCommand(new HomeCommand());
        registerCommand(new MeCommand());
        registerCommand(new TimeCommand());
        registerCommand(new WeatherCommand());
        registerCommand(new EventCommand());
        registerCommand(new DimensionCommand());
        registerCommand(new DifficultyCommand());
        registerCommand(new WorldSpawnCommand());
        registerCommand(new ExplodeCommand());
        registerCommand(new SeedCommand());
        registerCommand(new LocateCommand());
        registerCommand(new ChunkInfoCommand());
        registerCommand(new WorldInfoCommand());
        registerCommand(new SetBlockCommand());
        registerCommand(new ClearCommand());
        registerCommand(new FillCommand());
        registerCommand(new CopyCommand());
        registerCommand(new PointCommand());
        registerCommand(new CutCommand());
        registerCommand(new ExportCommand());
        registerCommand(new PasteCommand());
        registerCommand(new UndoCommand());
        registerCommand(new ReplaceCommand());
        registerCommand(new RotateCommand());
        registerCommand(new WandCommand());
    }

    protected static void registerCommand(Command c) {
        if (cmdNext == 256) {
            throw new Error("Maximum command count reached, increase the array capacity!");
        }

        List<String> aliases = c.getAliases();
        if (aliases != null) {
            for (String alias : aliases) {
                registerCommand(new AliasCommand(c, alias));
            }
        }

        if (COMMANDS[cmdNext] != null) {
            throw new Error("Command slot already occupied");
        }

        c.init();
        COMMANDS[cmdNext] = c;
        cmdNext++;
    }

    public final void runCommand(CommandHolder holder, CommandAccessor ca) {
        String[] argv = holder.command.split(" ");
        CommandSource src = holder.source;
        Command.UserType user = src.getType();

        for (Command cmd : COMMANDS) {
            if (cmd != null && cmd.name().equals(argv[0])) {
                if (src.canRunCommand(cmd)) {
                    switch (cmd.envType()) {
                        case BOTH:
                        default:
                            break;
                        case SERVER:
                            if (!ca.isServer()) {
                                src.print(Translator.getInstance().translateKey("commands.base.unknown"));
                                return;
                            }
                            break;
                        case CLIENT:
                            if (ca.isServer()) {
                                src.print(Translator.getInstance().translateKey("commands.base.unknown"));
                                return;
                            }
                    }

                    try {
                        String[] dest = new String[argv.length - 1];
                        System.arraycopy(argv, 1, dest, 0, argv.length - 1);
                        cmd.action(dest, ca, src);
                        if (ca.isServer() && user != Command.UserType.ADMINISTRATOR) {
                            logger.info(src.getUsername() + " issued server command " + cmd.name());
                        }

                        return;
                    } catch (Exception e) {
                        if (e instanceof CommandException) {
                            cmd.errorAction(e.getMessage(), src);
                        } else {
                            logger.log(Level.WARNING, "Uncaught exception in command " + cmd, e);
                        }

                        return;
                    }
                }

                src.print(Translator.getInstance().translateKey("commands.base.noperms"));
                if (ca.isServer() && user != Command.UserType.ADMINISTRATOR) {
                    logger.info(src.getUsername() + " tried command " + cmd.name());
                }

                return;
            }
        }

        src.print(Translator.getInstance().translateKey("commands.base.unknown"));
    }
}
