package net.minecraft.game.entity.mob;

import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.arrow.Arrow;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.entity.projectile.Projectile;
import net.minecraft.localization.Translator;

public class DamageSource {
    public static final DamageSource inFire = new DamageSource("inFire").fireDamage();
    public static final DamageSource onFire = new DamageSource("onFire").fireDamage();
    public static final DamageSource lava = new DamageSource("lava").fireDamage();
    public static final DamageSource inWall = new DamageSource("inWall").bypassArmor();
    public static final DamageSource drown = new DamageSource("drown").bypassArmor();
    public static final DamageSource starve = new DamageSource("starve").bypassArmor();
    public static final DamageSource cactus = new DamageSource("cactus");
    public static final DamageSource fall = new DamageSource("fall").bypassArmor();
    public static final DamageSource outOfWorld = new DamageSource("outOfWorld").bypassArmor().creativeDamage();
    public static final DamageSource generic = new DamageSource("generic");
    public static final DamageSource explosion = new DamageSource("explosion");
    public static final DamageSource magic = new DamageSource("magic").bypassArmor();
    public static final DamageSource wither = new DamageSource("wither").bypassArmor();
    public String damageType;
    private boolean isBlockable = false;
    private boolean creativeDamage = false;
    private boolean fireDamage;
    private float hungerDamage = 0.3F;

    public static DamageSource mobDamage(Mob living) {
        return new DamageSource.EntitySource("mob", living);
    }

    public static DamageSource playerDamage(Player player) {
        return new DamageSource.EntitySource("player", player);
    }

    public static DamageSource arrowDamage(Arrow arrow, Entity entity) {
        return new DamageSource.IndirectSource("arrow", arrow, entity);
    }

    public static DamageSource fireballDamage(Projectile fireball, Entity entity) {
        return fireball.damage == 10
            ? new DamageSource.IndirectSource("fireball", fireball, entity).fireDamage()
            : new DamageSource.IndirectSource("thrown", fireball, entity).fireDamage();
    }

    public static DamageSource projectileDamage(Entity source, Entity thrower) {
        return new DamageSource.IndirectSource("thrown", source, thrower);
    }

    public static DamageSource magicDamage(Entity source, Entity target) {
        return new DamageSource.IndirectSource("indirectMagic", source, target).bypassArmor();
    }

    protected DamageSource(String typeName) {
        this.damageType = typeName;
    }

    protected DamageSource creativeDamage() {
        this.creativeDamage = true;
        return this;
    }

    protected DamageSource fireDamage() {
        this.fireDamage = true;
        return this;
    }

    protected DamageSource bypassArmor() {
        this.isBlockable = true;
        this.hungerDamage = 0.0F;
        return this;
    }

    public boolean unblockable() {
        return this.isBlockable;
    }

    public float getHungerDamage() {
        return this.hungerDamage;
    }

    public boolean hurtCreative() {
        return this.creativeDamage;
    }

    public Entity getEntity() {
        return null;
    }

    public boolean isFireDamage() {
        return this.fireDamage;
    }

    public String getDeathMsg(Player player) {
        return Translator.getInstance().translateKeyFormat("death." + this.damageType, player.username);
    }

    public static class EntitySource extends DamageSource {
        private Entity entity;

        public EntitySource(String name, Entity entity) {
            super(name);
            this.entity = entity;
        }

        @Override
        public Entity getEntity() {
            return this.entity;
        }

        @Override
        public String getDeathMsg(Player player) {
            return Translator.getInstance().translateKeyFormat("death." + this.damageType, player.username, this.entity.getEntityString());
        }
    }

    public static class IndirectSource extends DamageSource.EntitySource {
        private Entity shooter;

        public IndirectSource(String name, Entity projectile, Entity entity) {
            super(name, projectile);
            this.shooter = entity;
        }

        @Override
        public Entity getEntity() {
            return this.shooter;
        }

        @Override
        public String getDeathMsg(Player player) {
            return Translator.getInstance().translateKeyFormat("death." + this.damageType, player.username, this.shooter.getEntityString());
        }
    }
}
