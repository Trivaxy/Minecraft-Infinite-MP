package net.minecraft.server.commands;

import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;

public class CommandSaveOff extends Command {
    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        sendNoticeToOps(ca, src, "Disabling level saving..");

        // setSaving(true) is what *suspends* saving: WorldServer stores it in levelSaving and
        // ChunkProviderServer.canSave() returns !levelSaving. save-off and save-on had the
        // booleans the wrong way round, so save-off enabled saving and save-on silently
        // disabled it — and every later save-all did nothing.
        for (int i = 0; i < ca.getWorldCount(); i++) {
            ca.getWorld(i).setSaving(true);
        }
    }

    @Override
    public String name() {
        return "save-off";
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
        return "disables level saving on this server";
    }
}
