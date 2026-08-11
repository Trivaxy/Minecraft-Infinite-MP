package net.minecraft.game.item.tools;

public enum ArmorMaterial {
    CLOTH(0, 4, new int[]{1, 3, 2, 1}, 4171),
    CHAIN(0, 8, new int[]{2, 4, 3, 1}, 4353),
    COPPER(1, 12, new int[]{2, 5, 4, 2}, 4363),
    IRON(2, 16, new int[]{2, 6, 5, 2}, 4105),
    GOLD(3, 32, new int[]{3, 7, 5, 2}, 4106),
    DIAMOND(4, 64, new int[]{3, 8, 6, 3}, 4104),
    RUBY(5, 32, new int[]{2, 6, 5, 2}, 4266),
    SAPPHIRE(5, 32, new int[]{2, 6, 5, 2}, 4267),
    EMERALD(5, 32, new int[]{2, 6, 5, 2}, 4268),
    AMETHYST(5, 32, new int[]{2, 6, 5, 2}, 4335),
    TOPAZ(5, 32, new int[]{2, 6, 5, 2}, 4240),
    TIDE(5, 32, new int[]{2, 6, 5, 2}, 4267),
    DAMASCUS(5, 128, new int[]{3, 8, 6, 3}, 4326),
    ADAMANTINE(5, 128, new int[]{3, 8, 6, 3}, 241);

    private int maxDmg;
    private int[] defValues;
    private int repairID;
    private int tier;

    ArmorMaterial(int tier, int dmg, int[] def, int repair) {
        this.tier = tier;
        this.maxDmg = dmg;
        this.defValues = def;
        this.repairID = repair;
    }

    public int getDurability(int val) {
        return Armor.getDurability()[val] * this.maxDmg;
    }

    public int getDefense(int val) {
        return this.defValues[val];
    }

    public int getRepairID() {
        return this.repairID;
    }

    public int getTier() {
        return this.tier;
    }
}
