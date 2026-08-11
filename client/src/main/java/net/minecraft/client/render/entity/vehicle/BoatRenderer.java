package net.minecraft.client.render.entity.vehicle;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.misc.BoatModel;
import net.minecraft.game.entity.boat.Boat;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class BoatRenderer extends EntityRenderer<Boat> {
    protected Model modelBoat = new BoatModel();
    private String texture;

    public BoatRenderer(String name) {
        this.setShadowSize(0.5F);
        this.texture = name;
    }

    public void render(Boat boat, double x, double y, double z, float offset, float tick) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glRotatef(180.0F - offset, 0.0F, 1.0F, 0.0F);
        float shake = boat.getHitTime() - tick;
        float health = boat.getDamageTaken() - tick;
        if (health < 0.0F) {
            health = 0.0F;
        }

        if (shake > 0.0F) {
            GL11.glRotatef(MathHelper.sin(shake) * shake * health / 10.0F * boat.getDirection(), 1.0F, 0.0F, 0.0F);
        }

        float scale = 0.75F;
        GL11.glScalef(scale, scale, scale);
        GL11.glScalef(1.0F / scale, 1.0F / scale, 1.0F / scale);
        this.loadTexture("/entity/vehicle/" + this.texture + ".png");
        GL11.glScalef(-1.0F, -1.0F, 1.0F);
        this.modelBoat.render(boat, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        GL11.glPopMatrix();
    }
}
