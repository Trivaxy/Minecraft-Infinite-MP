package net.minecraft.game.entity.mob.monster.arachnid;

import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.misc.InfestedStone;
import net.minecraft.game.entity.mob.CreatureType;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.ai.hostile.Attack;
import net.minecraft.game.entity.mob.ai.hostile.DetectEnemy;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.mob.follower.Decoy;
import net.minecraft.game.entity.mob.monster.Monster;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.world.World;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class Silverfish extends Monster {
    private int summonCooldown;

    public Silverfish(World ass) {
        super(ass);
        this.setSize(0.5F, 0.4F);
        this.setHealth(8);
        this.texture = "/mob/silverfish/silverfish.png";
        this.moveSpeed = 0.25F;
        this.attackStrength = 1;
        this.scoreValue = 75;
        this.produceFootsteps = false;
        this.behaviors.addTask(0, new Swim(this));
        this.behaviors.addTask(2, new Attack(this, this.moveSpeed, false, EffectList.badLuck.id, 150, 1, false));
        this.behaviors.addTask(3, new Silverfish.SummonSilverfish(this));
        this.behaviors.addTask(4, new Silverfish.HideInBlock(this));
        this.behaviors.addTask(5, new WanderAround(this, this.moveSpeed, false));
        this.detections.addTask(1, new Aggro(this, true));
        this.detections.addTask(2, new DetectEnemy(this, Decoy.class, 16.0F, 0, true));
        this.detections.addTask(3, new DetectEnemy(this, Player.class, 16.0F, 0, true));
    }

    @Override
    public int getSpawnID() {
        return 42;
    }

    @Override
    public CreatureType getCreatureType() {
        return CreatureType.ARTHROPOD;
    }

    @Override
    public String getSound() {
        return "mob.silverfish";
    }

    @Override
    protected String getHurtSound() {
        return "mob.silverfishhurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.silverfishdeath";
    }

    @Override
    public float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public boolean damageEntity(DamageSource damageSrc, int damageAmount) {
        if (this.summonCooldown <= 0 && damageSrc instanceof DamageSource.EntitySource) {
            this.summonCooldown = 20;
        }

        return super.damageEntity(damageSrc, damageAmount);
    }

    @Override
    public final boolean isInWater() {
        return this.world.handleWaterMovement(this.boundingBox, MaterialList.water, this);
    }

    @Override
    public float getPathWeight(int x, int y, int z) {
        return this.world.getBlockId(x, y - 1, z) == BlockList.stone.id ? 10.0F : super.getPathWeight(x, y, z);
    }

    private class HideInBlock extends Action {
        private Silverfish silverfish;

        private HideInBlock(Silverfish fish) {
            this.silverfish = fish;
            this.setMutexBits(1);
        }

        @Override
        public boolean canExecute() {
            return this.silverfish.attackTarget == null && this.silverfish.navigator.noPath() && this.silverfish.rand.nextInt(10) == 0;
        }

        @Override
        public void tick() {
            int x = MathHelper.floor_double(this.silverfish.posX);
            int y = MathHelper.floor_double(this.silverfish.posY + 0.5);
            int z = MathHelper.floor_double(this.silverfish.posZ);
            int side = this.silverfish.rand.nextInt(6);
            int block = this.silverfish
                .world
                .getBlockId(x + Direction.offsetsXForSide[side], y + Direction.offsetsYForSide[side], z + Direction.offsetsZForSide[side]);
            if (InfestedStone.getPosingIdByMetadata(block)) {
                this.silverfish
                    .world
                    .setBlockAndMetadataWithNotify(
                        x + Direction.offsetsXForSide[side],
                        y + Direction.offsetsYForSide[side],
                        z + Direction.offsetsZForSide[side],
                        BlockList.silverfish.id,
                        this.silverfish
                            .world
                            .getBlockMetadata(x + Direction.offsetsXForSide[side], y + Direction.offsetsYForSide[side], z + Direction.offsetsZForSide[side])
                    );
                this.silverfish.spawnPuffParticles();
                this.silverfish.setDead();
            }
        }
    }

    private class SummonSilverfish extends Action {
        private Silverfish silverfish;

        private SummonSilverfish(Silverfish silverfish) {
            this.silverfish = silverfish;
        }

        @Override
        public boolean canExecute() {
            return this.silverfish.summonCooldown > 0;
        }

        @Override
        public void tick() {
            --this.silverfish.summonCooldown;
            if (this.silverfish.summonCooldown == 0) {
                int x = MathHelper.floor_double(this.silverfish.posX);
                int y = MathHelper.floor_double(this.silverfish.posY);
                int z = MathHelper.floor_double(this.silverfish.posZ);
                boolean summon = false;

                for (int yOff = 0; !summon && yOff <= 5 && yOff >= -5; yOff = yOff <= 0 ? 1 - yOff : 0 - yOff) {
                    for (int xOff = 0; !summon && xOff <= 10 && xOff >= -10; xOff = xOff <= 0 ? 1 - xOff : 0 - xOff) {
                        for (int zOff = 0; !summon && zOff <= 10 && zOff >= -10; zOff = zOff <= 0 ? 1 - zOff : 0 - zOff) {
                            int block = this.silverfish.world.getBlockId(x + xOff, y + yOff, z + zOff);
                            if (block == BlockList.silverfish.id) {
                                this.silverfish
                                    .world
                                    .playEffect(
                                        2001,
                                        x + xOff,
                                        y + yOff,
                                        z + zOff,
                                        BlockList.silverfish.id + this.silverfish.world.getBlockMetadata(x + xOff, y + yOff, z + zOff) * 4096
                                    );
                                this.silverfish.world.setBlockWithNotify(x + xOff, y + yOff, z + zOff, 0);
                                BlockList.silverfish.onBroken(this.silverfish.world, x + xOff, y + yOff, z + zOff, 0);
                                if (this.silverfish.rand.nextBoolean()) {
                                    summon = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
