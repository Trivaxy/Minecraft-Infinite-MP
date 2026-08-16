package net.minecraft.network;

import net.minecraft.network.packet.Packet;
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
import net.minecraft.network.packet.entity.MobPacket;
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
import net.minecraft.network.packet.player.PlayerMovementPacket;
import net.minecraft.network.packet.player.PlayerSpawnPacket;
import net.minecraft.network.packet.player.PlayerStatePacket;
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
import net.minecraft.network.packet.world.SoundPacket;
import net.minecraft.network.packet.world.SpawnPacket;
import net.minecraft.network.packet.world.TeleportPacket;
import net.minecraft.network.packet.world.UpdateBlockPacket;
import net.minecraft.network.packet.world.UpdateChunkPacket;
import net.minecraft.network.packet.world.UpdateSectionPacket;
import net.minecraft.network.packet.world.UpdateTimePacket;
import net.minecraft.network.packet.world.WeatherPacket;
import net.minecraft.network.packet.world.WorldEventPacket;

public abstract class NetworkHandler {
    public abstract boolean isServerHandler();

    public void handleChunkUpdate(UpdateChunkPacket packet51MapChunk1) {
        this.registerPacket(packet51MapChunk1);
    }

    public void registerPacket(Packet packet1) {
    }

    public void handleErrorMessage(String message, Object... format) {
    }

    public void handleDisconnect(DisconnectPacket packet255KickDisconnect1) {
        this.registerPacket(packet255KickDisconnect1);
    }

    public void handleLogin(LoginPacket packet1Login1) {
        this.registerPacket(packet1Login1);
    }

    public void handlePlayerMovement(PlayerMovementPacket packet10Flying1) {
        this.registerPacket(packet10Flying1);
    }

    public void handleSectionUpdate(UpdateSectionPacket packet52MultiBlockChange1) {
        this.registerPacket(packet52MultiBlockChange1);
    }

    public void handleBlockDig(DigPacket packet14BlockDig1) {
        this.registerPacket(packet14BlockDig1);
    }

    public void handleBlockUpdate(UpdateBlockPacket packet53BlockChange1) {
        this.registerPacket(packet53BlockChange1);
    }

    public void handleChunkPrep(PrepChunkPacket packet50PreChunk1) {
        this.registerPacket(packet50PreChunk1);
    }

    public void handlePlayerSpawn(PlayerSpawnPacket packet20NamedEntitySpawn1) {
        this.registerPacket(packet20NamedEntitySpawn1);
    }

    public void handleEntityMovement(EntityMovementPacket packet30Entity1) {
        this.registerPacket(packet30Entity1);
    }

    public void handleEntityTeleport(TeleportPacket packet34EntityTeleport1) {
        this.registerPacket(packet34EntityTeleport1);
    }

    public void handlePlace(PlacePacket packet15Place1) {
        this.registerPacket(packet15Place1);
    }

    public void handleBlockItemSwitch(ItemSwitchPacket packet16BlockItemSwitch1) {
        this.registerPacket(packet16BlockItemSwitch1);
    }

    public void handleRemoveEntity(RemoveEntityPacket packet29DestroyEntity1) {
        this.registerPacket(packet29DestroyEntity1);
    }

    public void handleItemSpawn(ItemPacket packet21PickupSpawn1) {
        this.registerPacket(packet21PickupSpawn1);
    }

    public void handleCollect(PickupPacket packet22Collect1) {
        this.registerPacket(packet22Collect1);
    }

    public void handleChat(ChatPacket packet3Chat1) {
        this.registerPacket(packet3Chat1);
    }

    public void handleEntitySpawn(EntitySpawnPacket packet23VehicleSpawn1) {
        this.registerPacket(packet23VehicleSpawn1);
    }

    public void handleAnimation(AnimationPacket packet18Animation1) {
        this.registerPacket(packet18Animation1);
    }

    public void handleEntityAction(EntityActionPacket packet19EntityAction1) {
        this.registerPacket(packet19EntityAction1);
    }

    public void handleSleep(SleepPacket var1) {
        this.registerPacket(var1);
    }

    public void handleHandshake(HandshakePacket packet2Handshake1) {
        this.registerPacket(packet2Handshake1);
    }

    public void handleMobSpawn(MobPacket packet24MobSpawn1) {
        this.registerPacket(packet24MobSpawn1);
    }

    public void handleUpdateTime(UpdateTimePacket packet4UpdateTime1) {
        this.registerPacket(packet4UpdateTime1);
    }

    public void handleSpawn(SpawnPacket packet6SpawnPosition1) {
        this.registerPacket(packet6SpawnPosition1);
    }

    public void handleEntityVelocity(EntityVelocityPacket packet28EntityVelocity1) {
        this.registerPacket(packet28EntityVelocity1);
    }

    public void handleEntityMetadata(EntityMetadataPacket packet40EntityMetadata1) {
        this.registerPacket(packet40EntityMetadata1);
    }

    public void handleRideEntity(RideEntityPacket packet39AttachEntity1) {
        this.registerPacket(packet39AttachEntity1);
    }

    public void handleUseEntity(UsePacket packet7UseEntity1) {
        this.registerPacket(packet7UseEntity1);
    }

    public void handleEntityState(EntityStatePacket packet38EntityStatus1) {
        this.registerPacket(packet38EntityStatus1);
    }

    public void handleHealth(EntityHealthPacket packet8UpdateHealth1) {
        this.registerPacket(packet8UpdateHealth1);
    }

    public void handleRespawn(RespawnPacket packet9Respawn1) {
        this.registerPacket(packet9Respawn1);
    }

    public void handleExplosion(ExplosionPacket packet60Explosion1) {
        this.registerPacket(packet60Explosion1);
    }

    public void handleContainerOpen(ContainerOpenPacket packet100OpenWindow1) {
        this.registerPacket(packet100OpenWindow1);
    }

    public void handleContainerClose(ContainerClosePacket packet101CloseWindow1) {
        this.registerPacket(packet101CloseWindow1);
    }

    public void handleWindowClick(ClickPacket packet102WindowClick1) {
        this.registerPacket(packet102WindowClick1);
    }

    public void handleSetSlot(SlotPacket packet103SetSlot1) {
        this.registerPacket(packet103SetSlot1);
    }

    public void handleContainerContents(ContainerContentsPacket packet104WindowItems1) {
        this.registerPacket(packet104WindowItems1);
    }

    public void handleSignUpdate(SignPacket packet130UpdateSign1) {
        this.registerPacket(packet130UpdateSign1);
    }

    public void handleContainerData(ContainerDataPacket packet105UpdateProgressbar1) {
        this.registerPacket(packet105UpdateProgressbar1);
    }

    public void handleItemEquip(ItemEquipPacket packet5PlayerInventory1) {
        this.registerPacket(packet5PlayerInventory1);
    }

    public void handleTransaction(TransactionPacket packet106Transaction1) {
        this.registerPacket(packet106Transaction1);
    }

    public void handlePaintingPlacement(PaintingPacket packet25EntityPainting1) {
        this.registerPacket(packet25EntityPainting1);
    }

    public void handleNotePlay(PlayNotePacket packet54PlayNoteBlock1) {
        this.registerPacket(packet54PlayNoteBlock1);
    }

    public void handlePosition(PositionPacket packet27Position1) {
        this.registerPacket(packet27Position1);
    }

    public void handleBlockEvent(BlockEventPacket packet61DoorChange1) {
        this.registerPacket(packet61DoorChange1);
    }

    public void handlePlayerInfo(PlayerInfoPacket par1Packet201PlayerInfo) {
        this.registerPacket(par1Packet201PlayerInfo);
    }

    public void handleKeepAlive(KeepAlivePacket packet0KeepAlive) {
        this.registerPacket(packet0KeepAlive);
    }

    public void handleGameEvent(WorldEventPacket var1) {
        this.registerPacket(var1);
    }

    public void handleWeather(WeatherPacket packet) {
        this.registerPacket(packet);
    }

    public void handleAttackDirection(AttackDirectionPacket attackedDirection) {
        this.registerPacket(attackedDirection);
    }

    public void handlePlayerState(PlayerStatePacket packet26PlayerState) {
        this.registerPacket(packet26PlayerState);
    }

    public void handleOp(OpPacket packetUpdateOpState) {
        this.registerPacket(packetUpdateOpState);
    }

    public void handleGenerator(GeneratorPacket packet91ActivateGenerator) {
        this.registerPacket(packet91ActivateGenerator);
    }

    public void handleMapData(MapPacket packet131MapData) {
        this.registerPacket(packet131MapData);
    }

    public void handleCustomPayload(CustomPayloadPacket packet250CustomPayload) {
        this.registerPacket(packet250CustomPayload);
    }

    public void handleMusic(MusicPacket packet69PlayShit) {
        this.registerPacket(packet69PlayShit);
    }

    public void handleEntityEffect(EntityEffectPacket packet41EntityEffect) {
        this.registerPacket(packet41EntityEffect);
    }

    public void handleRemoveEffect(RemoveEffectPacket packet42RemoveEntityEffect) {
        this.registerPacket(packet42RemoveEntityEffect);
    }

    public void handleMobSpawner(MobSpawnerPacket packet132MobSpawnerData) {
        this.registerPacket(packet132MobSpawnerData);
    }

    public void handleEntityRotation(RotationPacket packet35EntityHeadRotation) {
        this.registerPacket(packet35EntityHeadRotation);
    }

    public void handlePing(PingPacket packet) {
        this.registerPacket(packet);
    }

    public void handleServerIcon(ServerIconPacket packet) {
        this.registerPacket(packet);
    }

    public void handleSound(SoundPacket packet) {
        this.registerPacket(packet);
    }
}
