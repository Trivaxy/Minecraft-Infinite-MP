package net.minecraft.game.entity.mob.animal.mounts.horse;

import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class Pegasus extends Horse {
    public float wingFold;
    public float wingAngle;
    private float foldMaxValue;

    public Pegasus(World world) {
        super(world);
        this.tamedItem = ItemList.bark.id;
    }

    @Override
    public void setType() {
        this.texture = "/mob/horse/pegasus.png";
    }

    @Override
    public int getSpawnID() {
        return 65;
    }

    @Override
    public void mobTick() {
        super.mobTick();
        if (this.onGround) {
            this.wingAngle *= 0.8F;
            this.foldMaxValue = 0.1F;
        } else {
            this.foldMaxValue = 1.0F;
        }

        this.wingAngle = this.wingFold * (float)Math.sin(this.ticksExisted / 31.830988F);
        this.wingFold = this.wingFold + (this.foldMaxValue - this.wingFold) / 5.0F;
        this.motionY *= 0.5;
    }

    @Override
    public void jump() {
        this.motionY = 5.52F;
        float force = this.yaw * 0.01745329F;
        this.motionX = this.motionX - MathHelper.sin(force) * 0.2F / 2.0F;
        this.motionZ = this.motionZ + MathHelper.cos(force) * 0.2F / 2.0F;
    }

    @Override
    protected void fall(float dist) {
    }

    @Override
    protected boolean validFood(Item fond) {
        return fond == ItemList.bark;
    }

    @Override
    public void onDeath(DamageSource entity) {
        super.onDeath(entity);
        if (!this.world.isOnline) {
            int id = ItemList.feather.id;
            if (id > 0) {
                int count = this.rand.nextInt(3);

                for (int i = 0; i < count; i++) {
                    this.dropItem(id, 1);
                }
            }
        }
    }
}
