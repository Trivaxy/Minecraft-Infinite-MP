package net.minecraft.client.render.entity.tileentity;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.localization.Font;
import net.minecraft.client.render.texture.TextureLoader;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.tileentity.container.ChestEntity;
import net.minecraft.game.block.tileentity.other.BeaconEntity;
import net.minecraft.game.block.tileentity.other.LecternEntity;
import net.minecraft.game.block.tileentity.other.NoteBlockEntity;
import net.minecraft.game.block.tileentity.other.PistonEntity;
import net.minecraft.game.block.tileentity.other.PortalEntity;
import net.minecraft.game.block.tileentity.other.SignEntity;
import net.minecraft.game.block.tileentity.other.SpawnerEntity;
import net.minecraft.game.entity.mob.Mob;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class TileEntityRenderManager {
    public static TileEntityRenderManager instance = new TileEntityRenderManager();
    private Map<Class<? extends TileEntity>, TileEntityRenderer<? extends TileEntity>> renderMap = new HashMap<>();
    private Font font;
    public TextureLoader texLoader;
    public Mob player;
    public float playerYaw;
    public float playerPitch;
    public double playerX;
    public double playerY;
    public double playerZ;
    public static double staticPlayerX;
    public static double staticPlayerY;
    public static double staticPlayerZ;

    private TileEntityRenderManager() {
        this.renderMap.put(SignEntity.class, new SignRenderer());
        this.renderMap.put(SpawnerEntity.class, new SpawnerRenderer());
        this.renderMap.put(PistonEntity.class, new PistonRenderer());
        this.renderMap.put(NoteBlockEntity.class, new NoteRenderer());
        this.renderMap.put(ChestEntity.class, new ChestRenderer());
        this.renderMap.put(LecternEntity.class, new LecternRenderer());
        this.renderMap.put(PortalEntity.class, new PortalRenderer());
        this.renderMap.put(BeaconEntity.class, new BeaconRenderer());

        for (TileEntityRenderer<? extends TileEntity> renderer : this.renderMap.values()) {
            renderer.setManager(this);
        }
    }

    public TileEntityRenderer<? extends TileEntity> getRenderer(Class<? extends TileEntity> TEClass) {
        TileEntityRenderer<? extends TileEntity> renderer = this.renderMap.get(TEClass);
        if (renderer == null && TEClass != TileEntity.class) {
            Class<? extends TileEntity> klass = (Class<? extends TileEntity>)TEClass.getSuperclass();
            renderer = this.getRenderer(klass);
            this.renderMap.put(TEClass, renderer);
        }

        return renderer;
    }

    public TileEntityRenderer<? extends TileEntity> getRenderer(TileEntity tileEntity1) {
        return tileEntity1 == null ? null : this.getRenderer((Class<? extends TileEntity>)tileEntity1.getClass());
    }

    public boolean hasRenderer(TileEntity tileEntity) {
        return this.getRenderer(tileEntity) != null;
    }

    public void cacheInfo(TextureLoader texLoader, Font font, Mob cameraMob, float tick) {
        this.texLoader = texLoader;
        this.player = cameraMob;
        this.font = font;
        this.playerYaw = cameraMob.prevYaw + (cameraMob.yaw - cameraMob.prevYaw) * tick;
        this.playerPitch = cameraMob.prevPitch + (cameraMob.pitch - cameraMob.prevPitch) * tick;
        this.playerX = cameraMob.lastTickPosX + (cameraMob.posX - cameraMob.lastTickPosX) * tick;
        this.playerY = cameraMob.lastTickPosY + (cameraMob.posY - cameraMob.lastTickPosY) * tick;
        this.playerZ = cameraMob.lastTickPosZ + (cameraMob.posZ - cameraMob.lastTickPosZ) * tick;
    }

    public void renderTileEntity(TileEntity tileEntity, float tick) {
        if (tileEntity != null && tileEntity.getDistanceFrom(this.playerX, this.playerY, this.playerZ) < tileEntity.getMaxDistance()) {
            int brightness = Minecraft.getMinecraft().world.getBrightness(tileEntity.x, tileEntity.y, tileEntity.z, 0);
            int x = brightness % 65536;
            int y = brightness / 65536;
            GL13.glMultiTexCoord2f(33985, x / 1.0F, y / 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.renderTileEntityAt(tileEntity, tileEntity.x - staticPlayerX, tileEntity.y - staticPlayerY, tileEntity.z - staticPlayerZ, tick);
        }
    }

    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float tick) {
        TileEntityRenderer<? super TileEntity> renderer = (TileEntityRenderer<? super TileEntity>)this.getRenderer(tileEntity);
        if (renderer != null) {
            renderer.render(tileEntity, x, y, z, tick);
        }
    }

    public Font getFontRenderer() {
        return this.font;
    }
}
