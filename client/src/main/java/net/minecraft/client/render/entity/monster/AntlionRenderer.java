package net.minecraft.client.render.entity.monster;

import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.model.arachnid.AntlionModel;
import net.minecraft.game.entity.mob.monster.arachnid.Antlion;
import org.lwjgl.opengl.GL11;

public final class AntlionRenderer extends MobRenderer<Antlion> {
    private AntlionModel model = (AntlionModel)this.mainModel;

    public AntlionRenderer() {
        super(new AntlionModel(), 0.75F);
    }

    public void render(Antlion antlion, double x, double y, double z, float off, float tick) {
        this.model.isBuried = antlion.sitting;
        if (antlion.sitting) {
            this.shadowSize = 0.0F;
        } else {
            this.shadowSize = 0.75F;
        }

        super.render(antlion, x, y, z, off, tick);
    }

    protected final float maxRotation(Antlion entity) {
        return 180.0F;
    }

    protected void preRender(Antlion entity, float tick) {
        float scale = 0.7F;
        GL11.glScalef(scale, scale, scale);
    }
}
