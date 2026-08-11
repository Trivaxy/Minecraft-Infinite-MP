package net.minecraft.game.world.generation.features.structure.stronghold;

import java.util.Random;
import net.minecraft.game.world.generation.features.structure.LegacyStructureBlocks;

class BrickType extends LegacyStructureBlocks {
    private BrickType() {
    }

    @Override
    public void selectBlocks(Random rand, int x, int y, int z, boolean solid) {
        if (!solid) {
            this.id = 0;
            this.meta = 0;
        } else {
            this.id = Stronghold.bricksId;
            float chance = rand.nextFloat();
            if (chance < 0.2F) {
                this.id = Stronghold.bricksAlt1Id;
                this.meta = Stronghold.bricksAlt1Meta;
            } else if (chance < 0.5F) {
                this.id = Stronghold.bricksAlt2Id;
                this.meta = Stronghold.bricksAlt2Meta;
            } else {
                this.id = Stronghold.bricksId;
                this.meta = Stronghold.bricksMeta;
            }
        }
    }

    BrickType(GuaranteedPieceWeight weight) {
        this();
    }
}
