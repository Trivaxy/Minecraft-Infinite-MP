package net.minecraft.server.entity;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.arrow.Arrow;
import net.minecraft.game.entity.boat.Boat;
import net.minecraft.game.entity.hanging.Frame;
import net.minecraft.game.entity.hanging.Painting;
import net.minecraft.game.entity.minecart.Minecart;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.misc.FallingBlock;
import net.minecraft.game.entity.misc.LightningBolt;
import net.minecraft.game.entity.misc.LingeringCloud;
import net.minecraft.game.entity.misc.PrimedTNT;
import net.minecraft.game.entity.misc.VoidCrystal;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.projectile.Bobber;
import net.minecraft.game.entity.projectile.Projectile;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.player.EntityPlayerMP;

public class EntityTracker {
    private Set<EntityTrackerEntry> trackedEntitySet = new HashSet<>();
    private Int2ObjectOpenHashMap<EntityTrackerEntry> trackedEntityHashTable = new Int2ObjectOpenHashMap<>();
    private MinecraftServer mcServer;
    private int maxTrackingDistanceThreshold;
    private int id;

    public EntityTracker(MinecraftServer minecraftServer1, int i2) {
        this.mcServer = minecraftServer1;
        this.id = i2;
        this.maxTrackingDistanceThreshold = minecraftServer1.configManager.getMaxTrackingDistance();
    }

    public void trackEntity(Entity entity1) {
        if (entity1 instanceof EntityPlayerMP) {
            this.trackEntity(entity1, 512, 2);
            EntityPlayerMP entityPlayerMP2 = (EntityPlayerMP)entity1;

            for (EntityTrackerEntry entityTrackerEntry4 : this.trackedEntitySet) {
                if (entityTrackerEntry4.trackedEntity != entityPlayerMP2) {
                    entityTrackerEntry4.updatePlayerEntity(entityPlayerMP2);
                }
            }
        } else if (entity1 instanceof Arrow) {
            this.trackEntity(entity1, 64, 20, false);
        } else if (entity1 instanceof Projectile) {
            this.trackEntity(entity1, 64, 10, true);
        } else if (entity1 instanceof DroppedItem) {
            this.trackEntity(entity1, 64, 20, true);
        } else if (entity1 instanceof Minecart) {
            this.trackEntity(entity1, 160, 5, true);
        } else if (entity1 instanceof Boat) {
            this.trackEntity(entity1, 160, 5, true);
        } else if (entity1 instanceof Mob) {
            this.trackEntity(entity1, 160, 3);
        } else if (entity1 instanceof PrimedTNT) {
            this.trackEntity(entity1, 160, 10, true);
        } else if (entity1 instanceof FallingBlock) {
            this.trackEntity(entity1, 160, 20, true);
        } else if (entity1 instanceof Frame) {
            this.trackEntity(entity1, 160, Integer.MAX_VALUE, false);
        } else if (entity1 instanceof Painting) {
            this.trackEntity(entity1, 160, Integer.MAX_VALUE, false);
        } else if (entity1 instanceof Bobber) {
            this.trackEntity(entity1, 64, 20, false);
        } else if (entity1 instanceof LightningBolt) {
            this.trackEntity(entity1, 512, Integer.MAX_VALUE, false);
        } else if (entity1 instanceof LingeringCloud) {
            this.trackEntity(entity1, 160, Integer.MAX_VALUE, false);
        } else if (entity1 instanceof VoidCrystal) {
            this.trackEntity(entity1, 256, Integer.MAX_VALUE, false);
        }
    }

    public void trackEntity(Entity entity1, int i2, int i3) {
        this.trackEntity(entity1, i2, i3, false);
    }

    public void trackEntity(Entity entity1, int i2, int i3, boolean z4) {
        if (i2 > this.maxTrackingDistanceThreshold) {
            i2 = this.maxTrackingDistanceThreshold;
        }

        if (this.trackedEntityHashTable.containsKey(entity1.entityId)) {
            throw new IllegalStateException("Entity is already tracked!");
        }

        EntityTrackerEntry entityTrackerEntry5 = new EntityTrackerEntry(entity1, i2, i3, z4);
        this.trackedEntitySet.add(entityTrackerEntry5);
        this.trackedEntityHashTable.put(entity1.entityId, entityTrackerEntry5);
        entityTrackerEntry5.updatePlayerEntities(this.mcServer.getWorldManager(this.id).players);
    }

    public void untrackEntity(Entity entity1) {
        if (entity1 instanceof EntityPlayerMP) {
            EntityPlayerMP entityPlayerMP2 = (EntityPlayerMP)entity1;

            for (EntityTrackerEntry entityTrackerEntry4 : this.trackedEntitySet) {
                entityTrackerEntry4.removeFromTrackedPlayers(entityPlayerMP2);
            }
        }

        EntityTrackerEntry entityTrackerEntry5 = this.trackedEntityHashTable.remove(entity1.entityId);
        if (entityTrackerEntry5 != null) {
            this.trackedEntitySet.remove(entityTrackerEntry5);
            entityTrackerEntry5.sendDestroyEntityPacketToTrackedPlayers();
        }
    }

    public void updateTrackedEntities() {
        ArrayList<EntityPlayerMP> arrayList1 = new ArrayList<>();

        for (EntityTrackerEntry entityTrackerEntry3 : this.trackedEntitySet) {
            entityTrackerEntry3.updatePlayerList(this.mcServer.getWorldManager(this.id).players);
            if (entityTrackerEntry3.playerEntitiesUpdated && entityTrackerEntry3.trackedEntity instanceof EntityPlayerMP) {
                arrayList1.add((EntityPlayerMP)entityTrackerEntry3.trackedEntity);
            }
        }

        for (int i6 = 0; i6 < arrayList1.size(); i6++) {
            EntityPlayerMP entityPlayerMP7 = arrayList1.get(i6);

            for (EntityTrackerEntry entityTrackerEntry5 : this.trackedEntitySet) {
                if (entityTrackerEntry5.trackedEntity != entityPlayerMP7) {
                    entityTrackerEntry5.updatePlayerEntity(entityPlayerMP7);
                }
            }
        }
    }

    public void sendPacketToTrackedPlayers(Entity entity1, Packet packet2) {
        EntityTrackerEntry entityTrackerEntry3 = this.trackedEntityHashTable.get(entity1.entityId);
        if (entityTrackerEntry3 != null) {
            entityTrackerEntry3.sendPacketToTrackedPlayers(packet2);
        }
    }

    public void sendPacketToTrackedPlayersAndTrackedEntity(Entity entity1, Packet packet2) {
        EntityTrackerEntry entityTrackerEntry3 = this.trackedEntityHashTable.get(entity1.entityId);
        if (entityTrackerEntry3 != null) {
            entityTrackerEntry3.sendPacketToTrackedPlayersAndTrackedEntity(packet2);
        }
    }

    public void removeTrackedPlayerSymmetric(EntityPlayerMP entityPlayerMP1) {
        for (EntityTrackerEntry entityTrackerEntry3 : this.trackedEntitySet) {
            entityTrackerEntry3.removeTrackedPlayerSymmetric(entityPlayerMP1);
        }
    }
}
