package net.minecraft.client.render.blocks.model.furniture;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.utility.Anvil;
import org.lwjgl.opengl.GL11;

public class AnvilModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        this.applyColorCorrection(block, x, y, z, 1.0F);
        return this.renderAnvil((Anvil)block, x, y, z, this.world.getBlockMetadata(x, y, z), false, true);
    }

    public boolean renderAnvil(Anvil anvil, int x, int y, int z, int meta, boolean inventory, boolean offset) {
        int facing = inventory ? 0 : meta & 3;
        boolean rotated = false;
        double orientVal = 0.0;
        if (facing == 1 || facing == 3) {
            rotated = true;
        }

        orientVal = this.renderRotatedPart(anvil, x, y, z, 0, orientVal, 0.75, 0.25, 0.75, rotated, inventory, meta, offset);
        orientVal = this.renderRotatedPart(anvil, x, y, z, 1, orientVal, 0.5, 0.0625, 0.625, rotated, inventory, meta, offset);
        orientVal = this.renderRotatedPart(anvil, x, y, z, 2, orientVal, 0.25, 0.3125, 0.5, rotated, inventory, meta, offset);
        this.renderRotatedPart(anvil, x, y, z, 3, orientVal, 0.625, 0.375, 1.0, rotated, inventory, meta, offset);
        anvil.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        return true;
    }

    private double renderRotatedPart(
        Anvil anvil,
        int x,
        int y,
        int z,
        int meta,
        double orientation,
        double boundX,
        double boundY,
        double boundZ,
        boolean flip,
        boolean inventory,
        int side,
        boolean offset
    ) {
        if (flip) {
            double flipX = boundX;
            boundX = boundZ;
            boundZ = flipX;
        }

        boundX /= 2.0;
        boundZ /= 2.0;
        anvil.iconState = meta;
        anvil.setBounds(0.5 - boundX, orientation, 0.5 - boundZ, 0.5 + boundX, orientation + boundY, 0.5 + boundZ);
        if (inventory) {
            Tessellator tess = Tessellator.instance;
            tess.begin();
            this.renderFace(anvil, 0.0, 0.0, 0.0, anvil.getIcon(0, side), BlockModel.Face.BOTTOM, offset);
            this.renderFace(anvil, 0.0, 0.0, 0.0, anvil.getIcon(1, side), BlockModel.Face.TOP, offset);
            this.renderFace(anvil, 0.0, 0.0, 0.0, anvil.getIcon(2, side), BlockModel.Face.NORTH, offset);
            this.renderFace(anvil, 0.0, 0.0, 0.0, anvil.getIcon(3, side), BlockModel.Face.SOUTH, offset);
            this.renderFace(anvil, 0.0, 0.0, 0.0, anvil.getIcon(4, side), BlockModel.Face.WEST, offset);
            this.renderFace(anvil, 0.0, 0.0, 0.0, anvil.getIcon(5, side), BlockModel.Face.EAST, offset);
            tess.draw();
        } else {
            this.renderStandardBlock(anvil, x, y, z);
        }

        return orientation + boundY;
    }

    @Override
    public void renderInGUI(Block block, int dat) {
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        this.renderAnvil((Anvil)block, 0, 0, 0, dat, true, false);
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }
}
