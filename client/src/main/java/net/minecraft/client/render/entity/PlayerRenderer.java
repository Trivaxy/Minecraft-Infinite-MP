package net.minecraft.client.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gamemode.SpectatorMode;
import net.minecraft.client.localization.Font;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.BlocksRenderer;
import net.minecraft.client.render.entity.model.bipedal.BipedalModel;
import net.minecraft.client.render.entity.model.bipedal.PlayerModel;
import net.minecraft.client.render.entity.model.misc.ElytraModel;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.block.BlockItem;
import net.minecraft.game.item.block.FlagItem;
import net.minecraft.game.item.enchantment.EnchantmentHelper;
import net.minecraft.game.item.enchantment.EnchantmentList;
import net.minecraft.game.item.misc.Cape;
import net.minecraft.game.item.staff.Staff;
import net.minecraft.game.item.tools.Armor;
import net.minecraft.game.item.tools.ArmorMaterial;
import net.minecraft.game.item.tools.Charm;
import net.minecraft.game.item.tools.FishingRod;
import net.minecraft.game.item.tools.Hoe;
import net.minecraft.game.item.tools.Tool;
import net.minecraft.game.item.weapons.Bow;
import net.minecraft.game.item.weapons.Crossbow;
import net.minecraft.game.item.weapons.Musket;
import net.minecraft.game.item.weapons.Spear;
import net.minecraft.game.item.weapons.Sword;
import net.minecraft.game.item.weapons.TridentItem;
import net.minecraft.game.item.weapons.Whip;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class PlayerRenderer extends MobRenderer<Player> {
    private PlayerModel model = (PlayerModel)this.mainModel;
    private PlayerModel.SlimPlayerModel slimModel = new PlayerModel.SlimPlayerModel(0.0F);
    private BipedalModel armorOverlay = new BipedalModel(1.0F);
    private BipedalModel leggingsOverlay = new BipedalModel(0.5F);
    private BipedalModel accessoriesOverlay = new BipedalModel(0.5F);
    private ElytraModel elytraModel = new ElytraModel();
    private BlocksRenderer blockRenderer = new BlocksRenderer();

    public PlayerRenderer() {
        super(new PlayerModel(0.0F, 0.0F), 0.5F);
    }

    public void render(Player player, double x, double y, double z, float offset, float tick) {
        if (!(Minecraft.getMinecraft().gamemode instanceof SpectatorMode)) {
            ItemStack equippedItem = player.inventory.getCurrentItem();
            if (player.inventory.getOffHandItem() != null && player.inventory.getOffHandItem() == player.itemInUse) {
                equippedItem = player.inventory.getOffHandItem();
            }

            if (equippedItem != null && player.getItemAnimFrames() > 0) {
                Item.Action action = equippedItem.getItemUseAction();
                if (action == Item.Action.block || action == Item.Action.dig || action == Item.Action.joust) {
                    this.armorOverlay.blockingLeft = this.leggingsOverlay.blockingLeft = this.accessoriesOverlay.blockingLeft = this.model.blockingLeft = equippedItem
                        == player.inventory.getOffHandItem();
                    this.armorOverlay.blocking = this.leggingsOverlay.blocking = this.accessoriesOverlay.blocking = this.model.blocking = true;
                } else if (action == Item.Action.bow) {
                    this.armorOverlay.aimedBow = this.leggingsOverlay.aimedBow = this.model.aimedBow = true;
                }
            }

            this.mainModel = player.isSlim ? this.slimModel : this.model;
            this.model = player.isSlim ? this.slimModel : this.model;
            this.armorOverlay.swingMain = this.leggingsOverlay.swingMain = this.accessoriesOverlay.swingMain = this.model.swingMain = this.getSwing(
                player, tick
            );
            this.armorOverlay.swingOff = this.leggingsOverlay.swingOff = this.accessoriesOverlay.swingOff = this.model.swingOff = this.getSwingOffhand(
                player, tick
            );
            this.armorOverlay.sneaking = this.leggingsOverlay.sneaking = this.accessoriesOverlay.sneaking = this.model.sneaking = player.sneaking;
            this.armorOverlay.crawling = this.leggingsOverlay.crawling = this.accessoriesOverlay.crawling = this.model.crawling = player.crawling;
            this.armorOverlay.riding = this.leggingsOverlay.riding = this.accessoriesOverlay.riding = this.model.riding = player.sitting || player.isRiding();
            this.armorOverlay.flying = this.leggingsOverlay.flying = this.accessoriesOverlay.flying = this.model.flying = player.isGliding();
            this.armorOverlay.leftHanded = this.leggingsOverlay.leftHanded = this.accessoriesOverlay.leftHanded = this.model.leftHanded = player.swingingOffhand;
            this.armorOverlay.heldItemLeft = this.leggingsOverlay.heldItemLeft = this.accessoriesOverlay.heldItemLeft = this.model.heldItemLeft = player.inventory
                    .getOffHandItem()
                != null;
            this.armorOverlay.heldItemRight = this.leggingsOverlay.heldItemRight = this.accessoriesOverlay.heldItemRight = this.model.heldItemRight = player.inventory
                    .getCurrentItem()
                != null;
            GL11.glDisable(2884);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            super.render(player, x, y - player.yOffset, z, offset, tick);
            this.armorOverlay.aimedBow = this.leggingsOverlay.aimedBow = this.accessoriesOverlay.aimedBow = this.model.aimedBow = false;
            this.armorOverlay.blocking = this.leggingsOverlay.blocking = this.accessoriesOverlay.blocking = this.model.blocking = false;
            this.armorOverlay.blockingLeft = this.leggingsOverlay.blockingLeft = this.accessoriesOverlay.blockingLeft = this.model.blockingLeft = false;
            GL11.glDisable(3042);
            GL11.glEnable(2884);
        }
    }

    protected boolean renderOverlay(Player player, int type, float tick) {
        ItemStack armorSlot = player.inventory.armorSlots[3 - type];
        if (armorSlot != null) {
            Item item = armorSlot.getItem();
            if (item instanceof Armor) {
                Armor armorPiece = (Armor)item;
                this.loadTexture("/armor/" + Armor.armors[armorPiece.renderIndex] + "_" + (type == 2 ? 2 : 1) + ".png");
                BipedalModel model = type == 2 ? this.leggingsOverlay : this.armorOverlay;
                model.head.render = type == 0;
                model.hair.render = type == 0;
                model.body.render = type == 1 || type == 2;
                model.rightArm.render = type == 1;
                model.leftArm.render = type == 1;
                model.rightLeg.render = type == 2 || type == 3;
                model.leftLeg.render = type == 2 || type == 3;
                this.setOverlay(model);
                if (armorPiece.tier == ArmorMaterial.CLOTH) {
                    int armorColor = armorPiece.getColor(armorSlot);
                    float red = (armorColor >> 16 & 0xFF) / 255.0F;
                    float green = (armorColor >> 8 & 0xFF) / 255.0F;
                    float blue = (armorColor & 0xFF) / 255.0F;
                    GL11.glColor3f(red, green, blue);
                    return true;
                }

                return true;
            }
        }

        return false;
    }

    protected boolean renderGlint(
        Player entity, int type, float limbSwing, float limbYaw, float lookAngle, float yawHead, float yaw, float pitch, float defaultOffset, float tick
    ) {
        ItemStack armorSlot = entity.inventory.armorSlots[3 - type];
        if (armorSlot != null && armorSlot.isItemEnchanted()) {
            float existTime = (float)(System.currentTimeMillis() % 300000L) / 50.0F;
            float zoom = 0.33333334F;
            float colorOffset = 0.76F;
            this.loadTexture("/misc/glint.png");
            GL11.glEnable(3042);
            GL11.glDepthFunc(514);
            GL11.glDepthMask(false);

            for (int pass = 0; pass < 2; pass++) {
                float glintSpd = existTime * (0.001F + pass * 0.003F) * 20.0F;
                GL11.glDisable(2896);
                if (EnchantmentHelper.getLevel(EnchantmentList.protection.id, armorSlot) > 0) {
                    GL11.glColor4f(0.8F * colorOffset, 0.8F * colorOffset, 0.25F * colorOffset, 1.0F);
                } else if (EnchantmentHelper.getLevel(EnchantmentList.unbreaking.id, armorSlot) > 0) {
                    GL11.glColor4f(0.25F * colorOffset, 0.8F * colorOffset, 0.25F * colorOffset, 1.0F);
                } else {
                    GL11.glColor4f(0.76F * colorOffset, 0.38F * colorOffset, 0.98F * colorOffset, 1.0F);
                }

                GL11.glBlendFunc(768, 1);
                GL11.glMatrixMode(5890);
                GL11.glLoadIdentity();
                GL11.glScalef(zoom, zoom, zoom);
                GL11.glRotatef(30.0F - pass * 60.0F, 0.0F, 0.0F, 1.0F);
                GL11.glTranslatef(0.0F, glintSpd, 0.0F);
                GL11.glMatrixMode(5888);
                this.overlayModel.render(entity, limbSwing, limbYaw, lookAngle, yawHead - yaw, pitch, defaultOffset);
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glMatrixMode(5890);
            GL11.glDepthMask(true);
            GL11.glLoadIdentity();
            GL11.glMatrixMode(5888);
            GL11.glEnable(2896);
            GL11.glDisable(3042);
            GL11.glDepthFunc(515);
            return true;
        } else {
            return false;
        }
    }

    protected final void renderEquippedItems(Player player, float tick) {
        this.renderHeldItem(player);
        this.renderOffhand(player);
        this.renderBackpack(player);
        this.renderShield(player);
        this.renderCloak(player, tick);
        this.renderArrows(player, tick);
    }

    private void renderShield(Player player) {
        ItemStack charm = player.inventory.accessorySlots[1];
        if (charm != null) {
            GL11.glPushMatrix();
            this.model.leftArm.applyRotation(0.0625F);
            GL11.glTranslatef(0.25F, 0.3F, 0.25F);
            float scale = 0.5F;
            GL11.glScalef(scale, scale, scale);
            GL11.glRotatef(-75.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            this.renderManager.itemRenderer.renderItemModel(charm);
            GL11.glPopMatrix();
        }
    }

    private void renderBackpack(Player player) {
        ItemStack quiver = player.inventory.accessorySlots[2];
        if (quiver != null && quiver.getItem() instanceof Charm) {
            float scale = 0.5F;
            GL11.glPushMatrix();
            GL11.glTranslatef(-0.3F, 0.5F, 0.175F);
            GL11.glScalef(scale, scale, scale);
            GL11.glRotatef(205.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
            if (player.sneaking) {
                GL11.glTranslatef(0.4F, -0.2F, 0.2F);
                GL11.glRotatef(25.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(15.0F, 0.0F, 1.0F, 0.0F);
            } else if (player.crawling || player.isGliding()) {
                GL11.glTranslatef(0.2F, -1.2F, 1.1F);
                GL11.glRotatef(75.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(-20.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(55.0F, 0.0F, 1.0F, 0.0F);
            }

            this.renderManager.itemRenderer.renderItemModel(quiver);
            GL11.glPopMatrix();
        }
    }

    private void renderCloak(Player player, float tick) {
        ItemStack quiver = player.inventory.accessorySlots[2];
        if (quiver != null && quiver.getItem() instanceof Cape) {
            if (quiver.getItem() == ItemList.elytra) {
                String texture;
                if (player.cloakUrl != null) {
                    texture = player.cloakUrl;
                } else {
                    texture = "/armor/elytra.png";
                }

                this.renderElytra(player, tick, texture, player.cloakUrl != null);
            } else {
                String texture;
                if (quiver.getItem() == ItemList.capeAgility) {
                    texture = "/cape/agilityCape.png";
                } else if (quiver.getItem() == ItemList.capeInvisibility) {
                    texture = "/cape/invisibilityCloak.png";
                } else {
                    texture = "/cape/" + quiver.getItemName(quiver.itemDmg) + ".png";
                }

                this.renderCape(player, tick, texture, false);
            }
        } else if (player.cloakUrl != null) {
            this.renderCape(player, tick, player.cloakUrl, true);
        }
    }

    private void renderCape(Player player, float tick, String texture, boolean online) {
        GL11.glPushMatrix();
        if (online) {
            this.loadSkin(texture, null);
        } else {
            this.loadTexture(texture);
        }

        GL11.glTranslatef(0.0F, 0.0F, 0.125F);
        double capeX = player.capeX + (player.shiftX - player.capeX) * tick - (player.prevX + (player.posX - player.prevX) * tick);
        double capeY = player.capeY + (player.shiftY - player.capeY) * tick - (player.prevY + (player.posY - player.prevY) * tick);
        double capeZ = player.capeZ + (player.shiftZ - player.capeZ) * tick - (player.prevZ + (player.posZ - player.prevZ) * tick);
        float playerYaw = player.prevYawOffset + (player.yawOffset - player.prevYawOffset) * tick;
        double yaw1 = MathHelper.sin(playerYaw * (float) Math.PI / 180.0F);
        double yaw2 = -MathHelper.cos(playerYaw * (float) Math.PI / 180.0F);
        float sway = (float)capeY * 10.0F;
        sway = MathHelper.clamp(sway, -6.0F, 32.0F);
        float angle = (float)(capeX * yaw1 + capeZ * yaw2) * 100.0F;
        float rotation = (float)(capeX * yaw2 - capeZ * yaw1) * 100.0F;
        angle = MathHelper.clamp(angle, 0.0F, 90.0F);
        float facing = player.prevCamYaw + (player.camYaw - player.prevCamYaw) * tick;
        sway += MathHelper.sin((player.prevDistance + (player.distance - player.prevDistance) * tick) * 6.0F) * 32.0F * facing;
        if (player.sneaking) {
            sway += 25.0F;
            GL11.glTranslatef(0.0F, 0.1F, 0.0F);
        } else if (player.crawling || player.isGliding()) {
            sway += 90.0F;
            GL11.glTranslatef(0.0F, 1.2F, 0.0F);
        }

        GL11.glRotatef(6.0F + angle / 2.0F + sway, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(rotation / 2.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(-rotation / 2.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        this.model.cloak.render(0.0625F);
        GL11.glPopMatrix();
    }

    private void renderElytra(Player player, float tick, String texture, boolean online) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(3042);
        GL11.glBlendFunc(1, 0);
        if (online) {
            this.loadSkin(texture, null);
        } else {
            this.loadTexture(texture);
        }

        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.0F, 0.125F);
        float yaw = player.prevYaw + (player.yaw - player.prevYaw) * tick;
        float pitch = player.prevPitch + (player.pitch - player.prevPitch) * tick;
        float entityTicks = this.getDefaultAngle(player, tick);
        float defaultOffset = 0.0625F;
        float limbYaw = player.prevLimbYaw + (player.limbYaw - player.prevLimbYaw) * tick;
        float limbSwing = player.limbSwing - player.limbYaw * (1.0F - tick);
        if (limbYaw > 1.0F) {
            limbYaw = 1.0F;
        }

        if (player.crawling) {
            GL11.glTranslatef(0.0F, 1.2F, 0.0F);
        }

        this.elytraModel.animateWings(limbSwing, limbYaw, entityTicks, yaw - tick, pitch, defaultOffset, player);
        this.elytraModel.render(player, limbSwing, limbYaw, entityTicks, yaw - tick, pitch, defaultOffset);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    private void renderHeldItem(Player player) {
        ItemStack heldItem = player.inventory.getCurrentItem();
        if (heldItem != null) {
            this.mainModel = player.isSlim ? this.slimModel : this.model;
            this.model = player.isSlim ? this.slimModel : this.model;
            GL11.glPushMatrix();
            this.model.rightArm.applyRotation(0.0625F);
            this.model.heldItemRight = true;
            GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);
            this.repositionItem(player, heldItem);
            this.renderManager.itemRenderer.renderItemModel(heldItem);
            GL11.glPopMatrix();
        } else {
            this.model.heldItemRight = false;
        }
    }

    private void renderOffhand(Player player) {
        ItemStack offHand = player.inventory.getOffHandItem();
        if (offHand != null) {
            this.mainModel = player.isSlim ? this.slimModel : this.model;
            this.model = player.isSlim ? this.slimModel : this.model;
            GL11.glPushMatrix();
            this.model.leftArm.applyRotation(0.0625F);
            this.model.heldItemLeft = true;
            GL11.glTranslatef(0.0625F, 0.4375F, 0.0625F);
            this.repositionItem(player, offHand);
            this.renderManager.itemRenderer.renderItemModel(offHand);
            GL11.glPopMatrix();
        } else {
            this.model.heldItemLeft = false;
        }
    }

    private void repositionItem(Player player, ItemStack heldItem) {
        if (heldItem.getItem() instanceof BlockItem && this.blockRenderer.isModel3D(BlockList.blocks[heldItem.itemID].getRenderType())) {
            float scale = 1.0F;
            GL11.glTranslatef(0.0F, 0.15F, -0.25F);
            scale *= 0.35F;
            GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            GL11.glScalef(scale, -scale, scale);
        } else if (!(heldItem.getItem() instanceof BlockItem)) {
            if (heldItem.getItem() instanceof Tool
                || heldItem.getItem() instanceof Sword
                || heldItem.getItem() instanceof Hoe
                || heldItem.getItem() instanceof Whip
                || heldItem.getItem() == ItemList.belialScythe
                || heldItem.getItem() instanceof FlagItem
                || heldItem.getItem() instanceof Spear) {
                float scale = 0.55F;
                GL11.glTranslatef(0.0F, 0.15F, 0.0F);
                GL11.glScalef(scale, -scale, scale);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
                if (player.getItemAnimFrames() > 0 && heldItem.getItemUseAction() == Item.Action.block) {
                    GL11.glTranslatef(0.05F, 0.0F, -0.1F);
                    GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(-60.0F, 0.0F, 0.0F, 1.0F);
                }

                if (player.getItemAnimFrames() > 0 && heldItem.getItemUseAction() == Item.Action.joust) {
                    GL11.glRotatef(-45.0F, 1.0F, 0.0F, 0.0F);
                    GL11.glRotatef(10.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(-45.0F, 0.0F, 0.0F, 1.0F);
                }
            } else if (heldItem.getItem() instanceof Bow) {
                float scale = 0.55F;
                GL11.glTranslatef(0.0F, 0.15F, 0.3F);
                GL11.glScalef(scale, -scale, scale);
                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            } else if (heldItem.getItem() instanceof Crossbow) {
                float scale = 0.55F;
                GL11.glTranslatef(0.35F, 0.15F, -0.25F);
                GL11.glScalef(scale, -scale, scale);
                GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-15.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            } else if (heldItem.getItem() instanceof Musket) {
                float scale = 0.55F;
                GL11.glTranslatef(0.0F, 0.15F, 0.0F);
                GL11.glScalef(scale, -scale, scale);
                GL11.glRotatef(-105.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            } else if (heldItem.getItem() instanceof TridentItem) {
                float scale = 0.55F;
                GL11.glTranslatef(0.0F, 0.4F, -0.4F);
                GL11.glScalef(scale, -scale, scale);
                GL11.glRotatef(-25.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            } else if (!(heldItem.getItem() instanceof FishingRod)
                && heldItem.getItem() != ItemList.stick
                && !(heldItem.getItem() instanceof Staff)
                && heldItem.getItem() != ItemList.stickCarrot) {
                float scale = 0.25F;
                GL11.glTranslatef(0.15F, 0.15F, -0.2F);
                GL11.glScalef(scale, -scale, scale);
                GL11.glRotatef(-75.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(35.0F, 0.0F, 0.0F, 1.0F);
            } else {
                float scale = 0.55F;
                GL11.glTranslatef(0.0F, 0.15F, 0.0F);
                GL11.glScalef(scale, -scale, scale);
                GL11.glRotatef(-50.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(220.0F, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(0.05F, 0.325F, 0.0F);
            }
        } else {
            float scale = 0.35F;
            GL11.glTranslatef(0.0F, 0.25F, -0.25F);
            GL11.glScalef(scale, -scale, scale);
            GL11.glRotatef(-20.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(-70.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(20.0F, 0.0F, 1.0F, 0.0F);
        }
    }

    protected final boolean renderAccessories(Player entityLiving, int type) {
        Player player = entityLiving;
        BipedalModel overlayModel = this.accessoriesOverlay;
        ItemStack accessorySlot = player.inventory.accessorySlots[3];
        this.loadTexture("/armor/accessories.png");
        if (accessorySlot == null && player.inventory.accessorySlots[0] == null) {
            return false;
        }

        overlayModel.rightLeg.render = false;
        overlayModel.leftLeg.render = false;
        overlayModel.rightArm.render = false;
        overlayModel.leftArm.render = false;
        overlayModel.head.render = false;
        overlayModel.hair.render = false;
        overlayModel.body.render = false;
        if (accessorySlot != null && accessorySlot.getItem() == ItemList.spectacles) {
            overlayModel.head.render = true;
            overlayModel.hair.render = true;
        } else if (accessorySlot != null && (accessorySlot.getItem() == ItemList.necklace || accessorySlot.getItem() == ItemList.windAmulet)) {
            overlayModel.body.render = true;
        }

        if (player.inventory.accessorySlots[0] != null && player.inventory.accessorySlots[0].getItem() == ItemList.gloves) {
            overlayModel.rightArm.render = true;
            overlayModel.leftArm.render = true;
        }

        this.setAccessories(overlayModel);
        return true;
    }

    protected void rotate(Player player, float x, float y, float z) {
        if (player.isAlive() && player.isPlayerSleeping()) {
            GL11.glRotatef(player.getBedAngle(), 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(this.maxRotation(player), 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(270.0F, 0.0F, 1.0F, 0.0F);
        } else {
            super.rotate(player, x, y, z);
        }
    }

    protected void transform(Player player, double x, double y, double z) {
        if (player.isAlive() && player.isPlayerSleeping()) {
            super.transform(player, x + player.modelRotX, y + player.modelRotY, z + player.modelRotZ);
        } else {
            super.transform(player, x, y, z);
        }
    }

    protected void renderLabels(Player player, double x, double y, double z) {
        if (Minecraft.isGuiEnabled() && player != this.renderManager.player) {
            float offset = 1.6F;
            float scale = 0.016666668F * offset;
            float distToPlayer = player.getDistanceTo(this.renderManager.player);
            float yOffset = player.sneaking ? 32.0F : 64.0F;
            if (distToPlayer < yOffset) {
                String label = player.username;
                if (player.op) {
                    label = "§d" + label;
                } else if (player.username.equalsIgnoreCase("Yoniko98")) {
                    label = "§a" + label;
                } else if (player.username.equalsIgnoreCase("Notch")) {
                    label = "§e" + label;
                }

                if (!player.sneaking) {
                    if (player.isPlayerSleeping()) {
                        this.renderLabel(player, label, x, y - 1.5, z, 64);
                    } else {
                        this.renderLabel(player, label, x, y + 2.15, z, 64);
                    }
                } else {
                    Font font = this.renderManager.getFont();
                    Tessellator tess = Tessellator.instance;
                    int nameWidth = font.getStringWidth(label) / 2;
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)x + 0.0F, (float)y + 2.3F, (float)z);
                    GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(-this.renderManager.yaw, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(this.renderManager.pitch, 1.0F, 0.0F, 0.0F);
                    GL11.glScalef(-scale, -scale, scale);
                    GL11.glDisable(2896);
                    GL11.glTranslatef(0.0F, 0.25F / scale, 0.0F);
                    GL11.glDepthMask(false);
                    GL11.glEnable(3042);
                    GL11.glBlendFunc(770, 771);
                    GL11.glDisable(3553);
                    tess.begin();
                    tess.setColorAlpha(0.0F, 0.0F, 0.0F, 0.25F);
                    tess.addVertex(-nameWidth - 1, -1.0, 0.0);
                    tess.addVertex(-nameWidth - 1, 8.0, 0.0);
                    tess.addVertex(nameWidth + 1, 8.0, 0.0);
                    tess.addVertex(nameWidth + 1, -1.0, 0.0);
                    tess.draw();
                    GL11.glEnable(3553);
                    GL11.glDepthMask(true);
                    font.drawString(label, -font.getStringWidth(label) / 2, 0, 553648127);
                    GL11.glEnable(2896);
                    GL11.glDisable(3042);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glPopMatrix();
                }
            }
        }
    }

    public void drawFirstPersonHand(Player player) {
        this.mainModel = player.isSlim ? this.slimModel : this.model;
        this.model = player.isSlim ? this.slimModel : this.model;
        this.model.swingMain = 0.0F;
        this.model.crawling = false;
        this.model.sneaking = false;
        this.model.riding = false;
        this.model.flying = false;
        this.model.heldItemRight = false;
        this.model.heldItemLeft = false;
        this.model.animate(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
        GL11.glDisable(2884);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        this.model.rightArm.render(0.0625F);
        this.model.rightArmOverlay.render(0.0625F);
        GL11.glDisable(3042);
        GL11.glEnable(2884);
    }
}
