package net.minecraft.game.world.generation.features.structure.mineshaft;

import net.minecraft.game.world.generation.features.structure.LegacyStructure;
import net.minecraft.game.world.generation.features.structure.LegacyStructureStart;

public class Mineshaft extends LegacyStructure {
    @Override
    protected boolean canSpawnStructureAtCoords(int x, int z) {
        return rand.nextInt(250) == 0 && rand.nextInt(80) < Math.max(Math.abs(x), Math.abs(z));
    }

    @Override
    protected LegacyStructureStart getStructureStart(int x, int z) {
        return new MineshaftStart(this.world, rand, x, z);
    }
}
