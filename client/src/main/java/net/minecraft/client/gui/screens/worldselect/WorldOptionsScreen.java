package net.minecraft.client.gui.screens.worldselect;

import java.util.Random;
import net.minecraft.SharedConstants;
import net.minecraft.client.gamemode.SurvivalMode;
import net.minecraft.client.gui.button.Button;
import net.minecraft.client.gui.button.LockerButton;
import net.minecraft.client.gui.button.TextField;
import net.minecraft.client.gui.screens.options.SettingsScreen;
import net.minecraft.client.gui.screens.title.TitleScreen;
import net.minecraft.game.world.WorldInfo;
import net.minecraft.game.world.WorldSettings;
import net.minecraft.game.world.WorldType;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.chunk.loader.format.SaveFormat;
import net.minecraft.localization.Translator;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class WorldOptionsScreen extends SettingsScreen {
    public WorldSettings settings = new WorldSettings();
    private Button customize;
    private Button general;
    private Button world;
    private Button more;
    private Button create;
    private Button cancel;
    private LockerButton locker;
    private TextField nameBox;
    private TextField seedBox;
    private String worldName = Translator.getInstance().translateKey("selectWorld.newWorld");
    protected String seed = "";
    private boolean createClicked;
    public int subScreen = 0;

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.nameBox = new TextField(this.fontRenderer, this.width / 2 - 100, 60, 200, 20);
        this.nameBox.setText(this.worldName);
        this.nameBox.setFocused(true);
        this.nameBox.setMaxStringLength(32);
        this.seedBox = new TextField(this.fontRenderer, this.width / 2 - 100, 60, 200, 20);
        this.seedBox.setText(this.seed);
        this.optionButtons = 5;
        this.refreshButtons();
        this.replaceInvalidChars();
        if (this.settings.singleBiomeId == null) {
            this.settings.singleBiomeId = Biome.findById(0);
        }
    }

    private void refreshButtons() {
        this.title = Translator.getInstance().translateKey("selectWorld.create");
        this.buttons.clear();
        int upper = 0;
        int lower = 3;
        if (this.subScreen == 1) {
            upper = 4;
            lower = 9;
        } else if (this.subScreen == 2) {
            upper = 10;
            lower = 19;
        }

        for (int index = upper; index <= lower; index++) {
            this.buttons
                .add(
                    new Button(
                        index,
                        this.width / 2 - 65 + index % 2 * 125,
                        this.height / 6 - 24 + 64 * (index >> 1),
                        60,
                        20,
                        WorldSettings.options[index].getOptionLabel(index).trim(),
                        this.settings.getOptionDesc(WorldSettings.options[index]).trim()
                    )
                );
        }

        this.buttons.add(this.general = new Button(100, this.width / 2 - 100, this.height - 50, 60, 20, "selectWorld.general"));
        this.buttons.add(this.world = new Button(101, this.width / 2 - 30, this.height - 50, 60, 20, "selectWorld.world"));
        this.buttons.add(this.more = new Button(102, this.width / 2 + 40, this.height - 50, 60, 20, "selectWorld.biomes"));
        this.buttons.add(this.create = new Button(103, this.width / 2 - 100, this.height - 25, 100, 20, "selectWorld.create"));
        this.buttons.add(this.cancel = new Button(104, this.width / 2 + 4, this.height - 25, 96, 20, "gui.cancel"));
        this.customize = new Button(105, this.width / 2 + 126, this.height / 2 - 22, 80, 20, "selectWorld.customize");
        this.locker = new LockerButton(106, this.width / 2 + 124, this.height / 2 - 9, "difficulty.lock");
        this.locker.locked = this.settings.difficultyLock;
        this.nameBox.setEnabled(false);
        this.nameBox.setVisible(false);
        this.seedBox.setEnabled(false);
        this.seedBox.setVisible(false);
        this.locker.visible = false;
        this.locker.enabled = false;
        this.customize.enabled = false;
        this.customize.visible = false;
        switch (this.subScreen) {
            case 0:
                this.nameBox.setEnabled(true);
                this.nameBox.setVisible(true);
                this.locker.visible = true;
                this.locker.enabled = true;
                this.general.enabled = false;
                break;
            case 1:
                this.seedBox.setEnabled(true);
                this.seedBox.setVisible(true);
                this.world.enabled = false;
                break;
            case 2:
                this.more.enabled = false;
                this.title = Translator.getInstance().translateKey("selectWorld.gameRules");
        }

        if (this.subScreen == 1) {
            this.customize.enabled = true;
            this.customize.visible = true;
        }

        this.optionSlot = new SettingsScreen.OptionsSlot(this.mc, this.width, this.height, 32, this.height - 58 + 2, 25, this.subScreen == 2 ? 4 : 30);
    }

    @Override
    protected void buttonClicked(Button button) {
        if (button.enabled) {
            if (button.id < 100) {
                this.settings.setOptionValue(WorldSettings.options[button.id], 1);
                this.refreshButtons();
            }

            if (button == this.general) {
                this.subScreen = 0;
                this.refreshButtons();
            }

            if (button == this.world) {
                this.subScreen = 1;
                this.refreshButtons();
            }

            if (button == this.more) {
                this.subScreen = 2;
                this.refreshButtons();
            }

            if (button == this.customize) {
                if (WorldSettings.worldType == WorldType.BIOME) {
                    this.mc.setScreen(new BiomeSelectionScreen(this));
                } else if (WorldSettings.worldType == WorldType.FLAT) {
                    this.mc.setScreen(new FlatCustomizationScreen(this));
                } else {
                    this.mc.setScreen(new BiomeToggleScreen(this));
                }
            }

            if (button == this.locker) {
                this.settings.difficultyLock = !this.settings.difficultyLock;
                this.locker.locked = this.settings.difficultyLock;
            }

            if (button == this.cancel) {
                this.mc.setScreen(new WorldSelectScreen(new TitleScreen()));
            }

            if (button == this.create) {
                this.mc.setScreen(null);
                if (this.createClicked) {
                    return;
                }

                this.createClicked = true;
                long seed = new Random().nextLong();
                String seedText = this.seedBox.getText();
                if (!MathHelper.stringNullOrLengthZero(seedText)) {
                    try {
                        long parsedSeed = Long.parseLong(seedText);
                        if (parsedSeed != 0L) {
                            seed = parsedSeed;
                        }
                    } catch (NumberFormatException cuntflap) {
                        seed = seedText.hashCode();
                    }
                }

                this.mc.gamemode = new SurvivalMode(this.mc);
                this.mc.startWorld(this.worldName, this.nameBox.getText(), seed, this.settings);
                this.mc.setScreen(null);
            }
        }
    }

    private void replaceInvalidChars() {
        this.worldName = this.nameBox.getText().trim();

        for (char shitter : SharedConstants.INVALID_FILENAME_CHARS) {
            this.worldName = this.worldName.replace(shitter, '_');
        }

        if (MathHelper.stringNullOrLengthZero(this.worldName)) {
            this.worldName = "A Nice World";
        }

        this.worldName = generateUnusedFolderName(this.mc.getSaveLoader(), this.worldName);
    }

    public static String generateUnusedFolderName(SaveFormat save, String name) {
        for (String unallowedNames : SharedConstants.ILLEGAL_WORLD_NAMES) {
            if (name.equalsIgnoreCase(unallowedNames)) {
                name = "_" + name + "_";
            }
        }

        while (save.getWorldInfo(name) != null) {
            name = name + "-";
        }

        return name;
    }

    @Override
    public void onClose() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void drawScreen(int x, int y, float tickDelta) {
        super.drawScreen(x, y, tickDelta);
        Translator translator = Translator.getInstance();
        if (this.subScreen == 0) {
            this.drawString(this.fontRenderer, translator.translateKey("selectWorld.enterName"), this.width / 2 - 100, 47, 10526880);
            this.drawString(this.fontRenderer, translator.translateKey("selectWorld.resultFolder") + " " + this.worldName, this.width / 2 - 100, 85, 10526880);
            this.nameBox.drawTextBox();
            this.locker.drawButton(this.mc, x, y);
        } else if (this.subScreen == 1) {
            this.drawString(this.fontRenderer, translator.translateKey("selectWorld.enterSeed"), this.width / 2 - 100, 47, 10526880);
            this.drawString(this.fontRenderer, translator.translateKey("selectWorld.seedInfo"), this.width / 2 - 100, 85, 10526880);
            this.seedBox.drawTextBox();
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, 0.0F, -200.0F);
            this.customize.drawButton(this.mc, x, y);
            GL11.glPopMatrix();
        }
    }

    @Override
    public String getOptionLabel(Button button) {
        String spacer = ": ";
        return Translator.getInstance().translateKey(this.settings.getOptionName(WorldSettings.options[button.id])).trim() + spacer;
    }

    @Override
    protected void keyTyped(char character, int key) {
        if (this.nameBox.isFocused()) {
            this.nameBox.textboxKeyTyped(character, key);
        } else if (this.seedBox.isFocused()) {
            this.seedBox.textboxKeyTyped(character, key);
        }

        if (character == '\r') {
            this.buttonClicked(this.create);
        }

        this.replaceInvalidChars();
    }

    @Override
    protected void mouseClicked(int x, int y, int click) {
        super.mouseClicked(x, y, click);
        this.nameBox.mouseClicked(x, y, click);
        this.seedBox.mouseClicked(x, y, click);
        if (this.customize.mousePressed(this.mc, x, y)) {
            this.selectedButton = this.customize;
            this.mc.sndManager.playSoundFX("random.click", 1.0F, 0.25F);
            this.buttonClicked(this.customize);
        }

        if (this.locker.mousePressed(this.mc, x, y)) {
            this.selectedButton = this.locker;
            this.mc.sndManager.playSoundFX("random.click", 1.0F, 0.25F);
            this.buttonClicked(this.locker);
        }
    }

    @Override
    public void update() {
        this.nameBox.updateCursor();
        this.seedBox.updateCursor();
        this.seed = this.seedBox.getText();
    }

    public void recreateWorld(WorldInfo world) {
        this.seed = world.getRandomSeed() + "";
        this.settings.biomes = world.getBiomes();
        this.settings.singleBiomeId = world.getSingleBiome();
        this.settings.difficultyLock = world.difficultyLock;
        WorldSettings.cheats = world.getCheats();
        WorldSettings.structures = world.generateStructures();
        WorldSettings.gamemode = world.getGamemode();
        WorldSettings.bonus = world.bonusHouse;
        WorldSettings.difficulty = world.getDifficulty();
        WorldSettings.height = world.getMaxSections() >= 16;
        WorldSettings.worldType = world.getType();
        WorldSettings.size = world.border;
        WorldSettings.permaDay = world.isPermaDay();
        WorldSettings.dayLength = (int)world.dayLength;
        WorldSettings.heightInt = WorldSettings.height ? 20 : 12;
        this.settings.layerOne = world.firstLayer;
        this.settings.layerTwo = world.secondLayer;
        this.settings.topLayer = world.topLayer;
        this.settings.bottomLayer = world.thirdLayer;
        this.settings.topBlock = world.thirdBlock;
        this.settings.blockOne = world.bottomBlock;
        this.settings.blockTwo = world.firstBlock;
        this.settings.bottomBlock = world.secondBlock;
    }
}
