package net.minecraft.client.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.options.OptionValues;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.texture.TextureLoader;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public abstract class EntityRenderer<T extends Entity> {
    protected EntityRenderManager renderManager;
    protected float shadowSize = 0.0F;
    protected float shadowOpaque = 1.0F;
    protected static float rippleAnimTime = 0.0F;

    public void render(T entity, double x, double y, double z, float pitch, float yaw) {
        GL11.glPushMatrix();
        renderOffsetAABB(entity.boundingBox, x - entity.lastTickPosX, y - entity.lastTickPosY, z - entity.lastTickPosZ);
        GL11.glPopMatrix();
    }

    protected void loadTexture(String name) {
        TextureLoader texLoader = this.renderManager.texLoader;
        texLoader.bindTexture(texLoader.getTexture(name));
    }

    protected void loadSkin(String url, String name) {
        TextureLoader texLoader = this.renderManager.texLoader;
        texLoader.bindTexture(texLoader.downloadTexture(url, name));
    }

    public void setRenderManager(EntityRenderManager renderManager) {
        this.renderManager = renderManager;
    }

    public float getShadowSize() {
        return this.shadowSize;
    }

    public void setShadowSize(float shadowSize) {
        this.shadowSize = shadowSize;
    }

    protected void renderFire(Entity entity, double x, double y, double z, float ticks) {
        GL11.glDisable(2896);
        Icon fireIcon = BlockList.fire.getIcon(0);
        float minU = fireIcon.getMinU();
        float maxU = fireIcon.getMaxU();
        float minV = fireIcon.getMinV();
        float maxV = fireIcon.getMaxV();
        float width = entity.width * 1.4F;
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        GL11.glScalef(width, width, width);
        this.loadTexture("/terrain.png");
        Tessellator tess = Tessellator.instance;
        float xStart = 1.0F;
        float xOffset = 0.5F;
        float yOffset = entity.yOffset;
        float movement = entity.height / entity.width;
        GL11.glRotatef(-this.renderManager.yaw, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(0.0F, 0.0F, 0.4F + (int)movement * 0.02F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        tess.begin();

        while (movement > 0.0F) {
            tess.addTexturedVertex(xStart - xOffset, 0.0F - yOffset, 0.0, maxU, maxV);
            tess.addTexturedVertex(0.0F - xOffset, 0.0F - yOffset, 0.0, minU, maxV);
            tess.addTexturedVertex(0.0F - xOffset, 1.4F - yOffset, 0.0, minU, minV);
            tess.addTexturedVertex(xStart - xOffset, 1.4F - yOffset, 0.0, maxU, minV);
            movement--;
            yOffset--;
            xStart *= 0.9F;
            GL11.glTranslatef(0.0F, 0.0F, -0.04F);
        }

        tess.draw();
        GL11.glPopMatrix();
        GL11.glEnable(2896);
    }

    private void renderShadowAndRipple(Entity entity, double x, double y, double z, float distToGround, float tick) {
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        TextureLoader texLoader = this.renderManager.texLoader;
        Tessellator tess = Tessellator.instance;
        World world = this.renderManager.world;
        int entityX = MathHelper.floor_double(entity.posX);
        int entityY = MathHelper.floor_double(entity.posY);
        int entityZ = MathHelper.floor_double(entity.posZ);
        boolean inWater = world.getBlock(entityX, entityY, entityZ) == BlockList.waterStill && world.getBlockMetadata(entityX, entityY, entityZ) == 0;
        String texturePath;
        if (inWater) {
            int frame = entity.ticksExisted / 4 % 3;
            texturePath = "%%/misc/ripple" + frame + ".png";
        } else {
            texturePath = "%%/misc/shadow.png";
        }

        texLoader.bindTexture(texLoader.getTexture(texturePath));
        GL11.glDepthMask(false);
        float shadowSize = this.getShadowSize();
        double movementX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * tick;
        double movementY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * tick;
        double movementZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * tick;
        int startX = MathHelper.floor_double(movementX - shadowSize);
        int endX = MathHelper.floor_double(movementX + shadowSize);
        int startY = MathHelper.floor_double(movementY - shadowSize);
        int endY = MathHelper.floor_double(movementY);
        int startZ = MathHelper.floor_double(movementZ - shadowSize);
        int endZ = MathHelper.floor_double(movementZ + shadowSize);
        double finalX = x - movementX;
        double finalY = y - movementY;
        double finalZ = z - movementZ;
        tess.begin();

        for (int blockX = startX; blockX <= endX; blockX++) {
            for (int blockY = startY; blockY <= endY; blockY++) {
                for (int blockZ = startZ; blockZ <= endZ; blockZ++) {
                    int block = world.getBlockId(blockX, blockY - 1, blockZ);
                    if (block > 0
                        && world.getBlockBrightness(blockX, blockY, blockZ) > 3
                        && (!inWater || !world.getBlockMaterial(blockX, blockY + 1, blockZ).getIsLiquid())) {
                        this.renderShadow(BlockList.blocks[block], x, y, z, blockX, blockY, blockZ, distToGround, shadowSize, finalX, finalY, finalZ, inWater);
                    }
                }
            }
        }

        tess.draw();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(3042);
        GL11.glDepthMask(true);
    }

    private void renderShadow(
        Block block,
        double x,
        double y,
        double z,
        int blockX,
        int blockY,
        int blockZ,
        float distToGround,
        float shadowSize,
        double finalX,
        double finalY,
        double finalZ,
        boolean water
    ) {
        Tessellator tess = Tessellator.instance;
        if (block.fullBlock() || water) {
            double alpha = (distToGround - (y - (blockY + finalY)) / 2.0) * 0.5 * this.renderManager.world.getBrightness(blockX, blockY, blockZ);
            double yOffset = 0.015625;
            if (water) {
                alpha = 1.0;
                yOffset = 0.85;
            }

            if (alpha >= 0.0) {
                if (alpha > 1.0) {
                    alpha = 1.0;
                }

                double xStart = blockX + block.minX + finalX;
                double xEnd = blockX + block.maxX + finalX;
                double yStart = blockY + block.minY + finalY + yOffset;
                double zStart = blockZ + block.minZ + finalZ;
                double zEnd = blockZ + block.maxZ + finalZ;
                float u1 = (float)((x - xStart) / 2.0 / shadowSize + 0.5);
                float u2 = (float)((x - xEnd) / 2.0 / shadowSize + 0.5);
                float v1 = (float)((z - zStart) / 2.0 / shadowSize + 0.5);
                float v2 = (float)((z - zEnd) / 2.0 / shadowSize + 0.5);
                tess.setColorAlpha(1.0F, 1.0F, 1.0F, (float)alpha);
                tess.addTexturedVertex(xStart, yStart, zStart, u1, v1);
                tess.addTexturedVertex(xStart, yStart, zEnd, u1, v2);
                tess.addTexturedVertex(xEnd, yStart, zEnd, u2, v2);
                tess.addTexturedVertex(xEnd, yStart, zStart, u2, v1);
            }
        }
    }

    public void renderOverlays(Entity entity, double x, double y, double z, float heightToGround, float tickDelta) {
        if (OptionValues.enableShadows && this.getShadowSize() > 0.0F) {
            double camDist = this.renderManager.distToCam(entity.posX, entity.posY, entity.posZ);
            float strength = (float)((1.0 - camDist / 256.0) * this.shadowOpaque);
            if (strength > 0.0F) {
                this.renderShadowAndRipple(entity, x, y, z, strength, tickDelta);
            }
        }

        if (entity.fire > 0) {
            this.renderFire(entity, x, y, z, tickDelta);
        }
    }

    public static void renderOffsetAABB(BoundingBox aabb, double x, double y, double z) {
        Tessellator tess = Tessellator.instance;
        GL11.glDisable(3553);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        tess.begin();
        tess.setTranslation(x, y, z);
        tess.setNormals(0.0F, 0.0F, -1.0F);
        tess.addVertex(aabb.minX, aabb.maxY, aabb.minZ);
        tess.addVertex(aabb.maxX, aabb.maxY, aabb.minZ);
        tess.addVertex(aabb.maxX, aabb.minY, aabb.minZ);
        tess.addVertex(aabb.minX, aabb.minY, aabb.minZ);
        tess.setNormals(0.0F, 0.0F, 1.0F);
        tess.addVertex(aabb.minX, aabb.minY, aabb.maxZ);
        tess.addVertex(aabb.maxX, aabb.minY, aabb.maxZ);
        tess.addVertex(aabb.maxX, aabb.maxY, aabb.maxZ);
        tess.addVertex(aabb.minX, aabb.maxY, aabb.maxZ);
        tess.setNormals(0.0F, -1.0F, 0.0F);
        tess.addVertex(aabb.minX, aabb.minY, aabb.minZ);
        tess.addVertex(aabb.maxX, aabb.minY, aabb.minZ);
        tess.addVertex(aabb.maxX, aabb.minY, aabb.maxZ);
        tess.addVertex(aabb.minX, aabb.minY, aabb.maxZ);
        tess.setNormals(0.0F, 1.0F, 0.0F);
        tess.addVertex(aabb.minX, aabb.maxY, aabb.maxZ);
        tess.addVertex(aabb.maxX, aabb.maxY, aabb.maxZ);
        tess.addVertex(aabb.maxX, aabb.maxY, aabb.minZ);
        tess.addVertex(aabb.minX, aabb.maxY, aabb.minZ);
        tess.setNormals(-1.0F, 0.0F, 0.0F);
        tess.addVertex(aabb.minX, aabb.minY, aabb.maxZ);
        tess.addVertex(aabb.minX, aabb.maxY, aabb.maxZ);
        tess.addVertex(aabb.minX, aabb.maxY, aabb.minZ);
        tess.addVertex(aabb.minX, aabb.minY, aabb.minZ);
        tess.setNormals(1.0F, 0.0F, 0.0F);
        tess.addVertex(aabb.maxX, aabb.minY, aabb.minZ);
        tess.addVertex(aabb.maxX, aabb.maxY, aabb.minZ);
        tess.addVertex(aabb.maxX, aabb.maxY, aabb.maxZ);
        tess.addVertex(aabb.maxX, aabb.minY, aabb.maxZ);
        tess.setTranslation(0.0, 0.0, 0.0);
        tess.draw();
        GL11.glEnable(3553);
    }

    public static void renderAABB(BoundingBox aabb) {
        Tessellator tess = Tessellator.instance;
        tess.begin();
        tess.addVertex(aabb.minX, aabb.maxY, aabb.minZ);
        tess.addVertex(aabb.maxX, aabb.maxY, aabb.minZ);
        tess.addVertex(aabb.maxX, aabb.minY, aabb.minZ);
        tess.addVertex(aabb.minX, aabb.minY, aabb.minZ);
        tess.addVertex(aabb.minX, aabb.minY, aabb.maxZ);
        tess.addVertex(aabb.maxX, aabb.minY, aabb.maxZ);
        tess.addVertex(aabb.maxX, aabb.maxY, aabb.maxZ);
        tess.addVertex(aabb.minX, aabb.maxY, aabb.maxZ);
        tess.addVertex(aabb.minX, aabb.minY, aabb.minZ);
        tess.addVertex(aabb.maxX, aabb.minY, aabb.minZ);
        tess.addVertex(aabb.maxX, aabb.minY, aabb.maxZ);
        tess.addVertex(aabb.minX, aabb.minY, aabb.maxZ);
        tess.addVertex(aabb.minX, aabb.maxY, aabb.maxZ);
        tess.addVertex(aabb.maxX, aabb.maxY, aabb.maxZ);
        tess.addVertex(aabb.maxX, aabb.maxY, aabb.minZ);
        tess.addVertex(aabb.minX, aabb.maxY, aabb.minZ);
        tess.addVertex(aabb.minX, aabb.minY, aabb.maxZ);
        tess.addVertex(aabb.minX, aabb.maxY, aabb.maxZ);
        tess.addVertex(aabb.minX, aabb.maxY, aabb.minZ);
        tess.addVertex(aabb.minX, aabb.minY, aabb.minZ);
        tess.addVertex(aabb.maxX, aabb.minY, aabb.minZ);
        tess.addVertex(aabb.maxX, aabb.maxY, aabb.minZ);
        tess.addVertex(aabb.maxX, aabb.maxY, aabb.maxZ);
        tess.addVertex(aabb.maxX, aabb.minY, aabb.maxZ);
        tess.draw();
    }

    public void renderDebugHitboxes(T entity, double xo, double yo, double zo) {
        if (Minecraft.isDebugScreenFull()) {
            float entityWidth = entity.width;
            float entityHeight = entity.height;
            float scale = 2.0F;
            GL11.glDepthMask(false);
            GL11.glDisable(3553);
            GL11.glEnable(3042);
            GL11.glEnable(2848);
            GL11.glBlendFunc(770, 771);
            GL11.glColor4f(0.0F, 0.0F, 0.0F, 1.0F);
            GL11.glLineWidth(1.0F);
            BoundingBox aabb = new BoundingBox(
                xo - entityWidth / 2.0F,
                yo - entity.yOffset,
                zo - entityWidth / 2.0F,
                xo + entityWidth / 2.0F,
                yo + entityHeight - entity.yOffset,
                zo + entityWidth / 2.0F
            );
            Tessellator tess = Tessellator.instance;
            tess.begin(3);
            tess.addVertex(aabb.minX, aabb.minY, aabb.minZ);
            tess.addVertex(aabb.maxX, aabb.minY, aabb.minZ);
            tess.addVertex(aabb.maxX, aabb.minY, aabb.maxZ);
            tess.addVertex(aabb.minX, aabb.minY, aabb.maxZ);
            tess.addVertex(aabb.minX, aabb.minY, aabb.minZ);
            tess.draw();
            tess.begin(3);
            tess.addVertex(aabb.minX, aabb.maxY, aabb.minZ);
            tess.addVertex(aabb.maxX, aabb.maxY, aabb.minZ);
            tess.addVertex(aabb.maxX, aabb.maxY, aabb.maxZ);
            tess.addVertex(aabb.minX, aabb.maxY, aabb.maxZ);
            tess.addVertex(aabb.minX, aabb.maxY, aabb.minZ);
            tess.draw();
            tess.begin(1);
            tess.addVertex(aabb.minX, aabb.minY, aabb.minZ);
            tess.addVertex(aabb.minX, aabb.maxY, aabb.minZ);
            tess.addVertex(aabb.maxX, aabb.minY, aabb.minZ);
            tess.addVertex(aabb.maxX, aabb.maxY, aabb.minZ);
            tess.addVertex(aabb.maxX, aabb.minY, aabb.maxZ);
            tess.addVertex(aabb.maxX, aabb.maxY, aabb.maxZ);
            tess.addVertex(aabb.minX, aabb.minY, aabb.maxZ);
            tess.addVertex(aabb.minX, aabb.maxY, aabb.maxZ);
            tess.draw();
            GL11.glDepthMask(true);
            GL11.glDisable(3042);
            GL11.glDisable(2848);
            GL11.glEnable(3553);
        }
    }
}
