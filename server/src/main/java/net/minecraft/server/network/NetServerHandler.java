package net.minecraft.server.network;

import com.mojang.nbt.tag.special.StringTag;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandOutput;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.tileentity.other.SignEntity;
import net.minecraft.game.block.tileentity.smelters.GeneratorEntity;
import net.minecraft.game.container.slot.Slot;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.misc.Book;
import net.minecraft.game.item.misc.WrittenBook;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.world.util.Position;
import net.minecraft.localization.Translator;
import net.minecraft.network.NetworkHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.packet.player.AnimationPacket;
import net.minecraft.network.packet.misc.ChatPacket;
import net.minecraft.network.packet.container.ContainerClosePacket;
import net.minecraft.network.packet.container.CustomPayloadPacket;
import net.minecraft.network.packet.player.DigPacket;
import net.minecraft.network.packet.misc.DisconnectPacket;
import net.minecraft.network.packet.entity.EntityActionPacket;
import net.minecraft.network.packet.container.GeneratorPacket;
import net.minecraft.network.packet.misc.KeepAlivePacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.player.ClickPacket;
import net.minecraft.network.packet.player.PlacePacket;
import net.minecraft.network.packet.player.ItemSwitchPacket;
import net.minecraft.network.packet.entity.PositionPacket;
import net.minecraft.network.packet.player.UsePacket;
import net.minecraft.network.packet.player.PlayerMovementPacket;
import net.minecraft.network.packet.player.PlayerStatePacket;
import net.minecraft.network.packet.player.PlayerTeleportPacket;
import net.minecraft.network.packet.world.RespawnPacket;
import net.minecraft.network.packet.container.SignPacket;
import net.minecraft.network.packet.container.CreativeSlotPacket;
import net.minecraft.network.packet.player.SlotPacket;
import net.minecraft.network.packet.misc.TransactionPacket;
import net.minecraft.network.packet.world.UpdateBlockPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerConfigurationManager;
import net.minecraft.server.player.EntityPlayerMP;
import net.minecraft.server.player.bans.BanEntry;
import net.minecraft.server.world.WorldServer;
import net.minecraft.util.io.IOUtil;
import net.minecraft.util.logging.LoggingUtils;
import net.minecraft.util.math.MathHelper;

public class NetServerHandler extends NetworkHandler implements CommandOutput {
    public static Logger logger = Logger.getLogger("Minecraft");
    public NetworkManager netManager;
    public boolean connectionClosed = false;
    private MinecraftServer mcServer;
    private EntityPlayerMP playerEntity;
    private int lastHealth;
    private int currentHealth;
    private int playerInAirTime;
    private double lastPosX;
    private double lastPosY;
    private double lastPosZ;
    private boolean hasMoved = true;
    private int lastPingId;
    private long oPingId;
    private static final Random RANDOM = new Random();
    private Map<Integer, Short> idMap = new HashMap<>();

    public NetServerHandler(MinecraftServer minecraftServer1, NetworkManager networkManager2, EntityPlayerMP entityPlayerMP3) {
        this.mcServer = minecraftServer1;
        this.netManager = networkManager2;
        networkManager2.setNetHandler(this);
        this.playerEntity = entityPlayerMP3;
        entityPlayerMP3.playerNetServerHandler = this;
    }

    public void handlePackets() {
        this.netManager.update();
        if (this.lastHealth - this.currentHealth > 20) {
            this.currentHealth = this.lastHealth;
            this.oPingId = System.nanoTime() / 1000000L;
            this.lastPingId = RANDOM.nextInt();
            this.sendPacket(new KeepAlivePacket(this.lastPingId));
        }
    }

    public void kickPlayer(String string1) {
        this.playerEntity.updateRidingState();
        this.sendPacket(new DisconnectPacket(string1));
        this.netManager.closeConnection();
        this.mcServer.configManager.sendPacketToAll(new ChatPacket("§e" + this.playerEntity.username + " left the game."));
        this.mcServer.configManager.playerLoggedOut(this.playerEntity);
        this.connectionClosed = true;
    }

    @Override
    public void handlePlayerState(PlayerStatePacket packet26PlayerState) {
        if (packet26PlayerState.type == 2) {
            this.playerEntity.crawling = packet26PlayerState.state;
            this.playerEntity.setCrawling(packet26PlayerState.state);
        } else {
            this.playerEntity.sneaking = packet26PlayerState.state;
        }

        this.mcServer.configManager.sendPacketToAll(packet26PlayerState);
    }

    @Override
    public void handlePosition(PositionPacket packet27Position1) {
        this.playerEntity
            .setMovementType(
                packet27Position1.getStrafeMovement(),
                packet27Position1.getForwardMov(),
                packet27Position1.getState(),
                packet27Position1.getPitchRot(),
                packet27Position1.getYawRot()
            );
    }

    @Override
    public void handlePlayerMovement(PlayerMovementPacket packet10Flying1) {
        WorldServer worldServer2 = this.mcServer.getWorldManager(this.playerEntity.dimension);
        if (!this.hasMoved) {
            double d3 = packet10Flying1.yPosition - this.lastPosY;
            if (packet10Flying1.xPosition == this.lastPosX && d3 * d3 < 0.01 && packet10Flying1.zPosition == this.lastPosZ) {
                this.hasMoved = true;
            }
        }

        if (this.hasMoved) {
            if (this.playerEntity.mount != null) {
                float f26 = this.playerEntity.yaw;
                float f4 = this.playerEntity.pitch;
                this.playerEntity.mount.updateRiderPosition();
                double d5 = this.playerEntity.posX;
                double d7 = this.playerEntity.posY;
                double d9 = this.playerEntity.posZ;
                double d27 = 0.0;
                double d13 = 0.0;
                if (packet10Flying1.rotating) {
                    f26 = packet10Flying1.yaw;
                    this.playerEntity.headFacing = f26;
                    f4 = packet10Flying1.pitch;
                }

                if (packet10Flying1.moving && packet10Flying1.yPosition == -999.0 && packet10Flying1.stance == -999.0) {
                    d27 = packet10Flying1.xPosition;
                    d13 = packet10Flying1.zPosition;
                }

                this.playerEntity.onGround = packet10Flying1.onGround;
                this.playerEntity.onUpdateEntity(true);
                this.playerEntity.moveEntity(d27, 0.0, d13);
                this.playerEntity.teleportTo(d5, d7, d9, f26, f4);
                this.playerEntity.motionX = d27;
                this.playerEntity.motionZ = d13;
                if (this.playerEntity.mount != null) {
                    worldServer2.moveEntity(this.playerEntity.mount, true);
                }

                if (this.playerEntity.mount != null) {
                    this.playerEntity.mount.updateRiderPosition();
                }

                this.mcServer.configManager.setPositionUponLogin(this.playerEntity);
                this.lastPosX = this.playerEntity.posX;
                this.lastPosY = this.playerEntity.posY;
                this.lastPosZ = this.playerEntity.posZ;
                worldServer2.tickEntity(this.playerEntity);
                return;
            }

            if (this.playerEntity.isPlayerSleeping()) {
                this.playerEntity.onUpdateEntity(true);
                this.playerEntity.teleportTo(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.yaw, this.playerEntity.pitch);
                worldServer2.tickEntity(this.playerEntity);
                return;
            }

            double d3 = this.playerEntity.posY;
            this.lastPosX = this.playerEntity.posX;
            this.lastPosY = this.playerEntity.posY;
            this.lastPosZ = this.playerEntity.posZ;
            double d5 = this.playerEntity.posX;
            double d7 = this.playerEntity.posY;
            double d9 = this.playerEntity.posZ;
            float f11 = this.playerEntity.yaw;
            float f12 = this.playerEntity.pitch;
            if (packet10Flying1.moving && packet10Flying1.yPosition == -999.0 && packet10Flying1.stance == -999.0) {
                packet10Flying1.moving = false;
            }

            if (packet10Flying1.moving) {
                d5 = packet10Flying1.xPosition;
                d7 = packet10Flying1.yPosition;
                d9 = packet10Flying1.zPosition;
                double d13 = packet10Flying1.stance - packet10Flying1.yPosition;
            }

            if (packet10Flying1.rotating) {
                f11 = packet10Flying1.yaw;
                this.playerEntity.headFacing = f11;
                f12 = packet10Flying1.pitch;
            }

            this.playerEntity.onUpdateEntity(true);
            this.playerEntity.colHeight = 0.0F;
            this.playerEntity.teleportTo(this.lastPosX, this.lastPosY, this.lastPosZ, f11, f12);
            if (!this.hasMoved) {
                return;
            }

            double d13 = d5 - this.playerEntity.posX;
            double d15 = d7 - this.playerEntity.posY;
            double d17 = d9 - this.playerEntity.posZ;
            double d19 = d13 * d13 + d15 * d15 + d17 * d17;
            if (d19 > 100.0) {
                logger.warning(this.playerEntity.username + " moved too quickly!");
                this.kickPlayer("You moved too quickly :( (Hacking?)");
                return;
            }

            double f21 = 0.0625;
            boolean z22 = worldServer2.getBoundingBoxes(this.playerEntity, this.playerEntity.boundingBox.copy().shrink(f21, f21, f21)).size() == 0;
            this.playerEntity.moveEntity(d13, d15, d17);
            d13 = d5 - this.playerEntity.posX;
            d15 = d7 - this.playerEntity.posY;
            // `||` here is tautologically true, so d15 was unconditionally zeroed and the
            // vertical half of the desync check below never ran. The intent is plainly to
            // ignore *small* vertical disagreements, which is `&&`. Legitimate play keeps
            // |d15| well under 0.5, so this behaves identically except for the >0.5-block
            // discrepancies the check exists to catch.
            if (d15 > -0.5 && d15 < 0.5) {
                d15 = 0.0;
            }

            d17 = d9 - this.playerEntity.posZ;
            d19 = d13 * d13 + d15 * d15 + d17 * d17;
            boolean z23 = false;
            if (d19 > 0.0625 && !this.playerEntity.isPlayerSleeping()) {
                z23 = true;
                logger.warning(this.playerEntity.username + " moved wrongly!");
                LoggingUtils.warnf(logger, "Got position %f, %f, %f", d5, d7, d9);
                LoggingUtils.warnf(logger, "Expected %f, %f, %f", this.playerEntity.posX, this.playerEntity.posY, this.playerEntity.posZ);
            }

            this.playerEntity.teleportTo(d5, d7, d9, f11, f12);
            boolean z24 = this.playerEntity.properties.isSpectator()
                || worldServer2.getBoundingBoxes(this.playerEntity, this.playerEntity.boundingBox.copy().shrink(f21, f21, f21)).size() == 0;
            if (z22 && (z23 || !z24) && !this.playerEntity.isPlayerSleeping()) {
                this.teleportTo(this.lastPosX, this.lastPosY, this.lastPosZ, f11, f12);
                return;
            }

            if (!this.mcServer.allowFlight && !this.playerEntity.properties.isCreative() && !this.playerEntity.properties.isSpectator()) {
                // Ask the world, not the entity. playerEntity.onGround comes from the moveEntity()
                // call above, which only sets it when a *downward* move was clipped by a block. A
                // client walking on flat ground reports the same Y every tick, so the delta
                // re-applied here is dy=0, nothing is clipped, and onGround is false for someone
                // standing on the floor - which used to kick everyone four seconds after they
                // joined. Blocks under the feet is the real question, and it covers water,
                // ladders and vines for free.
                boolean supported = !isAllAir(
                    worldServer2, this.playerEntity.boundingBox.copy().expand(f21, f21, f21).addCoord(0.0, -0.55, 0.0)
                );
                if (!supported && this.playerEntity.posY - d3 >= -0.03125) {
                    this.playerInAirTime++;
                    if (this.playerInAirTime > 80) {
                        logger.warning(this.playerEntity.username + " was kicked for floating too long!");
                        this.kickPlayer("Flying is not enabled on this server");
                        return;
                    }
                } else {
                    this.playerInAirTime = 0;
                }
            } else {
                this.playerInAirTime = 0;
            }

            this.playerEntity.onGround = packet10Flying1.onGround;
            this.mcServer.configManager.setPositionUponLogin(this.playerEntity);
            this.playerEntity.handleFalling(this.playerEntity.posY - d3, packet10Flying1.onGround);
        }
    }

    private static boolean isAllAir(WorldServer world, BoundingBox box) {
        int minX = MathHelper.floor_double(box.minX);
        int maxX = MathHelper.floor_double(box.maxX);
        int minY = MathHelper.floor_double(box.minY);
        int maxY = MathHelper.floor_double(box.maxY);
        int minZ = MathHelper.floor_double(box.minZ);
        int maxZ = MathHelper.floor_double(box.maxZ);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (!world.isAir(x, y, z)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void teleportTo(double d1, double d3, double d5, float f7, float f8) {
        this.hasMoved = false;
        this.lastPosX = d1;
        this.lastPosY = d3;
        this.lastPosZ = d5;
        this.playerEntity.teleportTo(d1, d3, d5, f7, f8);
        // The constructor takes (x, y, stance, z) - same order as PlayerPositionPacket - but the
        // arguments were (x, y + 1.62, y, z), which swaps y and stance. Every server-initiated
        // teleport therefore told the client it was standing 1.62 blocks higher than the server
        // thought, with its eyes 1.62 blocks below its feet. That includes the teleport sent on
        // login, so every player arrived floating and immediately fell.
        this.playerEntity.playerNetServerHandler.sendPacket(new PlayerTeleportPacket(d1, d3, d3 + 1.62, d5, f7, f8, false));
    }

    @Override
    public void handleBlockDig(DigPacket packet14BlockDig1) {
        WorldServer worldServer2 = this.mcServer.getWorldManager(this.playerEntity.dimension);
        if (packet14BlockDig1.status == 4) {
            this.playerEntity.dropCurrentItem(1);
        } else if (packet14BlockDig1.status == 5) {
            this.playerEntity.stopUsingItem();
        } else {
            boolean z3 = worldServer2.blockBreaking = this.mcServer.configManager.isOp(this.playerEntity.username);
            boolean z4 = false;
            if (packet14BlockDig1.status == 0) {
                z4 = true;
            }

            if (packet14BlockDig1.status == 2) {
                z4 = true;
            }

            int i5 = packet14BlockDig1.xPosition;
            int i6 = packet14BlockDig1.yPosition;
            int i7 = packet14BlockDig1.zPosition;
            if (z4) {
                double d8 = this.playerEntity.posX - (i5 + 0.5);
                double d10 = this.playerEntity.posY - (i6 + 0.5);
                double d12 = this.playerEntity.posZ - (i7 + 0.5);
                double d14 = d8 * d8 + d10 * d10 + d12 * d12;
                if (d14 > 36.0) {
                    return;
                }
            }

            Position chunkCoordinates19 = worldServer2.getSpawnPoint();
            int i9 = (int)MathHelper.abs((float)(i5 - chunkCoordinates19.x));
            int i20 = (int)MathHelper.abs((float)(i7 - chunkCoordinates19.z));
            if (i9 > i20) {
                i20 = i9;
            }

            if (packet14BlockDig1.status == 0) {
                if (i20 <= this.mcServer.spawnProtectionRadius && !z3) {
                    this.playerEntity.playerNetServerHandler.sendPacket(new UpdateBlockPacket(i5, i6, i7, worldServer2));
                } else {
                    this.playerEntity.itemInWorldManager.useBlock(i5, i6, i7, packet14BlockDig1.face);
                }
            } else if (packet14BlockDig1.status == 2 || packet14BlockDig1.status == 6) {
                this.playerEntity.itemInWorldManager.onBlockRemoving(i5, i6, i7);
                if (worldServer2.getBlockId(i5, i6, i7) != 0) {
                    this.playerEntity.playerNetServerHandler.sendPacket(new UpdateBlockPacket(i5, i6, i7, worldServer2));
                }

                if (packet14BlockDig1.status == 6) {
                    this.playerEntity.setUsing(true);
                }
            } else if (packet14BlockDig1.status == 3) {
                double d11 = this.playerEntity.posX - (i5 + 0.5);
                double d13 = this.playerEntity.posY - (i6 + 0.5);
                double d15 = this.playerEntity.posZ - (i7 + 0.5);
                double d17 = d11 * d11 + d13 * d13 + d15 * d15;
                if (d17 < 256.0) {
                    this.playerEntity.playerNetServerHandler.sendPacket(new UpdateBlockPacket(i5, i6, i7, worldServer2));
                }
            }

            worldServer2.blockBreaking = false;
        }

        this.playerEntity.setUsing(false);
    }

    @Override
    public void handlePlace(PlacePacket packet15Place1) {
        WorldServer worldServer2 = this.mcServer.getWorldManager(this.playerEntity.dimension);
        ItemStack itemStack3 = this.playerEntity.inventory.getCurrentItem();
        if (this.playerEntity.inventory.canUseItem()) {
            itemStack3 = this.playerEntity.inventory.getOffHandItem();
        }

        boolean z4 = worldServer2.blockBreaking = this.mcServer.configManager.isOp(this.playerEntity.username);
        if (packet15Place1.direction == 255) {
            if (itemStack3 == null) {
                return;
            }

            this.playerEntity.itemInWorldManager.destroyCurrentlyHeldItem(this.playerEntity, worldServer2, itemStack3);
        } else {
            int i5 = packet15Place1.xPosition;
            int i6 = packet15Place1.yPosition;
            int i7 = packet15Place1.zPosition;
            int i8 = packet15Place1.direction;
            Position chunkCoordinates9 = worldServer2.getSpawnPoint();
            int i10 = (int)MathHelper.abs((float)(i5 - chunkCoordinates9.x));
            int i11 = (int)MathHelper.abs((float)(i7 - chunkCoordinates9.z));
            if (i10 > i11) {
                i11 = i10;
            }

            if (this.hasMoved && this.playerEntity.getDistanceSq(i5 + 0.5, i6 + 0.5, i7 + 0.5) < 64.0 && (i11 > this.mcServer.spawnProtectionRadius || z4)) {
                this.playerEntity.itemInWorldManager.activeBlockOrUseItem(this.playerEntity, worldServer2, itemStack3, i5, i6, i7, i8);
            }

            this.playerEntity.playerNetServerHandler.sendPacket(new UpdateBlockPacket(i5, i6, i7, worldServer2));
            if (i8 == 0) {
                i6--;
            }

            if (i8 == 1) {
                i6++;
            }

            if (i8 == 2) {
                i7--;
            }

            if (i8 == 3) {
                i7++;
            }

            if (i8 == 4) {
                i5--;
            }

            if (i8 == 5) {
                i5++;
            }

            this.playerEntity.playerNetServerHandler.sendPacket(new UpdateBlockPacket(i5, i6, i7, worldServer2));
        }

        if (this.playerEntity.inventory.canUseItem()) {
            itemStack3 = this.playerEntity.inventory.getOffHandItem();
            if (itemStack3 != null && itemStack3.stackSize == 0) {
                this.playerEntity.inventory.offhandSlot[0] = null;
            }
        } else {
            itemStack3 = this.playerEntity.inventory.getCurrentItem();
            if (itemStack3 != null && itemStack3.stackSize == 0) {
                this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentSlot] = null;
            }
        }

        if (itemStack3 == null || itemStack3.getMaxItemUseDuration() == 0) {
            Slot slot12 = this.playerEntity.openContainer.getSlotInInventory(this.playerEntity.inventory, this.playerEntity.inventory.currentSlot);
            this.playerEntity.openContainer.updateCraftResults();
            if (!ItemStack.areItemStacksEqual(this.playerEntity.inventory.getCurrentItem(), packet15Place1.itemStack)) {
                if (this.playerEntity.inventory.canUseItem()) {
                    this.sendPacket(new SlotPacket(this.playerEntity.openContainer.windowId, slot12.slotNumber, this.playerEntity.inventory.getOffHandItem()));
                } else {
                    this.sendPacket(new SlotPacket(this.playerEntity.openContainer.windowId, slot12.slotNumber, this.playerEntity.inventory.getCurrentItem()));
                }
            }
        }

        worldServer2.blockBreaking = false;
    }

    @Override
    public void handleErrorMessage(String string1, Object... format) {
        logger.info(this.playerEntity.username + " lost connection: " + Translator.getInstance().translateKeyFormat(string1, format));
        this.mcServer.configManager.sendPacketToAll(new ChatPacket("§e" + this.playerEntity.username + " left the game."));
        this.mcServer.configManager.playerLoggedOut(this.playerEntity);
        this.connectionClosed = true;
    }

    @Override
    public void registerPacket(Packet packet1) {
        logger.warning(this.getClass() + " wasn't prepared to deal with a " + packet1.getClass());
        this.kickPlayer("Protocol error, unexpected packet");
    }

    public void sendPacket(Packet packet1) {
        this.netManager.sendPacket(packet1);
        this.currentHealth = this.lastHealth;
    }

    @Override
    public void handleBlockItemSwitch(ItemSwitchPacket packet16BlockItemSwitch1) {
        if (packet16BlockItemSwitch1.id >= 0 && packet16BlockItemSwitch1.id <= 9) {
            this.playerEntity.inventory.currentSlot = packet16BlockItemSwitch1.id;
        } else if (packet16BlockItemSwitch1.id == 44) {
            this.playerEntity.inventory.swapHands(this.playerEntity.inventory.getCurrentItem(), this.playerEntity.inventory.getOffHandItem());
        } else {
            logger.warning(this.playerEntity.username + " tried to set an invalid carried item");
        }
    }

    @Override
    public void handleChat(ChatPacket packet3Chat1) {
        String string2 = packet3Chat1.message;
        if (string2.length() > 100) {
            this.kickPlayer("Chat message too long");
        } else {
            string2 = string2.trim();

            for (int i3 = 0; i3 < string2.length(); i3++) {
                if (SharedConstants.VALID_TEXT_CHARACTERS.indexOf(string2.charAt(i3)) < 0) {
                    this.kickPlayer("Illegal characters in chat");
                    return;
                }
            }

            if (string2.startsWith("/")) {
                this.handleSlashCommand(string2);
            } else {
                String prefix = "<";
                String suffix = "> ";
                if (this.mcServer.configManager.isOp(this.playerEntity.username)) {
                    prefix = "<§d";
                    suffix = "§f> ";
                } else if (this.playerEntity.username.equalsIgnoreCase("Yoniko98")) {
                    prefix = "<§a";
                    suffix = "§f> ";
                }

                string2 = prefix + this.playerEntity.username + suffix + string2;
                logger.info(SharedConstants.deleteColorCodes(string2));
                this.mcServer.configManager.sendPacketToAll(new ChatPacket(string2));
            }
        }
    }

    private void handleSlashCommand(String string1) {
        this.mcServer.addCommand(string1.substring(1).trim(), this::createCommandSource);
    }

    public CommandSourceStack createCommandSource() {
        int level = this.mcServer.configManager.isOp(this.playerEntity.username)
            ? CommandSourceStack.LEVEL_OP
            : CommandSourceStack.LEVEL_ALL;
        return CommandSourceStack.of(this, this.playerEntity, level, this.mcServer.configManager, true);
    }

    @Override
    public void handleAnimation(AnimationPacket packet18Animation1) {
        if (packet18Animation1.animate == 1) {
            this.playerEntity.swingItem();
        } else if (packet18Animation1.animate == 4) {
            this.playerEntity.swingOffhandItem();
        }
    }

    @Override
    public void handleEntityAction(EntityActionPacket packet19EntityAction1) {
        if (packet19EntityAction1.state == 3) {
            // resetTimer, or leaving the bed early skips the night the same way sleeping through it does
            this.playerEntity.wakeUpPlayer(true, true, true);
            this.hasMoved = false;
        } else if (packet19EntityAction1.state == 4) {
            this.playerEntity.setSprinting(true);
        } else if (packet19EntityAction1.state == 5) {
            this.playerEntity.setSprinting(false);
        }
    }

    @Override
    public void handleDisconnect(DisconnectPacket packet255KickDisconnect1) {
        this.netManager.networkShutdown("disconnect.quitting");
    }

    public int getNumChunkDataPackets() {
        return this.netManager.getNumChunkDataPackets();
    }

    @Override
    public void sendCommandMessage(String string1) {
        this.sendPacket(new ChatPacket("§7" + string1));
    }

    @Override
    public void handleUseEntity(UsePacket packet7UseEntity1) {
        WorldServer worldServer2 = this.mcServer.getWorldManager(this.playerEntity.dimension);
        Entity entity3 = worldServer2.findEntityBasedOnId(packet7UseEntity1.targetEntity);
        this.playerEntity.setUsing(packet7UseEntity1.offHand);
        if (entity3 != null && this.playerEntity.isVisible(entity3) && this.playerEntity.getDistanceSqTo(entity3) < 36.0) {
            if (packet7UseEntity1.isLeftClick == 0) {
                this.playerEntity.useItemOnEntity(entity3);
            } else if (packet7UseEntity1.isLeftClick == 1) {
                this.playerEntity.attackEntityWithItem(entity3);
            }
        }

        this.playerEntity.setUsing(false);
    }

    @Override
    public void handleRespawn(RespawnPacket packet9Respawn1) {
        if (this.playerEntity.properties.getPermadeath()) {
            BanEntry var2 = new BanEntry(this.playerEntity.username);
            var2.setBanReason("Death in Hardcore");
            ((ServerConfigurationManager)this.mcServer.getConfigManager()).getBannedPlayers().put(var2);
            this.playerEntity.playerNetServerHandler.kickPlayer("You have died. Game over, man, it's game over!");
        } else if (this.playerEntity.health <= 0) {
            this.playerEntity = this.mcServer.configManager.recreatePlayerEntity(this.playerEntity, 0);
        }
    }

    @Override
    public void handleContainerClose(ContainerClosePacket packet101CloseWindow1) {
        this.playerEntity.closeCraftingGui();
    }

    @Override
    public void handleCreativeSlot(CreativeSlotPacket packet) {
        if (packet.slot < 0 || packet.slot >= this.playerEntity.container.slots.size()) {
            return;
        }

        if (!this.playerEntity.properties.isCreative()) {
            this.sendPacket(new SlotPacket(0, packet.slot, this.playerEntity.container.getSlot(packet.slot).getStack()));
            return;
        }

        this.playerEntity.container.putStackInSlot(packet.slot, packet.itemStack);
        this.playerEntity.container.updateCraftResults();
    }

    @Override
    public void handleWindowClick(ClickPacket packet102WindowClick1) {
        if (this.playerEntity.openContainer.windowId == packet102WindowClick1.window_Id && this.playerEntity.openContainer.canUseMP(this.playerEntity)) {
            if (packet102WindowClick1.isActive && packet102WindowClick1.mouseClick >= 2) {
                this.playerEntity.openContainer.swapWithHotbarSlot(packet102WindowClick1.inventorySlot, packet102WindowClick1.mouseClick - 2, this.playerEntity);
                this.playerEntity.updateCraftingInventory(this.playerEntity.openContainer, this.playerEntity.openContainer.getStacks());
                return;
            }

            ItemStack itemStack2 = this.playerEntity
                .openContainer
                .windowClick(packet102WindowClick1.inventorySlot, packet102WindowClick1.mouseClick, packet102WindowClick1.isActive, this.playerEntity);
            if (ItemStack.areItemStacksEqual(packet102WindowClick1.itemStack, itemStack2)) {
                this.playerEntity.playerNetServerHandler.sendPacket(new TransactionPacket(packet102WindowClick1.window_Id, packet102WindowClick1.action, true));
                this.playerEntity.openContainer.updateCraftResults();
                this.playerEntity.updateHeldItem();
            } else {
                this.idMap.put(this.playerEntity.openContainer.windowId, packet102WindowClick1.action);
                this.playerEntity
                    .playerNetServerHandler
                    .sendPacket(new TransactionPacket(packet102WindowClick1.window_Id, packet102WindowClick1.action, false));
                this.playerEntity.openContainer.updatePlayers(this.playerEntity, false);
                ArrayList<ItemStack> arrayList3 = new ArrayList<>();

                for (int i4 = 0; i4 < this.playerEntity.openContainer.slots.size(); i4++) {
                    arrayList3.add(this.playerEntity.openContainer.slots.get(i4).getStack());
                }

                this.playerEntity.updateCraftingInventory(this.playerEntity.openContainer, arrayList3);
            }
        }
    }

    @Override
    public void handleTransaction(TransactionPacket packet106Transaction1) {
        Short short2 = this.idMap.get(this.playerEntity.openContainer.windowId);
        if (short2 != null
            && packet106Transaction1.transactionId == short2
            && this.playerEntity.openContainer.windowId == packet106Transaction1.windowId
            && !this.playerEntity.openContainer.canUseMP(this.playerEntity)) {
            this.playerEntity.openContainer.updatePlayers(this.playerEntity, true);
        }
    }

    @Override
    public void handleSignUpdate(SignPacket packet130UpdateSign1) {
        WorldServer worldServer2 = this.mcServer.getWorldManager(this.playerEntity.dimension);
        if (worldServer2.blockExists(packet130UpdateSign1.xPosition, packet130UpdateSign1.yPosition, packet130UpdateSign1.zPosition)) {
            TileEntity tileEntity3 = worldServer2.getTileEntity(packet130UpdateSign1.xPosition, packet130UpdateSign1.yPosition, packet130UpdateSign1.zPosition);
            if (tileEntity3 instanceof SignEntity) {
                SignEntity tileEntitySign4 = (SignEntity)tileEntity3;
                if (!tileEntitySign4.isEditable()) {
                    this.mcServer.logWarning("Player " + this.playerEntity.username + " just tried to change non-editable sign");
                    return;
                }
            }

            for (int i9 = 0; i9 < 4; i9++) {
                boolean z5 = true;
                if (packet130UpdateSign1.signLines[i9].length() > 15) {
                    z5 = false;
                } else {
                    for (int i6 = 0; i6 < packet130UpdateSign1.signLines[i9].length(); i6++) {
                        if (SharedConstants.VALID_TEXT_CHARACTERS.indexOf(packet130UpdateSign1.signLines[i9].charAt(i6)) < 0) {
                            z5 = false;
                        }
                    }
                }

                if (!z5) {
                    packet130UpdateSign1.signLines[i9] = "!?";
                }
            }

            if (tileEntity3 instanceof SignEntity) {
                int var11 = packet130UpdateSign1.xPosition;
                int i10 = packet130UpdateSign1.yPosition;
                int i6 = packet130UpdateSign1.zPosition;
                SignEntity tileEntitySign7 = (SignEntity)tileEntity3;

                for (int i8 = 0; i8 < 4; i8++) {
                    tileEntitySign7.signText[i8] = packet130UpdateSign1.signLines[i8];
                }

                tileEntitySign7.setEditable(false);
                tileEntitySign7.onChanged();
                worldServer2.markForUpdate(var11, i10, i6);
            }
        }
    }

    @Override
    public void handleKeepAlive(KeepAlivePacket par1Packet0KeepAlive) {
        if (par1Packet0KeepAlive.randomId == this.lastPingId) {
            int i = (int)(System.nanoTime() / 1000000L - this.oPingId);
            this.playerEntity.ping = (this.playerEntity.ping * 3 + i) / 4;
        }
    }

    @Override
    public void handleGenerator(GeneratorPacket packet) {
        TileEntity tileEntity = this.mcServer
            .getWorldManager(this.playerEntity.dimension)
            .getTileEntity(packet.xPosition, packet.yPosition, packet.zPosition);
        if (tileEntity instanceof GeneratorEntity) {
            ((GeneratorEntity)tileEntity).active = packet.state;
            this.sendPacket(new GeneratorPacket(packet.xPosition, packet.yPosition, packet.zPosition, packet.state));
        }
    }

    @Override
    public void handleCustomPayload(CustomPayloadPacket ass) {
        if ("MC|BEdit".equals(ass.channel)) {
            try {
                DataInputStream var2 = new DataInputStream(new ByteArrayInputStream(ass.data));
                ItemStack var3 = IOUtil.readItemStack(var2);
                if (!Book.validBookTagPages(var3.getTag())) {
                    throw new IOException("Invalid book tag!");
                }

                ItemStack var4 = this.playerEntity.inventory.getCurrentItem();
                if (var3 != null && var3.itemID == ItemList.bookAndQuill.id && var3.itemID == var4.itemID) {
                    var4.setTagInfo("pages", var3.getTag().getTagList("pages"));
                }
            } catch (Exception var12) {
                LoggingUtils.warnThrowable(logger, "Error receiving custom payload", var12);
            }
        } else if ("MC|BSign".equals(ass.channel)) {
            try {
                DataInputStream var2 = new DataInputStream(new ByteArrayInputStream(ass.data));
                ItemStack var3 = IOUtil.readItemStack(var2);
                if (!WrittenBook.validBookContents(var3.getTag())) {
                    throw new IOException("Invalid book tag!");
                }

                ItemStack var4 = this.playerEntity.inventory.getCurrentItem();
                if (var3 != null && var3.itemID == ItemList.writtenBook.id && var4.itemID == ItemList.bookAndQuill.id) {
                    var4.setTagInfo("author", new StringTag(this.playerEntity.username).setKey("author"));
                    var4.setTagInfo("title", new StringTag(var3.getTag().getString("title")).setKey("title"));
                    var4.setTagInfo("pages", var3.getTag().getTagList("pages"));
                    var4.itemID = ItemList.writtenBook.id;
                }
            } catch (Exception var11) {
                LoggingUtils.warnThrowable(logger, "Error receiving custom payload", var11);
            }
        }
    }

    @Override
    public boolean isServerHandler() {
        return true;
    }

}
