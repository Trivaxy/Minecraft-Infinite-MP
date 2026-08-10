package net.minecraft.game.block.building;

import java.util.List;
import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class Cobblestone extends Block {
    public static final String[] stoneType = new String[]{
        "cobblestone", "cobblemarble", "cobblebasalt", "cobblelime", "cobblegran", "cobblechlor", "cobbleice", "cobbledrip", "cobbleholy"
    };
    private Icon[] icons;

    public Cobblestone(int blockid) {
        super(blockid, MaterialList.rock);
        this.hasStates = true;
        this.multipleNames = true;
        this.names = stoneType;
        this.setTickable(true);
    }

    @Override
    public final void onTick(World world, int xCoord, int yCoord, int zCoord, Random random) {
        if (random.nextInt(20) == 0) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (world.getBlockMaterial(xCoord + i, yCoord, zCoord + j) == MaterialList.water && world.getBlockMetadata(xCoord, yCoord, zCoord) == 0) {
                        world.setBlockWithNotify(xCoord, yCoord, zCoord, BlockList.cobblestoneMossy.id);
                    }
                }
            }
        }
    }

    @Override
    public Icon getIcon(int side, int meta) {
        if (meta < 0 || meta >= this.icons.length) {
            meta = 0;
        }

        return this.icons[meta];
    }

    @Override
    public int dropMeta(int meta) {
        return meta;
    }

    @Override
    public int getMaxMeta() {
        return 8;
    }

    @Override
    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i <= this.getMaxMeta(); i++) {
            ls.add(new ItemStack(id, 1, i));
        }
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.icons = new Icon[stoneType.length];

        for (int ass = 0; ass < this.icons.length; ass++) {
            this.icons[ass] = ir.registerIcon("cobblestones/" + stoneType[ass]);
        }
    }

    @Override
    public ItemStack getCarverContents(int id, int dmg) {
        switch (id) {
            case 0:
                return new ItemStack(BlockList.stairStone.id, 2, dmg * 8);
            case 1:
                return new ItemStack(BlockList.slabCobble.id, 2, dmg);
            case 2:
                return new ItemStack(BlockList.panelCobblestone.id, 2, dmg * 4);
            case 3:
                return new ItemStack(BlockList.wall.id, 1, dmg);
            default:
                return null;
        }
    }
}
