package net.minecraft.game.entity.mob.monster.boss;

import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.monster.skeleton.Lich;
import net.minecraft.game.entity.mob.monster.skeleton.Stray;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.potion.EffectData;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.world.World;

public class LichKing extends Lich {
    private String[] names = new String[]{
        "Negaqudh", "Rhikzaqec", "Vhaur'zez", "Halkiger", "Drumkog", "Nom'ghokec", "Dhoonqir", "Stoul'zeneq", "Dar'qan", "Szeagodh"
    };

    public LichKing(World world) {
        super(world);
        this.setHealth(250);
        this.heldItem = ItemList.lichStaff;
        this.scoreValue = 1000;
        this.boss = true;
        this.tier = 2;
        this.customName = this.names[this.rand.nextInt(this.names.length)];
    }

    @Override
    public int getSpawnID() {
        return 50;
    }

    @Override
    public boolean damageEntity(DamageSource src, int amount) {
        if (this.passiveTimer > 0) {
            return super.damageEntity(src, amount);
        }

        Mob human1 = this.rand.nextBoolean() ? new Stray(this.world) : new Lich(this.world);
        Mob human2 = this.rand.nextBoolean() ? new Stray(this.world) : new Lich(this.world);
        human1.setPosAndRot(this.posX + 1.0, this.posY, this.posZ, this.pitch, this.yaw);
        human2.setPosAndRot(this.posX - 1.0, this.posY, this.posZ, this.pitch, this.yaw);
        human1.skinUrl = human2.skinUrl = this.skinUrl;
        this.world.spawnEntity(human1);
        this.world.spawnEntity(human2);
        this.world.playSoundEffect(this.posX, this.posY, this.posZ, "random.breath", 1.0F, 1.0F / (this.world.rand.nextFloat() * 0.4F + 0.8F));

        for (int i = 0; i < 20; i++) {
            this.world
                .spawnParticle(
                    ParticleType.EXPLODE,
                    human1.posX + this.world.rand.nextFloat() * human1.width * 2.0F - human1.width,
                    human1.posY + this.world.rand.nextFloat() * human1.height,
                    human1.posZ + this.world.rand.nextFloat() * human1.width * 2.0F - human1.width,
                    this.world.rand.nextGaussian() * 0.02,
                    this.world.rand.nextGaussian() * 0.02,
                    this.world.rand.nextGaussian() * 0.02
                );
            this.world
                .spawnParticle(
                    ParticleType.EXPLODE,
                    human2.posX + this.world.rand.nextFloat() * human2.width * 2.0F - human2.width,
                    human2.posY + this.world.rand.nextFloat() * human2.height,
                    human2.posZ + this.world.rand.nextFloat() * human2.width * 2.0F - human2.width,
                    this.world.rand.nextGaussian() * 0.02,
                    this.world.rand.nextGaussian() * 0.02,
                    this.world.rand.nextGaussian() * 0.02
                );
        }

        this.addEffect(new EffectData(EffectList.invisibility.id, 200, 0));
        this.passiveTimer = 200;
        this.attackTarget = this.lastTarget = null;
        return true;
    }
}
