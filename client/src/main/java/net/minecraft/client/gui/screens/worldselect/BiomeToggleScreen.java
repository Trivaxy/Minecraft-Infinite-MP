package net.minecraft.client.gui.screens.worldselect;

import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.screens.options.SettingsScreen;
import net.minecraft.localization.Translator;

public class BiomeToggleScreen extends SettingsScreen {
    private WorldOptionsScreen parent;

    public BiomeToggleScreen(WorldOptionsScreen options) {
        this.parent = options;
    }

    @Override
    public void initGui() {
        this.title = Translator.getInstance().translateKey("options.selectBiome");
        this.refreshButtons();
        this.optionSlot = new SettingsScreen.OptionsSlot(this.mc, this.width, this.height, 32, this.height - 58 + 2, 25, 4);
    }

    private void refreshButtons() {
        this.buttons.clear();
        int upper = 21;
        int lower = 41;

        for (int ass = upper; ass <= lower; ass++) {
            this.buttons
                .add(
                    new Button(
                        ass,
                        this.width / 2 - 65 + ass % 2 * 125,
                        this.height / 6 - 24 + 64 * (ass >> 1),
                        60,
                        20,
                        this.parent.settings.getBiomeState(ass).trim()
                    )
                );
        }

        this.buttons.add(new Button(200, this.width / 2 - 75, this.height - 40, 150, 20, "gui.done"));
    }

    @Override
    protected void buttonClicked(Button button) {
        if (button.enabled) {
            if (button.id < 100) {
                this.parent.settings.toggleBiome(button.id);
                button.label = Translator.getInstance().translateKey(this.parent.settings.getBiomeState(button.id)).trim();
            }

            if (button.id == 200) {
                this.mc.setScreen(this.parent);
            }
        }
    }

    @Override
    public void onClose() {
    }

    @Override
    public String getOptionLabel(Button button) {
        String spacer = ": ";
        return Translator.getInstance().translateKey(this.parent.settings.getBiome(button.id)).trim() + spacer;
    }
}
