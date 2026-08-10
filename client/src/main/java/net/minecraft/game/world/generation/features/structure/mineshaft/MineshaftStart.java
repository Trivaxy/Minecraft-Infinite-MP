package net.minecraft.game.world.generation.features.structure.mineshaft;

import java.util.Random;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.generation.features.structure.LegacyStructureStart;
import net.minecraft.game.world.generation.features.structure.mineshaft.pieces.DirtRoom;

public class MineshaftStart extends LegacyStructureStart {
    public MineshaftStart(World world, Random random, int x, int z) {
        DirtRoom room = new DirtRoom(0, random, (x << 4) + 2, (z << 4) + 2);
        this.pieces.add(room);
        room.buildComponent(room, this.pieces, random);
        this.updateBoundingBox();
        if (world.getBiomeAt(x, z) == BiomeList.mesa) {
            this.markAvailableHeight(world, random, -20);
        } else {
            this.markAvailableHeight(world, random, 10);
        }
    }
}
