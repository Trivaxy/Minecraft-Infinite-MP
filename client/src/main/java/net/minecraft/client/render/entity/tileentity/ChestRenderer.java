package net.minecraft.client.render.entity.tileentity;

import net.minecraft.client.options.OptionValues;
import net.minecraft.client.render.entity.model.misc.ChestModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.tileentity.container.ChestEntity;
import net.minecraft.game.block.utility.Chest;
import org.lwjgl.opengl.GL11;

public class ChestRenderer extends TileEntityRenderer<ChestEntity> {
    private ChestModel chestModel = new ChestModel();
    private ChestModel largeChestModel = new ChestModel.LongChest();

    public void render(ChestEntity chest, double x, double y, double z, float tick) {
        int meta = 0;
        if (!(OptionValues.offsetAmount > 0.0F)) {
            if (chest.world != null) {
                Block block = chest.getBlockType();
                meta = chest.getBlockMetadata();
                if (block != null && block == BlockList.chest && meta == 0) {
                    ((Chest)block).unifyAdjacentChests(chest.world, chest.x, chest.y, chest.z);
                    meta = chest.getBlockMetadata();
                }

                chest.checkForChests();
            }

            if (chest.chestLeft == null && chest.chestBack == null
                || chest.getBlockType() == BlockList.lockedChest
                || chest.getBlockType() == BlockList.chestObsidian) {
                ChestModel model = this.chestModel;
                if (chest.getBlockType() == BlockList.lockedChest) {
                    this.bindTexture("/entity/chest/chestStone.png");
                } else if (chest.getBlockType() == BlockList.chestObsidian) {
                    this.bindTexture("/entity/chest/chestObsidian.png");
                } else if (chest.chestFront == null && chest.chestRight == null) {
                    this.bindTexture("/entity/chest/chest.png");
                } else {
                    model = this.largeChestModel;
                    this.bindTexture("/entity/chest/largechest.png");
                }

                GL11.glPushMatrix();
                GL11.glEnable(32826);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glTranslatef((float)x, (float)y + 1.0F, (float)z + 1.0F);
                GL11.glScalef(1.0F, -1.0F, -1.0F);
                GL11.glTranslatef(0.5F, 0.5F, 0.5F);
                short angle = 0;
                if (meta == 2) {
                    angle = 180;
                }

                if (meta == 3) {
                    angle = 0;
                }

                if (meta == 4) {
                    angle = 90;
                }

                if (meta == 5) {
                    angle = -90;
                }

                if (chest.getBlockType() == BlockList.chest) {
                    if (meta == 2 && chest.chestFront != null) {
                        GL11.glTranslatef(1.0F, 0.0F, 0.0F);
                    }

                    if (meta == 5 && chest.chestRight != null) {
                        GL11.glTranslatef(0.0F, 0.0F, -1.0F);
                    }
                }

                GL11.glRotatef(angle, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
                float lidAngle = chest.lidAngle;
                lidAngle = 1.0F - lidAngle;
                lidAngle = 1.0F - lidAngle * lidAngle * lidAngle;
                model.lid.rotationX = -(lidAngle * (float) Math.PI / 2.0F);
                model.renderAll();
                GL11.glDisable(32826);
                GL11.glPopMatrix();
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
