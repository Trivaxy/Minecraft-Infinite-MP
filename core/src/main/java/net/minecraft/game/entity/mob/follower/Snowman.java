package net.minecraft.game.entity.mob.follower;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.ai.hostile.DetectEnemy;
import net.minecraft.game.entity.mob.ai.hostile.RangedAttack;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.mob.monster.Monster;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.dimension.HellDimension;
import net.minecraft.util.math.MathHelper;

public class Snowman extends Mob {
    public Snowman(World world) {
        super(world);
        this.setSize(0.7F, 1.8F);
        this.setHealth(10);
        this.texture = "/mob/snowman.png";
        this.produceFootsteps = false;
        this.moveSpeed = 0.25F;
        this.behaviors.addTask(1, new Swim(this));
        this.behaviors.addTask(2, new RangedAttack(this, this.moveSpeed, -1, 10));
        this.behaviors.addTask(3, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(4, new LookAt(this, Player.class, 8.0F));
        this.behaviors.addTask(5, new LookAround(this));
        this.detections.addTask(1, new Aggro(this, false));
        this.detections.addTask(2, new DetectEnemy(this, Monster.class, 16.0F, 0, true));
    }

    @Override
    protected void fall(float dist) {
    }

    @Override
    public boolean canDespawn() {
        return false;
    }

    @Override
    public void mobTick() {
        super.mobTick();
        int xfloor = MathHelper.floor_double(this.posX);
        int yfloor = MathHelper.floor_double(this.posY);
        int zfloor = MathHelper.floor_double(this.posZ);
        if (this.world.getBiomeProvider().getBiomeAt(xfloor, zfloor).getTemperature() > 1 || this.world.currDim instanceof HellDimension) {
            this.damageEntity(DamageSource.onFire, 1);
        }

        for (int var5 = 0; var5 < 4; var5++) {
            yfloor = MathHelper.floor_double(this.posX + (var5 % 2 * 2 - 1) * 0.25F);
            zfloor = MathHelper.floor_double(this.posY);
            int zfloorf = MathHelper.floor_double(this.posZ + (var5 / 2 % 2 * 2 - 1) * 0.25F);
            if (this.world.getBlockId(yfloor, zfloor, zfloorf) == 0
                && this.world.getBlockId(yfloor, zfloor - 1, zfloorf) != BlockList.snowLayer.id
                && BlockList.snowLayer.placeableAt(this.world, yfloor, zfloor, zfloorf)) {
                this.world.setBlockWithNotify(yfloor, zfloor, zfloorf, BlockList.snowLayer.id);
            }
        }
    }

    @Override
    public int getDrop() {
        return ItemList.snowball.id;
    }

    @Override
    public int getDropCount() {
        return this.rand.nextInt(16);
    }

    @Override
    protected String getHurtSound() {
        return null;
    }

    @Override
    protected String getDeathSound() {
        return null;
    }
}
