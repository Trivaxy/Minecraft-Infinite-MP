package net.minecraft.game.entity.mob.monster.slime;

import com.mojang.nbt.tag.special.CompoundTag;
import java.util.List;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.hostile.Aggro;
import net.minecraft.game.entity.mob.ai.hostile.DetectEnemy;
import net.minecraft.game.entity.mob.follower.Decoy;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.Rarity;
import net.minecraft.game.item.block.BlockItem;
import net.minecraft.game.world.World;
import net.minecraft.statistics.achievement.AchievementList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.random.WeightedRandom;

public class AquaCube extends Slime {
    public Block block = null;
    public int meta;
    public boolean tamed = false;
    private static AquaCube.RandomBlock[] blocks = new AquaCube.RandomBlock[]{
        new AquaCube.RandomBlock(65, BlockList.grass, 9),
        new AquaCube.RandomBlock(65, BlockList.stone, 8),
        new AquaCube.RandomBlock(65, BlockList.dirt, 7),
        new AquaCube.RandomBlock(55, BlockList.wood, 16),
        new AquaCube.RandomBlock(55, BlockList.quickSoil, 0),
        new AquaCube.RandomBlock(55, BlockList.sand, 0),
        new AquaCube.RandomBlock(55, BlockList.gravel, 0),
        new AquaCube.RandomBlock(55, BlockList.clay, 0),
        new AquaCube.RandomBlock(45, BlockList.snowBlock, 0),
        new AquaCube.RandomBlock(45, BlockList.ice, 0),
        new AquaCube.RandomBlock(45, BlockList.melon, 0),
        new AquaCube.RandomBlock(45, BlockList.pumpkin, 0),
        new AquaCube.RandomBlock(35, BlockList.bone, 0),
        new AquaCube.RandomBlock(35, BlockList.slimeBlock, 0),
        new AquaCube.RandomBlock(25, BlockList.aetherIron, 0),
        new AquaCube.RandomBlock(25, BlockList.oreTopaz, 0),
        new AquaCube.RandomBlock(10, BlockList.oreAdamantine, 0)
    };

    public AquaCube(World world) {
        super(world);
        this.setSlimeSize(3);
        this.detections.tasks.clear();
        this.detections.addTask(1, new AquaCube.AngryAt(this, false));
        this.detections.addTask(2, new AquaCube.DetectUntamed(this, Decoy.class, 16.0F, 0, true));
        this.detections.addTask(3, new AquaCube.DetectUntamed(this, Player.class, 16.0F, 0, true));
        this.particle = ParticleType.SPLASH;
        AquaCube.RandomBlock loot = WeightedRandom.getRandomItem(this.rand, blocks);
        this.block = loot.block;
        this.meta = loot.meta;
        if (this.block.hardness > 0.0F) {
            this.setHealth((int)(this.block.hardness * 10.0F));
            this.attackStrength = (int)(this.block.hardness * 10.0F);
        }
    }

    @Override
    public boolean canBurn() {
        return this.block == null || this.block != BlockList.netherrack && this.block != BlockList.magma;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public int getBrightnessInt(float brightness) {
        return this.block != null && Block.lightValue[this.block.id] > 0.0F ? 15728880 : super.getBrightnessInt(brightness);
    }

    @Override
    public int getSpawnID() {
        return 77;
    }

    @Override
    public void mobTick() {
        if (this.block != null) {
            if (this.block.slipperiness >= 1.0F && this.onGround) {
                this.motionX /= 0.55F;
                this.motionZ /= 0.55F;
            }

            if (this.block.hardness > 0.0F) {
                if (this.isInWater() && this.block.hardness < 1.0F) {
                    this.motionY = this.motionY + 0.04F / this.block.hardness;
                }

                if (this.collidedHorizontally) {
                    this.motionX = this.motionX - this.motionX * (0.04F / this.block.hardness);
                    this.motionZ = this.motionZ - this.motionZ * (0.04F / this.block.hardness);
                }
            }
        }

        if (this.block == BlockList.magma) {
            List<Entity> entities = this.world.getEntities(this, this.boundingBox.expand(0.2, 0.0, 0.2));
            if (entities != null) {
                for (int entity = 0; entity < entities.size(); entity++) {
                    if (entities.get(entity) instanceof Mob) {
                        entities.get(entity).damageEntity(DamageSource.inFire, 1);
                    }
                }
            }
        }

        super.mobTick();
    }

    @Override
    public void setType() {
        this.texture = "/mob/slime/slime_cerulean.png";
    }

    @Override
    public void setDead() {
        if (this.block != null) {
            if (this.block == BlockList.tnt) {
                this.world.createExplosion(this, this.posX, this.posY, this.posZ, 6.0F);
            } else if (this.block == BlockList.slimeBlock) {
                Slime slime = new Slime(this.world);
                slime.setSlimeSize(this.getSlimeSize());
                slime.teleportTo(this.posX, this.posY + 0.5 + this.getEyeHeight(), this.posZ, this.rand.nextFloat() * 360.0F, 0.0F);
                this.world.spawnEntity(slime);
            }
        }

        this.isDead = true;
    }

    @Override
    public void jump() {
        if (this.block != null && this.block.hardness > 0.0F) {
            this.motionY = 0.5 / this.block.hardness;
        } else {
            this.motionY = 0.5;
        }
    }

    @Override
    public void knockBack(Entity entity, double forceX, double forceZ) {
        boolean hasBlock = this.block != null && this.block.hardness > 0.0F && this.block.slipperiness < 1.0F;
        float dist = MathHelper.sqrt_double(forceX * forceX + forceZ * forceZ);
        float force = entity.knockback / (hasBlock ? this.block.hardness : 1.0F);
        this.motionX -= forceX / dist * force;
        this.motionZ -= forceZ / dist * force;
        if (this.block == null) {
            this.motionY += 0.4F;
        } else if (this.block.slipperiness < 1.0F && this.block.hardness > 0.0F) {
            this.motionY = this.motionY + 0.5F / this.block.hardness;
        }
    }

    @Override
    protected void dropFewItems(int lootingModifier) {
        if (this.block != null) {
            int count = 1;
            if (!this.tamed) {
                if (this.block.rarity == Rarity.EPIC) {
                    count = this.rand.nextInt(3) + 1;
                } else if (this.block.rarity == Rarity.RARE) {
                    count = this.rand.nextInt(5) + 1;
                } else {
                    count = this.rand.nextInt(6) + 3;
                }
            }

            DroppedItem item = new DroppedItem(this.world, this.posX, this.posY, this.posZ, new ItemStack(this.block.id, count, this.meta));
            item.pickupDelay = 10;
            this.world.spawnEntity(item);
        }
    }

    @Override
    public boolean damageEntity(DamageSource source, int amount) {
        if (this.block != null && source.damageType == "explosion") {
            amount = (int)(amount / this.block.blastResistance);
            if (amount < 1) {
                amount = 1;
            }
        }

        return super.damageEntity(source, amount);
    }

    @Override
    public void onCollideWith(Entity player) {
        if (!this.tamed) {
            super.onCollideWith(player);
        }
    }

    @Override
    public boolean interact(Player player) {
        ItemStack heldItem = player.getCurrentEquippedItem();
        if (player.getUsing()) {
            heldItem = player.inventory.getOffHandItem();
        }

        if (this.block != null && heldItem != null && heldItem.getItem() == ItemList.shears) {
            if (!this.world.isOnline) {
                int count = 1;
                if (!this.tamed) {
                    if (this.block.rarity == Rarity.EPIC) {
                        count = this.rand.nextInt(3) + 1;
                    } else if (this.block.rarity == Rarity.RARE) {
                        count = this.rand.nextInt(5) + 1;
                    } else {
                        count = this.rand.nextInt(6) + 3;
                    }
                }

                DroppedItem drop = this.dropItem(this.block.id, count, this.meta, 1.0F);
                drop.motionY = drop.motionY + this.rand.nextFloat() * 0.05F;
                drop.motionX = drop.motionX + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.5F;
                drop.motionZ = drop.motionZ + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.5F;
                drop.pickupDelay = 20;
                this.clearBlock();
            }

            if (player.getUsing()) {
                player.swingOffhandItem();
            } else {
                player.swingItem();
            }

            heldItem.damageItem(1, player);
            this.setHealth(this.getSlimeSize() * this.getSlimeSize());
            this.world.playSoundEffect(this.posX + 0.5, this.posY + 0.5, this.posZ + 0.5, "random.shear", 1.0F, this.rand.nextFloat() * 0.4F + 0.8F);
            return true;
        } else if (!this.tamed && heldItem != null && heldItem.getItem() == ItemList.bark) {
            this.decrStackSize(player, heldItem);
            if (!this.world.isOnline) {
                if (this.rand.nextInt(3) == 0) {
                    this.tamed = true;
                    this.health = 20;
                    this.showParticles(true);
                    player.triggerAchievement(AchievementList.tame);
                    this.world.setEntityState(this, (byte)7);
                    this.moveForward = this.landSpeed = 0.0F;
                } else {
                    this.showParticles(false);
                    this.world.setEntityState(this, (byte)6);
                }
            }

            if (player.getUsing()) {
                player.swingOffhandItem();
            } else {
                player.swingItem();
            }

            return true;
        } else {
            if (this.tamed && heldItem != null && heldItem.itemID < 4096 && heldItem.getItem() instanceof BlockItem) {
                Block block = BlockList.blocks[heldItem.itemID];
                if (this.block == null
                    && block != null
                    && (
                        block.getRenderType() == Block.RenderType.NORMAL
                            || block.getRenderType() == Block.RenderType.CHEST
                            || block.getRenderType() == Block.RenderType.SLIME
                    )) {
                    this.block = BlockList.blocks[heldItem.itemID];
                    this.meta = heldItem.itemDmg;
                    this.decrStackSize(player, heldItem);
                    this.world.playEntitySound(this, "mob.slimeattack", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
                    this.moveForward = this.landSpeed = 0.0F;
                    if (this.block == BlockList.grass) {
                        player.triggerAchievement(AchievementList.grass);
                    }

                    if (player.getUsing()) {
                        player.swingOffhandItem();
                    } else {
                        player.swingItem();
                    }
                }
            }

            return false;
        }
    }

    private void decrStackSize(Player player, ItemStack stack) {
        if (player.properties.gamemode != 1) {
            stack.stackSize--;
        }

        if (stack.stackSize <= 0) {
            if (player.getUsing()) {
                player.inventory.setSlotContents(44, null);
            } else {
                player.inventory.setSlotContents(player.inventory.currentSlot, null);
            }
        }
    }

    private void showParticles(boolean success) {
        ParticleType particle = ParticleType.HEART;
        if (!success) {
            particle = ParticleType.SMOKE;
        }

        for (int i = 0; i < 7; i++) {
            this.world
                .spawnParticle(
                    particle,
                    this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
                    this.posY + 0.5 + this.rand.nextFloat() * this.height,
                    this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
                    this.rand.nextGaussian() * 0.02,
                    this.rand.nextGaussian() * 0.02,
                    this.rand.nextGaussian() * 0.02
                );
        }
    }

    public void clearBlock() {
        this.block = null;
        this.meta = 0;
        this.setHealth(this.getSlimeSize() * this.getSlimeSize());
    }

    @Override
    protected void updateAI() {
        if (!this.tamed || this.block == null) {
            super.updateAI();
        }
    }

    @Override
    public boolean canSpawnHere() {
        return this.world.settings.getDifficulty() > 0
            && this.world.isClear(this.boundingBox)
            && this.world.getBoundingBoxes(this, this.boundingBox).size() == 0
            && !this.world.containsLiquid(this.boundingBox);
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        if (this.block != null) {
            tag.setInteger("ID", this.block.id);
            tag.setInteger("Meta", this.meta);
        }

        tag.setBoolean("Tamed", this.tamed);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        this.block = BlockList.blocks[tag.getInteger("ID")];
        this.meta = tag.getInteger("Meta");
        this.tamed = tag.getBoolean("Tamed");
        if (this.block != null && this.block.hardness > 0.0F) {
            this.setHealth((int)(this.block.hardness * 10.0F));
        }
    }

    @Override
    public boolean canDespawn() {
        return this.tamed ? false : super.canDespawn();
    }

    private class AngryAt extends Aggro {
        private AquaCube cube;

        public AngryAt(Mob target, boolean agro) {
            super(target, agro);
            this.cube = (AquaCube)target;
        }

        @Override
        public boolean canExecute() {
            return !this.cube.tamed && super.canExecute();
        }
    }

    private class DetectUntamed extends DetectEnemy {
        private AquaCube cube;

        public DetectUntamed(Mob target, Class<?> targetClass, float dist, int chance, boolean chase) {
            super(target, targetClass, dist, chance, chase);
            this.cube = (AquaCube)target;
        }

        @Override
        public boolean canExecute() {
            return !this.cube.tamed && super.canExecute();
        }

        @Override
        public boolean continueExecuting() {
            return !this.cube.tamed && super.continueExecuting();
        }
    }

    private static class RandomBlock extends WeightedRandom.Choice {
        public final Block block;
        public final int meta;

        public RandomBlock(int weight, Block cock, int meta) {
            super(weight);
            this.block = cock;
            this.meta = meta;
        }
    }
}
