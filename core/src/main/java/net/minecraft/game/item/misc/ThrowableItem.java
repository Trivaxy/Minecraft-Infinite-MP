package net.minecraft.game.item.misc;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.stepsound.StepSound;
import net.minecraft.game.block.tileentity.other.SpawnerEntity;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.entity.projectile.Egg;
import net.minecraft.game.entity.projectile.Fireball;
import net.minecraft.game.entity.projectile.Projectile;
import net.minecraft.game.entity.projectile.Rock;
import net.minecraft.game.entity.projectile.Sandball;
import net.minecraft.game.entity.projectile.Snowball;
import net.minecraft.game.entity.projectile.Windball;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.sky.SkyBiome;
import net.minecraft.statistics.StatList;
import net.minecraft.statistics.achievement.AchievementList;

public class ThrowableItem extends Item {
    public int type;
    private Icon falseIdol;
    private Icon scythe;

    public ThrowableItem(int id, int type) {
        super(id);
        this.maxStackSize = 16;
        this.type = type;
        this.priorityItem = true;
    }

    @Override
    public boolean useOnBlock(ItemStack stack, Player player, World world, int x, int y, int z, int side) {
        if (this.type == 5 && world.getBlock(x, y, z) == BlockList.mobSpawner && world.getTileEntity(x, y, z) != null) {
            SpawnerEntity spawner = (SpawnerEntity)world.getTileEntity(x, y, z);
            if (spawner.spawns < spawner.limit) {
                return false;
            }

            spawner.spawns = 0;
            if (player.properties.gamemode != 1) {
                stack.stackSize--;
            }

            world.playEntitySound(player, "mob.ghast.shoot", 0.5F, 1.0F / (rand.nextFloat() * 0.4F + 0.8F));
            player.addStat(StatList.useStats[this.id], 1);
            player.triggerAchievement(AchievementList.spawner);
            return true;
        } else if (this.type == 4) {
            if (side == 0) {
                y--;
            }

            if (side == 1) {
                y++;
            }

            if (side == 2) {
                z--;
            }

            if (side == 3) {
                z++;
            }

            if (side == 4) {
                x--;
            }

            if (side == 5) {
                x++;
            }

            if (stack.stackSize == 0) {
                return false;
            }

            Block block = BlockList.blocks[BlockList.pebble.id];
            if (block.placeableAt(world, x, y, z)) {
                int meta = world.getBiomeAt(x, z).defaultStoneType;
                if (meta == 1 && world.getBiomeAt(x, z).getTemperatureEnum() != Biome.Temperature.COLD) {
                    meta = 0;
                }

                if (world.getBiomeAt(x, z) instanceof SkyBiome) {
                    meta = 8;
                }

                if (world.setBlockAndMetadataWithNotify(x, y, z, BlockList.pebble.id, meta)) {
                    block.onPlaced(world, player, x, y, z, side);
                    StepSound snd = block.getStepSound(0);
                    world.playBlockSound(player, x + 0.5F, y + 0.5F, z + 0.5F, snd.getSound(), (snd.getVolume() + 1.0F) / 2.0F, snd.getPitch() * 0.8F);
                    if (player.properties.gamemode != 1) {
                        stack.stackSize--;
                    }

                    player.addStat(StatList.useStats[this.id], 1);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public ItemStack use(ItemStack stack, World world, Player player) {
        if (player.properties.gamemode != 1) {
            stack.stackSize--;
        }

        if (player.inventory.canUseItem() && player.inventory.getOffHandItem().getItem() == stack.getItem()) {
            player.swingOffhandItem();
        } else {
            player.swingItem();
        }

        if (this.type == 5) {
            world.playEntitySound(player, "mob.ghast.shoot", 0.5F, 1.0F / (rand.nextFloat() * 0.4F + 0.8F));
        } else {
            world.playEntitySound(player, "random.throw", 0.5F, 0.4F / (rand.nextFloat() * 0.4F + 0.8F));
        }

        Projectile throwable = new Snowball(world, player, this.type);
        switch (this.type) {
            case 1:
                throwable = new Egg(world, player, this.type);
                break;
            case 2:
                throwable = new Windball(world, player, this.type);
                break;
            case 3:
                throwable = new Sandball(world, player, 0);
                break;
            case 4:
                throwable = new Rock(world, player, 3);
                break;
            case 5:
                throwable = new Fireball(world, player, this.type);
        }

        if (!world.isOnline) {
            world.spawnEntity(throwable);
        }

        player.addStat(StatList.useStats[this.id], 1);
        return stack;
    }

    @Override
    public void registerIcons(IconRegister rock) {
        this.itemIcon = rock.registerIcon("projectile/" + this.internalName);
        this.falseIdol = rock.registerIcon("orb");
        this.scythe = rock.registerIcon("scytheHead");
    }

    @Override
    public Icon getIconFromMeta(int meta) {
        if (meta == -1) {
            return this.falseIdol;
        } else {
            return meta == -2 ? this.scythe : this.itemIcon;
        }
    }

    @Override
    public void triggerAchievement(Player player) {
        if (this.type == 4) {
            player.triggerAchievement(AchievementList.getHard);
        }
    }
}
