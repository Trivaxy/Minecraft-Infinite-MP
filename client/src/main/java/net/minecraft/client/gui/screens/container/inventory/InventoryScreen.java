package net.minecraft.client.gui.screens.container.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.button.RecipeButton;
import net.minecraft.client.gui.screens.container.ContainerScreen;
import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.localization.Translator;
import org.lwjgl.opengl.GL11;

public class InventoryScreen extends ContainerScreen {
    protected float rotX;
    protected float rotY;
    protected Button recipeButton;
    protected String texture = "inventory";

    public InventoryScreen(Player player) {
        super(player.container);
        this.contHeight = 166;
    }

    @Override
    public void initGui() {
        super.initGui();
        int btnX = (this.width - this.contWidth) / 2;
        int btnY = (this.height - this.contHeight) / 2;
        this.buttons.get(0).y = this.height / 2 - 79;
        this.buttons.add(this.recipeButton = new RecipeButton(1, btnX + 150, btnY + 140 - 80, 20, 20, "", "gui.recipes", ItemList.greenBook.id));
    }

    @Override
    protected void drawForegroundLayer() {
        this.drawStringNoShadow(this.fontRenderer, Translator.getInstance().translateKey("container.craft"), 100, 9, this.txtColor);
        this.drawCenteredString(this.fontRenderer, Translator.getInstance().translateKey("gui.score") + ": " + "§e" + this.mc.player.score, 90, -10, 16777215);
        if (this.mc.player.inventory.accessorySlots[2] != null && this.mc.player.inventory.accessorySlots[2].getItem() == ItemList.backpack) {
            this.drawStringNoShadow(this.fontRenderer, Translator.getInstance().translateKey("container.backpack"), 194, 24, this.txtColor);
        }
    }

    @Override
    public void drawScreen(int x, int y, float deltaTicks) {
        super.drawScreen(x, y, deltaTicks);
        this.rotX = x;
        this.rotY = y;
    }

    @Override
    protected void buttonClicked(Button button) {
        super.buttonClicked(button);
        if (button == this.recipeButton) {
            this.mc.player.displayGUIRecipe();
        }
    }

    @Override
    protected void drawBackgroundLayer(float tickDelta) {
        int invTex = this.mc.textureLoader.getTexture("/gui/inventory/" + this.texture + ".png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.textureLoader.bindTexture(invTex);
        int x = (this.width - this.contWidth) / 2;
        int y = (this.height - this.contHeight) / 2;
        if (this.mc.player.inventory.accessorySlots[2] != null && this.mc.player.inventory.accessorySlots[2].getItem() == ItemList.backpack) {
            this.drawGraphic(x, y, 0, 0, 256, this.contHeight);
        } else {
            this.drawGraphic(x, y, 0, 0, this.contWidth, this.contHeight);
        }

        if (this.mc.player.inventory.accessorySlots[2] != null && this.mc.player.inventory.accessorySlots[2].getItem() == ItemList.quiver) {
            this.drawGraphic(x - 20, y + 11, 233, 166, 23, 62);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.displayDebuffEffects();
        drawPlayerOnGui(this.mc, x + 51, y + 75, 30, x + 51 - this.rotX, y + 75 - 50 - this.rotY);
    }

    public static final void drawPlayerOnGui(Minecraft mc, int x, int y, int scale, float mouseX, float mouseY) {
        GL11.glEnable(32826);
        GL11.glEnable(2903);
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 50.0F);
        GL11.glScalef(-scale, scale, scale);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        float yawOffset = mc.player.yawOffset;
        float yaw = mc.player.yaw;
        float pitch = mc.player.pitch;
        GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
        Lighting.enable();
        GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-((float)Math.atan(mouseY / 40.0F)) * 20.0F, 1.0F, 0.0F, 0.0F);
        mc.player.yawOffset = (float)Math.atan(mouseX / 40.0F) * 20.0F;
        mc.player.yaw = (float)Math.atan(mouseX / 40.0F) * 40.0F;
        mc.player.pitch = -((float)Math.atan(mouseY / 40.0F)) * 20.0F;
        mc.player.headFacing = mc.player.yaw;
        GL11.glTranslatef(0.0F, mc.player.yOffset, 0.0F);
        EntityRenderManager.instance.yaw = 180.0F;
        EntityRenderManager.instance.renderEntityAt(mc.player, 0.0, 0.0, 0.0, 0.0F, 1.0F);
        mc.player.yawOffset = yawOffset;
        mc.player.yaw = yaw;
        mc.player.pitch = pitch;
        mc.player.headFacing = mc.player.yaw;
        GL11.glPopMatrix();
        Lighting.disable();
        GL11.glDisable(32826);
    }
}
