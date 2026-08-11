package net.minecraft.game.entity.projectile;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.game.world.World;

public enum ProjectileType {
    SNOWBALL(0, Snowball.class),
    ROCK(1, Rock.class),
    EGG(2, Egg.class),
    FIREBALL(3, Fireball.class),
    SANDBALL(4, Sandball.class),
    WINDBALL(5, Windball.class),
    THROWN_POTION(6, ThrownPotion.class),
    ENDER_EYE(7, EnderEye.class),
    GLOWSTICK(8, Glowstick.class),
    BULLET(9, Bullet.class),
    ORB(10, Orb.class),
    RING(11, Ring.class),
    SPIT(12, Spit.class),
    SCYTHE_HEAD(13, ScytheHead.class),
    HOOK(14, Hook.class);

    private static final Map<Class<? extends Projectile>, ProjectileType> BY_CLASS = new HashMap<>();

    public final short id;
    public final Class<? extends Projectile> type;

    ProjectileType(int id, Class<? extends Projectile> type) {
        this.id = (short)id;
        this.type = type;
    }

    static {
        for (ProjectileType projectileType : values()) {
            BY_CLASS.put(projectileType.type, projectileType);
        }
    }

    public static ProjectileType byId(short id) {
        for (ProjectileType projectileType : values()) {
            if (projectileType.id == id) {
                return projectileType;
            }
        }

        throw new IllegalArgumentException("No projectile type with id " + id + "!");
    }

    public static ProjectileType of(Projectile projectile) {
        ProjectileType projectileType = BY_CLASS.get(projectile.getClass());
        if (projectileType == null) {
            throw new IllegalArgumentException("Projectile type missing for " + projectile.getClass() + "!");
        }

        return projectileType;
    }

    public Projectile create(World world) {
        switch (this) {
            case SNOWBALL:
                return new Snowball(world);
            case ROCK:
                return new Rock(world);
            case EGG:
                return new Egg(world);
            case FIREBALL:
                return new Fireball(world);
            case SANDBALL:
                return new Sandball(world);
            case WINDBALL:
                return new Windball(world);
            case THROWN_POTION:
                return new ThrownPotion(world);
            case ENDER_EYE:
                return new EnderEye(world);
            case GLOWSTICK:
                return new Glowstick(world);
            case BULLET:
                return new Bullet(world);
            case ORB:
                return new Orb(world);
            case RING:
                return new Ring(world);
            case SPIT:
                return new Spit(world);
            case SCYTHE_HEAD:
                return new ScytheHead(world);
            case HOOK:
                return new Hook(world);
            default:
                throw new IllegalArgumentException("No constructor for " + this + "!");
        }
    }
}
