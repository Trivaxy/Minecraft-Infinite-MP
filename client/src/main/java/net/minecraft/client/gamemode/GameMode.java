package net.minecraft.client.gamemode;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.GuiScreen;
import net.minecraft.client.gui.screens.container.inventory.InventoryScreen;
import net.minecraft.client.options.OptionValues;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.tools.Tool;
import net.minecraft.game.item.tools.ToolMaterial;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;
import net.minecraft.statistics.achievement.AchievementList;

public class GameMode {
    protected final Minecraft mc;
    protected int hitX = -1;
    protected int hitY = -1;
    protected int hitZ = -1;
    protected double entityDist = 4.0;
    protected float blockDist = 4.0F;

    public GameMode(Minecraft minecraft) {
        this.mc = minecraft;
        this.mc.gameRenderer.cameraMob = this.mc.player;
    }

    public void onWorldChange(World world) {
    }

    public void swapItem(int id, int meta) {
    }

    public void clickBlock(int x, int y, int z, int side) {
        this.mc.world.extinguishFire(this.mc.player, x, y, z, side);
        this.removeBlock(x, y, z, side);
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
        if (OptionValues.particleCount < 2 && player.properties.gamemode != 3) {
            this.mc.particleRenderer.spawnBlockHitParticles(this.hitX, this.hitY, this.hitZ, side);
            if (player.itemAnimFrames % 6 == 0 && target != null) {
                this.mc
                    .world
                    .playBlockSound(this.hitX, this.hitY, this.hitZ, target.getStepSound(meta).getSound(), 0.5F, this.mc.world.rand.nextFloat() * 0.1F + 0.9F);
            }
        }
    }

    public boolean removeBlock(int x, int y, int z, int side) {
        World world = this.mc.world;
        int metadata = world.getBlockMetadata(x, y, z);
        Block block = BlockList.blocks[world.getBlockId(x, y, z)];
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
    }

    public void resetBreakTime() {
    }

    public void setBlockBreakTime(float time) {
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
                : this.blockDist;
        }
    }

    public double entityReach() {
        return (this.mc.player.getCurrentEquippedItem() == null || this.mc.player.getCurrentEquippedItem().getItem() != ItemList.swordValkyrie)
                && (this.mc.player.inventory.getOffHandItem() == null || this.mc.player.inventory.getOffHandItem().getItem() != ItemList.swordValkyrie)
            ? this.entityDist
            : 8.0;
    }

    public boolean useItem(Player player, World world, ItemStack item) {
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
        if (!this.mc.world.isDaytime() && this.mc.player.dimension == 0) {
            this.mc.sndManager.playRandomMusic(10);
        } else {
            this.mc.sndManager.playRandomMusic(this.mc.player.dimension);
        }
    }

    public boolean shouldDrawHUD() {
        return true;
    }

    public void onRespawn(Player player) {
    }

    public boolean useItemOnBlock(Player player, World world, ItemStack item, int x, int y, int z, int side) {
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
        player.useItemOnEntity(target);
    }

    public void attackEntity(Player player, Entity entity) {
        player.attackEntityWithItem(entity);
    }

    public ItemStack windowClick(int windowId, int slot, int click, boolean shift, Player player) {
        return player.openContainer.windowClick(slot, click, shift, player);
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
        return new InventoryScreen(this.mc.player);
    }
}
