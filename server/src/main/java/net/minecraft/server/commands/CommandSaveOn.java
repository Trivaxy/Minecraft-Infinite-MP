package net.minecraft.server.commands;

import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;

public class CommandSaveOn extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        sendNoticeToOps(ca, src, "Enabling level saving..");

        // See CommandSaveOff: setSaving(false) is the one that *allows* saving.
        for (int i = 0; i < ca.getWorldCount(); i++) {
            ca.getWorld(i).setSaving(false);
        }
    }

    @Override
    public String name() {
        return "save-on";
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
        return "re-enables level saving on this server";
    }
}
