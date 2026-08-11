package net.minecraft.game.block.misc;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class SlimeBlock extends Block {
    public Icon coreIcon;

    public SlimeBlock(int blockID, Material material) {
        super(blockID, material);
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public int renderPass() {
        return 1;
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.SLIME;
    }

    @Override
    public void registerIcons(IconRegister r) {
        super.registerIcons(r);
        this.coreIcon = r.registerIcon("sludge");
    }

    @Override
    public void onEntityCollision(Entity entity, World world, int x, int y, int z) {
        if (Math.abs(entity.motionY) < 0.1 && !entity.sneaking) {
            double motion = 0.4 + Math.abs(entity.motionY) * 0.2;
            entity.motionX *= motion;
            entity.motionZ *= motion;
        }
    }
}
