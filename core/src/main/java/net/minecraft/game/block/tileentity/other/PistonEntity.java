package net.minecraft.game.block.tileentity.other;

import com.mojang.nbt.tag.special.CompoundTag;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.util.math.Direction;

public class PistonEntity extends TileEntity {
    private static List<Entity> pushedObjects = new ArrayList<>();
    private int blockID;
    private int blockMeta;
    private int orientation;
    private boolean extending;
    private boolean renderHead;
    private float progress;
    private float lastProgress;

    public PistonEntity(int id, int meta, int facing, boolean extend, boolean render) {
        this.blockID = id;
        this.blockMeta = meta;
        this.orientation = facing;
        this.extending = extend;
        this.renderHead = render;
    }

    public int getStoredBlock() {
        return this.blockID;
    }

    @Override
    public int getBlockMetadata() {
        return this.blockMeta;
    }

    public boolean isExtending() {
        return this.extending;
    }

    public int getPistonOrientation() {
        return this.orientation;
    }

    public boolean shouldRenderHead() {
        return this.renderHead;
    }

    public float getProgress(float val) {
        if (val > 1.0F) {
            val = 1.0F;
        }

        return this.lastProgress + (this.progress - this.lastProgress) * val;
    }

    public float getOffsetX(float prog) {
        return this.extending
            ? (this.getProgress(prog) - 1.0F) * Direction.offsetsXForSide[this.orientation]
            : (1.0F - this.getProgress(prog)) * Direction.offsetsXForSide[this.orientation];
    }

    public float getOffsetY(float prog) {
        return this.extending
            ? (this.getProgress(prog) - 1.0F) * Direction.offsetsYForSide[this.orientation]
            : (1.0F - this.getProgress(prog)) * Direction.offsetsYForSide[this.orientation];
    }

    public float getOffsetZ(float prog) {
        return this.extending
            ? (this.getProgress(prog) - 1.0F) * Direction.offsetsZForSide[this.orientation]
            : (1.0F - this.getProgress(prog)) * Direction.offsetsZForSide[this.orientation];
    }

    private void pushBlocks(float progress, float force) {
        if (!this.extending) {
            progress--;
        } else {
            progress = 1.0F - progress;
        }

        BoundingBox boundingBox = BlockList.pulleyMoving.getBoundingBox(this.world, this.x, this.y, this.z, this.blockID, progress, this.orientation);
        if (boundingBox != null && this.world != null) {
            List<Entity> entities = this.world.getEntities((Entity)null, boundingBox);
            if (!entities.isEmpty()) {
                pushedObjects.addAll(entities);

                for (Entity entity : pushedObjects) {
                    entity.moveEntity(
                        force * Direction.offsetsXForSide[this.orientation],
                        force * Direction.offsetsYForSide[this.orientation],
                        force * Direction.offsetsZForSide[this.orientation]
                    );
                }

                pushedObjects.clear();
            }
        }
    }

    public void remove() {
        if (this.lastProgress < 1.0F && this.world != null) {
            this.lastProgress = this.progress = 1.0F;
            this.world.removeBlockTileEntity(this.x, this.y, this.z);
            this.invalidate();
            if (this.world.getBlockId(this.x, this.y, this.z) == BlockList.pulleyMoving.id) {
                this.world.setBlockAndMetadataWithNotify(this.x, this.y, this.z, this.blockID, this.blockMeta);
            }
        }
    }

    @Override
    public void tick() {
        this.lastProgress = this.progress;
        if (this.lastProgress >= 1.0F && this.world != null) {
            this.pushBlocks(1.0F, 0.25F);
            this.world.removeBlockTileEntity(this.x, this.y, this.z);
            this.invalidate();
            int id = this.world.getBlockId(this.x, this.y, this.z);
            if (id == BlockList.pulleyMoving.id) {
                this.world.setBlockAndMetadataWithNotify(this.x, this.y, this.z, this.blockID, this.blockMeta);
            }

            if (id > 0) {
                Block block = BlockList.blocks[this.world.getBlockId(this.x, this.y, this.z)];
                if (block != null) {
                    block.onNeighborChange(this.world, this.x, this.y, this.z, id);
                }
            }
        } else {
            this.progress += 0.5F;
            if (this.progress >= 1.0F) {
                this.progress = 1.0F;
            }

            if (this.extending) {
                this.pushBlocks(this.progress, this.progress - this.lastProgress + 0.0625F);
            }
        }
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        this.blockID = tag.getInteger("blockId");
        this.blockMeta = tag.getInteger("blockData");
        this.orientation = tag.getInteger("facing");
        this.lastProgress = this.progress = tag.getFloat("progress");
        this.extending = tag.getBoolean("extending");
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setInteger("blockId", this.blockID);
        tag.setInteger("blockData", this.blockMeta);
        tag.setInteger("facing", this.orientation);
        tag.setFloat("progress", this.lastProgress);
        tag.setBoolean("extending", this.extending);
    }
}
