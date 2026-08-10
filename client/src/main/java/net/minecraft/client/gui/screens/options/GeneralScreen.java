package net.minecraft.client.gui.screens.options;

import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.button.LockerButton;
import net.minecraft.client.gui.button.Slider;
import net.minecraft.client.gui.screens.DialogueScreen;
import net.minecraft.client.options.Option;
import net.minecraft.client.options.OptionsList;
import net.minecraft.localization.Translator;

public final class GeneralScreen extends SettingsScreen {
    private LockerButton locker;

    public GeneralScreen(OptionsList options) {
        this.title = Translator.getInstance().translateKey("options.title");
        this.options = options;
    }

    @Override
    public void initGui() {
        this.buttons.clear();
        int i = 8;
        if (this.mc.world == null) {
            i = 7;
        }

        for (int index = 0; index < i; index++) {
            if (OptionsList.options[index].getType() == Option.Type.SLIDER) {
                this.buttons
                    .add(
                        new Slider(
                            index,
                            this.width / 2 - 155 + index % 2 * 160,
                            this.height / 6 - 24 + 48 * (index >> 1),
                            OptionsList.options[index].getOptionDesc(),
                            150,
                            20
                        )
                    );
            } else {
                this.buttons
                    .add(
                        new Button(
                            index,
                            this.width / 2 - 65 + index % 2 * 160,
                            this.height / 6 + 24 + 24 * (index >> 1),
                            60,
                            20,
                            ("" + OptionsList.options[index].getOptionLabel(index)).trim(),
                            ("" + this.options.getOptionDesc(OptionsList.options[index])).trim()
                        )
                    );
            }
        }

        if (this.mc.world != null) {
            this.locker = new LockerButton(205, this.width / 2 + 160, this.height / 2 + 15, "difficulty.lock");
            this.locker.locked = this.mc.world.settings.difficultyLock;
            if (this.mc.world.settings.difficultyLock) {
                this.buttons.get(7).enabled = false;
                this.locker.enabled = false;
            }
        }

        super.initGui();
        this.general.enabled = false;
    }

    @Override
    public void drawScreen(int x, int y, float tick) {
        super.drawScreen(x, y, tick);
        if (this.locker != null) {
            this.locker.drawButton(this.mc, x, y);
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int click) {
        super.mouseClicked(x, y, click);
        if (this.locker != null && this.locker.mousePressed(this.mc, x, y)) {
            this.selectedButton = this.locker;
            this.buttonClicked(this.locker);
        }
    }

    @Override
    public void confirmClicked(boolean check, int id) {
        if (check) {
            this.mc.world.settings.difficultyLock = !this.mc.world.settings.difficultyLock;
            this.locker.locked = this.mc.world.settings.difficultyLock;
        }

        this.mc.setScreen(this);
    }

    @Override
    protected void buttonClicked(Button button) {
        super.buttonClicked(button);
        if (button.enabled && button.id == 205) {
            String confirmationString = Translator.getInstance().translateKey("gui.difficultyLockConfirmation");
            String warning = Translator.getInstance().translateKey("gui.lockDifficulty");
            String yes = "gui.yes";
            String no = "gui.no";
            DialogueScreen choiceScreen = new DialogueScreen(this, confirmationString, warning, yes, no, 0);
            this.mc.setScreen(choiceScreen);
        }
    }
}
