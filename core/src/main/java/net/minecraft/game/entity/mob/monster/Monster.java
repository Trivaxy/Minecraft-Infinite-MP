package net.minecraft.game.entity.mob.monster;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.mob.CreatureType;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.monster.arachnid.Termite;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.tools.Armor;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;

public abstract class Monster extends Mob {
    public ItemStack[] armorInventory = new ItemStack[4];
    public Item heldItem;

    public Monster(World world) {
        super(world);
        this.setHealth(20);
    }

    @Override
    public CreatureType getCreatureType() {
        return CreatureType.MONSTER;
    }

    @Override
    public void mobTick() {
        float brightness = this.getBrightness(1.0F);
        if (brightness > 0.5F) {
            this.age += 2;
        }

        super.mobTick();
    }

    @Override
    public void onTick() {
        super.onTick();
        if (!this.world.isOnline && this.world.settings.getDifficulty() == 0 && !this.boss && this.customName.isEmpty()) {
            this.setDead();
        }

        if (this.calmTime > 0) {
            this.attackTarget = null;
            this.calmTime--;
            this.world
                .spawnParticle(
                    ParticleType.HEART,
                    this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
                    this.posY + 0.5 + this.rand.nextFloat() * this.height,
                    this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
                    this.rand.nextGaussian() * 0.02,
                    this.rand.nextGaussian() * 0.02,
                    this.rand.nextGaussian() * 0.02
                );
        }
    }

    public ItemStack getEquipmentInSlot(int slot) {
        return this.armorInventory[slot];
    }

    @Override
    public boolean damageEntity(DamageSource source, int amount) {
        if (!source.unblockable()) {
            amount -= this.getArmorValue() / 3;
            if (amount < 0) {
                amount = 0;
            }
        }

        return super.damageEntity(source, amount);
    }

    public int getArmorValue() {
        if (this.world.isOnline) {
            return 0;
        }

        int value = 0;
        ItemStack[] armorSlots = this.armorInventory;

        for (int slot = 0; slot < armorSlots.length; slot++) {
            ItemStack armorPiece = armorSlots[slot];
            if (armorPiece != null && armorPiece.getItem() instanceof Armor) {
                value += ((Armor)armorPiece.getItem()).defense;
            }
        }

        return value;
    }

    @Override
    public void onDeath(DamageSource entity) {
        super.onDeath(entity);
        if (entity.getEntity() instanceof Termite) {
            int dropCount = this.rand.nextInt(3) + 1;

            for (int i = 0; i < dropCount; i++) {
                this.dropItem(ItemList.nuggetGold.id, 1);
            }
        }
    }

    @Override
    public float getPathWeight(int xCoord, int yCoord, int zCoord) {
        return 0.5F - this.world.getBrightness(xCoord, yCoord, zCoord);
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setByte("CalmTime", (byte)this.calmTime);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        this.calmTime = tag.getByte("CalmTime");
    }

    @Override
    public boolean canSpawnHere() {
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.boundingBox.minY);
        int z = MathHelper.floor_double(this.posZ);
        return this.world.getBlockBrightness(x, y, z) <= this.rand.nextInt(8) && super.canSpawnHere();
    }
}
