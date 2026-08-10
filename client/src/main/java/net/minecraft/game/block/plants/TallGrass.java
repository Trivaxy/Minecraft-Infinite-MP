package net.minecraft.game.block.plants;

import java.util.List;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;

public class TallGrass extends Flower {
    private Icon[] icons;
    private Icon[] iconsAlt;
    private Icon altIcon;
    public static String[] GRASSES = new String[]{
        "tallGrass",
        "tallFrostedGrass",
        "tallLushGrass",
        "tallDryGrass",
        "roots",
        "spores",
        "holyGrass",
        "purpleGrass",
        "coldGrass",
        "fern",
        "purpleFern",
        "coldFern"
    };
    public static String[] GLOWGRASSES = new String[]{"tallPaleGrass", "paleFern"};

    public TallGrass(int id) {
        super(id);
        float offset = 0.4F;
        this.setBounds(0.5F - offset, 0.0, 0.5F - offset, 0.5F + offset, 0.6F, 0.5F + offset);
        this.hasStates = true;
        this.multipleNames = true;
        this.names = GRASSES;
        if (id == 189) {
            this.multipleNames = false;
        } else if (id == 278) {
            this.names = GLOWGRASSES;
        }
    }

    @Override
    public boolean canStay(World world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z) != 4 && world.getBlockMetadata(x, y, z) != 5
            ? world.getBlockId(x, y - 1, z) == BlockList.moss.id || super.canStay(world, x, y, z)
            : (world.getBlockId(x, y - 1, z) == BlockList.grass.id || world.getBlockId(x, y - 1, z) == BlockList.dirt.id)
                && world.getBlockMetadata(x, y - 1, z) >= 5;
    }

    @Override
    public boolean canGrowHere(int blockid) {
        if (this.id == BlockList.duneGrass.id) {
            return blockid == BlockList.sand.id;
        } else {
            return this.id == BlockList.glowGrass.id ? blockid == BlockList.voidGrass.id : super.canGrowHere(blockid);
        }
    }

    @Override
    public Icon getIcon(int side, int mdata) {
        if (mdata < 0 || mdata >= this.icons.length) {
            mdata = 0;
        }

        if (this.id == BlockList.duneGrass.id) {
            return this.altIcon;
        }

        if (this.id == BlockList.glowGrass.id) {
            if (mdata >= this.iconsAlt.length) {
                mdata = 0;
            }

            return this.iconsAlt[mdata];
        } else {
            return this.icons[mdata];
        }
    }

    @Override
    public void registerIcons(IconRegister r) {
        super.registerIcons(r);
        this.altIcon = r.registerIcon("plants/duneGrass");
        this.icons = new Icon[GRASSES.length * 2];
        this.iconsAlt = new Icon[GRASSES.length * 2];

        for (int index = 0; index < GRASSES.length; index++) {
            this.icons[index] = r.registerIcon("plants/" + GRASSES[index]);
        }

        for (int index = 0; index < GLOWGRASSES.length; index++) {
            this.iconsAlt[index] = r.registerIcon("plants/" + GLOWGRASSES[index]);
        }
    }

    @Override
    public int dropID(int meta, Random rand) {
        int drop = ItemList.seeds.id;
        if (rand.nextInt(5) == 0) {
            drop = ItemList.seedsCotton.id;
        }

        if (meta == 4 || meta == 5) {
            drop = ItemList.seedsBeetroot.id;
        }

        return rand.nextInt(8) == 0 ? drop : -1;
    }

    @Override
    public int pickID(int meta) {
        return this.id;
    }

    @Override
    public int guaranteedID(int id) {
        return ItemList.seeds.id;
    }

    @Override
    public int getMaxMeta() {
        if (this.id == BlockList.duneGrass.id) {
            return 0;
        } else {
            return this.id == BlockList.glowGrass.id ? 1 : 11;
        }
    }

    @Override
    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i <= this.getMaxMeta(); i++) {
            ls.add(new ItemStack(id, 1, i));
        }
    }
}
