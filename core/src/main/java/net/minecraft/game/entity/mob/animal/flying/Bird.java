package net.minecraft.game.entity.mob.animal.flying;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.ParticleType;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.entity.mob.ai.flying.FlyAtRandom;
import net.minecraft.game.entity.mob.ai.flying.LookAroundFlying;
import net.minecraft.game.entity.mob.ai.flying.LookAtOnGround;
import net.minecraft.game.entity.mob.ai.hostile.Attack;
import net.minecraft.game.entity.mob.ai.hostile.DetectEnemy;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.mob.ai.util.MoveControl;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.block.Seeds;
import net.minecraft.game.world.World;
import net.minecraft.statistics.achievement.AchievementList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.random.WeightedRandom;

public class Bird extends FlyingMob {
    public boolean tamed;
    public boolean giveItem;
    private String birdType;
    private static Bird.LootHolder[] loots = new Bird.LootHolder[]{
        new Bird.LootHolder(100, ItemList.stick),
        new Bird.LootHolder(5, ItemList.diamond),
        new Bird.LootHolder(15, ItemList.emerald),
        new Bird.LootHolder(15, ItemList.sapphire),
        new Bird.LootHolder(15, ItemList.ruby),
        new Bird.LootHolder(25, ItemList.scroll),
        new Bird.LootHolder(45, ItemList.nuggetIron),
        new Bird.LootHolder(35, ItemList.nuggetGold),
        new Bird.LootHolder(65, ItemList.coal),
        new Bird.LootHolder(85, ItemList.rock),
        new Bird.LootHolder(75, ItemList.reeds),
        new Bird.LootHolder(95, ItemList.silk),
        new Bird.LootHolder(90, ItemList.flint),
        new Bird.LootHolder(80, ItemList.berry),
        new Bird.LootHolder(85, BlockList.dandelion),
        new Bird.LootHolder(85, BlockList.rose),
        new Bird.LootHolder(85, BlockList.lilac),
        new Bird.LootHolder(85, BlockList.petunia)
    };

    public Bird(World world) {
        super(world);
        this.setSize(0.5F, 0.5F);
        this.setHealth(3);
        this.moveSpeed = 0.33F;
        this.type = (byte)this.rand.nextInt(6);
        this.setType();
        this.behaviors.addTask(1, new Swim(this));
        this.behaviors.addTask(2, new Bird.GiveGift(this, this.moveSpeed, false, false));
        this.behaviors.addTask(3, new Bird.SpookedByPlayer(this, 16.0F));
        this.behaviors.addTask(4, new FlyAtRandom(this, 6.0F, 0.15F));
        this.behaviors.addTask(5, new LookAroundFlying(this, false));
        this.behaviors.addTask(6, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(7, new LookAtOnGround(this, Player.class, 6.0F));
        this.behaviors.addTask(8, new LookAround(this));
        this.detections.addTask(2, new Bird.DetectOwner(this, Player.class, 32.0F, 0, true));
    }

    @Override
    public void setType() {
        switch (this.type) {
            case 1:
                this.birdType = "birdblue";
                break;
            case 2:
                this.birdType = "birdred";
                break;
            case 3:
                this.birdType = "birdgreen";
                break;
            case 4:
                this.birdType = "birdyellow";
                break;
            case 5:
                this.birdType = "birdwhite";
                break;
            default:
                this.birdType = "birdblack";
        }

        this.texture = "/mob/bird/" + this.birdType + ".png";
    }

    @Override
    public int getSpawnID() {
        return 21;
    }

    @Override
    public boolean canDespawn() {
        return this.tamed ? false : super.canDespawn();
    }

    @Override
    public void updateState(byte state) {
        if (state == 7) {
            this.showParticles(true);
        } else if (state == 6) {
            this.showParticles(false);
        } else {
            super.updateState(state);
        }
    }

    @Override
    public void mobTick() {
        super.mobTick();
        if (!this.world.isOnline && this.tamed && !this.world.isDaytime()) {
            this.giveItem = true;
        }

        if (this.navigator.noPath() && this.onGround && this.rand.nextInt(100) == 0 && !this.giveItem) {
            this.flying = true;
            this.moveControl = this.flyMovement;
        }
    }

    @Override
    public final boolean isInWater() {
        return this.world.handleWaterMovement(this.boundingBox, MaterialList.water, this);
    }

    @Override
    public boolean interact(Player player) {
        ItemStack heldItem = player.inventory.getCurrentItem();
        if (player.getUsing()) {
            heldItem = player.inventory.getOffHandItem();
        }

        if (this.tamed) {
            if (heldItem == null || !(heldItem.getItem() instanceof Seeds)) {
                this.mountEntity(player);
                return true;
            }

            if (this.health < this.maxHealth) {
                this.heal(1);
                this.decrStackSize(player, heldItem);
                return true;
            }
        } else if (heldItem != null && heldItem.getItem() instanceof Seeds) {
            this.decrStackSize(player, heldItem);
            if (!this.world.isOnline) {
                if (this.rand.nextInt(3) == 0) {
                    this.tamed = true;
                    this.health = 20;
                    this.showParticles(true);
                    player.triggerAchievement(AchievementList.tame);
                    this.world.setEntityState(this, (byte)7);
                } else {
                    this.showParticles(false);
                    this.world.setEntityState(this, (byte)6);
                }
            }

            return true;
        }

        return false;
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

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setBoolean("Tamed", this.tamed);
        tag.setBoolean("Drop", this.giveItem);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        this.tamed = tag.getBoolean("Tamed");
        this.giveItem = tag.getBoolean("Drop");
    }

    @Override
    public float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public String getSound() {
        return "mob." + this.birdType;
    }

    @Override
    protected String getHurtSound() {
        return "mob." + this.birdType;
    }

    @Override
    protected String getDeathSound() {
        return "mob." + this.birdType;
    }

    @Override
    public void playStepSound() {
        this.world.playEntitySound(this, "mob.chickenstep", 0.15F, 1.0F);
    }

    @Override
    public int getDrop() {
        return ItemList.feather.id;
    }

    @Override
    public double getYOffset() {
        return -0.9;
    }

    @Override
    public float getPathWeight(int xCoord, int yCoord, int zCoord) {
        return this.world.getBlockId(xCoord, yCoord - 1, zCoord) != BlockList.grass.id
                && this.world.getBlockId(xCoord, yCoord - 1, zCoord) != BlockList.leaves.id
            ? this.world.getBrightness(xCoord, yCoord, zCoord) - 0.5F
            : 10.0F;
    }

    @Override
    public boolean canSpawnHere() {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.boundingBox.minY);
        int z = MathHelper.floor_double(this.posZ);
        return (this.world.getBlockId(x, y - 1, z) == BlockList.grass.id || this.world.getBlockId(x, y - 1, z) == BlockList.leaves.id)
            && this.world.getBlockBrightness(x, y, z) > 8
            && this.getPathWeight(x, y, z) >= 0.0F
            && this.world.isClear(this.boundingBox)
            && this.world.getBoundingBoxes(this, this.boundingBox).size() == 0
            && !this.world.containsLiquid(this.boundingBox);
    }

    private class DetectOwner extends DetectEnemy {
        private Bird bird;

        private DetectOwner(Mob target, Class<?> targetClass, float dist, int chance, boolean chase) {
            super(target, targetClass, dist, chance, chase);
            this.bird = (Bird)target;
        }

        @Override
        public boolean canExecute() {
            return this.bird.tamed && this.bird.giveItem && this.bird.world.isDaytime() && super.canExecute();
        }

        @Override
        public void start() {
            super.start();
            this.bird.flying = false;
            this.bird.moveControl = new MoveControl(this.bird);
        }
    }

    private class GiveGift extends Attack {
        private Bird bird;

        private GiveGift(Mob src, float spd, boolean priority, boolean sprint) {
            super(src, spd, priority, sprint);
            this.bird = (Bird)src;
        }

        @Override
        protected void attack() {
            this.bird.world.playEntitySound(this.bird, "mob.chickenplop", 1.0F, (this.bird.rand.nextFloat() - this.bird.rand.nextFloat()) * 0.2F + 1.0F);
            Bird.LootHolder randomItem = WeightedRandom.getRandomItem(this.bird.rand, Bird.loots);
            if (this.bird.attackTarget instanceof Player) {
                ((Player)this.bird.attackTarget).triggerAchievement(AchievementList.gift);
            }

            this.bird.dropItem(randomItem.itemId, 1);
            this.bird.giveItem = false;
            this.bird.attackTarget = null;
        }
    }

    private static class LootHolder extends WeightedRandom.Choice {
        public final int itemId;

        private LootHolder(int weight, Item item) {
            super(weight);
            this.itemId = item.id;
        }

        public LootHolder(int weight, Block cock) {
            super(weight);
            this.itemId = cock.id;
        }
    }

    private class SpookedByPlayer extends Action {
        private Bird bird;
        private float range;

        private SpookedByPlayer(Bird bat, float range) {
            this.bird = bat;
            this.range = range;
        }

        @Override
        public boolean canExecute() {
            return !this.bird.flying && !this.bird.tamed;
        }

        @Override
        public void tick() {
            Player closestPlayer = this.bird.world.getClosestPlayer(this.bird, this.range);
            if (closestPlayer != null && this.bird.navigator.noPath()) {
                this.bird.flying = true;
                this.bird.moveControl = this.bird.flyMovement;
            }
        }
    }
}
