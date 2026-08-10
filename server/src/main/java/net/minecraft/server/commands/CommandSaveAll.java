package net.minecraft.server.commands;

import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.world.World;
import net.minecraft.util.ProgressBar;

public class CommandSaveAll extends Command implements ProgressBar {
    private CommandSource src;

    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        this.src = src;
        sendNoticeToOps(ca, src, "Forcing save..");
        ca.getConfigManager().savePlayerStates();

        World last = null;

        for (int i = 0; i < ca.getWorldCount(); i++) {
            World ii = ca.getWorld(i);
            ii.save(true, this);
            last = ii;
        }

        // postSaveCleanup() drains the shared IO thread and clears the global RegionFileCache,
        // so it belongs after every world has been written, not once per world.
        if (last != null) {
            last.postSaveCleanup();
        }

        sendNoticeToOps(ca, src, "Save complete.");
    }

    @Override
    public String name() {
        return "save-all";
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
        return "forces a server-wide level save";
    }

    @Override
    public void displayProgressMessage(String string1) {
        this.src.print(string1);
    }

    @Override
    public void displayLoadingString(String string1) {
        this.src.print(string1);
    }

    @Override
    public void setLoadingProgress(int i1) {
        this.src.print(i1 + "%");
    }
}
