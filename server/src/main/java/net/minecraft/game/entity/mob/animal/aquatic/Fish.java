package net.minecraft.game.entity.mob.animal.aquatic;

import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;

public class Fish extends AquaticMob {
    public Fish(World world) {
        super(world);
        this.setHealth(3);
        this.setSize(0.5F, 0.35F);
        this.type = (byte)this.rand.nextInt(2);
        this.setType();
    }

    @Override
    public void setType() {
        if (this.type == 1) {
            this.texture = "/mob/fish/salmon.png";
        } else {
            this.texture = "/mob/fish/cod.png";
        }
    }

    @Override
    public int getSpawnID() {
        return 13;
    }

    @Override
    public int getDrop() {
        return this.fire > 0 ? ItemList.fishCooked.id : ItemList.fishRaw.id;
    }

    @Override
    public void onDeath(DamageSource source) {
        if (source.getEntity() != null && source.getEntity() instanceof Player) {
            super.onDeath(source);
        } else {
            this.dead = true;
            this.world.updateServerEntity(this, (byte)3);
        }
    }

    @Override
    protected void dropFewItems(int lootingModifier) {
        this.dropItem(this.getDrop(), 1);
    }
}
