package net.minecraft.client.gui.screens.container.tileentity;

import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.button.RecipeButton;
import net.minecraft.client.gui.screens.container.ContainerScreen;
import net.minecraft.game.container.inventory.CraftingInventory;
import net.minecraft.game.container.inventory.PlayerInventory;
import net.minecraft.game.container.inventory.WorkbenchInventory;
import net.minecraft.game.container.tileentities.CrafterContainer;
import net.minecraft.game.container.tileentities.WorkbenchContainer;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;
import net.minecraft.localization.Translator;

public class CraftingScreen extends ContainerScreen {
    public CraftingScreen(PlayerInventory inventory, World world, int x, int y, int z, boolean portable) {
        super(new WorkbenchContainer(inventory, world, x, y, z, portable));
    }

    public CraftingScreen(PlayerInventory upperChestInventory, CraftingInventory lowerChestInventory, WorkbenchInventory inv, World world) {
        super(new CrafterContainer(upperChestInventory, lowerChestInventory, inv, world));
    }

    @Override
    public void initGui() {
        super.initGui();
        int x = (this.width - this.contWidth) / 2;
        int y = (this.height - this.contHeight) / 2;
        this.buttons.add(new RecipeButton(1, x + 148, y + 34, 20, 20, "craft", "gui.recipes", ItemList.greenBook.id));
    }

    @Override
    protected void buttonClicked(Button button) {
        super.buttonClicked(button);
        if (button.id == 1) {
            this.mc.player.displayGUIRecipe();
        }
    }

    @Override
    protected void drawForegroundLayer() {
        this.drawString(this.fontRenderer, Translator.getInstance().translateKey("container.craft"), 32, 6, 16777215);
        this.drawStringNoShadow(this.fontRenderer, Translator.getInstance().translateKey("container.inventory"), 8, this.contHeight - 91, this.txtColor);
    }

    @Override
    protected void drawBackgroundLayer(float deltaTick) {
        int tex = this.mc.textureLoader.getTexture("/gui/utility/crafting.png");
        this.mc.textureLoader.bindTexture(tex);
        int x = (this.width - this.contWidth) / 2;
        int y = (this.height - this.contHeight) / 2;
        this.drawGraphic(x, y, 0, 0, this.contWidth, 203);
    }
}
