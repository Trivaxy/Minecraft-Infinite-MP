package net.minecraft.client.gui.screens.options;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.button.Button;
import net.minecraft.client.options.KeyBind;
import net.minecraft.client.options.OptionsList;
import net.minecraft.client.render.Tessellator;
import net.minecraft.localization.Translator;
import org.lwjgl.input.Mouse;

public final class ControlsScreen extends SettingsScreen {
    private int selectedKeybind = -1;
    private List<Object> displayList = new ArrayList<>();
    private List<Button> resetButtons = new ArrayList<>();

    public ControlsScreen(OptionsList options) {
        this.title = Translator.getInstance().translateKey("options.controls");
        this.options = options;
    }

    @Override
    public void initGui() {
        KeyBind lastBind = null;
        this.buttons.clear();
        this.displayList.clear();
        this.resetButtons.clear();

        for (int index = 0; index < OptionsList.allKeys.length; index++) {
            if (lastBind == null) {
                this.displayList.add(OptionsList.allKeys[0].category.getName());
            } else if (lastBind.category != OptionsList.allKeys[index].category) {
                this.displayList.add(OptionsList.allKeys[index].category.getName());
            }

            lastBind = OptionsList.allKeys[index];
            Button btn = new Button(index, 0, 0, 60, 20, this.options.getKeyBindKey(index));
            this.buttons.add(btn);
            this.displayList.add(btn);
            this.resetButtons.add(new Button(200 + index, 0, 0, 60, 20, "gui.reset"));
        }

        super.initGui();
        this.controls.enabled = false;
        this.optionSlot = new ControlsScreen.ControlsSlot(this.mc, this.width, this.height, 32, this.height - 58 + 2, 25, 4);
    }

    @Override
    protected void buttonClicked(Button button) {
        for (int index = 0; index < OptionsList.allKeys.length; index++) {
            this.buttons.get(index).label = this.options.getKeyBindKey(index);
        }

        if (button.id >= 200) {
            this.options.setKeyBind(button.id - 200, OptionsList.allKeys[button.id - 200].defaultKey);
            this.buttons.get(button.id - 200).label = this.options.getKeyBindKey(button.id - 200);
        }

        if (button.enabled && button.visible && button.id < OptionsList.allKeys.length) {
            this.selectedKeybind = button.id;
            button.label = "> " + this.options.getKeyBindKey(button.id) + " <";
        } else {
            super.buttonClicked(button);
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int click) {
        if (this.selectedKeybind >= 0) {
            this.options.setKeyBind(this.selectedKeybind, -100 + click);
            this.buttons.get(this.selectedKeybind).label = this.options.getKeyBindKey(this.selectedKeybind);
            this.selectedKeybind = -1;
            KeyBind.refreshBinds();
        } else {
            super.mouseClicked(x, y, click);
        }

        for (int i = 0; i < this.resetButtons.size(); i++) {
            if (this.resetButtons.get(i).mousePressed(this.mc, x, y)) {
                this.buttonClicked(this.resetButtons.get(i));
            }
        }
    }

    @Override
    protected void keyTyped(char character, int key) {
        if (this.selectedKeybind >= 0) {
            this.options.setKeyBind(this.selectedKeybind, key);
            this.buttons.get(this.selectedKeybind).label = this.options.getKeyBindKey(this.selectedKeybind);
            this.selectedKeybind = -1;
            KeyBind.refreshBinds();
        } else {
            super.keyTyped(character, key);
        }
    }

    @Override
    public String getOptionLabel(Button button) {
        return this.options.getKeyBindName(button.id);
    }

    private void refreshResetButtons() {
        for (int i = 0; i < this.resetButtons.size(); i++) {
            Button reset = this.resetButtons.get(i);
            if (OptionsList.allKeys[i].key != OptionsList.allKeys[i].defaultKey && reset.y >= 12 && reset.y <= this.height - 68) {
                reset.enabled = true;
            } else {
                reset.enabled = false;
            }
        }
    }

    public class ControlsSlot extends SettingsScreen.OptionsSlot {
        public ControlsSlot(Minecraft mc, int width, int height, int top, int bottom, int slot, int offset) {
            super(mc, width, height, top, bottom, slot, offset);
        }

        @Override
        protected int listSize() {
            return ControlsScreen.this.displayList.size();
        }

        @Override
        protected void drawSlot(int id, int x, int y, int offset, Tessellator tess) {
            int scaledWidth = this.mc.rescaler.getScaledWidth();
            int scaledHeight = this.mc.rescaler.getScaledHeight();
            int mouseX = Mouse.getX() * scaledWidth / this.mc.displayWidth;
            int mouseY = scaledHeight - Mouse.getY() * scaledHeight / this.mc.displayHeight - 1;
            if (ControlsScreen.this.displayList.get(id) instanceof Button) {
                Button buton = (Button)ControlsScreen.this.displayList.get(id);
                this.drawButton(buton, mouseX, mouseY, y);
                this.drawButton(ControlsScreen.this.resetButtons.get(buton.id), mouseX, mouseY, y);
            } else if (ControlsScreen.this.displayList.get(id) instanceof String) {
                this.drawCenteredString(this.mc.fontRenderer, (String)ControlsScreen.this.displayList.get(id), this.width / 2, y + 5, 16777045);
            }
        }

        @Override
        protected void drawButton(Button button, int mouseX, int mouseY, int y) {
            button.x = this.width / 2;
            button.y = y;
            if (button.id >= 200) {
                button.x += 65;
            }

            if (button.id < 200) {
                button.enabled = true;
                if (button.y < this.top - 20 || button.y > this.bottom - 14) {
                    button.enabled = false;
                }
            } else {
                ControlsScreen.this.refreshResetButtons();
            }

            button.drawButton(this.mc, mouseX, mouseY);
            if (button.visible && button.id < ControlsScreen.this.displayList.size()) {
                this.drawString(this.mc.fontRenderer, ControlsScreen.this.getOptionLabel(button), button.x - 125, button.y + (button.height - 8) / 2, 14737632);
            }
        }
    }
}
