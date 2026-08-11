package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.packet.container.ContainerClosePacket;
import net.minecraft.network.packet.container.ContainerContentsPacket;
import net.minecraft.network.packet.container.ContainerDataPacket;
import net.minecraft.network.packet.container.ContainerOpenPacket;
import net.minecraft.network.packet.container.CustomPayloadPacket;
import net.minecraft.network.packet.container.GeneratorPacket;
import net.minecraft.network.packet.container.PlayNotePacket;
import net.minecraft.network.packet.container.SignPacket;
import net.minecraft.network.packet.entity.EntityActionPacket;
import net.minecraft.network.packet.entity.EntityEffectPacket;
import net.minecraft.network.packet.entity.EntityHealthPacket;
import net.minecraft.network.packet.entity.EntityMetadataPacket;
import net.minecraft.network.packet.entity.EntityMovementPacket;
import net.minecraft.network.packet.entity.EntitySpawnPacket;
import net.minecraft.network.packet.entity.EntityStatePacket;
import net.minecraft.network.packet.entity.EntityVelocityPacket;
import net.minecraft.network.packet.entity.LookPacket;
import net.minecraft.network.packet.entity.MobPacket;
import net.minecraft.network.packet.entity.MoveLookPacket;
import net.minecraft.network.packet.entity.MovePacket;
import net.minecraft.network.packet.entity.PositionPacket;
import net.minecraft.network.packet.entity.RemoveEffectPacket;
import net.minecraft.network.packet.entity.RideEntityPacket;
import net.minecraft.network.packet.entity.RotationPacket;
import net.minecraft.network.packet.misc.ChatPacket;
import net.minecraft.network.packet.misc.DisconnectPacket;
import net.minecraft.network.packet.misc.HandshakePacket;
import net.minecraft.network.packet.misc.KeepAlivePacket;
import net.minecraft.network.packet.misc.LoginPacket;
import net.minecraft.network.packet.misc.MusicPacket;
import net.minecraft.network.packet.misc.OpPacket;
import net.minecraft.network.packet.misc.PaintingPacket;
import net.minecraft.network.packet.misc.PingPacket;
import net.minecraft.network.packet.misc.ServerIconPacket;
import net.minecraft.network.packet.misc.TransactionPacket;
import net.minecraft.network.packet.player.AnimationPacket;
import net.minecraft.network.packet.player.AttackDirectionPacket;
import net.minecraft.network.packet.player.ClickPacket;
import net.minecraft.network.packet.player.DigPacket;
import net.minecraft.network.packet.player.ItemEquipPacket;
import net.minecraft.network.packet.player.ItemPacket;
import net.minecraft.network.packet.player.ItemSwitchPacket;
import net.minecraft.network.packet.player.PickupPacket;
import net.minecraft.network.packet.player.PlacePacket;
import net.minecraft.network.packet.player.PlayerInfoPacket;
import net.minecraft.network.packet.player.PlayerLookPacket;
import net.minecraft.network.packet.player.PlayerMovementPacket;
import net.minecraft.network.packet.player.PlayerPositionPacket;
import net.minecraft.network.packet.player.PlayerSpawnPacket;
import net.minecraft.network.packet.player.PlayerStatePacket;
import net.minecraft.network.packet.player.PlayerTeleportPacket;
import net.minecraft.network.packet.player.SleepPacket;
import net.minecraft.network.packet.player.SlotPacket;
import net.minecraft.network.packet.player.UsePacket;
import net.minecraft.network.packet.world.BlockEventPacket;
import net.minecraft.network.packet.world.ExplosionPacket;
import net.minecraft.network.packet.world.MapPacket;
import net.minecraft.network.packet.world.MobSpawnerPacket;
import net.minecraft.network.packet.world.PrepChunkPacket;
import net.minecraft.network.packet.world.RemoveEntityPacket;
import net.minecraft.network.packet.world.RespawnPacket;
import net.minecraft.network.packet.world.SpawnPacket;
import net.minecraft.network.packet.world.TeleportPacket;
import net.minecraft.network.packet.world.UpdateBlockPacket;
import net.minecraft.network.packet.world.UpdateChunkPacket;
import net.minecraft.network.packet.world.UpdateSectionPacket;
import net.minecraft.network.packet.world.UpdateTimePacket;
import net.minecraft.network.packet.world.WorldEventPacket;
import net.minecraft.util.logging.LoggingUtils;

public abstract class Packet {
    protected static Logger logger = Logger.getLogger("Minecraft");
    private static Map<Integer, Class<? extends Packet>> idToClass = new HashMap<>();
    private static Map<Class<? extends Packet>, Integer> classToId = new HashMap<>();
    private static Set<Integer> clientboundAllowedIds = new HashSet<>();
    private static Set<Integer> serverboundAllowedIds = new HashSet<>();
    public final long creationTimeMillis = System.currentTimeMillis();
    public boolean isDelayed = false;

    private static void map(int id, Packet.Flow direction, Class<? extends Packet> c) {
        if (idToClass.containsKey(id)) {
            throw new Error("Duplicate packet id:" + id);
        }

        if (classToId.containsKey(c)) {
            throw new Error("Duplicate packet class:" + c);
        }

        if (direction == null) {
            throw new Error("java enum should not be null!");
        }

        idToClass.put(id, c);
        classToId.put(c, id);
        if (direction == Packet.Flow.BOTH || direction == Packet.Flow.SERVER_TO_CLIENT) {
            clientboundAllowedIds.add(id);
        }

        if (direction == Packet.Flow.BOTH || direction == Packet.Flow.CLIENT_TO_SERVER) {
            serverboundAllowedIds.add(id);
        }
    }

    @Deprecated
    private static void map(int id, boolean isclientbound, boolean isserverbound, Class<? extends Packet> c) {
        Packet.Flow dir;
        if (isclientbound && isserverbound) {
            dir = Packet.Flow.BOTH;
        } else if (isclientbound) {
            dir = Packet.Flow.SERVER_TO_CLIENT;
        } else {
            if (!isserverbound) {
                throw new Error("Invalid arguments in Packet.map()");
            }

            dir = Packet.Flow.CLIENT_TO_SERVER;
        }

        map(id, dir, c);
    }

    public static Packet createPacket(int id) {
        try {
            Class<? extends Packet> class1 = idToClass.get(id);
            return class1 == null ? null : class1.newInstance();
        } catch (Exception exception2) {
            LoggingUtils.warnf(logger, exception2, "Skipping packet with id %d: %s", id, exception2);
            return null;
        }
    }

    public final int getPacketId() {
        return classToId.get(this.getClass());
    }

    public static Packet readPacket(DataInputStream dis, boolean isServerPacketHandler) throws IOException {
        Packet ret = null;

        try {
            int id_unsigned_byte = dis.read();
            if (id_unsigned_byte == -1) {
                return null;
            }

            if ((!isServerPacketHandler || serverboundAllowedIds.contains(id_unsigned_byte))
                && (isServerPacketHandler || clientboundAllowedIds.contains(id_unsigned_byte))) {
                ret = createPacket(id_unsigned_byte);
                if (ret == null) {
                    throw new IOException(String.format("Bad packet id %d (createPacket returned null)", id_unsigned_byte));
                }

                ret.readPacketData(dis);
                return ret;
            } else {
                throw new IOException(
                    String.format(
                        "isServerPacketHandler && !serverboundAllowedIds.contains(%1$s) || !isServerPacketHandler && !clientboundAllowedIds.contains(%1$s) == true",
                        String.valueOf(id_unsigned_byte)
                    )
                );
            }
        } catch (EOFException e) {
            logger.warning("Reached end of stream");
            return null;
        }
    }

    public static void writePacket(Packet packet0, DataOutputStream dataOutputStream1) throws IOException {
        dataOutputStream1.write(packet0.getPacketId());
        packet0.writePacketData(dataOutputStream1);
    }

    public abstract void readPacketData(DataInputStream var1) throws IOException;

    public abstract void writePacketData(DataOutputStream var1) throws IOException;

    public abstract void processPacket(NetworkHandler var1);

    public abstract int getPacketSize();

    static {
        map(0, Packet.Flow.BOTH, KeepAlivePacket.class);
        map(1, Packet.Flow.BOTH, LoginPacket.class);
        map(2, Packet.Flow.BOTH, HandshakePacket.class);
        map(3, Packet.Flow.BOTH, ChatPacket.class);
        map(4, Packet.Flow.SERVER_TO_CLIENT, UpdateTimePacket.class);
        map(5, Packet.Flow.SERVER_TO_CLIENT, ItemEquipPacket.class);
        map(6, Packet.Flow.SERVER_TO_CLIENT, SpawnPacket.class);
        map(7, Packet.Flow.CLIENT_TO_SERVER, UsePacket.class);
        map(8, Packet.Flow.SERVER_TO_CLIENT, EntityHealthPacket.class);
        map(9, Packet.Flow.BOTH, RespawnPacket.class);
        map(10, Packet.Flow.BOTH, PlayerMovementPacket.class);
        map(11, Packet.Flow.BOTH, PlayerPositionPacket.class);
        map(12, Packet.Flow.BOTH, PlayerLookPacket.class);
        map(13, Packet.Flow.BOTH, PlayerTeleportPacket.class);
        map(14, Packet.Flow.CLIENT_TO_SERVER, DigPacket.class);
        map(15, Packet.Flow.CLIENT_TO_SERVER, PlacePacket.class);
        map(16, Packet.Flow.CLIENT_TO_SERVER, ItemSwitchPacket.class);
        map(17, Packet.Flow.CLIENT_TO_SERVER, SleepPacket.class);
        map(18, Packet.Flow.BOTH, AnimationPacket.class);
        map(19, Packet.Flow.CLIENT_TO_SERVER, EntityActionPacket.class);
        map(20, Packet.Flow.SERVER_TO_CLIENT, PlayerSpawnPacket.class);
        map(21, Packet.Flow.SERVER_TO_CLIENT, ItemPacket.class);
        map(22, Packet.Flow.SERVER_TO_CLIENT, PickupPacket.class);
        map(23, Packet.Flow.BOTH, EntitySpawnPacket.class);
        map(24, Packet.Flow.SERVER_TO_CLIENT, MobPacket.class);
        map(25, Packet.Flow.SERVER_TO_CLIENT, PaintingPacket.class);
        map(26, Packet.Flow.BOTH, PlayerStatePacket.class);
        map(27, Packet.Flow.CLIENT_TO_SERVER, PositionPacket.class);
        map(28, Packet.Flow.SERVER_TO_CLIENT, EntityVelocityPacket.class);
        map(29, Packet.Flow.SERVER_TO_CLIENT, RemoveEntityPacket.class);
        map(30, Packet.Flow.SERVER_TO_CLIENT, EntityMovementPacket.class);
        map(31, Packet.Flow.SERVER_TO_CLIENT, MovePacket.class);
        map(32, Packet.Flow.SERVER_TO_CLIENT, LookPacket.class);
        map(33, Packet.Flow.SERVER_TO_CLIENT, MoveLookPacket.class);
        map(34, Packet.Flow.SERVER_TO_CLIENT, TeleportPacket.class);
        map(35, Packet.Flow.SERVER_TO_CLIENT, RotationPacket.class);
        map(36, Packet.Flow.SERVER_TO_CLIENT, ServerIconPacket.class);
        map(38, Packet.Flow.SERVER_TO_CLIENT, EntityStatePacket.class);
        map(39, Packet.Flow.SERVER_TO_CLIENT, RideEntityPacket.class);
        map(40, Packet.Flow.SERVER_TO_CLIENT, EntityMetadataPacket.class);
        map(41, Packet.Flow.SERVER_TO_CLIENT, EntityEffectPacket.class);
        map(42, Packet.Flow.SERVER_TO_CLIENT, RemoveEffectPacket.class);
        map(50, Packet.Flow.SERVER_TO_CLIENT, PrepChunkPacket.class);
        map(51, Packet.Flow.SERVER_TO_CLIENT, UpdateChunkPacket.class);
        map(52, Packet.Flow.SERVER_TO_CLIENT, UpdateSectionPacket.class);
        map(53, Packet.Flow.SERVER_TO_CLIENT, UpdateBlockPacket.class);
        map(54, Packet.Flow.SERVER_TO_CLIENT, PlayNotePacket.class);
        map(60, Packet.Flow.SERVER_TO_CLIENT, ExplosionPacket.class);
        map(61, Packet.Flow.SERVER_TO_CLIENT, BlockEventPacket.class);
        map(69, Packet.Flow.SERVER_TO_CLIENT, MusicPacket.class);
        map(70, Packet.Flow.SERVER_TO_CLIENT, WorldEventPacket.class);
        map(91, Packet.Flow.BOTH, GeneratorPacket.class);
        map(100, Packet.Flow.SERVER_TO_CLIENT, ContainerOpenPacket.class);
        map(101, Packet.Flow.BOTH, ContainerClosePacket.class);
        map(102, Packet.Flow.CLIENT_TO_SERVER, ClickPacket.class);
        map(103, Packet.Flow.SERVER_TO_CLIENT, SlotPacket.class);
        map(104, Packet.Flow.SERVER_TO_CLIENT, ContainerContentsPacket.class);
        map(105, Packet.Flow.SERVER_TO_CLIENT, ContainerDataPacket.class);
        map(106, Packet.Flow.BOTH, TransactionPacket.class);
        map(130, Packet.Flow.BOTH, SignPacket.class);
        map(131, Packet.Flow.BOTH, MapPacket.class);
        map(133, Packet.Flow.BOTH, MobSpawnerPacket.class);
        map(192, Packet.Flow.CLIENT_TO_SERVER, PingPacket.class);
        map(201, Packet.Flow.SERVER_TO_CLIENT, PlayerInfoPacket.class);
        map(202, Packet.Flow.SERVER_TO_CLIENT, OpPacket.class);
        map(239, Packet.Flow.SERVER_TO_CLIENT, AttackDirectionPacket.class);
        map(250, Packet.Flow.BOTH, CustomPayloadPacket.class);
        map(255, Packet.Flow.BOTH, DisconnectPacket.class);
    }

    public enum Flow {
        SERVER_TO_CLIENT,
        CLIENT_TO_SERVER,
        BOTH;
    }
}
