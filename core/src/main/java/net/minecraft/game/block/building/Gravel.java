package net.minecraft.game.block.building;

import java.util.Random;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.item.ItemList;

public class Gravel extends Sand {
    public Gravel(int id) {
        super(id, MaterialList.sand);
    }

    @Override
    public int dropID(int blockid, Random random) {
        return random.nextInt(10) == 0 ? ItemList.flint.id : this.id;
    }

    @Override
    public int guaranteedID(int id) {
        return ItemList.flint.id;
    }
}
