package net.minecraft.game.entity.mob.animal;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.entity.mob.ai.passive.LookAround;
import net.minecraft.game.entity.mob.ai.passive.LookAt;
import net.minecraft.game.entity.mob.ai.passive.Panic;
import net.minecraft.game.entity.mob.ai.passive.Swim;
import net.minecraft.game.entity.mob.ai.passive.WanderAround;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.statistics.StatList;
import net.minecraft.statistics.achievement.AchievementList;
import net.minecraft.util.math.MathHelper;

public class Sheep extends Animal {
    public boolean grazing = false;
    public int grazingTime;
    public static final float[][] fleeceColors = new float[][]{
        {1.0F, 1.0F, 1.0F},
        {0.6F, 0.6F, 0.6F},
        {0.3F, 0.3F, 0.3F},
        {1.0F, 0.25F, 0.625F},
        {1.0F, 0.25F, 1.0F},
        {0.75F, 0.375F, 1.0F},
        {0.643F, 0.318F, 1.0F},
        {0.5F, 0.5F, 1.0F},
        {0.0F, 0.75F, 1.0F},
        {0.25F, 1.0F, 1.0F},
        {0.25F, 1.0F, 0.625F},
        {0.25F, 1.0F, 0.25F},
        {0.625F, 1.0F, 0.25F},
        {1.0F, 1.0F, 0.25F},
        {1.0F, 0.625F, 0.25F},
        {1.0F, 0.25F, 0.25F},
        {0.1F, 0.1F, 0.1F},
        {0.3F, 0.2F, 0.1F}
    };

    public Sheep(World world) {
        super(world);
        this.setSize(0.9F, 1.3F);
        this.texture = "/mob/sheep/sheep.png";
        this.moveSpeed = 0.23F;
        int color = this.rand.nextInt(5);
        if (color == 3) {
            color = 16;
        }

        if (color == 4) {
            color = 17;
        }

        if (this.rand.nextInt(500) == 0) {
            color = 4;
        }

        this.setFleeceColor(color);
        this.behaviors.addTask(0, new Swim(this));
        this.behaviors.addTask(1, new Panic(this, this.moveSpeed + this.moveSpeed / 2.0F));
        this.behaviors.addTask(2, new Sheep.Graze(this));
        this.behaviors.addTask(5, new WanderAround(this, this.moveSpeed, true));
        this.behaviors.addTask(6, new LookAt(this, Player.class, 6.0F));
        this.behaviors.addTask(7, new LookAround(this));
    }

    @Override
    public int getSpawnID() {
        return 1;
    }

    @Override
    protected void init() {
        super.init();
        this.dataSync.addObject(16, (byte)0);
        this.dataSync.addObject(17, (byte)0);
    }

    @Override
    public boolean interact(Player player) {
        ItemStack heldItem = player.getCurrentEquippedItem();
        if (player.getUsing()) {
            heldItem = player.inventory.getOffHandItem();
        }

        if (!this.getSheared() && heldItem != null && heldItem.getItem() == ItemList.shears) {
            if (!this.world.isOnline) {
                this.setSheared(true);
                int amount = 2 + this.rand.nextInt(4);
                if (player.getLuckModifier()) {
                    amount = 3 + this.rand.nextInt(6);
                }

                int metadata = this.getFleeceColor();

                for (int i = 0; i < amount; i++) {
                    DroppedItem drop = this.dropItem(BlockList.cloth.id, 1, metadata, 1.0F);
                    drop.motionY = drop.motionY + this.rand.nextFloat() * 0.05F;
                    drop.motionX = drop.motionX + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F;
                    drop.motionZ = drop.motionZ + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F;
                }
            }

            if (player.getUsing()) {
                player.swingOffhandItem();
            } else {
                player.swingItem();
            }

            heldItem.damageItem(1, player);
            this.world.playSoundEffect(this.posX + 0.5, this.posY + 0.5, this.posZ + 0.5, "random.shear", 1.0F, this.rand.nextFloat() * 0.4F + 0.8F);
            player.addStat(StatList.sheepShear, 1);
            player.triggerAchievement(AchievementList.shear);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setBoolean("Sheared", this.getSheared());
        tag.setInteger("Color", this.getFleeceColor());
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        this.setSheared(tag.getBoolean("Sheared"));
        this.setFleeceColor(tag.getInteger("Color"));
    }

    @Override
    public String getSound() {
        return "mob.sheep";
    }

    @Override
    protected String getHurtSound() {
        return "mob.sheep";
    }

    @Override
    protected String getDeathSound() {
        return "mob.sheep";
    }

    @Override
    public void playStepSound() {
        this.world.playEntitySound(this, "mob.sheepstep", 0.25F, 1.0F);
    }

    public boolean getSheared() {
        return (this.dataSync.getObjectByte(17) & 17) != 0;
    }

    public void setSheared(boolean sheared) {
        byte state = this.dataSync.getObjectByte(17);
        if (sheared) {
            this.dataSync.updateObject(17, (byte)(state | 16));
        } else {
            this.dataSync.updateObject(17, (byte)(state & -17));
        }
    }

    public int getFleeceColor() {
        return this.dataSync.getObjectByte(16);
    }

    public void setFleeceColor(int col) {
        this.dataSync.updateObject(16, (byte)col);
    }

    private class Graze extends Action {
        private Sheep sheep;

        private Graze(Sheep sheep) {
            this.sheep = sheep;
            this.setMutexBits(7);
        }

        @Override
        public boolean canExecute() {
            float xOff = MathHelper.sin(this.sheep.headFacing * (float) Math.PI / 180.0F);
            float zOff = MathHelper.cos(this.sheep.headFacing * (float) Math.PI / 180.0F);
            xOff = -0.7F * xOff;
            zOff = 0.7F * zOff;
            int x = MathHelper.floor_double(this.sheep.posX + xOff);
            int y = MathHelper.floor_double(this.sheep.posY - 1.0);
            int z = MathHelper.floor_double(this.sheep.posZ + zOff);
            return this.sheep.getSheared() && this.sheep.world.getBlockId(x, y, z) == BlockList.grass.id && this.sheep.rand.nextInt(100) == 0;
        }

        @Override
        public boolean continueExecuting() {
            return this.sheep.grazingTime < 60;
        }

        @Override
        public void start() {
            this.sheep.grazing = true;
            this.sheep.grazingTime = 0;
            this.sheep.navigator.clearPath();
        }

        @Override
        public void stop() {
            this.sheep.grazing = false;
        }

        @Override
        public void tick() {
            float xOff = MathHelper.sin(this.sheep.headFacing * (float) Math.PI / 180.0F);
            float zOff = MathHelper.cos(this.sheep.headFacing * (float) Math.PI / 180.0F);
            xOff = -0.7F * xOff;
            zOff = 0.7F * zOff;
            int x = MathHelper.floor_double(this.sheep.posX + xOff);
            int y = MathHelper.floor_double(this.sheep.posY - 1.0);
            int z = MathHelper.floor_double(this.sheep.posZ + zOff);
            if (++this.sheep.grazingTime == 60 && this.sheep.world.getBlock(x, y, z) == BlockList.grass) {
                if (!this.sheep.world.isOnline) {
                    int metadata = this.sheep.world.getBlockMetadata(x, y, z);
                    int id = this.sheep.world.getBlockId(x, y, z);
                    this.sheep.world.playEffect(2001, x, y, z, metadata << 12 | id);
                    this.sheep.world.setBlockAndMetadataWithNotify(x, y, z, BlockList.dirt.id, metadata);
                }

                if (this.sheep.rand.nextInt(5) == 0) {
                    this.sheep.setSheared(false);
                }
            }
        }
    }
}
