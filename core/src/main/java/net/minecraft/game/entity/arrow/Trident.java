package net.minecraft.game.entity.arrow;

import com.mojang.nbt.tag.special.CompoundTag;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.misc.LightningBolt;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.statistics.achievement.AchievementList;

public class Trident extends Arrow {
    private int dmg = 0;
    public int id = 0;
    private int maxDMG;

    public Trident(World world, Mob entityLiving, int dmg, int atk, int id, int max, float f) {
        super(world, entityLiving, f);
        this.dmg = dmg;
        this.arrowDamage = atk;
        this.id = id;
        this.maxDMG = max;
        this.pickupID = ItemList.trident.id;
    }

    public Trident(World world) {
        super(world);
        this.pickupID = ItemList.trident.id;
    }

    public Trident(World level, float x, float y, float z) {
        super(level, x, y, z);
        this.pickupID = ItemList.trident.id;
    }

    @Override
    public void onTick() {
        if (this.posY <= -50.0) {
            if (((Player)this.shooter).inventory.addItemToInventory(new ItemStack(this.id, 1, this.dmg))) {
                ((Player)this.shooter).onItemPickup(this);
                this.setDead();
            } else {
                this.dropTrident();
            }
        }

        super.onTick();
    }

    @Override
    protected void onBlockHit() {
        super.onBlockHit();
        if (this.shooter != null && this.shooter.health > 0 && ((Player)this.shooter).inventory.addItemToInventory(new ItemStack(this.id, 1, this.dmg))) {
            ((Player)this.shooter).onItemPickup(this);
        } else {
            this.dropTrident();
        }

        this.setDead();
    }

    @Override
    protected void onEntityHit(Entity entityHit) {
        this.dmg++;
        if (this.world.isRaining()) {
            this.world.addWeatherEffect(new LightningBolt(this.world, entityHit.posX, entityHit.posY, entityHit.posZ));
            if (this.shooter instanceof Player) {
                ((Player)this.shooter).triggerAchievement(AchievementList.trident);
            }
        }

        if (this.dmg < this.maxDMG) {
            if (this.shooter.health > 0) {
                if (((Player)this.shooter).inventory.addItemToInventory(new ItemStack(this.id, 1, this.dmg))) {
                    ((Player)this.shooter).onItemPickup(this);
                    this.setDead();
                } else {
                    this.dropTrident();
                }
            } else {
                this.dropTrident();
            }

            this.setDead();
        }

        this.setDead();
        if (this.shooter instanceof Player && entityHit instanceof Mob && ((Mob)entityHit).health <= 0) {
            this.shooter.addScore(entityHit, ((Mob)entityHit).scoreValue);
        }
    }

    @Override
    protected void produceParticles() {
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        tag.setInteger("ID", this.id);
        tag.setInteger("Damage", this.dmg);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        this.dmg = tag.getInteger("Damage");
        this.id = tag.getInteger("ID");
    }

    private void dropTrident() {
        float offsetX = this.world.rand.nextFloat() * 0.7F + 0.15F;
        float offsetY = this.world.rand.nextFloat() * 0.7F + 0.15F;
        float offsetZ = this.world.rand.nextFloat() * 0.7F + 0.15F;
        DroppedItem entityItem13 = new DroppedItem(
            this.world, (float)this.posX + offsetX, (float)this.posY + offsetY, (float)this.posZ + offsetZ, new ItemStack(this.id, 1, this.dmg)
        );
        this.world.spawnEntity(entityItem13);
    }
}
