package net.minecraft.game.entity.mob.monster.boss;

import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.hostile.RangedAttack;
import net.minecraft.game.entity.mob.monster.Harpy;
import net.minecraft.game.world.World;

public class HarpyQueen extends Harpy {
    private String[] names = new String[]{"Maeraphe", "Faenyana", "Mesynea", "Verea", "Airophis", "Ollynea", "Nyphialle", "Glorara", "Ierethe", "Zeanophis"};

    public HarpyQueen(World world) {
        super(world);
        this.setSize(1.2F, 2.5F);
        this.setHealth(200);
        this.texture = "/mob/harpy/harpy_queen.png";
        this.boss = true;
        this.scoreValue = 1000;
        this.tier = 4;
        this.customName = this.names[this.rand.nextInt(this.names.length)];
        this.behaviors.addTask(3, new HarpyQueen.ShootProjectile(this, 0.0F, -5, 50));
    }

    @Override
    public int getSpawnID() {
        return 67;
    }

    private class ShootProjectile extends RangedAttack {
        public ShootProjectile(Mob src, float friction, int arrow, int timer) {
            super(src, friction, arrow, timer);
        }

        @Override
        public boolean canExecute() {
            Mob target = this.source.attackTarget;
            if (target != null && !(this.source.getDistanceSqTo(target) > 128.0)) {
                this.target = target;
                return true;
            } else {
                return false;
            }
        }
    }
}
