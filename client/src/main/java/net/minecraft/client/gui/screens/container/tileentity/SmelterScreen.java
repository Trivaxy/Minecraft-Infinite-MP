package net.minecraft.client.gui.screens.container.tileentity;

import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.button.InvisibleButton;
import net.minecraft.client.gui.screens.container.ContainerScreen;
import net.minecraft.game.block.tileentity.smelters.FurnaceEntity;
import net.minecraft.game.block.tileentity.smelters.HellforgeEntity;
import net.minecraft.game.block.tileentity.smelters.SmokerEntity;
import net.minecraft.game.container.inventory.PlayerInventory;
import net.minecraft.game.container.tileentities.FurnaceContainer;
import net.minecraft.game.container.tileentities.HellforgeContainer;
import net.minecraft.game.container.tileentities.SmokerContainer;
import net.minecraft.localization.Translator;

public class SmelterScreen extends ContainerScreen {
    private FurnaceEntity furnaceInventory;
    private String texName = "furnace";
    private int yOffset = 54;
    private int xOffset = 49;

    public SmelterScreen(PlayerInventory playerInv, FurnaceEntity furnaceInv) {
        super(new FurnaceContainer(playerInv, furnaceInv));
        this.furnaceInventory = furnaceInv;
    }

    public SmelterScreen(PlayerInventory playerInv, HellforgeEntity blstFurnace) {
        super(new HellforgeContainer(playerInv, blstFurnace));
        this.furnaceInventory = blstFurnace;
        this.texName = "hellforge";
        this.yOffset = 72;
    }

    public SmelterScreen(PlayerInventory playerInv, SmokerEntity blstFurnace) {
        super(new SmokerContainer(playerInv, blstFurnace));
        this.furnaceInventory = blstFurnace;
        this.texName = "smoker";
        this.yOffset = 31;
        this.xOffset = 16;
    }

    @Override
    public void initGui() {
        super.initGui();
        if (this.container instanceof SmokerContainer) {
            int guiTop = (this.height - this.contHeight) / 2 + this.contHeight - 92;
            this.buttons.get(0).y = guiTop;
            this.buttons.add(new InvisibleButton(1, this.width / 2 + 62, guiTop, 10, 10, "v", "gui.takeall"));
            this.buttons.add(new InvisibleButton(2, this.width / 2 + 52, guiTop + 2, 10, 10, "^", "gui.putall"));
        }
    }

    @Override
    protected void buttonClicked(Button button) {
        super.buttonClicked(button);
        if (button.id == 1) {
            for (int index = 0; index < this.container.slots.size() - 36; index++) {
                this.transferItems(index);
            }
        }

        if (button.id == 2) {
            for (int index = this.container.slots.size() - 36; index < this.container.slots.size(); index++) {
                this.transferItems(index);
            }
        }
    }

    @Override
    protected void drawForegroundLayer() {
        this.drawCenteredString(this.fontRenderer, Translator.getInstance().translateKey(this.furnaceInventory.getName()), this.contWidth / 2, 7, 16777215);
        this.drawStringNoShadow(this.fontRenderer, Translator.getInstance().translateKey("container.inventory"), 8, this.contHeight - 91, this.txtColor);
    }

    @Override
    protected void drawBackgroundLayer(float deltaTicks) {
        int tex = this.mc.textureLoader.getTexture("/gui/smelters/" + this.texName + ".png");
        this.mc.textureLoader.bindTexture(tex);
        int x = (this.width - this.contWidth) / 2;
        int y = (this.height - this.contHeight) / 2;
        this.drawGraphic(x, y, 0, 0, this.contWidth, 206);
        if (this.furnaceInventory.isBurning()) {
            int burnTime = this.furnaceInventory.getRemainingFuel(12);
            this.drawGraphic(x + this.xOffset, y + this.yOffset - burnTime, 176, 12 - burnTime, 14, burnTime + 2);
        }

        if (!(this.furnaceInventory instanceof SmokerEntity)) {
            int burnTime = this.furnaceInventory.getCookProgress(24);
            this.drawGraphic(x + 71, y + 40, 176, 14, burnTime + 1, 16);
        }
    }
}
