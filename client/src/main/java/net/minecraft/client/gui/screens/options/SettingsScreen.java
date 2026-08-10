package net.minecraft.client.gui.screens.options;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiList;
import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.button.Slider;
import net.minecraft.client.gui.screens.GuiScreen;
import net.minecraft.client.gui.screens.game.PauseScreen;
import net.minecraft.client.gui.screens.title.TitleScreen;
import net.minecraft.client.options.OptionsList;
import net.minecraft.client.render.Tessellator;
import net.minecraft.localization.Translator;
import org.lwjgl.input.Mouse;

public class SettingsScreen extends GuiScreen {
    protected Button done;
    protected Button general;
    protected Button graphics;
    protected Button audio;
    protected Button controls;
    protected Button packs;
    protected OptionsList options;
    protected SettingsScreen.OptionsSlot optionSlot;
    protected String title = Translator.getInstance().translateKey("options.preferences");
    protected int optionButtons = 6;

    @Override
    public void initGui() {
        this.buttons.add(this.done = new Button(100, this.width / 2 - 75, this.height - 25, 150, 20, "gui.done"));
        this.buttons.add(this.general = new Button(101, this.width / 2 - 210, this.height - 50, 80, 20, "options.title", 48, 128));
        this.buttons.add(this.graphics = new Button(102, this.width / 2 - 125, this.height - 50, 80, 20, "options.video", 160, 128));
        this.buttons.add(this.audio = new Button(103, this.width / 2 - 40, this.height - 50, 80, 20, "options.audio", 176, 128));
        this.buttons.add(this.controls = new Button(104, this.width / 2 + 45, this.height - 50, 80, 20, "options.controls", 192, 128));
        this.buttons.add(this.packs = new Button(105, this.width / 2 + 130, this.height - 50, 80, 20, "options.texturepacks", 208, 128));
        this.optionSlot = new SettingsScreen.OptionsSlot(this.mc, this.width, this.height, 32, this.height - 58 + 2, 25, 4);
    }

    @Override
    protected void buttonClicked(Button button) {
        if (button.enabled && button.visible) {
            if (button.id < 100) {
                this.options.setOptionValue(OptionsList.options[button.id], 1, 0.0F);
                button.label = Translator.getInstance().translateKey(OptionsList.options[button.id].getOptionLabel(button.id).trim());
            }

            if (button == this.done) {
                if (this.mc.world != null) {
                    this.mc.setScreen(new PauseScreen());
                } else {
                    this.mc.setScreen(new TitleScreen());
                }

                this.options.saveOptions();
            }

            if (button == this.general) {
                this.mc.setScreen(new GeneralScreen(this.options));
            }

            if (button == this.graphics) {
                this.mc.setScreen(new GraphicsScreen(this.options));
            }

            if (button == this.audio) {
                this.mc.setScreen(new AudioScreen(this.options));
            }

            if (button == this.controls) {
                this.mc.setScreen(new ControlsScreen(this.options));
            }

            if (button == this.packs) {
                this.mc.setScreen(new TexturePackScreen(this.options));
            }
        }
    }

    @Override
    public void drawScreen(int x, int y, float tickDelta) {
        this.optionSlot.draw(x, y, tickDelta);

        for (int index = this.buttons.size() - this.optionButtons; index < this.buttons.size(); index++) {
            this.buttons.get(index).drawButton(this.mc, x, y);
        }

        this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 12, 16777215);
    }

    @Override
    public void onClose() {
        super.onClose();
        this.options.saveOptions();
    }

    @Override
    public void scroll(int scrollValue) {
        this.optionSlot.scrollMouse();
    }

    public String getOptionLabel(Button button) {
        return button instanceof Slider ? " " : this.options.getOptionName(OptionsList.options[button.id]).trim() + ": ";
    }

    public class OptionsSlot extends GuiList {
        public OptionsSlot(Minecraft mc, int width, int height, int top, int bottom, int slot, int offset) {
            super(mc, width, height, top, bottom, slot);
            this.slotOffset = offset;
        }

        @Override
        protected int listSize() {
            return (SettingsScreen.this.buttons.size() - SettingsScreen.this.optionButtons + 1) / 2;
        }

        @Override
        protected void elementClicked(int var1, boolean var2) {
        }

        @Override
        protected boolean isSelected(int var1) {
            return false;
        }

        @Override
        protected int contentSize() {
            return this.listSize() * 25;
        }

        @Override
        protected void drawBackground() {
            SettingsScreen.this.drawBackground();
        }

        @Override
        protected void drawSlot(int id, int x, int y, int offset, Tessellator tess) {
            int scaledWidth = this.mc.rescaler.getScaledWidth();
            int scaledHeight = this.mc.rescaler.getScaledHeight();
            int mouseX = Mouse.getX() * scaledWidth / this.mc.displayWidth;
            int mouseY = scaledHeight - Mouse.getY() * scaledHeight / this.mc.displayHeight - 1;
            int buttonId1 = id * 2;
            int buttonId2 = buttonId1 + 1;
            if (buttonId1 < SettingsScreen.this.buttons.size() - SettingsScreen.this.optionButtons) {
                this.drawButton(SettingsScreen.this.buttons.get(buttonId1), mouseX, mouseY, y);
            }

            if (buttonId2 < SettingsScreen.this.buttons.size() - SettingsScreen.this.optionButtons) {
                this.drawButton(SettingsScreen.this.buttons.get(buttonId2), mouseX, mouseY, y);
            }
        }

        protected void drawButton(Button button, int mouseX, int mouseY, int y) {
            button.y = y;
            if (this.contentSize() - (this.bottom - this.top - 4) > 0) {
                button.enabled = true;
                if (button.y < this.top - 20 || button.y > this.bottom - 14) {
                    button.enabled = false;
                }
            }

            button.drawButton(this.mc, mouseX, mouseY);
            if (button.visible) {
                this.drawOffsettedString(
                    this.mc.fontRenderer, SettingsScreen.this.getOptionLabel(button), button.x, button.y + (button.height - 8) / 2, 14737632
                );
            }
        }
    }
}
