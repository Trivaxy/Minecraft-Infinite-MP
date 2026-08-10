package net.minecraft.client.options;

import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSettings;
import net.minecraft.localization.Translator;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.property.PropertiesConfig;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

public class OptionsList {
    private OptionsList.OptionsIO io;
    private static final String ON = "options.on";
    private static final String OFF = "options.off";
    private static final String FANCY = "options.fancy";
    private static final String FAST = "options.fast";
    private static final String CLASSIC = "options.classic";
    private static final String ALPHA = "options.alpha";
    private static final String[] GUI_SCALES = new String[]{
        "options.guiScale.auto", "options.guiScale.tiny", "options.guiScale.small", "options.guiScale.normal", "options.guiScale.large"
    };
    private static final String[] PARTICLES = new String[]{"options.all", "options.minimal", "options.none"};
    private static final String[] CLOUDS = new String[]{"options.fabulous", "options.fancy", "options.fast", "options.none"};
    private static final String[] SKIES = new String[]{"options.skybox.default", "options.skybox.original", "options.skybox.classic"};
    private static final String[] RENDER_DISTANCES = new String[]{
        "options.renderDistance.tiny",
        "options.renderDistance.short",
        "options.renderDistance.normal",
        "options.renderDistance.far",
        "options.renderDistance.veryFar"
    };
    private static final String[] VIEWS = new String[]{"options.view.thirdperson", "options.view.frontfacing", "options.view.all"};
    public static final int MAX_OPTIONS_AMOUNT = 46;
    public static final Option<?>[] options = new Option[46];
    public static final int MAX_KEYBIND_COUNT = 41;
    public static final KeyBind[] allKeys = new KeyBind[41];
    public String texturePack = "default";
    public String languagePack = "default";
    public String lastServer = "";
    public static final Option<?> FOV = new Option<>(
        "fov",
        Option.Type.SLIDER,
        Option.Category.GENERAL,
        () -> (OptionValues.fov - 70.0F) / 50.0F,
        v -> OptionValues.fov = (int)(70.0F + v * 50.0F),
        v -> {
            if (OptionValues.fov <= 70.0F) {
                return translateString("options.fov") + ": " + translateString("options.fov.min");
            } else {
                return OptionValues.fov >= 120.0F
                    ? translateString("options.fov") + ": " + translateString("options.fov.max")
                    : translateString("options.fov") + ": " + (int)OptionValues.fov;
            }
        }
    );
    public static final Option<?> SENSITIVITY = new Option<>(
        "sensitivity",
        Option.Type.SLIDER,
        Option.Category.GENERAL,
        () -> OptionValues.sensitivity,
        v -> OptionValues.sensitivity = v,
        v -> {
            if (OptionValues.sensitivity <= 0.0F) {
                return translateString("options.sensitivity") + ": " + translateString("options.sensitivity.min");
            } else {
                return OptionValues.sensitivity >= 1.0F
                    ? translateString("options.sensitivity") + ": " + translateString("options.sensitivity.max")
                    : translateString("options.sensitivity") + ": " + (int)(OptionValues.sensitivity * 200.0F) + "%";
            }
        }
    );
    public static final Option<?> INVERT_MOUSE = new Option<>(
        "invert",
        Option.Type.TOGGLE,
        Option.Category.GENERAL,
        () -> OptionValues.invertMouse,
        v -> OptionValues.invertMouse = v,
        v -> OptionValues.invertMouse ? "options.on" : "options.off"
    );
    public static final Option<?> PICKUPS = new Option<>(
        "pickup",
        Option.Type.TOGGLE,
        Option.Category.GENERAL,
        () -> OptionValues.showItems,
        v -> OptionValues.showItems = v,
        v -> OptionValues.showItems ? "options.on" : "options.off"
    );
    public static final Option<?> SCROLL_INVERT = new Option<>(
        "scroll",
        Option.Type.TOGGLE,
        Option.Category.GENERAL,
        () -> OptionValues.scrollInvert,
        v -> OptionValues.scrollInvert = v,
        v -> OptionValues.scrollInvert ? "options.on" : "options.off"
    );
    public static final Option<?> SNEAK_MODE = new Option<>(
        "sneak",
        Option.Type.TOGGLE,
        Option.Category.GENERAL,
        () -> OptionValues.sneakMode,
        v -> OptionValues.sneakMode = v,
        v -> OptionValues.sneakMode ? "options.hold" : "options.toggle"
    );
    public static final Option<?> CURSOR = new Option<>("cursor", Option.Type.TOGGLE, Option.Category.GENERAL, () -> OptionValues.mouseCursor, v -> {
        OptionValues.mouseCursor = v;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.mouseHelper != null) {
            if (OptionValues.mouseCursor) {
                mc.mouseHelper.setCursor(1);
            } else {
                mc.mouseHelper.setCursor(0);
            }
        }
    }, v -> OptionValues.mouseCursor ? "options.on" : "options.off");
    public static final Option<?> DIFFICULTY = new Option<>("difficulty", Option.Type.CHOICE, Option.Category.GENERAL, () -> OptionValues.difficulty, v -> {
        OptionValues.difficulty = v;
        if (OptionValues.difficulty > 3) {
            OptionValues.difficulty = 0;
        }

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world != null) {
            mc.world.settings.setDifficulty((byte)OptionValues.difficulty);
            OptionValues.difficulty = mc.world.settings.getDifficulty();
        }
    }, v -> WorldSettings.DIFFICULTIES[Minecraft.getMinecraft().world != null ? Minecraft.getMinecraft().world.settings.getDifficulty() : 2]);
    public static final Option<?> BRIGHTNESS = new Option<>(
        "gamma",
        Option.Type.SLIDER,
        Option.Category.GRAPHICS,
        () -> OptionValues.gamma,
        v -> OptionValues.gamma = v,
        v -> {
            if (OptionValues.gamma <= 0.0F) {
                return translateString("options.gamma") + ": " + translateString("options.gamma.min");
            } else {
                return OptionValues.gamma >= 1.0F
                    ? translateString("options.gamma") + ": " + translateString("options.gamma.max")
                    : translateString("options.gamma") + ": " + (int)(OptionValues.gamma * 100.0F) + "%";
            }
        }
    );
    public static final Option<?> RENDER_DIST = new Option<>(
        "renderDistance",
        Option.Type.SLIDER,
        Option.Category.GRAPHICS,
        () -> (OptionValues.renderDistance - 1) / 31.0F,
        v -> OptionValues.renderDistance = Math.round(v * 31.0F) + 1,
        v -> translateString("options.renderDistance") + ": " + OptionValues.renderDistance + " " + translateString("options.chunks")
    );
    public static final Option<?> OFFSETS = new Option<>(
        "offsets",
        Option.Type.SLIDER,
        Option.Category.GRAPHICS,
        () -> OptionValues.offsetAmount,
        v -> {
            OptionValues.offsetAmount = v;
            reloadRenderers();
        },
        v -> OptionValues.offsetAmount == 0.0F
            ? translateString("options.offsets") + ": " + translateString("options.off")
            : translateString("options.offsets") + ": " + (int)(OptionValues.offsetAmount * 100.0F) + "%"
    );
    public static final Option<?> SIM_DIST = new Option<>(
        "entityDistance", Option.Type.SLIDER, Option.Category.GRAPHICS, () -> (OptionValues.simDistance - 4) / 28.0F, v -> {
            OptionValues.simDistance = Math.round(v * 28.0F) + 4;
            if (Minecraft.getMinecraft().world != null) {
                Minecraft.getMinecraft().world.simDistance = OptionValues.simDistance;
            }
        }, v -> translateString("options.entityDistance") + ": " + OptionValues.simDistance + " " + translateString("options.chunks")
    );
    public static final Option<?> BLUR = new Option<>(
        "blur",
        Option.Type.SLIDER,
        Option.Category.GRAPHICS,
        () -> OptionValues.blurAmount,
        v -> OptionValues.blurAmount = v,
        v -> OptionValues.blurAmount == 0.0F
            ? translateString("options.blur") + ": " + translateString("options.off")
            : translateString("options.blur") + ": " + (int)(OptionValues.blurAmount * 100.0F) + "%"
    );
    public static final Option<?> ENTITY_DIST = new Option<>(
        "entity",
        Option.Type.SLIDER,
        Option.Category.GRAPHICS,
        () -> Entity.renderDistance,
        v -> Entity.renderDistance = v,
        v -> translateString("options.entity") + ": " + translateString(RENDER_DISTANCES[(int)(Entity.renderDistance * 4.0F)])
    );
    public static final Option<?> FULLSCREEN = new Option<>(
        "fullscr",
        Option.Type.TOGGLE,
        Option.Category.GRAPHICS,
        () -> OptionValues.fullScreen,
        v -> OptionValues.fullScreen = v,
        v -> OptionValues.fullScreen ? "options.on" : "options.off"
    );
    public static final Option<?> GUI_SCALE = new Option<>("scale", Option.Type.CHOICE, Option.Category.GRAPHICS, () -> OptionValues.guiScale, v -> {
        OptionValues.guiScale = v;
        if (OptionValues.guiScale > 4) {
            OptionValues.guiScale = 0;
        }
    }, v -> GUI_SCALES[OptionValues.guiScale]);
    public static final Option<?> VSYNC = new Option<>("vsync", Option.Type.TOGGLE, Option.Category.GRAPHICS, () -> OptionValues.limitFramerate, v -> {
        OptionValues.limitFramerate = v;
        Display.setVSyncEnabled(OptionValues.limitFramerate);
    }, v -> OptionValues.limitFramerate ? "options.on" : "options.off");
    public static final Option<?> MIPMAPPING = new Option<>("mipmapping", Option.Type.CHOICE, Option.Category.GRAPHICS, () -> OptionValues.mipmapping, v -> {
        OptionValues.mipmapping = v;
        if (OptionValues.mipmapping > 4) {
            OptionValues.mipmapping = 0;
        }

        refreshTextures();
    }, v -> OptionValues.mipmapping == 0 ? "options.off" : OptionValues.mipmapping + "x");
    public static final Option<?> ANIMATIONS = new Option<>(
        "animations", Option.Type.TOGGLE, Option.Category.GRAPHICS, () -> OptionValues.animatedTextures, v -> {
            OptionValues.animatedTextures = v;
            refreshTextures();
        }, v -> OptionValues.animatedTextures ? "options.on" : "options.off"
    );
    public static final Option<?> ROTATIONS = new Option<>(
        "textureRotation", Option.Type.TOGGLE, Option.Category.GRAPHICS, () -> OptionValues.textureRotation, v -> {
            OptionValues.textureRotation = v;
            reloadRenderers();
        }, v -> OptionValues.textureRotation ? "options.on" : "options.off"
    );
    public static final Option<?> LIGHTING = new Option<>("ao", Option.Type.TOGGLE, Option.Category.GRAPHICS, () -> OptionValues.smoothLighting, v -> {
        OptionValues.smoothLighting = v;
        reloadRenderers();
    }, v -> OptionValues.smoothLighting ? "options.on" : "options.off");
    public static final Option<?> TORCH_FLICKER = new Option<>(
        "torchFlicker",
        Option.Type.TOGGLE,
        Option.Category.GRAPHICS,
        () -> OptionValues.torchFlicker,
        v -> OptionValues.torchFlicker = v,
        v -> OptionValues.torchFlicker ? "options.on" : "options.off"
    );
    public static final Option<?> DYNAMIC = new Option<>("lighting", Option.Type.TOGGLE, Option.Category.GRAPHICS, () -> OptionValues.dynamicLighting, v -> {
        OptionValues.dynamicLighting = v;
        restoreLighting();
    }, v -> OptionValues.dynamicLighting ? "options.on" : "options.off");
    public static final Option<?> SHADOWS = new Option<>(
        "shadows",
        Option.Type.TOGGLE,
        Option.Category.GRAPHICS,
        () -> OptionValues.enableShadows,
        v -> OptionValues.enableShadows = v,
        v -> OptionValues.enableShadows ? "options.on" : "options.off"
    );
    public static final Option<?> PARTICLES_AMOUNT = new Option<>(
        "particles", Option.Type.CHOICE, Option.Category.GRAPHICS, () -> OptionValues.particleCount, v -> {
            OptionValues.particleCount = v;
            if (OptionValues.particleCount >= 3) {
                OptionValues.particleCount = 0;
            }
        }, v -> PARTICLES[OptionValues.particleCount]
    );
    public static final Option<?> EFFECTS = new Option<>(
        "effects",
        Option.Type.TOGGLE,
        Option.Category.GRAPHICS,
        () -> OptionValues.fancyGraphics,
        v -> OptionValues.fancyGraphics = v,
        v -> OptionValues.fancyGraphics ? "options.fancy" : "options.fast"
    );
    public static final Option<?> PARTICLE_STYLE = new Option<>(
        "particle",
        Option.Type.TOGGLE,
        Option.Category.GRAPHICS,
        () -> OptionValues.fancyParticles,
        v -> OptionValues.fancyParticles = v,
        v -> OptionValues.fancyParticles ? "options.fancy" : "options.fast"
    );
    public static final Option<?> ITEM_STYLE = new Option<>(
        "items",
        Option.Type.TOGGLE,
        Option.Category.GRAPHICS,
        () -> OptionValues.fancyItems,
        v -> OptionValues.fancyItems = v,
        v -> OptionValues.fancyItems ? "options.fancy" : "options.fast"
    );
    public static final Option<?> TEXTURES = new Option<>("leaves", Option.Type.TOGGLE, Option.Category.GRAPHICS, () -> OptionValues.fancyTextures, v -> {
        OptionValues.fancyTextures = v;
        reloadRenderers();
    }, v -> OptionValues.fancyTextures ? "options.fancy" : "options.fast");
    public static final Option<?> CLOUD_STYLE = new Option<>("clouds", Option.Type.CHOICE, Option.Category.GRAPHICS, () -> OptionValues.cloudStyle, v -> {
        OptionValues.cloudStyle = v;
        if (OptionValues.cloudStyle >= 4) {
            OptionValues.cloudStyle = 0;
        }
    }, v -> CLOUDS[OptionValues.cloudStyle]);
    public static final Option<?> SKY_STYLE = new Option<>("skybox", Option.Type.CHOICE, Option.Category.GRAPHICS, () -> OptionValues.skyStyle, v -> {
        OptionValues.skyStyle = v;
        if (OptionValues.skyStyle > 2) {
            OptionValues.skyStyle = 0;
        }
    }, v -> SKIES[OptionValues.skyStyle]);
    public static final Option<?> STARS = new Option<>(
        "stars",
        Option.Type.TOGGLE,
        Option.Category.GRAPHICS,
        () -> OptionValues.showStars,
        v -> OptionValues.showStars = v,
        v -> OptionValues.showStars ? "options.on" : "options.off"
    );
    public static final Option<?> FOG = new Option<>(
        "fog",
        Option.Type.TOGGLE,
        Option.Category.GRAPHICS,
        () -> OptionValues.showFog,
        v -> OptionValues.showFog = v,
        v -> OptionValues.showFog ? "options.on" : "options.off"
    );
    public static final Option<?> VIEW_BOBBING = new Option<>(
        "bobbing",
        Option.Type.TOGGLE,
        Option.Category.GRAPHICS,
        () -> OptionValues.viewBobbing,
        v -> OptionValues.viewBobbing = v,
        v -> OptionValues.viewBobbing ? "options.on" : "options.off"
    );
    public static final Option<?> FOV_EFFECTS = new Option<>(
        "fovEffects",
        Option.Type.TOGGLE,
        Option.Category.GRAPHICS,
        () -> OptionValues.fovEffects,
        v -> OptionValues.fovEffects = v,
        v -> OptionValues.fovEffects ? "options.on" : "options.off"
    );
    public static final Option<?> SHOW_ARM = new Option<>(
        "arm",
        Option.Type.TOGGLE,
        Option.Category.GRAPHICS,
        () -> OptionValues.showArm,
        v -> OptionValues.showArm = v,
        v -> OptionValues.showArm ? "options.classic" : "options.fancy"
    );
    public static final Option<?> VIEW = new Option<>("view", Option.Type.CHOICE, Option.Category.GRAPHICS, () -> OptionValues.cameraViews, v -> {
        OptionValues.cameraViews = v;
        if (OptionValues.cameraViews > 2) {
            OptionValues.cameraViews = 0;
        }
    }, v -> VIEWS[OptionValues.cameraViews]);
    public static final Option<?> ARM_ANIMATION = new Option<>(
        "anim",
        Option.Type.TOGGLE,
        Option.Category.GRAPHICS,
        () -> OptionValues.armFlailing,
        v -> OptionValues.armFlailing = v,
        v -> OptionValues.armFlailing ? "options.classic" : "options.alpha"
    );
    public static final Option<?> OUTLINE = new Option<>(
        "outline",
        Option.Type.TOGGLE,
        Option.Category.GRAPHICS,
        () -> OptionValues.showOutline,
        v -> OptionValues.showOutline = v,
        v -> OptionValues.showOutline ? "options.on" : "options.off"
    );
    public static final Option<?> MUSIC = new Option<>(
        "music",
        Option.Type.SLIDER,
        Option.Category.AUDIO,
        () -> OptionValues.musicVol,
        v -> {
            OptionValues.musicVol = v;
            if (Minecraft.getMinecraft().sndManager.sndSystem != null) {
                Minecraft.getMinecraft().sndManager.sndSystem.setVolume("BgMusic", OptionValues.musicVol * OptionValues.masterVol);
            }
        },
        v -> OptionValues.musicVol == 0.0F
            ? translateString("options.music") + ": " + translateString("options.off")
            : translateString("options.music") + ": " + (int)(OptionValues.musicVol * 100.0F) + "%"
    );
    public static final Option<?> SOUND = new Option<>(
        "sound",
        Option.Type.SLIDER,
        Option.Category.AUDIO,
        () -> OptionValues.soundVol,
        v -> OptionValues.soundVol = v,
        v -> OptionValues.soundVol == 0.0F
            ? translateString("options.sound") + ": " + translateString("options.off")
            : translateString("options.sound") + ": " + (int)(OptionValues.soundVol * 100.0F) + "%"
    );
    public static final Option<?> MOBS = new Option<>(
        "mobs",
        Option.Type.SLIDER,
        Option.Category.AUDIO,
        () -> OptionValues.mobVol,
        v -> OptionValues.mobVol = v,
        v -> OptionValues.mobVol == 0.0F
            ? translateString("options.mobs") + ": " + translateString("options.off")
            : translateString("options.mobs") + ": " + (int)(OptionValues.mobVol * 100.0F) + "%"
    );
    public static final Option<?> BLOCKS = new Option<>(
        "blocks",
        Option.Type.SLIDER,
        Option.Category.AUDIO,
        () -> OptionValues.blocksVol,
        v -> OptionValues.blocksVol = v,
        v -> OptionValues.blocksVol == 0.0F
            ? translateString("options.blocks") + ": " + translateString("options.off")
            : translateString("options.blocks") + ": " + (int)(OptionValues.blocksVol * 100.0F) + "%"
    );
    public static final Option<?> AMBIENCE = new Option<>(
        "ambience",
        Option.Type.SLIDER,
        Option.Category.AUDIO,
        () -> OptionValues.ambienceVol,
        v -> OptionValues.ambienceVol = v,
        v -> OptionValues.ambienceVol == 0.0F
            ? translateString("options.ambience") + ": " + translateString("options.off")
            : translateString("options.ambience") + ": " + (int)(OptionValues.ambienceVol * 100.0F) + "%"
    );
    public static final Option<?> JUKEBOX = new Option<>(
        "jukebox",
        Option.Type.SLIDER,
        Option.Category.AUDIO,
        () -> OptionValues.streamingVol,
        v -> {
            OptionValues.streamingVol = v;
            if (Minecraft.getMinecraft().sndManager.sndSystem != null) {
                Minecraft.getMinecraft().sndManager.sndSystem.setVolume("streaming", OptionValues.streamingVol * OptionValues.masterVol);
            }
        },
        v -> OptionValues.streamingVol == 0.0F
            ? translateString("options.jukebox") + ": " + translateString("options.off")
            : translateString("options.jukebox") + ": " + (int)(OptionValues.streamingVol * 100.0F) + "%"
    );
    public static final Option<?> MASTER = new Option<>(
        "master",
        Option.Type.SLIDER,
        Option.Category.AUDIO,
        () -> OptionValues.masterVol,
        v -> {
            OptionValues.masterVol = v;
            if (Minecraft.getMinecraft().sndManager.sndSystem != null) {
                Minecraft.getMinecraft().sndManager.sndSystem.setVolume("BgMusic", OptionValues.musicVol * OptionValues.masterVol);
                Minecraft.getMinecraft().sndManager.sndSystem.setVolume("streaming", OptionValues.streamingVol * OptionValues.masterVol);
            }
        },
        v -> OptionValues.masterVol == 0.0F
            ? translateString("options.master") + ": " + translateString("options.off")
            : translateString("options.master") + ": " + (int)(OptionValues.masterVol * 100.0F) + "%"
    );
    public KeyBind forwardKey = new KeyBind("key.forward", 17, KeyBind.Category.MOVEMENT);
    public KeyBind backwardKey = new KeyBind("key.back", 31, KeyBind.Category.MOVEMENT);
    public KeyBind leftKey = new KeyBind("key.left", 30, KeyBind.Category.MOVEMENT);
    public KeyBind rightKey = new KeyBind("key.right", 32, KeyBind.Category.MOVEMENT);
    public KeyBind jumpKey = new KeyBind("key.jump", 57, KeyBind.Category.MOVEMENT);
    public KeyBind sneakKey = new KeyBind("key.sneak", 42, KeyBind.Category.MOVEMENT);
    public KeyBind crawlKey = new KeyBind("key.crawl", 29, KeyBind.Category.MOVEMENT);
    public KeyBind sprintKey = new KeyBind("key.sprint", 19, KeyBind.Category.MOVEMENT);
    public KeyBind flightKey = new KeyBind("key.flight", 46, KeyBind.Category.MOVEMENT);
    public KeyBind attackKey = new KeyBind("key.attack", -100, KeyBind.Category.GAMEPLAY);
    public KeyBind useKey = new KeyBind("key.use", -99, KeyBind.Category.GAMEPLAY);
    public KeyBind pickKey = new KeyBind("key.pickItem", -98, KeyBind.Category.GAMEPLAY);
    public KeyBind inventoryKey = new KeyBind("key.inventory", 18, KeyBind.Category.INVENTORY);
    public KeyBind dropKey = new KeyBind("key.drop", 16, KeyBind.Category.INVENTORY);
    public KeyBind swapKey = new KeyBind("key.swap", 33, KeyBind.Category.INVENTORY);
    public KeyBind firstItemKey = new KeyBind("key.item1", 2, KeyBind.Category.INVENTORY);
    public KeyBind secondItemKey = new KeyBind("key.item2", 3, KeyBind.Category.INVENTORY);
    public KeyBind thirdItemKey = new KeyBind("key.item3", 4, KeyBind.Category.INVENTORY);
    public KeyBind fourthItemKey = new KeyBind("key.item4", 5, KeyBind.Category.INVENTORY);
    public KeyBind fifthItemKey = new KeyBind("key.item5", 6, KeyBind.Category.INVENTORY);
    public KeyBind sixthItemKey = new KeyBind("key.item6", 7, KeyBind.Category.INVENTORY);
    public KeyBind seventhItemKey = new KeyBind("key.item7", 8, KeyBind.Category.INVENTORY);
    public KeyBind eightItemKey = new KeyBind("key.item8", 9, KeyBind.Category.INVENTORY);
    public KeyBind ninthItemKey = new KeyBind("key.item9", 10, KeyBind.Category.INVENTORY);
    public KeyBind prevItemKey = new KeyBind("key.itemPrev", -97, KeyBind.Category.INVENTORY);
    public KeyBind nextItemKey = new KeyBind("key.itemNext", -96, KeyBind.Category.INVENTORY);
    public KeyBind chatKey = new KeyBind("key.chat", 20, KeyBind.Category.GENERAL);
    public KeyBind pauseKey = new KeyBind("key.pause", 1, KeyBind.Category.GENERAL);
    public KeyBind playerListKey = new KeyBind("key.playerList", 15, KeyBind.Category.GENERAL);
    public KeyBind changeModeKey = new KeyBind("key.invert", 34, KeyBind.Category.GENERAL);
    public KeyBind zoomKey = new KeyBind("key.zoom", 44, KeyBind.Category.GENERAL);
    public KeyBind toggleHUDKey = new KeyBind("key.hud", 59, KeyBind.Category.DEBUG);
    public KeyBind screenshotKey = new KeyBind("key.screenshot", 60, KeyBind.Category.DEBUG);
    public KeyBind debugKey = new KeyBind("key.debug", 61, KeyBind.Category.DEBUG);
    public KeyBind smoothCamKey = new KeyBind("key.smoothCamera", 62, KeyBind.Category.DEBUG);
    public KeyBind thirdPersonKey = new KeyBind("key.thirdPerson", 63, KeyBind.Category.DEBUG);
    public KeyBind advancedDebugKey = new KeyBind("key.coords", 64, KeyBind.Category.DEBUG);
    public KeyBind isometricKey = new KeyBind("key.iso", 65, KeyBind.Category.DEBUG);
    public KeyBind reloadKey = new KeyBind("key.reload", 66, KeyBind.Category.DEBUG);
    public KeyBind texRefreshKey = new KeyBind("key.texture", 67, KeyBind.Category.DEBUG);
    public KeyBind fullscreenKey = new KeyBind("key.fullscr", 87, KeyBind.Category.DEBUG);

    public OptionsList(File file) {
        this.io = new OptionsList.OptionsIO(new File(file, "options.properties"));
        this.loadOptions();
    }

    public void setKeyBind(int keyBindingNumber, int keyCode) {
        allKeys[keyBindingNumber].key = keyCode;
        this.saveOptions();
    }

    public void setOptionValue(Option<?> option, int toggleValue, float sliderValue) {
        switch (option.getType()) {
            case TOGGLE:
                Option<Boolean> toggle = (Option<Boolean>)option;
                toggle.setValue(!toggle.getValue());
                break;
            case CHOICE:
                Option<Integer> choice = (Option<Integer>)option;
                int newValue = choice.getValue() + toggleValue;
                choice.setValue(newValue);
                break;
            case SLIDER:
                Option<Float> slider = (Option<Float>)option;
                slider.setValue(sliderValue);
        }

        this.saveOptions();
    }

    public String getOptionName(Option<?> option) {
        return option != null ? option.getOptionName() : "";
    }

    public String getOptionDesc(Option<?> option) {
        return option != null ? option.getOptionDesc() : "";
    }

    public String getKeyBindKey(int keyBindingNumber) {
        return allKeys[keyBindingNumber].key < 0
            ? Translator.getInstance().translateKeyFormat("key.mouseButton", allKeys[keyBindingNumber].key + 101)
            : Keyboard.getKeyName(allKeys[keyBindingNumber].key);
    }

    public String getKeyBindName(int keyBindingNumber) {
        return Translator.getInstance().translateKey(allKeys[keyBindingNumber].name);
    }

    public void loadOptions() {
        this.texturePack = this.io.get("texturePack", this.texturePack);
        this.languagePack = this.io.get("languagePack", this.languagePack);
        this.lastServer = this.io.get("lastServer", this.lastServer);

        for (Option<?> opt : options) {
            this.io.loadOption(opt);
        }

        for (KeyBind km : allKeys) {
            this.io.loadKeyMapping(km);
            KeyBind.refreshBinds();
        }
    }

    public void saveOptions() {
        this.io.set("texturePack", this.texturePack);
        this.io.set("languagePack", this.languagePack);
        this.io.set("lastServer", this.lastServer);

        for (Option<?> opt : options) {
            this.io.saveOption(opt);
        }

        for (KeyBind km : allKeys) {
            this.io.saveKeyMapping(km);
        }
    }

    private static void reloadRenderers() {
        if (Minecraft.getMinecraft().world != null) {
            Minecraft.getMinecraft().worldRenderer.reloadChunks();
        }
    }

    private static void refreshTextures() {
        if (Minecraft.getMinecraft().textureLoader != null) {
            Minecraft.getMinecraft().textureLoader.refreshTextureMaps();
        }
    }

    private static void restoreLighting() {
        Player player = Minecraft.getMinecraft().player;
        World world = Minecraft.getMinecraft().world;
        if (world != null) {
            world.restoreBlockEmittance(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY), MathHelper.floor_double(player.posZ));
        }
    }

    private static String translateString(String str) {
        return Translator.getInstance().translateKey(str);
    }

    public class OptionsIO extends PropertiesConfig {
        public OptionsIO(File configFile) {
            super(configFile, "Minecraft Client Options");
        }

        public void loadKeyMapping(KeyBind bind) {
            int key = this.<Integer>get("keyMapping_" + bind.name, bind.key);
            bind.key = key;
        }

        public void saveKeyMapping(KeyBind bind) {
            this.set("keyMapping_" + bind.name, bind.key);
        }

        public <T> void loadOption(Option<T> opt) {
            if (opt != null) {
                T value = this.get(opt.optionName, opt.getValue());
                opt.setValue(value);
            }
        }

        public <T> void saveOption(Option<T> opt) {
            if (opt != null) {
                this.set(opt.optionName, opt.getValue());
            }
        }
    }
}
