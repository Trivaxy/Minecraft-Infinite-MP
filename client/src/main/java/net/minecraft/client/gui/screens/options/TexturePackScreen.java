package net.minecraft.client.gui.screens.options;

import java.io.File;
import java.util.List;
import net.minecraft.client.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.button.Button;
import net.minecraft.client.options.OptionsList;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.texture.pack.PackBase;
import net.minecraft.client.render.texture.pack.TexturePack;
import net.minecraft.localization.Translator;
import org.lwjgl.opengl.GL11;

public class TexturePackScreen extends SettingsScreen {
    protected String packPath;
    private int selectedPack = -1;

    public TexturePackScreen(OptionsList options) {
        this.title = Translator.getInstance().translateKey("options.textures");
        this.options = options;
        this.optionButtons = 6;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.refreshList();
        this.packPath = new File("texturepacks").getAbsolutePath();
        this.packs.label = Translator.getInstance().translateKey("options.packFolder");
        this.packs.icnWidth = 32;
        this.packs.icnHeight = 128;
        this.optionSlot = new TexturePackScreen.TexPackSlot(this.mc, this.width, this.height, 32, this.height - 58 + 2, 36);
    }

    @Override
    protected void buttonClicked(Button button) {
        if (button == this.packs) {
            File languagePacks = new File(this.packPath);
            ClientUtils.openFile(languagePacks);
        } else {
            super.buttonClicked(button);
        }
    }

    @Override
    public void drawScreen(int x, int y, float tickDelta) {
        if (this.selectedPack <= 0) {
            this.refreshList();
            this.selectedPack += 20;
        }

        super.drawScreen(x, y, tickDelta);
    }

    @Override
    public void update() {
        super.update();
        this.selectedPack--;
    }

    protected void refreshList() {
        this.mc.texturePackList.updateAvaliableTexturePacks();
    }

    private class TexPackSlot extends SettingsScreen.OptionsSlot {
        public TexPackSlot(Minecraft mc, int width, int height, int top, int bottom, int slot) {
            super(mc, width, height, top, bottom, slot, 4);
        }

        @Override
        protected int listSize() {
            List<PackBase> texPackList = TexturePackScreen.this.mc.texturePackList.availablePacks();
            return texPackList.size();
        }

        @Override
        protected void elementClicked(int id, boolean click) {
            List<PackBase> texPackList = TexturePackScreen.this.mc.texturePackList.availablePacks();
            TexturePackScreen.this.mc.texturePackList.setTexturePack((TexturePack)texPackList.get(id));
            TexturePackScreen.this.mc.textureLoader.texturePack = (TexturePack)texPackList.get(id);
            TexturePackScreen.this.mc.textureLoader.refreshTextures();
            TexturePackScreen.this.mc.textureLoader.refreshTextureMaps();
            if (TexturePackScreen.this.mc.world != null) {
                TexturePackScreen.this.mc.worldRenderer.reloadChunks();
            }
        }

        @Override
        protected boolean isSelected(int id) {
            List<PackBase> texPackList = TexturePackScreen.this.mc.texturePackList.availablePacks();
            return TexturePackScreen.this.mc.texturePackList.selectedPack == texPackList.get(id);
        }

        @Override
        protected int contentSize() {
            return this.listSize() * 36;
        }

        @Override
        protected void drawSlot(int id, int x, int y, int offset, Tessellator tess) {
            TexturePack texPack = (TexturePack)TexturePackScreen.this.mc.texturePackList.availablePacks().get(id);
            texPack.bindThumbnail(TexturePackScreen.this.mc.textureLoader);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            tess.begin();
            tess.setColor(16777215);
            tess.addTexturedVertex(x, y + offset, 0.0, 0.0F, 1.0F);
            tess.addTexturedVertex(x + 32, y + offset, 0.0, 1.0F, 1.0F);
            tess.addTexturedVertex(x + 32, y, 0.0, 1.0F, 0.0F);
            tess.addTexturedVertex(x, y, 0.0, 0.0F, 0.0F);
            tess.draw();
            TexturePackScreen.this.drawString(TexturePackScreen.this.fontRenderer, texPack.getFileName(), x + 32 + 2, y + 1, 16777215);
            TexturePackScreen.this.drawString(TexturePackScreen.this.fontRenderer, texPack.firstDescLine(), x + 32 + 2, y + 12, 8421504);
            TexturePackScreen.this.drawString(TexturePackScreen.this.fontRenderer, texPack.secondDescLine(), x + 32 + 2, y + 12 + 10, 8421504);
        }
    }
}
