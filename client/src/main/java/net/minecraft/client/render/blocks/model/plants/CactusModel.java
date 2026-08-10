package net.minecraft.client.render.blocks.model.plants;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import org.lwjgl.opengl.GL11;

public class CactusModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        Tessellator tess = Tessellator.instance;
        boolean render = false;
        int color = block.getRenderColor(this.world, x, y, z);
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        float west = 0.5F;
        float east = 1.0F;
        float north = 0.8F;
        float south = 0.6F;
        float westRed = west * red;
        float eastRed = east * red;
        float northRed = north * red;
        float southRed = south * red;
        float westGreen = west * green;
        float eastGreen = east * green;
        float northGreen = north * green;
        float southGreen = south * green;
        float westBlue = west * blue;
        float eastBlue = east * blue;
        float northBlue = north * blue;
        float southBlue = south * blue;
        float offset = 0.0625F;
        int brightness = block.getBrightness(this.world, x, y, z);
        if (block.renderFace(this.world, x, y - 1, z, 0)) {
            tess.setBrightness(block.minY > 0.0 ? brightness : block.getBrightness(this.world, x, y - 1, z));
            tess.setColor(westRed, westGreen, westBlue);
            this.renderFace(block, x, y, z, block.getIcon(0, 0), BlockModel.Face.BOTTOM, true);
            render = true;
        }

        if (block.renderFace(this.world, x, y + 1, z, 1)) {
            tess.setBrightness(block.maxY < 1.0 ? brightness : block.getBrightness(this.world, x, y + 1, z));
            tess.setColor(eastRed, eastGreen, eastBlue);
            this.renderFace(block, x, y, z, block.getIcon(1, 0), BlockModel.Face.TOP, true);
            render = true;
        }

        if (block.renderFace(this.world, x, y, z - 1, 2)) {
            tess.setBrightness(block.minZ > 0.0 ? brightness : block.getBrightness(this.world, x, y, z - 1));
            tess.setColor(northRed, northGreen, northBlue);
            tess.addTranslation(0.0F, 0.0F, offset);
            this.renderFace(block, x, y, z, block.getIcon(2, 0), BlockModel.Face.NORTH, true);
            tess.addTranslation(0.0F, 0.0F, -offset);
            render = true;
        }

        if (block.renderFace(this.world, x, y, z + 1, 3)) {
            tess.setBrightness(block.maxZ < 1.0 ? brightness : block.getBrightness(this.world, x, y, z + 1));
            tess.setColor(northRed, northGreen, northBlue);
            tess.addTranslation(0.0F, 0.0F, -offset);
            this.renderFace(block, x, y, z, block.getIcon(3, 0), BlockModel.Face.SOUTH, true);
            tess.addTranslation(0.0F, 0.0F, offset);
            render = true;
        }

        if (block.renderFace(this.world, x - 1, y, z, 4)) {
            tess.setBrightness(block.minX > 0.0 ? brightness : block.getBrightness(this.world, x - 1, y, z));
            tess.setColor(southRed, southGreen, southBlue);
            tess.addTranslation(offset, 0.0F, 0.0F);
            this.renderFace(block, x, y, z, block.getIcon(4, 0), BlockModel.Face.WEST, true);
            tess.addTranslation(-offset, 0.0F, 0.0F);
            render = true;
        }

        if (block.renderFace(this.world, x + 1, y, z, 5)) {
            tess.setBrightness(block.maxX < 1.0 ? brightness : block.getBrightness(this.world, x + 1, y, z));
            tess.setColor(southRed, southGreen, southBlue);
            tess.addTranslation(-offset, 0.0F, 0.0F);
            this.renderFace(block, x, y, z, block.getIcon(5, 0), BlockModel.Face.EAST, true);
            tess.addTranslation(offset, 0.0F, 0.0F);
            render = true;
        }

        return render;
    }

    @Override
    public void renderInGUI(Block block, int dat) {
        Tessellator tess = Tessellator.instance;
        block.forceBounds(0);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        float offset = 0.0625F;
        tess.begin();
        this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(0, 0), BlockModel.Face.BOTTOM, false);
        this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(1, 0), BlockModel.Face.TOP, false);
        tess.addTranslation(0.0F, 0.0F, offset);
        this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(2, 0), BlockModel.Face.NORTH, false);
        tess.addTranslation(0.0F, 0.0F, -offset);
        tess.addTranslation(0.0F, 0.0F, -offset);
        this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(3, 0), BlockModel.Face.SOUTH, false);
        tess.addTranslation(0.0F, 0.0F, offset);
        tess.addTranslation(offset, 0.0F, 0.0F);
        this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(4, 0), BlockModel.Face.WEST, false);
        tess.addTranslation(-offset, 0.0F, 0.0F);
        tess.addTranslation(-offset, 0.0F, 0.0F);
        this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(5, 0), BlockModel.Face.EAST, false);
        tess.addTranslation(offset, 0.0F, 0.0F);
        tess.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }
}
