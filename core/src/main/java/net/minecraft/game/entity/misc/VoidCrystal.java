package net.minecraft.game.entity.misc;

import com.mojang.nbt.tag.special.CompoundTag;
import java.util.List;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.monster.Monster;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.statistics.achievement.AchievementList;
import net.minecraft.util.math.MathHelper;

public class VoidCrystal extends Entity {
    public Entity beamTarget;
    public int rotation;
    public boolean playerPlaced;

    public VoidCrystal(World world) {
        super(world);
        this.setSize(1.5F, 3.0F);
        this.rotation = 0;
        this.preventSpawning = true;
        this.yOffset = this.height / 2.0F;
        this.rotation = this.rand.nextInt(100000);
    }

    public VoidCrystal(World world, double x, double y, double z) {
        this(world);
        this.setPosition(x, y, z);
    }

    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    protected void init() {
    }

    @Override
    public void onTick() {
        this.prevX = this.posX;
        this.prevY = this.posY;
        this.prevZ = this.posZ;
        this.rotation++;
        this.ticksExisted++;
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.posY);
        int z = MathHelper.floor_double(this.posZ);
        if (this.playerPlaced) {
            List<Entity> entities = this.world.getEntities(this, this.boundingBox.expand(32.0, 16.0, 32.0));
            Player player = this.world.getClosestPlayer(this, 64.0);
            if (!entities.isEmpty()) {
                Entity closest = null;
                double closestDist = Double.MAX_VALUE;

                for (Entity e : entities) {
                    if (e instanceof Monster) {
                        double dist = this.getDistanceTo(e);
                        if (dist < closestDist) {
                            closestDist = dist;
                            closest = e;
                        }
                    }
                }

                this.beamTarget = closest;
                if (this.beamTarget != null) {
                    this.beamTarget.damageEntity(DamageSource.magic, 4);
                    if (player != null) {
                        player.triggerAchievement(AchievementList.crystal);
                    }
                }
            }
        } else if (this.world.getBlockId(x, y - 1, z) != BlockList.fire.id) {
            this.world.setBlockWithNotify(x, y, z, BlockList.fire.id);
        }
    }

    @Override
    protected void writeToNBT(CompoundTag tag) {
        tag.setBoolean("PlayerPlaced", this.playerPlaced);
    }

    @Override
    protected void readFromNBT(CompoundTag tag) {
        this.playerPlaced = tag.getBoolean("PlayerPlaced");
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    @Override
    public boolean alwaysVisible() {
        return true;
    }

    @Override
    public boolean damageEntity(DamageSource src, int amount) {
        if (!this.isDead && !this.world.isOnline) {
            if (this.playerPlaced && (src.getEntity() == null || !(src.getEntity() instanceof Player))) {
                return false;
            }

            if (!this.world.isOnline) {
                this.setDead();
                if (this.playerPlaced) {
                    this.world.spawnEntity(new DroppedItem(this.world, this.posX, this.posY, this.posZ, new ItemStack(ItemList.endCrystal, 1)));
                } else {
                    this.world.createExplosion((Entity)null, this.posX, this.posY, this.posZ, 6.0F);
                }
            } else {
                this.setDead();
            }
        }

        return true;
    }
}
