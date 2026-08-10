package net.minecraft.client.render.blocks.model.furniture;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.building.Log;
import net.minecraft.game.block.decoration.Table;
import net.minecraft.game.texture.Icon;
import org.lwjgl.opengl.GL11;

public class TableModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        Tessellator tessellator = Tessellator.instance;
        int meta = this.world.getBlockMetadata(x, y, z);
        float brightness = block.getBrightness(this.world, x, y, z);
        tessellator.setColor(brightness, brightness, brightness);
        Table.renderSides = false;
        block.setBounds(0.0, 0.75, 0.0, 1.0, 1.0, 1.0);
        boolean render = this.renderStandardBlock(block, x, y, z);
        Table.renderSides = true;
        boolean renderLeg1 = true;
        boolean renderLeg2 = true;
        boolean renderLeg3 = true;
        boolean renderLeg4 = true;
        if (this.world != null) {
            renderLeg1 &= this.world.getBlockId(x - 1, y, z) != BlockList.table.id;
            renderLeg1 &= this.world.getBlockId(x, y, z - 1) != BlockList.table.id;
            renderLeg2 &= this.world.getBlockId(x + 1, y, z) != BlockList.table.id;
            renderLeg2 &= this.world.getBlockId(x, y, z - 1) != BlockList.table.id;
            renderLeg3 &= this.world.getBlockId(x - 1, y, z) != BlockList.table.id;
            renderLeg3 &= this.world.getBlockId(x, y, z + 1) != BlockList.table.id;
            renderLeg4 &= this.world.getBlockId(x + 1, y, z) != BlockList.table.id;
            renderLeg4 &= this.world.getBlockId(x, y, z + 1) != BlockList.table.id;
        }

        Icon legs = meta == 255 ? BlockList.blockIron.blockIcon : ((Log)BlockList.wood).front[meta];
        if (!this.hasOverrideTexture()) {
            this.setOverrideTexture(legs);
        }

        if (renderLeg1) {
            block.setBounds(0.125, 0.0, 0.125, 0.25, 0.75, 0.25);
            this.renderStandardBlock(block, x, y, z);
        }

        if (renderLeg2) {
            if (!renderLeg4) {
                block.setBounds(0.75, 0.0, 0.125, 0.875, 0.75, 0.25);
                this.renderStandardBlock(block, x, y, z);
            } else {
                block.setBounds(0.75, 0.0, 0.75, 0.875, 0.75, 0.875);
                this.renderStandardBlock(block, x, y, z);
            }
        }

        if (renderLeg3) {
            block.setBounds(0.125, 0.0, 0.75, 0.25, 0.75, 0.875);
            this.renderStandardBlock(block, x, y, z);
        }

        if (renderLeg4) {
            if (!renderLeg2) {
                block.setBounds(0.75, 0.0, 0.75, 0.875, 0.75, 0.875);
                this.renderStandardBlock(block, x, y, z);
            } else {
                block.setBounds(0.75, 0.0, 0.125, 0.875, 0.75, 0.25);
                this.renderStandardBlock(block, x, y, z);
            }
        }

        this.clearOverrideTexture();
        block.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        return render;
    }

    @Override
    public void renderInGUI(Block block, int dat) {
        Tessellator tess = Tessellator.instance;
        Icon legs = dat == 255 ? BlockList.blockIron.blockIcon : ((Log)BlockList.wood).front[dat];

        for (int part = 0; part < 5; part++) {
            if (part == 0) {
                block.setBounds(0.0, 0.75, 0.0, 1.0, 1.0, 1.0);
            } else {
                if (!this.hasOverrideTexture()) {
                    this.setOverrideTexture(legs);
                }

                if (part == 1) {
                    block.setBounds(0.125, 0.0, 0.125, 0.25, 0.75, 0.25);
                }

                if (part == 2) {
                    block.setBounds(0.75, 0.0, 0.75, 0.875, 0.75, 0.875);
                }

                if (part == 3) {
                    block.setBounds(0.125, 0.0, 0.75, 0.25, 0.75, 0.875);
                }

                if (part == 4) {
                    block.setBounds(0.75, 0.0, 0.125, 0.875, 0.75, 0.25);
                }
            }

            GL11.glTranslatef(-0.5F, -0.6F, -0.5F);
            tess.begin();
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(0, dat), BlockModel.Face.BOTTOM, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(1, dat), BlockModel.Face.TOP, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(2, dat), BlockModel.Face.NORTH, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(3, dat), BlockModel.Face.SOUTH, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(4, dat), BlockModel.Face.WEST, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(5, dat), BlockModel.Face.EAST, false);
            tess.draw();
            GL11.glTranslatef(0.5F, 0.6F, 0.5F);
        }

        block.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        this.clearOverrideTexture();
    }
}
