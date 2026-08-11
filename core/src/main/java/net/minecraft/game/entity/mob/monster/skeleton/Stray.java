package net.minecraft.game.entity.mob.monster.skeleton;

import net.minecraft.game.entity.mob.ai.hostile.RangedAttack;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;

public class Stray extends Skeleton {
    public Stray(World world) {
        super(world);
        this.texture = "/mob/skeleton/stray.png";
        this.scoreValue = 150;
        this.dropId = ItemList.arrowFrost.id;
        this.vampiric = false;
        this.clearAttributes();
        this.behaviors.addTask(4, new RangedAttack(this, this.moveSpeed, 2, 50));
    }

    @Override
    public int getSpawnID() {
        return 48;
    }
}
