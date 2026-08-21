package net.minecraft.client.render.game;

import java.util.List;
import net.minecraft.Profiler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.game.PhotoMode;
import net.minecraft.client.options.OptionValues;
import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.BlocksRenderer;
import net.minecraft.client.render.camera.Camera;
import net.minecraft.client.render.entity.ParticleRenderer;
import net.minecraft.client.render.environment.FogRenderer;
import net.minecraft.client.render.environment.WeatherRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.world.WorldRenderer;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.decoration.Bed;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.physics.TargetingSystem;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.world.WorldType;
import net.minecraft.statistics.achievement.AchievementList;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class GameRenderer {
    private Minecraft mc;
    public ItemRenderer itemRenderer;
    public Lightmap lightmap;
    public FogRenderer fog;
    public WeatherRenderer weather;
    public OverlayRenderer overlays;
    public BlocksRenderer blocks;
    private Entity pointedEntity = null;
    public Mob cameraMob;
    private GameRenderer.MouseFilter mouseFilterXAxis = new GameRenderer.MouseFilter();
    private GameRenderer.MouseFilter mouseFilterYAxis = new GameRenderer.MouseFilter();
    public float farPlaneDistance = 0.0F;
    public int rendererUpdateCount;
    private boolean isDisplayActive = false;
    private float camFOV = 0.0F;
    private float prevCamFOV = 0.0F;
    private float fovHand;
    private float fovHandPrev;

    public GameRenderer(Minecraft mc) {
        this.mc = mc;
        this.lightmap = new Lightmap(mc);
        this.fog = new FogRenderer(mc);
        this.weather = new WeatherRenderer(mc);
        this.itemRenderer = new ItemRenderer(mc);
        this.overlays = new OverlayRenderer(mc);
        this.blocks = new BlocksRenderer();
    }

    public void update() {
        this.updateFOV();
        this.lightmap.flicker();
        this.fog.prevFogColor = this.fog.fogColor;
        this.prevCamFOV = this.camFOV;
        float playerLightVal = this.mc
            .world
            .getBrightness(
                MathHelper.floor_double(this.cameraMob.posX), MathHelper.floor_double(this.cameraMob.posY), MathHelper.floor_double(this.cameraMob.posZ)
            );
        float renderDist = (32 - OptionValues.renderDistance) / 32.0F;
        float col = playerLightVal * (1.0F - renderDist) + renderDist;
        this.fog.fogColor = this.fog.fogColor + (col - this.fog.fogColor) * 0.1F;
        this.rendererUpdateCount++;
        this.itemRenderer.updateEquippedItem();
        if (this.mc.world.currDim.weatherEnabled()) {
            this.weather.addRainParticles();
        }
    }

    private Vector getCameraPosition(float deltaTime) {
        Mob player = this.cameraMob;
        double x = player.prevX + (player.posX - player.prevX) * deltaTime;
        double y = player.prevY + (player.posY - player.prevY) * deltaTime;
        double z = player.prevZ + (player.posZ - player.prevZ) * deltaTime;
        return Vector.createVector(x, y, z);
    }

    public void setSelectedObject(float deltaTime) {
        if (this.cameraMob != null && this.mc.world != null) {
            double reachDist = this.mc.gamemode.blockReach();
            this.mc.selectedObject = this.cameraMob.getSelectedBlock(reachDist, deltaTime);
            Vector playerPos = this.cameraMob.getPosition(deltaTime);
            reachDist = this.mc.gamemode.entityReach();
            if (this.mc.selectedObject != null) {
                reachDist = this.mc.selectedObject.hitVec.distanceTo(playerPos);
            }

            Vector playerFacing = this.cameraMob.getLookVector(deltaTime);
            Vector finalPos = playerPos.addVector(playerFacing.xCoord * reachDist, playerFacing.yCoord * reachDist, playerFacing.zCoord * reachDist);
            this.pointedEntity = null;
            float boundingBoxOffset = 1.0F;
            List<Entity> nearbyEntities = this.mc
                .world
                .getEntities(
                    this.cameraMob,
                    this.cameraMob
                        .boundingBox
                        .addCoord(playerFacing.xCoord * reachDist, playerFacing.yCoord * reachDist, playerFacing.zCoord * reachDist)
                        .expand(boundingBoxOffset, boundingBoxOffset, boundingBoxOffset)
                );
            double finalDist = reachDist;

            for (int index = 0; index < nearbyEntities.size(); index++) {
                Entity entity = nearbyEntities.get(index);
                if (entity.isSelectable()) {
                    float colSize = entity.getbbSize();
                    BoundingBox boundingBox = entity.boundingBox.expand(colSize, colSize, colSize);
                    TargetingSystem targetedObject = boundingBox.calculateIntercept(playerPos, finalPos);
                    if (boundingBox.isVecInside(playerPos)) {
                        if (0.0 < finalDist || finalDist == 0.0) {
                            this.pointedEntity = entity;
                            finalDist = 0.0;
                        }
                    } else if (targetedObject != null) {
                        double targetDist = playerPos.distanceTo(targetedObject.hitVec);
                        if (targetDist < finalDist || finalDist == 0.0) {
                            this.pointedEntity = entity;
                            finalDist = targetDist;
                        }
                    }
                }
            }

            if (this.pointedEntity != null && (finalDist < reachDist || this.mc.selectedObject == null)) {
                this.mc.selectedObject = new TargetingSystem(this.pointedEntity);
            }
        }
    }

    private void updateFOV() {
        float multiplier = this.mc.player.getFOVMultiplier();
        this.fovHandPrev = this.fovHand;
        this.fovHand = this.fovHand + (multiplier - this.fovHand) * 0.5F;
    }

    private float getFOV(float timeDelta, boolean override) {
        Mob player = this.cameraMob;
        float fov = 70.0F;
        if (override) {
            fov = OptionValues.fov;
            fov *= this.fovHandPrev + (this.fovHand - this.fovHandPrev) * timeDelta;
        }

        if (player.isInMaterial(MaterialList.water)) {
            fov = fov * 60.0F / 70.0F;
        }

        if (player.health <= 0) {
            float shrinkTime = player.deathTimer + timeDelta;
            fov /= (1.0F - 500.0F / (shrinkTime + 500.0F)) * 2.0F + 1.0F;
        }

        if (player instanceof Player) {
            Player player2 = (Player)player;
            if ((
                    player2.properties.gamemode == 1
                        || player2.properties.gamemode == 3
                        || player2.inventory.accessorySlots[0] != null && player2.inventory.accessorySlots[0].getItem() == ItemList.spyGlass
                )
                && this.mc.options.zoomKey.pressed
                && this.mc.currentScreen == null) {
                fov = 25.0F;
                player2.triggerAchievement(AchievementList.spyglass);
            }
        }

        return fov + this.prevCamFOV + (this.camFOV - this.prevCamFOV) * timeDelta;
    }

    private void applyDamageTilt(float tickDelta) {
        Mob player = this.cameraMob;
        float hurtDuration = player.hurtTime - tickDelta;
        if (player.health <= 0) {
            float deathDuration = player.deathTimer + tickDelta;
            GL11.glRotatef(40.0F - 8000.0F / (deathDuration + 200.0F), 0.0F, 0.0F, 1.0F);
        }

        if (hurtDuration >= 0.0F) {
            hurtDuration /= player.hurtDuration;
            hurtDuration = MathHelper.sin(hurtDuration * hurtDuration * hurtDuration * hurtDuration * (float) Math.PI);
            float deathDuration = player.attackAngle;
            GL11.glRotatef(-deathDuration, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-hurtDuration * 14.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(deathDuration, 0.0F, 1.0F, 0.0F);
        }
    }

    private void applyViewBob(float tickDelta) {
        if (this.cameraMob instanceof Player) {
            Player player = (Player)this.cameraMob;
            float walkDistance = player.distance - player.prevDistance;
            float prevWalkDist = player.distance + walkDistance * tickDelta;
            float camYaw = player.prevCamYaw + (player.camYaw - player.prevCamYaw) * tickDelta;
            float camPitch = player.prevCamPitch + (player.camPitch - player.prevCamPitch) * tickDelta;
            GL11.glTranslatef(
                MathHelper.sin(prevWalkDist * (float) Math.PI) * camYaw * 0.5F, -Math.abs(MathHelper.cos(prevWalkDist * (float) Math.PI) * camYaw), 0.0F
            );
            GL11.glRotatef(MathHelper.sin(prevWalkDist * (float) Math.PI) * camYaw * 3.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(Math.abs(MathHelper.cos(prevWalkDist * (float) Math.PI + 0.2F) * camYaw) * 5.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(camPitch, 1.0F, 0.0F, 0.0F);
        }
    }

    private void renderWorldAxis(float partialTicks) {
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(1.0F);
        GL11.glDisable(3553);
        GL11.glDepthMask(false);
        GL11.glPushMatrix();
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        this.updateCamera(partialTicks);
        this.drawColoredLines(new BoundingBox(0.0, 0.0, 0.0, 0.005, 1.0E-4, 1.0E-4), 255, 0, 0, 255);
        this.drawColoredLines(new BoundingBox(0.0, 0.0, 0.0, 1.0E-4, 1.0E-4, 0.005), 0, 0, 255, 255);
        this.drawColoredLines(new BoundingBox(0.0, 0.0, 0.0, 1.0E-4, 0.0033, 1.0E-4), 0, 255, 0, 255);
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
    }

    public void updateCamera(float tickDelta) {
        Mob player = this.cameraMob;
        double x = player.prevX + (player.posX - player.prevX) * tickDelta;
        double y = player.prevY + (player.posY - player.prevY) * tickDelta;
        double z = player.prevZ + (player.posZ - player.prevZ) * tickDelta;
        if (!(player instanceof Player)) {
            GL11.glTranslatef(0.0F, -player.getEyeHeight(), 0.0F);
        }

        if (player instanceof Player && ((Player)player).isPlayerSleeping() && OptionValues.cameraMode != 3) {
            float yOffset = player.yOffset - 1.62F;
            yOffset = (float)(yOffset + 1.0);
            GL11.glTranslatef(0.0F, yOffset + 0.3F, 0.0F);
            Block blockOn = this.mc
                .world
                .getBlock(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY), MathHelper.floor_double(player.posZ));
            if (blockOn instanceof Bed) {
                int meta = this.mc
                    .world
                    .getBlockMetadata(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY), MathHelper.floor_double(player.posZ));
                int facing = meta & 3;
                GL11.glRotatef(facing * 90, 0.0F, 1.0F, 0.0F);
            }

            GL11.glRotatef(player.prevYaw + (player.yaw - player.prevYaw) * tickDelta + 180.0F, 0.0F, -1.0F, 0.0F);
            GL11.glRotatef(player.prevPitch + (player.pitch - player.prevPitch) * tickDelta, -1.0F, 0.0F, 0.0F);
        } else if (OptionValues.cameraMode >= 1) {
            double distFromCam = 4.0;
            float yaw = player.yaw;
            float pitch = player.pitch;
            if (OptionValues.cameraMode == 2) {
                pitch += 180.0F;
                distFromCam = 5.0;
            }

            if (OptionValues.cameraMode == 3) {
                pitch = 90.0F;
                distFromCam = 10.5;
            }

            double upperYaw = -MathHelper.sin(yaw / 180.0F * (float) Math.PI) * MathHelper.cos(pitch / 180.0F * (float) Math.PI) * distFromCam;
            double lowerYaw = MathHelper.cos(yaw / 180.0F * (float) Math.PI) * MathHelper.cos(pitch / 180.0F * (float) Math.PI) * distFromCam;
            double camRotation = -MathHelper.sin(pitch / 180.0F * (float) Math.PI) * distFromCam;

            for (int face = 0; face < 8; face++) {
                float rotX = (face & 1) * 2 - 1;
                float rotY = (face >> 1 & 1) * 2 - 1;
                float rotZ = (face >> 2 & 1) * 2 - 1;
                rotX *= 0.1F;
                rotY *= 0.1F;
                rotZ *= 0.1F;
                TargetingSystem target = this.mc
                    .world
                    .rayTraceBlocks(
                        Vector.createVector(x + rotX, y + rotY + player.getEyeHeight(), z + rotZ),
                        Vector.createVector(x - upperYaw + rotX + rotZ, y - camRotation + rotY, z - lowerYaw + rotZ)
                    );
                if (target != null) {
                    double targetDist = target.hitVec.distanceTo(Vector.createVector(x, y, z));
                    if (targetDist < distFromCam) {
                        distFromCam = targetDist;
                    }
                }
            }

            GL11.glTranslatef(0.0F, 0.0F, (float)(-distFromCam));
            if (!(this.mc.currentScreen instanceof PhotoMode)) {
                if (OptionValues.cameraMode == 2) {
                    GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                }

                if (OptionValues.cameraMode == 3) {
                    GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                }
            }
        } else if (!OptionValues.showArm && player instanceof Player) {
            float yOff = -0.2F;
            float zOffset = player.crawling ? 0.1F : -0.0F;
            Vector eye = Vector.createVector(x, y + player.getEyeHeight() - yOff, z);
            Vector target = eye.addVector(0.0, yOff, zOffset);
            TargetingSystem hit = this.mc.world.rayTraceBlocks(eye, target);
            float newYOffset = yOff;
            if (hit != null && !((Player)player).properties.isSpectator()) {
                double allowedDist = hit.hitVec.distanceTo(eye) - 0.05;
                Vector dir = target.subtract(eye).normalize();
                newYOffset = (float)(dir.yCoord * allowedDist);
            }

            if (player.crawling) {
                newYOffset = 0.0F;
            }

            GL11.glTranslatef(0.0F, newYOffset, zOffset);
        } else {
            GL11.glTranslatef(0.0F, 0.0F, -0.1F);
        }

        if (this.mc.currentScreen instanceof PhotoMode) {
            GL11.glRotatef(((PhotoMode)this.mc.currentScreen).getTilt(tickDelta), 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F + 90.0F * ((PhotoMode)this.mc.currentScreen).getRotation(tickDelta), 0.0F, 1.0F, 0.0F);
        } else {
            if (OptionValues.cameraMode != 3) {
                GL11.glRotatef(player.prevPitch + (player.pitch - player.prevPitch) * tickDelta, 1.0F, 0.0F, 0.0F);
            }

            GL11.glRotatef(player.prevYaw + (player.yaw - player.prevYaw) * tickDelta + 180.0F, 0.0F, 1.0F, 0.0F);
        }
    }

    private void applyCameraEffects(float tickDelta) {
        this.farPlaneDistance = 16 * OptionValues.renderDistance;
        if (this.farPlaneDistance < 8.0F) {
            this.farPlaneDistance = 8.0F;
        }

        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        if (this.mc.currentScreen instanceof PhotoMode) {
            PhotoMode photoMode = (PhotoMode)this.mc.currentScreen;
            double div = Math.pow(2.0, photoMode.getZoom(tickDelta));
            double width = this.mc.displayWidth / div;
            double height = this.mc.displayHeight / div;
            GL11.glOrtho(-width, width, -height, height, this.farPlaneDistance * -2.0F, this.farPlaneDistance * 2.0F);
            GL11.glTranslatef(((PhotoMode)this.mc.currentScreen).getPanX(tickDelta), -((PhotoMode)this.mc.currentScreen).getPanY(tickDelta), 0.0F);
        } else {
            GLU.gluPerspective(this.getFOV(tickDelta, true), (float)this.mc.displayWidth / this.mc.displayHeight, 0.05F, this.farPlaneDistance * 4.0F);
        }

        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        if (OptionValues.viewBobbing && !(this.mc.currentScreen instanceof PhotoMode)) {
            this.applyDamageTilt(tickDelta);
            this.applyViewBob(tickDelta);
        }

        float confusionOffset = this.mc.player.prevNauseaTime + (this.mc.player.nauseaTime - this.mc.player.prevNauseaTime) * tickDelta;
        if (confusionOffset > 0.0F) {
            byte speed = 7;
            if (this.mc.player.isInMaterial(MaterialList.water)) {
                speed = 1;
            }

            float scaleDistort = 5.0F / (confusionOffset * confusionOffset + 5.0F) - confusionOffset * 0.04F;
            scaleDistort *= scaleDistort;
            GL11.glRotatef((this.rendererUpdateCount + tickDelta) * speed, 0.0F, 1.0F, 1.0F);
            GL11.glScalef(1.0F / scaleDistort, 1.0F, 1.0F);
            GL11.glRotatef(-(this.rendererUpdateCount + tickDelta) * speed, 0.0F, 1.0F, 1.0F);
        }

        this.updateCamera(tickDelta);
    }

    private void renderHand(float deltaTick) {
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GLU.gluPerspective(this.getFOV(deltaTick, false), (float)this.mc.displayWidth / this.mc.displayHeight, 0.05F, this.farPlaneDistance * 2.0F);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glPushMatrix();
        if (OptionValues.viewBobbing) {
            this.applyDamageTilt(deltaTick);
            this.applyViewBob(deltaTick);
        }

        if (OptionValues.cameraMode == 0 && OptionValues.showHUD && OptionValues.showArm && !this.mc.player.isPlayerSleeping()) {
            this.lightmap.enable();
            this.itemRenderer.renderHeldItem(this.itemRenderer.mainHand, deltaTick);
            this.itemRenderer.renderHeldItem(this.itemRenderer.offHand, deltaTick);
            if (this.itemRenderer.mainHand == null) {
                this.itemRenderer.renderArm(this.mc.player, deltaTick);
            }

            this.lightmap.disable();
        }

        GL11.glPopMatrix();
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        this.overlays.renderOverlays(deltaTick);
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
    }

    public void render(float deltaTick) {
        Profiler.startSection("lightTex");
        this.lightmap.update(deltaTick);
        Profiler.endSection();
        if (this.isDisplayActive && !Display.isActive()) {
            this.mc.displayPauseScreen();
        }

        this.isDisplayActive = Display.isActive();
        Profiler.startSection("mouse");
        if (this.mc.inFocus) {
            this.mc.mouseHelper.moveMouse();
            float sensitivity = OptionValues.sensitivity * 0.6F + 0.2F;
            float smoothing = sensitivity * sensitivity * sensitivity * 8.0F;
            float deltaX = this.mc.mouseHelper.deltaX * smoothing;
            float deltaY = this.mc.mouseHelper.deltaY * smoothing;
            byte axis = 1;
            if (OptionValues.invertMouse) {
                axis = -1;
            }

            if (OptionValues.smoothCamera) {
                deltaX = this.mouseFilterXAxis.smoothenMouse(deltaX, 0.005F * smoothing);
                deltaY = this.mouseFilterYAxis.smoothenMouse(deltaY, 0.005F * smoothing);
            }

            this.mc.player.updateRotation(deltaX, deltaY * axis);
        }

        Profiler.endSection();
        if (this.cameraMob == null || this.cameraMob.isDead) {
            this.cameraMob = this.mc.player;
        }

        int width = this.mc.rescaler.getScaledWidth();
        int height = this.mc.rescaler.getScaledHeight();
        int mouseX = Mouse.getX() * width / this.mc.displayWidth;
        int mouseY = height - Mouse.getY() * height / this.mc.displayHeight - 1;
        if (this.mc.world != null) {
            Profiler.startSection("level");
            this.renderWorld(deltaTick);
            Profiler.endStartSection("gui");
            this.mc.HUD.render(deltaTick);
            Profiler.endSection();
        } else {
            GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
            GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            GL11.glClear(16640);
            GL11.glMatrixMode(5889);
            GL11.glLoadIdentity();
            GL11.glMatrixMode(5888);
            GL11.glLoadIdentity();
            this.setupOverlayRendering();
        }

        if (this.mc.currentScreen != null) {
            GL11.glClear(256);
            this.mc.currentScreen.drawScreen(mouseX, mouseY, deltaTick);
        }

        this.mc.HUD.achievement.updateToast();
    }

    public void renderWorld(float tickDelta) {
        Profiler.startSection("lightTex");
        this.lightmap.update(tickDelta);
        GL11.glEnable(2884);
        GL11.glEnable(2929);
        Profiler.endStartSection("pick");
        this.setSelectedObject(tickDelta);
        Mob player = this.cameraMob;
        WorldRenderer worldRenderer = this.mc.worldRenderer;
        ParticleRenderer particleRenderer = this.mc.particleRenderer;
        double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * tickDelta;
        double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * tickDelta;
        double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * tickDelta;
        GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        Profiler.endStartSection("fog");
        this.fog.updateColor(tickDelta);
        GL11.glClear(16640);
        GL11.glEnable(2884);
        Profiler.endStartSection("camera");
        this.applyCameraEffects(tickDelta);
        if (!(this.mc.currentScreen instanceof PhotoMode)) {
            this.fog.set(-1);
            Profiler.endStartSection("sky");
            this.mc.skyRenderer.renderSky(tickDelta);
        }

        GL11.glEnable(2912);
        this.fog.set(1);
        if (OptionValues.smoothLighting) {
            GL11.glShadeModel(7425);
        }

        Profiler.endStartSection("culling");
        Camera camera = new Camera();
        camera.setPosition(x, y, z);
        this.mc.worldRenderer.clipRenderers(camera, tickDelta);
        Profiler.endStartSection("updatechunks");
        this.mc.worldRenderer.updateChunks(player, false);
        this.fog.set(0);
        GL11.glEnable(2912);
        GL11.glBindTexture(3553, this.mc.textureLoader.getTexture("/terrain.png"));
        Lighting.disable();
        Profiler.endStartSection("terrain");
        worldRenderer.sortChunks(player, 0, tickDelta);
        GL11.glShadeModel(7424);
        Lighting.enable();
        Profiler.endStartSection("entities");
        worldRenderer.renderEntities(this.getCameraPosition(tickDelta), camera, tickDelta);
        this.lightmap.enable();
        Lighting.disable();
        this.fog.set(0);
        Profiler.endStartSection("particles");
        particleRenderer.renderParticles(player, tickDelta);
        Profiler.endStartSection("litParticles");
        particleRenderer.renderLitParticles(player, tickDelta);
        this.lightmap.disable();
        if (this.mc.world.settings.getType() != WorldType.CAVES && this.mc.world.currDim.hasClouds()) {
            Profiler.endStartSection("clouds");
            GL11.glPushMatrix();
            this.fog.set(0);
            this.mc.skyRenderer.renderClouds(tickDelta);
            this.fog.set(1);
            GL11.glPopMatrix();
        }

        GL11.glDisable(2884);
        Profiler.endStartSection("weather");
        this.weather.renderWeather(tickDelta);
        GL11.glDisable(3042);
        GL11.glEnable(2884);
        GL11.glBlendFunc(770, 771);
        this.fog.set(0);
        GL11.glEnable(3042);
        GL11.glDepthMask(false);
        GL11.glBindTexture(3553, this.mc.textureLoader.getTexture("/terrain.png"));
        Profiler.endStartSection("water");
        if (OptionValues.smoothLighting) {
            GL11.glShadeModel(7425);
        }

        worldRenderer.renderAllSortedChunks(1, tickDelta);
        GL11.glShadeModel(7424);
        GL11.glDepthMask(true);
        GL11.glEnable(2884);
        GL11.glDisable(3042);
        this.drawOutline(player, tickDelta);
        GL11.glDisable(2912);
        GL11.glClear(256);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GLU.gluPerspective(this.getFOV(tickDelta, false), (float)this.mc.displayWidth / this.mc.displayHeight, 0.05F, this.farPlaneDistance);
        GL11.glMatrixMode(5888);
        if (!(this.mc.currentScreen instanceof PhotoMode) && this.mc.player.properties.gamemode != 3) {
            Profiler.endStartSection("hand");
            this.renderHand(tickDelta);
            if (OptionValues.advCoords) {
                this.renderWorldAxis(tickDelta);
            }
        }

        Profiler.endSection();
    }

    private void drawOutline(Mob player, float tickDelta) {
        if (this.mc.selectedObject != null && OptionValues.showHUD && this.mc.player.properties.gamemode != 3) {
            GL11.glDisable(3008);
            Profiler.endStartSection("outline");
            this.blocks.renderBlockOutline(player, this.mc.selectedObject, tickDelta);
            this.blocks.renderBlockCracks(player, this.mc.selectedObject, tickDelta);
            GL11.glEnable(3008);
        }
    }

    public void setupOverlayRendering() {
        GL11.glClear(256);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0, this.mc.rescaler.getScaledWidth(), this.mc.rescaler.getScaledHeight(), 0.0, 1000.0, 3000.0);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
    }

    public void drawColoredLines(BoundingBox boundingBox, int red, int green, int blue, int alpha) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.begin(3);
        tessellator.setColorAlpha(red, green, blue, alpha);
        tessellator.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        tessellator.addVertex(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        tessellator.addVertex(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
        tessellator.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
        tessellator.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        tessellator.draw();
        tessellator.begin(3);
        tessellator.setColorAlpha(red, green, blue, alpha);
        tessellator.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        tessellator.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
        tessellator.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        tessellator.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        tessellator.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        tessellator.draw();
        tessellator.begin(1);
        tessellator.setColorAlpha(red, green, blue, alpha);
        tessellator.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        tessellator.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        tessellator.addVertex(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        tessellator.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
        tessellator.addVertex(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
        tessellator.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        tessellator.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
        tessellator.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        tessellator.draw();
    }

    public class MouseFilter {
        private float x;
        private float y;
        private float offset;

        public float smoothenMouse(float x, float y) {
            this.x += x;
            x = (this.x - this.y) * y;
            this.offset = this.offset + (x - this.offset) * 0.5F;
            if (x > 0.0F && x > this.offset || x < 0.0F && x < this.offset) {
                x = this.offset;
            }

            this.y += x;
            return x;
        }
    }
}
