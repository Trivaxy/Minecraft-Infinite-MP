package net.minecraft.client.render.entity.model.arachnid;

import net.minecraft.client.render.entity.model.Model;
import net.minecraft.client.render.entity.model.ModelPart;
import net.minecraft.game.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class SilverfishModel extends Model {
    private ModelPart[] part = new ModelPart[7];
    private ModelPart[] grime;
    private float[] specks = new float[7];
    private static final int[][] boxLength = new int[][]{{3, 2, 2}, {4, 3, 2}, {6, 4, 3}, {3, 3, 3}, {2, 2, 3}, {2, 1, 2}, {1, 1, 2}};
    private static final int[][] texturePositions = new int[][]{{0, 0}, {0, 4}, {0, 9}, {0, 16}, {0, 22}, {11, 0}, {13, 4}};

    public SilverfishModel() {
        float offset = -3.5F;

        for (int part = 0; part < this.part.length; part++) {
            this.part[part] = new ModelPart(this, texturePositions[part][0], texturePositions[part][1]);
            this.part[part]
                .addBox(boxLength[part][0] * -0.5F, 0.0F, boxLength[part][2] * -0.5F, boxLength[part][0], boxLength[part][1], boxLength[part][2], 0.0F, false);
            this.part[part].setPivots(0.0F, 24 - boxLength[part][1], offset);
            this.specks[part] = offset;
            if (part < this.part.length - 1) {
                offset += (boxLength[part][2] + boxLength[part + 1][2]) * 0.5F;
            }
        }

        this.grime = new ModelPart[3];
        this.grime[0] = new ModelPart(this, 20, 0);
        this.grime[0].addBox(-5.0F, 0.0F, boxLength[2][2] * -0.5F, 10, 8, boxLength[2][2], 0.0F, false);
        this.grime[0].setPivots(0.0F, 16.0F, this.specks[2]);
        this.grime[1] = new ModelPart(this, 20, 11);
        this.grime[1].addBox(-3.0F, 0.0F, boxLength[4][2] * -0.5F, 6, 4, boxLength[4][2], 0.0F, false);
        this.grime[1].setPivots(0.0F, 20.0F, this.specks[4]);
        this.grime[2] = new ModelPart(this, 20, 18);
        this.grime[2].addBox(-3.0F, 0.0F, boxLength[4][2] * -0.5F, 6, 5, boxLength[1][2], 0.0F, false);
        this.grime[2].setPivots(0.0F, 19.0F, this.specks[1]);
    }

    @Override
    public void render(Entity entity, float swing, float anim, float angle, float yaw, float pitch, float tick) {
        this.animate(swing, anim, angle, yaw, pitch, tick);

        for (int part = 0; part < this.part.length; part++) {
            this.part[part].render(tick);
        }

        for (int var9 = 0; var9 < this.grime.length; var9++) {
            this.grime[var9].render(tick);
        }
    }

    @Override
    public void animate(float swing, float anim, float angle, float yaw, float pitch, float tick) {
        for (int part = 0; part < this.part.length; part++) {
            this.part[part].rotationY = MathHelper.cos(angle * 0.9F + part * 0.15F * (float) Math.PI) * (float) Math.PI * 0.05F * (1 + Math.abs(part - 2));
            this.part[part].pivotX = MathHelper.sin(angle * 0.9F + part * 0.15F * (float) Math.PI) * (float) Math.PI * 0.2F * Math.abs(part - 2);
        }

        this.grime[0].rotationY = this.part[2].rotationY;
        this.grime[1].rotationY = this.part[4].rotationY;
        this.grime[1].pivotX = this.part[4].pivotX;
        this.grime[2].rotationY = this.part[1].rotationY;
        this.grime[2].pivotX = this.part[1].pivotX;
    }
}
