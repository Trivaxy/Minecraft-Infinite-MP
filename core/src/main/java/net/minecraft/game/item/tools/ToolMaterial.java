package net.minecraft.game.item.tools;

public enum ToolMaterial {
    STONE(0, 64, 4.0F, 0, 4),
    COPPER(1, 128, 5.0F, 1, 4363),
    IRON(2, 256, 6.0F, 2, 4105),
    GOLD(3, 512, 8.0F, 3, 4106),
    DIAMOND(4, 1024, 10.0F, 4, 4104),
    DAMASCUS(5, 2048, 12.0F, 5, 4326),
    ADAMANTINE(5, 2048, 12.0F, 5, 241),
    RUBY(4, 512, 7.0F, 2, 4266),
    SAPPHIRE(4, 512, 7.0F, 2, 4267),
    EMERALD(4, 512, 7.0F, 2, 4268),
    AMETHYST(5, 512, 8.0F, 3, 4335),
    TOPAZ(5, 512, 8.0F, 3, 4240),
    SPECIAL(4, 2048, 2.0F, 4, 4326),
    DARKMATTER(6, 4096, 14.0F, 6, 4448),
    MICHAEL(4, 256, 7.0F, 4, 4266);

    private final int harvestLevel;
    private final int durability;
    private final float efficiency;
    private final int entityDamage;
    private final int repairID;

    ToolMaterial(int harvest, int dur, float eff, int dmg, int repair) {
        this.harvestLevel = harvest;
        this.durability = dur;
        this.efficiency = eff;
        this.entityDamage = dmg;
        this.repairID = repair;
    }

    public int getDurability() {
        return this.durability;
    }

    public float getEfficiency() {
        return this.efficiency;
    }

    public int getEntityDamage() {
        return this.entityDamage;
    }

    public int getHarvestLevel() {
        return this.harvestLevel;
    }

    public int getRepairID() {
        return this.repairID;
    }
}
