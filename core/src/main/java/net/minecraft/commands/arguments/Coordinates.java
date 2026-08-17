package net.minecraft.commands.arguments;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.world.chunk.BlockPosition;

public interface Coordinates {
    Vector getPosition(CommandSourceStack source);

    BlockPosition getBlockPos(CommandSourceStack source);
}
