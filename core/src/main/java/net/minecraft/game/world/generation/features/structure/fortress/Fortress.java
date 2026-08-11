package net.minecraft.game.world.generation.features.structure.fortress;

import net.minecraft.game.world.generation.features.structure.LegacyStructure;
import net.minecraft.game.world.generation.features.structure.LegacyStructureStart;

public class Fortress extends LegacyStructure {
    @Override
    protected boolean canSpawnStructureAtCoords(int x, int z) {
        int realX = x >> 4;
        int realZ = z >> 4;
        rand.setSeed(realX ^ realZ << 4 ^ this.world.getSeed());
        rand.nextInt();
        return rand.nextInt(3) != 0 ? false : (x != (realX << 4) + 4 + rand.nextInt(8) ? false : z == (realZ << 4) + 4 + rand.nextInt(8));
    }

    @Override
    protected LegacyStructureStart getStructureStart(int x, int z) {
        return new FortressStart(this.world, rand, x, z);
    }
}
