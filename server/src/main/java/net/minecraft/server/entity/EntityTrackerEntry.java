package net.minecraft.server.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.game.entity.DataSyncer;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.arrow.Arrow;
import net.minecraft.game.entity.boat.Boat;
import net.minecraft.game.entity.hanging.Frame;
import net.minecraft.game.entity.hanging.Painting;
import net.minecraft.game.entity.minecart.Minecart;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.misc.FallingBlock;
import net.minecraft.game.entity.misc.PrimedTNT;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.entity.projectile.Bobber;
import net.minecraft.game.entity.projectile.Projectile;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.misc.MapItem;
import net.minecraft.game.world.mapdata.MapData;
import net.minecraft.network.packet.EntityMetadataPacket;
import net.minecraft.network.packet.EntitySpawnPacket;
import net.minecraft.network.packet.EntityVelocityPacket;
import net.minecraft.network.packet.ItemEquipPacket;
import net.minecraft.network.packet.ItemPacket;
import net.minecraft.network.packet.MobPacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet31RelEntityMove;
import net.minecraft.network.packet.Packet32EntityLook;
import net.minecraft.network.packet.Packet33RelEntityMoveLook;
import net.minecraft.network.packet.PaintingPacket;
import net.minecraft.network.packet.PlayerSpawnPacket;
import net.minecraft.network.packet.RemoveEntityPacket;
import net.minecraft.network.packet.RideEntityPacket;
import net.minecraft.network.packet.RotationPacket;
import net.minecraft.network.packet.TeleportPacket;
import net.minecraft.server.player.EntityPlayerMP;
import net.minecraft.util.math.MathHelper;

public class EntityTrackerEntry {
    public Entity trackedEntity;
    public int trackingDistanceThreshold;
    public int totalUpdates;
    public int encodedPosX;
    public int encodedPosY;
    public int encodedPosZ;
    public int encodedRotationYaw;
    public int encodedRotationPitch;
    public int field_48617_i;
    public double lastTrackedEntityMotionX;
    public double lastTrackedEntityMotionY;
    public double lastTrackedEntityMotionZ;
    public int updateCounter = 0;
    private double lastTrackedEntityPosX;
    private double lastTrackedEntityPosY;
    private double lastTrackedEntityPosZ;
    private boolean firstUpdateDone = false;
    private boolean shouldSendMotionUpdates;
    private int updateTicks = 0;
    public boolean playerEntitiesUpdated = false;
    public Set<EntityPlayerMP> trackedPlayers = new HashSet<>();
    private Entity otherEntity;

    public EntityTrackerEntry(Entity entity1, int i2, int i3, boolean z4) {
        this.trackedEntity = entity1;
        this.trackingDistanceThreshold = i2;
        this.totalUpdates = i3;
        this.shouldSendMotionUpdates = z4;
        this.encodedPosX = MathHelper.floor_double(entity1.posX * 32.0);
        this.encodedPosY = MathHelper.floor_double(entity1.posY * 32.0);
        this.encodedPosZ = MathHelper.floor_double(entity1.posZ * 32.0);
        this.encodedRotationYaw = MathHelper.floor_float(entity1.yaw * 256.0F / 360.0F);
        this.encodedRotationPitch = MathHelper.floor_float(entity1.pitch * 256.0F / 360.0F);
        this.field_48617_i = MathHelper.floor_float(entity1.getFacingAngle() * 256.0F / 360.0F);
    }

    @Override
    public boolean equals(Object object1) {
        return object1 instanceof EntityTrackerEntry ? ((EntityTrackerEntry)object1).trackedEntity.entityId == this.trackedEntity.entityId : false;
    }

    @Override
    public int hashCode() {
        return this.trackedEntity.entityId;
    }

    public void updatePlayerList(List<Player> list1) {
        this.playerEntitiesUpdated = false;
        if (!this.firstUpdateDone
            || this.trackedEntity.getDistanceSq(this.lastTrackedEntityPosX, this.lastTrackedEntityPosY, this.lastTrackedEntityPosZ) > 16.0) {
            this.lastTrackedEntityPosX = this.trackedEntity.posX;
            this.lastTrackedEntityPosY = this.trackedEntity.posY;
            this.lastTrackedEntityPosZ = this.trackedEntity.posZ;
            this.firstUpdateDone = true;
            this.playerEntitiesUpdated = true;
            this.updatePlayerEntities(list1);
        }

        if (this.otherEntity != this.trackedEntity.mount || this.trackedEntity.mount != null && this.updateCounter % 60 == 0) {
            this.otherEntity = this.trackedEntity.mount;
            this.sendPacketToTrackedPlayers(new RideEntityPacket(this.trackedEntity, this.trackedEntity.mount));
        }

        if (this.trackedEntity instanceof Frame && this.updateCounter % 10 == 0) {
            Frame var23 = (Frame)this.trackedEntity;
            ItemStack var24 = var23.getDisplayedItem();
            if (var24 != null && var24.getItem() instanceof MapItem) {
                MapData var26 = ItemList.map.getMapData(var24, this.trackedEntity.world);

                for (Player var31 : list1) {
                    var26.updateVisiblePlayers(var31, var24);
                    Packet var32 = ItemList.map.createMapDataPacket(var24, this.trackedEntity.world, var31);
                    if (var32 != null) {
                        ((EntityPlayerMP)var31).playerNetServerHandler.sendPacket(var32);
                    }
                }
            }

            DataSyncer var27 = this.trackedEntity.getDataSync();
            if (var27.hasObjectChanged()) {
                this.sendPacketToTrackedPlayers(new EntityMetadataPacket(this.trackedEntity.entityId, var27));
            }
        } else if (++this.updateCounter % this.totalUpdates == 0) {
            int i2 = MathHelper.floor_double(this.trackedEntity.posX * 32.0);
            int i3 = MathHelper.floor_double(this.trackedEntity.posY * 32.0);
            int i4 = MathHelper.floor_double(this.trackedEntity.posZ * 32.0);
            int i5 = MathHelper.floor_float(this.trackedEntity.yaw * 256.0F / 360.0F);
            int i6 = MathHelper.floor_float(this.trackedEntity.pitch * 256.0F / 360.0F);
            int i7 = i2 - this.encodedPosX;
            int i8 = i3 - this.encodedPosY;
            int i9 = i4 - this.encodedPosZ;
            Object object10 = null;
            boolean z11 = Math.abs(i2) >= 8 || Math.abs(i3) >= 8 || Math.abs(i4) >= 8;
            boolean z12 = Math.abs(i5 - this.encodedRotationYaw) >= 8 || Math.abs(i6 - this.encodedRotationPitch) >= 8;
            if (i7 < -128 || i7 >= 128 || i8 < -128 || i8 >= 128 || i9 < -128 || i9 >= 128 || this.updateTicks > 400) {
                this.updateTicks = 0;
                this.trackedEntity.posX = i2 / 32.0;
                this.trackedEntity.posY = i3 / 32.0;
                this.trackedEntity.posZ = i4 / 32.0;
                object10 = new TeleportPacket(this.trackedEntity.entityId, i2, i3, i4, (byte)i5, (byte)i6);
            } else if (z11 && z12) {
                object10 = new Packet33RelEntityMoveLook(this.trackedEntity.entityId, (byte)i7, (byte)i8, (byte)i9, (byte)i5, (byte)i6);
            } else if (z11) {
                object10 = new Packet31RelEntityMove(this.trackedEntity.entityId, (byte)i7, (byte)i8, (byte)i9);
            } else if (z12) {
                object10 = new Packet32EntityLook(this.trackedEntity.entityId, (byte)i5, (byte)i6);
            }

            if (this.shouldSendMotionUpdates) {
                double d13 = this.trackedEntity.motionX - this.lastTrackedEntityMotionX;
                double d15 = this.trackedEntity.motionY - this.lastTrackedEntityMotionY;
                double d17 = this.trackedEntity.motionZ - this.lastTrackedEntityMotionZ;
                double d19 = 0.02;
                double d21 = d13 * d13 + d15 * d15 + d17 * d17;
                if (d21 > d19 * d19 || d21 > 0.0 && this.trackedEntity.motionX == 0.0 && this.trackedEntity.motionY == 0.0 && this.trackedEntity.motionZ == 0.0
                    )
                 {
                    this.lastTrackedEntityMotionX = this.trackedEntity.motionX;
                    this.lastTrackedEntityMotionY = this.trackedEntity.motionY;
                    this.lastTrackedEntityMotionZ = this.trackedEntity.motionZ;
                    this.sendPacketToTrackedPlayers(
                        new EntityVelocityPacket(
                            this.trackedEntity.entityId, this.lastTrackedEntityMotionX, this.lastTrackedEntityMotionY, this.lastTrackedEntityMotionZ
                        )
                    );
                }
            }

            if (object10 != null) {
                this.sendPacketToTrackedPlayers((Packet)object10);
            }

            DataSyncer dataWatcher23 = this.trackedEntity.getDataSync();
            if (dataWatcher23.hasObjectChanged()) {
                this.sendPacketToTrackedPlayersAndTrackedEntity(new EntityMetadataPacket(this.trackedEntity.entityId, dataWatcher23));
            }

            int i14 = MathHelper.floor_float(this.trackedEntity.getFacingAngle() * 256.0F / 360.0F);
            if (Math.abs(i14 - this.field_48617_i) >= 4) {
                this.sendPacketToTrackedPlayers(new RotationPacket(this.trackedEntity.entityId, (byte)i14));
                this.field_48617_i = i14;
            }

            if (z11) {
                this.encodedPosX = i2;
                this.encodedPosY = i3;
                this.encodedPosZ = i4;
            }

            if (z12) {
                this.encodedRotationYaw = i5;
                this.encodedRotationPitch = i6;
            }
        }

        this.updateTicks++;
        if (this.trackedEntity.beenAttacked) {
            this.sendPacketToTrackedPlayersAndTrackedEntity(new EntityVelocityPacket(this.trackedEntity));
            this.trackedEntity.beenAttacked = false;
        }
    }

    public void sendPacketToTrackedPlayers(Packet packet1) {
        for (EntityPlayerMP entityPlayerMP3 : this.trackedPlayers) {
            entityPlayerMP3.playerNetServerHandler.sendPacket(packet1);
        }
    }

    public void sendPacketToTrackedPlayersAndTrackedEntity(Packet packet1) {
        this.sendPacketToTrackedPlayers(packet1);
        if (this.trackedEntity instanceof EntityPlayerMP) {
            ((EntityPlayerMP)this.trackedEntity).playerNetServerHandler.sendPacket(packet1);
        }
    }

    public void sendDestroyEntityPacketToTrackedPlayers() {
        this.sendPacketToTrackedPlayers(new RemoveEntityPacket(this.trackedEntity.entityId));
    }

    public void removeFromTrackedPlayers(EntityPlayerMP entityPlayerMP1) {
        if (this.trackedPlayers.contains(entityPlayerMP1)) {
            this.trackedPlayers.remove(entityPlayerMP1);
        }
    }

    public void updatePlayerEntity(EntityPlayerMP entityPlayerMP1) {
        if (entityPlayerMP1 != this.trackedEntity) {
            double d2 = entityPlayerMP1.posX - this.encodedPosX / 32;
            double d4 = entityPlayerMP1.posZ - this.encodedPosZ / 32;
            if (d2 >= -this.trackingDistanceThreshold
                && d2 <= this.trackingDistanceThreshold
                && d4 >= -this.trackingDistanceThreshold
                && d4 <= this.trackingDistanceThreshold) {
                if (!this.trackedPlayers.contains(entityPlayerMP1)) {
                    this.trackedPlayers.add(entityPlayerMP1);
                    entityPlayerMP1.playerNetServerHandler.sendPacket(this.getSpawnPacket());
                    if (this.shouldSendMotionUpdates) {
                        entityPlayerMP1.playerNetServerHandler
                            .sendPacket(
                                new EntityVelocityPacket(
                                    this.trackedEntity.entityId, this.trackedEntity.motionX, this.trackedEntity.motionY, this.trackedEntity.motionZ
                                )
                            );
                    }

                    ItemStack[] itemStack6 = this.trackedEntity.getInventory();
                    if (itemStack6 != null) {
                        for (int i7 = 0; i7 < itemStack6.length; i7++) {
                            entityPlayerMP1.playerNetServerHandler.sendPacket(new ItemEquipPacket(this.trackedEntity.entityId, i7, itemStack6[i7]));
                        }
                    }
                }
            } else if (this.trackedPlayers.contains(entityPlayerMP1)) {
                this.trackedPlayers.remove(entityPlayerMP1);
                entityPlayerMP1.playerNetServerHandler.sendPacket(new RemoveEntityPacket(this.trackedEntity.entityId));
            }
        }
    }

    public void updatePlayerEntities(List<Player> list1) {
        for (int i2 = 0; i2 < list1.size(); i2++) {
            this.updatePlayerEntity((EntityPlayerMP)list1.get(i2));
        }
    }

    private Packet getSpawnPacket() {
        if (this.trackedEntity instanceof DroppedItem) {
            DroppedItem entityItem6 = (DroppedItem)this.trackedEntity;
            ItemPacket packet21PickupSpawn7 = new ItemPacket(entityItem6);
            entityItem6.posX = packet21PickupSpawn7.xPosition / 32.0;
            entityItem6.posY = packet21PickupSpawn7.yPosition / 32.0;
            entityItem6.posZ = packet21PickupSpawn7.zPosition / 32.0;
            return packet21PickupSpawn7;
        } else if (this.trackedEntity instanceof EntityPlayerMP) {
            return new PlayerSpawnPacket((Player)this.trackedEntity);
        } else if (this.trackedEntity instanceof Minecart) {
            return new EntitySpawnPacket(this.trackedEntity, EntitySpawnPacket.EntityType.MINECART, (byte)((Minecart)this.trackedEntity).type);
        } else if (this.trackedEntity instanceof Boat) {
            return new EntitySpawnPacket(this.trackedEntity, EntitySpawnPacket.EntityType.BOAT);
        } else if (this.trackedEntity instanceof Mob) {
            return new MobPacket((Mob)this.trackedEntity);
        } else if (this.trackedEntity instanceof Arrow) {
            Mob entityLiving5 = ((Arrow)this.trackedEntity).shooter;
            return new EntitySpawnPacket(
                this.trackedEntity, EntitySpawnPacket.EntityType.ARROW, entityLiving5 != null ? entityLiving5.entityId : this.trackedEntity.entityId
            );
        } else if (this.trackedEntity instanceof Projectile) {
            Mob entityLiving5 = ((Projectile)this.trackedEntity).thrower;
            return new EntitySpawnPacket(
                this.trackedEntity, EntitySpawnPacket.EntityType.SNOWBALL, entityLiving5 != null ? entityLiving5.entityId : this.trackedEntity.entityId
            );
        } else if (this.trackedEntity instanceof PrimedTNT) {
            return new EntitySpawnPacket(this.trackedEntity, EntitySpawnPacket.EntityType.TNT);
        } else if (this.trackedEntity instanceof Bobber) {
            Mob entity = ((Bobber)this.trackedEntity).angler;
            return new EntitySpawnPacket(
                this.trackedEntity, EntitySpawnPacket.EntityType.BOBBER, entity != null ? entity.entityId : this.trackedEntity.entityId
            );
        } else if (this.trackedEntity instanceof FallingBlock) {
            FallingBlock sand = (FallingBlock)this.trackedEntity;
            return new EntitySpawnPacket(this.trackedEntity, EntitySpawnPacket.EntityType.FALLING_BLOCK, (short)sand.blockID);
        } else if (this.trackedEntity instanceof Frame) {
            Frame var1 = (Frame)this.trackedEntity;
            EntitySpawnPacket var2 = new EntitySpawnPacket(
                var1, EntitySpawnPacket.EntityType.FRAME, var1.direction, (byte)var1.getRotation(), var1.getDisplayedItem()
            );
            var2.xPosition = MathHelper.floor_float(var1.xPosition * 32);
            var2.yPosition = MathHelper.floor_float(var1.yPosition * 32);
            var2.zPosition = MathHelper.floor_float(var1.zPosition * 32);
            return var2;
        } else if (this.trackedEntity instanceof Painting) {
            return new PaintingPacket((Painting)this.trackedEntity);
        } else {
            throw new IllegalArgumentException("Spawn packet case missing for" + this.trackedEntity.getClass() + "!");
        }
    }

    public void removeTrackedPlayerSymmetric(EntityPlayerMP entityPlayerMP1) {
        if (this.trackedPlayers.contains(entityPlayerMP1)) {
            this.trackedPlayers.remove(entityPlayerMP1);
            entityPlayerMP1.playerNetServerHandler.sendPacket(new RemoveEntityPacket(this.trackedEntity.entityId));
        }
    }
}
