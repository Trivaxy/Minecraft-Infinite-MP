package net.minecraft.client.gui.screens.title;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiList;
import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.screens.GuiScreen;
import net.minecraft.client.render.Tessellator;
import net.minecraft.localization.Translator;

public class ChangelogScreen extends GuiScreen {
    private GuiList changelog;
    private GuiScreen parent;
    private ArrayList<String> changelogLines = new ArrayList<>();

    public ChangelogScreen(GuiScreen parent) {
        this.parent = parent;

        try {
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream("/title/changelog.txt"), Charset.forName("UTF-8"))
            );
            String line = "";

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.startsWith("##")) {
                    line = line.replaceAll("%%", "§");
                    this.changelogLines.add(line);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void initGui() {
        this.buttons.clear();
        this.buttons.add(new Button(0, this.width / 2 - 75, this.height - 40, 150, 20, "gui.exit"));
        this.changelog = new ChangelogScreen.ChangelogSlot(this.mc, this.width, this.height, 32, this.height - 58 + 2, 20, this);
    }

    @Override
    protected void buttonClicked(Button button) {
        if (button.id == 0) {
            this.mc.setScreen(this.parent);
        }
    }

    @Override
    public void drawScreen(int x, int y, float tickDelta) {
        this.changelog.draw(x, y, tickDelta);
        this.drawCenteredString(
            this.fontRenderer, "Minecraft Infinite v1.0 " + Translator.getInstance().translateKey("menu.changelog"), this.width / 2, 12, 16777215
        );
        super.drawScreen(x, y, tickDelta);
    }

    @Override
    public void scroll(int scrollValue) {
        this.changelog.scrollMouse();
    }

    private static class ChangelogSlot extends GuiList {
        private ChangelogScreen parent;

        public ChangelogSlot(Minecraft mc, int width, int height, int top, int bottom, int slot, ChangelogScreen screen) {
            super(mc, width, height, top, bottom, slot);
            this.parent = screen;
        }

        @Override
        protected int listSize() {
            return this.parent.changelogLines.size();
        }

        @Override
        protected void elementClicked(int click, boolean state) {
        }

        @Override
        protected boolean isSelected(int state) {
            return false;
        }

        @Override
        protected int contentSize() {
            return this.listSize() * this.slotHeight;
        }

        @Override
        protected void drawSlot(int id, int x, int y, int offset, Tessellator tess) {
            this.drawSplitString(this.parent.fontRenderer, this.parent.changelogLines.get(id), 24, y + 2, this.parent.width - 32, 16777215, false, 0);
        }

        @Override
        protected void drawBackground() {
            this.parent.drawBackground();
        }
    }
}
