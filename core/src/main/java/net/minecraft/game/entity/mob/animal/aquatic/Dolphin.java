package net.minecraft.game.entity.mob.animal.aquatic;

import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;

public class Dolphin extends AquaticMob {
    public Dolphin(World world) {
        super(world);
        this.setSize(1.7F, 1.0F);
        this.type = (byte)this.rand.nextInt(25);
        this.setType();
    }

    @Override
    public void setType() {
        if (this.type == 0) {
            this.texture = "/mob/dolphin/dolphin_pink.png";
        } else {
            this.texture = "/mob/dolphin/dolphin.png";
        }
    }

    @Override
    public int getSpawnID() {
        return 22;
    }

    @Override
    public String getSound() {
        return "mob.dolphin";
    }

    @Override
    protected String getHurtSound() {
        return "mob.dolphinhurt";
    }

    @Override
    protected String getDeathSound() {
        return "mob.dolphindeath";
    }

    @Override
    public int getDrop() {
        return this.fire > 0 ? ItemList.fishCooked.id : ItemList.fishRaw.id;
    }

    @Override
    public void mobTick() {
        super.mobTick();
        Player mfw = this.world.getClosestPlayer(this, 32.0);
        if (mfw != null && mfw.isInWater()) {
            mfw.jumpSpeed = mfw.jumpSpeed + mfw.prevJumpSpeed * 0.5F;
        }
    }
}
