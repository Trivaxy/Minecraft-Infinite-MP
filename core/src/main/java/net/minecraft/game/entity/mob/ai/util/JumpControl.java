package net.minecraft.game.entity.mob.ai.util;

import net.minecraft.game.entity.mob.Mob;

public class JumpControl {
    private Mob entity;
    private boolean jumping = false;

    public JumpControl(Mob mob) {
        this.entity = mob;
    }

    public void setJumping() {
        this.jumping = true;
    }

    public void jump() {
        this.entity.jumping = this.jumping;
        this.jumping = false;
    }
}
