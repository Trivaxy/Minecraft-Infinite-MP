package net.minecraft.game.entity.misc;

import com.mojang.nbt.tag.special.CompoundTag;
import java.util.List;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public class LightningBolt extends Entity {
    private int lightningState;
    public long boltVertex = 0L;
    private int boltLivingTime;

    public LightningBolt(World world, double x, double y, double z) {
        super(world);
        this.setPosAndRot(x, y, z, 0.0F, 0.0F);
        this.lightningState = 2;
        this.boltVertex = this.rand.nextLong();
        this.boltLivingTime = this.rand.nextInt(3) + 1;
        if (!world.isOnline && world.chunksInRadiusExist(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z), 10)) {
            int blockX = MathHelper.floor_double(x);
            int blockY = MathHelper.floor_double(y);
            int blockZ = MathHelper.floor_double(z);
            if (world.getBlockId(blockX, blockY, blockZ) == 0 && BlockList.fire.placeableAt(world, blockX, blockY, blockZ)) {
                world.setBlockWithNotify(blockX, blockY, blockZ, BlockList.fire.id);
            }

            if (world.getBlockId(blockX, blockY - 1, blockZ) != BlockList.rod.id) {
                for (int var11 = 0; var11 < 4; var11++) {
                    blockY = MathHelper.floor_double(x) + this.rand.nextInt(3) - 1;
                    blockY = MathHelper.floor_double(y) + this.rand.nextInt(3) - 1;
                    blockZ = MathHelper.floor_double(z) + this.rand.nextInt(3) - 1;
                    if (world.getBlockId(var11, blockY, blockZ) == 0 && BlockList.fire.placeableAt(world, var11, blockY, blockZ)) {
                        world.setBlockWithNotify(var11, blockY, blockZ, BlockList.fire.id);
                    }
                }
            }
        }
    }

    @Override
    public void onTick() {
        super.onTick();
        if (this.lightningState == 2) {
            this.world.playSoundEffect(this.posX, this.posY, this.posZ, "random.thunder", 10000.0F, 0.8F + this.rand.nextFloat() * 0.2F);
            this.world.playSoundEffect(this.posX, this.posY, this.posZ, "random.explode", 2.0F, 0.5F + this.rand.nextFloat() * 0.2F);
        }

        this.lightningState--;
        if (this.lightningState < 0) {
            if (this.boltLivingTime == 0) {
                this.setDead();
            } else if (this.lightningState < -this.rand.nextInt(10)) {
                this.boltLivingTime--;
                this.lightningState = 1;
                this.boltVertex = this.rand.nextLong();
                if (!this.world.isOnline
                    && this.world
                        .chunksInRadiusExist(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), 10)) {
                    int x = MathHelper.floor_double(this.posX);
                    int y = MathHelper.floor_double(this.posY);
                    int z = MathHelper.floor_double(this.posZ);
                    if (this.world.getBlockId(x, y, z) == 0 && BlockList.fire.placeableAt(this.world, x, y, z)) {
                        this.world.setBlockWithNotify(x, y, z, BlockList.fire.id);
                    }
                }
            }
        }

        if (this.lightningState >= 0) {
            if (!this.world.isOnline) {
                double bbOffset = 3.0;
                List<Entity> entities = this.world
                    .getEntities(
                        this,
                        BoundingBox.getBoundingBox(
                            this.posX - bbOffset,
                            this.posY - bbOffset,
                            this.posZ - bbOffset,
                            this.posX + bbOffset,
                            this.posY + 6.0 + bbOffset,
                            this.posZ + bbOffset
                        )
                    );

                for (int i = 0; i < entities.size(); i++) {
                    Entity entity = entities.get(i);
                    entity.onStruckByLightning(this);
                }
            }

            this.world.lightningStrikeTime = 2;
        }
    }

    @Override
    protected void init() {
    }

    @Override
    protected void readFromNBT(CompoundTag tag) {
    }

    @Override
    protected void writeToNBT(CompoundTag tag) {
    }

    @Override
    public boolean inRange(Vector vec3D) {
        return this.lightningState >= 0;
    }
}
