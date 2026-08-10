package net.minecraft.game.physics;

import net.minecraft.game.entity.Entity;

public class TargetingSystem {
    public TargetingSystem.HitType target;
    public Vector hitVec;
    public Entity entityHit;
    public int blockX;
    public int blockY;
    public int blockZ;
    public int sideHit;

    public TargetingSystem(int x, int y, int z, int side, Vector vector) {
        this.target = TargetingSystem.HitType.TILE;
        this.blockX = x;
        this.blockY = y;
        this.blockZ = z;
        this.sideHit = side;
        this.hitVec = Vector.createVector(vector.xCoord, vector.yCoord, vector.zCoord);
    }

    public TargetingSystem(Entity entity) {
        this.target = TargetingSystem.HitType.ENTITY;
        this.entityHit = entity;
        this.hitVec = Vector.createVector(entity.posX, entity.posY, entity.posZ);
    }

    public enum HitType {
        TILE,
        ENTITY;
    }
}
