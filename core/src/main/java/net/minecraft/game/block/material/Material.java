package net.minecraft.game.block.material;

import net.minecraft.game.world.mapdata.MapColor;

public class Material {
    public MapColor mapColor;
    private int mobilityFlag;
    private boolean burning;
    private boolean groundCover = false;

    public Material(MapColor col) {
        this.mapColor = col;
    }

    public boolean getIsLiquid() {
        return false;
    }

    public boolean isSolid() {
        return true;
    }

    public int getMaterialMobility() {
        return this.mobilityFlag;
    }

    protected Material setNoPushMobility() {
        this.mobilityFlag = 1;
        return this;
    }

    protected Material setImmovableMobility() {
        this.mobilityFlag = 2;
        return this;
    }

    public boolean getBurning() {
        return this.burning;
    }

    protected Material setBurning() {
        this.burning = true;
        return this;
    }

    public Material setGroundCover() {
        this.groundCover = true;
        return this;
    }

    public boolean isGroundCover() {
        return this.groundCover;
    }
}
