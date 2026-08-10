package net.minecraft.game;

public enum ParticleType {
    BUBBLE(1, "bubble"),
    SMOKE(2, "smoke"),
    EXPLODE(3, "explosion"),
    GUST(4, "gust"),
    HUGEEXPLODE(5, "explosion_emitter"),
    FLAME(6, "flame"),
    BLUEFLAME(7, "blue_flame"),
    LAVA(8, "lava_blob"),
    SPLASH(9, "splash"),
    LARGESMOKE(10, "large_smoke"),
    FOOTSTEP(11, "footstep"),
    FIREFLY(12, "firefly"),
    TOWNAURA(13, "spore"),
    DRIPWATER(14, "water_drop"),
    DRIPLAVA(15, "lava_drop"),
    SUSPENDED(16, "void"),
    SLIMEBALLPOOF(17, "slime"),
    NOTE(18, "note"),
    ICONCRACK(19, "item"),
    TILECRACK(0, "block"),
    SNOWBALLPOOF(20, "snowball"),
    BUBBLECOLUMN(21, "bubble_column"),
    FOOTSTEPSNOW(22, "snow_footstep"),
    LEAVES(23, "falling_leaf"),
    HEART(24, "heart"),
    CRIT(25, "crit"),
    SNOWSHOVEL(26, "snow"),
    SPELL(27, "spell"),
    POTION(28, "potion"),
    FIRE(29, "fire"),
    GROW(30, "grow"),
    PEBBLEPOOF(31, "rock_smash"),
    BULLETPOOF(32, "bullet_smash"),
    DUST(33, "dust"),
    SPORES(34, "spore_red"),
    SPORESCYAN(35, "spore_cyan"),
    SPORESGOLD(36, "spore_gold"),
    SPORESPURPLE(37, "spore_purple"),
    SPORESBLUE(38, "aura"),
    SPORESPINK(39, "aura_pink"),
    MAGIC(40, "magic"),
    SOUL(41, "soul"),
    FIREFLYPINK(42, "firefly_pink"),
    FIREFLYBLUE(43, "firefly_blue"),
    SWEEP(44, "sweep"),
    EGGPOOF(45, "egg_shells"),
    RAINDROP(46, "raindrop"),
    FISH(47, "fish"),
    ASH(48, "ash"),
    BARRIER(49, "barrier"),
    LIGHT(50, "lightbulb"),
    DAMAGE(51, "damage"),
    SANDBALLPOOF(52, "sandball"),
    FOAM(53, "foam");

    private final int id;
    private final String name;

    ParticleType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public static ParticleType getEnum(String name) {
        for (ParticleType category : values()) {
            if (category.name.equalsIgnoreCase(name)) {
                return category;
            }
        }

        return null;
    }
}
