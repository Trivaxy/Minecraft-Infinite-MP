package net.minecraft.client.render.entity.tileentity;

import net.minecraft.client.render.entity.model.misc.BookModel;
import net.minecraft.game.block.tileentity.other.LecternEntity;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class LecternRenderer extends TileEntityRenderer<LecternEntity> {
    private BookModel model = new BookModel();

    public void render(LecternEntity lectern, double x, double y, double z, float tick) {
        if (lectern.book != null) {
            GL11.glPushMatrix();
            GL11.glTranslatef((float)x + 0.5F, (float)y + 1.0F, (float)z + 0.5F);
            float aliveTime = lectern.tickCount + tick;
            GL11.glTranslatef(0.0F, 0.065F, 0.0F);
            float rotAngle = lectern.currentYaw - lectern.prevYaw;

            while (rotAngle >= (float) Math.PI) {
                rotAngle -= (float) (Math.PI * 2);
            }

            while (rotAngle < (float) -Math.PI) {
                rotAngle += (float) (Math.PI * 2);
            }

            float rotation = lectern.prevYaw + rotAngle * tick;
            GL11.glRotatef(-rotation * 180.0F / (float) Math.PI, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(90.0F, lectern.closingAngle, 0.0F, 1.0F);
            this.bindTexture("/entity/book.png");
            float flipLeft = lectern.pageFlipPrev + (lectern.pageFlip - lectern.pageFlipPrev) * tick + 0.25F;
            float flipRight = lectern.pageFlipPrev + (lectern.pageFlip - lectern.pageFlipPrev) * tick + 0.75F;
            flipLeft = (flipLeft - MathHelper.offsetBy(flipLeft)) * 1.6F - 0.3F;
            flipRight = (flipRight - MathHelper.offsetBy(flipRight)) * 1.6F - 0.3F;
            flipLeft = MathHelper.clamp(flipLeft, 0.0F, 1.0F);
            flipRight = MathHelper.clamp(flipRight, 0.0F, 1.0F);
            float f14 = lectern.bookSpreadPrev + (lectern.bookSpread - lectern.bookSpreadPrev) * tick;
            this.model.render(null, aliveTime, flipLeft, flipRight, f14, 0.0F, 0.0625F);
            GL11.glPopMatrix();
        }
    }
}
