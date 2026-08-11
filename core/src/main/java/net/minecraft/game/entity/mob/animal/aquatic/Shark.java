package net.minecraft.game.entity.mob.animal.aquatic;

import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.flying.FlyToTarget;
import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.ai.hostile.DetectEnemy;
import net.minecraft.game.entity.mob.animal.Seal;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;

public class Shark extends AquaticMob {
    public Shark(World world) {
        super(world);
        this.setHealth(20);
        this.setSize(1.7F, 1.3F);
        this.scoreValue = 130;
        this.attackStrength = 8;
        this.type = (byte)this.rand.nextInt(10);
        this.setType();
        this.behaviors.addTask(4, new Shark.FindDamagedTarget(this, 0.1F));
        this.detections.addTask(1, new Aggro(this, false));
        this.detections.addTask(2, new DetectEnemy(this, Player.class, 32.0F, 0, true));
        this.detections.addTask(3, new DetectEnemy(this, Seal.class, 32.0F, 0, true));
    }

    @Override
    public void setType() {
        if (this.type == 0) {
            this.texture = "/mob/shark/shark_white.png";
        } else {
            this.texture = "/mob/shark/shark.png";
        }
    }

    @Override
    public int getSpawnID() {
        return 18;
    }

    @Override
    public int getDrop() {
        return this.fire > 0 ? ItemList.fishCooked.id : ItemList.fishRaw.id;
    }

    private class FindDamagedTarget extends FlyToTarget {
        public FindDamagedTarget(Mob parent, float dist) {
            super(parent, dist);
        }

        @Override
        public boolean canExecute() {
            return this.mob.world.settings.getDifficulty() != 0
                && this.mob.attackTarget != null
                && this.mob.attackTarget.health < this.mob.attackTarget.maxHealth / 2
                && this.mob.attackTarget.isInWater()
                && this.mob.attackTarget.mount == null;
        }

        @Override
        public boolean continueExecuting() {
            return this.mob.world.settings.getDifficulty() > 0 && this.mob.attackTarget != null && this.mob.attackTarget.isInWater() && this.mob.mount == null;
        }
    }
}
