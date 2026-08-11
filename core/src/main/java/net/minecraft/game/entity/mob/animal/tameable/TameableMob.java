package net.minecraft.game.entity.mob.animal.tameable;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.tameable.SitDown;
import net.minecraft.game.entity.mob.animal.Animal;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.item.food.FoodItem;
import net.minecraft.game.world.World;
import net.minecraft.statistics.achievement.AchievementList;

public abstract class TameableMob extends Animal {
    public SitDown sitAI = new SitDown(this);
    protected int tamedItem;

    public TameableMob(World world) {
        super(world);
    }

    @Override
    protected void init() {
        super.init();
        this.dataSync.addObject(16, (byte)0);
        this.dataSync.addObject(17, "");
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        if (this.getOwnerName() == null) {
            tag.setString("Owner", "");
        } else {
            tag.setString("Owner", this.getOwnerName());
        }

        tag.setBoolean("Sitting", this.isSitting());
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        String ownerName = tag.getString("Owner");
        if (ownerName.length() > 0) {
            this.setOwner(ownerName);
            this.setTamed(true);
        }

        this.sitAI.setSitting(tag.getBoolean("Sitting"));
    }

    protected void onTamed(boolean success) {
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
    public boolean canDespawn() {
        return this.isTamed() ? false : super.canDespawn();
    }

    @Override
    public void updateState(byte id) {
        if (id == 7) {
            this.onTamed(true);
        } else if (id == 6) {
            this.onTamed(false);
        } else {
            super.updateState(id);
        }
    }

    protected boolean tameMob(Player player, ItemStack heldItem) {
        if (heldItem != null && heldItem.itemID == this.tamedItem) {
            if (player.properties.gamemode != 1) {
                heldItem.stackSize--;
            }

            if (heldItem.stackSize <= 0) {
                if (player.getUsing()) {
                    player.inventory.setSlotContents(44, null);
                } else {
                    player.inventory.setSlotContents(player.inventory.currentSlot, null);
                }
            }

            if (!this.world.isOnline) {
                if (this.rand.nextInt(3) == 0) {
                    this.setTamed(true);
                    this.sitAI.setSitting(true);
                    this.setHealth(20);
                    this.setOwner(player.username);
                    this.onTamed(true);
                    player.triggerAchievement(AchievementList.tame);
                    this.world.setEntityState(this, (byte)7);
                } else {
                    this.onTamed(false);
                    this.world.setEntityState(this, (byte)6);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    protected boolean healMob(Player player, ItemStack heldItem) {
        if (heldItem != null && ItemList.items[heldItem.itemID] instanceof FoodItem) {
            FoodItem itemFood3 = (FoodItem)ItemList.items[heldItem.itemID];
            if (this.validFood(itemFood3) && this.health < this.maxHealth) {
                if (player.properties.gamemode != 1) {
                    heldItem.stackSize--;
                }

                this.heal(itemFood3.healAmount);
                if (heldItem.stackSize <= 0) {
                    if (player.getUsing()) {
                        player.inventory.setSlotContents(44, null);
                    } else {
                        player.inventory.setSlotContents(player.inventory.currentSlot, null);
                    }
                }

                return true;
            }
        }

        if (player.username.equalsIgnoreCase(this.getOwnerName()) && !this.world.isOnline) {
            this.sitAI.setSitting(!this.isSitting());
            this.jumping = false;
        }

        return false;
    }

    protected boolean validFood(Item fond) {
        return fond == ItemList.rottenFlesh;
    }

    public boolean isTamed() {
        return (this.dataSync.getObjectByte(16) & 4) != 0;
    }

    public void setTamed(boolean tamed) {
        byte state = this.dataSync.getObjectByte(16);
        if (tamed) {
            this.dataSync.updateObject(16, (byte)(state | 4));
        } else {
            this.dataSync.updateObject(16, (byte)(state & -5));
        }
    }

    public boolean isSitting() {
        return (this.dataSync.getObjectByte(16) & 1) != 0;
    }

    public void setSitting(boolean fuckYou) {
        byte pieceOfShit = this.dataSync.getObjectByte(16);
        if (fuckYou) {
            this.dataSync.updateObject(16, (byte)(pieceOfShit | 1));
        } else {
            this.dataSync.updateObject(16, (byte)(pieceOfShit & -2));
        }
    }

    public String getOwnerName() {
        return this.dataSync.getObjectString(17);
    }

    public void setOwner(String name) {
        this.dataSync.updateObject(17, name);
    }

    public Mob getOwner() {
        return this.world.getExactPlayer(this.getOwnerName());
    }

    public SitDown getSittingState() {
        return this.sitAI;
    }

    @Override
    public int getFaceSpeed() {
        return this.isSitting() ? 20 : super.getFaceSpeed();
    }
}
