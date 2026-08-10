package net.minecraft.client.render.entity.projectile;

import java.util.HashMap;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.game.entity.arrow.Arrow;
import net.minecraft.game.item.ItemList;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class ArrowRenderer extends EntityRenderer<Arrow> {
    private static HashMap<Integer, String> arrowsMap = new HashMap<>();

    public ArrowRenderer() {
        arrowsMap.put(ItemList.arrow.id, "bolt");
        arrowsMap.put(ItemList.arrowFlame.id, "fire");
        arrowsMap.put(ItemList.arrowFrost.id, "frost");
        arrowsMap.put(ItemList.arrowPoison.id, "poison");
        arrowsMap.put(ItemList.arrowExplosive.id, "fire");
        arrowsMap.put(ItemList.arrowEffect.id, "effect");
        arrowsMap.put(ItemList.arrowLightning.id, "golden");
        arrowsMap.put(ItemList.arrowPiercing.id, "golden");
        arrowsMap.put(ItemList.arrowCalming.id, "effect");
        arrowsMap.put(ItemList.arrowGlowing.id, "glowing");
        arrowsMap.put(ItemList.bone.id, "bone");
    }

    public void render(Arrow arrow, double x, double y, double z, float yaw, float tick) {
        String texture = arrowsMap.get(arrow.pickupID);
        if (texture == null || texture.isEmpty()) {
            texture = "arrow";
        }

        this.loadTexture("/entity/arrows/" + texture + ".png");
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        float rotYaw = arrow.prevYaw + (arrow.yaw - arrow.prevYaw) * tick;
        float rotPitch = arrow.prevPitch + (arrow.pitch - arrow.prevPitch) * tick;
        GL11.glRotatef(rotYaw - 90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(rotPitch, 0.0F, 0.0F, 1.0F);
        GL11.glEnable(32826);
        float shake = arrow.shakeFactor - tick;
        if (shake > 0.0F) {
            GL11.glRotatef(-MathHelper.sin(shake * 3.0F) * shake, 0.0F, 0.0F, 1.0F);
        }

        GL11.glRotatef(45.0F, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(0.05625F, 0.05625F, 0.05625F);
        GL11.glTranslatef(-4.0F, 0.0F, 0.0F);
        Tessellator tess = Tessellator.instance;
        float texW = 16.0F;
        float texH = 16.0F;
        float shaftU0 = 0.0F / texW;
        float shaftU1 = 16.0F / texW;
        float shaftV0 = 0.0F / texH;
        float shaftV1 = 5.0F / texH;
        float tailU0 = 0.0F / texW;
        float tailU1 = 5.0F / texW;
        float tailV0 = 5.0F / texH;
        float tailV1 = 10.0F / texH;
        GL11.glNormal3f(0.05625F, 0.0F, 0.0F);
        tess.begin();
        tess.addTexturedVertex(-7.0, -2.0, -2.0, tailU0, tailV0);
        tess.addTexturedVertex(-7.0, -2.0, 2.0, tailU1, tailV0);
        tess.addTexturedVertex(-7.0, 2.0, 2.0, tailU1, tailV1);
        tess.addTexturedVertex(-7.0, 2.0, -2.0, tailU0, tailV1);
        tess.draw();
        GL11.glNormal3f(-0.05625F, 0.0F, 0.0F);
        tess.begin();
        tess.addTexturedVertex(-7.0, 2.0, -2.0, tailU0, tailV0);
        tess.addTexturedVertex(-7.0, 2.0, 2.0, tailU1, tailV0);
        tess.addTexturedVertex(-7.0, -2.0, 2.0, tailU1, tailV1);
        tess.addTexturedVertex(-7.0, -2.0, -2.0, tailU0, tailV1);
        tess.draw();

        for (int side = 0; side < 4; side++) {
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glNormal3f(0.0F, 0.0F, 0.05625F);
            tess.begin();
            tess.addTexturedVertex(-8.0, -2.0, 0.0, shaftU0, shaftV0);
            tess.addTexturedVertex(8.0, -2.0, 0.0, shaftU1, shaftV0);
            tess.addTexturedVertex(8.0, 2.0, 0.0, shaftU1, shaftV1);
            tess.addTexturedVertex(-8.0, 2.0, 0.0, shaftU0, shaftV1);
            tess.draw();
        }

        GL11.glDisable(32826);
        GL11.glPopMatrix();
    }
}
