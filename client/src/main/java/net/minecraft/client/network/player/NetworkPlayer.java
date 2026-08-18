package net.minecraft.client.network.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Session;
import net.minecraft.client.network.ClientHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.world.World;
import net.minecraft.network.packet.container.ContainerClosePacket;
import net.minecraft.network.packet.entity.EntityActionPacket;
import net.minecraft.network.packet.misc.ChatPacket;
import net.minecraft.network.packet.player.AnimationPacket;
import net.minecraft.network.packet.player.DigPacket;
import net.minecraft.network.packet.player.PlayerLookPacket;
import net.minecraft.network.packet.player.PlayerMovementPacket;
import net.minecraft.network.packet.player.PlayerPositionPacket;
import net.minecraft.network.packet.player.PlayerStatePacket;
import net.minecraft.network.packet.player.PlayerTeleportPacket;
import net.minecraft.network.packet.world.RespawnPacket;
import net.minecraft.util.math.MathHelper;

public class NetworkPlayer extends LocalPlayer {
    public ClientHandler connection;
    private int inventoryUpdateInterval = 0;
    private int movementInterval = 0;
    private double oldPosX;
    private double oldPosY;
    private double oldPosZ;
    private double oldHeight;
    private float oldRotYaw;
    private float oldRotPitch;
    private boolean onSolidTile = false;
    private boolean wasSneaking = false;
    private boolean wasCrawling = false;
    private boolean wasSprinting = false;

    public NetworkPlayer(Minecraft mc, World world, Session session, ClientHandler con) {
        super(mc, world, session);
        this.connection = con;
    }

    @Override
    public boolean damageEntity(DamageSource entity, int damage) {
        return false;
    }

    @Override
    public void heal(int amount) {
    }

    @Override
    public void onTick() {
        if (this.world.blockExists(MathHelper.floor_double(this.posX), this.world.getWorldHeight() / 2, MathHelper.floor_double(this.posZ))) {
            super.onTick();
            this.movePlayerLocally();
        }
    }

    @Override
    public void switchDimension(int dim, boolean forced) {
    }

    public void movePlayerLocally() {
        if (this.inventoryUpdateInterval++ == 20) {
            this.sendInventoryChanged();
            this.inventoryUpdateInterval = 0;
        }

        this.sendStatePacket((byte)1);
        double posX = this.posX - this.oldPosX;
        double posY = this.posY - this.oldPosY;
        double posZ = this.posZ - this.oldPosZ;
        double yaw = this.yaw - this.oldRotYaw;
        double pitch = this.pitch - this.oldRotPitch;
        double height = this.boundingBox.minY - this.oldHeight;
        boolean moving = height != 0.0 || posY != 0.0 || posX != 0.0 || posZ != 0.0;
        boolean looking = yaw != 0.0 || pitch != 0.0;
        if (this.mount != null) {
            if (looking) {
                this.connection.sendPacket(new PlayerPositionPacket(this.motionX, -999.0, -999.0, this.motionZ, this.onGround));
            } else {
                this.connection.sendPacket(new PlayerTeleportPacket(this.motionX, -999.0, -999.0, this.motionZ, this.yaw, this.pitch, this.onGround));
            }

            moving = false;
        } else if (moving && looking) {
            this.connection.sendPacket(new PlayerTeleportPacket(this.posX, this.boundingBox.minY, this.posY, this.posZ, this.yaw, this.pitch, this.onGround));
            this.movementInterval = 0;
        } else if (moving) {
            this.connection.sendPacket(new PlayerPositionPacket(this.posX, this.boundingBox.minY, this.posY, this.posZ, this.onGround));
            this.movementInterval = 0;
        } else if (looking) {
            this.connection.sendPacket(new PlayerLookPacket(this.yaw, this.pitch, this.onGround));
            this.movementInterval = 0;
        } else {
            this.connection.sendPacket(new PlayerMovementPacket(this.onGround));
            if (this.onSolidTile == this.onGround && this.movementInterval <= 200) {
                this.movementInterval++;
            } else {
                this.movementInterval = 0;
            }
        }

        this.onSolidTile = this.onGround;
        if (moving) {
            this.oldPosX = this.posX;
            this.oldHeight = this.boundingBox.minY;
            this.oldPosY = this.posY;
            this.oldPosZ = this.posZ;
        }

        if (looking) {
            this.oldRotYaw = this.yaw;
            this.oldRotPitch = this.pitch;
        }
    }

    @Override
    public void crawl() {
        super.crawl();
        this.sendStatePacket((byte)2);
    }

    private void sendStatePacket(byte state) {
        boolean sneak = this.sneaking;
        boolean sneakPrev = this.wasSneaking;
        if (state == 2) {
            sneak = this.crawling;
            sneakPrev = this.wasCrawling;
        }

        if (sneak != sneakPrev) {
            this.connection.sendPacket(new PlayerStatePacket(this.entityId, state, sneak));
            if (state == 2) {
                this.wasCrawling = sneak;
            } else {
                this.wasSneaking = sneak;
            }
        }

        boolean isSprinting = this.isSprinting();
        if (this.isSprinting() != this.wasSprinting) {
            if (isSprinting) {
                this.connection.sendPacket(new EntityActionPacket(this, 4));
            } else {
                this.connection.sendPacket(new EntityActionPacket(this, 5));
            }

            this.wasSprinting = isSprinting;
        }
    }

    @Override
    public void dropCurrentItem(int size) {
        this.connection.sendPacket(new DigPacket(4, 0, 0, 0, 0));
    }

    private void sendInventoryChanged() {
    }

    @Override
    protected void spawnItem(DroppedItem item) {
    }

    @Override
    public void displayGUIPainting(int xCoord, int yCoord, int zCoord, byte id) {
    }

    @Override
    public void sendChatMessage(String msg) {
        if (this.mc.HUD.chatOverlay.getChatHistory().isEmpty()
            || !this.mc.HUD.chatOverlay.getChatHistory().get(this.mc.HUD.chatOverlay.getChatHistory().size() - 1).equals(msg)) {
            this.mc.HUD.chatOverlay.getChatHistory().add(msg);
        }

        this.connection.sendPacket(new ChatPacket(msg));
    }

    @Override
    public void swingItem() {
        super.swingItem();
        this.connection.sendPacket(new AnimationPacket(this, 1));
    }

    @Override
    public void swingOffhandItem() {
        super.swingOffhandItem();
        this.connection.sendPacket(new AnimationPacket(this, 4));
    }

    @Override
    public void respawnPlayer() {
        this.sendInventoryChanged();
        this.connection.sendPacket(new RespawnPacket((byte)0));
    }

    @Override
    public void closeScreen() {
        this.connection.sendPacket(new ContainerClosePacket(this.openContainer.windowId));
        this.inventory.setItemStack(null);
        super.closeScreen();
    }

    @Override
    public void setHealth(int hp) {
        // In multiplayer the server is the only authority on damage: this packet
        // is the *result*, already armor-reduced and source-filtered. Routing a
        // health sync back through LocalPlayer.setHealth re-runs
        // applyDamageMultiplier with DamageSource.generic - which damages armor
        // for every damage type, even bypass sources like fall that the server
        // deliberately exempts, then double-applies armor reduction to health,
        // adds generic's exhaustion and can fantasm the rosary revive. The hurt
        // flash and sound already arrive separately as EntityStatePacket(2),
        // broadcast to the player themselves by Mob.damageEntity, so nothing is
        // lost by accepting the value as-is.
        this.health = hp;
    }
}
