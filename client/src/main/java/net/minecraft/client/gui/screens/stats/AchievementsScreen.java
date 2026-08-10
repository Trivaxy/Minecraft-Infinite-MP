package net.minecraft.client.gui.screens.stats;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.ScreenRescaler;
import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.button.CategoryButton;
import net.minecraft.client.gui.button.InvisibleButton;
import net.minecraft.client.gui.screens.GuiScreen;
import net.minecraft.client.render.Lighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.statistics.StatWriter;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.item.ItemList;
import net.minecraft.localization.Translator;
import net.minecraft.statistics.achievement.Achievement;
import net.minecraft.statistics.achievement.AchievementList;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class AchievementsScreen extends GuiScreen {
    private static final int MAP_TOP = AchievementList.minDisplayColumn * 24 - 56;
    private static final int MAP_LEFT = AchievementList.minDisplayRow * 24 - 56;
    private static final int MAP_BOTTOM = AchievementList.maxDisplayColumn * 24 - 56;
    private static final int MAP_RIGHT = AchievementList.maxDisplayRow * 24 - 56;
    private StatWriter statistics;
    private static List<Achievement> achievementList = new ArrayList<>();
    private String background = "backgroundStone";
    private CategoryButton mainCategory;
    private CategoryButton adventureCategory;
    private CategoryButton farmingCategory;
    private CategoryButton oceanCategory;
    private CategoryButton netherCategory;
    private CategoryButton aetherCategory;
    private CategoryButton voidCategory;
    protected int paneWidth = 256;
    protected int paneHeight = 144;
    protected int mouseX = 0;
    protected int mouseY = 0;
    private int isButtonDown = 0;
    protected double offsetX;
    protected double offsetY;
    protected double mapX;
    protected double mapY;
    protected double totalX;
    protected double totalY;

    public AchievementsScreen(StatWriter stats) {
        this.statistics = stats;
        this.centerMap();
    }

    private void centerMap() {
        this.offsetX = this.mapX = this.totalX = AchievementList.getHard.displayColumn * 24 - 70 - 12;
        this.offsetY = this.mapY = this.totalY = AchievementList.getHard.displayRow * 24 - 70 + 24;
    }

    @Override
    public void initGui() {
        achievementList = AchievementList.achievementList;
        this.buttons.clear();
        this.mainCategory = new CategoryButton(
            6, this.width / 2 - 120, (this.height - this.paneHeight) / 2 - 26, "achievement.overworld", BlockList.grass, 0, false
        );
        this.adventureCategory = new CategoryButton(
            5, this.width / 2 - 90, (this.height - this.paneHeight) / 2 - 26, "achievement.adventureList", ItemList.map, 0, false
        );
        this.farmingCategory = new CategoryButton(
            4, this.width / 2 - 60, (this.height - this.paneHeight) / 2 - 26, "achievement.husbandry", BlockList.hayBlock, 0, false
        );
        this.oceanCategory = new CategoryButton(
            3, this.width / 2 - 30, (this.height - this.paneHeight) / 2 - 26, "achievement.ocean", BlockList.seaLantern, 0, false
        );
        this.netherCategory = new CategoryButton(
            2, this.width / 2, (this.height - this.paneHeight) / 2 - 26, "achievement.nether", BlockList.netherrack, 0, false
        );
        this.aetherCategory = new CategoryButton(
            1, this.width / 2 + 30, (this.height - this.paneHeight) / 2 - 26, "achievement.aether", BlockList.stone, 8, false
        );
        this.voidCategory = new CategoryButton(
            0, this.width / 2 + 60, (this.height - this.paneHeight) / 2 - 26, "achievement.void", BlockList.voidRock, 0, false
        );
        this.buttons.add(this.voidCategory);
        this.buttons.add(this.aetherCategory);
        this.buttons.add(this.netherCategory);
        this.buttons.add(this.oceanCategory);
        this.buttons.add(this.farmingCategory);
        this.buttons.add(this.adventureCategory);
        this.buttons.add(this.mainCategory);
        this.buttons
            .add(
                new InvisibleButton(
                    100, (this.width - this.paneWidth) / 2 + this.paneWidth - 18, (this.height - this.paneHeight) / 2 + 4, 10, 10, "x", "gui.close"
                )
            );
        this.mainCategory.selected = true;
    }

    @Override
    protected void buttonClicked(Button button) {
        if (button.id == 6) {
            achievementList = AchievementList.achievementList;
            this.background = "backgroundStone";
        }

        if (button.id == 5) {
            achievementList = AchievementList.adventureList;
            this.background = "backgroundAdventure";
        }

        if (button.id == 4) {
            achievementList = AchievementList.husbandryList;
            this.background = "backgroundFarming";
        }

        if (button.id == 3) {
            achievementList = AchievementList.oceanList;
            this.background = "backgroundOcean";
        }

        if (button.id == 2) {
            achievementList = AchievementList.netherList;
            this.background = "backgroundNether";
        }

        if (button.id == 1) {
            achievementList = AchievementList.aetherList;
            this.background = "backgroundAether";
        }

        if (button.id == 0) {
            achievementList = AchievementList.voidList;
            this.background = "backgroundVoid";
        }

        if (button.id == 100) {
            this.mc.setScreen(null);
            this.mc.setFocus();
        }

        for (int i = 0; i < this.buttons.size() - 1; i++) {
            ((CategoryButton)this.buttons.get(i)).selected = false;
        }

        if (button.id < this.buttons.size() - 1) {
            ((CategoryButton)this.buttons.get(button.id)).selected = true;
        }

        this.centerMap();
    }

    @Override
    public void drawScreen(int x, int y, float deltaTick) {
        this.handleMouseDrag(x, y);
        this.drawBackground();
        this.renderAchievementMap(x, y, deltaTick);
    }

    @Override
    public void update() {
        this.offsetX = this.mapX;
        this.offsetY = this.mapY;
        double lerpX = this.totalX - this.mapX;
        double lerpY = this.totalY - this.mapY;
        if (lerpX * lerpX + lerpY * lerpY < 4.0) {
            this.mapX += lerpX;
            this.mapY += lerpY;
        } else {
            this.mapX += lerpX * 0.85;
            this.mapY += lerpY * 0.85;
        }
    }

    @Override
    public boolean shouldPause() {
        return true;
    }

    protected void drawText() {
        int x = (this.width - this.paneWidth) / 2;
        int y = (this.height - this.paneHeight) / 2;
        this.drawStringNoShadow(this.fontRenderer, Translator.getInstance().translateKey("gui.achievements"), x + 10, y + 5, 4210752);
    }

    private void handleMouseDrag(int x, int y) {
        if (Mouse.isButtonDown(0)) {
            int centerX = (this.width - this.paneWidth) / 2;
            int centerY = (this.height - this.paneHeight) / 2;
            int maxX = centerX + 8;
            int maxY = centerY + 17;
            boolean inBounds = x >= maxX && x < maxX + 224 && y >= maxY && y < maxY + 155;
            if ((this.isButtonDown == 0 || this.isButtonDown == 1) && inBounds) {
                if (this.isButtonDown == 0) {
                    this.isButtonDown = 1;
                } else {
                    this.mapX = this.mapX - (x - this.mouseX);
                    this.mapY = this.mapY - (y - this.mouseY);
                    this.totalX = this.offsetX = this.mapX;
                    this.totalY = this.offsetY = this.mapY;
                }

                this.mouseX = x;
                this.mouseY = y;
            }

            this.totalX = MathHelper.clamp(this.totalX, MAP_TOP, MAP_BOTTOM);
            this.totalY = MathHelper.clamp(this.totalY, MAP_LEFT, MAP_RIGHT);
        } else {
            this.isButtonDown = 0;
        }
    }

    protected void renderAchievementMap(int x, int y, float deltaTick) {
        ScreenRescaler scaledResolution = new ScreenRescaler(this.mc.displayWidth, this.mc.displayHeight);
        int factor = scaledResolution.scaleFactor;
        int areaX = MathHelper.floor_double(this.offsetX + (this.mapX - this.offsetX) * deltaTick);
        int areaY = MathHelper.floor_double(this.offsetY + (this.mapY - this.offsetY) * deltaTick);
        areaX = MathHelper.clamp(areaX, MAP_TOP, MAP_BOTTOM);
        areaY = MathHelper.clamp(areaY, MAP_LEFT, MAP_RIGHT);
        int guiTex = this.mc.textureLoader.getTexture("/gui/achievement.png");
        int centerX = (this.width - this.paneWidth) / 2;
        int centerY = (this.height - this.paneHeight) / 2;
        int maxX = centerX + 8;
        int maxY = centerY + 17;
        this.zLevel = 0.0F;
        GL11.glDepthFunc(518);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.0F, -200.0F);
        GL11.glEnable(3553);
        GL11.glDisable(2896);
        GL11.glEnable(32826);
        GL11.glEnable(2903);
        GL11.glEnable(3089);
        GL11.glScissor((this.width - this.paneWidth + 8) / 2 * factor, (this.height - this.paneHeight + 8) / 2 * factor, 250 * factor, 149 * factor);
        this.renderBackground(areaX, areaY, maxX, maxY);
        GL11.glEnable(2929);
        GL11.glDepthFunc(515);
        GL11.glDisable(3553);
        this.renderConnectingLines(areaX, areaY, maxX, maxY);
        GL11.glPushMatrix();
        GL11.glRotatef(120.0F, 1.0F, 0.0F, 0.0F);
        Lighting.enable();
        GL11.glPopMatrix();
        GL11.glDisable(2896);
        GL11.glEnable(32826);
        GL11.glEnable(2903);
        this.renderAchievements(areaX, areaY, maxX, maxY, x, y, guiTex);
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3089);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.textureLoader.bindTexture(guiTex);
        this.drawGraphic(centerX, centerY, 0, 0, this.paneWidth, this.paneHeight);
        GL11.glPopMatrix();
        GL11.glDepthFunc(515);
        GL11.glDisable(2929);
        GL11.glEnable(3553);
        this.drawText();
        super.drawScreen(x, y, deltaTick);
        GL11.glEnable(2929);
        GL11.glEnable(2896);
        Lighting.disable();
    }

    private void renderAchievements(int areaX, int areaY, int maxX, int maxY, int x, int y, int guiTex) {
        Achievement selectedAchievement = null;
        ItemRenderer itemRenderer = new ItemRenderer();

        for (int index = 0; index < achievementList.size(); index++) {
            Achievement achievement = achievementList.get(index);
            int achievementX = achievement.displayColumn * 24 - areaX;
            int achievementY = achievement.displayRow * 24 - areaY;
            if (achievementX >= -24 && achievementY >= -24 && achievementX <= 250 && achievementY <= 250) {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.mc.textureLoader.bindTexture(guiTex);
                int xBound = maxX + achievementX;
                int yBound = maxY + achievementY;
                int height = 202;
                if (x >= maxX && y >= maxY && x < maxX + 250 && y < maxY + 250 && x >= xBound && x <= xBound + 22 && y >= yBound && y <= yBound + 22) {
                    selectedAchievement = achievement;
                }

                if (selectedAchievement != null && selectedAchievement == achievement) {
                    height -= 28;
                } else if (this.statistics.hasAchievementUnlocked(achievement)) {
                    height += 28;
                }

                GL11.glTranslated(0.0, 0.0, 2.0);
                if (achievement.getSpecial()) {
                    this.drawGraphic(xBound - 2, yBound - 2, 26, height, 26, 26);
                } else if (achievement.getGoal()) {
                    this.drawGraphic(xBound - 2, yBound - 2, 52, height, 26, 26);
                } else {
                    this.drawGraphic(xBound - 2, yBound - 2, 0, height, 26, 26);
                }

                GL11.glEnable(2896);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                itemRenderer.zLevel = 100.0F;
                itemRenderer.renderItemSprite(this.mc.fontRenderer, this.mc.textureLoader, achievement.renderItem, xBound + 3, yBound + 3);
                itemRenderer.zLevel = 0.0F;
                GL11.glDisable(2896);
                GL11.glTranslated(0.0, 0.0, -2.0);
            }
        }

        if (selectedAchievement != null) {
            String name = Translator.getInstance().translateKey(selectedAchievement.getName());
            String description = Translator.getInstance().translateKey(selectedAchievement.getDescription());
            int mouseX = x + 12;
            int mouseY = y - 4;
            int strWidth = Math.max(this.fontRenderer.getStringWidth(name), 120);
            int splitWidth = this.fontRenderer.getMaximumStringWidth(description, strWidth);
            if (this.statistics.hasAchievementUnlocked(selectedAchievement)) {
                splitWidth += 12;
            }

            int color = 16777045;
            if (selectedAchievement.getSpecial()) {
                color = 16733695;
            } else if (selectedAchievement.getGoal()) {
                color = 5636095;
            }

            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, 0.0F, 200.0F);
            this.drawGradient(mouseX - 3, mouseY - 3, mouseX + strWidth + 3, mouseY + splitWidth + 3 + 12, 1610941696, -1607454624, 1.5F);
            this.drawSplitString(this.fontRenderer, description, mouseX, mouseY + 12, strWidth, -1, false, 1);
            this.drawString(this.fontRenderer, name, mouseX, mouseY, color);
            if (this.statistics.hasAchievementUnlocked(selectedAchievement)) {
                this.drawString(this.fontRenderer, Translator.getInstance().translateKey("achievement.taken"), mouseX, mouseY + splitWidth + 4, 5635925);
            }

            GL11.glPopMatrix();
        }
    }

    private void renderConnectingLines(int areaX, int areaY, int maxX, int maxY) {
        for (int minU = 0; minU < achievementList.size(); minU++) {
            Achievement achievement = achievementList.get(minU);
            if (achievement.parentAchievement != null) {
                int startX = achievement.displayColumn * 24 - areaX + 11 + maxX;
                int startY = achievement.displayRow * 24 - areaY + 11 + maxY;
                int endX = achievement.parentAchievement.displayColumn * 24 - areaX + 11 + maxX;
                int endY = achievement.parentAchievement.displayRow * 24 - areaY + 11 + maxY;
                boolean unlocked = this.statistics.hasAchievementUnlocked(achievement);
                if (achievement.parentAchievement != null && !this.statistics.hasAchievementUnlocked(achievement.parentAchievement)) {
                    unlocked = false;
                }

                int txtColor;
                if (unlocked) {
                    txtColor = -3631853;
                } else {
                    txtColor = -16777216;
                }

                if (unlocked) {
                    GL11.glTranslated(0.0, 0.0, 1.0);
                    this.drawHorizontalLine(startX, endX, startY, txtColor);
                    this.drawVerticalLine(endX, startY, endY, txtColor);
                    GL11.glTranslated(0.0, 0.0, -1.0);
                } else {
                    this.drawHorizontalLine(startX, endX, startY, txtColor);
                    this.drawVerticalLine(endX, startY, endY, txtColor);
                }
            }
        }
    }

    private void renderBackground(int areaX, int areaY, int maxX, int maxY) {
        int backgroundTex = this.mc.textureLoader.getTexture("/gui/background/" + this.background + ".png");
        this.mc.textureLoader.bindTexture(backgroundTex);
        int maxU = (areaX / 4 + 288) % 16;
        int maxV = (areaY / 4 + 288) % 16;

        for (int tileV = 0; tileV * 16 - maxV < 250; tileV++) {
            GL11.glColor4f(0.6F, 0.6F, 0.6F, 1.0F);

            for (int tileU = 0; tileU * 16 - maxU < 250; tileU++) {
                this.drawTiledGraphic(maxX + tileU * 16 - maxU, maxY + tileV * 16 - maxV, 0, 0, 16, 16, 0.0625F, 0.0625F);
            }
        }
    }

    public void drawTiledGraphic(int x, int y, int minU, int minV, int maxU, int maxV, float vOffset, float uOffset) {
        Tessellator tess = Tessellator.instance;
        tess.begin();
        tess.addTexturedVertex(x + 0, y + maxV, this.zLevel, (minU + 0) * vOffset, (minV + maxV) * uOffset);
        tess.addTexturedVertex(x + maxU, y + maxV, this.zLevel, (minU + maxU) * vOffset, (minV + maxV) * uOffset);
        tess.addTexturedVertex(x + maxU, y + 0, this.zLevel, (minU + maxU) * vOffset, (minV + 0) * uOffset);
        tess.addTexturedVertex(x + 0, y + 0, this.zLevel, (minU + 0) * vOffset, (minV + 0) * uOffset);
        tess.draw();
    }
}
