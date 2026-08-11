package net.minecraft.server.player;

import com.mojang.nbt.tag.Tag;
import com.mojang.nbt.tag.special.CompoundTag;
import com.mojang.nbt.tag.special.ListTag;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.game.block.decoration.Bed;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.tileentity.smelters.FurnaceEntity;
import net.minecraft.game.block.tileentity.smelters.GeneratorEntity;
import net.minecraft.game.container.Container;
import net.minecraft.game.container.ContainerListener;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.container.slot.CraftingSlot;
import net.minecraft.game.container.tileentities.BarrelContainer;
import net.minecraft.game.container.tileentities.BookshelfContainer;
import net.minecraft.game.container.tileentities.ChestContainer;
import net.minecraft.game.container.tileentities.FurnaceContainer;
import net.minecraft.game.container.tileentities.GeneratorContainer;
import net.minecraft.game.container.tileentities.WorkbenchContainer;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.arrow.Arrow;
import net.minecraft.game.entity.hanging.Painting;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.misc.MapItem;
import net.minecraft.game.item.potion.EffectData;
import net.minecraft.game.world.World;
import net.minecraft.game.world.chunk.ChunkUtils;
import net.minecraft.game.world.util.Position;
import net.minecraft.util.math.MathHelper;
import net.minecraft.network.packet.player.AnimationPacket;
import net.minecraft.network.packet.player.AttackDirectionPacket;
import net.minecraft.network.packet.misc.ChatPacket;
import net.minecraft.network.packet.container.ContainerClosePacket;
import net.minecraft.network.packet.container.ContainerContentsPacket;
import net.minecraft.network.packet.container.ContainerDataPacket;
import net.minecraft.network.packet.container.ContainerOpenPacket;
import net.minecraft.network.packet.entity.EntityEffectPacket;
import net.minecraft.network.packet.entity.EntityHealthPacket;
import net.minecraft.network.packet.entity.EntityStatePacket;
import net.minecraft.network.packet.player.ItemEquipPacket;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.player.PickupPacket;
import net.minecraft.network.packet.entity.RemoveEffectPacket;
import net.minecraft.network.packet.entity.RideEntityPacket;
import net.minecraft.network.packet.player.SleepPacket;
import net.minecraft.network.packet.player.SlotPacket;
import net.minecraft.network.packet.world.UpdateChunkPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.EntityTracker;
import net.minecraft.server.network.NetServerHandler;
import net.minecraft.server.world.WorldServer;

public class EntityPlayerMP extends Player implements ContainerListener {
    public NetServerHandler playerNetServerHandler;
    public MinecraftServer mcServer;
    public ItemInWorldManager itemInWorldManager;
    public double serverPosX;
    public double serverPosZ;
    public List<Long> loadedChunks = new LinkedList<>();
    public LongOpenHashSet loadedChunksMP = new LongOpenHashSet();
    private int lastHealth = -99999999;
    private int lastFood = -99999999;
    private float lastSaturation = -99999999.0F;
    private int ticksOfInvuln = 60;
    private ItemStack[] playerInventory = new ItemStack[]{null, null, null, null, null, null};
    private int currentWindowId = 0;
    public boolean isChangingQuantityOnly;
    public int ping;

    public EntityPlayerMP(MinecraftServer minecraftServer1, World world2, String string3, ItemInWorldManager itemInWorldManager4) {
        super(world2);
        itemInWorldManager4.thisPlayer = this;
        this.itemInWorldManager = itemInWorldManager4;
        Position chunkCoordinates5 = world2.getSpawnPoint();
        int i6 = chunkCoordinates5.x;
        int i7 = chunkCoordinates5.z;
        int i8 = chunkCoordinates5.y;
        i6 += this.rand.nextInt(20) - 10;
        i8 = world2.getTopSolidTile(i6, i7);
        i7 += this.rand.nextInt(20) - 10;
        this.setPosAndRot(i6 + 0.5, i8, i7 + 0.5, 0.0F, 0.0F);
        this.mcServer = minecraftServer1;
        this.stepHeight = 0.0F;
        this.username = string3;
        this.yOffset = 0.0F;
    }

    @Override
    public void setWorld(World world1) {
        super.setWorld(world1);
        this.itemInWorldManager = new ItemInWorldManager((WorldServer)world1);
        this.itemInWorldManager.thisPlayer = this;
    }

    public void onGuiOpened() {
        this.openContainer.addListener(this);
    }

    @Override
    public ItemStack[] getInventory() {
        return this.playerInventory;
    }

    @Override
    protected void resetHeight() {
        this.yOffset = 0.0F;
    }

    // An MP player is anchored at its bounding-box minY (feet), not at an eye
    // line... so the constructor sets yOffset = 0 and every position the client
    // sends is feet-relative. Player.setCrawling is singleplayer code that
    // moves yOffset between 1.62 and 0.56 to swap eye height; run as-is here
    // it would leave yOffset at 0.56 while the feet anchor stays put, and the
    // movement handler's teleportTo(posY) then adds that offset a second time,
    // a persistent +0.56 desync that every moved-wrongly rubber-band resets to
    // the log's "Expected +0.56". The crawl bounding box (0.9m) is still what
    // collision and fitting use; only the eye offset is pinned
    @Override
    public void setCrawling(boolean state) {
        this.crawling = state;
        this.setSize(0.6F, state ? 0.9F : 1.8F);
    }

    @Override
    public float getEyeHeight() {
        return 1.62F;
    }

    // getEyeHeight is pinned at the standing 1.62 above, so cap the suffocation probe
    // under the box top - strictly under, or a box touching a ceiling floors into it.
    @Override
    public boolean isInsideBlock() {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(Math.min(this.posY + this.getEyeHeight(), this.boundingBox.maxY - 0.1));
        int z = MathHelper.floor_double(this.posZ);
        return this.world.isBlockFull(x, y, z);
    }

    @Override
    public void onTick() {
        this.itemInWorldManager.beginBlockRemoval();
        this.ticksOfInvuln--;
        this.openContainer.updateCraftResults();

        for (int i1 = 0; i1 < this.playerInventory.length; i1++) {
            ItemStack itemStack2 = this.getEquipmentInSlot(i1);
            if (itemStack2 != this.playerInventory[i1]) {
                this.mcServer.getEntityTracker(this.dimension).sendPacketToTrackedPlayers(this, new ItemEquipPacket(this.entityId, i1, itemStack2));
                this.playerInventory[i1] = itemStack2;
            }

            ItemStack var5 = this.inventory.getStackInSlot(i1);
            if (var5 != null && ItemList.items[var5.itemID] instanceof MapItem) {
                Packet var6 = ((MapItem)ItemList.items[var5.itemID]).createMapDataPacket(var5, this.world, this);
                if (var6 != null) {
                    this.playerNetServerHandler.sendPacket(var6);
                }
            }
        }
    }

    public ItemStack getEquipmentInSlot(int i1) {
        return i1 == 5 ? this.inventory.getOffHandItem() : (i1 == 0 ? this.inventory.getCurrentItem() : this.inventory.armorSlots[i1 - 1]);
    }

    @Override
    public void displayGUIPainting(int xCoord, int yCoord, int zCoord, byte b8) {
        Painting entityPainting9 = new Painting(this.world, xCoord, yCoord, zCoord, b8);
        if (entityPainting9.onValidSurface()) {
            this.world.spawnEntity(entityPainting9);
            if (this.inventory.canUseItem() && this.inventory.getOffHandItem().getItem() == ItemList.painting) {
                this.inventory.getOffHandItem().stackSize--;
            } else {
                this.inventory.getCurrentItem().stackSize--;
            }
        }
    }

    @Override
    public void onDeath(DamageSource entity1) {
        this.inventory.clearContents(true);
        this.score /= 2;
        String died = entity1.getDeathMsg(this);
        this.mcServer.configManager.sendPacketToAll(new ChatPacket(died));
        MinecraftServer.logger.info(died);
    }

    @Override
    public boolean damageEntity(DamageSource ds, int i2) {
        if (this.ticksOfInvuln > 0) {
            return false;
        }

        if (!this.mcServer.pvpOn) {
            Entity entity1 = ds.getEntity();
            if (entity1 instanceof Player) {
                return false;
            }

            if (entity1 instanceof Arrow) {
                Arrow entityArrow3 = (Arrow)entity1;
                if (entityArrow3.shooter instanceof Player) {
                    return false;
                }
            }
        }

        boolean attacked = super.damageEntity(ds, i2);
        this.playerNetServerHandler.sendPacket(new AttackDirectionPacket(this.attackAngle));
        return attacked;
    }

    @Override
    protected boolean isPVPEnabled() {
        return this.mcServer.pvpOn;
    }

    @Override
    public void heal(int i1) {
        super.heal(i1);
    }

    @Override
    public void writeToNBT(CompoundTag nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);
        nbtTagCompound.setInteger("Score", this.score);
        nbtTagCompound.setTag("Inventory", this.inventory.saveContents(new ListTag<>()));
    }

    @Override
    public void readFromNBT(CompoundTag nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
        this.score = nbtTagCompound.getInteger("Score");
        ListTag<? extends Tag> nBTTagList2 = nbtTagCompound.getTagList("Inventory");
        this.inventory.loadContents(nBTTagList2);
    }

    public void onUpdateEntity(boolean z1) {
        super.onTick();
        if (z1 && !this.loadedChunks.isEmpty()) {
            long chunkCoordIntPair7 = this.loadedChunks.get(0);
            boolean z8 = false;
            if (this.playerNetServerHandler.getNumChunkDataPackets() < 4) {
                z8 = true;
            }

            if (z8) {
                WorldServer worldServer9 = this.mcServer.getWorldManager(this.dimension);
                this.loadedChunks.remove(chunkCoordIntPair7);
                int chunkX = ChunkUtils.getChunkX(chunkCoordIntPair7);
                int chunkZ = ChunkUtils.getChunkZ(chunkCoordIntPair7);
                this.playerNetServerHandler.sendPacket(new UpdateChunkPacket(worldServer9.getChunk(chunkX, chunkZ), true, 0));
                List<TileEntity> list5 = worldServer9.getTileEntityList(
                    chunkX * 16, 0, chunkZ * 16, chunkX * 16 + 16, worldServer9.getWorldHeight(), chunkZ * 16 + 16
                );

                for (int i6 = 0; i6 < list5.size(); i6++) {
                    this.getTileEntityInfo(list5.get(i6));
                }
            }
        }

        int food = this.foodStats.getHunger();
        float saturation = this.foodStats.getSaturation();
        if (this.health != this.lastHealth || food != this.lastFood || saturation != this.lastSaturation) {
            this.playerNetServerHandler.sendPacket(new EntityHealthPacket(this.health, food, saturation));
            this.lastHealth = this.health;
            this.lastFood = food;
            this.lastSaturation = saturation;
        }
    }

    private void getTileEntityInfo(TileEntity tileEntity1) {
        if (tileEntity1 != null) {
            Packet packet2 = tileEntity1.getPacket();
            if (packet2 != null) {
                this.playerNetServerHandler.sendPacket(packet2);
            }
        }
    }

    @Override
    public void mobTick() {
        super.mobTick();
    }

    @Override
    public void onItemPickup(Entity entity1) {
        if (!entity1.isDead) {
            EntityTracker entityTracker3 = this.mcServer.getEntityTracker(this.dimension);
            if (entity1 instanceof DroppedItem) {
                entityTracker3.sendPacketToTrackedPlayers(entity1, new PickupPacket(entity1.entityId, this.entityId));
            }

            if (entity1 instanceof Arrow) {
                entityTracker3.sendPacketToTrackedPlayers(entity1, new PickupPacket(entity1.entityId, this.entityId));
            }
        }

        super.onItemPickup(entity1);
        this.openContainer.updateCraftResults();
    }

    @Override
    public void swingItem() {
        if (!this.swinging) {
            this.swingProgress = -1;
            this.swinging = true;
            EntityTracker entityTracker1 = this.mcServer.getEntityTracker(this.dimension);
            entityTracker1.sendPacketToTrackedPlayers(this, new AnimationPacket(this, 1));
        }
    }

    @Override
    public void swingOffhandItem() {
        if (!this.swingingOffhand) {
            this.swingProgress = -1;
            this.swingingOffhand = true;
            EntityTracker entityTracker1 = this.mcServer.getEntityTracker(this.dimension);
            entityTracker1.sendPacketToTrackedPlayers(this, new AnimationPacket(this, 4));
        }
    }

    public void respawn() {
    }

    @Override
    public Bed.Status goToSleep(int var1, int var2, int var3) {
        Bed.Status var4 = super.goToSleep(var1, var2, var3);
        if (var4 == Bed.Status.OK) {
            this.mcServer.getEntityTracker(this.dimension).sendPacketToTrackedPlayers(this, new SleepPacket(this, 0, var1, var2, var3));
        }

        return var4;
    }

    @Override
    public boolean preventSleeping() {
        return this.world.isDaytime();
    }

    @Override
    protected boolean awaken() {
        return this.world.isDaytime();
    }

    @Override
    public void wakeUpPlayer(boolean var1, boolean var2, boolean var3) {
        if (this.isPlayerSleeping()) {
            this.mcServer.getEntityTracker(this.dimension).sendPacketToTrackedPlayersAndTrackedEntity(this, new AnimationPacket(this, 3));
        }

        super.wakeUpPlayer(var1, var2, var3);
        this.playerNetServerHandler.teleportTo(this.posX, this.posY, this.posZ, this.yaw, this.pitch);
    }

    @Override
    public void mountEntity(Entity entity1) {
        super.mountEntity(entity1);
        this.playerNetServerHandler.sendPacket(new RideEntityPacket(this, this.mount));
        this.playerNetServerHandler.teleportTo(this.posX, this.posY, this.posZ, this.yaw, this.pitch);
    }

    @Override
    protected void updateFallState(double d1, boolean z3) {
    }

    public void handleFalling(double d1, boolean z3) {
        super.updateFallState(d1, z3);
    }

    private void getNextWidowId() {
        this.currentWindowId = this.currentWindowId % 100 + 1;
    }

    @Override
    public void displayWorkbenchGUI(int i1, int i2, int i3, boolean portable) {
        this.getNextWidowId();
        this.playerNetServerHandler.sendPacket(new ContainerOpenPacket(this.currentWindowId, 1, "Crafting", 9));
        this.openContainer = new WorkbenchContainer(this.inventory, this.world, i1, i2, i3, portable);
        this.openContainer.windowId = this.currentWindowId;
        this.openContainer.addListener(this);
    }

    @Override
    public void displayGUIChest(Inventory iInventory1, boolean b) {
        this.getNextWidowId();
        this.playerNetServerHandler.sendPacket(new ContainerOpenPacket(this.currentWindowId, 0, iInventory1.getName(), iInventory1.getSize()));
        this.openContainer = new ChestContainer(this.inventory, iInventory1);
        this.openContainer.windowId = this.currentWindowId;
        this.openContainer.addListener(this);
    }

    @Override
    public void displayGUIFurnace(FurnaceEntity tileEntityFurnace1) {
        this.getNextWidowId();
        this.playerNetServerHandler.sendPacket(new ContainerOpenPacket(this.currentWindowId, 2, tileEntityFurnace1.getName(), tileEntityFurnace1.getSize()));
        this.openContainer = new FurnaceContainer(this.inventory, tileEntityFurnace1);
        this.openContainer.windowId = this.currentWindowId;
        this.openContainer.addListener(this);
    }

    @Override
    public void displayGUIBarrel(Inventory tileEntityFurnace1) {
        this.getNextWidowId();
        this.playerNetServerHandler.sendPacket(new ContainerOpenPacket(this.currentWindowId, 3, tileEntityFurnace1.getName(), tileEntityFurnace1.getSize()));
        this.openContainer = new BarrelContainer(this.inventory, tileEntityFurnace1);
        this.openContainer.windowId = this.currentWindowId;
        this.openContainer.addListener(this);
    }

    @Override
    public void displayGUIBookshelf(Inventory tileEntityFurnace1) {
        this.getNextWidowId();
        this.playerNetServerHandler.sendPacket(new ContainerOpenPacket(this.currentWindowId, 4, tileEntityFurnace1.getName(), tileEntityFurnace1.getSize()));
        this.openContainer = new BookshelfContainer(this.inventory, tileEntityFurnace1);
        this.openContainer.windowId = this.currentWindowId;
        this.openContainer.addListener(this);
    }

    @Override
    public void displayGUIGenerator(GeneratorEntity tileEntityFurnace1) {
        this.getNextWidowId();
        this.playerNetServerHandler
            .sendPacket(
                new ContainerOpenPacket(
                    this.currentWindowId,
                    5,
                    tileEntityFurnace1.getName(),
                    tileEntityFurnace1.getSize(),
                    tileEntityFurnace1.x,
                    tileEntityFurnace1.y,
                    tileEntityFurnace1.z,
                    tileEntityFurnace1.active
                )
            );
        this.openContainer = new GeneratorContainer(this.inventory, tileEntityFurnace1);
        this.openContainer.windowId = this.currentWindowId;
        this.openContainer.addListener(this);
    }

    @Override
    public void setStack(Container container1, int i2, ItemStack itemStack3) {
        if (!(container1.getSlot(i2) instanceof CraftingSlot) && !this.isChangingQuantityOnly) {
            this.playerNetServerHandler.sendPacket(new SlotPacket(container1.windowId, i2, itemStack3));
        }
    }

    public void updateCrafting(Container container1) {
        this.updateCraftingInventory(container1, container1.getStacks());
    }

    @Override
    public void updateCraftingInventory(Container container1, List<ItemStack> list2) {
        this.playerNetServerHandler.sendPacket(new ContainerContentsPacket(container1.windowId, list2));
        this.playerNetServerHandler.sendPacket(new SlotPacket(-1, -1, this.inventory.getItemStack()));
    }

    @Override
    public void setId(Container container1, int i2, int i3) {
        this.playerNetServerHandler.sendPacket(new ContainerDataPacket(container1.windowId, i2, i3));
    }

    @Override
    public void closeScreen() {
        this.playerNetServerHandler.sendPacket(new ContainerClosePacket(this.openContainer.windowId));
        this.closeCraftingGui();
    }

    public void updateHeldItem() {
        if (!this.isChangingQuantityOnly) {
            this.playerNetServerHandler.sendPacket(new SlotPacket(-1, -1, this.inventory.getItemStack()));
        }
    }

    public void closeCraftingGui() {
        this.openContainer.onClose(this);
        this.openContainer = this.container;
    }

    @Override
    public void useItem() {
        this.playerNetServerHandler.sendPacket(new EntityStatePacket(this.entityId, (byte)9));
        super.useItem();
    }

    @Override
    public void setItemInUse(ItemStack itemStack1, int i2) {
        super.setItemInUse(itemStack1, i2);
        if (itemStack1 != null && itemStack1.getItem() != null && itemStack1.getItem().getItemUseAction(itemStack1) == Item.Action.eat) {
            EntityTracker entityTracker3 = this.mcServer.getEntityTracker(this.dimension);
            entityTracker3.sendPacketToTrackedPlayersAndTrackedEntity(this, new AnimationPacket(this, 5));
        }
    }

    public void setMovementType(float f1, float f2, boolean z3, float f5, float f6) {
        this.moveStrafing = f1;
        this.moveForward = f2;
        this.jumping = z3;
        this.pitch = f5;
        this.yaw = f6;
    }

    public void updateRidingState() {
        if (this.mount != null) {
            this.mountEntity(this.mount);
        }

        if (this.rider != null) {
            this.rider.mountEntity(this);
        }

        if (this.sleeping) {
            this.wakeUpPlayer(true, false, false);
        }
    }

    public void setHealth() {
        this.lastHealth = -99999999;
        this.lastFood = -99999999;
        this.lastSaturation = -99999999.0F;
    }

    @Override
    public void sendChatMessage(String string1) {
        String string3 = string1;
        this.playerNetServerHandler.sendPacket(new ChatPacket(string3));
    }

    public String getPlayerIP() {
        String var1 = this.playerNetServerHandler.netManager.getRemoteAddress().toString();
        var1 = var1.substring(var1.indexOf("/") + 1);
        return var1.substring(0, var1.indexOf(":"));
    }

    public String getName() {
        return this.username;
    }

    @Override
    protected void onEffectAdded(EffectData pe) {
        super.onEffectAdded(pe);
        this.playerNetServerHandler.sendPacket(new EntityEffectPacket(this.entityId, pe));
    }

    @Override
    protected void onEffectChanged(EffectData pe) {
        super.onEffectChanged(pe);
        this.playerNetServerHandler.sendPacket(new EntityEffectPacket(this.entityId, pe));
    }

    @Override
    protected void onEffectRemoved(EffectData pe) {
        super.onEffectRemoved(pe);
        this.playerNetServerHandler.sendPacket(new RemoveEffectPacket(this.entityId, pe));
    }
}
