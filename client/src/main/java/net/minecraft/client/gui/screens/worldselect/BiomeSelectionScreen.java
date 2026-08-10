package net.minecraft.client.gui.screens.worldselect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiList;
import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.screens.GuiScreen;
import net.minecraft.client.render.Tessellator;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.localization.Translator;

public class BiomeSelectionScreen extends GuiScreen {
    protected WorldOptionsScreen options;
    private GuiList slot;

    public BiomeSelectionScreen(WorldOptionsScreen options) {
        this.options = options;
    }

    @Override
    public void initGui() {
        this.buttons.clear();
        this.buttons.add(new Button(200, this.width / 2 - 75, this.height - 40, 150, 20, "gui.done"));
        this.slot = new BiomeSelectionScreen.BiomeSlot(this.mc, this.width, this.height, 32, this.height - 58 + 2, 16);
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
        this.drawCenteredString(this.fontRenderer, Translator.getInstance().translateKey("options.selectBiome"), this.width / 2, 12, 16777215);
        super.drawScreen(x, y, tickDelta);
    }

    private class BiomeSlot extends GuiList {
        public BiomeSlot(Minecraft mc, int width, int height, int top, int bottom, int slot) {
            super(mc, width, height, top, bottom, slot);
        }

        @Override
        protected int listSize() {
            return BiomeList.LAST_VALID_INDEX;
        }

        @Override
        protected void elementClicked(int index, boolean click) {
            BiomeSelectionScreen.this.options.settings.singleBiomeId = Biome.findById(index);
        }

        @Override
        protected boolean isSelected(int index) {
            return BiomeSelectionScreen.this.options.settings.singleBiomeId == Biome.findById(index);
        }

        @Override
        protected int contentSize() {
            return this.listSize() * 16;
        }

        @Override
        public void drawBackground() {
            BiomeSelectionScreen.this.drawBackground();
        }

        @Override
        protected void drawSlot(int id, int x, int y, int height, Tessellator tess) {
            this.drawCenteredString(
                BiomeSelectionScreen.this.fontRenderer, BiomeList.biomes[id].getName(), BiomeSelectionScreen.this.width / 2, y + 2, 16777215
            );
        }
    }
}
