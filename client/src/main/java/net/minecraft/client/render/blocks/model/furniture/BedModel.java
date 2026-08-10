package net.minecraft.client.render.blocks.model.furniture;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.decoration.Bed;
import net.minecraft.game.texture.Icon;
import net.minecraft.util.math.Direction;

public class BedModel extends BlockModel {
    private static final double[][] LEG_OFFSETS = new double[][]{{0.0, 0.0}, {0.8125, 0.0}, {0.0, 0.8125}, {0.8125, 0.8125}};

    @Override
    public boolean render(Block block, int x, int y, int z) {
        Tessellator tess = Tessellator.instance;
        int meta = this.world.getBlockMetadata(x, y, z);
        int direction = Bed.getDirection(meta);
        boolean foot = Bed.isFoot(meta);
        boolean render = false;
        float north = 0.5F;
        float south = 1.0F;
        float west = 0.8F;
        float east = 0.6F;
        int brightness = block.getBrightness(this.world, x, y, z);
        tess.setBrightness(brightness);
        tess.setColor(north, north, north);
        Icon icon = block.getTexture(this.world, x, y, z, 0);
        if (this.textureOverride != null) {
            icon = this.textureOverride;
        }

        this.renderFace(block, x, y + 0.1875F, z, icon, BlockModel.Face.BOTTOM, true);
        if (direction == 0) {
            this.rotationTop = 1;
        } else if (direction == 2) {
            this.rotationTop = 3;
        } else if (direction == 3) {
            this.rotationTop = 0;
        } else {
            this.rotationTop = 2;
        }

        icon = block.getTexture(this.world, x, y, z, 1);
        if (this.textureOverride != null) {
            icon = this.textureOverride;
        }

        tess.setBrightness(block.getBrightness(this.world, x, y + 1, z));
        tess.setColor(south, south, south);
        this.renderFace(block, x, y, z, icon, BlockModel.Face.TOP, true);
        int bedFacing = Direction.directionToFacing[direction];
        if (foot) {
            bedFacing = Direction.directionToFacing[Direction.rotateOpposite[direction]];
        }

        if (bedFacing != 2 && block.renderFace(this.world, x, y, z - 1, 2)) {
            tess.setBrightness(block.minZ > 0.0 ? brightness : block.getBrightness(this.world, x, y, z - 1));
            tess.setColor(west, west, west);
            this.flipTexture = direction == 3;
            this.renderFace(block, x, y, z, block.getTexture(this.world, x, y, z, 2), BlockModel.Face.NORTH, true);
            render = true;
        }

        if (bedFacing != 3 && block.renderFace(this.world, x, y, z + 1, 3)) {
            tess.setBrightness(block.maxZ < 1.0 ? brightness : block.getBrightness(this.world, x, y, z + 1));
            tess.setColor(west, west, west);
            this.flipTexture = direction == 1;
            this.renderFace(block, x, y, z, block.getTexture(this.world, x, y, z, 3), BlockModel.Face.SOUTH, true);
            render = true;
        }

        if (bedFacing != 4 && block.renderFace(this.world, x - 1, y, z, 4)) {
            tess.setBrightness(block.minZ > 0.0 ? brightness : block.getBrightness(this.world, x - 1, y, z));
            tess.setColor(east, east, east);
            this.flipTexture = direction == 2;
            this.renderFace(block, x, y, z, block.getTexture(this.world, x, y, z, 4), BlockModel.Face.WEST, true);
            render = true;
        }

        if (bedFacing != 5 && block.renderFace(this.world, x + 1, y, z, 5)) {
            tess.setBrightness(block.maxZ < 1.0 ? brightness : block.getBrightness(this.world, x + 1, y, z));
            tess.setColor(east, east, east);
            this.flipTexture = direction == 0;
            this.renderFace(block, x, y, z, block.getTexture(this.world, x, y, z, 5), BlockModel.Face.EAST, true);
            render = true;
        }

        Icon legIcon = BlockList.doorWood.getSideIcon(0);
        float legWidth = 0.1875F;
        float legHeight = 0.1875F;

        for (int i = 0; i < LEG_OFFSETS.length; i++) {
            block.setBounds(
                (float)LEG_OFFSETS[i][0], 0.0, (float)LEG_OFFSETS[i][1], (float)LEG_OFFSETS[i][0] + legWidth, legHeight, (float)LEG_OFFSETS[i][1] + legWidth
            );
            if ((meta != 0 && meta != 10 || i < 2)
                && (meta != 2 && meta != 8 || i >= 2)
                && (meta != 1 && meta != 11 || i == 3 || i == 1)
                && (meta != 3 && meta != 9 || i == 2 || i == 0)) {
                if (block.renderFace(this.world, x, y - 1, z, 0)) {
                    tess.setBrightness(block.minZ > 0.0 ? brightness : block.getBrightness(this.world, x, y - 1, z));
                    tess.setColor(west, west, west);
                    this.renderFace(block, x, y, z, legIcon, BlockModel.Face.BOTTOM, true);
                }

                if (block.renderFace(this.world, x, y, z - 1, 2)) {
                    tess.setBrightness(block.minZ > 0.0 ? brightness : block.getBrightness(this.world, x, y, z - 1));
                    tess.setColor(west, west, west);
                    this.flipTexture = direction == 3;
                    this.renderFace(block, x, y, z, legIcon, BlockModel.Face.NORTH, true);
                }

                if (block.renderFace(this.world, x, y, z + 1, 3)) {
                    tess.setBrightness(block.maxZ < 1.0 ? brightness : block.getBrightness(this.world, x, y, z + 1));
                    tess.setColor(west, west, west);
                    this.flipTexture = direction == 1;
                    this.renderFace(block, x, y, z, legIcon, BlockModel.Face.SOUTH, true);
                }

                if (block.renderFace(this.world, x - 1, y, z, 4)) {
                    tess.setBrightness(block.minZ > 0.0 ? brightness : block.getBrightness(this.world, x - 1, y, z));
                    tess.setColor(east, east, east);
                    this.flipTexture = direction == 2;
                    this.renderFace(block, x, y, z, legIcon, BlockModel.Face.WEST, true);
                }

                if (block.renderFace(this.world, x + 1, y, z, 5)) {
                    tess.setBrightness(block.maxZ < 1.0 ? brightness : block.getBrightness(this.world, x + 1, y, z));
                    tess.setColor(east, east, east);
                    this.flipTexture = direction == 0;
                    this.renderFace(block, x, y, z, legIcon, BlockModel.Face.EAST, true);
                }
            }
        }

        block.setBounds(0.0, 0.0, 0.0, 1.0, 0.5625, 1.0);
        this.flipTexture = false;
        return render;
    }
}
