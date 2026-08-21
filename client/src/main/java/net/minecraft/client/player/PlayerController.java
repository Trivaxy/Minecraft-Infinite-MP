package net.minecraft.client.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshotter;
import net.minecraft.client.gui.screens.game.PhotoMode;
import net.minecraft.client.options.OptionValues;
import net.minecraft.client.options.OptionsList;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.boat.Boat;
import net.minecraft.game.entity.boat.LavaBoat;
import net.minecraft.game.entity.boat.Raft;
import net.minecraft.game.entity.hanging.Frame;
import net.minecraft.game.entity.hanging.Painting;
import net.minecraft.game.entity.minecart.Minecart;
import net.minecraft.game.entity.misc.VoidCrystal;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.misc.Bucket;
import net.minecraft.game.item.tools.Tool;
import net.minecraft.game.physics.TargetingSystem;

public class PlayerController {
    private OptionsList options;
    private Minecraft mc;
    public float moveStrafe = 0.0F;
    public float moveForward = 0.0F;
    public boolean jump = false;
    private int flyToggleTimer;
    private int sprintToggleTimer;
    private int leftClickCounter = 0;

    public PlayerController(Minecraft mc, OptionsList options) {
        this.options = options;
        this.mc = mc;
    }

    public void resetKeyState() {
        this.moveStrafe = 0.0F;
        this.moveForward = 0.0F;
    }

    public void detectInputs(Player player) {
        this.updateMovementKeys(player);
        this.updateInventoryKeys(player);
        this.updateMouseKeys(player);
        this.updateDebugKeys(player);
        if (this.leftClickCounter > 0) {
            this.leftClickCounter--;
        }
    }

    private void updateMouseKeys(Player player) {
        if (player.isUsingItem()) {
            if (!this.options.useKey.pressed) {
                this.mc.gamemode.stopUsingItem(player);
            }

            if (!this.options.attackKey.pressed()) {
                while (this.options.useKey.pressed()) {
                }

                while (this.options.pickKey.pressed()) {
                }
            }
        } else {
            while (this.options.attackKey.pressed()) {
                this.clickMouse(0);
                this.leftClickCounter = 5;
            }

            while (this.options.useKey.pressed()) {
                this.clickMouse(1);
                this.leftClickCounter = 5;
            }

            while (this.options.pickKey.pressed()) {
                this.pickBlock();
            }

            if (this.options.attackKey.pressed && this.leftClickCounter <= 0) {
                this.clickMouse(0);
                this.leftClickCounter = 5;
            }

            if (this.options.useKey.pressed && this.leftClickCounter <= 0) {
                this.clickMouse(1);
                this.leftClickCounter = 5;
            }
        }

        if (player.inventory.canUseItem() && player.inventory.getOffHandItem().getItem() instanceof Tool && this.options.useKey.pressed) {
            player.setUsing(true);
            this.clickBlock(0, this.mc.currentScreen == null && this.options.useKey.pressed && this.mc.inFocus);
        } else {
            player.setUsing(false);
            this.clickBlock(0, this.mc.currentScreen == null && this.options.attackKey.pressed && this.mc.inFocus);
        }

        boolean digWithMain = player.itemInUse != null
            && player.itemInUse == player.getCurrentEquippedItem()
            && player.getCurrentEquippedItem().getItemUseAction() == Item.Action.dig;
        boolean digWithOff = player.itemInUse != null
            && player.itemInUse == player.inventory.getOffHandItem()
            && player.inventory.getOffHandItem().getItemUseAction() == Item.Action.dig;
        if (player.isUsingItem()
            && (digWithMain || digWithOff)
            && this.mc.selectedObject != null
            && this.mc.selectedObject.target == TargetingSystem.HitType.TILE) {
            this.mc
                .gamemode
                .brushBlock(player, this.mc.selectedObject.blockX, this.mc.selectedObject.blockY, this.mc.selectedObject.blockZ, this.mc.selectedObject.sideHit);
        }
    }

    private void updateDebugKeys(Player player) {
        while (this.options.reloadKey.pressed()) {
            if (this.mc.world != null) {
                this.mc.worldRenderer.reloadChunks();
            }
        }

        while (this.options.texRefreshKey.pressed()) {
            this.mc.textureLoader.refreshTextures();
            this.mc.textureLoader.refreshTextureMaps();
            if (this.mc.world != null) {
                this.mc.worldRenderer.reloadChunks();
            }
        }

        while (this.options.isometricKey.pressed()) {
            this.mc.setScreen(new PhotoMode());
        }

        while (this.options.screenshotKey.pressed()) {
            this.mc.HUD.chatOverlay.addChatMessage(Screenshotter.takeScreenshot(Minecraft.getMinecraftDir(), this.mc.displayWidth, this.mc.displayHeight));
        }

        while (this.options.debugKey.pressed()) {
            OptionValues.showFPS = !OptionValues.showFPS;
            if (!OptionValues.showFPS) {
                OptionValues.advCoords = false;
            }
        }

        if (OptionValues.showFPS) {
            while (this.options.advancedDebugKey.pressed()) {
                OptionValues.advCoords = !OptionValues.advCoords;
            }
        }

        while (this.options.toggleHUDKey.pressed()) {
            OptionValues.showHUD = !OptionValues.showHUD;
        }

        while (this.options.thirdPersonKey.pressed()) {
            OptionValues.cameraMode = (OptionValues.cameraMode + 1) % (2 + OptionValues.cameraViews);
        }

        while (this.options.smoothCamKey.pressed()) {
            OptionValues.smoothCamera = !OptionValues.smoothCamera;
        }
    }

    private void updateInventoryKeys(Player player) {
        while (this.options.inventoryKey.pressed()) {
            this.mc.setScreen(this.mc.gamemode.displayInventory());
        }

        while (this.options.swapKey.pressed()) {
            if (player.itemAnimFrames <= 0) {
                this.mc.gamemode.swapHands(player.inventory.getCurrentItem(), player.inventory.getOffHandItem());
            }
        }

        while (this.options.dropKey.pressed()) {
            if (player.inventory.mainInventory[player.inventory.currentSlot] != null) {
                player.swingItem();
                if (this.mc.options.sneakKey.pressed) {
                    player.dropCurrentItem(player.getCurrentEquippedItem().stackSize);
                } else {
                    player.dropCurrentItem(1);
                }
            }
        }

        while (this.options.prevItemKey.pressed()) {
            player.inventory.changeCurrentItem(OptionValues.scrollInvert ? 1 : -1);
        }

        while (this.options.nextItemKey.pressed()) {
            player.inventory.changeCurrentItem(OptionValues.scrollInvert ? -1 : 1);
        }

        for (int slot = 0; slot < 9; slot++) {
            while (OptionsList.allKeys[15 + slot].pressed()) {
                player.inventory.currentSlot = slot;
                if (player.inventory.getCurrentItem() != null) {
                    player.inventory.getCurrentItem().textTime = 30.0F;
                }

                if (Minecraft.isDebugScreenFull()) {
                    this.mc.HUD.updateDebugProfilerName(slot);
                }
            }
        }
    }

    private void updateMovementKeys(Player player) {
        if (!player.sitting) {
            float forwardOffset = 0.8F;
            boolean movementFlag = this.moveForward >= forwardOffset;
            this.moveStrafe = 0.0F;
            this.moveForward = 0.0F;
            if (this.options.forwardKey.pressed) {
                this.moveForward++;
            }

            if (this.options.backwardKey.pressed) {
                this.moveForward--;
            }

            if (this.options.leftKey.pressed) {
                this.moveStrafe++;
            }

            if (this.options.rightKey.pressed) {
                this.moveStrafe--;
            }

            if (player.isUsingItem() && player.itemInUse.getItemUseAction() != Item.Action.joust) {
                this.moveStrafe *= 0.2F;
                this.moveForward *= 0.2F;
                this.sprintToggleTimer = 0;
            }

            boolean canToggleSprint = player.canSprint() && !player.isSprinting() && player.properties.getSprint() && this.moveForward >= forwardOffset;
            if (this.mc.options.sprintKey.pressed && canToggleSprint) {
                player.setSprinting(true);
            }

            if (this.sprintToggleTimer > 0) {
                this.sprintToggleTimer--;
            }

            if (player.onGround && !movementFlag && canToggleSprint) {
                if (this.sprintToggleTimer <= 0) {
                    this.sprintToggleTimer = 7;
                } else {
                    player.setSprinting(true);
                    this.sprintToggleTimer = 0;
                }
            }

            boolean jumpFlag = this.jump;
            this.jump = this.options.jumpKey.pressed;
            if (this.flyToggleTimer > 0) {
                this.flyToggleTimer--;
            }

            if (!player.sneaking && !jumpFlag && this.jump && !player.isRiding()) {
                if (this.flyToggleTimer <= 0) {
                    this.flyToggleTimer = 7;
                } else {
                    ItemStack elytraSlot = player.inventory.accessorySlots[2];
                    if (elytraSlot != null && elytraSlot.getItem() == ItemList.elytra && elytraSlot.getItemDamage() < elytraSlot.getMaxDamage()) {
                        if (!player.isGliding() && !player.isInWater()) {
                            player.setGliding();
                        } else {
                            player.clearGliding();
                        }
                    } else if (player.properties.isCreative()) {
                        player.flying = !player.flying;
                    }

                    this.flyToggleTimer = 0;
                }
            }

            while (this.options.crawlKey.pressed()) {
                player.crawl();
            }

            if (!OptionValues.sneakMode && player.onGround && !player.isInWater() && !player.isPlayerSleeping()) {
                while (this.options.sneakKey.pressed()) {
                    player.setSneaking();
                }
            }

            if (!player.sneaking && player.properties.isCreative() && !player.isRiding()) {
                while (this.options.flightKey.pressed()) {
                    player.flying = !player.flying;
                }
            }

            if (player.flying && this.mc.options.jumpKey.pressed && this.mc.currentScreen == null) {
                player.motionY += 0.3F;
            }

            if (player.flying && this.mc.options.sneakKey.pressed && this.mc.currentScreen == null) {
                player.motionY -= 0.3F;
            }

            if (OptionValues.sneakMode && !player.sleeping && !player.crawling && !player.sitting) {
                if (this.mc.options.sneakKey.pressed) {
                    player.sneaking = true;
                    player.yOffset = 1.42F;
                } else {
                    player.sneaking = false;
                    player.yOffset = 1.62F;
                }
            }
        }
    }

    public void clickMouse(int buttonClick) {
        boolean canSwing = this.mc.player.inventory.canUseItem()
            && this.mc.player.inventory.getOffHandItem().getItem().canSwing
            && !this.mc.player.isUsingItem();
        if (buttonClick == 0) {
            this.mc.player.swingItem();
        }

        if (buttonClick == 1) {
            if (canSwing) {
                this.mc.player.swingOffhandItem();
            }

            if (this.mc.player.properties.isSpectator()) {
                this.mc.gameRenderer.cameraMob = this.mc.player;
            }
        }

        boolean canInteractWithBlock = true;
        if (this.mc.selectedObject == null) {
            if (buttonClick == 1 && canSwing) {
                this.triggerAttackDelay();
            }

            if (buttonClick == 0) {
                this.triggerAttackDelay();
            }
        } else if (this.mc.selectedObject.target == TargetingSystem.HitType.ENTITY) {
            if (buttonClick == 0 && this.mc.selectedObject.entityHit != this.mc.player.mount) {
                this.mc.gamemode.attackEntity(this.mc.player, this.mc.selectedObject.entityHit);
                this.triggerAttackDelay();
            }

            if (buttonClick == 1) {
                if (this.mc.player.inventory.getOffHandItem() != null && canSwing && this.mc.selectedObject.entityHit != this.mc.player.mount) {
                    this.mc.player.setUsing(true);
                    this.mc.player.swingOffhandItem();
                    if (!this.mc.player.inventory.getOffHandItem().getItem().isWeapon) {
                        this.mc.gamemode.interactWithEntity(this.mc.player, this.mc.selectedObject.entityHit);
                    } else if (this.mc.player.getCurrentEquippedItem() != null && this.mc.player.getCurrentEquippedItem().getItem().priorityItem) {
                        this.mc.player.setUsing(false);
                        this.mc.gamemode.interactWithEntity(this.mc.player, this.mc.selectedObject.entityHit);
                    } else {
                        this.mc.gamemode.attackEntity(this.mc.player, this.mc.selectedObject.entityHit);
                        this.triggerAttackDelay();
                    }
                } else {
                    this.mc.player.setUsing(false);
                    this.mc.gamemode.interactWithEntity(this.mc.player, this.mc.selectedObject.entityHit);
                }
            }

            ItemStack activeStack = this.mc.player.inventory.getCurrentItem();
            if (this.mc.player.inventory.canUseItem()) {
                activeStack = this.mc.player.inventory.getOffHandItem();
            }

            if (activeStack != null && activeStack.getItem() instanceof Bucket) {
                return;
            }
        } else if (this.mc.selectedObject.target == TargetingSystem.HitType.TILE) {
            int x = this.mc.selectedObject.blockX;
            int y = this.mc.selectedObject.blockY;
            int z = this.mc.selectedObject.blockZ;
            int face = this.mc.selectedObject.sideHit;
            if (buttonClick == 0) {
                if (!this.mc.world.containsFire(x, y, z, this.mc.selectedObject.sideHit)) {
                    this.mc.gamemode.clickBlock(x, y, z, this.mc.selectedObject.sideHit);
                }
            } else {
                ItemStack activeStack = this.mc.player.inventory.getCurrentItem();
                if (this.mc.player.inventory.canUseItem()) {
                    activeStack = this.mc.player.inventory.getOffHandItem();
                    if (this.mc.player.inventory.getOffHandItem().getItem() instanceof Tool) {
                        this.mc.player.setUsing(true);
                    }
                } else {
                    this.mc.player.setUsing(false);
                }

                if (this.mc.gamemode.useItemOnBlock(this.mc.player, this.mc.world, activeStack, x, y, z, face)) {
                    canInteractWithBlock = false;
                    if (this.mc.player.inventory.canUseItem()) {
                        this.mc.player.swingOffhandItem();
                    } else {
                        this.mc.player.swingItem();
                    }
                }

                if (activeStack == null) {
                    return;
                }

                if (this.mc.player.inventory.getCurrentItem() != null && this.mc.player.inventory.getCurrentItem().stackSize <= 0) {
                    this.mc.player.inventory.mainInventory[this.mc.player.inventory.currentSlot] = null;
                } else if (this.mc.player.inventory.getOffHandItem() != null && this.mc.player.inventory.getOffHandItem().stackSize <= 0) {
                    this.mc.player.inventory.offhandSlot[0] = null;
                }
            }
        }

        if (canInteractWithBlock && buttonClick == 1) {
            ItemStack activeStack = this.mc.player.inventory.getCurrentItem();
            if (this.mc.player.inventory.canUseItem()) {
                activeStack = this.mc.player.inventory.getOffHandItem();
            }

            if (activeStack != null) {
                this.mc.gamemode.useItem(this.mc.player, this.mc.world, activeStack);
            }
        }
    }

    public void clickBlock(int buttonClick, boolean canBreakBlock) {
        if (!this.mc.player.properties.isCreative() && !this.mc.player.properties.isSpectator()) {
            if (canBreakBlock && this.mc.selectedObject != null && this.mc.selectedObject.target == TargetingSystem.HitType.TILE && buttonClick == 0) {
                this.mc
                    .gamemode
                    .updateBreakTime(
                        this.mc.selectedObject.blockX, this.mc.selectedObject.blockY, this.mc.selectedObject.blockZ, this.mc.selectedObject.sideHit
                    );
            } else {
                this.mc.gamemode.resetBreakTime();
            }
        }
    }

    public void pickBlock() {
        if (this.mc.selectedObject != null) {
            if (this.mc.selectedObject.target == TargetingSystem.HitType.TILE) {
                int id = this.mc.world.getBlockId(this.mc.selectedObject.blockX, this.mc.selectedObject.blockY, this.mc.selectedObject.blockZ);
                int metadata = 0;
                Block block = BlockList.blocks[id];
                metadata = this.mc.world.getBlockMetadata(this.mc.selectedObject.blockX, this.mc.selectedObject.blockY, this.mc.selectedObject.blockZ);
                if (block.pickMeta(metadata) != -1) {
                    metadata = block.pickMeta(metadata);
                }

                id = block.pickID(metadata);
                this.mc.gamemode.swapItem(id, metadata);
                this.mc.gameRenderer.itemRenderer.resetEquippedProgress();
            }

            if (this.mc.selectedObject.target == TargetingSystem.HitType.ENTITY) {
                if (this.mc.selectedObject.entityHit instanceof Mob) {
                    this.mc.gamemode.swapItem(ItemList.spawnEgg.id, this.mc.selectedObject.entityHit.getSpawnID());
                }

                if (this.mc.selectedObject.entityHit instanceof Painting) {
                    int id = ItemList.painting.id;
                    if (this.mc.selectedObject.entityHit instanceof Frame) {
                        id = this.mc.selectedObject.entityHit.canGlow ? ItemList.itemFrameGlowing.id : ItemList.itemFrame.id;
                    }

                    this.mc.gamemode.swapItem(id, 0);
                }

                if (this.mc.selectedObject.entityHit instanceof Minecart) {
                    int chosenCart = ItemList.minecart.id;
                    switch (((Minecart)this.mc.selectedObject.entityHit).type) {
                        case 1:
                            chosenCart = ItemList.minecartChest.id;
                            break;
                        case 2:
                            chosenCart = ItemList.minecartFurnace.id;
                            break;
                        case 3:
                            chosenCart = ItemList.minecartExplosive.id;
                    }

                    this.mc.gamemode.swapItem(chosenCart, 0);
                }

                if (this.mc.selectedObject.entityHit instanceof Boat) {
                    this.mc.gamemode.swapItem(ItemList.boat.id, 0);
                }

                if (this.mc.selectedObject.entityHit instanceof LavaBoat) {
                    this.mc.gamemode.swapItem(ItemList.boatObsidian.id, 0);
                }

                if (this.mc.selectedObject.entityHit instanceof Raft) {
                    this.mc.gamemode.swapItem(ItemList.raft.id, 0);
                }

                if (this.mc.selectedObject.entityHit instanceof VoidCrystal) {
                    this.mc.gamemode.swapItem(ItemList.endCrystal.id, 0);
                }
            }
        }
    }

    private void triggerAttackDelay() {
        ItemStack mainhand = this.mc.player.inventory.getCurrentItem();
        ItemStack offhand = this.mc.player.inventory.getOffHandItem();
        if (this.mc.player.properties.getAttack()) {
            if (this.mc.player.swingingOffhand && offhand != null) {
                this.mc.player.itemUseCooldown = this.mc.player.maxDelay = offhand.getItem().attackDelay;
            } else if (mainhand != null) {
                this.mc.player.itemUseCooldown = this.mc.player.maxDelay = mainhand.getItem().attackDelay;
            } else {
                this.mc.player.itemUseCooldown = this.mc.player.maxDelay = 5;
            }
        }
    }
}
