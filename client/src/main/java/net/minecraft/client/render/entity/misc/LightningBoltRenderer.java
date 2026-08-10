package net.minecraft.client.render.entity.misc;

import java.util.Random;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.game.entity.misc.LightningBolt;
import org.lwjgl.opengl.GL11;

public class LightningBoltRenderer extends EntityRenderer<LightningBolt> {
    public void render(LightningBolt entity, double x, double y, double z, float yaw, float pitch) {
        Tessellator tess = Tessellator.instance;
        GL11.glDisable(3553);
        GL11.glDisable(2896);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        double[] us = new double[8];
        double[] vs = new double[8];
        double u = 0.0;
        double v = 0.0;
        Random rand = new Random(entity.boltVertex);

        for (int vert = 7; vert >= 0; vert--) {
            us[vert] = u;
            vs[vert] = v;
            u += rand.nextInt(11) - 5;
            v += rand.nextInt(11) - 5;
        }

        for (int offset = 0; offset < 4; offset++) {
            Random random = new Random(entity.boltVertex);

            for (int index = 0; index < 3; index++) {
                int vert = 7;
                int max = 0;
                if (index > 0) {
                    vert = 7 - index;
                }

                if (index > 0) {
                    max = vert - 2;
                }

                double finalU = us[vert] - u;
                double finalV = vs[vert] - v;

                for (int uvcount = vert; uvcount >= max; uvcount--) {
                    double uOffset = finalU;
                    double vOffset = finalV;
                    float colorOffset = 0.5F;
                    if (index == 0) {
                        finalU += random.nextInt(11) - 5;
                        finalV += random.nextInt(11) - 5;
                    } else {
                        finalU += random.nextInt(31) - 15;
                        finalV += random.nextInt(31) - 15;
                    }

                    tess.begin(5);
                    tess.setColorAlpha(0.9F * colorOffset, 0.9F * colorOffset, 1.0F * colorOffset, 0.3F);
                    double startOffset = 0.1 + offset * 0.2;
                    if (index == 0) {
                        startOffset *= uvcount * 0.1 + 1.0;
                    }

                    double endOffset = 0.1 + offset * 0.2;
                    if (index == 0) {
                        endOffset *= (uvcount - 1) * 0.1 + 1.0;
                    }

                    for (int face = 0; face < 5; face++) {
                        double startX = x + 0.5 - startOffset;
                        double startZ = z + 0.5 - startOffset;
                        if (face == 1 || face == 2) {
                            startX += startOffset * 2.0;
                        }

                        if (face == 2 || face == 3) {
                            startZ += startOffset * 2.0;
                        }

                        double endX = x + 0.5 - endOffset;
                        double endZ = z + 0.5 - endOffset;
                        if (face == 1 || face == 2) {
                            endX += endOffset * 2.0;
                        }

                        if (face == 2 || face == 3) {
                            endZ += endOffset * 2.0;
                        }

                        tess.addVertex(endX + finalU, y + uvcount * 16, endZ + finalV);
                        tess.addVertex(startX + uOffset, y + (uvcount + 1) * 16, startZ + vOffset);
                    }

                    tess.draw();
                }
            }
        }

        GL11.glDisable(3042);
        GL11.glEnable(2896);
        GL11.glEnable(3553);
    }
}
