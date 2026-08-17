package net.minecraft.game.item.misc;

import net.minecraft.game.ParticleType;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.animal.Goat;
import net.minecraft.game.entity.mob.animal.aquatic.Fish;
import net.minecraft.game.entity.mob.animal.cow.Cow;
import net.minecraft.game.entity.mob.monster.slime.AquaCube;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.physics.TargetingSystem;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.dimension.HellDimension;
import net.minecraft.game.world.dimension.SkyDimension;
import net.minecraft.statistics.StatList;
import net.minecraft.statistics.achievement.AchievementList;
import net.minecraft.util.math.MathHelper;

public class Bucket extends Item {
    private int liquidID;

    public Bucket(int itemid, int id) {
        super(itemid);
        this.maxDamage = 64;
        this.liquidID = id;
    }

    @Override
    public ItemStack use(ItemStack stack, World world, Player player) {
        float pitch = player.prevPitch + (player.pitch - player.prevPitch);
        float yaw = player.prevYaw + (player.yaw - player.prevYaw);
        double x = player.prevX + (player.posX - player.prevX);
        double y = player.prevY + (player.posY - player.prevY) * 1.0 + player.yOffset - player.yOffset;
        double z = player.prevZ + (player.posZ - player.prevZ);
        Vector position = Vector.createVector(x, y, z);
        float yaw1 = MathHelper.cos(-yaw * (float) (Math.PI / 180.0) - (float) Math.PI);
        float yaw2 = MathHelper.sin(-yaw * (float) (Math.PI / 180.0) - (float) Math.PI);
        float pitch1 = -MathHelper.cos(-pitch * (float) (Math.PI / 180.0));
        float pitch2 = MathHelper.sin(-pitch * (float) (Math.PI / 180.0));
        float angle1 = yaw2 * pitch1;
        float angle2 = yaw1 * pitch1;
        double offset = 5.0;
        Vector lookVector = position.addVector(angle1 * offset, pitch2 * offset, angle2 * offset);
        TargetingSystem target = world.rayTrace(position, lookVector, this.liquidID == 0);
        if (target == null) {
            return stack;
        }

        if (target.target == TargetingSystem.HitType.TILE) {
            int hitX = target.blockX;
            int hitY = target.blockY;
            int hitZ = target.blockZ;
            if (this.liquidID == 0) {
                if (this.id == ItemList.bucketSlime.id) {
                    AquaCube cube = new AquaCube(world);
                    cube.setPosition(hitX, hitY + 1, hitZ);
                    cube.tamed = true;
                    cube.clearBlock();
                    world.spawnEntity(cube);
                    if (player.inventory.getOffHandItem() != null) {
                        player.swingOffhandItem();
                    } else {
                        player.swingItem();
                    }

                    world.playBlockSound(player, hitX, hitY, hitZ, "mob.slimeattack", 1.0F, 1.0F / (rand.nextFloat() * 0.4F + 1.2F));
                    if (player.properties.gamemode != 1) {
                        return new ItemStack(ItemList.bucketEmpty);
                    }
                }

                if (world.getBlockMaterial(hitX, hitY, hitZ) == MaterialList.water && world.getBlockMetadata(hitX, hitY, hitZ) == 0) {
                    world.setBlockWithNotify(hitX, hitY, hitZ, 0);
                    world.playBlockSound(player, hitX, hitY, hitZ, "random.waterBucket", 1.0F, 1.0F / (rand.nextFloat() * 0.4F + 1.2F));
                    player.addStat(StatList.useStats[this.id], 1);
                    if (player.properties.gamemode == 1) {
                        return stack;
                    }

                    if (stack.stackSize == 1) {
                        return new ItemStack(ItemList.bucketWater, 1);
                    }

                    if (!player.inventory.addItemToInventory(new ItemStack(ItemList.bucketWater, 1))) {
                        DroppedItem item = new DroppedItem(world, hitX, hitY, hitZ, new ItemStack(ItemList.bucketWater, 1));
                        world.spawnEntity(item);
                    }

                    return new ItemStack(stack.getItem(), stack.stackSize - 1);
                }

                if (world.getBlockMaterial(hitX, hitY, hitZ) == MaterialList.lava && world.getBlockMetadata(hitX, hitY, hitZ) == 0) {
                    world.setBlockWithNotify(hitX, hitY, hitZ, 0);
                    world.playBlockSound(player, hitX, hitY, hitZ, "random.lavaBucket", 1.0F, 1.0F / (rand.nextFloat() * 0.4F + 1.2F));
                    player.addStat(StatList.useStats[this.id], 1);
                    if (player.properties.gamemode == 1) {
                        return stack;
                    }

                    if (stack.stackSize == 1) {
                        return new ItemStack(ItemList.bucketLava, 1);
                    }

                    if (!player.inventory.addItemToInventory(new ItemStack(ItemList.bucketLava, 1))) {
                        DroppedItem item = new DroppedItem(world, hitX, hitY, hitZ, new ItemStack(ItemList.bucketLava, 1));
                        world.spawnEntity(item);
                    }

                    return new ItemStack(stack.getItem(), stack.stackSize - 1);
                }
            } else {
                if (target.sideHit == 0) {
                    hitY--;
                }

                if (target.sideHit == 1) {
                    hitY++;
                }

                if (target.sideHit == 2) {
                    hitZ--;
                }

                if (target.sideHit == 3) {
                    hitZ++;
                }

                if (target.sideHit == 4) {
                    hitX--;
                }

                if (target.sideHit == 5) {
                    hitX++;
                }

                player.addStat(StatList.useStats[this.id], 1);
                if (world.getBlockId(hitX, hitY, hitZ) == 0 || !world.getBlockMaterial(hitX, hitY, hitZ).isSolid()) {
                    if (world.currDim instanceof HellDimension && this.liquidID == BlockList.waterMoving.id) {
                        world.playSoundEffect(
                            player, hitX + 0.5, hitY + 0.5, hitZ + 0.5, "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F
                        );

                        for (int i = 0; i < 8; i++) {
                            world.spawnParticle(ParticleType.LARGESMOKE, hitX + Math.random(), hitY + Math.random(), hitZ + Math.random(), 0.0, 0.0, 0.0);
                        }
                    } else if (world.currDim instanceof SkyDimension && this.liquidID == BlockList.lavaMoving.id) {
                        world.playSoundEffect(
                            player, hitX + 0.5, hitY + 0.5, hitZ + 0.5, "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F
                        );

                        for (int i = 0; i < 8; i++) {
                            world.spawnParticle(ParticleType.LARGESMOKE, hitX + Math.random(), hitY + Math.random(), hitZ + Math.random(), 0.0, 0.0, 0.0);
                        }
                    } else {
                        world.setBlockAndMetadataWithNotify(hitX, hitY, hitZ, this.liquidID, 0);
                    }

                    if (this.id == ItemList.bucketFish.id) {
                        Fish fish = new Fish(world);
                        fish.setPosition(hitX + 0.5F, hitY, hitZ + 0.5F);
                        world.spawnEntity(fish);
                    }

                    world.playBlockSound(player, hitX, hitY, hitZ, "random.emptyBucket", 1.0F, 1.0F / (rand.nextFloat() * 0.4F + 1.2F));
                    if (player.properties.gamemode != 1) {
                        return new ItemStack(ItemList.bucketEmpty);
                    }

                    if (player.inventory.getOffHandItem() != null) {
                        player.swingOffhandItem();
                    } else {
                        player.swingItem();
                    }
                }
            }
        }

        return stack;
    }

    @Override
    public void useOnEntity(ItemStack stack, Mob targetMob, Player player) {
        if (this == ItemList.bucketEmpty) {
            Item returnItem = null;
            if (targetMob instanceof Fish) {
                returnItem = ItemList.bucketFish;
            }

            if (targetMob instanceof Cow) {
                returnItem = ItemList.bucketMilk;
            }

            if (targetMob instanceof Goat) {
                returnItem = ItemList.bucketMilk;
                targetMob.attackTarget = targetMob.lastTarget = player;
            }

            if (targetMob instanceof AquaCube && ((AquaCube)targetMob).tamed) {
                returnItem = ItemList.bucketSlime;
            }

            if (stack != null && returnItem != null) {
                if (stack.stackSize > 1) {
                    if (!player.inventory.addItemToInventory(new ItemStack(returnItem, 1))) {
                        DroppedItem item = new DroppedItem(
                            targetMob.world, targetMob.posX, targetMob.posY, targetMob.posZ, new ItemStack(ItemList.bucketFish, 1)
                        );
                        player.world.spawnEntity(item);
                    }

                    stack.stackSize--;
                } else if (player.getUsing()) {
                    player.inventory.setSlotContents(44, new ItemStack(returnItem, 1));
                } else {
                    player.inventory.setSlotContents(player.inventory.currentSlot, new ItemStack(returnItem, 1));
                }

                if (player.getUsing()) {
                    player.swingOffhandItem();
                } else {
                    player.swingItem();
                }

                if (returnItem == ItemList.bucketFish) {
                    int hitX = MathHelper.floor_double(targetMob.posX);
                    int hitY = MathHelper.floor_double(targetMob.posY);
                    int hitZ = MathHelper.floor_double(targetMob.posZ);
                    if (player.world.getBlockMaterial(hitX, hitY, hitZ) == MaterialList.water && player.world.getBlockMetadata(hitX, hitY, hitZ) == 0) {
                        player.world
                            .setBlockWithNotify(
                                MathHelper.floor_double(targetMob.posX), MathHelper.floor_double(targetMob.posY), MathHelper.floor_double(targetMob.posZ), 0
                            );
                    }

                    player.triggerAchievement(AchievementList.fishBucket);
                    targetMob.setDead();
                } else if (returnItem == ItemList.bucketSlime) {
                    AquaCube cube = (AquaCube)targetMob;
                    if (cube.block != null) {
                        DroppedItem drop = new DroppedItem(cube.world, cube.posX, cube.posY, cube.posZ, new ItemStack(cube.block.id, 1, cube.meta));
                        cube.world.spawnEntity(drop);
                    }

                    cube.world.playBlockSound(player, cube.posX, cube.posY, cube.posZ, "mob.slimeattack", 1.0F, 1.0F / (rand.nextFloat() * 0.4F + 1.2F));
                    targetMob.isDead = true;
                } else if (returnItem == ItemList.bucketMilk) {
                    player.triggerAchievement(AchievementList.milkCow);
                }
            }
        }
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.itemIcon = ir.registerIcon("bucket/" + this.internalName);
    }
}
