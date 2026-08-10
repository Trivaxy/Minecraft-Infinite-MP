package net.minecraft.game.block.material;

import net.minecraft.game.world.mapdata.MapColor;

public class TransparentMaterial extends Material {
    public TransparentMaterial(MapColor color) {
        super(color);
    }

    @Override
    public boolean isSolid() {
        return false;
    }
}
