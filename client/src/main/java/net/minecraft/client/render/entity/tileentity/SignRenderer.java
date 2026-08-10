package net.minecraft.client.render.entity.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.localization.Font;
import net.minecraft.client.render.entity.model.misc.SignModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.tileentity.other.SignEntity;
import net.minecraft.game.physics.TargetingSystem;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class SignRenderer extends TileEntityRenderer<SignEntity> {
    private SignModel model = new SignModel();

    public void render(SignEntity sign, double x, double y, double z, float tick) {
        Block block = sign.getBlockType();
        GL11.glPushMatrix();
        float offset = 0.6666667F;
        if (block == BlockList.signStanding || block == BlockList.signStanding2) {
            GL11.glTranslated(x + 0.5, y + 0.75 * offset, z + 0.5);
            float facing = sign.getBlockMetadata() * 360 / 16.0F;
            GL11.glRotatef(-facing, 0.0F, 1.0F, 0.0F);
            this.model.pole.render = true;
            this.model.legLeft.render = false;
            this.model.legRight.render = false;
            this.model.bokluk.render = false;
        } else if (block != BlockList.signHanging && block != BlockList.signHanging2) {
            int meta = sign.getBlockMetadata() & 15;
            float angle = 0.0F;
            if (meta == 2) {
                angle = 180.0F;
            }

            if (meta == 4) {
                angle = 90.0F;
            }

            if (meta == 5) {
                angle = -90.0F;
            }

            GL11.glTranslatef((float)x + 0.5F, (float)y + 0.75F * offset, (float)z + 0.5F);
            GL11.glRotatef(-angle, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(0.0F, -0.3125F, -0.4375F);
            this.model.pole.render = false;
            this.model.legLeft.render = false;
            this.model.legRight.render = false;
            this.model.pole.render = false;
            this.model.bokluk.render = false;
        } else {
            GL11.glTranslated(x + 0.5, y + 0.75 * offset, z + 0.5);
            float facing = sign.getBlockMetadata() * 360 / 16.0F;
            GL11.glRotatef(-facing, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(0.0F, -0.5F, 0.0F);
            this.model.pole.render = false;
            int topBlock = sign.world.getBlockId(sign.x, sign.y + 1, sign.z);
            if (topBlock == BlockList.fence.id) {
                this.model.legLeft.render = false;
                this.model.legRight.render = false;
                this.model.bokluk.render = true;
            } else {
                this.model.legLeft.render = true;
                this.model.legRight.render = true;
                this.model.bokluk.render = false;
            }
        }

        Font font = this.renderManager.getFontRenderer();
        float scale = 0.016666668F * offset;
        int texOffset = 0;
        if (block == BlockList.signStanding2 || block == BlockList.signHanging2 || block == BlockList.signWall2) {
            texOffset = 256;
        }

        this.bindTexture("/entity/signs/sign_" + (sign.world.getBlockMetadata(sign.x, sign.y, sign.z) + texOffset >> 4) + ".png");
        GL11.glPushMatrix();
        GL11.glScalef(offset, -offset, -offset);
        this.model.renderModel();
        this.renderBlockCracks(sign, tick);
        GL11.glPopMatrix();
        GL11.glTranslatef(0.0F, 0.5F * offset, 0.07F * offset);
        GL11.glScalef(scale, -scale, scale);
        GL11.glNormal3f(0.0F, 0.0F, -1.0F * scale);
        GL11.glDepthMask(false);

        for (int line = 0; line < sign.signText.length; line++) {
            String text = sign.signText[line];
            if (line == sign.activeLine) {
                text = "> " + text + " <";
                if (sign.glowing) {
                    GL11.glPushMatrix();
                    GL11.glDisable(2896);
                    font.drawStringWithOutline(text, -font.getStringWidth(text) / 2, line * 10 - sign.signText.length * 5, sign.textColor);
                    GL11.glEnable(2896);
                    GL11.glPopMatrix();
                } else {
                    font.drawString(text, -font.getStringWidth(text) / 2, line * 10 - sign.signText.length * 5, sign.textColor);
                }
            } else if (sign.glowing) {
                GL11.glPushMatrix();
                GL11.glDisable(2896);
                GL13.glMultiTexCoord2f(33985, 240.0F, 240.0F);
                font.drawStringWithOutline(text, -font.getStringWidth(text) / 2, line * 10 - sign.signText.length * 5, sign.textColor);
                GL11.glEnable(2896);
                GL11.glPopMatrix();
            } else {
                font.drawString(text, -font.getStringWidth(text) / 2, line * 10 - sign.signText.length * 5, sign.textColor);
            }
        }

        GL11.glDepthMask(true);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    public void renderBlockCracks(SignEntity sign, float ticks) {
        TargetingSystem target = Minecraft.getMinecraft().selectedObject;
        World world = Minecraft.getMinecraft().world;
        if (!(Minecraft.getMinecraft().gameRenderer.blocks.blockBreakTime <= 0.0F) && target != null) {
            int id = world.getBlockId(target.blockX, target.blockY, target.blockZ);
            Block block = id > 0 ? BlockList.blocks[id] : null;
            if (block == null) {
                block = BlockList.stone;
            }

            boolean isSign = sign.x == target.blockX && sign.y == target.blockY && sign.z == target.blockZ;
            GL11.glPushAttrib(24832);
            GL11.glPushMatrix();
            GL11.glEnable(3042);
            GL11.glDisable(2896);
            GL11.glEnable(3008);
            GL11.glBlendFunc(774, 768);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, (MathHelper.sin((float)System.currentTimeMillis() / 100.0F) * 0.2F + 0.4F) * 0.5F);
            GL11.glDepthMask(false);
            GL11.glPolygonOffset(-1.0F, -1.0F);
            GL11.glEnable(32823);
            int stage = (int)(Minecraft.getMinecraft().gameRenderer.blocks.blockBreakTime * 10.0F);
            this.bindTexture("/blocks/destroy/destroy_" + stage + ".png");
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.6F);
            GL11.glMatrixMode(5890);
            GL11.glPushMatrix();
            GL11.glScalef(2.25F, 1.25F, 1.0F);
            GL11.glMatrixMode(5888);
            if (isSign) {
                this.model.renderModel();
            }

            GL11.glMatrixMode(5890);
            GL11.glPopMatrix();
            GL11.glMatrixMode(5888);
            GL11.glDisable(32823);
            GL11.glDepthMask(true);
            GL11.glEnable(2896);
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }
}
