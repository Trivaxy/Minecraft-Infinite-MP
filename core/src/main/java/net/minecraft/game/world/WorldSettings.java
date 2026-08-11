package net.minecraft.game.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.localization.Translator;

public class WorldSettings {
    public static final int BIOME_OFFSET = 20;
    public static final WorldSettings.Option<?>[] options = new WorldSettings.Option[20];
    private static final String[] GAMEMODES = new String[]{
        "selectWorld.gameMode.survival", "selectWorld.gameMode.creative", "selectWorld.gameMode.adventure", "selectWorld.gameMode.spectator"
    };
    public static final String[] DIFFICULTIES = new String[]{
        "options.difficulty.peaceful", "options.difficulty.easy", "options.difficulty.normal", "options.difficulty.hard"
    };
    private static final String[] BIOMES;
    public static final String OFF = "options.off";
    public static final String ON = "options.on";
    public Biome singleBiomeId;
    public static WorldType worldType = WorldType.DEFAULT;
    public static boolean cheats = false;
    public static boolean structures = true;
    public static boolean height = true;
    public static boolean events = true;
    public static boolean bonus = false;
    public static boolean permaDay = false;
    public boolean difficultyLock = false;
    public static int gamemode = 0;
    public static int difficulty = 2;
    public static int type = 0;
    public static int dayLength = 36000;
    public static int size = 0;
    public static int heightInt = height ? 16 : 8;
    public boolean[] biomes = new boolean[256];
    public static boolean sprint = true;
    public static boolean hunger = false;
    public static boolean atkCooldown = false;
    public static boolean armor = false;
    public static boolean keepInventory = false;
    public static boolean permadeath = false;
    public static boolean sleep = true;
    public static boolean grave = true;
    public static boolean durability = true;
    public int topLayer = 6;
    public int bottomLayer = 0;
    public int layerOne = 5;
    public int layerTwo = 3;
    public int topBlock = BlockList.grass.id;
    public int bottomBlock = BlockList.bedrock.id;
    public int blockOne = BlockList.dirt.id;
    public int blockTwo = BlockList.stone.id;
    public static final WorldSettings.Option<?> gm = new WorldSettings.Option<>(
        "gameMode", WorldSettings.Option.Type.CHOICE, WorldSettings.Option.Category.GENERAL, () -> gamemode, v -> {
            gamemode = v;
            if (gamemode > 2) {
                gamemode = 0;
            }

            cheats = gamemode == 1;
        }, v -> GAMEMODES[gamemode]
    );
    public static final WorldSettings.Option<?> diff = new WorldSettings.Option<>(
        "difficulty", WorldSettings.Option.Type.CHOICE, WorldSettings.Option.Category.GENERAL, () -> difficulty, v -> {
            difficulty = v;
            if (difficulty > 3) {
                difficulty = 0;
            }
        }, v -> DIFFICULTIES[difficulty]
    );
    public static final WorldSettings.Option<?> bonusHouse = new WorldSettings.Option<>(
        "bonus",
        WorldSettings.Option.Type.TOGGLE,
        WorldSettings.Option.Category.GENERAL,
        () -> bonus,
        v -> bonus = !bonus,
        v -> bonus ? "options.on" : "options.off"
    );
    public static final WorldSettings.Option<?> commands = new WorldSettings.Option<>(
        "cheats",
        WorldSettings.Option.Type.TOGGLE,
        WorldSettings.Option.Category.GENERAL,
        () -> cheats,
        v -> cheats = !cheats,
        v -> cheats ? "options.on" : "options.off"
    );
    public static final WorldSettings.Option<?> struct = new WorldSettings.Option<>(
        "mapFeatures",
        WorldSettings.Option.Type.TOGGLE,
        WorldSettings.Option.Category.WORLD,
        () -> structures,
        v -> structures = !structures,
        v -> structures ? "options.on" : "options.off"
    );
    public static final WorldSettings.Option<?> mapType = new WorldSettings.Option<>(
        "mapType", WorldSettings.Option.Type.CHOICE, WorldSettings.Option.Category.WORLD, () -> type, v -> {
            type = v;
            worldType = WorldType.fromNumber(type % WorldType.values().length);
        }, v -> worldType.translateKey
    );
    public static final WorldSettings.Option<?> hght = new WorldSettings.Option<>(
        "mapHeight", WorldSettings.Option.Type.TOGGLE, WorldSettings.Option.Category.WORLD, () -> height, v -> {
            height = !height;
            heightInt = height ? 20 : 12;
        }, v -> height ? "selectWorld.mapHeight.new" : "selectWorld.mapHeight.old"
    );
    public static final WorldSettings.Option<?> border = new WorldSettings.Option<>(
        "finite", WorldSettings.Option.Type.CHOICE, WorldSettings.Option.Category.WORLD, () -> size, v -> {
            size *= 2;
            if (size == 0) {
                size = 128;
            }

            if (size > 4096) {
                size = 0;
            }
        }, v -> size == 0 ? "selectWorld.infinite" : size + "x" + size
    );
    public static final WorldSettings.Option<?> randomEvents = new WorldSettings.Option<>(
        "events",
        WorldSettings.Option.Type.TOGGLE,
        WorldSettings.Option.Category.WORLD,
        () -> events,
        v -> events = !events,
        v -> events ? "options.on" : "options.off"
    );
    public static final WorldSettings.Option<?> days = new WorldSettings.Option<>(
        "dayLength", WorldSettings.Option.Type.CHOICE, WorldSettings.Option.Category.WORLD, () -> dayLength, v -> {
            dayLength += 12000;
            if (dayLength > 72000) {
                dayLength = 12000;
            }
        }, v -> dayLength / 20 / 60 + " mins"
    );
    public static final WorldSettings.Option<?> sprnt = new WorldSettings.Option<>(
        "sprint",
        WorldSettings.Option.Type.TOGGLE,
        WorldSettings.Option.Category.RULES,
        () -> sprint,
        v -> sprint = !sprint,
        v -> sprint ? "options.on" : "options.off"
    );
    public static final WorldSettings.Option<?> hungy = new WorldSettings.Option<>(
        "hunger",
        WorldSettings.Option.Type.TOGGLE,
        WorldSettings.Option.Category.RULES,
        () -> hunger,
        v -> hunger = !hunger,
        v -> hunger ? "options.on" : "options.off"
    );
    public static final WorldSettings.Option<?> modernCombat = new WorldSettings.Option<>(
        "atkCooldown",
        WorldSettings.Option.Type.TOGGLE,
        WorldSettings.Option.Category.RULES,
        () -> atkCooldown,
        v -> atkCooldown = !atkCooldown,
        v -> atkCooldown ? "options.on" : "options.off"
    );
    public static final WorldSettings.Option<?> oldArmor = new WorldSettings.Option<>(
        "armor",
        WorldSettings.Option.Type.TOGGLE,
        WorldSettings.Option.Category.RULES,
        () -> armor,
        v -> armor = !armor,
        v -> armor ? "options.on" : "options.off"
    );
    public static final WorldSettings.Option<?> babyMode = new WorldSettings.Option<>(
        "keepInventory",
        WorldSettings.Option.Type.TOGGLE,
        WorldSettings.Option.Category.RULES,
        () -> keepInventory,
        v -> keepInventory = !keepInventory,
        v -> keepInventory ? "options.on" : "options.off"
    );
    public static final WorldSettings.Option<?> insomnia = new WorldSettings.Option<>(
        "sleep",
        WorldSettings.Option.Type.TOGGLE,
        WorldSettings.Option.Category.RULES,
        () -> sleep,
        v -> sleep = !sleep,
        v -> sleep ? "options.on" : "options.off"
    );
    public static final WorldSettings.Option<?> graves = new WorldSettings.Option<>(
        "graves",
        WorldSettings.Option.Type.TOGGLE,
        WorldSettings.Option.Category.RULES,
        () -> grave,
        v -> grave = !grave,
        v -> grave ? "options.on" : "options.off"
    );
    public static final WorldSettings.Option<?> durabilities = new WorldSettings.Option<>(
        "durability",
        WorldSettings.Option.Type.TOGGLE,
        WorldSettings.Option.Category.RULES,
        () -> durability,
        v -> durability = !durability,
        v -> durability ? "options.on" : "options.off"
    );
    public static final WorldSettings.Option<?> hardcore = new WorldSettings.Option<>(
        "permadeath",
        WorldSettings.Option.Type.TOGGLE,
        WorldSettings.Option.Category.RULES,
        () -> permadeath,
        v -> permadeath = !permadeath,
        v -> permadeath ? "options.on" : "options.off"
    );
    public static final WorldSettings.Option<?> paradise = new WorldSettings.Option<>(
        "permaDay",
        WorldSettings.Option.Type.TOGGLE,
        WorldSettings.Option.Category.RULES,
        () -> permaDay,
        v -> permaDay = !permaDay,
        v -> permaDay ? "options.on" : "options.off"
    );

    public WorldSettings() {
        Arrays.fill(this.biomes, true);
        heightInt = height ? 20 : 12;
    }

    public void setOptionValue(WorldSettings.Option<?> option, int toggleValue) {
        switch (option.type) {
            case TOGGLE:
                WorldSettings.Option<Boolean> toggle = (WorldSettings.Option<Boolean>)option;
                toggle.setValue(!toggle.getValue());
                break;
            case CHOICE:
                WorldSettings.Option<Integer> choice = (WorldSettings.Option<Integer>)option;
                int newValue = choice.getValue() + toggleValue;
                choice.setValue(newValue);
        }
    }

    public final String getOptionName(WorldSettings.Option<?> option) {
        return option != null ? option.getOptionName() : "";
    }

    public final String getOptionDesc(WorldSettings.Option<?> option) {
        return option != null ? option.getOptionDesc() : "";
    }

    public final String getBiomeState(int index) {
        return this.biomes[index - 20] ? "options.on" : "options.off";
    }

    public final String getBiome(int index) {
        return BIOMES[index - 20];
    }

    public final void toggleBiome(int optionMenuValue) {
        if (optionMenuValue >= 20) {
            this.biomes[optionMenuValue - 20] = !this.biomes[optionMenuValue - 20];
        }
    }

    public void setLayer(int index, int id) {
        switch (index) {
            case 0:
                this.topBlock = id;
                break;
            case 1:
                this.blockOne = id;
                break;
            case 2:
                this.blockTwo = id;
                break;
            case 3:
                this.bottomBlock = id;
                break;
            case 4:
                this.topLayer = id;
                break;
            case 5:
                this.layerOne = id;
                break;
            case 6:
                this.layerTwo = id;
                break;
            case 7:
                this.bottomLayer = id;
        }
    }

    public int getLayer(int index) {
        switch (index) {
            case 0:
                return this.topBlock;
            case 1:
                return this.blockOne;
            case 2:
                return this.blockTwo;
            case 3:
                return this.bottomBlock;
            case 4:
                return this.topLayer;
            case 5:
                return this.layerOne;
            case 6:
                return this.layerTwo;
            case 7:
                return this.bottomLayer;
            default:
                return 0;
        }
    }

    public String getLayerName(int index) {
        switch (index) {
            case 0:
                return "selectWorld.topBlock";
            case 1:
                return "selectWorld.firstBlock";
            case 2:
                return "selectWorld.secondBlock";
            case 3:
                return "selectWorld.bottomBlock";
            case 4:
                return "selectWorld.topLayer";
            case 5:
                return "selectWorld.firstLayer";
            case 6:
                return "selectWorld.secondLayer";
            case 7:
                return "selectWorld.bottomLayer";
            default:
                return "I AM ERROR!";
        }
    }

    static {
        String[] toBiomes = new String[256];

        for (int i = 0; i < toBiomes.length; i++) {
            Biome biome = BiomeList.biomes[i];
            if (biome != null) {
                toBiomes[i] = biome.getName();
            }
        }

        BIOMES = toBiomes;
        ArrayList<String> shits = new ArrayList<>();

        for (int i = 0; i < BiomeList.biomes.length; i++) {
            Biome biom = BiomeList.biomes[i];
            if (biom != null && biom != BiomeList.biomeDefault) {
                shits.add(biom.getName());
            }
        }
    }

    public static class Option<T> {
        final String optionName;
        private final WorldSettings.Option.Type type;
        private final WorldSettings.Option.Category category;
        private static int pointer = 0;
        private final Supplier<T> getter;
        private final Consumer<T> setter;
        private final IntFunction<String> label;

        public Option(
            String name,
            WorldSettings.Option.Type type,
            WorldSettings.Option.Category category,
            Supplier<T> getter,
            Consumer<T> setter,
            IntFunction<String> label
        ) {
            this.optionName = name;
            this.getter = getter;
            this.setter = setter;
            this.label = label;
            this.type = type;
            this.category = category;
            WorldSettings.options[pointer++] = this;
        }

        public T getValue() {
            return this.getter.get();
        }

        public void setValue(T value) {
            this.setter.accept(value);
        }

        public String getOptionName() {
            return Translator.getInstance().translateKey("selectWorld." + this.optionName);
        }

        public WorldSettings.Option.Type getType() {
            return this.type;
        }

        public WorldSettings.Option.Category getCategory() {
            return this.category;
        }

        public String getOptionDesc() {
            if (this.optionName.equals("gameMode")) {
                if (WorldSettings.gamemode == 1) {
                    return "desc.creative";
                } else {
                    return WorldSettings.gamemode == 2 ? "desc.adventure" : "desc.survival";
                }
            } else {
                return this.optionName.equals("mapType")
                    ? WorldSettings.worldType.translateKey + ".desc"
                    : Translator.getInstance().translateKey("desc." + this.optionName);
            }
        }

        public String getOptionLabel(int optionMenuValue) {
            return optionMenuValue >= 0 && optionMenuValue < WorldSettings.options.length ? this.label.apply(optionMenuValue) : "";
        }

        public enum Category {
            GENERAL,
            WORLD,
            RULES;
        }

        public enum Type {
            TOGGLE,
            CHOICE;
        }
    }
}
