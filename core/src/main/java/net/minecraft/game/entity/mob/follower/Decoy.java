package net.minecraft.game.entity.mob.follower;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.Panic;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.World;

public class Decoy extends Mob {
    public Decoy(World world) {
        super(world);
        this.moveSpeed = 0.23F;
        this.behaviors.addTask(0, new Swim(this));
        this.behaviors.addTask(1, new Panic(this, this.moveSpeed * 2.0F));
        this.behaviors.addTask(5, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(6, new LookAt(this, Player.class, 6.0F));
        this.behaviors.addTask(7, new LookAround(this));
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        if (this.skinUrl != null && !this.skinUrl.isEmpty()) {
            tag.setString("CustomSkin", this.skinUrl);
        }
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("CustomSkin")) {
            this.skinUrl = tag.getString("CustomSkin");
        }
    }
}
