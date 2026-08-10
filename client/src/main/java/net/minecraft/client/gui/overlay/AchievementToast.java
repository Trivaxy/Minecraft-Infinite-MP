package net.minecraft.client.gui.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.ScreenRescaler;
import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.localization.Translator;
import net.minecraft.statistics.achievement.Achievement;
import org.lwjgl.opengl.GL11;

public class AchievementToast extends GuiComponent {
    private Minecraft mc;
    private Achievement achievement;
    private ItemRenderer itemRender;
    private String type;
    private String name;
    public long time;
    private int width;
    private int height;
    private boolean hint;

    public AchievementToast(Minecraft mc) {
        this.mc = mc;
        this.itemRender = new ItemRenderer(mc);
    }

    public void queueAchievement(Achievement achievement) {
        this.mc.sndManager.playSoundFX("random.in", 1.0F, 1.0F);
        if (achievement.getSpecial()) {
            this.mc.sndManager.playSoundFX("random.levelup", 1.0F, 1.0F);
        }

        this.type = achievement.getSpecial()
            ? Translator.getInstance().translateKey("challenge.get")
            : (achievement.getGoal() ? Translator.getInstance().translateKey("goal.get") : Translator.getInstance().translateKey("achievement.get"));
        this.name = achievement.getName();
        this.time = System.currentTimeMillis();
        this.achievement = achievement;
        this.hint = false;
    }

    public void queueHint(Achievement achievement) {
        this.type = achievement.getName();
        this.name = achievement.getDescription();
        this.time = System.currentTimeMillis() - 2500L;
        this.achievement = achievement;
        this.hint = true;
    }

    private void rescaleToast() {
        GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        this.width = this.mc.displayWidth;
        this.height = this.mc.displayHeight;
        ScreenRescaler scaler = new ScreenRescaler(this.mc.displayWidth, this.mc.displayHeight);
        this.width = scaler.getScaledWidth();
        this.height = scaler.getScaledHeight();
        GL11.glClear(256);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0, this.width, this.height, 0.0, 1000.0, 3000.0);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
    }

    private void drawItem(int x, int y) {
        GL11.glPushMatrix();
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        Lighting.enable();
        GL11.glPopMatrix();
        GL11.glDisable(2896);
        GL11.glEnable(32826);
        GL11.glEnable(2903);
        GL11.glEnable(2896);
        this.itemRender.zLevel = 100.0F;
        this.itemRender.renderItemSprite(this.mc.fontRenderer, this.mc.textureLoader, this.achievement.renderItem, x + 8, y + 8);
        this.itemRender.zLevel = 0.0F;
        GL11.glDisable(2896);
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
    }

    public void updateToast() {
        if (this.achievement != null && this.time != 0L) {
            double deltaTime = (System.currentTimeMillis() - this.time) / 3000.0;
            if (deltaTime >= 0.0 && deltaTime <= 1.0) {
                this.rescaleToast();
                GL11.glDisable(2929);
                GL11.glDepthMask(false);
                double timer = deltaTime * 2.0;
                if (timer > 1.0) {
                    timer = 2.0 - timer;
                }

                timer *= 4.0;
                timer = 1.0 - timer;
                if (timer < 0.0) {
                    timer = 0.0;
                }

                timer *= timer;
                timer *= timer;
                int x = this.width - 160 + (int)(timer * 160.0);
                int y = 0;
                int tex = this.mc.textureLoader.getTexture("/gui/achievement.png");
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glEnable(3553);
                GL11.glBindTexture(3553, tex);
                GL11.glDisable(2896);
                this.drawGraphic(x, y, 96, 202, 160, 32);
                if (this.achievement.getSpecial()) {
                    this.drawGraphic(x + 3, y + 3, 26, 202, 26, 26);
                } else if (this.achievement.getGoal()) {
                    this.drawGraphic(x + 3, y + 3, 52, 202, 26, 26);
                } else {
                    this.drawGraphic(x + 3, y + 3, 0, 202, 26, 26);
                }

                if (this.hint) {
                    this.drawSplitString(this.mc.fontRenderer, this.name, x + 30, y + 7, 120, -1, false, 0);
                } else {
                    this.drawStringNoShadow(
                        this.mc.fontRenderer,
                        this.type,
                        x + 30,
                        y + 7,
                        this.achievement.getGoal() ? 5636095 : (this.achievement.getSpecial() ? 16733695 : -256)
                    );
                    this.drawStringNoShadow(this.mc.fontRenderer, this.name, x + 30, y + 18, -1);
                }

                this.drawItem(x, y);
            } else {
                this.mc.sndManager.playSoundFX("random.out", 1.0F, 1.0F);
                this.time = 0L;
            }
        }
    }
}
