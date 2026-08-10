package net.minecraft.client.gui.screens.container.inventory;

import java.util.List;
import net.minecraft.client.gui.ScreenRescaler;
import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.button.PageButton;
import net.minecraft.client.gui.screens.container.ContainerScreen;
import net.minecraft.client.render.Lighting;
import net.minecraft.game.container.inventory.PlayerInventory;
import net.minecraft.game.container.player.LoreContainer;
import net.minecraft.game.container.slot.RecipeSlot;
import net.minecraft.game.container.slot.Slot;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.crafting.Recipe;
import net.minecraft.game.item.crafting.ShapedRecipe;
import net.minecraft.localization.Translator;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class RecipeScreen extends ContainerScreen {
    private List<Recipe> recipeList;
    private PageButton nextButton;
    private PageButton prevButton;
    private int page = 0;
    private int maxPages = 0;

    public RecipeScreen(PlayerInventory inventory) {
        super(new LoreContainer(inventory));
        this.contWidth = 256;
        this.contHeight = 205;
        if (inventory.getItemStack() != null) {
            this.container.putStackInSlot(0, inventory.getItemStack());
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        int guiTop = (this.height - this.contHeight) / 2 + this.contHeight - 89;
        this.buttons.get(0).y = guiTop;
        int x = this.width / 2;
        int y = this.height / 2;
        this.buttons.add(this.nextButton = new PageButton(1, x + 85, y - 15, true));
        this.buttons.add(this.prevButton = new PageButton(2, x - 110, y - 15, false));
        this.updateButtons();
    }

    @Override
    public void update() {
        super.update();
        if (!this.container.slots.get(0).hasStack()) {
            this.page = 0;
        }

        RecipeSlot slot = (RecipeSlot)((LoreContainer)this.container).slots.get(0);
        this.recipeList = slot.recipeList;
        this.maxPages = this.recipeList.size();
        this.updateButtons();
    }

    @Override
    protected void buttonClicked(Button button) {
        super.buttonClicked(button);
        if (button.enabled) {
            if (button.id == 1) {
                this.nextPage();
            } else if (button.id == 2) {
                this.prevPage();
            }
        }
    }

    private void nextPage() {
        int count = 1;
        if (this.page == 0) {
            count = 0;
        }

        if (this.page < this.maxPages - count) {
            if (this.page == 0) {
                this.page++;
            } else {
                this.page += 2;
            }
        }
    }

    private void prevPage() {
        if (this.page > 0) {
            if (this.page == 1) {
                this.page--;
            } else {
                this.page -= 2;
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int click) {
        if (click == 0 || click == 1) {
            Slot slot = this.getSlot(mouseX, mouseY);
            if (slot != null && slot.slotNumber == 0 && this.page > 0) {
                return;
            }
        }

        super.mouseClicked(mouseX, mouseY, click);
    }

    @Override
    protected void keyTyped(char character, int key) {
        super.keyTyped(character, key);
        switch (key) {
            case 201:
                this.nextPage();
                this.mc.sndManager.playSoundFX("random.page", 1.0F, 1.0F);
                return;
            case 209:
                this.prevPage();
                this.mc.sndManager.playSoundFX("random.page", 1.0F, 1.0F);
                return;
        }
    }

    private void updateButtons() {
        if (this.container.slots.get(0).hasStack()) {
            if (this.page == 0) {
                this.nextButton.visible = this.page < this.maxPages;
            } else {
                this.nextButton.visible = this.page < this.maxPages - 1;
            }

            this.nextButton.enabled = true;
        } else {
            this.nextButton.visible = false;
            this.nextButton.enabled = false;
        }

        this.prevButton.visible = this.prevButton.enabled = this.page > 0;
    }

    @Override
    protected void drawSlot(Slot slot) {
        if (this.page <= 0 || slot.slotNumber != 0) {
            super.drawSlot(slot);
        }
    }

    @Override
    protected void renderTooltip(int width, int height, int x, int y, Slot lastSlot) {
        if (this.page <= 0 || lastSlot == null || lastSlot.slotNumber != 0) {
            super.renderTooltip(width, height, x, y, lastSlot);
        }
    }

    @Override
    protected void highlightSlot(Slot slot) {
        if (this.page <= 0 || slot.slotNumber != 0) {
            super.highlightSlot(slot);
        }
    }

    @Override
    protected void drawForegroundLayer() {
        this.drawStringNoShadow(this.fontRenderer, this.mc.player.inventory.getName(), 48, this.contHeight - 87, this.txtColor);
        if (this.page == 0) {
            this.drawCenteredStringNoShadow(this.fontRenderer, Translator.getInstance().translateKey("book.info1"), 68, 18, 4210752);
            this.drawCenteredStringNoShadow(this.fontRenderer, Translator.getInstance().translateKey("book.info2"), 68, 28, 4210752);
            this.drawCenteredStringNoShadow(this.fontRenderer, Translator.getInstance().translateKey("book.info3"), 68, 38, 4210752);
            this.drawStringNoShadow(this.fontRenderer, Translator.getInstance().translateKey("book.item") + " : ", 46, 72, 4210752);
            ItemStack itemstack = ((LoreContainer)this.container).loreSlot.getStackInSlot(0);
            if (itemstack != null) {
                this.drawStringNoShadow(
                    this.fontRenderer, Translator.getInstance().translateKey(itemstack.getItemName(itemstack.itemDmg) + ".name"), 134, 14, 4210752
                );
                this.drawSplitString(
                    this.fontRenderer,
                    itemstack.getItemDescription(itemstack.itemDmg) != null
                        ? Translator.getInstance().translateKey(itemstack.getItemDescription(itemstack.itemDmg))
                        : "",
                    134,
                    28,
                    100,
                    4210752,
                    false,
                    1
                );
            }
        }
    }

    @Override
    protected void drawBackgroundLayer(float deltaTime) {
        int tex = this.mc.textureLoader.getTexture("/gui/container/lore.png");
        if (this.page >= 1) {
            tex = this.mc.textureLoader.getTexture("/gui/container/recipes.png");
        }

        this.mc.textureLoader.bindTexture(tex);
        int x = (this.width - this.contWidth) / 2;
        int y = (this.height - this.contHeight) / 2;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawGraphic(x, y, 0, 0, this.contWidth, this.contHeight / 2 + 14);
        this.drawGraphic(x, y + 110, 0, this.contHeight / 2 + 9, this.contWidth, this.contHeight / 2 + 12);
        if (this.page != 0) {
            this.drawStringNoShadow(this.fontRenderer, Translator.getInstance().translateKey("book.recipes"), x + 38, y + 12, 4210752);
            this.drawStringNoShadow(this.fontRenderer, Translator.getInstance().translateKey("book.recipes"), x + 142, y + 12, 4210752);
            this.drawStringNoShadow(
                this.fontRenderer, Translator.getInstance().translateKey("gui.book.page") + ": " + this.page + " / " + this.maxPages, x + 42, y + 86, 4210752
            );
            if (this.page < this.maxPages) {
                this.drawStringNoShadow(
                    this.fontRenderer,
                    Translator.getInstance().translateKey("gui.book.page") + ": " + (this.page + 1) + " / " + this.maxPages,
                    x + 134,
                    y + 86,
                    4210752
                );
            }
        }

        if (this.page >= 1) {
            Recipe leftRecipe = this.recipeList.get(this.page - 1);
            Recipe rightRecipe = null;
            if (this.page < this.recipeList.size()) {
                rightRecipe = this.recipeList.get(this.page);
            }

            int sz = 106;
            int leftRecipeX = x + 131 - sz - 2;
            int rightRecipeX = x + 127;
            if (rightRecipe != null) {
                this.drawRecipe(rightRecipe, rightRecipeX, y + 17);
            }

            this.drawRecipe(leftRecipe, leftRecipeX, y + 17);
        }
    }

    private void drawRecipe(Recipe recipe, int recipeX, int recipeY) {
        int size = 106;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(32826);
        GL11.glPushMatrix();
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        Lighting.enable();
        GL11.glPopMatrix();
        GL11.glEnable(2884);
        GL11.glEnable(2929);
        ItemStack[] items = recipe.getIngredients();
        int recipeWidth = 3;
        int recipeHeight = 3;
        if (recipe instanceof ShapedRecipe) {
            recipeWidth = ((ShapedRecipe)recipe).getRecipeWidth();
            recipeHeight = ((ShapedRecipe)recipe).getRecipeHeight();
        }

        for (int x = 0; x < recipeWidth; x++) {
            for (int y = 0; y < recipeHeight; y++) {
                int recipeIndex = y * recipeWidth + x;
                if (recipeIndex < items.length) {
                    ItemStack item = items[recipeIndex];
                    if (item != null) {
                        int meta = item.itemDmg;
                        if (item.itemDmg < 0) {
                            item.itemDmg = 0;
                        }

                        GL11.glColor3f(1.0F, 1.0F, 1.0F);
                        itemRenderer.zLevel = 50.0F;
                        itemRenderer.renderItemSprite(this.fontRenderer, this.mc.textureLoader, item, recipeX + 8 + x * 18, recipeY + 8 + y * 18);
                        itemRenderer.renderGUIOverlays(this.fontRenderer, this.mc.textureLoader, item, recipeX + 8 + x * 18, recipeY + 8 + y * 18);
                        itemRenderer.zLevel = 0.0F;
                        item.itemDmg = meta;
                    }
                }
            }
        }

        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        itemRenderer.zLevel = 50.0F;
        itemRenderer.renderItemSprite(this.fontRenderer, this.mc.textureLoader, recipe.getRecipeOutput(), recipeX + size - 16 - 8, recipeY + 26);
        itemRenderer.renderGUIOverlays(this.fontRenderer, this.mc.textureLoader, recipe.getRecipeOutput(), recipeX + size - 16 - 8, recipeY + 26);
        itemRenderer.zLevel = 0.0F;
        GL11.glTranslatef(0.0F, 0.0F, 100.0F);
        Lighting.disable();
        String count = "" + recipe.getRecipeOutput().stackSize;
        if (recipe.getRecipeOutput().stackSize > 1) {
            this.drawString(this.fontRenderer, count, recipeX + size - 16 - 8 + 17 - this.fontRenderer.getStringWidth(count), recipeY + 26 + 9, 16777215);
        }

        ScreenRescaler rescaler = this.mc.rescaler;
        int scaledWidth = rescaler.getScaledWidth();
        int scaledHeight = rescaler.getScaledHeight();
        int mouseX = Mouse.getX() * scaledWidth / this.mc.displayWidth;
        int mouseY = scaledHeight - Mouse.getY() * scaledHeight / this.mc.displayHeight - 1;
        String itemName;
        if (mouseX >= recipeX + size - 16 - 8 && mouseY >= recipeY + 26 && mouseX <= recipeX + size - 16 - 8 + 16 && mouseY <= recipeY + 26 + 16) {
            itemName = Translator.getInstance().translateNamedKey(recipe.getRecipeOutput().getItemName(recipe.getRecipeOutput().itemDmg));
        } else {
            itemName = "";
        }

        itemName = itemName.trim();
        if (itemName.length() > 0) {
            int newX = mouseX + 12;
            int newY = mouseY - 12;
            int nameWidth = this.fontRenderer.getStringWidth(itemName);
            this.drawGradient(newX - 3, newY - 3, newX + nameWidth + 3, newY + 8 + 3, 1610941696, -1607454624, 1.5F);
            this.drawString(this.fontRenderer, itemName, newX, newY, 16777215);
        }

        for (int x = 0; x < recipeWidth; x++) {
            for (int y = 0; y < recipeHeight; y++) {
                int ix = y * recipeWidth + x;
                if (ix < items.length) {
                    ItemStack item = items[ix];
                    if (item != null) {
                        int dmg = item.itemDmg;
                        if (dmg < 0) {
                            dmg = 0;
                        }

                        if (mouseX >= recipeX + 8 + x * 18
                            && mouseY >= recipeY + 8 + y * 18
                            && mouseX <= recipeX + 8 + x * 18 + 16
                            && mouseY <= recipeY + 8 + y * 18 + 16) {
                            itemName = Translator.getInstance().translateNamedKey(item.getItemName(dmg));
                        } else {
                            itemName = "";
                        }

                        itemName = itemName.trim();
                        if (itemName.length() > 0) {
                            int newX = mouseX + 12;
                            int newY = mouseY - 12;
                            int nameWidth = this.fontRenderer.getStringWidth(itemName);
                            this.drawGradient(newX - 3, newY - 3, newX + nameWidth + 3, newY + 8 + 3, 1610941696, -1607454624, 1.5F);
                            this.drawString(this.fontRenderer, itemName, newX, newY, 16777215);
                        }
                    }
                }
            }
        }
    }
}
