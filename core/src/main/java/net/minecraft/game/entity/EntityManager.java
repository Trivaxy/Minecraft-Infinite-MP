package net.minecraft.game.entity;

import com.mojang.nbt.tag.special.CompoundTag;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import net.minecraft.game.entity.arrow.Arrow;
import net.minecraft.game.entity.arrow.Bolt;
import net.minecraft.game.entity.arrow.BoneArrow;
import net.minecraft.game.entity.arrow.CalmingArrow;
import net.minecraft.game.entity.arrow.ExplosiveArrow;
import net.minecraft.game.entity.arrow.FlameArrow;
import net.minecraft.game.entity.arrow.FrostArrow;
import net.minecraft.game.entity.arrow.GlowArrow;
import net.minecraft.game.entity.arrow.LightningArrow;
import net.minecraft.game.entity.arrow.MagicArrow;
import net.minecraft.game.entity.arrow.PiercingBolt;
import net.minecraft.game.entity.arrow.PoisonArrow;
import net.minecraft.game.entity.arrow.RopeArrow;
import net.minecraft.game.entity.arrow.SpawnArrow;
import net.minecraft.game.entity.arrow.Trident;
import net.minecraft.game.entity.boat.Boat;
import net.minecraft.game.entity.boat.LavaBoat;
import net.minecraft.game.entity.boat.Raft;
import net.minecraft.game.entity.hanging.Frame;
import net.minecraft.game.entity.hanging.Painting;
import net.minecraft.game.entity.minecart.EngineMinecart;
import net.minecraft.game.entity.minecart.ExplosiveMinecart;
import net.minecraft.game.entity.minecart.Minecart;
import net.minecraft.game.entity.minecart.StorageMinecart;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.misc.FallingBlock;
import net.minecraft.game.entity.misc.LingeringCloud;
import net.minecraft.game.entity.misc.PrimedTNT;
import net.minecraft.game.entity.misc.VoidCrystal;
import net.minecraft.game.entity.mob.animal.Alligator;
import net.minecraft.game.entity.mob.animal.Bear;
import net.minecraft.game.entity.mob.animal.Bunny;
import net.minecraft.game.entity.mob.animal.Crab;
import net.minecraft.game.entity.mob.animal.Deer;
import net.minecraft.game.entity.mob.animal.Frog;
import net.minecraft.game.entity.mob.animal.Goat;
import net.minecraft.game.entity.mob.animal.Meerkat;
import net.minecraft.game.entity.mob.animal.Monkey;
import net.minecraft.game.entity.mob.animal.Moose;
import net.minecraft.game.entity.mob.animal.Seal;
import net.minecraft.game.entity.mob.animal.Sheep;
import net.minecraft.game.entity.mob.animal.Turtle;
import net.minecraft.game.entity.mob.animal.aquatic.Dolphin;
import net.minecraft.game.entity.mob.animal.aquatic.Fish;
import net.minecraft.game.entity.mob.animal.aquatic.Jellyfish;
import net.minecraft.game.entity.mob.animal.aquatic.Shark;
import net.minecraft.game.entity.mob.animal.cow.Cow;
import net.minecraft.game.entity.mob.animal.cow.Moobloom;
import net.minecraft.game.entity.mob.animal.cow.Mooshroom;
import net.minecraft.game.entity.mob.animal.flying.Bat;
import net.minecraft.game.entity.mob.animal.flying.Bird;
import net.minecraft.game.entity.mob.animal.flying.Chicken;
import net.minecraft.game.entity.mob.animal.flying.Fae;
import net.minecraft.game.entity.mob.animal.flying.Ostrich;
import net.minecraft.game.entity.mob.animal.flying.Parrot;
import net.minecraft.game.entity.mob.animal.flying.Penguin;
import net.minecraft.game.entity.mob.animal.flying.Vulture;
import net.minecraft.game.entity.mob.animal.mounts.Camel;
import net.minecraft.game.entity.mob.animal.mounts.Lama;
import net.minecraft.game.entity.mob.animal.mounts.Pig;
import net.minecraft.game.entity.mob.animal.mounts.horse.Horse;
import net.minecraft.game.entity.mob.animal.mounts.horse.Pegasus;
import net.minecraft.game.entity.mob.animal.mounts.horse.SkeletonHorse;
import net.minecraft.game.entity.mob.animal.mounts.horse.ZombieHorse;
import net.minecraft.game.entity.mob.animal.tameable.Fox;
import net.minecraft.game.entity.mob.animal.tameable.Ocelot;
import net.minecraft.game.entity.mob.animal.tameable.Wolf;
import net.minecraft.game.entity.mob.follower.Decoy;
import net.minecraft.game.entity.mob.follower.Snowman;
import net.minecraft.game.entity.mob.monster.Ghast;
import net.minecraft.game.entity.mob.monster.Golem;
import net.minecraft.game.entity.mob.monster.Harpy;
import net.minecraft.game.entity.mob.monster.Imp;
import net.minecraft.game.entity.mob.monster.Ogre;
import net.minecraft.game.entity.mob.monster.Phantom;
import net.minecraft.game.entity.mob.monster.Pigman;
import net.minecraft.game.entity.mob.monster.Stalker;
import net.minecraft.game.entity.mob.monster.Treant;
import net.minecraft.game.entity.mob.monster.Wisp;
import net.minecraft.game.entity.mob.monster.arachnid.Antlion;
import net.minecraft.game.entity.mob.monster.arachnid.Silverfish;
import net.minecraft.game.entity.mob.monster.arachnid.Spider;
import net.minecraft.game.entity.mob.monster.arachnid.Termite;
import net.minecraft.game.entity.mob.monster.boss.Guardian;
import net.minecraft.game.entity.mob.monster.boss.HarpyQueen;
import net.minecraft.game.entity.mob.monster.boss.Inferno;
import net.minecraft.game.entity.mob.monster.boss.LichKing;
import net.minecraft.game.entity.mob.monster.boss.Pharaoh;
import net.minecraft.game.entity.mob.monster.boss.Priest;
import net.minecraft.game.entity.mob.monster.boss.Sentry;
import net.minecraft.game.entity.mob.monster.boss.Seraphim;
import net.minecraft.game.entity.mob.monster.boss.Witch;
import net.minecraft.game.entity.mob.monster.creeper.Creeper;
import net.minecraft.game.entity.mob.monster.creeper.CreeperFire;
import net.minecraft.game.entity.mob.monster.creeper.CreeperFrost;
import net.minecraft.game.entity.mob.monster.creeper.CreeperMagic;
import net.minecraft.game.entity.mob.monster.creeper.CreeperWind;
import net.minecraft.game.entity.mob.monster.elemental.Blaze;
import net.minecraft.game.entity.mob.monster.elemental.Breeze;
import net.minecraft.game.entity.mob.monster.elemental.Flood;
import net.minecraft.game.entity.mob.monster.skeleton.Bogged;
import net.minecraft.game.entity.mob.monster.skeleton.Lich;
import net.minecraft.game.entity.mob.monster.skeleton.Skeleton;
import net.minecraft.game.entity.mob.monster.skeleton.Stray;
import net.minecraft.game.entity.mob.monster.slime.AquaCube;
import net.minecraft.game.entity.mob.monster.slime.MagmaCube;
import net.minecraft.game.entity.mob.monster.slime.Slime;
import net.minecraft.game.entity.mob.monster.zombie.Mummy;
import net.minecraft.game.entity.mob.monster.zombie.PigmanZombie;
import net.minecraft.game.entity.mob.monster.zombie.Zombie;
import net.minecraft.game.entity.projectile.Bullet;
import net.minecraft.game.entity.projectile.Egg;
import net.minecraft.game.entity.projectile.EnderEye;
import net.minecraft.game.entity.projectile.Fireball;
import net.minecraft.game.entity.projectile.Glowstick;
import net.minecraft.game.entity.projectile.Orb;
import net.minecraft.game.entity.projectile.Ring;
import net.minecraft.game.entity.projectile.Rock;
import net.minecraft.game.entity.projectile.Sandball;
import net.minecraft.game.entity.projectile.ScytheHead;
import net.minecraft.game.entity.projectile.Snowball;
import net.minecraft.game.entity.projectile.Spit;
import net.minecraft.game.entity.projectile.ThrownPotion;
import net.minecraft.game.entity.projectile.Windball;
import net.minecraft.game.world.World;
import net.minecraft.util.logging.LoggingUtils;

public class EntityManager {
    public static final List<String> blockedIds = Arrays.asList("Item", "Painting", "ItemFrame", "FallingSand");
    private static final Logger logger = Logger.getLogger("Minecraft");
    private static final Object2ObjectMap<String, Class<? extends Entity>> stringToClass = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectMap<Class<? extends Entity>, String> classToString = new Object2ObjectOpenHashMap<>();
    private static final Int2ObjectMap<Class<? extends Entity>> IdToClass = new Int2ObjectOpenHashMap<>();
    private static final Object2IntMap<Class<? extends Entity>> classToId = new Object2IntOpenHashMap<>();
    private static final Object2ObjectMap<String, Constructor<? extends Entity>> stringToConstructor = new Object2ObjectOpenHashMap<>();

    private static void addMapping(Class<? extends Entity> entityclass, String name, int id) {
        stringToClass.put(name, entityclass);
        classToString.put(entityclass, name);
        IdToClass.put(id, entityclass);
        classToId.put(entityclass, id);

        try {
            stringToConstructor.put(name, entityclass.getConstructor(World.class));
        } catch (NoSuchMethodException ex) {
            String clsname = entityclass.getSimpleName();
            throw new Error("Entity class " + clsname + " is missing the constructor " + clsname + "(" + World.class.getSimpleName() + ")");
        }
    }

    public static Entity createEntity(String string, World world) {
        Entity entity = null;

        try {
            entity = stringToConstructor.get(string).newInstance(world);
        } catch (Exception e) {
            LoggingUtils.warnThrowable(logger, "Failed to instantiate entity", e);
        }

        return entity;
    }

    public static Entity createEntityFromNBT(CompoundTag nbt, World world) {
        Entity entity = null;

        try {
            entity = stringToConstructor.get(nbt.getString("id")).newInstance(world);
        } catch (Exception e) {
            LoggingUtils.warnThrowable(logger, "Failed to instantiate entity", e);
        }

        if (entity != null) {
            entity.load(nbt);
        } else {
            LoggingUtils.warnf(logger, "Skipping Entity with id %s", nbt.getString("id"));
        }

        return entity;
    }

    public static Entity createEntityFromId(int id, World world) {
        Entity entity = null;

        try {
            Class<? extends Entity> clas = IdToClass.get(id);
            if (clas != null) {
                entity = clas.getConstructor(World.class).newInstance(world);
            }
        } catch (Exception e) {
            LoggingUtils.warnThrowable(logger, "Failed to instantiate entity", e);
        }

        if (entity == null) {
            LoggingUtils.warnf(logger, "Skipping Entity with id %d", id);
        }

        return entity;
    }

    public static int getEntityID(Entity entity) {
        return classToId.getInt(entity.getClass());
    }

    public static String getEntityString(Entity entity) {
        return classToString.get(entity.getClass());
    }

    public static List<String> validEntityIDs() {
        return new ArrayList<>(stringToClass.keySet());
    }

    static {
        addMapping(DroppedItem.class, "Item", 1);
        addMapping(FallingBlock.class, "FallingSand", 2);
        addMapping(PrimedTNT.class, "PrimedTnt", 3);
        addMapping(VoidCrystal.class, "EndCrystal", 4);
        addMapping(LingeringCloud.class, "LingeringCloud", 5);
        addMapping(Decoy.class, "Decoy", 9);
        addMapping(Painting.class, "Painting", 10);
        addMapping(Frame.class, "ItemFrame", 11);
        addMapping(Minecart.class, "Minecart", 15);
        addMapping(StorageMinecart.class, "MinecartStorage", 16);
        addMapping(EngineMinecart.class, "MinecartEngine", 17);
        addMapping(ExplosiveMinecart.class, "MinecartExplosive", 18);
        addMapping(Boat.class, "Boat", 20);
        addMapping(Raft.class, "Raft", 21);
        addMapping(LavaBoat.class, "BoatLava", 22);
        addMapping(Arrow.class, "Arrow", 25);
        addMapping(FlameArrow.class, "ArrowFlame", 26);
        addMapping(FrostArrow.class, "ArrowFrost", 27);
        addMapping(PoisonArrow.class, "ArrowPoison", 28);
        addMapping(MagicArrow.class, "ArrowEffect", 29);
        addMapping(LightningArrow.class, "ArrowLightning", 30);
        addMapping(CalmingArrow.class, "ArrowCalming", 31);
        addMapping(GlowArrow.class, "ArrowGlowing", 32);
        addMapping(RopeArrow.class, "ArrowRope", 33);
        addMapping(SpawnArrow.class, "ArrowSpawn", 34);
        addMapping(ExplosiveArrow.class, "ArrowExplosive", 35);
        addMapping(PiercingBolt.class, "ArrowPiercing", 36);
        addMapping(Bolt.class, "ArrowBolt", 37);
        addMapping(BoneArrow.class, "ArrowBone", 38);
        addMapping(Trident.class, "Spear", 50);
        addMapping(ThrownPotion.class, "Potion", 51);
        addMapping(Snowball.class, "Snowball", 52);
        addMapping(Sandball.class, "Sandball", 53);
        addMapping(Fireball.class, "Fireball", 54);
        addMapping(Windball.class, "Windball", 55);
        addMapping(Rock.class, "Rock", 56);
        addMapping(Bullet.class, "Bullet", 57);
        addMapping(Egg.class, "Egg", 58);
        addMapping(Orb.class, "Orb", 59);
        addMapping(EnderEye.class, "Pearl", 60);
        addMapping(Ring.class, "Ring", 61);
        addMapping(ScytheHead.class, "Scythe", 62);
        addMapping(Glowstick.class, "GlowStick", 63);
        addMapping(Spit.class, "Spit", 64);
        addMapping(Pig.class, "Pig", 100);
        addMapping(Sheep.class, "Sheep", 101);
        addMapping(Cow.class, "Cow", 102);
        addMapping(Chicken.class, "Chicken", 103);
        addMapping(Wolf.class, "Wolf", 104);
        addMapping(Horse.class, "Horse", 105);
        addMapping(ZombieHorse.class, "ZombieHorse", 106);
        addMapping(SkeletonHorse.class, "SkeletonHorse", 107);
        addMapping(Pegasus.class, "Pegasus", 108);
        addMapping(Bunny.class, "Bunny", 109);
        addMapping(Ocelot.class, "Ocelot", 110);
        addMapping(Fox.class, "Fox", 111);
        addMapping(Bear.class, "Bear", 112);
        addMapping(Deer.class, "Deer", 113);
        addMapping(Goat.class, "Goat", 114);
        addMapping(Lama.class, "Llama", 115);
        addMapping(Camel.class, "Camel", 116);
        addMapping(Moose.class, "Elkir", 117);
        addMapping(Mooshroom.class, "Mooshroom", 118);
        addMapping(Moobloom.class, "Moobloom", 119);
        addMapping(Monkey.class, "Monkey", 120);
        addMapping(Meerkat.class, "Meerkat", 121);
        addMapping(Seal.class, "Seal", 122);
        addMapping(Crab.class, "Crab", 123);
        addMapping(Turtle.class, "Turtle", 124);
        addMapping(Frog.class, "Frog", 125);
        addMapping(Alligator.class, "Alligator", 126);
        addMapping(Bird.class, "Bird", 127);
        addMapping(Parrot.class, "Parrot", 128);
        addMapping(Ostrich.class, "Ostrich", 129);
        addMapping(Vulture.class, "Vulture", 120);
        addMapping(Bat.class, "Bat", 121);
        addMapping(Fae.class, "Fae", 122);
        addMapping(Penguin.class, "Penguin", 123);
        addMapping(Fish.class, "Fish", 124);
        addMapping(Dolphin.class, "Dolphin", 125);
        addMapping(Shark.class, "Shark", 126);
        addMapping(Jellyfish.class, "Jellyfish", 127);
        addMapping(Zombie.class, "Zombie", 150);
        addMapping(Mummy.class, "Mummy", 151);
        addMapping(Skeleton.class, "Skeleton", 152);
        addMapping(Stray.class, "Stray", 153);
        addMapping(Bogged.class, "Bogged", 154);
        addMapping(Spider.class, "Spider", 155);
        addMapping(Creeper.class, "Creeper", 156);
        addMapping(CreeperFire.class, "CreeperFire", 157);
        addMapping(CreeperFrost.class, "CreeperFrost", 158);
        addMapping(CreeperWind.class, "CreeperWind", 159);
        addMapping(CreeperMagic.class, "CreeperMagic", 160);
        addMapping(Slime.class, "Slime", 161);
        addMapping(MagmaCube.class, "MagmaCube", 162);
        addMapping(PigmanZombie.class, "ZombiePigman", 163);
        addMapping(Ghast.class, "Ghast", 164);
        addMapping(Silverfish.class, "Silverfish", 165);
        addMapping(Termite.class, "Termite", 166);
        addMapping(Blaze.class, "Blaze", 167);
        addMapping(Breeze.class, "Breeze", 168);
        addMapping(Flood.class, "Flood", 169);
        addMapping(Stalker.class, "Stalker", 170);
        addMapping(Antlion.class, "Antlion", 171);
        addMapping(Imp.class, "Imp", 172);
        addMapping(Harpy.class, "Harpy", 173);
        addMapping(Lich.class, "Lich", 174);
        addMapping(Treant.class, "Treant", 175);
        addMapping(Golem.class, "Lurker", 176);
        addMapping(Phantom.class, "Spectre", 177);
        addMapping(Wisp.class, "Wisp", 178);
        addMapping(Pigman.class, "PigMan", 179);
        addMapping(Snowman.class, "Snowman", 180);
        addMapping(Ogre.class, "Ogre", 181);
        addMapping(AquaCube.class, "Stratogel", 182);
        addMapping(Guardian.class, "Guardian", 200);
        addMapping(Pharaoh.class, "Pharaoh", 201);
        addMapping(LichKing.class, "LichKing", 202);
        addMapping(Witch.class, "Witch", 203);
        addMapping(Inferno.class, "Inferno", 204);
        addMapping(HarpyQueen.class, "HarpyQueen", 205);
        addMapping(Priest.class, "Priest", 206);
        addMapping(Sentry.class, "Sentry", 207);
        addMapping(Seraphim.class, "Seraphim", 208);
    }
}
