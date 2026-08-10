package net.minecraft.client.gui.screens.options;

import java.io.File;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.button.Button;
import net.minecraft.client.options.OptionsList;
import net.minecraft.client.render.Tessellator;
import net.minecraft.localization.Language;
import net.minecraft.localization.LanguageBase;
import net.minecraft.localization.Translator;

public class LanguagePackScreen extends TexturePackScreen {
    public LanguagePackScreen(OptionsList options) {
        super(options);
        this.title = Translator.getInstance().translateKey("options.languages");
        this.optionButtons = 2;
    }

    @Override
    public void initGui() {
        this.buttons.clear();
        this.buttons.add(this.done = new Button(100, this.width / 2 + 5, this.height - 38, 150, 20, "gui.done"));
        this.buttons.add(this.packs = new Button(105, this.width / 2 - 155, this.height - 38, 150, 20, "options.packFolder", 32, 128));
        this.optionSlot = new LanguagePackScreen.LangPackSlot(this.mc, this.width, this.height, 32, this.height - 58 + 2, 28);
        this.packPath = new File("languagepacks").getAbsolutePath();
    }

    @Override
    protected void refreshList() {
        this.mc.langPackList.updateLanguages();
        this.title = Translator.getInstance().translateKey("options.languages");
    }

    private class LangPackSlot extends SettingsScreen.OptionsSlot {
        public LangPackSlot(Minecraft mc, int width, int height, int top, int bottom, int slot) {
            super(mc, width, height, top, bottom, slot, 4);
        }

        @Override
        protected int listSize() {
            List<LanguageBase> langPackList = LanguagePackScreen.this.mc.langPackList.availableLanguages();
            return langPackList.size();
        }

        @Override
        protected void elementClicked(int id, boolean click) {
            List<LanguageBase> langPackList = LanguagePackScreen.this.mc.langPackList.availableLanguages();
            LanguagePackScreen.this.mc.langPackList.setLanguage(langPackList.get(id));
            Translator.resetLanguage(LanguagePackScreen.this.mc.langPackList.selectedLanguagePack);
            LanguagePackScreen.this.initGui();
            LanguagePackScreen.this.mc.loadingScreen.updateTips();
        }

        @Override
        protected boolean isSelected(int id) {
            List<LanguageBase> langPackList = LanguagePackScreen.this.mc.langPackList.availableLanguages();
            return LanguagePackScreen.this.mc.langPackList.selectedLanguagePack == langPackList.get(id);
        }

        @Override
        protected int contentSize() {
            return this.listSize() * 28;
        }

        @Override
        protected void drawSlot(int id, int x, int y, int offset, Tessellator tess) {
            Language langPack = (Language)LanguagePackScreen.this.mc.langPackList.availableLanguages().get(id);
            tess.begin();
            tess.setColor(16777215);
            tess.addTexturedVertex(x, y + offset, 0.0, 0.0F, 1.0F);
            tess.addTexturedVertex(x, y + offset, 0.0, 1.0F, 1.0F);
            tess.addTexturedVertex(x, y, 0.0, 1.0F, 0.0F);
            tess.addTexturedVertex(x, y, 0.0, 0.0F, 0.0F);
            tess.draw();
            this.drawCenteredString(LanguagePackScreen.this.fontRenderer, langPack.getLanguageName(), LanguagePackScreen.this.width / 2, y + 1, 16777215);
            this.drawCenteredString(LanguagePackScreen.this.fontRenderer, langPack.getPackAuthor(), LanguagePackScreen.this.width / 2, y + 12, 8421504);
        }
    }
}
