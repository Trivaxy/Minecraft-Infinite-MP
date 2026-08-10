package net.minecraft.game.block.material;

import net.minecraft.game.world.mapdata.MapColor;

public class LiquidMaterial extends Material {
    public LiquidMaterial(MapColor col) {
        super(col);
        this.setNoPushMobility();
    }

    @Override
    public boolean getIsLiquid() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }
}
