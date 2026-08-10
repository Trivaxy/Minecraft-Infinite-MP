package net.minecraft.client.gui.screens.worldselect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiList;
import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.screens.GuiScreen;
import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeItemsList;
import net.minecraft.localization.Translator;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class FlatCustomizationScreen extends GuiScreen {
    protected WorldOptionsScreen options;
    private GuiList slot;

    public FlatCustomizationScreen(WorldOptionsScreen options) {
        this.options = options;
    }

    @Override
    public void initGui() {
        this.buttons.clear();
        this.buttons.add(new Button(200, this.width / 2 - 75, this.height - 40, 150, 20, "gui.done"));
        this.slot = new FlatCustomizationScreen.FlatLayerSlot(this.mc, this.width, this.height, 32, this.height - 58 + 2, 24);
    }

    @Override
    protected void buttonClicked(Button button) {
        if (button.enabled || button.visible) {
            if (button.id == 200) {
                this.mc.setScreen(this.options);
            }
        }
    }

    @Override
    public void scroll(int scrollValue) {
        this.slot.scrollMouse();
    }

    @Override
    public void drawScreen(int x, int y, float tickDelta) {
        this.slot.draw(x, y, tickDelta);
        this.drawCenteredString(this.fontRenderer, Translator.getInstance().translateKey("selectWorld.customize"), this.width / 2, 12, 16777215);
        super.drawScreen(x, y, tickDelta);
    }

    private class FlatLayerSlot extends GuiList {
        private ItemRenderer renderItem = new ItemRenderer();
        private int[] startingIndices = new int[this.listSize()];

        public FlatLayerSlot(Minecraft mc, int width, int height, int top, int bottom, int slot) {
            super(mc, width, height, top, bottom, slot);
            this.startingIndices = new int[this.listSize()];

            for (int i = 0; i < this.startingIndices.length; i++) {
                this.startingIndices[i] = 0;
            }
        }

        @Override
        protected int listSize() {
            return 8;
        }

        @Override
        protected void elementClicked(int index, boolean click) {
            int max = CreativeItemsList.BLOCKS.size();
            if (index >= 4) {
                max = 255;
            }

            if (!Keyboard.isKeyDown(42) && !Keyboard.isKeyDown(54)) {
                this.startingIndices[index]++;
            } else {
                this.startingIndices[index] = this.startingIndices[index] + 16;
            }

            if (this.startingIndices[index] >= max) {
                this.startingIndices[index] = 0;
            }

            if (index < 4) {
                FlatCustomizationScreen.this.options.settings.setLayer(index, CreativeItemsList.BLOCKS.get(this.startingIndices[index]).id);
            } else {
                FlatCustomizationScreen.this.options.settings.setLayer(index, this.startingIndices[index]);
            }

            FlatCustomizationScreen.this.mc.sndManager.playSoundFX("random.click", 1.0F, 0.25F);
        }

        @Override
        public void rightClick(int index, boolean click) {
            int max = CreativeItemsList.BLOCKS.size() - 1;
            if (index >= 4) {
                max = 255;
            }

            if (!Keyboard.isKeyDown(42) && !Keyboard.isKeyDown(54)) {
                this.startingIndices[index]--;
            } else {
                this.startingIndices[index] = this.startingIndices[index] - 16;
            }

            if (this.startingIndices[index] < 0) {
                this.startingIndices[index] = max;
            }

            if (index < 4) {
                FlatCustomizationScreen.this.options.settings.setLayer(index, CreativeItemsList.BLOCKS.get(this.startingIndices[index]).id);
            } else {
                FlatCustomizationScreen.this.options.settings.setLayer(index, this.startingIndices[index]);
            }

            FlatCustomizationScreen.this.mc.sndManager.playSoundFX("random.click", 1.0F, 0.25F);
        }

        @Override
        protected boolean isSelected(int index) {
            return false;
        }

        @Override
        protected int contentSize() {
            return this.listSize() * 24;
        }

        @Override
        public void drawBackground() {
            FlatCustomizationScreen.this.drawBackground();
        }

        @Override
        protected void drawSlot(int id, int x, int y, int height, Tessellator tess) {
            if (id < 4) {
                this.drawItem(this.width / 2 + 84, y, FlatCustomizationScreen.this.options.settings.getLayer(id));
            } else {
                this.drawOffsettedString(
                    FlatCustomizationScreen.this.fontRenderer,
                    Translator.getInstance().translateKey("selectWorld.height") + ": " + FlatCustomizationScreen.this.options.settings.getLayer(id),
                    this.width / 2 + 104,
                    y + 6,
                    16777215
                );
            }

            this.drawString(
                FlatCustomizationScreen.this.fontRenderer,
                Translator.getInstance().translateKey(FlatCustomizationScreen.this.options.settings.getLayerName(id)),
                x + 4,
                y + 6,
                16777215
            );
        }

        private void drawItem(int x, int y, int id) {
            this.drawOffsettedString(
                FlatCustomizationScreen.this.fontRenderer,
                Translator.getInstance().translateNamedKey("tile." + BlockList.blocks[id].name),
                x - 4,
                y + 6,
                16777215
            );
            this.drawSlotBg(x + 1, y + 1, 0, 0);
            GL11.glEnable(32826);
            GL11.glPushMatrix();
            GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
            Lighting.enable();
            GL11.glPopMatrix();
            this.renderItem.zLevel = 50.0F;
            this.renderItem.renderItemSprite(FlatCustomizationScreen.this.fontRenderer, this.mc.textureLoader, new ItemStack(id, 1, 0), x + 2, y + 2);
            this.renderItem.zLevel = 0.0F;
            Lighting.disable();
            GL11.glDisable(32826);
        }

        private void drawSlotBg(int x, int y, int width, int height) {
            int tex = this.mc.textureLoader.getTexture("/gui/slot.png");
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.textureLoader.bindTexture(tex);
            Tessellator tess = Tessellator.instance;
            tess.begin();
            tess.addTexturedVertex(x + 0, y + 18, this.zLevel, (width + 0) * 0.0078125F, (height + 18) * 0.0078125F);
            tess.addTexturedVertex(x + 18, y + 18, this.zLevel, (width + 18) * 0.0078125F, (height + 18) * 0.0078125F);
            tess.addTexturedVertex(x + 18, y + 0, this.zLevel, (width + 18) * 0.0078125F, (height + 0) * 0.0078125F);
            tess.addTexturedVertex(x + 0, y + 0, this.zLevel, (width + 0) * 0.0078125F, (height + 0) * 0.0078125F);
            tess.draw();
        }
    }
}
