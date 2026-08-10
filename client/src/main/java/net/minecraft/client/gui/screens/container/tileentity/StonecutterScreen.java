package net.minecraft.client.gui.screens.container.tileentity;

import net.minecraft.client.gui.screens.container.ContainerScreen;
import net.minecraft.game.container.inventory.PlayerInventory;
import net.minecraft.game.container.tileentities.StonecutterContainer;
import net.minecraft.game.world.World;
import net.minecraft.localization.Translator;

public class StonecutterScreen extends ContainerScreen {
    public StonecutterScreen(PlayerInventory playerInv, World world, int x, int y, int z) {
        super(new StonecutterContainer(playerInv, world, x, y, z));
    }

    @Override
    protected void drawForegroundLayer() {
        this.drawCenteredString(this.fontRenderer, Translator.getInstance().translateKey("container.stonecutter"), this.contWidth / 2, 7, 16777215);
        this.drawStringNoShadow(this.fontRenderer, Translator.getInstance().translateKey("container.inventory"), 8, this.contHeight - 91, this.txtColor);
    }

    @Override
    protected void drawBackgroundLayer(float tick) {
        int tex = this.mc.textureLoader.getTexture("/gui/utility/stonecutter.png");
        this.mc.textureLoader.bindTexture(tex);
        int x = (this.width - this.contWidth) / 2;
        int y = (this.height - this.contHeight) / 2;
        this.drawGraphic(x, y, 0, 0, this.contWidth, 205);
    }
}
