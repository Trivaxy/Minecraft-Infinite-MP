package net.minecraft.game.world.generation.features.structure;

import net.minecraft.util.random.WeightedRandom;

public class LegacyStructureTreasure extends WeightedRandom.Choice {
    public int itemID;
    public int itemMetadata;
    public int minStackSize;
    public int maxStackSize;

    public LegacyStructureTreasure(int itemId, int itemMeta, int min, int max, int weight) {
        super(weight);
        this.itemID = itemId;
        this.itemMetadata = itemMeta;
        this.minStackSize = min;
        this.maxStackSize = max;
    }
}
