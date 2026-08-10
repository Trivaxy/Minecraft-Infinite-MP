package net.minecraft.client.render.blocks.model.furniture;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.decoration.Wall;
import net.minecraft.game.block.decoration.WindowPane;
import org.lwjgl.opengl.GL11;

public class WallModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        Wall wall = (Wall)block;
        boolean render = false;
        boolean westConnection = wall.canConnectWallTo(this.world, x - 1, y, z);
        boolean eastConnection = wall.canConnectWallTo(this.world, x + 1, y, z);
        boolean northConnection = wall.canConnectWallTo(this.world, x, y, z - 1);
        boolean southConnection = wall.canConnectWallTo(this.world, x, y, z + 1);
        boolean upConnection = wall.canConnectWallTo(this.world, x, y + 1, z);
        boolean connectedLeft = northConnection && southConnection && !westConnection && !eastConnection;
        boolean connectedRight = !northConnection && !southConnection && westConnection && eastConnection;
        Block blockAbove = BlockList.blocks[this.world.getBlockId(x, y + 1, z)];
        if (blockAbove != null && !(blockAbove instanceof Wall) && !(blockAbove instanceof WindowPane)) {
            wall.setBounds(0.25, 0.0, 0.25, 0.75, 1.0, 0.75);
            this.renderStandardBlock(wall, x, y, z);
        }

        float height = 0.8125F;
        if (upConnection) {
            height = 1.0F;
        }

        if (!connectedLeft && !connectedRight) {
            wall.setBounds(0.25, 0.0, 0.25, 0.75, 1.0, 0.75);
            render = this.renderStandardBlock(wall, x, y, z);
            if (westConnection) {
                wall.setBounds(0.0, 0.0, 0.3125, 0.25, height, 0.6875);
                this.renderStandardBlock(wall, x, y, z);
            }

            if (eastConnection) {
                wall.setBounds(0.75, 0.0, 0.3125, 1.0, height, 0.6875);
                this.renderStandardBlock(wall, x, y, z);
            }

            if (northConnection) {
                wall.setBounds(0.3125, 0.0, 0.0, 0.6875, height, 0.25);
                this.renderStandardBlock(wall, x, y, z);
            }

            if (southConnection) {
                wall.setBounds(0.3125, 0.0, 0.75, 0.6875, height, 1.0);
                this.renderStandardBlock(wall, x, y, z);
            }
        } else if (connectedLeft) {
            wall.setBounds(0.3125, 0.0, 0.0, 0.6875, height, 1.0);
            this.renderStandardBlock(wall, x, y, z);
        } else {
            wall.setBounds(0.0, 0.0, 0.3125, 1.0, height, 0.6875);
            this.renderStandardBlock(wall, x, y, z);
        }

        return render;
    }

    @Override
    public void renderInGUI(Block block, int dat) {
        Tessellator tess = Tessellator.instance;

        for (int part = 0; part < 2; part++) {
            if (part == 0) {
                block.setBounds(0.0, 0.0, 0.3125, 1.0, 0.8125, 0.6875);
            }

            if (part == 1) {
                block.setBounds(0.25, 0.0, 0.25, 0.75, 1.0, 0.75);
            }

            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            tess.begin();
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(0, dat), BlockModel.Face.BOTTOM, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(1, dat), BlockModel.Face.TOP, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(2, dat), BlockModel.Face.NORTH, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(3, dat), BlockModel.Face.SOUTH, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(4, dat), BlockModel.Face.WEST, false);
            this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(5, dat), BlockModel.Face.EAST, false);
            tess.draw();
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        }

        block.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
    }
}
