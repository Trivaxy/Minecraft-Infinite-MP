package net.minecraft.game.block.tileentity.container;

import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.localization.Translator;

public class BarrelEntity extends ContainerEntity implements Inventory {
    @Override
    public String getName() {
        return Translator.getInstance().translateKey("container.barrel");
    }

    @Override
    public void open() {
        this.world.playBlockSound(this.x, this.y, this.z, "random.barrelOpen", 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public void close() {
        this.world.playBlockSound(this.x, this.y, this.z, "random.barrelClose", 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
    }
}
