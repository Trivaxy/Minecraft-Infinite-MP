package net.minecraft.client.gui.screens.stats;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiList;
import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.screens.GuiScreen;
import net.minecraft.client.gui.screens.stats.slot.BlockSlot;
import net.minecraft.client.gui.screens.stats.slot.GeneralSlot;
import net.minecraft.client.gui.screens.stats.slot.ItemsSlot;
import net.minecraft.client.gui.screens.stats.slot.MobsSlot;
import net.minecraft.client.localization.Font;
import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.statistics.StatWriter;
import net.minecraft.game.item.ItemStack;
import net.minecraft.localization.Translator;
import org.lwjgl.opengl.GL11;

public class StatsScreen extends GuiScreen {
    private static ItemRenderer renderItem = new ItemRenderer();
    private StatWriter stattistics;
    protected GuiScreen parent;
    protected String title = "gui.statistics";
    private GeneralSlot general;
    private ItemsSlot item;
    private BlockSlot block;
    private MobsSlot mob;
    private GuiList selectedSlot = null;

    public StatsScreen(GuiScreen screen, StatWriter stat) {
        this.parent = screen;
        this.stattistics = stat;
    }

    @Override
    public void initGui() {
        this.general = new GeneralSlot(this);
        this.item = new ItemsSlot(this);
        this.block = new BlockSlot(this);
        this.mob = new MobsSlot(this);
        this.selectedSlot = this.general;
        this.buttons.clear();
        this.buttons.add(new Button(0, this.width / 2 - 70, this.height - 28, 150, 20, "gui.done"));
        this.buttons.add(new Button(1, this.width / 2 - 170, this.height - 52, 80, 20, "stat.general"));
        this.buttons.add(new Button(2, this.width / 2 - 80, this.height - 52, 80, 20, "stat.blocks"));
        this.buttons.add(new Button(3, this.width / 2 + 10, this.height - 52, 80, 20, "stat.items"));
        this.buttons.add(new Button(4, this.width / 2 + 100, this.height - 52, 80, 20, "stat.mobs"));
        this.setButtonStates();
    }

    private void setButtonStates() {
        Button general = this.buttons.get(1);
        Button blocks = this.buttons.get(2);
        Button items = this.buttons.get(3);
        Button mobs = this.buttons.get(4);
        general.enabled = true;
        blocks.enabled = true;
        items.enabled = true;
        mobs.enabled = true;
        if (this.selectedSlot == this.general) {
            general.enabled = false;
        }

        if (this.selectedSlot == this.block) {
            blocks.enabled = false;
        }

        if (this.selectedSlot == this.item) {
            items.enabled = false;
        }

        if (this.selectedSlot == this.mob) {
            mobs.enabled = false;
        }
    }

    @Override
    protected void buttonClicked(Button button) {
        if (button.enabled) {
            if (button.id == 0) {
                this.mc.setScreen(this.parent);
            } else if (button.id == 1) {
                this.selectedSlot = this.general;
            } else if (button.id == 3) {
                this.selectedSlot = this.item;
            } else if (button.id == 2) {
                this.selectedSlot = this.block;
            } else if (button.id == 4) {
                this.selectedSlot = this.mob;
            }

            this.setButtonStates();
        }
    }

    @Override
    public void scroll(int scrollValue) {
        this.selectedSlot.scrollMouse();
    }

    @Override
    public void drawScreen(int x, int y, float tick) {
        this.selectedSlot.draw(x, y, tick);
        this.drawCenteredString(this.fontRenderer, Translator.getInstance().translateKey(this.title), this.width / 2, 12, 16777215);
        super.drawScreen(x, y, tick);
    }

    private void drawItem(int x, int y, int id) {
        this.drawButtonBg(x + 1, y + 1, 0, 0);
        GL11.glEnable(32826);
        GL11.glPushMatrix();
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        Lighting.enable();
        GL11.glPopMatrix();
        renderItem.zLevel = 100.0F;
        renderItem.renderItemSprite(this.fontRenderer, this.mc.textureLoader, new ItemStack(id, 1, 0), x + 2, y + 2);
        renderItem.zLevel = 0.0F;
        Lighting.disable();
        GL11.glDisable(32826);
    }

    private void drawButtonBg(int x, int y, int width, int height) {
        int tex = this.mc.textureLoader.getTexture("/gui/slot.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.textureLoader.bindTexture(tex);
        Tessellator tess = Tessellator.instance;
        tess.begin();
        tess.addTexturedVertex(x + 0, y + 18, this.zLevel, (width + 0) * 0.0078125F, (height + 18) * 0.0078125F);
        tess.addTexturedVertex(x + 18, y + 18, this.zLevel, (width + 18) * 0.0078125F, (height + 18) * 0.0078125F);
        tess.addTexturedVertex(x + 18, y + 0, this.zLevel, (width + 18) * 0.0078125F, (height + 0) * 0.0078125F);
        tess.addTexturedVertex(x + 0, y + 0, this.zLevel, (width + 0) * 0.0078125F, (height + 0) * 0.0078125F);
        tess.draw();
    }

    public static Minecraft getMinecraft(StatsScreen screen) {
        return screen.mc;
    }

    public static Font getFontRenderer(StatsScreen screen) {
        return screen.fontRenderer;
    }

    public static StatWriter getStatsFileWriter(StatsScreen screen) {
        return screen.stattistics;
    }

    public static void drawSprite(StatsScreen screen, int x, int y, int w, int h) {
        screen.drawButtonBg(x, y, w, h);
    }

    public static void drawItemSprite(StatsScreen screen, int x, int y, int id) {
        screen.drawItem(x, y, id);
    }
}
