package net.minecraft.server.commands;

import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;

public class UsedMemory extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        Runtime r = Runtime.getRuntime();
        if (args.length == 1 && args[0].equals("gc")) {
            r.gc();
            src.print("Garbage collected.");
        } else {
            long free = r.freeMemory();
            long max = r.maxMemory();
            long tot = r.totalMemory();
            long used = tot - free;
            src.printf("Free mem: %d MBytes", free / 1024L / 1024L);
            src.printf("Max mem: %d MBytes", max / 1024L / 1024L);
            src.printf("Total mem: %d MBytes", tot / 1024L / 1024L);
            src.printf("Used mem: %d MBbytes", used / 1024L / 1024L);
        }
    }

    @Override
    public String name() {
        return "mem";
    }

    @Override
    public Command.UserType requiredUserType() {
        return Command.UserType.OPERATOR;
    }

    @Override
    public Command.EnvType envType() {
        return Command.EnvType.SERVER;
    }

    @Override
    public String help() {
        return "shows server memory usage";
    }
}
