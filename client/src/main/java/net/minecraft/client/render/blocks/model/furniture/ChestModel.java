package net.minecraft.client.render.blocks.model.furniture;

import net.minecraft.client.options.OptionValues;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;

public class ChestModel extends BlockModel {
    @Override
    public boolean render(Block block, int x, int y, int z) {
        return this.textureOverride == null && !(OptionValues.offsetAmount > 0.0F) ? false : super.render(block, x, y, z);
    }
}
