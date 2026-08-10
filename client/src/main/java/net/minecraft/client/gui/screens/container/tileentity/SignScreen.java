package net.minecraft.client.gui.screens.container.tileentity;

import net.minecraft.SharedConstants;
import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.screens.GuiScreen;
import net.minecraft.client.render.entity.tileentity.TileEntityRenderManager;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.tileentity.other.SignEntity;
import net.minecraft.localization.Translator;
import net.minecraft.network.packet.container.SignPacket;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class SignScreen extends GuiScreen {
    protected String screenTitle = Translator.getInstance().translateKey("menu.sign");
    private SignEntity sign;
    private int updateCounter;
    private int editLine = 0;

    public SignScreen(SignEntity sign) {
        this.sign = sign;
    }

    @Override
    public void initGui() {
        this.buttons.clear();
        Keyboard.enableRepeatEvents(true);
        this.buttons.add(new Button(0, this.width / 2 - 100, this.height / 4 + 120, 200, 20, "gui.done"));
    }

    @Override
    public void onClose() {
        super.onClose();
        Keyboard.enableRepeatEvents(false);
        if (this.mc.world.isOnline) {
            this.mc.getSendQueue().sendPacket(new SignPacket(this.sign.x, this.sign.y, this.sign.z, this.sign.signText));
        }
    }

    @Override
    public void update() {
        this.updateCounter++;
    }

    @Override
    protected void buttonClicked(Button button) {
        if (button.enabled && button.id == 0) {
            this.sign.onChanged();
            this.mc.setScreen(null);
        }
    }

    @Override
    protected void keyTyped(char character, int key) {
        if (key == 200) {
            this.editLine = this.editLine - 1 & 3;
        }

        if (key == 208 || key == 28) {
            this.editLine = this.editLine + 1 & 3;
        }

        if (key == 1) {
            this.sign.onChanged();
            this.mc.setScreen(null);
        }

        if (key == 14 && this.sign.signText[this.editLine].length() > 0) {
            this.sign.signText[this.editLine] = this.sign.signText[this.editLine].substring(0, this.sign.signText[this.editLine].length() - 1);
        }

        if (SharedConstants.VALID_TEXT_CHARACTERS.indexOf(character) >= 0 && this.sign.signText[this.editLine].length() < 15) {
            this.sign.signText[this.editLine] = this.sign.signText[this.editLine] + character;
        }
    }

    @Override
    public void drawScreen(int x, int y, float deltaTick) {
        this.drawBackground();
        super.drawScreen(x, y, deltaTick);
        this.drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 40, 16777215);
        GL11.glPushMatrix();
        GL11.glTranslatef(this.width / 2, this.height / 4 + 60, 50.0F);
        float modelScale = 93.75F;
        GL11.glScalef(-modelScale, -modelScale, -modelScale);
        GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
        Block signType = this.sign.getBlockType();
        if (signType != BlockList.signWall && signType != BlockList.signWall2) {
            float rotation = this.sign.getBlockMetadata() * 360 / 16.0F;
            GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(0.0F, 0.3125F, 0.0F);
        } else {
            int meta = this.sign.getBlockMetadata() & 3;
            float angle = 0.0F;
            if (meta == 2) {
                angle = -180.0F;
            }

            if (meta == 1) {
                angle = -90.0F;
            }

            if (meta == 0) {
                angle = 90.0F;
            }

            GL11.glRotatef(angle, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(0.0F, 0.3125F, 0.0F);
        }

        if (this.updateCounter / 6 % 2 == 0) {
            this.sign.activeLine = this.editLine;
        }

        TileEntityRenderManager.instance.renderTileEntityAt(this.sign, -0.5, -0.75, -0.5, 0.0F);
        this.sign.activeLine = -1;
        GL11.glPopMatrix();
    }
}
