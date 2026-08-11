package net.minecraft.commands.core.general;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandsManager;
import net.minecraft.localization.Translator;

public final class HelpCommand extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        List<Command> cmdList = new ArrayList<>();

        for (Command cmd : CommandsManager.COMMANDS) {
            if (cmd != null) {
                switch (cmd.envType()) {
                    case BOTH:
                    default:
                        break;
                    case SERVER:
                        if (!ca.isServer()) {
                            continue;
                        }
                        break;
                    case CLIENT:
                        if (ca.isServer()) {
                            continue;
                        }
                }

                if (src.canRunCommand(cmd)) {
                    if (args.length == 1) {
                        List<String> addHelp = cmd.additionalHelp();
                        if (args[0].equals(cmd.name()) && src.canRunCommand(cmd)) {
                            src.print("§e" + cmd.name() + "§f" + ": " + cmd.help());
                            if (addHelp != null) {
                                for (String string : addHelp) {
                                    src.print("§e" + cmd.name() + "§f" + ": " + string);
                                }
                            }
                            break;
                        }
                    } else if (cmd.help() != null && !cmdList.contains(cmd)) {
                        src.print("§e" + cmd.name() + "§f" + ": " + cmd.help());
                        cmdList.add(cmd);
                    }
                }
            }
        }

        src.print("§2" + Translator.getInstance().translateKey("commands.help.hint"));
    }

    @Override
    public String name() {
        return "help";
    }

    @Override
    public String help() {
        return Translator.getInstance().translateKey("commands.help");
    }

    @Override
    public List<String> additionalHelp() {
        return Arrays.asList("<command> " + Translator.getInstance().translateKey("commands.help.specific"));
    }

    @Override
    public List<String[]> suggestions() {
        List<String[]> list = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        for (Command cmd : CommandsManager.COMMANDS) {
            if (cmd != null) {
                commands.add(cmd.name());
            }
        }

        String[] cmdNames = commands.toArray(new String[0]);
        list.add(cmdNames);
        return list;
    }

    @Override
    public Command.UserType requiredUserType() {
        return Command.UserType.USER;
    }

    @Override
    public Command.EnvType envType() {
        return Command.EnvType.BOTH;
    }
}
