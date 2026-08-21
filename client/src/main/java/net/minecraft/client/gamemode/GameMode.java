package net.minecraft.client.gamemode;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.GuiScreen;
import net.minecraft.client.gui.screens.container.inventory.AdventureScreen;
import net.minecraft.client.gui.screens.container.inventory.CreativeScreen;
import net.minecraft.client.gui.screens.container.inventory.InventoryScreen;
import net.minecraft.client.options.OptionValues;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.stepsound.StepSound;
import net.minecraft.game.container.slot.Slot;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.tools.Tool;
import net.minecraft.game.item.tools.ToolMaterial;
import net.minecraft.game.item.weapons.Sword;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;
import net.minecraft.statistics.achievement.AchievementList;

public class GameMode {
    protected final Minecraft mc;
    protected int hitX = -1;
    protected int hitY = -1;
    protected int hitZ = -1;
    protected float curBlockDamage = 0.0F;
    protected float prevBlockDamage = 0.0F;
    protected float blockSoundCounter = 0.0F;
    protected int blockHitDelay;

    public GameMode(Minecraft minecraft) {
        this.mc = minecraft;
        this.mc.gameRenderer.cameraMob = this.mc.player;
    }

    protected boolean isCreative() {
        return this.mc.player != null && this.mc.player.properties.isCreative();
    }

    protected boolean isAdventure() {
        return this.mc.player != null && this.mc.player.properties.isAdventure();
    }

    protected boolean isSpectator() {
        return this.mc.player != null && this.mc.player.properties.isSpectator();
    }

    public void onWorldChange(World world) {
    }

    public void swapItem(int id, int meta) {
        if (!this.isCreative()) {
            this.mc.player.inventory.swapFromInventory(id, meta);
            return;
        }

        this.mc.player.inventory.swapItem(id, meta);
        Slot filled = this.mc.player.container.getSlotInInventory(this.mc.player.inventory, this.mc.player.inventory.currentSlot);
        if (filled != null) {
            this.setCreativeSlot(filled.slotNumber, filled.getStack());
        }
    }

    public void clickBlock(int x, int y, int z, int side) {
        if (this.isSpectator()) {
            return;
        }

        this.mc.world.extinguishFire(this.mc.player, x, y, z, side);
        if (this.isCreative()) {
            ItemStack held = this.mc.player.getCurrentEquippedItem();
            if (held == null || !(held.getItem() instanceof Sword)) {
                this.removeBlock(x, y, z, side);
            }

            return;
        }

        int id = this.mc.world.getBlockId(x, y, z);
        if (id > 0 && this.curBlockDamage == 0.0F) {
            BlockList.blocks[id].blockPunched(this.mc.world, x, y, z, this.mc.player);
        }

        if (id > 0 && BlockList.blocks[id].breakStrength(this.mc.player, this.mc.world.getBlockMetadata(x, y, z)) >= 1.0F) {
            this.removeBlock(x, y, z, side);
        }
    }

    public void brushBlock(Player player, int x, int y, int z, int side) {
        if (this.hitX != x || this.hitY != y || this.hitZ != z) {
            player.clearItemInUse();
            this.hitX = x;
            this.hitY = y;
            this.hitZ = z;
        }

        Block target = BlockList.blocks[this.mc.world.getBlockId(this.hitX, this.hitY, this.hitZ)];
        int meta = this.mc.world.getBlockMetadata(this.hitX, this.hitY, this.hitZ);
        if (OptionValues.particleCount < 2 && !player.properties.isSpectator()) {
            this.mc.particleRenderer.spawnBlockHitParticles(this.hitX, this.hitY, this.hitZ, side);
            if (player.itemAnimFrames % 6 == 0 && target != null) {
                this.mc
                    .world
                    .playBlockSound(this.hitX, this.hitY, this.hitZ, target.getStepSound(meta).getSound(), 0.5F, this.mc.world.rand.nextFloat() * 0.1F + 0.9F);
            }
        }
    }

    public boolean removeBlock(int x, int y, int z, int side) {
        if (this.isSpectator()) {
            return false;
        }

        World world = this.mc.world;
        int id = world.getBlockId(x, y, z);
        int metadata = world.getBlockMetadata(x, y, z);
        Block block = BlockList.blocks[id];
        if (!this.isCreative()) {
            boolean canHarvest = this.mc.player.canHarvestBlock(block);
            ItemStack heldItem = this.mc.player.getCurrentEquippedItem();
            if (this.mc.player.getUsing()) {
                heldItem = this.mc.player.inventory.getOffHandItem();
            }

            if (heldItem != null) {
                if (block.hardness > 0.0F) {
                    heldItem.onDestroyBlock(side, x, y, z, this.mc.player);
                }

                if (heldItem.stackSize == 0) {
                    if (this.mc.player.getUsing()) {
                        this.mc.player.destroyOffhandItem();
                    } else {
                        this.mc.player.destroyCurrentEquippedItem();
                    }
                }

                if (heldItem.getItem() == ItemList.gauntletUriel) {
                    canHarvest = false;
                }
            }

            if (canHarvest) {
                block.dropItem(world, this.mc.player, x, y, z, metadata);
            }
        }

        if (block != null) {
            world.playEffect(2001, x, y, z, metadata << 12 | block.id);
            this.mc.player.addStat(StatList.mineStats[block.id], 1);
            this.mc.player.addStat(StatList.blocksBrokenStat, 1);
            if (block.id == BlockList.stone.id && this.mc.statistics.getStatCount(StatList.mineStats[block.id]) >= 3456) {
                this.mc.player.triggerAchievement(AchievementList.cobble);
            }
        }

        int meta = world.getBlockMetadata(x, y, z);
        boolean remove = world.setBlockWithNotify(x, y, z, 0);
        if (block != null && remove) {
            block.onBroken(world, x, y, z, meta);
        }

        return remove;
    }

    public void updateBreakTime(int x, int y, int z, int side) {
        if (this.isCreative() || this.isSpectator()) {
            return;
        }

        int id = this.mc.world.getBlockId(x, y, z);
        int meta = this.mc.world.getBlockMetadata(x, y, z);
        if (this.isAdventure() && (id == 0 || !this.canHarvestBlock(BlockList.blocks[id], meta))) {
            return;
        }

        if (this.blockHitDelay > 0) {
            this.blockHitDelay--;
        } else if (x == this.hitX && y == this.hitY && z == this.hitZ) {
            if (id == 0 || this.mc.player.isBlocking()) {
                return;
            }

            Block block = BlockList.blocks[id];
            this.curBlockDamage = this.curBlockDamage + block.breakStrength(this.mc.player, this.mc.world.getBlockMetadata(x, y, z));
            if (this.blockSoundCounter % 4.0F == 0.0F && block != null) {
                StepSound snd = block.getStepSound(meta);
                this.mc.sndManager.playBlockSound(snd.getSound(), x + 0.5F, y + 0.5F, z + 0.5F, (snd.getVolume() + 1.0F) / 8.0F, snd.getPitch() * 0.5F);
            }

            if (OptionValues.particleCount < 2) {
                this.mc.particleRenderer.spawnBlockHitParticles(x, y, z, Minecraft.getMinecraft().selectedObject.sideHit);
            }

            this.blockSoundCounter++;
            if (this.curBlockDamage >= 1.0F) {
                this.removeBlock(x, y, z, side);
                this.curBlockDamage = 0.0F;
                this.prevBlockDamage = 0.0F;
                this.blockSoundCounter = 0.0F;
                this.blockHitDelay = 5;
            }
        } else {
            this.curBlockDamage = 0.0F;
            this.prevBlockDamage = 0.0F;
            this.blockSoundCounter = 0.0F;
            this.hitX = x;
            this.hitY = y;
            this.hitZ = z;
        }
    }

    public boolean canHarvestBlock(Block block, int meta) {
        if (block.breakStrength(this.mc.player, meta) <= 0.0F) {
            return true;
        }

        ItemStack stack = this.mc.player.inventory.getStackInSlot(this.mc.player.inventory.currentSlot);
        if (this.mc.player.getUsing()) {
            stack = this.mc.player.inventory.getStackInSlot(44);
        }

        return stack != null ? stack.canHarvestBlock(block) : false;
    }

    public void resetBreakTime() {
        this.curBlockDamage = 0.0F;
        this.blockHitDelay = 0;
    }

    public void setBlockBreakTime(float time) {
        if (this.curBlockDamage <= 0.0F) {
            this.mc.gameRenderer.blocks.blockBreakTime = 0.0F;
        } else {
            float offset = this.prevBlockDamage + (this.curBlockDamage - this.prevBlockDamage) * time;
            this.mc.gameRenderer.blocks.blockBreakTime = offset;
        }
    }

    public float blockReach() {
        if (this.mc.player.getCurrentEquippedItem() != null
            && this.mc.player.getCurrentEquippedItem().getItem() instanceof Tool
            && ((Tool)this.mc.player.getCurrentEquippedItem().getItem()).toolMaterial == ToolMaterial.TOPAZ) {
            return 10.0F;
        } else {
            return this.mc.player.inventory.getOffHandItem() != null
                    && this.mc.player.inventory.getOffHandItem().getItem() instanceof Tool
                    && ((Tool)this.mc.player.inventory.getOffHandItem().getItem()).toolMaterial == ToolMaterial.TOPAZ
                ? 10.0F
                : (this.isCreative() ? 5.0F : 4.0F);
        }
    }

    public double entityReach() {
        double reach = this.isCreative() ? 6.0 : 4.0;
        return (this.mc.player.getCurrentEquippedItem() == null || this.mc.player.getCurrentEquippedItem().getItem() != ItemList.swordValkyrie)
                && (this.mc.player.inventory.getOffHandItem() == null || this.mc.player.inventory.getOffHandItem().getItem() != ItemList.swordValkyrie)
            ? reach
            : 8.0;
    }

    public boolean useItem(Player player, World world, ItemStack item) {
        if (player.properties.isSpectator()) {
            return false;
        }

        int stackSize = item.stackSize;
        ItemStack usedStack = item.useItemOnBlock(world, player);
        if (usedStack != item || usedStack != null && usedStack.stackSize != stackSize) {
            if (player.inventory.canUseItem()) {
                player.inventory.offhandSlot[0] = usedStack;
                if (usedStack.stackSize <= 0) {
                    player.inventory.offhandSlot[0] = null;
                }
            } else {
                player.inventory.mainInventory[player.inventory.currentSlot] = usedStack;
                if (usedStack.stackSize <= 0) {
                    player.inventory.mainInventory[player.inventory.currentSlot] = null;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public void flipPlayer(Player player) {
        player.yaw = -180.0F;
    }

    public void onUpdate() {
        if (this.isCreative()) {
            this.mc.sndManager.playRandomMusic(4);
        } else if (!this.mc.world.isDaytime() && this.mc.player.dimension == 0) {
            this.mc.sndManager.playRandomMusic(10);
        } else {
            this.mc.sndManager.playRandomMusic(this.mc.player.dimension);
        }

        this.prevBlockDamage = this.curBlockDamage;
    }

    public boolean shouldDrawHUD() {
        return !this.isCreative() && !this.isSpectator();
    }

    public void onRespawn(Player player) {
        if (player.properties.isSpectator()) {
            player.noClip = true;
            player.flying = true;
        }
    }

    public boolean useItemOnBlock(Player player, World world, ItemStack item, int x, int y, int z, int side) {
        if (player.properties.isSpectator()) {
            return false;
        }

        int currId = world.getBlockId(x, y, z);
        if (currId > 0 && !player.sneaking && BlockList.blocks[currId].blockActivated(world, x, y, z, player)) {
            return true;
        } else {
            return item == null ? false : item.useItem(player, world, x, y, z, side);
        }
    }

    public Player createPlayer(World world) {
        return new LocalPlayer(this.mc, world, this.mc.session);
    }

    public void interactWithEntity(Player player, Entity target) {
        if (player.properties.isSpectator()) {
            if (target instanceof Mob) {
                this.mc.gameRenderer.cameraMob = (Mob)target;
            }

            return;
        }

        player.useItemOnEntity(target);
    }

    public void attackEntity(Player player, Entity entity) {
        if (player.properties.isSpectator()) {
            if (entity instanceof Mob) {
                this.mc.gameRenderer.cameraMob = (Mob)entity;
            }

            return;
        }

        player.attackEntityWithItem(entity);
    }

    public ItemStack windowClick(int windowId, int slot, int click, boolean shift, Player player) {
        return player.openContainer.windowClick(slot, click, shift, player);
    }

    public void setCreativeSlot(int slot, ItemStack stack) {
    }

    public void containerSwapWithHotbar(int windowId, int slot, int hotbarIndex, Player player) {
        player.openContainer.swapWithHotbarSlot(slot, hotbarIndex, player);
    }

    public void closeScreen(int id, Player player) {
        player.openContainer.onClose(player);
        player.openContainer = player.container;
    }

    public void stopUsingItem(Player player) {
        player.stopUsingItem();
    }

    public void swapHands(ItemStack mainItem, ItemStack offHandItem) {
        this.mc.player.inventory.swapHands(this.mc.player.inventory.getCurrentItem(), this.mc.player.inventory.getOffHandItem());
    }

    public GuiScreen displayInventory() {
        if (this.isSpectator()) {
            return null;
        } else if (this.isCreative()) {
            return new CreativeScreen(this.mc.player);
        } else {
            return this.isAdventure() ? new AdventureScreen(this.mc.player) : new InventoryScreen(this.mc.player);
        }
    }
}
