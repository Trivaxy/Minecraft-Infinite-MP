package net.minecraft.game.block.building;

import java.util.List;
import java.util.Random;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.mob.follower.Snowman;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.creative.CreativeTabs;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;
import net.minecraft.statistics.achievement.AchievementList;
import net.minecraft.util.math.MathHelper;

public class Fruit extends Block {
    private Icon top;
    private Icon front;

    public Fruit(int id) {
        super(id, MaterialList.pumpkin);
        this.compostable = true;
        this.hasStates = true;
    }

    @Override
    public Icon getIcon(int side, int state) {
        if (this.id == BlockList.pumpkin.id && state >= 1 && state < 6) {
            return side == 1 ? this.top : (side == 0 ? this.top : (side == 3 ? this.blockIcon : this.front));
        }

        int meta = state & 240;
        if (meta < 64) {
            if (side == 1 || side == 0) {
                return this.top;
            }
        } else if (meta < 128) {
            if (side == 5 || side == 4) {
                return this.top;
            }
        } else if (side == 2 || side == 3) {
            return this.top;
        }

        return this.front;
    }

    @Override
    public Icon getTexture(WorldSource world, int xCoord, int yCoord, int zCoord, int side) {
        int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
        if (meta < 2 || meta >= 6) {
            return super.getTexture(world, xCoord, yCoord, zCoord, side);
        } else if (side == 1 || side == 0) {
            return this.top;
        } else if (this.id == BlockList.pumpkin.id) {
            return side != meta ? this.front : this.blockIcon;
        } else {
            return super.getTexture(world, xCoord, yCoord, zCoord, side);
        }
    }

    @Override
    public boolean isRotatable(int mdata) {
        return this.id == BlockList.pumpkin.id && mdata > 0 && mdata < 64;
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.LOG;
    }

    @Override
    public int quantityDropped(Random random) {
        return this.id == BlockList.melon.id ? 4 : 1;
    }

    @Override
    public int dropID(int id, Random random) {
        return this.id == BlockList.melon.id ? ItemList.melonSlice.id : this.id;
    }

    @Override
    public void onPlaced(World world, Player player, int x, int y, int z, int side) {
        super.onPlaced(world, player, x, y, z, side);
        ItemStack stack = player.inventory.getCurrentItem();
        if (stack == null) {
            stack = player.inventory.getOffHandItem();
        }

        if (stack.itemID == BlockList.pumpkin.id && stack.itemDmg == 1) {
            int direction = MathHelper.floor_double(player.yaw * 4.0F / 360.0F + 0.5) & 3;
            if (direction == 0) {
                world.setBlockMetadataWithNotify(x, y, z, 2);
            }

            if (direction == 1) {
                world.setBlockMetadataWithNotify(x, y, z, 5);
            }

            if (direction == 2) {
                world.setBlockMetadataWithNotify(x, y, z, 3);
            }

            if (direction == 3) {
                world.setBlockMetadataWithNotify(x, y, z, 4);
            }

            if (world.getBlockId(x, y - 1, z) == BlockList.snowBlock.id && world.getBlockId(x, y - 2, z) == BlockList.snowBlock.id) {
                if (!world.isOnline) {
                    world.setBlockWithNotify(x, y, z, 0);
                    world.setBlockWithNotify(x, y - 1, z, 0);
                    world.setBlockWithNotify(x, y - 2, z, 0);
                    Snowman snowman = new Snowman(world);
                    snowman.setPosAndRot(x + 0.5, y - 1.95, z + 0.5, 0.0F, 0.0F);
                    world.spawnEntity(snowman);
                    player.triggerAchievement(AchievementList.snowman);
                }

                for (int i = 0; i < 120; i++) {
                    world.spawnParticle(
                        ParticleType.SNOWSHOVEL, x + world.rand.nextDouble(), y - 2 + world.rand.nextDouble() * 2.5, z + world.rand.nextDouble(), 0.0, 0.0, 0.0
                    );
                }
            }
        } else {
            int meta = stack.itemDmg & 63;
            switch (side) {
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
    }

    @Override
    public void registerIcons(IconRegister r) {
        this.top = r.registerIcon("fruits/" + this.name + "Top");
        this.front = r.registerIcon("fruits/" + this.name + "Side");
        this.blockIcon = r.registerIcon("fruits/pumpkinCarved");
    }

    @Override
    public void getSubStacks(int id, CreativeTabs ct, List<ItemStack> ls) {
        ls.add(new ItemStack(id, 1, 0));
        if (id == BlockList.pumpkin.id) {
            ls.add(new ItemStack(id, 1, 1));
        }
    }

    @Override
    public int dropMeta(int id) {
        return this.id == BlockList.pumpkin.id && id >= 1 && id < 6 ? 1 : 0;
    }

    @Override
    public int pickMeta(int id) {
        return this.dropMeta(id);
    }

    @Override
    public int guaranteedMeta(int id) {
        return this.dropMeta(id);
    }

    @Override
    public ItemStack getCarverContents(int id, int dmg) {
        return this.id == BlockList.pumpkin.id && id == 0 ? new ItemStack(BlockList.pumpkin.id, 1, 1) : null;
    }
}
