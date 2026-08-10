package net.minecraft.client.render.entity.animal;

import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.game.entity.mob.animal.aquatic.Fish;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class FishRenderer extends MobRenderer<Fish> {
    public FishRenderer(Model mainModel, float shadowSize) {
        super(mainModel, shadowSize);
    }

    protected void rotate(Fish entity, float x, float y, float z) {
        super.rotate(entity, x, y, z);
        if (!entity.isInWater()
            && !entity.world
                .getBlockMaterial(MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY - 1.0), MathHelper.floor_double(entity.posZ))
                .getIsLiquid()) {
            GL11.glRotatef(this.maxRotation(entity), 0.0F, 0.0F, 1.0F);
        }
    }
}
