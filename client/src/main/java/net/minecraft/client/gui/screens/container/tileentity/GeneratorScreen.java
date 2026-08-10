package net.minecraft.client.gui.screens.container.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.screens.container.ContainerScreen;
import net.minecraft.client.network.player.NetworkPlayer;
import net.minecraft.game.block.tileentity.smelters.GeneratorEntity;
import net.minecraft.game.container.inventory.PlayerInventory;
import net.minecraft.game.container.tileentities.GeneratorContainer;
import net.minecraft.localization.Translator;
import net.minecraft.network.packet.container.GeneratorPacket;
import org.lwjgl.opengl.GL11;

public final class GeneratorScreen extends ContainerScreen {
    private GeneratorEntity containerInventory;

    public GeneratorScreen(PlayerInventory playerInventory, GeneratorEntity furnace) {
        super(new GeneratorContainer(playerInventory, furnace));
        this.containerInventory = furnace;
    }

    @Override
    public void initGui() {
        super.initGui();
        int guiTop = (this.height - this.contHeight) / 2;
        this.buttons.get(0).y = guiTop + this.contHeight - 108;
        this.buttons.add(new GeneratorScreen.ToggleButton(1, this.width / 2 + 13, this.height / 2 - 55, ""));
    }

    @Override
    protected void drawForegroundLayer() {
        this.drawStringNoShadow(this.fontRenderer, Translator.getInstance().translateKey("container.generator"), 64, 6, this.txtColor);
        this.drawStringNoShadow(this.fontRenderer, Translator.getInstance().translateKey("container.inventory"), 8, this.contHeight - 107, this.txtColor);
    }

    @Override
    protected void buttonClicked(Button button) {
        super.buttonClicked(button);
        if (button.id == 1) {
            this.containerInventory.active = !this.containerInventory.active;
            if (this.mc.isMultiplayerWorld() && this.mc.player instanceof NetworkPlayer) {
                ((NetworkPlayer)this.mc.player)
                    .connection
                    .sendPacket(
                        new GeneratorPacket(this.containerInventory.x, this.containerInventory.y, this.containerInventory.z, this.containerInventory.active)
                    );
            }

            this.mc.setScreen(null);
        }
    }

    @Override
    protected final void drawBackgroundLayer(float deltaTime) {
        int tex = this.mc.textureLoader.getTexture("/gui/smelters/generator.png");
        this.mc.textureLoader.bindTexture(tex);
        int x = (this.width - this.contWidth) / 2;
        int y = (this.height - this.contHeight) / 2;
        this.drawGraphic(x, y, 0, 0, this.contWidth, this.contHeight);
        if (this.containerInventory.isBurning()) {
            int burnTime = this.containerInventory.getRemainingFuel(12);
            this.drawGraphic(x + 80, y + 30 - burnTime, 176, 12 - burnTime, 14, burnTime + 2);
        }
    }

    private class ToggleButton extends Button {
        public ToggleButton(int id, int x, int y, String str) {
            super(id, x, y, 16, 16, "", str);
        }

        @Override
        protected void drawBg(Minecraft mc, byte state, boolean hovered) {
            int tex = mc.textureLoader.getTexture("/gui/smelters/generator.png");
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            mc.textureLoader.bindTexture(tex);
            if (GeneratorScreen.this.containerInventory.active) {
                this.drawGraphic(this.x, this.y, 192, 0, 16, 16);
            } else {
                this.drawGraphic(this.x, this.y, 208, 0, 16, 16);
            }
        }
    }
}
