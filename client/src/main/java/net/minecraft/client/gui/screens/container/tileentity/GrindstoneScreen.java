package net.minecraft.client.gui.screens.container.tileentity;

import net.minecraft.client.gui.screens.container.ContainerScreen;
import net.minecraft.game.container.inventory.PlayerInventory;
import net.minecraft.game.container.tileentities.GrindstoneContainer;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;
import net.minecraft.localization.Translator;

public class GrindstoneScreen extends ContainerScreen {
    public GrindstoneScreen(PlayerInventory playerInv, World world, int x, int y, int z) {
        super(new GrindstoneContainer(playerInv, world, x, y, z));
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttons.get(0).y -= 11;
    }

    @Override
    protected void drawForegroundLayer() {
        this.drawCenteredString(this.fontRenderer, Translator.getInstance().translateKey("container.disenchant"), this.contWidth / 2, 8, 16777215);
        this.drawStringNoShadow(this.fontRenderer, Translator.getInstance().translateKey("container.inventory"), 8, this.contHeight - 102, this.txtColor);
        this.drawCenteredString(this.fontRenderer, Translator.getInstance().translateKey("gui.score") + ": " + "§e" + this.mc.player.score, 90, -10, 16777215);
        if (((GrindstoneContainer)this.container).grindstoneInv.getStackInSlot(0) != null) {
            if (!((GrindstoneContainer)this.container).canReduce
                && ((GrindstoneContainer)this.container).grindstoneInv.getStackInSlot(0).itemDmg > 0
                && ((GrindstoneContainer)this.container).grindstoneInv.getStackInSlot(0).getItem() != ItemList.scroll) {
                this.drawString(this.fontRenderer, Translator.getInstance().translateKey("container.damaged"), 12, 54, 16736352);
            } else {
                this.drawString(
                    this.fontRenderer,
                    Translator.getInstance().translateKey("container.repair.cost")
                        + ": "
                        + ((GrindstoneContainer)this.container).grindstoneInv.getStackInSlot(0).getRepairCost(),
                    98,
                    54,
                    ((GrindstoneContainer)this.container).getSlot(1).canTakeStack(this.mc.player) ? 16777215 : 16736352
                );
            }
        }
    }

    @Override
    protected void drawBackgroundLayer(float tickDelta) {
        int tex = this.mc.textureLoader.getTexture("/gui/utility/grindstone.png");
        this.mc.textureLoader.bindTexture(tex);
        int x = (this.width - this.contWidth) / 2;
        int y = (this.height - this.contHeight) / 2;
        this.drawGraphic(x, y, 0, 0, this.contWidth, 200);
    }
}
