package net.minecraft.client.gui.screens.container.tileentity;

import java.nio.charset.StandardCharsets;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.button.TextField;
import net.minecraft.client.gui.screens.container.ContainerScreen;
import net.minecraft.client.network.player.NetworkPlayer;
import net.minecraft.game.container.Container;
import net.minecraft.game.container.ContainerListener;
import net.minecraft.game.container.inventory.PlayerInventory;
import net.minecraft.game.container.tileentities.AnvilContainer;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.localization.Translator;
import net.minecraft.network.packet.container.CustomPayloadPacket;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class AnvilScreen extends ContainerScreen implements ContainerListener {
    private AnvilContainer anvil;
    private TextField nameField;
    private PlayerInventory inventory;

    public AnvilScreen(PlayerInventory inventory, World world, int x, int y, int z) {
        super(new AnvilContainer(inventory, world, x, y, z, Minecraft.getMinecraft().player));
        this.inventory = inventory;
        this.anvil = (AnvilContainer)this.container;
    }

    @Override
    public void update() {
        super.update();
        this.nameField.updateCursor();
    }

    @Override
    public void initGui() {
        super.initGui();
        Keyboard.enableRepeatEvents(true);
        int x = (this.width - this.contWidth) / 2;
        int y = (this.height - this.contHeight) / 2;
        this.buttons.get(0).y -= 3;
        this.nameField = new TextField(this.fontRenderer, x + 62, y + 24, 103, 12);
        this.nameField.setTextColor(-1);
        this.nameField.setRenderBackground(false);
        this.nameField.setMaxStringLength(30);
        this.container.removeListener(this);
        this.container.addListener(this);
    }

    @Override
    public void onClose() {
        super.onClose();
        Keyboard.enableRepeatEvents(false);
        this.container.removeListener(this);
    }

    @Override
    protected void drawForegroundLayer() {
        this.drawCenteredString(this.fontRenderer, Translator.getInstance().translateKey("container.repair"), 115, 7, 16777215);
        this.drawStringNoShadow(this.fontRenderer, Translator.getInstance().translateKey("container.inventory"), 8, this.contHeight - 93, 4210752);
        this.drawCenteredString(this.fontRenderer, Translator.getInstance().translateKey("gui.score") + ": " + "§e" + this.mc.player.score, 90, -10, 16777215);
        if (this.anvil.getSlot(2).getStack() != null) {
            int txtColor = 8453920;
            boolean hasStack = true;
            String cost = Translator.getInstance().translateKey("container.repair.cost") + ": " + this.anvil.getSlot(2).getStack().getRepairCost();
            if (!this.anvil.getSlot(2).hasStack()) {
                hasStack = false;
            } else if (!this.anvil.getSlot(2).canTakeStack(this.inventory.player)) {
                txtColor = 16736352;
            }

            if (hasStack) {
                int x = this.contWidth - 8 - this.fontRenderer.getStringWidth(cost);
                this.drawString(this.fontRenderer, cost, x, 65, txtColor);
            }
        }
    }

    @Override
    protected void keyTyped(char character, int key) {
        if (this.nameField.textboxKeyTyped(character, key)) {
            this.anvil.updateItemName(this.nameField.getText());
            if (this.mc.player instanceof NetworkPlayer) {
                NetworkPlayer player = (NetworkPlayer)this.mc.player;
                player.connection.sendPacket(new CustomPayloadPacket("MC|ItemName", this.nameField.getText().getBytes(StandardCharsets.UTF_8)));
            }
        } else {
            super.keyTyped(character, key);
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int click) {
        super.mouseClicked(x, y, click);
        this.nameField.mouseClicked(x, y, click);
    }

    @Override
    public void drawScreen(int x, int y, float deltaTime) {
        super.drawScreen(x, y, deltaTime);
        GL11.glDisable(2896);
        this.nameField.drawTextBox();
    }

    @Override
    protected void drawBackgroundLayer(float deltaTick) {
        this.mc.textureLoader.bindTexture("/gui/utility/repair.png");
        int x = (this.width - this.contWidth) / 2;
        int y = (this.height - this.contHeight) / 2;
        this.drawGraphic(x, y, 0, 0, this.contWidth, 180);
        this.drawGraphic(x + 59, y + 20, 0, this.contHeight + (this.anvil.getSlot(0).hasStack() ? 0 : 16), 110, 16);
        if ((this.anvil.getSlot(0).hasStack() || this.anvil.getSlot(1).hasStack()) && !this.anvil.getSlot(2).hasStack()) {
            this.drawGraphic(x + 99, y + 45, this.contWidth, 0, 28, 21);
        }
    }

    @Override
    public void updateCraftingInventory(Container container, List<ItemStack> list) {
        this.setStack(container, 0, container.getSlot(0).getStack());
    }

    @Override
    public void setStack(Container container, int slotId, ItemStack stack) {
        if (slotId == 0) {
            String field = "";
            if (stack != null) {
                if (!stack.customName.isEmpty()) {
                    field = stack.customName;
                } else {
                    field = Translator.getInstance().translateNamedKey(stack.getItemName(stack.itemDmg));
                }
            }

            this.nameField.setText(field);
            this.nameField.setEnabled(stack != null);
            if (stack != null) {
                this.anvil.updateItemName(this.nameField.getText());
                if (this.mc.player instanceof NetworkPlayer) {
                    NetworkPlayer player = (NetworkPlayer)this.mc.player;
                    player.connection.sendPacket(new CustomPayloadPacket("MC|ItemName", this.nameField.getText().getBytes(StandardCharsets.UTF_8)));
                }
            }
        }
    }

    @Override
    public void setId(Container container, int x, int y) {
    }
}
