package net.minecraft.game.entity.player;

import com.mojang.nbt.tag.special.CompoundTag;

public class PlayerProperties {
    private boolean hunger = false;
    private boolean sprint = true;
    private boolean atkCooldown = false;
    private boolean permadeath = false;
    private boolean armor = true;
    private boolean sleep = true;
    private boolean grave = true;
    private boolean keepinventory = false;
    private boolean durability = true;
    public int ATK = 0;
    public int DEF = 0;
    public int SPD = 0;
    public int LVL = 100;
    public int gamemode = 0;

    public void readNBT(CompoundTag tag) {
        this.hunger = tag.getBoolean("hungerEnabled");
        this.sprint = tag.getBoolean("sprintEnabled");
        this.atkCooldown = tag.getBoolean("attackCooldown");
        this.permadeath = tag.getBoolean("permadeathEnabled");
        this.armor = tag.getBoolean("oldArmor");
        this.sleep = tag.getBoolean("sleepEnabled");
        this.grave = tag.getBoolean("gravesEnabled");
        this.keepinventory = tag.getBoolean("keepInventory");
        this.durability = tag.getBoolean("durabilityEnabled");
        this.ATK = tag.getInteger("Attack");
        this.DEF = tag.getInteger("Defense");
        this.SPD = tag.getInteger("Speed");
        this.LVL = tag.getInteger("Level");
        this.gamemode = tag.getByte("Gamemode");
    }

    public void writeNBT(CompoundTag tag) {
        tag.setBoolean("hungerEnabled", this.hunger);
        tag.setBoolean("sleepEnabled", this.sleep);
        tag.setBoolean("sprintEnabled", this.sprint);
        tag.setBoolean("permadeathEnabled", this.permadeath);
        tag.setBoolean("oldArmor", this.armor);
        tag.setBoolean("gravesEnabled", this.grave);
        tag.setBoolean("keepInventory", this.keepinventory);
        tag.setBoolean("durabilityEnabled", this.durability);
        tag.setBoolean("attackCooldown", this.atkCooldown);
        tag.setInteger("Attack", this.ATK);
        tag.setInteger("Defense", this.DEF);
        tag.setInteger("Speed", this.SPD);
        tag.setInteger("Level", this.LVL);
        tag.setByte("Gamemode", (byte)this.gamemode);
    }

    public boolean getHunger() {
        return this.hunger;
    }

    public void setHunger(boolean bool) {
        this.hunger = bool;
    }

    public boolean getSprint() {
        return this.sprint;
    }

    public void setSprint(boolean bool) {
        this.sprint = bool;
    }

    public boolean getAttack() {
        return this.atkCooldown;
    }

    public void setAttack(boolean bool) {
        this.atkCooldown = bool;
    }

    public boolean getPermadeath() {
        return this.permadeath;
    }

    public void setPermadeath(boolean bool) {
        this.permadeath = bool;
    }

    public boolean getArmor() {
        return this.armor;
    }

    public void setArmor(boolean bool) {
        this.armor = bool;
    }

    public boolean getCanSleep() {
        return this.sleep;
    }

    public void setCanSleep(boolean bool) {
        this.sleep = bool;
    }

    public boolean getGrave() {
        return this.grave;
    }

    public void setGrave(boolean bool) {
        this.grave = bool;
    }

    public boolean getBabyMode() {
        return this.keepinventory;
    }

    public void setBabyMode(boolean bool) {
        this.keepinventory = bool;
    }

    public boolean getDurability() {
        return this.durability;
    }

    public void setDurability(boolean bool) {
        this.durability = bool;
    }

    public void setGamemode(int gamemode) {
        this.gamemode = gamemode;
    }
}
