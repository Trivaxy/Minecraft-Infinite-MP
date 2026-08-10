package net.minecraft.client.gui.screens.container.tileentity;

import net.minecraft.client.gui.screens.container.ContainerScreen;
import net.minecraft.game.block.tileentity.other.BeaconEntity;
import net.minecraft.game.container.inventory.PlayerInventory;
import net.minecraft.game.container.tileentities.BeaconContainer;
import net.minecraft.localization.Translator;
import org.lwjgl.opengl.GL11;

public class BeaconScreen extends ContainerScreen {
    public BeaconEntity beacon;

    public BeaconScreen(PlayerInventory playerInv, BeaconEntity beacon) {
        super(new BeaconContainer(playerInv, beacon));
        this.beacon = beacon;
        this.contWidth = 230;
        this.contHeight = 219;
    }

    @Override
    protected void drawForegroundLayer() {
        this.drawCenteredString(this.fontRenderer, Translator.getInstance().translateKey("tile.beacon.primary") + " " + this.beacon.range, 93, 114, 14737632);
        this.drawCenteredString(this.fontRenderer, Translator.getInstance().translateKey("tile.beacon.secondary") + ": ", 144, 114, 14737632);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttons.get(0).y -= 22;
        this.buttons.get(0).x += 26;
    }

    @Override
    protected void drawBackgroundLayer(float deltaTime) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.textureLoader.bindTexture("/gui/utility/beacon.png");
        int x = (this.width - this.contWidth) / 2;
        int y = (this.height - this.contHeight) / 2;
        this.drawGraphic(x, y, 0, 0, this.contWidth, this.contHeight);
    }
}
