package net.minecraft.client.gui.overlay;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import net.minecraft.Profiler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gamemode.CreativeMode;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.ScreenRescaler;
import net.minecraft.client.gui.screens.game.PhotoMode;
import net.minecraft.client.localization.Font;
import net.minecraft.client.network.ClientHandler;
import net.minecraft.client.network.player.NetworkPlayer;
import net.minecraft.client.options.OptionValues;
import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.container.player.PlayerContainer;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.player.FoodData;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.misc.WrittenBook;
import net.minecraft.game.item.potion.Effect;
import net.minecraft.game.item.potion.EffectData;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.physics.TargetingSystem;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.util.LightLayer;
import net.minecraft.localization.Translator;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

public class HeadsUpDisplay extends GuiComponent {
    private static final String[] faces = new String[]{
        Translator.getInstance().translateKey("gui.west"),
        Translator.getInstance().translateKey("gui.north"),
        Translator.getInstance().translateKey("gui.east"),
        Translator.getInstance().translateKey("gui.south")
    };
    private static final String[] dimensions = new String[]{
        Translator.getInstance().translateKey("gui.overworld"),
        Translator.getInstance().translateKey("gui.hell"),
        Translator.getInstance().translateKey("gui.sky"),
        Translator.getInstance().translateKey("gui.void"),
        Translator.getInstance().translateKey("gui.ocean"),
        Translator.getInstance().translateKey("gui.forest")
    };
    public static long[] tickTimes = new long[512];
    public static long[] frameTimes = new long[512];
    private static ItemRenderer itemRenderer = new ItemRenderer(Minecraft.getMinecraft());
    private Random rand = new Random();
    private Minecraft mc;
    public AchievementToast achievement;
    public final MusicOverlay nowPlaying = new MusicOverlay();
    public ChatOverlay chatOverlay;
    private String profilerName = "root";
    private float tintBrightness = 0.0F;
    private float vignetteBrightness = 1.0F;
    private int updateCounter = 0;
    public static int numRecordedFrameTimes = 0;
    public long prevFrameTime = -1L;

    public HeadsUpDisplay(Minecraft minecraft) {
        this.mc = minecraft;
        this.achievement = new AchievementToast(minecraft);
        this.chatOverlay = new ChatOverlay(minecraft);
    }

    public void render(float tickDelta) {
        int width = this.mc.rescaler.getScaledWidth();
        int height = this.mc.rescaler.getScaledHeight();
        Font font = this.mc.fontRenderer;
        this.mc.gameRenderer.setupOverlayRendering();
        if (OptionValues.fancyGraphics && this.mc.world.isRaining() && this.mc.player.dimension == 0) {
            this.renderTint(
                (float)(
                    this.mc.world.getRainStrength(tickDelta)
                        * (
                            this.mc
                                    .world
                                    .getLayerBrightness(
                                        LightLayer.Sky,
                                        MathHelper.floor_double(this.mc.player.posX),
                                        MathHelper.floor_double(this.mc.player.posY),
                                        MathHelper.floor_double(this.mc.player.posZ)
                                    )
                                / 15.0
                        )
                ),
                width,
                height,
                0.5F,
                0.5F,
                0.5F
            );
        }

        if (OptionValues.showHUD && !(this.mc.currentScreen instanceof PhotoMode)) {
            if (this.mc.player.properties.gamemode != 3) {
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
                if (OptionValues.fancyGraphics) {
                    this.renderVignette(this.mc.player.getBrightness(tickDelta), width, height);
                }

                this.renderBossHealth();
                this.displayStatusEffects(tickDelta);
                if (this.mc.player.freeze > 0) {
                    this.renderFrost(width, height);
                }

                int hotbarOffset = 2;
                this.renderHotbar(tickDelta, width, height - hotbarOffset);
                Lighting.disable();
                GL11.glDisable(32826);
                GL11.glBindTexture(3553, this.mc.textureLoader.getTexture("/gui/icons.png"));
                this.renderCrosshair(width, height);
                this.renderIcons(tickDelta, width, height - hotbarOffset);
                this.renderSubtitle(tickDelta, width, height - hotbarOffset);
                if (OptionValues.showItems) {
                    this.renderPickup(font, tickDelta, width, height);
                }
            }

            this.renderSleepOverlay(width, height);
            if (OptionValues.showFPS) {
                this.renderDebugText(font, tickDelta, width, height);
            } else {
                font.drawStringWithShadow(Translator.getInstance().translateKey("minecraft.version"), 2, 2, 16777215);
            }

            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(3008);
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, height - 48, 0.0F);
            this.chatOverlay.updateChat(font);
            GL11.glPopMatrix();
            this.renderPlayerList(width, height);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(2896);
            GL11.glEnable(3008);
            this.nowPlaying.display(font, tickDelta, width, height);
        }
    }

    public void update() {
        this.updateCounter++;

        for (int i1 = 0; i1 < this.chatOverlay.activeChatMessages.size(); i1++) {
            this.chatOverlay.activeChatMessages.get(i1).updateCounter++;
        }

        if (this.nowPlaying.duration > 0) {
            this.nowPlaying.duration--;
        }
    }

    private void renderDebugText(Font font, float tickDelta, int width, int height) {
        font.drawStringWithBackground("Minecraft Infinite v1.0", 2, 2, 16777215);
        font.drawStringWithBackground(this.mc.debugString, 2, 12, 16777215);
        font.drawStringWithBackground(this.mc.debugInfoRenders(), 2, 32, 16777215);
        font.drawStringWithBackground(this.mc.debugInfoEntities(), 2, 42, 16777215);
        font.drawStringWithBackground(this.mc.debugInfoLight(), 2, 52, 16777215);
        String target = "empty";
        if (this.mc.selectedObject != null) {
            if (this.mc.selectedObject.target == TargetingSystem.HitType.TILE) {
                Block block = BlockList.blocks[this.mc
                    .world
                    .getBlockId(this.mc.selectedObject.blockX, this.mc.selectedObject.blockY, this.mc.selectedObject.blockZ)];
                int meta = this.mc.world.getBlockMetadata(this.mc.selectedObject.blockX, this.mc.selectedObject.blockY, this.mc.selectedObject.blockZ);
                target = block.name
                    + " ("
                    + block.id
                    + ":"
                    + meta
                    + ") at "
                    + this.mc.selectedObject.blockX
                    + "x "
                    + this.mc.selectedObject.blockY
                    + "y "
                    + this.mc.selectedObject.blockZ
                    + "z, side: "
                    + this.mc.selectedObject.sideHit;
            } else if (this.mc.selectedObject.target == TargetingSystem.HitType.ENTITY) {
                target = this.mc.selectedObject.entityHit.getEntityString() + ", ID: " + this.mc.selectedObject.entityHit.entityId;
            }
        }

        font.drawStringWithBackground("Selected: " + target, 2, 72, 16777215);
        font.drawStringWithBackground("Yaw: " + (long)(this.mc.player.yaw % 360.0F) + ", Pitch: " + (long)this.mc.player.pitch, 2, 82, 16777215);
        font.drawStringWithBackground("Facing: " + faces[MathHelper.floor_double(this.mc.player.yaw * 4.0F / 360.0F + 0.5) & 3], 2, 92, 16777215);
        int shift;
        if (OptionValues.advCoords) {
            font.drawStringWithBackground("X: " + this.mc.player.posX, 2, 112, 16777215);
            font.drawStringWithBackground("Y: " + this.mc.player.posY, 2, 122, 16777215);
            font.drawStringWithBackground("Z: " + this.mc.player.posZ, 2, 132, 16777215);
            shift = 20;
        } else {
            font.drawStringWithBackground(
                "Position: " + (long)this.mc.player.posX + "x, " + (long)this.mc.player.posY + "y, " + (long)this.mc.player.posZ + "z", 2, 112, 16777215
            );
            shift = 0;
        }

        font.drawStringWithBackground(
            "ChunkPos: "
                + (int)(this.mc.player.posX - this.mc.player.chunkX * 16)
                + "x, "
                + (int)(this.mc.player.posY - this.mc.player.chunkY * 16)
                + "y, "
                + (int)(this.mc.player.posZ - this.mc.player.chunkZ * 16)
                + "z in "
                + this.mc.player.chunkX
                + "x, "
                + this.mc.player.chunkY
                + "y, "
                + this.mc.player.chunkZ
                + "z",
            2,
            122 + shift,
            16777215
        );
        font.drawStringWithBackground(this.mc.debugChunkProviderInfo(), 2, 132 + shift, 16777215);
        font.drawStringWithBackground("Seed: " + this.mc.world.getSeed(), 2, 152 + shift, 16777215);
        font.drawStringWithBackground("Day: " + (int)((float)this.mc.world.getWorldTime() / this.mc.world.settings.dayLength), 2, 162 + shift, 16777215);
        Biome biome = this.mc.world.getBiomeAt((int)this.mc.player.posX, (int)this.mc.player.posZ);
        String biomeStr = "null";
        if (biome != null) {
            biomeStr = biome.getName();
        }

        font.drawStringWithBackground("Biome: " + biomeStr, 2, 172 + shift, 16777215);
        font.drawStringWithBackground("Dimension: " + dimensions[this.mc.player.dimension], 2, 182 + shift, 16777215);
        long maxMemory = Runtime.getRuntime().maxMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long usedMemory = totalMemory - freeMemory;
        String memoryString = "Used memory: "
            + usedMemory * 100L / maxMemory
            + "% ("
            + usedMemory / 1024L / 1024L
            + "MB) of "
            + maxMemory / 1024L / 1024L
            + "MB";
        font.drawStringWithBackground(memoryString, width - font.getStringWidth(memoryString) - 2, 2, 14737632);
        memoryString = "Allocated memory: " + totalMemory * 100L / maxMemory + "% (" + totalMemory / 1024L / 1024L + "MB)";
        font.drawStringWithBackground(memoryString, width - font.getStringWidth(memoryString) - 2, 12, 14737632);
        width -= 2;
        memoryString = "GPU: " + GL11.glGetString(7937);
        font.drawStringWithBackground(memoryString, width - font.getStringWidth(memoryString), 32, 14737632);
        memoryString = "Driver: " + GL11.glGetString(7938) + ", " + this.mc.displayWidth + "x" + this.mc.displayHeight;
        font.drawStringWithBackground(memoryString, width - font.getStringWidth(memoryString), 42, 14737632);
        memoryString = "LWJGL Version: " + Sys.getVersion();
        font.drawStringWithBackground(memoryString, width - font.getStringWidth(memoryString), 62, 14737632);
        memoryString = "OS: " + System.getProperty("os.name") + " " + System.getProperty("os.arch");
        font.drawStringWithBackground(memoryString, width - font.getStringWidth(memoryString), 72, 14737632);
        memoryString = "Java Version: " + System.getProperty("java.version");
        font.drawStringWithBackground(memoryString, width - font.getStringWidth(memoryString), 82, 14737632);
    }

    private void renderCrosshair(int width, int height) {
        if (OptionValues.cameraMode == 0 && !OptionValues.advCoords) {
            GL11.glEnable(3042);
            GL11.glBlendFunc(775, 769);
            if (this.mc.selectedObject != null && this.mc.selectedObject.entityHit != null && this.mc.selectedObject.entityHit != this.mc.player.mount) {
                this.drawGraphic(width / 2 - 7, height / 2 - 7, 0, 16, 16, 16);
            } else {
                this.drawGraphic(width / 2 - 7, height / 2 - 7, 0, 0, 16, 16);
            }

            if (this.mc.player.itemUseCooldown > 0) {
                this.drawGraphic(width / 2 - 7, height / 2, 32, 64, 16, 16);
                float maxCooldown = this.mc.player.maxDelay;
                float progress = 1.0F - this.mc.player.itemUseCooldown / maxCooldown;
                progress = Math.max(0.0F, Math.min(1.0F, progress));
                int cooldownWidth = (int)(progress * 16.0F);
                this.drawGraphic(width / 2 - 7, height / 2, 48, 64, cooldownWidth, 16);
            }

            GL11.glDisable(3042);
        }
    }

    private void renderIcons(float tickDelta, int width, int height) {
        int health = this.mc.player.health;
        int prevHealth = this.mc.player.prevHealth;
        int maxHealth = this.mc.player.maxHealth;
        boolean damaged = this.mc.player.damage / 3 % 2 == 1;
        if (this.mc.player.damage < 10) {
            damaged = false;
        }

        if (this.mc.player.mount != null && this.mc.player.mount instanceof Mob) {
            health = ((Mob)this.mc.player.mount).health;
            prevHealth = ((Mob)this.mc.player.mount).prevHealth;
            maxHealth = ((Mob)this.mc.player.mount).maxHealth;
            damaged = ((Mob)this.mc.player.mount).damage / 3 % 2 == 1;
            if (((Mob)this.mc.player.mount).damage < 10) {
                damaged = false;
            }
        }

        this.rand.setSeed(this.updateCounter * 312871);
        boolean hungerChange = false;
        boolean isHungerEnabled = this.mc.player.properties.getHunger();
        FoodData foodStats = this.mc.player.getFoodStats();
        int hunger = foodStats.getHunger();
        int prevHunger = foodStats.prevHunger();
        int armor = this.mc.player.getTotalArmorValue(null);
        if (this.mc.gamemode.shouldDrawHUD()) {
            int maxStamina = this.mc.player.maxStamina;
            int offset = 40;
            int x = width / 2 - 93;
            int y = height - 32 + 3;
            int isFull = 47;
            byte xOffset = 0;
            byte yOffset = 0;
            if (this.mc.player.hasEffect(EffectList.stamina)) {
                isFull = 57;
            } else if (!this.mc.player.canSprint() || foodStats.getHunger() < 6.0F) {
                isFull = 52;
            }

            if (this.mc.player.properties.getSprint() && !this.mc.player.properties.getHunger()) {
                this.drawGraphic(x, y, 20, 42, 184, 5);
                if (maxStamina > 0) {
                    int var22 = (int)(this.mc.player.stamina * 0.92F);
                    this.drawGraphic(x, y, 20, isFull, var22, 5);
                } else if (maxStamina < 0) {
                    this.drawGraphic(x, y, 20, 32, 184, 5);
                }
            } else {
                offset = 33;
            }

            for (int arm = 0; arm < 10; arm++) {
                int newY = height - offset;
                if (armor > 0) {
                    int newX = width / 2 + 91 - arm * 8 - 9;
                    int armorOffset = 25;
                    if (isHungerEnabled) {
                        newX = x + arm * 8 + 2;
                        newY = height - (offset + 10);
                        armorOffset = 43;
                        if (maxHealth > 20) {
                            if (health > maxHealth && maxHealth > 30) {
                                newY = height - (offset + 30);
                            } else {
                                newY = height - (offset + 20);
                            }
                        } else if (health > maxHealth) {
                            newY = height - (offset + 20);
                        }

                        if (this.mc.player.mount != null && this.mc.player.mount instanceof Mob) {
                            break;
                        }
                    }

                    if (arm * 2 + 1 < armor) {
                        this.drawGraphic(newX, newY, 34, 9, 9, 9);
                    }

                    if (arm * 2 + 1 == armor) {
                        this.drawGraphic(newX, newY, armorOffset, 9, 9, 9);
                    }

                    if (arm * 2 + 1 > armor) {
                        this.drawGraphic(newX, newY, 16, 9, 9, 9);
                    }
                }
            }

            for (int hp = 0; hp < maxHealth / 2; hp++) {
                int newY = height - offset - hp / 10 * 10;
                int healthOffset = 16;
                if (this.mc.player.mount != null && this.mc.player.mount instanceof Mob) {
                    healthOffset += 126;
                } else if (this.mc.player.hasEffect(EffectList.poison)) {
                    healthOffset += 36;
                } else if (this.mc.player.hasEffect(EffectList.wither)) {
                    healthOffset += 90;
                }

                if (damaged) {
                    xOffset = 1;
                }

                int newX = width / 2 - 91 + hp * 8 - hp / 10 * (hp / 10) * 80;
                if (health <= 4) {
                    newY += this.rand.nextInt(2);
                }

                if (this.mc.player.properties.getPermadeath() && (this.mc.player.mount == null || !(this.mc.player.mount instanceof Mob))) {
                    yOffset = 1;
                }

                this.drawGraphic(newX, newY, 16 + xOffset * 9, 0, 9, 9);
                if (damaged) {
                    if (hp * 2 + 1 < prevHealth) {
                        this.drawGraphic(newX, newY, healthOffset + 54, 9 * yOffset, 9, 9);
                    }

                    if (hp * 2 + 1 == prevHealth) {
                        this.drawGraphic(newX, newY, healthOffset + 63, 9 * yOffset, 9, 9);
                    }
                }

                if (hp * 2 + 1 < health) {
                    this.drawGraphic(newX, newY, healthOffset + 36, 9 * yOffset, 9, 9);
                }

                if (hp * 2 + 1 == health) {
                    this.drawGraphic(newX, newY, healthOffset + 45, 9 * yOffset, 9, 9);
                }
            }

            if (health > maxHealth && this.mc.player.mount == null) {
                for (int goldHP = 0; goldHP < 10; goldHP++) {
                    int heartHeight = maxHealth <= 20 ? 10 : 20;
                    int newY = height - (offset + heartHeight);
                    int newX = width / 2 - 91 + (goldHP << 3);
                    if (maxHealth > 20 && maxHealth <= 30) {
                        newX += 8 * ((maxHealth - 20) / 2);
                        newY += 10;
                    }

                    if (this.mc.player.properties.getPermadeath() && this.mc.player.mount == null) {
                        yOffset = 1;
                    }

                    int healthOffset = 0;
                    if (this.mc.player.mount != null && this.mc.player.mount instanceof Mob) {
                        healthOffset += 54;
                    }

                    if ((goldHP << 1) + 1 < health - this.mc.player.maxHealth) {
                        this.drawGraphic(newX, newY, 16 + xOffset * 9, 0, 9, 9);
                        this.drawGraphic(newX - 1, newY, 123 + healthOffset, 9 * yOffset, 9, 9);
                    }

                    if ((goldHP << 1) + 1 == health - this.mc.player.maxHealth) {
                        this.drawGraphic(newX, newY, 16 + xOffset * 9, 0, 9, 9);
                        this.drawGraphic(newX - 1, newY, 132 + healthOffset, 9 * yOffset, 9, 9);
                    }
                }
            }

            if (isHungerEnabled) {
                for (int foodlvl = 0; foodlvl < 10; foodlvl++) {
                    int newY = y - 4;
                    int newOffset = 34;
                    byte innerGraphic = 0;
                    if (this.mc.player.hasEffect(EffectList.hunger)) {
                        newOffset += 36;
                        innerGraphic = 13;
                    }

                    if (foodStats.getSaturation() <= 0.0F && this.updateCounter % (hunger * 3 + 1) == 0) {
                        newY += this.rand.nextInt(3) - 1;
                    }

                    if (hungerChange) {
                        innerGraphic = 1;
                    }

                    int newX = x - foodlvl * 8 + 175;
                    this.drawGraphic(newX, newY, 34 + innerGraphic * 9, 18, 9, 9);
                    if (hungerChange) {
                        if (foodlvl * 2 + 1 < prevHunger) {
                            this.drawGraphic(newX, newY, newOffset + 54, 18, 9, 9);
                        }

                        if (foodlvl * 2 + 1 == prevHunger) {
                            this.drawGraphic(newX, newY, newOffset + 63, 18, 9, 9);
                        }
                    }

                    if (foodlvl * 2 + 1 < hunger) {
                        this.drawGraphic(newX, newY, newOffset + 36, 18, 9, 9);
                    }

                    if (foodlvl * 2 + 1 == hunger) {
                        this.drawGraphic(newX, newY, newOffset + 45, 18, 9, 9);
                    }
                }
            }

            if (this.mc.player.isInMaterial(MaterialList.water) || this.mc.player.air < this.mc.player.maxAir) {
                double maxAir = this.mc.player.maxAir;
                int air = (int)Math.ceil((this.mc.player.air - 2) * 10.0 / maxAir);
                int prevAir = (int)Math.ceil(this.mc.player.air * 10.0 / maxAir) - air;
                int offsetY = 10;
                if (isHungerEnabled) {
                    offsetY = 10;
                } else if (width > 20) {
                    if (armor > 0) {
                        offsetY = 10;
                    } else {
                        offsetY = 0;
                    }
                }

                for (int curAir = 0; curAir < air + prevAir; curAir++) {
                    if (curAir < air) {
                        if (width > 20) {
                            this.drawGraphic(width / 2 + 82 - (curAir << 3), height - offset - offsetY, 16, 18, 9, 9);
                        } else {
                            this.drawGraphic(width / 2 - 91 + (curAir << 3), height - offset - offsetY, 16, 18, 9, 9);
                        }
                    } else if (width > 20) {
                        this.drawGraphic(width / 2 + 82 - (curAir << 3), height - offset - offsetY, 25, 18, 9, 9);
                    } else {
                        this.drawGraphic(width / 2 - 91 + (curAir << 3), height - offset - offsetY, 25, 18, 9, 9);
                    }
                }
            }
        }
    }

    private void renderHotbar(float tickDelta, int width, int height) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glBindTexture(3553, this.mc.textureLoader.getTexture("/gui/gui.png"));
        this.zLevel = -90.0F;
        this.drawGraphic(width / 2 - 91, height - 22, 0, 0, 182, 22);
        this.drawGraphic(width / 2 - 91 - 1 + this.mc.player.inventory.currentSlot * 20, height - 22 - 1, 182, 0, 24, 24);
        if (this.mc.player.inventory.getOffHandItem() != null) {
            this.drawGraphic(width / 2 - 118, height - 22, 211, 0, 22, 23);
        }

        if (this.mc.player.inventory.accessorySlots[2] != null && this.mc.player.inventory.accessorySlots[2].itemID == ItemList.quiver.id) {
            this.drawGraphic(width / 2 + 96, height - 22, 211, 0, 22, 23);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(32826);
        GL11.glPushMatrix();
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        Lighting.enable();
        GL11.glPopMatrix();

        for (int slot = 0; slot < 9; slot++) {
            int x = width / 2 - 90 + slot * 20 + 2;
            int y = height - 16 - 3;
            this.renderItemInSlot(x, y, tickDelta, this.mc.player.inventory.mainInventory[slot]);
        }

        if (this.mc.player.inventory.getOffHandItem() != null) {
            int x = width / 2 - 115;
            int y = height - 16 - 3;
            this.renderItemInSlot(x, y, tickDelta, this.mc.player.inventory.offhandSlot[0]);
        }

        if (this.mc.player.inventory.accessorySlots[2] != null && this.mc.player.inventory.accessorySlots[2].itemID == ItemList.quiver.id) {
            int x = width / 2 + 99;
            int y = height - 16 - 3;
            this.renderItemInSlot(x, y, tickDelta, this.mc.player.inventory.accessorySlots[2]);
            Lighting.disable();
            int arrows = 0;
            if (((PlayerContainer)this.mc.player.container).quiverContents.getStackInSlot(0) != null) {
                arrows += ((PlayerContainer)this.mc.player.container).quiverContents.getStackInSlot(0).stackSize;
            }

            if (((PlayerContainer)this.mc.player.container).quiverContents.getStackInSlot(1) != null) {
                arrows += ((PlayerContainer)this.mc.player.container).quiverContents.getStackInSlot(1).stackSize;
            }

            if (((PlayerContainer)this.mc.player.container).quiverContents.getStackInSlot(2) != null) {
                arrows += ((PlayerContainer)this.mc.player.container).quiverContents.getStackInSlot(2).stackSize;
            }

            String arrowCount = "" + arrows;
            this.drawCenteredString(this.mc.fontRenderer, arrowCount, x + 30, y + 5, 16777215);
            Lighting.enable();
        }
    }

    private void renderSubtitle(float tickDelta, int width, int height) {
        ItemStack item = this.mc.player.inventory.mainInventory[this.mc.player.inventory.currentSlot];
        if (this.mc.player.properties.getHunger() && this.mc.player.maxHealth > 20) {
            height -= 3;
        }

        if (item != null && item.textTime > 0.0F) {
            int rarityColor = item.getItem().getRarity().getColor();
            String itemName = ("" + Translator.getInstance().translateNamedKey(item.getItemName(item.getItemDamage()))).trim();
            if (item.itemID == ItemList.writtenBook.id) {
                itemName = ((WrittenBook)item.getItem()).getItemName(item);
            }

            if (item.hasDisplayName()) {
                itemName = item.getDisplayName();
            }

            float textTime = item.textTime - tickDelta;
            int alpha = (int)(textTime * 256.0F / 10.0F);
            if (alpha > 255) {
                alpha = 255;
            }

            if (alpha > 8) {
                int offset;
                if (this.mc.gamemode instanceof CreativeMode) {
                    offset = 32;
                } else if (this.mc.player.health <= 20 && (this.mc.player.getTotalArmorValue(null) <= 0 || !this.mc.player.properties.getHunger())) {
                    offset = 50;
                } else {
                    offset = 60;
                }

                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
                this.drawCenteredString(this.mc.fontRenderer, itemName, width / 2, height - offset, rarityColor + (alpha << 24));
                GL11.glDisable(3042);
            }
        }
    }

    private void renderItemInSlot(int width, int height, float tickDelta, ItemStack stack) {
        if (stack != null) {
            float animFrames = stack.animationsToGo - tickDelta;
            if (animFrames > 0.0F) {
                GL11.glPushMatrix();
                float distort = 1.0F + animFrames / 5.0F;
                GL11.glTranslatef(width + 8, height + 12, 0.0F);
                GL11.glScalef(1.0F / distort, (distort + 1.0F) / 2.0F, 1.0F);
                GL11.glTranslatef(-(width + 8), -(height + 12), 0.0F);
            }

            itemRenderer.renderItemSprite(this.mc.fontRenderer, this.mc.textureLoader, stack, width, height);
            if (animFrames > 0.0F) {
                GL11.glPopMatrix();
            }

            itemRenderer.renderGUIOverlays(this.mc.fontRenderer, this.mc.textureLoader, stack, width, height);
        }
    }

    private void renderPickup(Font font, float deltaTime, int width, int height) {
        int offset = 10;
        int slots = 0;

        for (int slot = 0; slot < this.mc.player.inventory.mainInventory.length; slot++) {
            ItemStack item = this.mc.player.inventory.mainInventory[slot];
            if (item != null && item.popupTime > 0.0F) {
                slots++;
                int rarityColor = item.getItem().getRarity().getColor();
                String count = " x" + item.stackSize;
                String itemName = ("" + Translator.getInstance().translateNamedKey(item.getItemName(item.getItemDamage())) + count).trim();
                if (item.itemID == ItemList.writtenBook.id) {
                    itemName = ((WrittenBook)item.getItem()).getItemName(item);
                }

                if (item.hasDisplayName()) {
                    itemName = item.getDisplayName();
                }

                float textTime = item.popupTime - deltaTime;
                int alpha = (int)(textTime * 256.0F / 10.0F);
                if (alpha > 255) {
                    alpha = 255;
                }

                if (alpha > 8 && slots <= 10) {
                    offset = slots * 15;
                    GL11.glPushMatrix();
                    GL11.glEnable(3042);
                    GL11.glBlendFunc(770, 771);
                    this.drawString(font, itemName, width - font.getStringWidth(itemName) - 2, height - offset, rarityColor + (alpha << 24));
                    GL11.glDisable(3042);
                    GL11.glPopMatrix();
                    GL11.glEnable(32826);
                    GL11.glPushMatrix();
                    GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
                    Lighting.enable();
                    GL11.glPopMatrix();
                    itemRenderer.renderItemSprite(
                        this.mc.fontRenderer, this.mc.textureLoader, item, width - font.getStringWidth(itemName) - 20, height - offset - 5
                    );
                    Lighting.disable();
                    GL11.glDisable(32826);
                }
            }
        }
    }

    private void displayStatusEffects(float deltaTime) {
        int width = this.mc.rescaler.getScaledWidth() - 28;
        int height = 0;
        int texture = this.mc.textureLoader.getTexture("/gui/inventory/inventory.png");
        Collection<EffectData> activeEffects = this.mc.player.getEffects();
        if (this.achievement.time > 0L) {
            height = 32;
        }

        if (!activeEffects.isEmpty()) {
            int heightOffset = 25;
            if (activeEffects.size() > 5) {
                heightOffset = 132 / (activeEffects.size() - 1);
            }

            for (EffectData effect : activeEffects) {
                Effect potion = EffectList.effects[effect.getPotionID()];
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.mc.textureLoader.bindTexture(texture);
                int x = 140;
                if (effect.isSpecial) {
                    x = 167;
                }

                this.drawGraphic(width, height + 3, x, 166, 27, 32);
                if (potion.hasIcon()) {
                    float fade = 1.0F;
                    if (effect.getPotionDuration() <= 100) {
                        int time = 10 - effect.getPotionDuration() / 20;
                        fade = MathHelper.clamp_float(effect.getPotionDuration() / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F)
                            + MathHelper.cos(effect.getPotionDuration() * (float) Math.PI / 5.0F) * MathHelper.clamp_float(time / 10.0F * 0.25F, 0.0F, 0.25F);
                    }

                    GL11.glColor4f(1.0F, 1.0F, 1.0F, fade);
                    int icon = potion.getIconIndex();
                    this.drawGraphic(width + 6, height + 7, 0 + icon % 10 * 18, 198 + icon / 10 * 18, 18, 18);
                }

                height += heightOffset;
            }
        }
    }

    private void renderBossHealth() {
        List<Entity> entities = this.mc
            .world
            .getEntitiesOfType(
                Mob.class,
                BoundingBox.getBoundingBox(
                        this.mc.player.posX,
                        this.mc.player.posY,
                        this.mc.player.posZ,
                        this.mc.player.posX + 1.0,
                        this.mc.player.posY + 1.0,
                        this.mc.player.posZ + 1.0
                    )
                    .expand(64.0, 32.0, 64.0)
            );
        int yOff = 0;
        if (!entities.isEmpty()) {
            for (int entityCount = 0; entityCount < entities.size(); entityCount++) {
                if (((Mob)entities.get(entityCount)).boss) {
                    Mob boss = (Mob)entities.get(entityCount);
                    Font font = this.mc.fontRenderer;
                    ScreenRescaler resolution = new ScreenRescaler(this.mc.displayWidth, this.mc.displayHeight);
                    int width = resolution.getScaledWidth();
                    short textureX = 184;
                    int x = width / 2 - textureX / 2;
                    int damageTaken = (int)((float)boss.health / boss.maxHealth * (textureX + 1));
                    byte y = 12;
                    GL11.glBindTexture(3553, this.mc.textureLoader.getTexture("/gui/icons.png"));
                    this.drawGraphic(x, y + yOff, 20, 32, textureX, 5);
                    this.drawGraphic(x, y + yOff, 20, 32, textureX, 5);
                    if (damageTaken > 0) {
                        if (damageTaken > 200) {
                            damageTaken = 200;
                        }

                        this.drawGraphic(x, y + yOff, 20, 37, damageTaken, 5);
                    }

                    String name = !boss.customName.equals("") ? boss.customName : boss.getEntityString();
                    this.drawString(font, name, width / 2 - font.getStringWidth(name) / 2, y - 10 + yOff, 11141120);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    yOff += 18;
                }
            }
        }
    }

    private void renderPlayerList(int width, int height) {
        if (this.mc.player instanceof NetworkPlayer && this.mc.currentScreen == null && this.mc.options.playerListKey.pressed) {
            ClientHandler netclienthandler = ((NetworkPlayer)this.mc.player).connection;
            List<ClientHandler.PlayerInfo> playerList = netclienthandler.playerNames;
            int maxPlayers = netclienthandler.maxPlayers;
            int players = maxPlayers;

            int playerCount;
            for (playerCount = 1; players > 20; players = (maxPlayers + playerCount - 1) / playerCount) {
                playerCount++;
            }

            int boxWidth = 300 / playerCount;
            if (boxWidth > 150) {
                boxWidth = 150;
            }

            int x = (width - playerCount * boxWidth) / 2;
            byte y = 10;
            drawRectangle(x - 1, y - 1, x + boxWidth * playerCount, y + 9 * players, Integer.MIN_VALUE);

            for (int player = 0; player < maxPlayers; player++) {
                int newX = x + player % playerCount * boxWidth;
                int newY = y + player / playerCount * 9;
                drawRectangle(newX, newY, newX + boxWidth - 1, newY + 8, 553648127);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glEnable(3008);
                if (player < playerList.size()) {
                    ClientHandler.PlayerInfo playerInfo = playerList.get(player);
                    this.mc.fontRenderer.drawStringWithShadow(playerInfo.name, newX, newY, 16777215);
                    this.mc.textureLoader.bindTexture(this.mc.textureLoader.getTexture("/gui/icons.png"));
                    byte textureOffset = 0;
                    if (playerInfo.ping < 0) {
                        textureOffset = 5;
                    } else if (playerInfo.ping < 150) {
                        textureOffset = 0;
                    } else if (playerInfo.ping < 300) {
                        textureOffset = 1;
                    } else if (playerInfo.ping < 600) {
                        textureOffset = 2;
                    } else if (playerInfo.ping < 1000) {
                        textureOffset = 3;
                    } else {
                        textureOffset = 4;
                    }

                    this.zLevel += 100.0F;
                    this.drawGraphic(newX + boxWidth - 12, newY, 0, 31 + textureOffset * 8, 10, 8);
                    this.zLevel -= 100.0F;
                }
            }
        }
    }

    private void renderVignette(float lightValue, int width, int height) {
        lightValue = 1.0F - lightValue;
        if (lightValue < 0.0F) {
            lightValue = 0.0F;
        }

        if (lightValue > 1.0F) {
            lightValue = 1.0F;
        }

        this.vignetteBrightness = (float)(this.vignetteBrightness + (lightValue - this.vignetteBrightness) * 0.01);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(0, 769);
        GL11.glColor4f(this.vignetteBrightness, this.vignetteBrightness, this.vignetteBrightness, 1.0F);
        GL11.glBindTexture(3553, this.mc.textureLoader.getTexture("/misc/vignette.png"));
        Tessellator tessellator = Tessellator.instance;
        tessellator.begin();
        tessellator.addTexturedVertex(0.0, height, -90.0, 0.0F, 1.0F);
        tessellator.addTexturedVertex(width, height, -90.0, 1.0F, 1.0F);
        tessellator.addTexturedVertex(width, 0.0, -90.0, 1.0F, 0.0F);
        tessellator.addTexturedVertex(0.0, 0.0, -90.0, 0.0F, 0.0F);
        tessellator.draw();
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glBlendFunc(770, 771);
    }

    private void renderFrost(int width, int height) {
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, (this.mc.player.freeze - 2) / 98.0F);
        GL11.glBindTexture(3553, this.mc.textureLoader.getTexture("/misc/frozen.png"));
        Tessellator tessellator = Tessellator.instance;
        tessellator.begin();
        tessellator.addTexturedVertex(0.0, height, -90.0, 0.0F, 1.0F);
        tessellator.addTexturedVertex(width, height, -90.0, 1.0F, 1.0F);
        tessellator.addTexturedVertex(width, 0.0, -90.0, 1.0F, 0.0F);
        tessellator.addTexturedVertex(0.0, 0.0, -90.0, 0.0F, 0.0F);
        tessellator.draw();
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glBlendFunc(770, 771);
    }

    private void renderTint(float brightness, int width, int height, float red, float green, float blue) {
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        brightness = MathHelper.clamp(brightness, 0.0F, 1.0F);
        this.tintBrightness = this.tintBrightness + (float)((brightness - this.tintBrightness) * 0.01);
        GL11.glColor4f(red, green, blue, this.tintBrightness);
        GL11.glBindTexture(3553, this.mc.textureLoader.getTexture("/misc/tint.png"));
        Tessellator tessellator = Tessellator.instance;
        tessellator.begin();
        tessellator.addTexturedVertex(0.0, height, -90.0, 0.0F, 1.0F);
        tessellator.addTexturedVertex(width, height, -90.0, 1.0F, 1.0F);
        tessellator.addTexturedVertex(width, 0.0, -90.0, 1.0F, 0.0F);
        tessellator.addTexturedVertex(0.0, 0.0, -90.0, 0.0F, 0.0F);
        tessellator.draw();
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(3042);
    }

    private void renderSleepOverlay(int width, int height) {
        if (this.mc.player.getSleepTimer() > 0) {
            GL11.glDisable(2929);
            GL11.glDisable(3008);
            int snoozeTime = this.mc.player.getSleepTimer();
            float sleepTimer = snoozeTime / 100.0F;
            if (sleepTimer > 1.0F) {
                sleepTimer = 1.0F - (snoozeTime - 100) / 10.0F;
            }

            int color = (int)(220.0F * sleepTimer) << 24 | 1052704;
            drawRectangle(0, 0, width, height, color);
            GL11.glEnable(3008);
            GL11.glEnable(2929);
        }
    }

    public void updateDebugProfilerName(int sectionID) {
        List<?> data = Profiler.getProfilingData(this.profilerName);
        if (data != null && data.size() != 0) {
            Profiler.ProfilerResult profiler = (Profiler.ProfilerResult)data.remove(0);
            if (sectionID == 0) {
                if (profiler.name.length() > 0) {
                    int lastIndex = this.profilerName.lastIndexOf(".");
                    if (lastIndex >= 0) {
                        this.profilerName = this.profilerName.substring(0, lastIndex);
                    }
                }
            } else {
                sectionID--;
                if (sectionID < data.size() && !((Profiler.ProfilerResult)data.get(sectionID)).name.equals("unspecified")) {
                    if (this.profilerName.length() > 0) {
                        this.profilerName = this.profilerName + ".";
                    }

                    this.profilerName = this.profilerName + ((Profiler.ProfilerResult)data.get(sectionID)).name;
                }
            }
        }
    }

    public void renderProfiler(long deltaTime) {
        Tessellator tessellatorInstance = Tessellator.instance;
        this.renderLagometer(tessellatorInstance, deltaTime);
        this.renderPieChart(tessellatorInstance);
    }

    private void renderPieChart(Tessellator tessellatorInstance) {
        List<?> data = Profiler.getProfilingData(this.profilerName);
        Profiler.ProfilerResult profilerResult = (Profiler.ProfilerResult)data.remove(0);
        short chartHeight = 160;
        int x = this.mc.displayWidth - chartHeight - 10;
        int y = this.mc.displayHeight - chartHeight * 2;
        GL11.glEnable(3042);
        tessellatorInstance.begin();
        tessellatorInstance.setColorAlpha(0, 200);
        tessellatorInstance.addVertex(x - chartHeight * 1.1F, y - chartHeight * 0.6F - 16.0F, 0.0);
        tessellatorInstance.addVertex(x - chartHeight * 1.1F, y + chartHeight * 2, 0.0);
        tessellatorInstance.addVertex(x + chartHeight * 1.1F, y + chartHeight * 2, 0.0);
        tessellatorInstance.addVertex(x + chartHeight * 1.1F, y - chartHeight * 0.6F - 16.0F, 0.0);
        tessellatorInstance.draw();
        GL11.glDisable(3042);
        double sectionPercentage = 0.0;

        for (int dataBlob = 0; dataBlob < data.size(); dataBlob++) {
            Profiler.ProfilerResult profilerResult29 = (Profiler.ProfilerResult)data.get(dataBlob);
            int section = MathHelper.floor_double(profilerResult29.sectionPercentage / 4.0) + 1;
            tessellatorInstance.begin(6);
            tessellatorInstance.setColor(profilerResult29.getDisplayColor());
            tessellatorInstance.addVertex(x, y, 0.0);

            for (int currSection = section; currSection >= 0; currSection--) {
                float width = (float)((sectionPercentage + profilerResult29.sectionPercentage * currSection / section) * (float) Math.PI * 2.0 / 100.0);
                float height = MathHelper.sin(width) * chartHeight;
                float depth = MathHelper.cos(width) * chartHeight * 0.5F;
                tessellatorInstance.addVertex(x + height, y - depth, 0.0);
            }

            tessellatorInstance.draw();
            tessellatorInstance.begin(5);
            tessellatorInstance.setColor((profilerResult29.getDisplayColor() & 16711422) >> 1);

            for (int var21 = section; var21 >= 0; var21--) {
                float width = (float)((sectionPercentage + profilerResult29.sectionPercentage * var21 / section) * (float) Math.PI * 2.0 / 100.0);
                float height = MathHelper.sin(width) * chartHeight;
                float depth = MathHelper.cos(width) * chartHeight * 0.5F;
                tessellatorInstance.addVertex(x + height, y - depth, 0.0);
                tessellatorInstance.addVertex(x + height, y - depth + 10.0F, 0.0);
            }

            tessellatorInstance.draw();
            sectionPercentage += profilerResult29.sectionPercentage;
        }

        GL11.glEnable(3553);
        String valueString = "";
        if (!profilerResult.name.equals("unspecified")) {
            valueString = valueString + "[0] ";
        }

        if (profilerResult.name.length() == 0) {
            valueString = valueString + "ROOT ";
        } else {
            valueString = valueString + profilerResult.name + " ";
        }

        int section = 16777215;
        this.mc.fontRenderer.drawStringWithShadow(valueString, x - chartHeight, y - chartHeight / 2 - 16, section);
        this.mc
            .fontRenderer
            .drawStringWithShadow(
                valueString = String.format("%.2f", profilerResult.globalPercentage) + "%",
                x + chartHeight - this.mc.fontRenderer.getStringWidth(valueString),
                y - chartHeight / 2 - 16,
                section
            );

        for (int profilerSection = 0; profilerSection < data.size(); profilerSection++) {
            Profiler.ProfilerResult profilerInstance = (Profiler.ProfilerResult)data.get(profilerSection);
            String sectionName = "";
            if (!profilerInstance.name.equals("unspecified")) {
                sectionName = sectionName + "[" + (profilerSection + 1) + "] ";
            } else {
                sectionName = sectionName + "[?] ";
            }

            sectionName = sectionName + profilerInstance.name;
            this.mc
                .fontRenderer
                .drawStringWithShadow(sectionName, x - chartHeight, y + chartHeight / 2 + profilerSection * 8 + 20, profilerInstance.getDisplayColor());
            this.mc
                .fontRenderer
                .drawStringWithShadow(
                    sectionName = String.format("%.2f", profilerInstance.sectionPercentage) + "%",
                    x + chartHeight - 50 - this.mc.fontRenderer.getStringWidth(sectionName),
                    y + chartHeight / 2 + profilerSection * 8 + 20,
                    profilerInstance.getDisplayColor()
                );
            this.mc
                .fontRenderer
                .drawStringWithShadow(
                    sectionName = String.format("%.2f", profilerInstance.globalPercentage) + "%",
                    x + chartHeight - this.mc.fontRenderer.getStringWidth(sectionName),
                    y + chartHeight / 2 + profilerSection * 8 + 20,
                    profilerInstance.getDisplayColor()
                );
        }
    }

    private void renderLagometer(Tessellator tessellatorInstance, long deltaTime) {
        long timeOffset = 16666666L;
        if (this.prevFrameTime == -1L) {
            this.prevFrameTime = System.nanoTime();
        }

        long systemTime = System.nanoTime();
        tickTimes[numRecordedFrameTimes & frameTimes.length - 1] = deltaTime;
        frameTimes[numRecordedFrameTimes++ & frameTimes.length - 1] = systemTime - this.prevFrameTime;
        this.prevFrameTime = systemTime;
        GL11.glClear(256);
        GL11.glMatrixMode(5889);
        GL11.glEnable(2903);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0, this.mc.displayWidth, this.mc.displayHeight, 0.0, 1000.0, 3000.0);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
        GL11.glLineWidth(1.0F);
        GL11.glDisable(3553);
        tessellatorInstance.begin(7);
        int heightOffset = (int)(timeOffset / 200000L);
        tessellatorInstance.setColor(536870912);
        tessellatorInstance.addVertex(0.0, this.mc.displayHeight - heightOffset, 0.0);
        tessellatorInstance.addVertex(0.0, this.mc.displayHeight, 0.0);
        tessellatorInstance.addVertex(frameTimes.length, this.mc.displayHeight, 0.0);
        tessellatorInstance.addVertex(frameTimes.length, this.mc.displayHeight - heightOffset, 0.0);
        tessellatorInstance.setColor(538968064);
        tessellatorInstance.addVertex(0.0, this.mc.displayHeight - heightOffset * 2, 0.0);
        tessellatorInstance.addVertex(0.0, this.mc.displayHeight - heightOffset, 0.0);
        tessellatorInstance.addVertex(frameTimes.length, this.mc.displayHeight - heightOffset, 0.0);
        tessellatorInstance.addVertex(frameTimes.length, this.mc.displayHeight - heightOffset * 2, 0.0);
        tessellatorInstance.draw();
        long frameTime = 0L;

        for (int totalFrames = 0; totalFrames < frameTimes.length; totalFrames++) {
            frameTime += frameTimes[totalFrames];
        }

        int var20 = (int)(frameTime / 200000L / frameTimes.length);
        tessellatorInstance.begin(7);
        tessellatorInstance.setColor(541065216);
        tessellatorInstance.addVertex(0.0, this.mc.displayHeight - var20, 0.0);
        tessellatorInstance.addVertex(0.0, this.mc.displayHeight, 0.0);
        tessellatorInstance.addVertex(frameTimes.length, this.mc.displayHeight, 0.0);
        tessellatorInstance.addVertex(frameTimes.length, this.mc.displayHeight - var20, 0.0);
        tessellatorInstance.draw();
        tessellatorInstance.begin(1);

        for (int frame = 0; frame < frameTimes.length; frame++) {
            int red = (frame - numRecordedFrameTimes & frameTimes.length - 1) * 255 / frameTimes.length;
            int green = red * red / 255;
            green = green * green / 255;
            int blue = green * green / 255;
            blue = blue * blue / 255;
            if (frameTimes[frame] > timeOffset) {
                tessellatorInstance.setColor(-16777216 + green * 65536);
            } else {
                tessellatorInstance.setColor(-16777216 + green * 256);
            }

            long totalFrameTime = frameTimes[frame] / 200000L;
            long totalTickTime = tickTimes[frame] / 200000L;
            tessellatorInstance.addVertex(frame + 0.5F, (float)(this.mc.displayHeight - totalFrameTime) + 0.5F, 0.0);
            tessellatorInstance.addVertex(frame + 0.5F, this.mc.displayHeight + 0.5F, 0.0);
            tessellatorInstance.setColor(-16777216 + green * 65536 + green * 256 + green * 1);
            tessellatorInstance.addVertex(frame + 0.5F, (float)(this.mc.displayHeight - totalFrameTime) + 0.5F, 0.0);
            tessellatorInstance.addVertex(frame + 0.5F, (float)(this.mc.displayHeight - (totalFrameTime - totalTickTime)) + 0.5F, 0.0);
        }

        tessellatorInstance.draw();
    }
}
