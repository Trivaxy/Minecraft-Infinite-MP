package net.minecraft.game.entity.misc;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class FallingBlock extends Entity {
    private Player player;
    public int blockID;
    public int metadata;
    public int fallTime = 0;

    public FallingBlock(World world) {
        super(world);
    }

    public FallingBlock(World world, double xCoord, double yCoord, double zCoord, int blockid) {
        super(world);
        this.blockID = blockid;
        this.preventSpawning = true;
        this.yOffset = this.height / 2.0F;
        this.setSize(0.98F, 0.98F);
        this.setPosition(xCoord, yCoord, zCoord);
        this.motionX = 0.0;
        this.motionY = 0.0;
        this.motionZ = 0.0;
        this.produceFootsteps = false;
        this.prevX = xCoord;
        this.prevY = yCoord;
        this.prevZ = zCoord;
    }

    public FallingBlock(World world, double x, double y, double z, int id, int meta) {
        this(world, x, y, z, id);
        this.metadata = meta;
    }

    public FallingBlock(World world, double x, double y, double z, int id, int meta, Player player) {
        this(world, x, y, z, id, meta);
        this.player = player;
    }

    @Override
    protected void init() {
    }

    @Override
    public boolean isSelectable() {
        return !this.isDead;
    }

    @Override
    public void onTick() {
        if (this.blockID <= 0) {
            this.setDead();
        } else {
            this.prevX = this.posX;
            this.prevY = this.posY;
            this.prevZ = this.posZ;
            if (this.player != null) {
                Vector vec3D3 = this.player.getLookVector(1.0F);
                if (this.beenAttacked) {
                    this.fallTime++;
                    this.motionX = vec3D3.xCoord;
                    this.motionY = vec3D3.yCoord;
                    this.motionZ = vec3D3.zCoord;
                    this.motionY -= 0.04;
                    this.player = null;
                } else {
                    this.setPosition(this.player.posX + vec3D3.xCoord * 2.0, this.player.posY + vec3D3.yCoord * 2.0, this.player.posZ + vec3D3.zCoord * 2.0);
                }
            } else {
                this.fallTime++;
                this.motionY -= 0.04;
            }

            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.98;
            this.motionY *= 0.98;
            this.motionZ *= 0.98;
            int x = MathHelper.floor_double(this.posX);
            int y = MathHelper.floor_double(this.posY);
            int z = MathHelper.floor_double(this.posZ);
            Block drop = BlockList.blocks[this.blockID];
            if (this.world.getBlockId(x, y - 1, z) == BlockList.quickSand.id || this.world.getBlockId(x, y - 1, z) == BlockList.powderSnow.id) {
                this.world.setBlockAndMetadataWithNotify(x, y, z, this.blockID, this.metadata);
                this.setDead();
            }

            if (this.onGround) {
                this.motionX *= 0.7F;
                this.motionZ *= 0.7F;
                this.motionY *= -0.5;
                if ((!this.world.canPlace(this.blockID, x, y, z, true, 1) || !this.world.setBlockAndMetadataWithNotify(x, y, z, this.blockID, this.metadata))
                    && !this.world.isOnline) {
                    this.dropItem(drop.dropID(this.blockID, this.rand), 1, drop.dropMeta(this.metadata), 0.0F);
                }

                this.setDead();
            } else if (this.fallTime > 100 && !this.world.isOnline) {
                this.dropItem(drop.dropID(this.blockID, this.rand), 1, drop.dropMeta(this.metadata), 0.0F);
                this.setDead();
            }
        }
    }

    @Override
    public boolean interact(Player playa) {
        if (this.player != null) {
            this.player = null;
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void writeToNBT(CompoundTag tag) {
        tag.setByte("Tile", (byte)this.blockID);
        tag.setByte("Data", (byte)this.metadata);
    }

    @Override
    protected void readFromNBT(CompoundTag tag) {
        this.blockID = tag.getByte("Tile") & 255;
        this.metadata = tag.getByte("Data") & 255;
    }
}
