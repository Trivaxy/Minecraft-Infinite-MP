package net.minecraft.game.item.potion;

public class EffectList {
    public static final Effect[] effects = new Effect[32];
    public static final int MAX_CRAFTIBLE_POTIONS = 19;
    public static final int MAX_ICONS = 10;
    public static final Effect water = new Effect(0, false, 3093151).setDuration(0).setPotionName("potion.water");
    public static final Effect heal = new InstantEffect(1, false, 16262179).setDuration(1).setPotionName("potion.heal").setIconIndex(9, 0);
    public static final Effect regeneration = new Effect(2, false, 13458603)
        .setDuration(900)
        .setEffectiveness(0.25)
        .setPotionName("potion.regeneration")
        .setIconIndex(7, 0)
        .setMainEffect();
    public static final Effect swiftness = new Effect(3, false, 8171462).setPotionName("potion.moveSpeed").setIconIndex(0, 0).setMainEffect();
    public static final Effect strength = new Effect(4, false, 9643043).setPotionName("potion.damageBoost").setIconIndex(4, 0).setMainEffect();
    public static final Effect fireResistance = new Effect(5, false, 14981690).setPotionName("potion.fireResistance").setIconIndex(7, 1).setMainEffect();
    public static final Effect waterBreathing = new Effect(6, false, 3035801).setPotionName("potion.waterBreathing").setIconIndex(0, 2).setMainEffect();
    public static final Effect nightVision = new Effect(7, false, 2828584).setPotionName("potion.nightVision").setIconIndex(4, 1).setMainEffect();
    public static final Effect jump = new Effect(8, false, 7924119).setPotionName("potion.jump").setIconIndex(2, 1).setMainEffect();
    public static final Effect haste = new Effect(9, false, 14270531).setPotionName("potion.digSpeed").setIconIndex(2, 0).setMainEffect();
    public static final Effect slowFall = new Effect(10, false, 2872534).setDuration(1800).setPotionName("potion.slowFall").setIconIndex(1, 2).setMainEffect();
    public static final Effect stamina = new Effect(11, false, 13144269).setDuration(1800).setPotionName("potion.stamina").setIconIndex(4, 2).setMainEffect();
    public static final Effect resistance = new Effect(12, false, 8204959).setPotionName("potion.resistance").setIconIndex(6, 1).setMainEffect();
    public static final Effect invisibility = new Effect(17, false, 16777215)
        .setDuration(1200)
        .setPotionName("potion.invisibility")
        .setIconIndex(0, 1)
        .setMainEffect();
    public static final Effect absorption = new Effect(24, false, 16776998).setPotionName("potion.absorption").setIconIndex(2, 2);
    public static final Effect luck = new Effect(19, false, 3969852).setPotionName("potion.luck").setIconIndex(8, 1).setMainEffect();
    public static final Effect glowing = new Effect(30, false, 16777215).setPotionName("potion.glowing").setIconIndex(9, 1);
    public static final Effect poison = new Effect(13, true, 5149489)
        .setNegative()
        .setDuration(900)
        .setPotionName("potion.poison")
        .setEffectiveness(0.25)
        .setIconIndex(6, 0)
        .setMainEffect();
    public static final Effect slowness = new Effect(14, true, 5926017)
        .setNegative()
        .setDuration(1800)
        .setPotionName("potion.moveSlowdown")
        .setIconIndex(1, 0)
        .setMainEffect();
    public static final Effect weakness = new Effect(15, true, 4128860)
        .setNegative()
        .setDuration(1800)
        .setPotionName("potion.weakness")
        .setIconIndex(5, 0)
        .setMainEffect();
    public static final Effect harm = new InstantEffect(16, true, 4393481).setDuration(1).setNegative().setPotionName("potion.harm").setIconIndex(5, 2);
    public static final Effect fragility = new Effect(18, true, 7107444).setNegative().setPotionName("potion.fragility").setIconIndex(6, 2).setMainEffect();
    public static final Effect miningFatigue = new Effect(20, true, 4866583).setPotionName("potion.digSlowDown").setIconIndex(3, 0);
    public static final Effect confusion = new Effect(21, true, 5578058)
        .setNegative()
        .setEffectiveness(0.25)
        .setPotionName("potion.confusion")
        .setIconIndex(3, 1);
    public static final Effect blindness = new Effect(22, true, 2039587)
        .setNegative()
        .setEffectiveness(0.25)
        .setPotionName("potion.blindness")
        .setIconIndex(5, 1);
    public static final Effect nausea = new Effect(23, true, 11190581).setNegative().setPotionName("potion.nausea").setIconIndex(1, 1);
    public static final Effect levitation = new Effect(25, true, 16777215).setNegative().setPotionName("potion.levitation").setIconIndex(3, 2);
    public static final Effect wither = new Effect(26, false, 3614020).setNegative().setPotionName("potion.wither").setIconIndex(5, 2);
    public static final Effect fatigue = new Effect(27, true, 16777215).setNegative().setPotionName("potion.fatigue").setIconIndex(7, 2);
    public static final Effect hunger = new Effect(28, true, 5797459).setNegative().setPotionName("potion.hunger").setIconIndex(8, 0);
    public static final Effect badLuck = new Effect(29, true, 5797459).setNegative().setPotionName("potion.badLuck").setIconIndex(8, 2);
}
