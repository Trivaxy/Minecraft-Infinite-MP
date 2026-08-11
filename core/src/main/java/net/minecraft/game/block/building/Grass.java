package net.minecraft.game.block.building;

import java.util.List;
import java.util.Random;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;

public class Grass extends Block {
    private static final String[] NAMES = new String[]{
        "grass", "podzol", "jungleGrass", "dryGrass", "mycelium", "hellSoil", "nyliumGrass", "grassHeaven", "grassPurple", "grassCold", "grassGold"
    };
    private Icon[] snowSide;
    private Icon[] top;
    private Icon[] side;

    public Grass(int blockID) {
        super(blockID, MaterialList.grass);
        this.setTickable(true);
        this.hasStates = true;
        this.multipleNames = true;
        this.names = NAMES;
    }

    @Override
    public Icon getIcon(int side, int mdata) {
        if (mdata > this.top.length) {
            mdata = 0;
        }

        if (side == 1) {
            return this.top[mdata];
        }

        if (side == 0) {
            if (mdata == 6) {
                return BlockList.dirt.getIcon(side, 5);
            } else {
                return mdata >= 7 ? BlockList.dirt.getIcon(side, 7) : BlockList.dirt.getIcon(side, mdata);
            }
        } else {
            return this.side[mdata];
        }
    }

    @Override
    public Icon getTexture(WorldSource world, int x, int y, int z, int side) {
        int mdata = world.getBlockMetadata(x, y, z);
        if (mdata > this.top.length) {
            mdata = 0;
        }

        if (side == 1) {
            return this.top[mdata];
        }

        if (side == 0) {
            if (mdata == 6) {
                return BlockList.dirt.getIcon(side, 5);
            } else {
                return mdata >= 7 ? BlockList.dirt.getIcon(side, 7) : BlockList.dirt.getIcon(side, mdata);
            }
        } else {
            Material mat = world.getBlockMaterial(x, y + 1, z);
            Icon newSideIcon = this.side[mdata];
            Icon newSnowIcon = this.snowSide[mdata];
            if (mdata > 7) {
                newSnowIcon = this.snowSide[7];
            }

            return mat == MaterialList.snow ? newSnowIcon : newSideIcon;
        }
    }

    @Override
    public void onTick(World world, int xCoord, int yCoord, int zCoord, Random random) {
        if (!world.isOnline) {
            int thisMetadata = world.getBlockMetadata(xCoord, yCoord, zCoord);
            int lightLevel = world.getBlockBrightness(xCoord, yCoord + 1, zCoord);
            int targetMetadata = thisMetadata;
            if (thisMetadata == 6) {
                targetMetadata = 5;
            }

            if (thisMetadata >= 7) {
                targetMetadata = 7;
            }

            if (world.isSolid(xCoord, yCoord + 1, zCoord)) {
                if (random.nextInt(4) != 0) {
                    return;
                }

                world.setBlockAndMetadataWithNotify(xCoord, yCoord, zCoord, BlockList.dirt.id, targetMetadata);
            } else if (lightLevel >= 9) {
                int x = xCoord + random.nextInt(3) - 1;
                int y = yCoord + random.nextInt(5) - 3;
                int z = zCoord + random.nextInt(3) - 1;
                if (world.getBlockId(x, y, z) == BlockList.dirt.id
                    && world.getBlockMetadata(x, y, z) == targetMetadata
                    && world.getBlockMetadata(x, y, z) != 6
                    && lightLevel >= 4
                    && !world.isSolid(x, y + 1, z)
                    && !world.getBlockMaterial(x, y + 1, z).getIsLiquid()
                    && world.getBlockMaterial(x, y + 1, z) != MaterialList.ice) {
                    world.setBlockAndMetadataWithNotify(x, y, z, BlockList.grass.id, thisMetadata);
                }
            }
        }
    }

    @Override
    public int dropID(int blockid, Random random) {
        return BlockList.dirt.dropID(0, random);
    }

    @Override
    public int dropMeta(int meta) {
        if (meta == 6) {
            return 5;
        } else {
            return meta > 6 ? 7 : meta;
        }
    }

    @Override
    public void registerIcons(IconRegister r) {
        this.top = new Icon[this.names.length];
        this.side = new Icon[this.names.length];
        this.snowSide = new Icon[this.names.length];

        for (int i = 0; i < this.names.length; i++) {
            this.top[i] = r.registerIcon("grasses/" + this.names[i] + "_top");
            this.side[i] = r.registerIcon("grasses/" + this.names[i] + "_side");
            this.snowSide[i] = r.registerIcon("grasses/" + this.names[i] + "_snow");
        }
    }

    @Override
    public void spawnParticles(World world, int x, int y, int z, Random random) {
        if (!world.getBlockMaterial(x, y + 1, z).isSolid()) {
            ParticleType firefly = ParticleType.FIREFLY;
            int meta = world.getBlockMetadata(x, y, z);
            if (random.nextInt(1000) == 0 && !world.isDaytime() && meta == 0 && world.isBlockExposed(x, y + 1, z)) {
                world.spawnParticle(firefly, x + random.nextDouble(), y + 1.1, z + random.nextDouble(), 0.0, 0.0, 0.0);
            }

            if (random.nextInt(500) == 0 && meta >= 8) {
                if (meta == 9) {
                    firefly = ParticleType.FIREFLYBLUE;
                } else if (meta == 8) {
                    firefly = ParticleType.FIREFLYPINK;
                }

                world.spawnParticle(firefly, x + random.nextDouble(), y + 1.1, z + random.nextDouble(), 0.0, 0.0, 0.0);
            }

            if (random.nextInt(10) == 0 && world.getBlockMetadata(x, y, z) >= 4 && world.getBlockMetadata(x, y, z) < 7) {
                world.spawnParticle(ParticleType.TOWNAURA, x + random.nextDouble(), y + 1.1, z + random.nextDouble(), 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    public int getMaxMeta() {
        return 10;
    }

    @Override
    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        for (int i = 0; i <= this.getMaxMeta(); i++) {
            ls.add(new ItemStack(id, 1, i));
        }
    }

    @Override
    public int getRotation(int x, int y, int z, int side) {
        return side != 0 && side != 1 ? -1 : this.rotationRand(x, y, z, side);
    }
}
