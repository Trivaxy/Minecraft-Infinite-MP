package net.minecraft.client.gui.screens.container.tileentity;

import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.button.TextField;
import net.minecraft.client.gui.screens.GuiScreen;
import net.minecraft.game.block.tileentity.other.CommandBlockEntity;
import net.minecraft.localization.Translator;
import org.lwjgl.input.Keyboard;

public class CommandScreen extends GuiScreen {
    private CommandBlockEntity commandBlock;
    private TextField inputField;

    public CommandScreen(CommandBlockEntity cmd) {
        this.commandBlock = cmd;
    }

    @Override
    public void update() {
        this.inputField.updateCursor();
    }

    @Override
    public void initGui() {
        Translator stringTranslate1 = Translator.getInstance();
        Keyboard.enableRepeatEvents(true);
        this.buttons.clear();
        this.buttons.add(new Button(0, this.width / 2 - 100, this.height / 4 + 96 + 12, 200, 20, stringTranslate1.translateKey("gui.commandSet")));
        this.buttons.add(new Button(1, this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, stringTranslate1.translateKey("gui.cancel")));
        this.buttons.add(new Button(2, this.width / 2 - 100, this.height / 4 + 96 - 12, 200, 20, stringTranslate1.translateKey("gui.commandSuppress")));
        this.inputField = new TextField(this.fontRenderer, this.width / 2 - 100, this.height / 4 + 96 - 64, 200, 20);
        this.inputField.setMaxStringLength(64);
        this.inputField.setFocused(true);
        this.inputField.setText(this.commandBlock.command);
        this.buttons.get(0).enabled = !this.inputField.getText().trim().isEmpty();
    }

    @Override
    public void onClose() {
        super.onClose();
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void buttonClicked(Button button) {
        if (button.enabled) {
            if (button.id == 0) {
                this.commandBlock.command = this.inputField.getText();
                this.commandBlock.onChanged();
                this.mc.setScreen(null);
            } else if (button.id == 1) {
                this.mc.setScreen(null);
            } else if (button.id == 2) {
                this.commandBlock.suppressOutput = !this.commandBlock.suppressOutput;
                this.commandBlock.onChanged();
            }
        }
    }

    @Override
    protected void keyTyped(char character, int key) {
        this.inputField.textboxKeyTyped(character, key);
        this.buttons.get(0).enabled = !this.inputField.getText().trim().isEmpty();
        if (key == 1) {
            this.buttonClicked(this.buttons.get(1));
        } else if (key == 28) {
            this.buttonClicked(this.buttons.get(0));
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int click) {
        super.mouseClicked(x, y, click);
        this.inputField.mouseClicked(x, y, click);
    }

    @Override
    public void drawScreen(int x, int y, float deltaTick) {
        Translator translator = Translator.getInstance();
        this.drawBackground();
        this.drawString(this.fontRenderer, translator.translateKey("gui.command"), this.width / 2 - 100, this.height / 4 + 96 - 86, 10526880);
        this.buttons.get(2).label = translator.translateKey("gui.commandSuppress")
            + ": "
            + (this.commandBlock.suppressOutput ? translator.translateKey("options.on") : translator.translateKey("options.off"));
        this.inputField.drawTextBox();
        super.drawScreen(x, y, deltaTick);
    }
}
