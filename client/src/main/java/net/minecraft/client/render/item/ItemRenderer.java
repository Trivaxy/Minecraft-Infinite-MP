package net.minecraft.client.render.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.localization.Font;
import net.minecraft.client.options.OptionValues;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.BlocksRenderer;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PlayerRenderer;
import net.minecraft.client.render.texture.TextureLoader;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.building.LayerBlock;
import net.minecraft.game.block.decoration.Carpet;
import net.minecraft.game.block.decoration.Trapdoor;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.block.BlockItem;
import net.minecraft.game.item.enchantment.EnchantmentHelper;
import net.minecraft.game.item.enchantment.EnchantmentList;
import net.minecraft.game.item.tools.FishingRod;
import net.minecraft.game.item.weapons.Crossbow;
import net.minecraft.game.item.weapons.Spear;
import net.minecraft.game.item.weapons.TridentItem;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.world.mapdata.MapData;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class ItemRenderer {
    private Minecraft mc;
    public ItemStack mainHand = null;
    public ItemStack offHand = null;
    public MapRenderer mapRenderer;
    private BlocksRenderer blockRenderer = new BlocksRenderer();
    private TextureLoader textureLoader;
    private float equippedProgress = 0.0F;
    private float prevEquippedProgress = 0.0F;
    private float offhandProgress = 0.0F;
    private float prevOffhandProgress = 0.0F;
    public float zLevel = 0.0F;

    public ItemRenderer(Minecraft minecraft) {
        this.mc = minecraft;
        this.textureLoader = this.mc.textureLoader;
        this.mapRenderer = new MapRenderer(minecraft.fontRenderer, minecraft.textureLoader);
    }

    public ItemRenderer() {
        this.textureLoader = Minecraft.getMinecraft().textureLoader;
    }

    public void renderStacks(ItemStack stack, float yaw, int stacks) {
        GL11.glPushMatrix();
        GL11.glRotatef(yaw, 0.0F, 1.0F, 0.0F);
        GL11.glEnable(32826);
        float scale = 0.5F;
        GL11.glScalef(scale, scale, scale);
        float offset = 0.0625F;
        float offsetAddon = 0.021875F;
        float bobSpeed = 0.25F;
        GL11.glTranslatef(-0.5F, -bobSpeed, -((offset + offsetAddon) * stacks / 2.0F));

        for (int theStack = 0; theStack < stacks; theStack++) {
            GL11.glPushMatrix();
            if (theStack > 0) {
                if (theStack == 1) {
                    theStack = 2;
                }

                GL11.glTranslatef(0.0F, 0.0F, theStack * (offset + offsetAddon));
            } else {
                GL11.glTranslatef(0.0F, 0.0F, offset + offsetAddon);
            }

            this.renderItem(stack);
            GL11.glPopMatrix();
        }

        GL11.glDisable(32826);
        GL11.glPopMatrix();
    }

    public void renderItemModel(ItemStack stack) {
        GL11.glPushMatrix();
        if (!(stack.getItem() instanceof BlockItem) || !this.blockRenderer.isModel3D(BlockList.blocks[stack.itemID].getRenderType())) {
            GL11.glTranslatef(0.0F, -0.3F, 0.0F);
            GL11.glScalef(1.5F, 1.5F, 1.5F);
            GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(-0.9375F, -0.0625F, 0.0F);
        }

        this.renderItem(stack);
        GL11.glPopMatrix();
    }

    public void renderFramedItem(ItemStack stack) {
        GL11.glPushMatrix();
        if (!(stack.getItem() instanceof BlockItem) || !this.blockRenderer.isModel3D(BlockList.blocks[stack.itemID].getRenderType())) {
            GL11.glScalef(1.5F, 1.5F, 1.5F);
        }

        this.renderItem(stack);
        GL11.glPopMatrix();
    }

    public void renderItem(ItemStack stack) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        if (stack.getItem() instanceof BlockItem && this.blockRenderer.isModel3D(BlockList.blocks[stack.itemID].getRenderType())) {
            GL11.glBindTexture(3553, this.textureLoader.getTexture("/terrain.png"));
            this.blockRenderer.renderBlockGUI(BlockList.blocks[stack.itemID], stack.getItemDamage());
        } else {
            Icon icon = null;
            if (this.mc != null && this.mc.player != null) {
                icon = this.mc.player.getItemIcon(stack, 0);
            } else {
                icon = stack.getIconIndex();
            }

            if (stack.getItem() instanceof BlockItem) {
                GL11.glBindTexture(3553, this.textureLoader.getTexture("/terrain.png"));
            } else {
                GL11.glBindTexture(3553, this.textureLoader.getTexture("/gui/items.png"));
            }

            Tessellator tess = Tessellator.instance;
            float minU = icon.getMinU();
            float maxU = icon.getMaxU();
            float minV = icon.getMinV();
            float maxV = icon.getMaxV();
            float widthDifference = 0.5F * (maxU - minU) / icon.getIconWidth();
            float heightDifference = 0.5F * (maxV - minV) / icon.getIconHeight();
            float width = 0.0625F;
            GL11.glEnable(32826);
            tess.begin();
            tess.setColor(stack.getItem().getColorTint(stack));
            tess.setNormals(0.0F, 0.0F, 1.0F);
            tess.addTexturedVertex(0.0, 0.0, 0.0, maxU, maxV);
            tess.addTexturedVertex(1.0, 0.0, 0.0, minU, maxV);
            tess.addTexturedVertex(1.0, 1.0, 0.0, minU, minV);
            tess.addTexturedVertex(0.0, 1.0, 0.0, maxU, minV);
            tess.setNormals(0.0F, 0.0F, -1.0F);
            tess.addTexturedVertex(0.0, 1.0, -width, maxU, minV);
            tess.addTexturedVertex(1.0, 1.0, -width, minU, minV);
            tess.addTexturedVertex(1.0, 0.0, -width, minU, maxV);
            tess.addTexturedVertex(0.0, 0.0, -width, maxU, maxV);
            tess.setNormals(-1.0F, 0.0F, 0.0F);
            float offsetFix = 4.0E-4F;

            for (int pixel = 0; pixel < icon.getIconWidth(); pixel++) {
                float start = (float)pixel / icon.getIconWidth() + offsetFix;
                float end = maxU + (minU - maxU) * start - widthDifference;
                tess.addTexturedVertex(start, 0.0, -width, end, maxV);
                tess.addTexturedVertex(start, 0.0, 0.0, end, maxV);
                tess.addTexturedVertex(start, 1.0, 0.0, end, minV);
                tess.addTexturedVertex(start, 1.0, -width, end, minV);
            }

            tess.setNormals(1.0F, 0.0F, 0.0F);

            for (int var17 = 0; var17 < icon.getIconWidth(); var17++) {
                float start = (float)var17 / icon.getIconWidth() - offsetFix;
                float end = maxU + (minU - maxU) * start - widthDifference;
                float off = start + 1.0F / icon.getIconWidth();
                tess.addTexturedVertex(off, 1.0, -width, end, minV);
                tess.addTexturedVertex(off, 1.0, 0.0, end, minV);
                tess.addTexturedVertex(off, 0.0, 0.0, end, maxV);
                tess.addTexturedVertex(off, 0.0, -width, end, maxV);
            }

            tess.setNormals(0.0F, 1.0F, 0.0F);

            for (int var18 = 0; var18 < icon.getIconHeight(); var18++) {
                float start = (float)var18 / icon.getIconHeight() - offsetFix;
                float end = maxV + (minV - maxV) * start - heightDifference;
                float off = start + 1.0F / icon.getIconHeight();
                tess.addTexturedVertex(0.0, off, 0.0, maxU, end);
                tess.addTexturedVertex(1.0, off, 0.0, minU, end);
                tess.addTexturedVertex(1.0, off, -width, minU, end);
                tess.addTexturedVertex(0.0, off, -width, maxU, end);
            }

            tess.setNormals(0.0F, -1.0F, 0.0F);

            for (int var19 = 0; var19 < icon.getIconHeight(); var19++) {
                float start = (float)var19 / icon.getIconHeight() + offsetFix;
                float end = maxV + (minV - maxV) * start - heightDifference;
                tess.addTexturedVertex(1.0, start, 0.0, minU, end);
                tess.addTexturedVertex(0.0, start, 0.0, maxU, end);
                tess.addTexturedVertex(0.0, start, -width, maxU, end);
                tess.addTexturedVertex(1.0, start, -width, minU, end);
            }

            tess.draw();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            if (stack != null && stack.hasEffect()) {
                GL11.glDepthFunc(514);
                GL11.glDisable(2896);
                this.textureLoader.bindTexture(this.textureLoader.getTexture("/misc/glint.png"));
                GL11.glEnable(3042);
                GL11.glBlendFunc(768, 1);
                float start = 0.76F;
                if (EnchantmentHelper.getLevel(EnchantmentList.sharpness.id, stack) > 0) {
                    GL11.glColor4f(0.8F * start, 0.25F * start, 0.25F * start, 1.0F);
                } else if (EnchantmentHelper.getLevel(EnchantmentList.efficiency.id, stack) > 0) {
                    GL11.glColor4f(0.25F * start, 0.25F * start, 0.8F * start, 1.0F);
                } else if (EnchantmentHelper.getLevel(EnchantmentList.unbreaking.id, stack) > 0) {
                    GL11.glColor4f(0.25F * start, 0.8F * start, 0.25F * start, 1.0F);
                } else if (EnchantmentHelper.getLevel(EnchantmentList.protection.id, stack) > 0) {
                    GL11.glColor4f(0.8F * start, 0.8F * start, 0.25F * start, 1.0F);
                } else {
                    GL11.glColor4f(0.76F, 0.38F, 0.98F, 1.0F);
                }

                GL11.glMatrixMode(5890);
                GL11.glPushMatrix();
                float end = 0.125F;
                GL11.glScalef(end, end, end);
                float off = (float)(System.currentTimeMillis() % 16000L) / 16000.0F * 8.0F;
                GL11.glTranslatef(off, 0.0F, 0.0F);
                GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                this.applyGlintOverlay(tess, 0.0F, 0.0F, 1.0F, 1.0F);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glScalef(end, end, end);
                off = (float)(System.currentTimeMillis() % 8873L) / 8873.0F * 8.0F;
                GL11.glTranslatef(-off, 0.0F, 0.0F);
                GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                this.applyGlintOverlay(tess, 0.0F, 0.0F, 1.0F, 1.0F);
                GL11.glPopMatrix();
                GL11.glMatrixMode(5888);
                GL11.glDisable(3042);
                GL11.glEnable(2896);
                GL11.glDepthFunc(515);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            }

            GL11.glDisable(32826);
        }

        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    private void applyGlintOverlay(Tessellator tess, float minU, float maxU, float minV, float maxV) {
        double offset = 0.0625;
        tess.begin();
        tess.setNormals(0.0F, 0.0F, 1.0F);
        tess.addTexturedVertex(0.0, 0.0, 0.0, minU, maxV);
        tess.addTexturedVertex(1.0, 0.0, 0.0, minV, maxV);
        tess.addTexturedVertex(1.0, 1.0, 0.0, minV, maxU);
        tess.addTexturedVertex(0.0, 1.0, 0.0, minU, maxU);
        tess.setNormals(0.0F, 0.0F, -1.0F);
        tess.addTexturedVertex(0.0, 1.0, -offset, minU, maxU);
        tess.addTexturedVertex(1.0, 1.0, -offset, minV, maxU);
        tess.addTexturedVertex(1.0, 0.0, -offset, minV, maxV);
        tess.addTexturedVertex(0.0, 0.0, -offset, minU, maxV);
        tess.setNormals(-1.0F, 0.0F, 0.0F);
        float offsetFix = 4.0E-4F;

        for (int pixel = 0; pixel < 16; pixel++) {
            float start = pixel / 16.0F + offsetFix;
            float end = minU + (minV - minU) * start - 0.001953125F;
            double off = start;
            tess.addTexturedVertex(off, 0.0, -offset, end, maxV);
            tess.addTexturedVertex(off, 0.0, 0.0, end, maxV);
            tess.addTexturedVertex(off, 1.0, 0.0, end, maxU);
            tess.addTexturedVertex(off, 1.0, -offset, end, maxU);
        }

        tess.setNormals(1.0F, 0.0F, 0.0F);

        for (int var14 = 0; var14 < 16; var14++) {
            float start = var14 / 16.0F - offsetFix;
            float end = minU + (minV - minU) * start - 0.001953125F;
            double off = start + offset;
            tess.addTexturedVertex(off, 1.0, -offset, end, maxU);
            tess.addTexturedVertex(off, 1.0, 0.0, end, maxU);
            tess.addTexturedVertex(off, 0.0, 0.0, end, maxV);
            tess.addTexturedVertex(off, 0.0, -offset, end, maxV);
        }

        tess.setNormals(0.0F, 1.0F, 0.0F);

        for (int var15 = 0; var15 < 16; var15++) {
            float start = var15 / 16.0F - offsetFix;
            float end = maxV + (maxU - maxV) * start - 0.001953125F;
            double off = start + offset;
            tess.addTexturedVertex(0.0, off, 0.0, minU, end);
            tess.addTexturedVertex(1.0, off, 0.0, minV, end);
            tess.addTexturedVertex(1.0, off, -offset, minV, end);
            tess.addTexturedVertex(0.0, off, -offset, minU, end);
        }

        tess.setNormals(0.0F, -1.0F, 0.0F);

        for (int var16 = 0; var16 < 16; var16++) {
            float start = var16 / 16.0F + offsetFix;
            float end = maxV + (maxU - maxV) * start - 0.001953125F;
            double off = start;
            tess.addTexturedVertex(1.0, off, 0.0, minV, end);
            tess.addTexturedVertex(0.0, off, 0.0, minU, end);
            tess.addTexturedVertex(0.0, off, -offset, minU, end);
            tess.addTexturedVertex(1.0, off, -offset, minV, end);
        }

        tess.draw();
    }

    public void renderHeldItem(ItemStack stack, float tick) {
        LocalPlayer player = this.mc.player;
        float equipProgress = this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * tick;
        float swingProgress = player.getSwing(tick);
        if (stack == this.offHand) {
            equipProgress = this.prevOffhandProgress + (this.offhandProgress - this.prevOffhandProgress) * tick;
            swingProgress = player.getSwingOffhand(tick);
        }

        GL11.glPushMatrix();
        GL11.glRotatef(player.prevPitch + (player.pitch - player.prevPitch) * tick, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(player.prevYaw + (player.yaw - player.prevYaw) * tick, 0.0F, 1.0F, 0.0F);
        Lighting.enable();
        GL11.glPopMatrix();
        if (player instanceof LocalPlayer && OptionValues.viewBobbing) {
            float pitch = player.prevArmPitch + (player.armPitch - player.prevArmPitch) * tick;
            float yaw = player.prevArmYaw + (player.armYaw - player.prevArmYaw) * tick;
            GL11.glRotatef((player.pitch - pitch) * 0.1F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef((player.yaw - yaw) * 0.1F, 0.0F, 1.0F, 0.0F);
        }

        if (player.getItemAnimFrames() <= 0 && OptionValues.viewBobbing) {
            float timer = 18.0F + (player.ticksExisted + tick + 1.0F);
            float offsetAmount = timer / 20.0F;
            offsetAmount = (offsetAmount * offsetAmount + offsetAmount * 4.0F) / 3.0F;
            if (offsetAmount > 1.0F) {
                offsetAmount = 1.0F;
            }

            if (offsetAmount > 0.1F) {
                GL11.glTranslatef(0.0F, MathHelper.sin((timer - 0.1F) * 0.1F) * 0.01F * (offsetAmount - 0.1F), 0.0F);
            }
        }

        int brightness = this.mc
            .world
            .getBrightness(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY), MathHelper.floor_double(player.posZ), 0);
        int u = brightness % 65536;
        int v = brightness / 65536;
        GL13.glMultiTexCoord2f(33985, u / 1.0F, v / 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (stack != null) {
            if (stack.itemID == ItemList.map.id) {
                this.renderMap(player, stack, tick, swingProgress);
            } else {
                GL11.glPushMatrix();
                this.animateItems(stack, player, equipProgress, swingProgress, tick);
                if (stack.getItem() instanceof FishingRod || stack.getItem() == ItemList.stickCarrot) {
                    GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                } else if (stack.getItem() instanceof Crossbow) {
                    boolean isOffhand = stack == this.offHand;
                    GL11.glRotatef(-83.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glRotatef(35.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(isOffhand ? 35.0F : 30.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glTranslatef(0.0F, 0.0F, isOffhand ? -0.2F : -0.3F);
                    GL11.glTranslatef(0.0F, -0.5F, isOffhand ? -0.1F : 0.0F);
                    GL11.glTranslatef(-0.15F, 0.0F, 0.0F);
                } else if (stack.getItem() instanceof Spear) {
                    GL11.glRotatef(-7.5F, 1.0F, 0.0F, 0.0F);
                    GL11.glTranslatef(0.0F, 0.15F, 0.1F);
                }

                this.renderItemModel(stack);
                GL11.glPopMatrix();
            }
        }

        GL11.glDisable(32826);
        Lighting.disable();
    }

    private void renderMap(Player player, ItemStack stack, float tick, float swing) {
        GL11.glPushMatrix();
        float offset = 0.8F;
        float swingProgress = MathHelper.sin(swing * (float) Math.PI);
        float angle = MathHelper.sin(MathHelper.sqrt_float(swing) * (float) Math.PI);
        float rotation = player.prevPitch + (player.pitch - player.prevPitch) * tick;
        GL11.glTranslatef(
            (stack != this.offHand ? -1 : 1) * angle * 0.4F,
            MathHelper.sin(MathHelper.sqrt_float(swing) * (float) Math.PI * 2.0F) * 0.2F,
            -swingProgress * 0.2F
        );
        swing = 1.0F - rotation / 45.0F + 0.1F;
        swing = MathHelper.clamp(swing, 0.0F, 1.0F);
        swing = -MathHelper.cos(swing * (float) Math.PI) * 0.5F + 0.5F;
        float equipProgress = this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * tick;
        if (stack == this.offHand) {
            equipProgress = this.prevOffhandProgress + (this.offhandProgress - this.prevOffhandProgress) * tick;
        }

        if (this.offHand != null) {
            GL11.glTranslatef(0.0F, 0.0F * offset - (1.0F - equipProgress) * 1.2F - 0.5F + 0.04F, -0.9F * offset);
        } else {
            GL11.glTranslatef(0.0F, 0.0F * offset - (1.0F - equipProgress) * 1.2F - swing * 0.5F + 0.04F, -0.9F * offset);
        }

        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        if (this.offHand == null) {
            GL11.glRotatef(swing * -85.0F, 0.0F, 0.0F, 1.0F);
        }

        GL11.glEnable(32826);
        GL11.glBindTexture(3553, this.textureLoader.downloadTexture(this.mc.player.skinUrl, this.mc.player.getTexture()));

        for (int arm = 0; arm < 2; arm++) {
            int armoffset = arm * 2 - 1;
            if (this.offHand != null) {
                armoffset = 1;
            }

            if (stack == this.offHand) {
                armoffset = -1;
            }

            GL11.glPushMatrix();
            GL11.glTranslatef(-0.0F, -0.6F, 1.1F * armoffset);
            if (this.offHand == null) {
                GL11.glRotatef(-45 * armoffset, 1.0F, 0.0F, 0.0F);
            }

            GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(59.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(-65 * armoffset, 0.0F, 1.0F, 0.0F);
            EntityRenderer<? extends Entity> entity = EntityRenderManager.instance.getRenderer(this.mc.player);
            PlayerRenderer playerRenderer = (PlayerRenderer)entity;
            GL11.glScalef(1.0F, 1.0F, 1.0F);
            playerRenderer.drawFirstPersonHand(this.mc.player);
            GL11.glPopMatrix();
            if (this.offHand != null) {
                break;
            }
        }

        if (this.offHand == null) {
            swingProgress = player.getSwing(tick);
            angle = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
            float angleSecondary = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
            GL11.glRotatef(-angle * 20.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-angleSecondary * 20.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(-angleSecondary * 80.0F, 1.0F, 0.0F, 0.0F);
        }

        float scale = 0.38F;
        GL11.glScalef(scale, scale, scale);
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef(-1.0F, -1.0F, 0.0F);
        if (this.offHand != null) {
            scale = 0.01F;
        } else {
            scale = 0.015625F;
        }

        GL11.glScalef(scale, scale, scale);
        this.textureLoader.bindTexture("/misc/mapbg.png");
        Tessellator tess = Tessellator.instance;
        GL11.glNormal3f(0.0F, 0.0F, -1.0F);
        if (stack == this.offHand) {
            GL11.glTranslatef(-125.0F, -55.5F, 0.0F);
        } else if (this.offHand != null) {
            GL11.glTranslatef(180.0F, -55.5F, 0.0F);
        }

        Lighting.disable();
        tess.begin();
        tess.addTexturedVertex(-7.0, 135.0, 0.0, 0.0F, 1.0F);
        tess.addTexturedVertex(135.0, 135.0, 0.0, 1.0F, 1.0F);
        tess.addTexturedVertex(135.0, -7.0, 0.0, 1.0F, 0.0F);
        tess.addTexturedVertex(-7.0, -7.0, 0.0, 0.0F, 0.0F);
        tess.draw();
        MapData data = ItemList.map.getMapData(stack, this.mc.world);
        if (data != null) {
            this.mapRenderer.renderMap(this.mc.player, this.textureLoader, data, true);
        }

        Lighting.enable();
        GL11.glPopMatrix();
    }

    public void renderArm(Player player, float tick) {
        GL11.glPushMatrix();
        float offset = 0.8F;
        float swing = this.mc.player.getSwing(tick);
        float equipProgress = this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * tick;
        float horizontalAngle = MathHelper.sin(swing * (float) Math.PI);
        float verticalAngle = MathHelper.sin(MathHelper.sqrt_float(swing) * (float) Math.PI);
        GL11.glTranslatef(-verticalAngle * 0.3F, MathHelper.sin(MathHelper.sqrt_float(swing) * (float) Math.PI * 2.0F) * 0.4F, -horizontalAngle * 0.4F);
        GL11.glTranslatef(0.8F * offset, -0.75F * offset - (1.0F - equipProgress) * 0.6F, -0.9F * offset);
        GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
        GL11.glEnable(32826);
        GL11.glRotatef(verticalAngle * 70.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-horizontalAngle * 20.0F, 0.0F, 0.0F, 1.0F);
        GL11.glBindTexture(3553, this.mc.textureLoader.downloadTexture(this.mc.player.skinUrl, this.mc.player.getTexture()));
        GL11.glTranslatef(-1.0F, 3.6F, 3.5F);
        GL11.glRotatef(120.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(200.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
        GL11.glScalef(1.0F, 1.0F, 1.0F);
        GL11.glTranslatef(5.6F, 0.0F, 0.0F);
        PlayerRenderer playerRenderer = (PlayerRenderer)EntityRenderManager.instance.getRenderer(this.mc.player);
        verticalAngle = 1.0F;
        GL11.glScalef(verticalAngle, verticalAngle, verticalAngle);
        playerRenderer.drawFirstPersonHand(player);
        GL11.glPopMatrix();
    }

    private void animateItems(ItemStack stack, LocalPlayer player, float swingProgress, float swing, float tick) {
        float offset = 0.8F;
        boolean isOffhand = stack == this.offHand;
        boolean isBlock3D = stack.getItem() instanceof BlockItem && this.blockRenderer.isModel3D(BlockList.blocks[stack.itemID].getRenderType());
        if (player.getItemAnimFrames() > 0 && stack == player.getItemInUse()) {
            Item.Action action = stack.getItemUseAction();
            if (action == Item.Action.eat || action == Item.Action.drink) {
                float useCounter = player.getItemAnimFrames() - tick + 1.0F;
                float timer = 1.0F - useCounter / stack.getMaxItemUseDuration();
                float timerOffset = 1.0F - timer;
                timerOffset = timerOffset * timerOffset * timerOffset;
                timerOffset = timerOffset * timerOffset * timerOffset;
                timerOffset = timerOffset * timerOffset * timerOffset;
                float finalOffset = 1.0F - timerOffset;
                GL11.glTranslatef(0.0F, MathHelper.abs(MathHelper.cos(useCounter / 4.0F * 3.141593F) * 0.1F) * (timer <= 0.2 ? 0 : 1), 0.0F);
                GL11.glTranslatef((!isOffhand ? 1 : -1) * finalOffset * 0.6F, -finalOffset * 0.5F, 0.0F);
                GL11.glRotatef((!isOffhand ? 1 : -1) * (finalOffset * 90.0F), 0.0F, 1.0F, 0.0F);
                if (!isOffhand) {
                    GL11.glRotatef(finalOffset * 10.0F, 1.0F, 0.0F, 0.0F);
                }

                GL11.glRotatef((!isOffhand ? 1 : -1) * (finalOffset * 30.0F), 0.0F, 0.0F, 1.0F);
            }
        } else {
            float horizontalAngle = MathHelper.sin(swing * (float) Math.PI);
            float verticalAngle = MathHelper.sin(MathHelper.sqrt_float(swing) * (float) Math.PI);
            if (!(stack.getItem() instanceof Spear)) {
                GL11.glTranslatef(
                    (isOffhand ? 1 : -1) * verticalAngle * 0.4F,
                    MathHelper.sin(MathHelper.sqrt_float(swing) * (float) Math.PI * 2.0F) * 0.2F,
                    -horizontalAngle * 0.2F
                );
            }
        }

        if (isBlock3D
            && (
                BlockList.blocks[stack.itemID] instanceof Trapdoor
                    || BlockList.blocks[stack.itemID] instanceof LayerBlock
                    || BlockList.blocks[stack.itemID] instanceof Carpet
            )) {
            GL11.glTranslatef(0.0F, 0.2F, 0.0F);
        }

        GL11.glTranslatef((!isOffhand ? 1 : -1) * 0.7F * offset, -0.65F * offset - (1.0F - swingProgress) * 0.6F, -0.9F * offset);
        GL11.glRotatef(isOffhand && isBlock3D ? -45.0F : 45.0F, 0.0F, 1.0F, 0.0F);
        GL11.glEnable(32826);
        float horizontalAngle = MathHelper.sin(swing * swing * (float) Math.PI);
        float verticalAngle = MathHelper.sin(MathHelper.sqrt_float(swing) * (float) Math.PI);
        GL11.glRotatef(-horizontalAngle * (isOffhand && !isBlock3D ? 80.0F : 20.0F), 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-verticalAngle * (isOffhand && !isBlock3D ? 60.0F : 20.0F), 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(-verticalAngle * (isOffhand && !isBlock3D ? 20.0F : 80.0F), 1.0F, 0.0F, 0.0F);
        if (!this.mc.player.swinging && !this.mc.player.swingingOffhand && player.itemUseCooldown > 0 && this.mc.player.getSwing(tick) <= 1.0F) {
            float cooldown = player.maxDelay - (player.itemUseCooldown - tick + 1.0F);
            float angle = cooldown / (player.maxDelay + player.maxDelay / 4);
            if (angle > 0.0F) {
                GL11.glTranslatef(0.0F, -0.8F + angle, 0.0F);
            }
        }

        GL11.glScalef(0.4F, 0.4F, 0.4F);
        if (player.getItemAnimFrames() > 0 && stack == player.getItemInUse()) {
            Item.Action action = stack.getItemUseAction();
            if (action == Item.Action.block && player.inventory.getOffHandItem() == null) {
                GL11.glTranslatef(-0.5F, 0.2F, 0.0F);
                GL11.glRotatef(30.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-80.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(60.0F, 0.0F, 1.0F, 0.0F);
            } else if (action == Item.Action.bow) {
                float duration = stack.getMaxItemUseDuration() - (player.getItemAnimFrames() - tick + 1.0F);
                float progress = duration / 20.0F;
                progress = (progress * progress + progress * 2.0F) / 3.0F;
                if (progress > 1.0F) {
                    progress = 1.0F;
                }

                if (progress > 0.1F) {
                    GL11.glTranslatef(0.0F, MathHelper.sin((duration - 0.1F) * 1.3F) * 0.01F * (progress - 0.1F), 0.0F);
                }

                if (stack.getItem() instanceof TridentItem) {
                    GL11.glTranslatef(0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(-10.0F, 0.0F, 0.0F, 1.0F);
                }

                GL11.glTranslatef(0.0F, 0.0F, progress * 0.1F);
                GL11.glRotatef(-335.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(0.0F, 0.5F, 0.0F);
                float scale = 1.0F + progress * 0.2F;
                GL11.glScalef(1.0F, 1.0F, scale);
                GL11.glTranslatef(0.0F, -0.5F, 0.0F);
                GL11.glRotatef(50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(335.0F, 0.0F, 0.0F, 1.0F);
            } else if (action == Item.Action.dig) {
                float duration = stack.getMaxItemUseDuration() - (player.getItemAnimFrames() - tick + 1.0F);
                float progress = duration / 20.0F;
                swing = 1.0F + progress * 0.8F;
                horizontalAngle = MathHelper.sin(swing * swing * (float) Math.PI);
                GL11.glRotatef(-horizontalAngle * 50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(0.0F, -0.3F, 0.0F);
                GL11.glRotatef(-25.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(60.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(300.0F, 0.0F, 0.0F, 1.0F);
            } else if (action == Item.Action.joust) {
                float elapsedTicks = (float)stack.getMaxItemUseDuration() - player.getItemAnimFrames() + tick - 1.0F;
                if (elapsedTicks < 0.0F) {
                    elapsedTicks = 0.0F;
                }

                float speedPerTick = 0.08F;
                float progress = elapsedTicks * speedPerTick;
                progress = Math.min(progress, 1.0F);
                float ease = 1.0F - (1.0F - progress) * (1.0F - progress);
                GL11.glRotatef(-145.0F * ease, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-20.0F * ease, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-90.0F * ease, 0.0F, 0.0F, 1.0F);
                if (player.getItemAnimFrames() < stack.getMaxItemUseDuration() / 2) {
                    float overCharge = (progress - 0.85F) / 0.15F;
                    float t = elapsedTicks * 0.5F;
                    float swayX = MathHelper.sin(t) * 3.0F * overCharge;
                    float swayZ = MathHelper.sin(t * 0.7F + 1.0F) * 2.0F * overCharge;
                    float drop = MathHelper.sin(t * 0.5F) * 0.05F * overCharge;
                    GL11.glRotatef(swayX, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(swayZ, 0.0F, 0.0F, 1.0F);
                    GL11.glTranslatef(0.0F, -drop, 0.0F);
                    int useTime = player.getItemAnimFrames();
                    int maxUse = stack.getMaxItemUseDuration();
                    int lowerThreshold = maxUse / 4;
                    float smoothUseTime = useTime - tick;
                    if (useTime < lowerThreshold) {
                        float lowerProgress = 1.0F - smoothUseTime / lowerThreshold;
                        lowerProgress = lowerProgress * lowerProgress * (3.0F - 2.0F * lowerProgress);
                        float yOffset = -1.2F * lowerProgress;
                        GL11.glTranslatef(0.0F, yOffset, 0.0F);
                        GL11.glRotatef(20.0F * lowerProgress, 0.0F, 0.0F, 1.0F);
                    }
                }
            }
        }
    }

    public void updateEquippedItem() {
        this.prevEquippedProgress = this.equippedProgress;
        this.prevOffhandProgress = this.offhandProgress;
        LocalPlayer player = this.mc.player;
        ItemStack mainHand = player.inventory.getCurrentItem();
        ItemStack offHand = player.inventory.getOffHandItem();
        float offset = 0.4F;
        float isEquip = mainHand == this.mainHand ? 1.0F : 0.0F;
        float equipProg = isEquip - this.equippedProgress;
        equipProg = MathHelper.clamp(equipProg, -offset, offset);
        this.equippedProgress += equipProg;
        if (this.equippedProgress < 0.1F) {
            this.mainHand = mainHand;
        }

        float isOff = offHand == this.offHand ? 1.0F : 0.0F;
        float offProg = isOff - this.offhandProgress;
        offProg = MathHelper.clamp(offProg, -offset, offset);
        this.offhandProgress += offProg;
        if (this.offhandProgress < 0.1F) {
            this.offHand = offHand;
        }
    }

    public void resetEquippedProgress() {
        this.equippedProgress = 0.0F;
    }

    public void renderItemSprite(Font font, TextureLoader texLoader, ItemStack stack, int x, int y) {
        if (stack != null) {
            Icon icon;
            if (this.mc != null && this.mc.player != null) {
                icon = this.mc.player.getItemIcon(stack, 0);
            } else {
                icon = stack.getIconIndex();
            }

            if (stack.getItem() instanceof BlockItem && this.blockRenderer.isModel3D(BlockList.blocks[stack.itemID].getRenderType())) {
                int id = stack.itemID;
                texLoader.bindTexture(texLoader.getTexture("/terrain.png"));
                Block block = BlockList.blocks[id];
                GL11.glPushMatrix();
                GL11.glTranslatef(x - 2, y + 3, 100.0F + this.zLevel);
                GL11.glScalef(1.0F, 1.0F, -1.0F);
                GL11.glScalef(10.0F, 10.0F, 10.0F);
                GL11.glTranslatef(1.0F, 0.5F, 8.0F);
                GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
                this.blockRenderer.renderBlockGUI(block, stack.getItemDamage());
                GL11.glDisable(3042);
                GL11.glPopMatrix();
            } else {
                GL11.glDisable(2896);
                if (stack.getItem() instanceof BlockItem) {
                    texLoader.bindTexture(texLoader.getTexture("/terrain.png"));
                } else {
                    texLoader.bindTexture(texLoader.getTexture("/gui/items.png"));
                }

                if (icon == null) {
                    icon = texLoader.getMissingIcon(stack.getSpriteNumber());
                }

                GL11.glEnable(3042);
                GL11.glBlendFunc(770, 771);
                Tessellator tess = Tessellator.instance;
                float zLevel = this.zLevel + 1.0F;
                tess.begin();
                tess.setColor(stack.getItem().getColorTint(stack));
                tess.addTexturedVertex(x, y + 16, zLevel, icon.getMinU(), icon.getMaxV());
                tess.addTexturedVertex(x + 16, y + 16, zLevel, icon.getMaxU(), icon.getMaxV());
                tess.addTexturedVertex(x + 16, y, zLevel, icon.getMaxU(), icon.getMinV());
                tess.addTexturedVertex(x, y, zLevel, icon.getMinU(), icon.getMinV());
                tess.draw();
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glDisable(3042);
                GL11.glEnable(2896);
            }

            if (stack != null && stack.hasEffect()) {
                GL11.glDepthFunc(516);
                GL11.glDisable(2896);
                GL11.glDepthMask(false);
                texLoader.bindTexture(texLoader.getTexture("/misc/glint.png"));
                this.zLevel -= 50.0F;
                GL11.glEnable(3042);
                GL11.glBlendFunc(774, 774);
                if (EnchantmentHelper.getLevel(EnchantmentList.sharpness.id, stack) > 0) {
                    GL11.glColor4f(0.8F, 0.25F, 0.25F, 1.0F);
                } else if (EnchantmentHelper.getLevel(EnchantmentList.efficiency.id, stack) > 0) {
                    GL11.glColor4f(0.25F, 0.25F, 0.8F, 1.0F);
                } else if (EnchantmentHelper.getLevel(EnchantmentList.unbreaking.id, stack) > 0) {
                    GL11.glColor4f(0.25F, 0.8F, 0.25F, 1.0F);
                } else if (EnchantmentHelper.getLevel(EnchantmentList.protection.id, stack) > 0) {
                    GL11.glColor4f(0.8F, 0.8F, 0.25F, 1.0F);
                } else {
                    GL11.glColor4f(0.76F, 0.38F, 0.98F, 1.0F);
                }

                this.renderGlint(x * 431278612 + y * 32178161, x - 2, y - 2, 20, 20);
                GL11.glDepthMask(true);
                this.zLevel += 50.0F;
                GL11.glDisable(3042);
                GL11.glEnable(2896);
                GL11.glDepthFunc(515);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }

    private void renderGlint(int size, int x, int y, int width, int height) {
        for (int pass = 0; pass < 2; pass++) {
            if (pass == 0) {
                GL11.glBlendFunc(768, 1);
            }

            if (pass == 1) {
                GL11.glBlendFunc(768, 1);
            }

            float offset = 0.00390625F;
            float timer = (float)(System.currentTimeMillis() % (16000 + pass * 1873)) / (16000.0F + pass * 1873) * 256.0F;
            Tessellator tess = Tessellator.instance;
            float maxHeight = 4.0F;
            if (pass == 1) {
                maxHeight = -1.0F;
            }

            tess.begin();
            tess.addTexturedVertex(x, y + height, this.zLevel, (timer + height * maxHeight) * offset, height * offset);
            tess.addTexturedVertex(x + width, y + height, this.zLevel, (timer + width + height * maxHeight) * offset, height * offset);
            tess.addTexturedVertex(x + width, y, this.zLevel, (timer + width) * offset, 0.0F);
            tess.addTexturedVertex(x, y, this.zLevel, (timer + 0.0F) * offset, 0.0F);
            tess.draw();
        }
    }

    public void renderGUIOverlays(Font font, TextureLoader texLoader, ItemStack stack, int x, int y) {
        if (stack != null) {
            if (stack.stackSize != 1) {
                String stackSize = "" + stack.stackSize;
                int color = 16777215;
                if (stack.stackSize <= 0) {
                    color = 16733525;
                }

                GL11.glDisable(2896);
                GL11.glDisable(2929);
                font.drawStringWithShadow(stackSize, x + 19 - 2 - font.getStringWidth(stackSize), y + 6 + 3, color);
                GL11.glEnable(2896);
                GL11.glEnable(2929);
            }

            if (stack.itemDmg > 0 && !stack.hasSubtypes()) {
                int maxDmg = stack.getMaxDamage();
                if (maxDmg <= 0) {
                    maxDmg = 1;
                }

                int lower = 13 - stack.itemDmg * 13 / maxDmg;
                int upper = 255 - stack.itemDmg * 255 / maxDmg;
                int u = 255 - upper << 16 | upper << 8;
                int v = (255 - upper) / 4 << 16 | 16128;
                Tessellator tess = Tessellator.instance;
                GL11.glDisable(2896);
                GL11.glDisable(2929);
                GL11.glDisable(3553);
                this.renderQuad(tess, x + 2, y + 13, 13, 2, 0);
                this.renderQuad(tess, x + 2, y + 13, 12, 1, v);
                this.renderQuad(tess, x + 2, y + 13, lower, 1, u);
                GL11.glEnable(3553);
                GL11.glEnable(2896);
                GL11.glEnable(2929);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }

    private void renderQuad(Tessellator tess, int start, int end, int x, int y, int color) {
        tess.begin();
        tess.setColor(color);
        tess.addVertex(start, end, 0.0);
        tess.addVertex(start, end + y, 0.0);
        tess.addVertex(start + x, end + y, 0.0);
        tess.addVertex(start + x, end, 0.0);
        tess.draw();
    }
}
