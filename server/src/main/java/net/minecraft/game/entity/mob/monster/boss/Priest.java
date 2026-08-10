package net.minecraft.game.entity.mob.monster.boss;

import net.minecraft.game.ParticleType;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.ai.hostile.ChangeCourse;
import net.minecraft.game.entity.mob.ai.hostile.RangedAttack;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.mob.monster.Pigman;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.potion.EffectData;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class Priest extends Pigman {
    private String[] names = new String[]{"Asmodei", "Unedarin", "Esim", "Thopius", "Khebin", "Kramorn", "Igron", "Zomorn", "Crexon", "Preneus"};

    public Priest(World world) {
        super(world);
        this.setHealth(250);
        this.heldItem = ItemList.healingStaff;
        this.texture = "/mob/pigman/pigman.png";
        this.moveSpeed = 0.23F;
        this.boss = true;
        this.tier = 4;
        this.scoreValue = 1000;
        this.customName = this.names[this.rand.nextInt(this.names.length)];
        this.behaviors.tasks.clear();
        this.behaviors.addTask(0, new Swim(this));
        this.behaviors.addTask(3, new RangedAttack(this, this.moveSpeed + this.moveSpeed / 2.0F, -5, 50));
        this.behaviors.addTask(4, new ChangeCourse(this, this.moveSpeed));
        this.behaviors.addTask(5, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(6, new LookAt(this, Player.class, 8.0F));
        this.behaviors.addTask(7, new LookAround(this));
    }

    @Override
    public void setType() {
    }

    @Override
    public int getSpawnID() {
        return 68;
    }

    @Override
    public void mobTick() {
        if (this.rand.nextInt(100) == 0) {
            this.addEffect(new EffectData(EffectList.heal.id, 1, 0));
        }

        super.mobTick();
    }

    @Override
    public boolean damageEntity(DamageSource src, int amount) {
        int x = this.rand.nextInt(4) + 4;
        int z = this.rand.nextInt(4) + 4;
        if (this.rand.nextBoolean()) {
            x = -this.rand.nextInt(4) - 4;
            z = -this.rand.nextInt(4) - 4;
        }

        if (this.world.getBlock(MathHelper.floor_double(this.posX) + x, MathHelper.floor_double(this.posY) - 1, MathHelper.floor_double(this.posZ) + z)
                == BlockList.cobblestone
            && this.world.getBlock(MathHelper.floor_double(this.posX) + x, MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ) + z) == null
            && this.world.getBlock(MathHelper.floor_double(this.posX) + x, MathHelper.floor_double(this.posY) + 2, MathHelper.floor_double(this.posZ) + z)
                == null) {
            this.setPosition(MathHelper.floor_double(this.posX) + x, MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ) + z);

            for (int i = 0; i < 8; i++) {
                this.world.spawnParticle(ParticleType.EXPLODE, this.posX, this.posY, this.posZ, 0.0, 0.0, 0.0);
            }
        }

        return super.damageEntity(src, amount);
    }
}
