package net.minecraft.game.block.tileentity.other;

import com.mojang.nbt.tag.special.CompoundTag;
import java.util.Random;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.entity.EntityManager;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.world.MobSpawnerPacket;

public class SpawnerEntity extends TileEntity {
    private Random rand = new Random();
    public String mobID = "";
    public int delay = -1;
    public int limit;
    public int spawns;
    public double yaw;
    public double prevYaw = 0.0;

    public SpawnerEntity() {
        this.delay = 20;
        this.spawns = 0;
        this.limit = this.rand.nextInt(15) + 10;
    }

    public boolean anyPlayerInRange() {
        return this.world.getClosestPlayer(this.x, this.y, this.z, 16.0) != null;
    }

    @Override
    public void tick() {
        this.prevYaw = this.yaw;
        if (this.anyPlayerInRange() && this.spawns < this.limit && !this.mobID.isEmpty()) {
            double offsetX = this.x + this.world.rand.nextFloat();
            double offsetY = this.y + this.world.rand.nextFloat();
            double offsetZ = this.z + this.world.rand.nextFloat();
            this.world.spawnParticle(ParticleType.SMOKE, offsetX, offsetY, offsetZ, 0.0, 0.0, 0.0);
            this.world.spawnParticle(ParticleType.FLAME, offsetX, offsetY, offsetZ, 0.0, 0.0, 0.0);

            for (this.yaw = this.yaw + 1000.0F / (this.delay + 200.0F); this.yaw > 360.0; this.prevYaw -= 360.0) {
                this.yaw -= 360.0;
            }

            if (!this.world.isOnline) {
                if (this.delay == -1) {
                    this.updateDelay();
                }

                if (this.delay > 0) {
                    this.delay--;
                } else {
                    for (int i = 0; i < 4; i++) {
                        Mob mob = (Mob)EntityManager.createEntity(this.mobID, this.world);
                        if (mob == null) {
                            return;
                        }

                        int entitiesInBB = this.world
                            .getEntitiesOfType(
                                mob.getClass(), BoundingBox.getBoundingBox(this.x, this.y, this.z, this.x + 1, this.y + 1, this.z + 1).expand(8.0, 4.0, 8.0)
                            )
                            .size();
                        if (entitiesInBB >= 6) {
                            this.updateDelay();
                            return;
                        }

                        if (mob != null) {
                            double entityX = this.x + (this.world.rand.nextDouble() - this.world.rand.nextDouble()) * 4.0;
                            double entityY = this.y + this.world.rand.nextInt(3) - 1;
                            double entityZ = this.z + (this.world.rand.nextDouble() - this.world.rand.nextDouble()) * 4.0;
                            mob.teleportTo(entityX, entityY, entityZ, this.world.rand.nextFloat() * 360.0F, 0.0F);
                            if (mob.canSpawnHere()) {
                                this.world.spawnEntity(mob);
                                this.spawns++;
                                if (this.spawns >= this.limit && mob.getDrop() > 0) {
                                    DroppedItem item = new DroppedItem(
                                        this.world,
                                        this.x,
                                        this.y + 1,
                                        this.z,
                                        new ItemStack(mob.getDrop(), this.rand.nextInt(ItemList.items[mob.getDrop()].maxStackSize) + 1)
                                    );
                                    this.world.spawnEntity(item);
                                }

                                for (int j = 0; j < 20; j++) {
                                    offsetX = this.x + 0.5 + (this.world.rand.nextFloat() - 0.5) * 2.0;
                                    offsetY = this.y + 0.5 + (this.world.rand.nextFloat() - 0.5) * 2.0;
                                    offsetZ = this.z + 0.5 + (this.world.rand.nextFloat() - 0.5) * 2.0;
                                    this.world.spawnParticle(ParticleType.SMOKE, offsetX, offsetY, offsetZ, 0.0, 0.0, 0.0);
                                    this.world.spawnParticle(ParticleType.FLAME, offsetX, offsetY, offsetZ, 0.0, 0.0, 0.0);
                                }

                                mob.spawnPuffParticles();
                                this.updateDelay();
                            }
                        }
                    }
                }

                super.tick();
            }
        }
    }

    @Override
    public Packet getPacket() {
        return new MobSpawnerPacket(this.x, this.y, this.z, this.mobID);
    }

    private void updateDelay() {
        this.delay = 200 + this.world.rand.nextInt(600);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        this.mobID = tag.getString("EntityId");
        this.delay = tag.getShort("Delay");
        this.spawns = tag.getShort("Spawns");
        this.limit = tag.getShort("Limit");
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setString("EntityId", this.mobID);
        tag.setShort("Delay", (short)this.delay);
        tag.setShort("Limit", (short)this.limit);
        tag.setShort("Spawns", (short)this.spawns);
    }

    public void setMob(String mobID) {
        this.mobID = mobID;
    }
}
