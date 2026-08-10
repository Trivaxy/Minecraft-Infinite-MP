package net.minecraft.client.render.blocks.model.furniture;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.building.Stairs;
import net.minecraft.game.world.WorldSource;
import org.lwjgl.opengl.GL11;

public class StairModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        boolean render = false;
        this.renderNormal(this.world, block, x, y, z);
        render = this.renderStandardBlock(block, x, y, z);
        this.renderInnerCorner(this.world, block, x, y, z);
        render = this.renderStandardBlock(block, x, y, z);
        this.renderOuterCorner(this.world, block, x, y, z);
        render = this.renderStandardBlock(block, x, y, z);
        block.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        return render;
    }

    @Override
    public void renderInGUI(Block block, int dat) {
        Tessellator tess = Tessellator.instance;

        for (int part = 0; part < 2; part++) {
            if (part == 0) {
                block.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 0.5);
            }

            if (part == 1) {
                block.setBounds(0.0, 0.0, 0.5, 1.0, 0.5, 1.0);
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
    }

    private boolean renderInnerCorner(WorldSource world, Block block, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        int state = meta & 3;
        float maxWidth = 0.5F;
        float maxHeight = 1.0F;
        if ((meta & 4) != 0) {
            maxWidth = 0.0F;
            maxHeight = 0.5F;
        }

        float startW = 0.0F;
        float endW = 1.0F;
        float startH = 0.0F;
        float endH = 0.5F;
        boolean render = true;
        if (state == 0) {
            startW = 0.5F;
            endH = 1.0F;
            int neighborID = world.getBlockId(x + 1, y, z);
            int neighborMeta = world.getBlockMetadata(x + 1, y, z);
            if (this.isStairs(neighborID) && (meta & 4) == (neighborMeta & 4)) {
                int side = neighborMeta & 3;
                if (side == 3 && !this.setBoundState(world, x, y, z + 1, meta)) {
                    endH = 0.5F;
                    render = false;
                } else if (side == 2 && !this.setBoundState(world, x, y, z - 1, meta)) {
                    startH = 0.5F;
                    render = false;
                }
            }
        } else if (state == 1) {
            endW = 0.5F;
            endH = 1.0F;
            int neighborID = world.getBlockId(x - 1, y, z);
            int neighborMeta = world.getBlockMetadata(x - 1, y, z);
            if (this.isStairs(neighborID) && (meta & 4) == (neighborMeta & 4)) {
                int side = neighborMeta & 3;
                if (side == 3 && !this.setBoundState(world, x, y, z + 1, meta)) {
                    endH = 0.5F;
                    render = false;
                } else if (side == 2 && !this.setBoundState(world, x, y, z - 1, meta)) {
                    startH = 0.5F;
                    render = false;
                }
            }
        } else if (state == 2) {
            startH = 0.5F;
            endH = 1.0F;
            int neighborID = world.getBlockId(x, y, z + 1);
            int neighborMeta = world.getBlockMetadata(x, y, z + 1);
            if (this.isStairs(neighborID) && (meta & 4) == (neighborMeta & 4)) {
                int side = neighborMeta & 3;
                if (side == 1 && !this.setBoundState(world, x + 1, y, z, meta)) {
                    endW = 0.5F;
                    render = false;
                } else if (side == 0 && !this.setBoundState(world, x - 1, y, z, meta)) {
                    startW = 0.5F;
                    render = false;
                }
            }
        } else if (state == 3) {
            int neighborID = world.getBlockId(x, y, z - 1);
            int neighborMeta = world.getBlockMetadata(x, y, z - 1);
            if (this.isStairs(neighborID) && (meta & 4) == (neighborMeta & 4)) {
                int side = neighborMeta & 3;
                if (side == 1 && !this.setBoundState(world, x + 1, y, z, meta)) {
                    endW = 0.5F;
                    render = false;
                } else if (side == 0 && !this.setBoundState(world, x - 1, y, z, meta)) {
                    startW = 0.5F;
                    render = false;
                }
            }
        }

        block.setBounds(startW, maxWidth, startH, endW, maxHeight, endH);
        return render;
    }

    private boolean renderOuterCorner(WorldSource world, Block block, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        int state = meta & 3;
        float maxWidth = 0.5F;
        float maxHeight = 1.0F;
        if ((meta & 4) != 0) {
            maxWidth = 0.0F;
            maxHeight = 0.5F;
        }

        float startW = 0.0F;
        float endW = 0.5F;
        float startH = 0.5F;
        float endH = 1.0F;
        boolean render = false;
        if (state == 0) {
            int neighborID = world.getBlockId(x - 1, y, z);
            int neighborMeta = world.getBlockMetadata(x - 1, y, z);
            if (this.isStairs(neighborID) && (meta & 4) == (neighborMeta & 4)) {
                int side = neighborMeta & 3;
                if (side == 3 && !this.setBoundState(world, x, y, z - 1, meta)) {
                    startH = 0.0F;
                    endH = 0.5F;
                    render = true;
                } else if (side == 2 && !this.setBoundState(world, x, y, z + 1, meta)) {
                    startH = 0.5F;
                    endH = 1.0F;
                    render = true;
                }
            }
        } else if (state == 1) {
            int neighborID = world.getBlockId(x + 1, y, z);
            int neighborMeta = world.getBlockMetadata(x + 1, y, z);
            if (this.isStairs(neighborID) && (meta & 4) == (neighborMeta & 4)) {
                startW = 0.5F;
                endW = 1.0F;
                int side = neighborMeta & 3;
                if (side == 3 && !this.setBoundState(world, x, y, z - 1, meta)) {
                    startH = 0.0F;
                    endH = 0.5F;
                    render = true;
                } else if (side == 2 && !this.setBoundState(world, x, y, z + 1, meta)) {
                    startH = 0.5F;
                    endH = 1.0F;
                    render = true;
                }
            }
        } else if (state == 2) {
            int neighborID = world.getBlockId(x, y, z - 1);
            int neighborMeta = world.getBlockMetadata(x, y, z - 1);
            if (this.isStairs(neighborID) && (meta & 4) == (neighborMeta & 4)) {
                startH = 0.0F;
                endH = 0.5F;
                int side = neighborMeta & 3;
                if (side == 1 && !this.setBoundState(world, x - 1, y, z, meta)) {
                    render = true;
                } else if (side == 0 && !this.setBoundState(world, x + 1, y, z, meta)) {
                    startW = 0.5F;
                    endW = 1.0F;
                    render = true;
                }
            }
        } else if (state == 3) {
            int neighborID = world.getBlockId(x, y, z + 1);
            int neighborMeta = world.getBlockMetadata(x, y, z + 1);
            if (this.isStairs(neighborID) && (meta & 4) == (neighborMeta & 4)) {
                int side = neighborMeta & 3;
                if (side == 1 && !this.setBoundState(world, x - 1, y, z, meta)) {
                    render = true;
                } else if (side == 0 && !this.setBoundState(world, x + 1, y, z, meta)) {
                    startW = 0.5F;
                    endW = 1.0F;
                    render = true;
                }
            }
        }

        if (render) {
            block.setBounds(startW, maxWidth, startH, endW, maxHeight, endH);
        }

        return render;
    }

    private void renderNormal(WorldSource world, Block block, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z) & 4;
        if (meta != 0) {
            block.setBounds(0.0, 0.5, 0.0, 1.0, 1.0, 1.0);
        } else {
            block.setBounds(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
        }
    }

    private boolean isStairs(int id) {
        return id > 0 && BlockList.blocks[id] instanceof Stairs;
    }

    private boolean setBoundState(WorldSource world, int x, int y, int z, int meta) {
        return this.isStairs(world.getBlockId(x, y, z)) && world.getBlockMetadata(x, y, z) == meta;
    }
}
