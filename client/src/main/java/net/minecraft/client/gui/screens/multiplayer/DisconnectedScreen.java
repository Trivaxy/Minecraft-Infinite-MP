package net.minecraft.client.gui.screens.multiplayer;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.screens.GuiScreen;
import net.minecraft.localization.Translator;

public class DisconnectedScreen extends GuiScreen {
    private String errorMessage;
    private String errorDetail;
    private Object[] errorArguments;
    private List<String> errorDetailSplit;
    private GuiScreen parent;

    public DisconnectedScreen(GuiScreen screen, String msg, String details, Object... lines) {
        Translator translator = Translator.getInstance();
        this.errorMessage = translator.translateKey(msg);
        this.errorDetail = details;
        this.errorArguments = lines;
        this.parent = screen;
    }

    @Override
    public void update() {
    }

    @Override
    protected void keyTyped(char character, int key) {
    }

    @Override
    public void initGui() {
        Translator translator = Translator.getInstance();
        this.buttons.clear();
        this.buttons.add(new Button(0, this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, "gui.toMenu"));
        if (this.errorArguments != null) {
            this.errorDetailSplit = this.fontRenderer.getStringAsList(translator.translateKeyFormat(this.errorDetail, this.errorArguments), this.width - 50);
        } else {
            this.errorDetailSplit = this.fontRenderer.getStringAsList(translator.translateKey(this.errorDetail), this.width - 50);
        }
    }

    @Override
    protected void buttonClicked(Button button) {
        if (button.id == 0) {
            this.mc.setScreen(this.parent);
        }
    }

    @Override
    public void setResolution(Minecraft mc, int wdth, int hght) {
        this.mc = mc;
        this.fontRenderer = mc.fontRenderer;
        this.width = wdth;
        this.height = hght;
        this.buttons.clear();
        this.initGui();
    }

    @Override
    public void drawScreen(int x, int y, float deltaTick) {
        this.drawBackground();
        this.drawCenteredString(this.fontRenderer, this.errorMessage, this.width / 2, this.height / 2 - 50, 16777215);
        int stringYPos = this.height / 2 - 30;
        if (this.errorDetailSplit == null) {
            this.errorDetailSplit = this.fontRenderer.getStringAsList(Translator.getInstance().translateKey(this.errorDetail), this.width - 50);
        }

        for (String line : this.errorDetailSplit) {
            this.drawCenteredString(this.fontRenderer, line, this.width / 2, stringYPos, 16777215);
            stringYPos += 5;
        }

        super.drawScreen(x, y, deltaTick);
    }
}
