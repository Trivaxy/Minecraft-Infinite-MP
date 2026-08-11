package net.minecraft.game.entity.mob.ai.hostile;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.player.Player;

public class DetectEnemy extends ChooseTarget {
    protected Mob target;
    protected Class<?> targetClass;
    int chance;
    private DetectEnemy.EnemiesSorter targetSorter;

    public DetectEnemy(Mob target, Class<?> targetClass, float dist, int chance, boolean chase) {
        this(target, targetClass, dist, chance, chase, false);
    }

    public DetectEnemy(Mob target, Class<?> class2, float dist, int chance, boolean chase, boolean search) {
        super(target, dist, chase, search);
        this.targetClass = class2;
        this.dist = dist;
        this.chance = chance;
        this.targetSorter = new DetectEnemy.EnemiesSorter(this, target);
        this.setMutexBits(1);
    }

    @Override
    public boolean canExecute() {
        if ((this.chance <= 0 || this.src.rand.nextInt(this.chance) == 0) && this.src.calmTime <= 0) {
            if (this.targetClass == Player.class) {
                Player player = this.src.world.getClosestPlayer(this.src, this.dist);
                if (this.foundValidTarget(player, false)) {
                    this.target = player;
                    return true;
                }
            } else {
                List<Entity> entities = this.src.world.getEntitiesOfType(this.targetClass, this.src.boundingBox.expand(this.dist, 6.0, this.dist));
                Collections.sort(entities, this.targetSorter);

                for (Entity candidate : entities) {
                    Mob entity = (Mob)candidate;
                    if (this.foundValidTarget(entity, false)) {
                        this.target = entity;
                        return true;
                    }
                }
            }

            return false;
        } else {
            return false;
        }
    }

    @Override
    public void start() {
        if (this.src.calmTime <= 0) {
            this.src.attackTarget = this.target;
        }

        super.start();
    }

    class EnemiesSorter implements Comparator<Object> {
        private Entity target;
        final DetectEnemy parent;

        public EnemiesSorter(DetectEnemy task, Entity entity) {
            this.parent = task;
            this.target = entity;
        }

        public int calculateDistance(Entity entity1, Entity entity2) {
            double dist1 = this.target.getDistanceSqTo(entity1);
            double dist2 = this.target.getDistanceSqTo(entity2);
            return dist1 < dist2 ? -1 : (dist1 > dist2 ? 1 : 0);
        }

        @Override
        public int compare(Object object1, Object object2) {
            return this.calculateDistance((Entity)object1, (Entity)object2);
        }
    }
}
