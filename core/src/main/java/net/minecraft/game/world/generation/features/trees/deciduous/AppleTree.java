package net.minecraft.game.world.generation.features.trees.deciduous;

import net.minecraft.game.world.World;

public class AppleTree extends ProceduralTree {
    public AppleTree() {
        super(0, 0, 4);
        this.leafMeta = 1;
    }

    @Override
    public void place(World thisWorld, int x, int y, int z, int material, int dmg) {
        this.placeBlock(this.world, x, y, z, material, this.rnd.nextInt(2));
    }
}
