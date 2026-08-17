package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.util.math.MathHelper;

public final class WorldCoordinate {
    private final boolean relative;
    private final double value;

    private WorldCoordinate(boolean relative, double value) {
        this.relative = relative;
        this.value = value;
    }

    public static WorldCoordinate parse(StringReader reader) throws CommandSyntaxException {
        if (reader.canRead() && reader.peek() == '~') {
            reader.skip();
            double offset = reader.canRead() && reader.peek() != ' ' ? reader.readDouble() : 0.0;
            return new WorldCoordinate(true, offset);
        }

        return new WorldCoordinate(false, reader.readDouble());
    }

    public boolean isRelative() {
        return this.relative;
    }

    public double get(double origin) {
        return this.relative ? origin + this.value : this.value;
    }

    public int getBlock(double origin) {
        return MathHelper.floor_double(this.get(origin));
    }
}
