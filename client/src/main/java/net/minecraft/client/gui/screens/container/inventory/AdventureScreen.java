package net.minecraft.client.gui.screens.container.inventory;

import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.button.InvisibleButton;
import net.minecraft.client.gui.button.RecipeButton;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.localization.Translator;
import net.minecraft.util.math.MathHelper;

public class AdventureScreen extends InventoryScreen {
    public AdventureScreen(Player player) {
        super(player);
        this.texture = "inventoryAlt";
    }

    @Override
    public void initGui() {
        super.initGui();
        int btnX = (this.width - this.contWidth) / 2;
        int btnY = (this.height - this.contHeight) / 2;
        this.buttons.clear();
        this.buttons.add(this.closeButton = new InvisibleButton(0, this.width / 2 + 72, this.height / 2 - 79, 10, 10, "x", "gui.close"));
        this.buttons.add(new InvisibleButton(2, btnX + 140, btnY + 140 - 121, 10, 10, "+", "gui.upgrade"));
        this.buttons.add(new InvisibleButton(3, btnX + 140, btnY + 140 - 111, 10, 10, "+", "gui.upgrade"));
        this.buttons.add(new InvisibleButton(4, btnX + 140, btnY + 140 - 101, 10, 10, "+", "gui.upgrade"));
        this.buttons.add(this.recipeButton = new RecipeButton(1, btnX + 150, btnY + 140 - 80, 20, 20, "", "container.craft", BlockList.workbench.id));
    }

    @Override
    protected void drawForegroundLayer() {
        this.drawStringNoShadow(this.fontRenderer, this.mc.player.username, 100, 9, this.txtColor);
        this.drawStringNoShadow(this.fontRenderer, "ATK: " + (this.mc.player.properties.ATK + 1) * 10, 100, 20, this.txtColor);
        this.drawStringNoShadow(this.fontRenderer, "DEF: " + (this.mc.player.properties.DEF + 1) * 10, 100, 30, this.txtColor);
        this.drawStringNoShadow(this.fontRenderer, "SPD: " + (this.mc.player.properties.SPD + 1) * 10, 100, 40, this.txtColor);
        this.drawStringNoShadow(this.fontRenderer, "LVL: " + this.mc.player.properties.LVL, 100, 50, this.txtColor);
        this.drawCenteredString(this.fontRenderer, Translator.getInstance().translateKey("gui.score") + ": " + "§e" + this.mc.player.score, 90, -10, 16777215);
        if (this.mc.player.inventory.accessorySlots[2] != null && this.mc.player.inventory.accessorySlots[2].getItem() == ItemList.backpack) {
            this.drawStringNoShadow(this.fontRenderer, Translator.getInstance().translateKey("container.backpack"), 194, 24, this.txtColor);
        }
    }

    @Override
    protected void buttonClicked(Button button) {
        super.buttonClicked(button);
        if (button == this.recipeButton) {
            this.mc
                .player
                .displayWorkbenchGUI(
                    MathHelper.floor_double(this.mc.player.posX),
                    MathHelper.floor_double(this.mc.player.posY),
                    MathHelper.floor_double(this.mc.player.posZ),
                    true
                );
        }

        if (button.id == 2) {
            this.increaseStat("ATK", this.mc.player.properties.ATK);
        }

        if (button.id == 3) {
            this.increaseStat("DEF", this.mc.player.properties.DEF);
        }

        if (button.id == 4) {
            this.increaseStat("SPD", this.mc.player.properties.SPD);
        }
    }

    private void increaseStat(String statName, int stat) {
        if (this.mc.player.properties.LVL <= this.mc.player.score && stat < 9) {
            switch (statName) {
                case "ATK":
                    this.mc.player.properties.ATK++;
                    break;
                case "DEF":
                    this.mc.player.properties.DEF++;
                    break;
                case "SPD":
                    this.mc.player.properties.SPD++;
            }

            this.mc.player.score = this.mc.player.score - this.mc.player.properties.LVL;
            if (this.mc.player.properties.LVL < 102400) {
                this.mc.player.properties.LVL *= 2;
            }
        }
    }
}
