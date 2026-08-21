package net.minecraft.client.gui.screens.game;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.screens.GuiScreen;
import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.localization.Translator;
import org.lwjgl.opengl.GL11;

public class ModeSwitcher extends GuiScreen {
    @Override
    public void initGui() {
        super.initGui();
        this.buttons.clear();
        this.buttons.add(new ModeSwitcher.SelectorButton(1, this.width / 2 - 49, this.height / 4 + 50, "menu.creative", BlockList.grass));
        this.buttons.add(new ModeSwitcher.SelectorButton(0, this.width / 2 - 24, this.height / 4 + 50, "menu.survival", ItemList.swordSteel));
        this.buttons.add(new ModeSwitcher.SelectorButton(2, this.width / 2 + 1, this.height / 4 + 50, "menu.adventure", ItemList.map));
        this.buttons.add(new ModeSwitcher.SelectorButton(3, this.width / 2 + 26, this.height / 4 + 50, "menu.spectator", ItemList.enderEye));
    }

    @Override
    protected void buttonClicked(Button button) {
        if (this.mc.world.isOnline) {
            this.mc.player.sendChatMessage("/gamemode " + button.id);
        } else {
            this.mc.setGamemode(button.id);
        }

        this.closeScreen();
    }

    private void closeScreen() {
        this.mc.setScreen(null);
        this.mc.setFocus();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    protected void keyTyped(char c, int key) {
        super.keyTyped(c, key);
        if (key == this.mc.options.changeModeKey.key) {
            this.closeScreen();
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int click) {
        super.mouseClicked(x, y, click);
        if (click == 0 || click == 1) {
            this.closeScreen();
        }
    }

    @Override
    public void drawScreen(int width, int height, float tickDelta) {
        this.drawGradient(this.width / 2 - 65, this.height / 4 + 25, this.width / 2 + 65, this.height / 4 + 85, 1610941696, -1607454656, 1.5F);
        this.drawCenteredString(this.fontRenderer, Translator.getInstance().translateKey("menu.gamemode"), this.width / 2, this.height / 4 + 32, 16777215);
        super.drawScreen(width, height, tickDelta);
    }

    private class SelectorButton extends Button {
        private ItemRenderer itemRenderer = new ItemRenderer();
        private Block block;
        private Item item;

        public SelectorButton(int id, int x, int y, String str, Block block) {
            super(id, x, y, 20, 20, "");
            this.block = block;
            this.description = Translator.getInstance().translateKey(str);
        }

        public SelectorButton(int id, int x, int y, String str, Item item) {
            super(id, x, y, 20, 20, "");
            this.item = item;
            this.description = Translator.getInstance().translateKey(str);
        }

        @Override
        protected void drawBg(Minecraft mc, byte state, boolean hovered) {
            this.drawGraphic(this.x - 1, this.y - 1, 211, 0, 22, 23);
            GL11.glPushMatrix();
            GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
            Lighting.enable();
            GL11.glPopMatrix();
            GL11.glDisable(2896);
            GL11.glEnable(32826);
            GL11.glEnable(2903);
            GL11.glEnable(2896);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            if (this.block != null) {
                this.itemRenderer.renderItemSprite(mc.fontRenderer, mc.textureLoader, new ItemStack(this.block, 1), this.x + 2, this.y + 2);
            } else if (this.item != null) {
                this.itemRenderer.renderItemSprite(mc.fontRenderer, mc.textureLoader, new ItemStack(this.item, 1), this.x + 2, this.y + 2);
            }

            GL11.glDisable(2896);
            GL11.glDepthMask(true);
            GL11.glEnable(2929);
        }
    }
}
