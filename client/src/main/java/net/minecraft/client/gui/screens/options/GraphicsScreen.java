package net.minecraft.client.gui.screens.options;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenRescaler;
import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.button.Slider;
import net.minecraft.client.options.Option;
import net.minecraft.client.options.OptionValues;
import net.minecraft.client.options.OptionsList;
import net.minecraft.localization.Translator;

public final class GraphicsScreen extends SettingsScreen {
    public GraphicsScreen(OptionsList options) {
        this.title = Translator.getInstance().translateKey("options.video");
        this.options = options;
    }

    @Override
    public void initGui() {
        this.buttons.clear();

        for (int index = 0; index < 45; index++) {
            if (OptionsList.options[index].getCategory() == Option.Category.GRAPHICS) {
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
                                this.height / 6 - 24 + 48 * (index >> 1),
                                60,
                                20,
                                ("" + OptionsList.options[index].getOptionLabel(index)).trim(),
                                "" + this.options.getOptionDesc(OptionsList.options[index]).trim()
                            )
                        );
                }
            }
        }

        super.initGui();
        this.graphics.enabled = false;
    }

    @Override
    protected void buttonClicked(Button button) {
        int scale = OptionValues.guiScale;
        boolean fullscr = OptionValues.fullScreen;
        super.buttonClicked(button);
        if (OptionValues.guiScale != scale) {
            this.mc.rescaler = new ScreenRescaler(this.mc.displayWidth, this.mc.displayHeight);
            int scaledWidth = this.mc.rescaler.getScaledWidth();
            int scaledHeight = this.mc.rescaler.getScaledHeight();
            this.setResolution(this.mc, scaledWidth, scaledHeight);
        }

        if (OptionValues.fullScreen != fullscr) {
            Minecraft.getMinecraft().toggleFullscreen();
        }
    }
}
