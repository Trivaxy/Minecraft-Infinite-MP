package net.minecraft.client.network;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.multiplayer.DisconnectedScreen;
import net.minecraft.client.gui.screens.multiplayer.LoginScreen;
import net.minecraft.client.gui.screens.title.TitleScreen;
import net.minecraft.client.network.player.MultiplayerMode;
import net.minecraft.client.network.player.RemotePlayer;
import net.minecraft.client.network.world.NetworkWorld;
import net.minecraft.client.particle.special.PickupAnimation;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.world.WorldType;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.block.tileentity.container.BarrelEntity;
import net.minecraft.game.block.tileentity.container.BookcaseEntity;
import net.minecraft.game.block.tileentity.other.SignEntity;
import net.minecraft.game.block.tileentity.other.SpawnerEntity;
import net.minecraft.game.block.tileentity.smelters.BlastFurnaceEntity;
import net.minecraft.game.block.tileentity.smelters.FurnaceEntity;
import net.minecraft.game.block.tileentity.smelters.GeneratorEntity;
import net.minecraft.game.container.Container;
import net.minecraft.game.container.inventory.BasicInventory;
import net.minecraft.game.entity.DataSyncer;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.EntityManager;
import net.minecraft.game.entity.arrow.Arrow;
import net.minecraft.game.entity.boat.Boat;
import net.minecraft.game.entity.hanging.Frame;
import net.minecraft.game.entity.hanging.Painting;
import net.minecraft.game.entity.minecart.EngineMinecart;
import net.minecraft.game.entity.minecart.ExplosiveMinecart;
import net.minecraft.game.entity.minecart.Minecart;
import net.minecraft.game.entity.minecart.StorageMinecart;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.misc.FallingBlock;
import net.minecraft.game.entity.misc.LightningBolt;
import net.minecraft.game.entity.misc.LingeringCloud;
import net.minecraft.game.entity.misc.PrimedTNT;
import net.minecraft.game.entity.misc.VoidCrystal;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.monster.Monster;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.entity.projectile.Bobber;
import net.minecraft.game.entity.projectile.Projectile;
import net.minecraft.game.entity.projectile.ProjectileType;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.misc.MapItem;
import net.minecraft.game.item.potion.EffectData;
import net.minecraft.game.world.World;
import net.minecraft.game.world.chunk.Chunk;
import net.minecraft.game.world.util.Explosion;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.container.ContainerClosePacket;
import net.minecraft.network.packet.container.ContainerContentsPacket;
import net.minecraft.network.packet.container.ContainerDataPacket;
import net.minecraft.network.packet.container.ContainerOpenPacket;
import net.minecraft.network.packet.container.CustomPayloadPacket;
import net.minecraft.network.packet.container.GeneratorPacket;
import net.minecraft.network.packet.container.PlayNotePacket;
import net.minecraft.network.packet.container.SignPacket;
import net.minecraft.network.packet.entity.EntityEffectPacket;
import net.minecraft.network.packet.entity.EntityHealthPacket;
import net.minecraft.network.packet.entity.EntityMetadataPacket;
import net.minecraft.network.packet.entity.EntityMovementPacket;
import net.minecraft.network.packet.entity.EntitySpawnPacket;
import net.minecraft.network.packet.entity.EntityStatePacket;
import net.minecraft.network.packet.entity.EntityVelocityPacket;
import net.minecraft.network.packet.entity.MobPacket;
import net.minecraft.network.packet.entity.RemoveEffectPacket;
import net.minecraft.network.packet.entity.RideEntityPacket;
import net.minecraft.network.packet.misc.ChatPacket;
import net.minecraft.network.packet.misc.DisconnectPacket;
import net.minecraft.network.packet.misc.HandshakePacket;
import net.minecraft.network.packet.misc.KeepAlivePacket;
import net.minecraft.network.packet.misc.LoginPacket;
import net.minecraft.network.packet.misc.MusicPacket;
import net.minecraft.network.packet.misc.OpPacket;
import net.minecraft.network.packet.misc.PaintingPacket;
import net.minecraft.network.packet.misc.TransactionPacket;
import net.minecraft.network.packet.player.AnimationPacket;
import net.minecraft.network.packet.player.AttackDirectionPacket;
import net.minecraft.network.packet.player.ItemEquipPacket;
import net.minecraft.network.packet.player.ItemPacket;
import net.minecraft.network.packet.player.PickupPacket;
import net.minecraft.network.packet.player.PlayerInfoPacket;
import net.minecraft.network.packet.player.PlayerMovementPacket;
import net.minecraft.network.packet.player.PlayerSpawnPacket;
import net.minecraft.network.packet.player.PlayerStatePacket;
import net.minecraft.network.packet.player.ScorePacket;
import net.minecraft.network.packet.player.SleepPacket;
import net.minecraft.network.packet.player.SlotPacket;
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
import net.minecraft.util.math.MathHelper;
import org.json.JSONObject;

public class ClientHandler extends NetworkHandler {
    private Minecraft mc;
    private NetworkManager connection;
    private NetworkWorld world;
    private Random rand = new Random();
    private Map<String, ClientHandler.PlayerInfo> playersInfo = new HashMap<>();
    public List<ClientHandler.PlayerInfo> playerNames = new ArrayList<>();
    public String serverField;
    private boolean disconnected = false;
    private boolean playerMoving = false;
    public int maxPlayers = 20;

    public ClientHandler(Minecraft mc, String hostname, int port) throws UnknownHostException, IOException {
        this.mc = mc;
        this.connection = new NetworkManager(new Socket(InetAddress.getByName(hostname), port), "Client", this);
    }

    public void process() {
        if (!this.disconnected) {
            this.connection.update();
        }

        this.connection.stop();
    }

    @Override
    public void handleLogin(LoginPacket packet) {
        this.mc.gamemode = new MultiplayerMode(this.mc, this);
        WorldType worldType = WorldType.fromNumber(packet.worldType);
        Biome singleBiome = worldType == WorldType.BIOME ? Biome.findById(packet.singleBiomeId) : null;
        this.world = new NetworkWorld(this, packet.mapSeed, packet.dimension, worldType, singleBiome);
        this.world.isOnline = true;
        this.mc.changeWorld(this.world, "");
        this.mc.setScreen(new LoginScreen(this));
        this.mc.player.entityId = packet.protocolVersion;
        this.maxPlayers = packet.maxPlayers;
        this.world.setMaxSections(packet.maxSections);
    }

    @Override
    public void handleItemSpawn(ItemPacket packet) {
        double x = packet.xPosition / 32.0;
        double y = packet.yPosition / 32.0;
        double z = packet.zPosition / 32.0;
        DroppedItem item = new DroppedItem(this.world, x, y, z, packet.itemStack);
        item.motionX = packet.rotation / 128.0;
        item.motionY = packet.pitch / 128.0;
        item.motionZ = packet.roll / 128.0;
        item.serverX = packet.xPosition;
        item.serverY = packet.yPosition;
        item.serverZ = packet.zPosition;
        this.world.addEntityToMemory(packet.entityId, item);
    }

    @Override
    public void handleEntitySpawn(EntitySpawnPacket packt) {
        double x = packt.xPosition / 32.0;
        double y = packt.yPosition / 32.0;
        double z = packt.zPosition / 32.0;
        if (packt.type == EntitySpawnPacket.EntityType.LIGHTNING) {
            this.world.addWeatherEffect(new LightningBolt(this.world, x, y, z));
            return;
        }

        Entity entity = null;
        switch (packt.type) {
            case MINECART:
                if (packt.metadata == 1) {
                    entity = new StorageMinecart(this.world, x, y, z);
                } else if (packt.metadata == 2) {
                    entity = new EngineMinecart(this.world, x, y, z);
                } else if (packt.metadata == 3) {
                    entity = new ExplosiveMinecart(this.world, x, y, z);
                } else {
                    entity = new Minecart(this.world, x, y, z);
                }
                break;
            case BOAT:
                entity = new Boat(this.world, x, y, z);
                break;
            case ARROW:
                entity = new Arrow(this.world, x, y, z);
                break;
            case TNT:
                entity = new PrimedTNT(this.world, x, y, z);
                break;
            case FALLING_BLOCK:
                entity = new FallingBlock(this.world, x, y, z, packt.metadata);
                break;
            case BOBBER:
                entity = new Bobber(this.world, x, y, z);
                break;
            case FRAME:
                entity = new Frame(this.world, (int)x, (int)y, (int)z, packt.ownerId, false);
                break;
            case PROJECTILE:
                Projectile projectile = ProjectileType.byId(packt.metadata).create(this.world);
                projectile.setPosition(x, y, z);
                entity = projectile;
                break;
            case CLOUD:
                LingeringCloud cloud = new LingeringCloud(this.world);
                cloud.setPosition(x, y, z);
                cloud.applySpawnData(packt.metadata);
                entity = cloud;
                break;
            case CRYSTAL:
                VoidCrystal crystal = new VoidCrystal(this.world, x, y, z);
                crystal.playerPlaced = packt.metadata == 1;
                entity = crystal;
        }

        if (entity != null) {
            entity.serverX = packt.xPosition;
            entity.serverY = packt.yPosition;
            entity.serverZ = packt.zPosition;
            entity.yaw = 0.0F;
            entity.pitch = 0.0F;
            entity.entityId = packt.entityId;
            this.world.addEntityToMemory(packt.entityId, entity);
            if (packt.ownerId > 0) {
                switch (packt.type) {
                    case ARROW:
                        Entity entityToSpawnxx = this.getEntityByID(packt.ownerId);
                        if (entityToSpawnxx instanceof Mob) {
                            ((Arrow)entity).shooter = (Mob)entityToSpawnxx;
                        }
                    case TNT:
                    case FALLING_BLOCK:
                    default:
                        break;
                    case BOBBER:
                        Entity entityToSpawn = this.getEntityByID(packt.ownerId);
                        if (entityToSpawn instanceof Mob) {
                            ((Bobber)entity).angler = (Player)entityToSpawn;
                            ((Player)entityToSpawn).bobber = (Bobber)entity;
                        }
                        break;
                    case FRAME:
                        entity.yaw = ((Frame)entity).direction * 90;
                        ((Frame)entity).setItemRotation(packt.metadata);
                        ((Frame)entity).setDisplayedItem(packt.stack);
                        break;
                    case PROJECTILE:
                        Entity entityToSpawnx = this.getEntityByID(packt.ownerId);
                        if (entityToSpawnx instanceof Mob) {
                            ((Projectile)entity).thrower = (Mob)entityToSpawnx;
                        }
                }

                entity.setVelocity(packt.motionX / 8000.0, packt.motionY / 8000.0, packt.motionZ / 8000.0);
            }
        }
    }

    @Override
    public void handlePlayerState(PlayerStatePacket packet) {
        World world = this.mc.world;
        Player player = null;

        for (int playerId = 0; playerId < world.players.size(); playerId++) {
            if (world.players.get(playerId).entityId == packet.entityId) {
                player = world.players.get(playerId);
            }
        }

        if (player != null && player.entityId != this.mc.player.entityId) {
            if (packet.type == 2) {
                player.crawling = packet.state;
                player.setCrawling(packet.state);
            } else {
                player.sneaking = packet.state;
            }
        }
    }

    @Override
    public void handlePaintingPlacement(PaintingPacket packet) {
        Painting painting = new Painting(this.world, packet.xPosition, packet.yPosition, packet.zPosition, packet.direction, packet.title);
        this.world.addEntityToMemory(packet.entityId, painting);
    }

    @Override
    public void handleEntityVelocity(EntityVelocityPacket packet) {
        Entity entity = this.getEntityByID(packet.entityId);
        if (entity != null) {
            entity.setVelocity(packet.motionX / 8000.0, packet.motionY / 8000.0, packet.motionZ / 8000.0);
        }
    }

    @Override
    public void handleEntityMetadata(EntityMetadataPacket packet) {
        Entity entity = this.getEntityByID(packet.entityId);
        if (entity != null && packet.getObjects() != null) {
            entity.getDataSync().updateObjects(packet.getObjects());
        }
    }

    @Override
    public void handlePlayerSpawn(PlayerSpawnPacket packet) {
        double x = packet.xPosition / 32.0;
        double y = packet.yPosition / 32.0;
        double z = packet.zPosition / 32.0;
        float yaw = packet.yaw * 360 / 256.0F;
        float pitch = packet.pitch * 360 / 256.0F;
        RemotePlayer player = new RemotePlayer(this.mc.world, packet.name);
        player.op = packet.serverOp;
        player.prevX = player.lastTickPosX = player.serverX = packet.xPosition;
        player.prevY = player.lastTickPosY = player.serverY = packet.yPosition;
        player.prevZ = player.lastTickPosZ = player.serverZ = packet.zPosition;
        player.teleportTo(x, y, z, yaw, pitch);
        this.world.addEntityToMemory(packet.entityId, player);
    }

    @Override
    public void handleEntityTeleport(TeleportPacket packet) {
        Entity entity = this.getEntityByID(packet.entityId);
        if (entity != null) {
            entity.serverX = packet.xPosition;
            entity.serverY = packet.yPosition;
            entity.serverZ = packet.zPosition;
            double x = entity.serverX / 32.0;
            double y = entity.serverY / 32.0 + 0.015625;
            double z = entity.serverZ / 32.0;
            float yaw = packet.yaw * 360 / 256.0F;
            float pitch = packet.pitch * 360 / 256.0F;
            entity.teleportInServer(x, y, z, yaw, pitch, 3);
        }
    }

    @Override
    public void handleEntityMovement(EntityMovementPacket packet) {
        Entity entity = this.getEntityByID(packet.entityId);
        if (entity != null) {
            entity.serverX = entity.serverX + packet.xPosition;
            entity.serverY = entity.serverY + packet.yPosition;
            entity.serverZ = entity.serverZ + packet.zPosition;
            double x = entity.serverX / 32.0;
            double y = entity.serverY / 32.0;
            double z = entity.serverZ / 32.0;
            float yaw = packet.rotating ? packet.yaw * 360 / 256.0F : entity.yaw;
            float pitch = packet.rotating ? packet.pitch * 360 / 256.0F : entity.pitch;
            entity.teleportInServer(x, y, z, yaw, pitch, 3);
        }
    }

    @Override
    public void handleRemoveEntity(RemoveEntityPacket packet) {
        this.world.removeFromWorld(packet.entityId);
    }

    @Override
    public void handlePlayerMovement(PlayerMovementPacket packet) {
        LocalPlayer player = this.mc.player;
        double x = player.posX;
        double y = player.posY;
        double z = player.posZ;
        float yaw = player.yaw;
        float pitch = player.pitch;
        if (packet.moving) {
            x = packet.xPosition;
            y = packet.yPosition;
            z = packet.zPosition;
        }

        if (packet.rotating) {
            yaw = packet.yaw;
            pitch = packet.pitch;
        }

        player.colHeight = 0.0F;
        player.motionX = player.motionY = player.motionZ = 0.0;
        player.teleportTo(x, y, z, yaw, pitch);
        packet.xPosition = player.posX;
        packet.yPosition = player.boundingBox.minY;
        packet.zPosition = player.posZ;
        packet.stance = player.posY;
        this.connection.sendPacket(packet);
        if (!this.playerMoving) {
            this.mc.player.prevX = this.mc.player.posX;
            this.mc.player.prevY = this.mc.player.posY;
            this.mc.player.prevZ = this.mc.player.posZ;
            this.playerMoving = true;
            this.mc.setScreen(null);
        }
    }

    @Override
    public void handleChunkPrep(PrepChunkPacket packet) {
        this.world.doPreChunk(packet.xPosition, packet.yPosition, packet.mode);
    }

    @Override
    public void handleSectionUpdate(UpdateSectionPacket packet) {
        Chunk chunk = this.world.getChunk(packet.xPosition, packet.zPosition);
        int x = packet.xPosition * 16;
        int z = packet.zPosition * 16;

        for (int id = 0; id < packet.sizeOfAllArrays; id++) {
            int coords = packet.coordinateArray[id];
            short type = packet.typeArray[id];
            short metadata = packet.metadataArray[id];
            int chunkWidth = coords >> 20 & 15;
            int chunkLength = coords >> 16 & 15;
            int chunkHeight = coords & 0xFFFF;
            chunk.setBlockAndState(chunkWidth, chunkHeight, chunkLength, type, metadata);
            this.world.clearBlockUpdates(chunkWidth + x, chunkHeight, chunkLength + z, chunkWidth + x, chunkHeight, chunkLength + z);
            this.world.markBlocksDirty(chunkWidth + x, chunkHeight, chunkLength + z, chunkWidth + x, chunkHeight, chunkLength + z);
        }
    }

    @Override
    public void handleChunkUpdate(UpdateChunkPacket packet) {
        this.world.clearBlockUpdates(packet.xCh << 4, 0, packet.zCh << 4, (packet.xCh << 4) + 15, this.world.getWorldHeight(), (packet.zCh << 4) + 15);
        Chunk chunk = this.world.getChunk(packet.xCh, packet.zCh);
        if (packet.includeInitialize && chunk == null) {
            this.world.doPreChunk(packet.xCh, packet.zCh, true);
            chunk = this.world.getChunk(packet.xCh, packet.zCh);
        }

        if (chunk != null) {
            chunk.setChunkData(packet.chunkData, packet.hasLowerBlocks, packet.hasUpperBlocks, packet.includeInitialize);
            this.world.markBlocksDirty(packet.xCh << 4, 0, packet.zCh << 4, (packet.xCh << 4) + 15, this.world.getWorldHeight(), (packet.zCh << 4) + 15);
        }
    }

    @Override
    public void handleBlockUpdate(UpdateBlockPacket packet) {
        this.world.updateBlocksAndNeighbors(packet.xPosition, packet.yPosition, packet.zPosition, packet.type, packet.metadata);
    }

    @Override
    public void handleDisconnect(DisconnectPacket packet255KickDisconnect1) {
        this.connection.networkShutdown("disconnect.kicked");
        this.disconnected = true;
        this.mc.changeWorld(null, "");
        this.mc.setScreen(new DisconnectedScreen(new TitleScreen(), "disconnect.disconnected", "disconnect.genericReason", packet255KickDisconnect1.reason));
    }

    @Override
    public void handleErrorMessage(String error, Object... format) {
        if (!this.disconnected) {
            this.disconnected = true;
            this.mc.clearWorld();
            this.mc.setScreen(new DisconnectedScreen(new TitleScreen(), "disconnect.lost", error, format));
        }
    }

    public void disconnect(Packet packet) {
        if (!this.disconnected) {
            this.connection.sendPacket(packet);
            this.connection.closeConnection();
        }
    }

    public void sendPacket(Packet packet1) {
        if (!this.disconnected) {
            this.connection.sendPacket(packet1);
        }
    }

    @Override
    public void handleCollect(PickupPacket packet22Collect1) {
        Entity item = this.getEntityByID(packet22Collect1.collectedEntityId);
        Object owner = (Mob)this.getEntityByID(packet22Collect1.collectorEntityId);
        if (owner == null) {
            owner = this.mc.player;
        }

        if (item != null) {
            this.world.playEntitySound(item, "random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            this.mc.particleRenderer.addParticle(new PickupAnimation(this.mc.world, item, (Entity)owner, -0.5F));
            this.world.removeFromWorld(packet22Collect1.collectedEntityId);
        }
    }

    @Override
    public void handleChat(ChatPacket packet) {
        this.mc.HUD.chatOverlay.addChatMessage(packet.message);
    }

    @Override
    public void handleAnimation(AnimationPacket packet) {
        Entity entity = this.getEntityByID(packet.entityId);
        if (entity != null) {
            Player player = (Player)entity;
            if (packet.animate == 1) {
                player.swingItem();
            } else if (packet.animate == 2) {
                entity.hurtAnim();
            } else if (packet.animate == 3) {
                player = (Player)entity;
                player.wakeUpPlayer(false, false, false);
            } else if (packet.animate == 6) {
                player = (Player)entity;
                player.wakeUpPlayer(true, false, false);
            } else if (packet.animate == 4) {
                player = (Player)entity;
                player.swingOffhandItem();
            } else if (packet.animate == 5 && entity instanceof RemotePlayer) {
            }
        }
    }

    @Override
    public void handleSleep(SleepPacket packet) {
        Entity entity = this.getEntityByID(packet.entityID);
        if (entity != null && packet.state == 0) {
            // putToBed, not goToSleep: the server has decided, and re-running the checks here just
            // gives the client a second opinion to disagree with
            ((Player)entity).putToBed(packet.bedX, packet.bedY, packet.bedZ);
        }
    }

    @Override
    public void handleHandshake(HandshakePacket packet) {
        if (packet.username.equals("-")) {
            this.sendPacket(new LoginPacket(this.mc.session.username, SharedConstants.PROTOCOL_VERSION));
        } else if (!this.mc.session.canAuthenticate()) {
            this.connection
                .networkShutdown("disconnect.loginFailedInfo", "This server is in online mode. Launch from a launcher signed in to a Minecraft account.");
        } else {
            try {
                this.joinServer(packet.username);
                this.sendPacket(new LoginPacket(this.mc.session.username, SharedConstants.PROTOCOL_VERSION));
            } catch (Exception ex) {
                ex.printStackTrace();
                this.connection.networkShutdown("disconnect.genericReason", "Internal client error: " + ex.toString());
            }
        }
    }

    private void joinServer(String serverId) throws IOException {
        JSONObject body = new JSONObject();
        body.put("accessToken", this.mc.session.accessToken);
        body.put("selectedProfile", this.mc.session.uuid);
        body.put("serverId", serverId);

        HttpURLConnection http = (HttpURLConnection)new URL("https://sessionserver.mojang.com/session/minecraft/join").openConnection();
        http.setRequestMethod("POST");
        http.setRequestProperty("Content-Type", "application/json");
        http.setConnectTimeout(15000);
        http.setReadTimeout(15000);
        http.setDoOutput(true);

        OutputStream out = http.getOutputStream();
        out.write(body.toString().getBytes(StandardCharsets.UTF_8));
        out.close();

        int code = http.getResponseCode();
        http.disconnect();
        if (code != 204 && code != 200) {
            throw new IOException("Mojang rejected the join request (HTTP " + code + ")");
        }
    }

    public void shutdown() {
        this.disconnected = true;
        this.connection.stop();
        this.connection.networkShutdown("disconnect.closed");
    }

    @Override
    public void handleMobSpawn(MobPacket packet) {
        double x = packet.xPosition / 32.0;
        double y = packet.yPosition / 32.0;
        double z = packet.zPosition / 32.0;
        float yaw = packet.yaw * 360 / 256.0F;
        float pitch = packet.pitch * 360 / 256.0F;
        Mob mob = (Mob)EntityManager.createEntityFromId(packet.type, this.mc.world);
        mob.serverX = packet.xPosition;
        mob.serverY = packet.yPosition;
        mob.serverZ = packet.zPosition;
        mob.entityId = packet.entityId;
        mob.teleportTo(x, y, z, yaw, pitch);
        mob.isMultiplayer = true;
        this.world.addEntityToMemory(packet.entityId, mob);
        List<DataSyncer.DataItem> dataWatcher = packet.getMetadata();
        if (dataWatcher != null) {
            mob.getDataSync().updateObjects(dataWatcher);
        }
    }

    @Override
    public void handleUpdateTime(UpdateTimePacket packet) {
        this.mc.world.setTime(packet.time);
    }

    @Override
    public void handleWeather(WeatherPacket packet) {
        this.mc.world.getSettings().setIsRaining(packet.raining);
        this.mc.world.getSettings().setIsThundering(packet.thundering);
    }

    @Override
    public void handleSpawn(SpawnPacket packet) {
        this.mc.world.getSettings().setSpawn(packet.xPosition, packet.yPosition, packet.zPosition);
    }

    @Override
    public void handleRideEntity(RideEntityPacket packet) {
        Entity rider = this.getEntityByID(packet.entityId);
        Entity ridden = this.getEntityByID(packet.vehicleEntityId);
        if (packet.entityId == this.mc.player.entityId) {
            rider = this.mc.player;
        }

        if (rider != null) {
            rider.mountEntity(ridden);
        }
    }

    @Override
    public void handleEntityState(EntityStatePacket packet) {
        Entity entity = this.getEntityByID(packet.entityId);
        if (entity != null) {
            entity.updateState(packet.entityStatus);
        }
    }

    @Override
    public void handleHealth(EntityHealthPacket packet) {
        this.mc.player.setHealth(packet.healthMP);
        this.mc.player.getFoodStats().setHunger(packet.food);
        this.mc.player.getFoodStats().setSaturation(packet.foodSaturation);
    }

    @Override
    public void handleScore(ScorePacket packet) {
        this.mc.player.score = packet.score;
    }

    @Override
    public void handleRespawn(RespawnPacket packet) {
        this.mc.respawn(true);
    }

    @Override
    public void handleMusic(MusicPacket packet) {
        String trackName = packet.string1;
        if (trackName.equals("null")) {
            trackName = null;
        }

        this.world.playRecord(trackName, packet.x, packet.y, packet.z, packet.stop);
    }

    @Override
    public void handleExplosion(ExplosionPacket packet) {
        Explosion explosion2 = new Explosion(this.mc.world, null, packet.explosionX, packet.explosionY, packet.explosionZ, packet.explosionSize);
        explosion2.destroyedBlocks = packet.destroyedBlockPositions;
        explosion2.addParticles(true);
    }

    @Override
    public void handleContainerOpen(ContainerOpenPacket packet) {
        switch (packet.inventoryType) {
            case 0:
                BasicInventory inventory = new BasicInventory(packet.windowTitle, packet.slotsCount);
                this.mc.player.displayGUIChest(inventory, false);
                this.mc.player.openContainer.windowId = packet.windowId;
                break;
            case 1:
                LocalPlayer player = this.mc.player;
                this.mc
                    .player
                    .displayWorkbenchGUI(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY), MathHelper.floor_double(player.posZ), true);
                this.mc.player.openContainer.windowId = packet.windowId;
                break;
            case 2:
                FurnaceEntity furnace = new FurnaceEntity();
                furnace.world = this.world;
                this.mc.player.displayGUIFurnace(furnace);
                this.mc.player.openContainer.windowId = packet.windowId;
                break;
            case 3:
                BarrelEntity barrel = new BarrelEntity();
                barrel.world = this.world;
                this.mc.player.displayGUIBarrel(barrel);
                this.mc.player.openContainer.windowId = packet.windowId;
                break;
            case 4:
                BookcaseEntity bookshelf = new BookcaseEntity();
                bookshelf.world = this.world;
                this.mc.player.displayGUIBookshelf(bookshelf);
                this.mc.player.openContainer.windowId = packet.windowId;
                break;
            case 5:
                GeneratorEntity generator = new GeneratorEntity();
                generator.world = this.world;
                generator.x = packet.xPosition;
                generator.y = packet.yPosition;
                generator.z = packet.zPosition;
                generator.active = packet.state;
                this.mc.player.displayGUIGenerator(generator);
                this.mc.player.openContainer.windowId = packet.windowId;
                break;
            case 6:
                BlastFurnaceEntity blastFurnace = new BlastFurnaceEntity();
                blastFurnace.world = this.world;
                this.mc.player.displayGUIFurnace(blastFurnace);
                this.mc.player.openContainer.windowId = packet.windowId;
        }
    }

    @Override
    public void handleContainerClose(ContainerClosePacket packet) {
        this.mc.player.closeScreen();
    }

    @Override
    public void handleSetSlot(SlotPacket packet) {
        if (packet.windowId == -1) {
            this.mc.player.inventory.setItemStack(packet.myItemStack);
        } else if (packet.windowId == 0 && packet.itemSlot >= 36 && packet.itemSlot < 45) {
            ItemStack stack = this.mc.player.container.getSlot(packet.itemSlot).getStack();
            if (packet.myItemStack != null && (stack == null || stack.stackSize < packet.myItemStack.stackSize)) {
                packet.myItemStack.animationsToGo = 5;
            }

            this.mc.player.container.putStackInSlot(packet.itemSlot, packet.myItemStack);
        } else if (packet.windowId == this.mc.player.openContainer.windowId) {
            this.mc.player.openContainer.putStackInSlot(packet.itemSlot, packet.myItemStack);
        }
    }

    @Override
    public void handleTransaction(TransactionPacket packet) {
        Container container = null;
        if (packet.windowId == 0) {
            container = this.mc.player.container;
        } else if (packet.windowId == this.mc.player.openContainer.windowId) {
            container = this.mc.player.openContainer;
        }

        if (container != null) {
            if (packet.transactionOngoing) {
                container.deleteBackup(packet.transactionId);
            } else {
                container.rollbackChanges(packet.transactionId);
                this.sendPacket(new TransactionPacket(packet.windowId, packet.transactionId, true));
            }
        }
    }

    @Override
    public void handleContainerContents(ContainerContentsPacket packet) {
        if (packet.windowId == 0) {
            this.mc.player.container.putStacksInSlots(packet.itemStack);
        } else if (packet.windowId == this.mc.player.openContainer.windowId) {
            this.mc.player.openContainer.putStacksInSlots(packet.itemStack);
        }
    }

    @Override
    public void handleSignUpdate(SignPacket packet) {
        if (this.mc.world.blockExists(packet.xPosition, packet.yPosition, packet.zPosition)) {
            TileEntity tileEntity = this.mc.world.getTileEntity(packet.xPosition, packet.yPosition, packet.zPosition);
            if (tileEntity instanceof SignEntity) {
                SignEntity sign = (SignEntity)tileEntity;

                for (int line = 0; line < 4; line++) {
                    sign.signText[line] = packet.signLines[line];
                }

                sign.onChanged();
            }
        }
    }

    @Override
    public void handleContainerData(ContainerDataPacket packet) {
        this.registerPacket(packet);
        if (this.mc.player.openContainer != null && this.mc.player.openContainer.windowId == packet.windowId) {
            this.mc.player.openContainer.setData(packet.id, packet.value);
        }
    }

    @Override
    public void handleItemEquip(ItemEquipPacket packet) {
        Entity entity = this.getEntityByID(packet.entityID);
        if (entity != null && packet.getItemSlot() != null) {
            entity.equipItem(packet.slot, packet.getItemSlot().itemID, packet.getItemSlot().itemDmg);
        } else {
            entity.equipItem(packet.slot, -1, -1);
        }

        if (entity instanceof Monster) {
            ((Monster)entity).armorInventory[packet.slot] = packet.getItemSlot();
        }
    }

    @Override
    public void handleNotePlay(PlayNotePacket packet) {
        this.mc.world.blockEvent(packet.xLocation, packet.yLocation, packet.zLocation, packet.instrument, packet.pitch);
    }

    @Override
    public void handleBlockEvent(BlockEventPacket packet) {
        this.mc.world.playEffect(packet.sfx, packet.x, packet.y, packet.z, packet.data);
    }

    @Override
    public void handleSound(SoundPacket packet) {
        if (packet.soundName != null && !packet.soundName.isEmpty()) {
            if (packet.soundType == SoundPacket.TYPE_BLOCK) {
                this.mc.sndManager.playBlockSound(packet.soundName, (float)packet.x, (float)packet.y, (float)packet.z, packet.volume, packet.pitch);
            } else if (packet.soundType == SoundPacket.TYPE_MOB) {
                this.mc.sndManager.playMobSound(packet.soundName, -1, (float)packet.x, (float)packet.y, (float)packet.z, packet.volume, packet.pitch);
            } else {
                this.mc.sndManager.playSound(packet.soundName, (float)packet.x, (float)packet.y, (float)packet.z, packet.volume, packet.pitch);
            }
        }
    }

    @Override
    public void handleEntityEffect(EntityEffectPacket packet) {
        Entity entity = this.getEntityByID(packet.entityId);
        if (entity != null && entity instanceof Mob) {
            ((Mob)entity).addEffect(new EffectData(packet.effectId, packet.duration, packet.effectAmp));
        }
    }

    @Override
    public void handleRemoveEffect(RemoveEffectPacket packet) {
        Entity entity = this.getEntityByID(packet.entityId);
        if (entity != null && entity instanceof Mob) {
            ((Mob)entity).removeEffect(packet.effectId);
        }
    }

    @Override
    public void handlePlayerInfo(PlayerInfoPacket packet) {
        ClientHandler.PlayerInfo info = this.playersInfo.get(packet.playerName);
        if (info == null && packet.isConnected) {
            info = new ClientHandler.PlayerInfo(packet.playerName);
            this.playersInfo.put(packet.playerName, info);
            this.playerNames.add(info);
        }

        if (info != null && !packet.isConnected) {
            this.playersInfo.remove(packet.playerName);
            this.playerNames.remove(info);
        }

        if (packet.isConnected && info != null) {
            info.ping = packet.ping;
        }
    }

    @Override
    public void handleGenerator(GeneratorPacket packet) {
        if (this.mc.world.getTileEntity(packet.xPosition, packet.yPosition, packet.zPosition) != null
            && this.mc.world.getTileEntity(packet.xPosition, packet.yPosition, packet.zPosition) instanceof GeneratorEntity) {
            ((GeneratorEntity)this.mc.world.getTileEntity(packet.xPosition, packet.yPosition, packet.zPosition)).active = packet.state;
        }
    }

    @Override
    public void handleGameEvent(WorldEventPacket packet) {
        int eventId = packet.eventType;
        if (eventId >= 0 && eventId < WorldEventPacket.clientMessage.length) {
            this.mc.player.addChatMessage(WorldEventPacket.clientMessage[eventId]);
        }
    }

    @Override
    public void handleAttackDirection(AttackDirectionPacket packet) {
        this.mc.player.attackAngle = packet.yaw;
    }

    @Override
    public void handleKeepAlive(KeepAlivePacket packet) {
        this.sendPacket(new KeepAlivePacket(packet.randomId));
    }

    @Override
    public void handleMapData(MapPacket packet) {
        if (packet.itemID == ItemList.map.id) {
            MapItem.getMPMapData(packet.uniqueID, this.mc.world).updateMPMapData(packet.itemData);
        }
    }

    @Override
    public void handleMobSpawner(MobSpawnerPacket packet) {
        if (this.mc.world.blockExists(packet.x, packet.y, packet.z)) {
            TileEntity tileEntity = this.mc.world.getTileEntity(packet.x, packet.y, packet.z);
            if (tileEntity instanceof SpawnerEntity) {
                SpawnerEntity spawner = (SpawnerEntity)tileEntity;
                spawner.setMob(packet.entityId);
                spawner.onChanged();
            }
        }
    }

    @Override
    public void handleOp(OpPacket packet) {
        Entity entity = this.world.entityInMemory(packet.entityId);
        if (entity != null && entity instanceof RemotePlayer) {
            ((RemotePlayer)entity).op = packet.state;
        }
    }

    @Override
    public void handleCustomPayload(CustomPayloadPacket packet) {
    }

    @Override
    public boolean isServerHandler() {
        return false;
    }

    private Entity getEntityByID(int id) {
        return id == this.mc.player.entityId ? this.mc.player : this.world.entityInMemory(id);
    }

    public class PlayerInfo {
        public final String name;
        public int ping;

        public PlayerInfo(String name) {
            this.name = name;
        }

        public boolean nameStartsWith(String name) {
            return this.name.toLowerCase().startsWith(name);
        }
    }
}
