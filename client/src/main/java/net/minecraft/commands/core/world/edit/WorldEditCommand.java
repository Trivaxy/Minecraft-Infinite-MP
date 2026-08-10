package net.minecraft.commands.core.world.edit;

import net.minecraft.commands.Command;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.world.chunk.BlockPosition;
import net.minecraft.game.world.edit.WorldEdit;
import net.minecraft.localization.Translator;

public abstract class WorldEditCommand extends Command {
    public BlockPosition pointingAt;

    public boolean checkCorners(CommandSource src) {
        if (!WorldEdit.checkCorners()) {
            src.print(Translator.getInstance().translateKey("commands.worldedit.error"));
            return false;
        } else {
            return true;
        }
    }

    @Override
    public Command.UserType requiredUserType() {
        return Command.UserType.OPERATOR;
    }

    @Override
    public Command.EnvType envType() {
        return Command.EnvType.BOTH;
    }
}
