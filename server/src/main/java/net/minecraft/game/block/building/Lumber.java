package net.minecraft.game.block.building;

import java.util.List;
import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;

public class Lumber extends Block {
    private Icon[] front;
    private Icon[] top;
    public static String[] LOGS = new String[]{
        "oakLumber",
        "birchLumber",
        "spruceLumber",
        "palmLumber",
        "acaciaLumber",
        "jungleLumber",
        "mangroveLumber",
        "baobabLumber",
        "scorchedLumber",
        "mapleLumber",
        "cherryLumber",
        "mahoganyLumber",
        "oldenOakLumber",
        "hellbarkLumber",
        "nylliumLumber",
        "purpurLumber",
        "willowLumber",
        "flowerLumber",
        "stemLumber"
    };

    public Lumber(int blockid) {
        super(blockid, MaterialList.wood);
        this.setBounds(0.0625, 0.0, 0.0625, 0.9375, 1.0, 0.9375);
        this.hasStates = true;
        this.multipleNames = true;
        this.names = LOGS;
    }

    @Override
    public void onPlaced(World world, Player player, int x, int y, int z, int face) {
        super.onPlaced(world, player, x, y, z, face);
        ItemStack stack = player.inventory.getCurrentItem();
        if (player.inventory.canUseItem()) {
            stack = player.inventory.getOffHandItem();
        }

        int meta = stack.itemDmg & 63;
        switch (face) {
            case 0:
            case 1:
            default:
                break;
            case 2:
            case 3:
                meta += 128;
                break;
            case 4:
            case 5:
                meta += 64;
        }

        world.setBlockMetadataWithNotify(x, y, z, meta);
    }

    @Override
    public void forceBounds(int id) {
        this.setBounds(0.0625, 0.0, 0.0625, 0.9375, 1.0, 0.9375);
    }

    @Override
    public void setStateBounds(World world, int x, int y, int z) {
        this.modifyOutline(world, x, y, z);
    }

    @Override
    public void modifyOutline(WorldSource world, int x, int y, int z) {
        int metadata = world.getBlockMetadata(x, y, z) & 240;
        if (metadata >= 64 && metadata < 128) {
            this.setBounds(0.0, 0.0625, 0.0625, 1.0, 0.9375, 0.9375);
        } else if (metadata >= 128) {
            this.setBounds(0.0625, 0.0625, 0.0, 0.9375, 0.9375, 1.0);
        } else {
            this.setBounds(0.0625, 0.0, 0.0625, 0.9375, 1.0, 0.9375);
        }
    }

    @Override
    public Icon getIcon(int side, int meta) {
        int dmg = meta & 240;
        int top = meta & 63;
        if (top >= this.top.length || top >= this.front.length || top < 0) {
            top = 0;
        }

        if (dmg < 64) {
            if (side == 1 || side == 0) {
                return this.top[top];
            }
        } else if (dmg < 128) {
            if (side == 5 || side == 4) {
                return this.top[top];
            }
        } else if (side == 2 || side == 3) {
            return this.top[top];
        }

        return this.front[top];
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public boolean renderFace(WorldSource world, int xCoord, int yCoord, int zCoord, int side) {
        return true;
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.LOG;
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    public int dropMeta(int id) {
        return id & 63;
    }

    @Override
    protected int guaranteedMeta(int id) {
        return id & 63;
    }

    @Override
    public void registerIcons(IconRegister r) {
        this.top = new Icon[LOGS.length];
        this.front = new Icon[LOGS.length];

        for (int i = 0; i < LOGS.length; i++) {
            this.top[i] = r.registerIcon(Log.LOGS[i] + "_top");
            this.front[i] = r.registerIcon("logs/" + LOGS[i]);
        }
    }

    @Override
    public int getMaxMeta() {
        return 18;
    }

    @Override
    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i <= this.getMaxMeta(); i++) {
            ls.add(new ItemStack(id, 1, i & 63));
        }
    }

    @Override
    public int pickMeta(int id) {
        return id & 63;
    }
}
