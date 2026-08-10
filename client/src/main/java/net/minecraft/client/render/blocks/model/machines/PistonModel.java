package net.minecraft.client.render.blocks.model.machines;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.BlocksRenderer;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.machines.Piston;
import org.lwjgl.opengl.GL11;

public class PistonModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        return this.renderPiston(block, x, y, z, false);
    }

    public boolean renderPiston(Block block, int x, int y, int z, boolean fullRender) {
        int meta = this.world.getBlockMetadata(x, y, z);
        boolean extended = fullRender || (meta & 8) != 0;
        int facing = Piston.getOrientation(meta);
        if (BlocksRenderer.models.get(block.getRenderType()) != this) {
            return BlocksRenderer.models.get(block.getRenderType()).render(block, x, y, z);
        }

        switch (facing) {
            case 0:
                this.rotationNorth = 2;
                this.rotationSouth = 2;
                this.rotationEast = 2;
                this.rotationWest = 2;
            case 1:
            default:
                break;
            case 2:
                this.rotationEast = 3;
                this.rotationWest = 1;
                this.rotationBottom = 2;
                break;
            case 3:
                this.rotationEast = 1;
                this.rotationWest = 3;
                this.rotationTop = 2;
                break;
            case 4:
                this.rotationNorth = 3;
                this.rotationSouth = 1;
                this.rotationTop = 3;
                this.rotationBottom = 3;
                break;
            case 5:
                this.rotationNorth = 1;
                this.rotationSouth = 3;
                this.rotationTop = 1;
                this.rotationBottom = 1;
        }

        if (extended) {
            switch (facing) {
                case 0:
                    block.setBounds(0.0, 0.25, 0.0, 1.0, 1.0, 1.0);
                    break;
                case 1:
                    block.setBounds(0.0, 0.0, 0.0, 1.0, 0.75, 1.0);
                    break;
                case 2:
                    block.setBounds(0.0, 0.0, 0.25, 1.0, 1.0, 1.0);
                    break;
                case 3:
                    block.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 0.75);
                    break;
                case 4:
                    block.setBounds(0.25, 0.0, 0.0, 1.0, 1.0, 1.0);
                    break;
                case 5:
                    block.setBounds(0.0, 0.0, 0.0, 0.75, 1.0, 1.0);
            }

            block.setBounds(block.minX, block.minY, block.minZ, block.maxX, block.maxY, block.maxZ);
            this.renderStandardBlock(block, x, y, z);
            block.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
            block.setBounds(block.minX, block.minY, block.minZ, block.maxX, block.maxY, block.maxZ);
        } else {
            this.renderStandardBlock(block, x, y, z);
        }

        this.rotationNorth = 0;
        this.rotationSouth = 0;
        this.rotationEast = 0;
        this.rotationWest = 0;
        this.rotationTop = 0;
        this.rotationBottom = 0;
        return true;
    }

    @Override
    public void renderInGUI(Block block, int dat) {
        Tessellator tess = Tessellator.instance;
        block.forceBounds(dat);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        tess.begin();
        this.renderFace(block, 0.0, 0.0, 0.0, block.getSideIcon(0), BlockModel.Face.TOP, false);
        this.renderFace(block, 0.0, 0.0, 0.0, block.getSideIcon(1), BlockModel.Face.BOTTOM, false);
        this.renderFace(block, 0.0, 0.0, 0.0, block.getSideIcon(2), BlockModel.Face.NORTH, false);
        this.renderFace(block, 0.0, 0.0, 0.0, block.getSideIcon(3), BlockModel.Face.SOUTH, false);
        this.renderFace(block, 0.0, 0.0, 0.0, block.getSideIcon(4), BlockModel.Face.WEST, false);
        this.renderFace(block, 0.0, 0.0, 0.0, block.getSideIcon(5), BlockModel.Face.EAST, false);
        tess.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }
}
