package net.minecraft.client.gui.screens.options;

import net.minecraft.client.gui.button.Slider;
import net.minecraft.client.options.Option;
import net.minecraft.client.options.OptionsList;
import net.minecraft.localization.Translator;

public final class AudioScreen extends SettingsScreen {
    public AudioScreen(OptionsList options) {
        this.title = Translator.getInstance().translateKey("options.audio");
        this.options = options;
    }

    @Override
    public void initGui() {
        this.buttons.clear();

        for (int index = 0; index < 45; index++) {
            if (OptionsList.options[index].getCategory() == Option.Category.AUDIO) {
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
            }
        }

        this.buttons.add(new Slider(45, this.width / 2 - 100, this.height / 4 - 8, "desc.master", 200, 20));
        super.initGui();
        this.audio.enabled = false;
    }
}
