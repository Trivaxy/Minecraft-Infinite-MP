package net.minecraft.game.block.tileentity.smelters;

import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.localization.Translator;

public class HellforgeEntity extends FurnaceEntity implements Inventory {
    public HellforgeEntity() {
        this.smeltSpeed = 100;
        this.outputSlot = 1;
    }

    @Override
    public String getName() {
        return Translator.getInstance().translateKey("container.hellforge");
    }

    @Override
    public int getRemainingFuel(int amount) {
        return this.fuel * 12 / 6000;
    }

    @Override
    public boolean isBurning() {
        return true;
    }

    @Override
    public void tick() {
        if (this.fuel >= 6000) {
            this.world.createExplosion(null, this.x, this.y, this.z, 2.0F);
        }

        if (this.canSmelt()) {
            this.cookTime++;
            this.fuel++;
            if (this.cookTime == 100) {
                this.cookTime = 0;
                this.smeltItem();
            }
        } else {
            this.cookTime = 0;
            if (this.fuel > 200) {
                this.fuel--;
            }
        }

        this.onChanged();
    }
}
