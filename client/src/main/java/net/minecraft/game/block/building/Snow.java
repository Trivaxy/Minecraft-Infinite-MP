package net.minecraft.game.block.building;

import java.util.List;
import java.util.Random;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class Snow extends Block {
    private Icon altIcon;

    public Snow(int blockID, Material material) {
        super(blockID, material);
        this.hasStates = true;
        this.multipleNames = true;
        this.names = new String[]{"snow", "snowBricks"};
    }

    @Override
    public int dropID(int i, Random random) {
        return ItemList.snowball.id;
    }

    @Override
    public int quantityDropped(Random random) {
        return 4;
    }

    @Override
    public void onEntityWalking(World world, int xCoord, int yCoord, int zCoord, Entity entity) {
        if (entity.produceFootsteps) {
            world.spawnParticle(
                ParticleType.FOOTSTEPSNOW,
                xCoord + world.rand.nextDouble(),
                yCoord + this.maxY + 0.01,
                zCoord + world.rand.nextDouble(),
                entity.posX,
                entity.posY,
                entity.posZ
            );
        }
    }

    @Override
    public int dropMeta(int id) {
        return 0;
    }

    @Override
    public int getMaxMeta() {
        return 1;
    }

    @Override
    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i <= this.getMaxMeta(); i++) {
            ls.add(new ItemStack(id, 1, i));
        }
    }

    @Override
    public Icon getIcon(int side, int meta) {
        return meta == 1 ? this.altIcon : this.blockIcon;
    }

    @Override
    public void registerIcons(IconRegister ir) {
        super.registerIcons(ir);
        this.altIcon = ir.registerIcon("snowBricks");
    }
}
