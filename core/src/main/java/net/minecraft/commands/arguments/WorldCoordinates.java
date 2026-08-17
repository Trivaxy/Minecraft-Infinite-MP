package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandMessages;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.world.chunk.BlockPosition;

public final class WorldCoordinates implements Coordinates {
    private final WorldCoordinate x;
    private final WorldCoordinate y;
    private final WorldCoordinate z;

    public WorldCoordinates(WorldCoordinate x, WorldCoordinate y, WorldCoordinate z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static WorldCoordinates parse(StringReader reader) throws CommandSyntaxException {
        int start = reader.getCursor();
        WorldCoordinate x = WorldCoordinate.parse(reader);
        expectSeparator(reader, start);
        WorldCoordinate y = WorldCoordinate.parse(reader);
        expectSeparator(reader, start);
        WorldCoordinate z = WorldCoordinate.parse(reader);
        return new WorldCoordinates(x, y, z);
    }

    private static void expectSeparator(StringReader reader, int start) throws CommandSyntaxException {
        if (!reader.canRead() || reader.peek() != ' ') {
            reader.setCursor(start);
            throw CommandMessages.INCOMPLETE_POSITION.createWithContext(reader);
        }

        reader.skip();
    }

    @Override
    public Vector getPosition(CommandSourceStack source) {
        Vector origin = source.getPosition();
        return Vector.createVector(this.x.get(origin.xCoord), this.y.get(origin.yCoord), this.z.get(origin.zCoord));
    }

    @Override
    public BlockPosition getBlockPos(CommandSourceStack source) {
        Vector origin = source.getPosition();
        return new BlockPosition(this.x.getBlock(origin.xCoord), this.y.getBlock(origin.yCoord), this.z.getBlock(origin.zCoord));
    }
}
