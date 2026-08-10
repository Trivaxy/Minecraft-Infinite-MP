package net.minecraft.client.render.entity;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.localization.Font;
import net.minecraft.client.options.OptionsList;
import net.minecraft.client.render.entity.animal.AlligatorRenderer;
import net.minecraft.client.render.entity.animal.BatRenderer;
import net.minecraft.client.render.entity.animal.BirdRenderer;
import net.minecraft.client.render.entity.animal.CowRenderer;
import net.minecraft.client.render.entity.animal.FishRenderer;
import net.minecraft.client.render.entity.animal.FoxRenderer;
import net.minecraft.client.render.entity.animal.HorseRenderer;
import net.minecraft.client.render.entity.animal.JellyfishRenderer;
import net.minecraft.client.render.entity.animal.LamaRenderer;
import net.minecraft.client.render.entity.animal.MonkeyRenderer;
import net.minecraft.client.render.entity.animal.MooseRenderer;
import net.minecraft.client.render.entity.animal.OcelotRenderer;
import net.minecraft.client.render.entity.animal.PigRenderer;
import net.minecraft.client.render.entity.animal.SheepRenderer;
import net.minecraft.client.render.entity.animal.WolfRenderer;
import net.minecraft.client.render.entity.misc.CrystalRenderer;
import net.minecraft.client.render.entity.misc.DroppedItemRenderer;
import net.minecraft.client.render.entity.misc.FallingBlockRenderer;
import net.minecraft.client.render.entity.misc.FrameRenderer;
import net.minecraft.client.render.entity.misc.LightningBoltRenderer;
import net.minecraft.client.render.entity.misc.PaintingRenderer;
import net.minecraft.client.render.entity.misc.PrimedTNTRenderer;
import net.minecraft.client.render.entity.model.arachnid.CrabModel;
import net.minecraft.client.render.entity.model.arachnid.SilverfishModel;
import net.minecraft.client.render.entity.model.avian.BirdModel;
import net.minecraft.client.render.entity.model.avian.ChickenModel;
import net.minecraft.client.render.entity.model.avian.OstrichModel;
import net.minecraft.client.render.entity.model.avian.ParrotModel;
import net.minecraft.client.render.entity.model.avian.PenguinModel;
import net.minecraft.client.render.entity.model.avian.VultureModel;
import net.minecraft.client.render.entity.model.bipedal.BipedalModel;
import net.minecraft.client.render.entity.model.bipedal.GolemModel;
import net.minecraft.client.render.entity.model.bipedal.HarpyModel;
import net.minecraft.client.render.entity.model.bipedal.ImpModel;
import net.minecraft.client.render.entity.model.bipedal.MonkeyModel;
import net.minecraft.client.render.entity.model.bipedal.OgreModel;
import net.minecraft.client.render.entity.model.bipedal.PlayerModel;
import net.minecraft.client.render.entity.model.bipedal.SkeletonModel;
import net.minecraft.client.render.entity.model.bipedal.SnowmanModel;
import net.minecraft.client.render.entity.model.bipedal.StatueModel;
import net.minecraft.client.render.entity.model.bipedal.TreantModel;
import net.minecraft.client.render.entity.model.bipedal.WitchModel;
import net.minecraft.client.render.entity.model.bipedal.ZombieModel;
import net.minecraft.client.render.entity.model.elemental.ElementalModel;
import net.minecraft.client.render.entity.model.elemental.InfernoModel;
import net.minecraft.client.render.entity.model.elemental.SlimeModel;
import net.minecraft.client.render.entity.model.piscean.AlligatorModel;
import net.minecraft.client.render.entity.model.piscean.DolphinModel;
import net.minecraft.client.render.entity.model.piscean.FishModel;
import net.minecraft.client.render.entity.model.piscean.JellyfishModel;
import net.minecraft.client.render.entity.model.piscean.SealModel;
import net.minecraft.client.render.entity.model.piscean.SharkModel;
import net.minecraft.client.render.entity.model.quadruped.BearModel;
import net.minecraft.client.render.entity.model.quadruped.BunnyModel;
import net.minecraft.client.render.entity.model.quadruped.CamelModel;
import net.minecraft.client.render.entity.model.quadruped.CarbuncleModel;
import net.minecraft.client.render.entity.model.quadruped.CowModel;
import net.minecraft.client.render.entity.model.quadruped.DeerModel;
import net.minecraft.client.render.entity.model.quadruped.FoxModel;
import net.minecraft.client.render.entity.model.quadruped.FrogModel;
import net.minecraft.client.render.entity.model.quadruped.GoatModel;
import net.minecraft.client.render.entity.model.quadruped.HorseModel;
import net.minecraft.client.render.entity.model.quadruped.LamaModel;
import net.minecraft.client.render.entity.model.quadruped.MeerkatModel;
import net.minecraft.client.render.entity.model.quadruped.MooseModel;
import net.minecraft.client.render.entity.model.quadruped.OcelotModel;
import net.minecraft.client.render.entity.model.quadruped.PegasusModel;
import net.minecraft.client.render.entity.model.quadruped.PigModel;
import net.minecraft.client.render.entity.model.quadruped.SheepModel;
import net.minecraft.client.render.entity.model.quadruped.TurtleModel;
import net.minecraft.client.render.entity.model.quadruped.WolfModel;
import net.minecraft.client.render.entity.monster.AntlionRenderer;
import net.minecraft.client.render.entity.monster.CreeperRenderer;
import net.minecraft.client.render.entity.monster.ElementalRenderer;
import net.minecraft.client.render.entity.monster.EndermanRenderer;
import net.minecraft.client.render.entity.monster.GhastRenderer;
import net.minecraft.client.render.entity.monster.GolemRenderer;
import net.minecraft.client.render.entity.monster.LichRenderer;
import net.minecraft.client.render.entity.monster.PigmanRenderer;
import net.minecraft.client.render.entity.monster.PriestRenderer;
import net.minecraft.client.render.entity.monster.SeraphimRenderer;
import net.minecraft.client.render.entity.monster.SkeletonRenderer;
import net.minecraft.client.render.entity.monster.SlimeRenderer;
import net.minecraft.client.render.entity.monster.SpiderRenderer;
import net.minecraft.client.render.entity.monster.WispRenderer;
import net.minecraft.client.render.entity.monster.ZombieRenderer;
import net.minecraft.client.render.entity.projectile.ArrowRenderer;
import net.minecraft.client.render.entity.projectile.BobberRenderer;
import net.minecraft.client.render.entity.projectile.GlowstickRenderer;
import net.minecraft.client.render.entity.projectile.HookRenderer;
import net.minecraft.client.render.entity.projectile.ProjectileRenderer;
import net.minecraft.client.render.entity.projectile.SpearRenderer;
import net.minecraft.client.render.entity.vehicle.BoatRenderer;
import net.minecraft.client.render.entity.vehicle.MinecartRenderer;
import net.minecraft.client.render.entity.vehicle.RaftRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.texture.TextureLoader;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.arrow.Arrow;
import net.minecraft.game.entity.arrow.Trident;
import net.minecraft.game.entity.boat.Boat;
import net.minecraft.game.entity.boat.LavaBoat;
import net.minecraft.game.entity.boat.Raft;
import net.minecraft.game.entity.hanging.Frame;
import net.minecraft.game.entity.hanging.Painting;
import net.minecraft.game.entity.minecart.Minecart;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.misc.FallingBlock;
import net.minecraft.game.entity.misc.LightningBolt;
import net.minecraft.game.entity.misc.PrimedTNT;
import net.minecraft.game.entity.misc.VoidCrystal;
import net.minecraft.game.entity.mob.Mob;
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
import net.minecraft.game.entity.mob.monster.boss.Seraphim;
import net.minecraft.game.entity.mob.monster.boss.Witch;
import net.minecraft.game.entity.mob.monster.creeper.Creeper;
import net.minecraft.game.entity.mob.monster.elemental.Blaze;
import net.minecraft.game.entity.mob.monster.elemental.Breeze;
import net.minecraft.game.entity.mob.monster.elemental.Flood;
import net.minecraft.game.entity.mob.monster.skeleton.Lich;
import net.minecraft.game.entity.mob.monster.skeleton.Skeleton;
import net.minecraft.game.entity.mob.monster.slime.MagmaCube;
import net.minecraft.game.entity.mob.monster.slime.Slime;
import net.minecraft.game.entity.mob.monster.zombie.PigmanZombie;
import net.minecraft.game.entity.mob.monster.zombie.Zombie;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.entity.projectile.Bobber;
import net.minecraft.game.entity.projectile.Glowstick;
import net.minecraft.game.entity.projectile.Hook;
import net.minecraft.game.entity.projectile.Projectile;
import net.minecraft.game.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class EntityRenderManager {
    public static EntityRenderManager instance = new EntityRenderManager();
    private Map<Class<? extends Entity>, EntityRenderer<? extends Entity>> renderers = new HashMap<>();
    private Font fontRenderer;
    public ItemRenderer itemRenderer;
    public TextureLoader texLoader;
    public World world;
    public Mob player;
    public OptionsList options;
    public float yaw;
    public float pitch;
    public double viewerPosX;
    public double viewerPosY;
    public double viewerPosZ;
    public static double renderPosX;
    public static double renderPosY;
    public static double renderPosZ;

    private EntityRenderManager() {
        this.renderers.put(Player.class, new PlayerRenderer());
        this.renderers.put(Mob.class, new MobRenderer(new PlayerModel(0.0F, 0.0F), 0.5F));
        this.renderers.put(Decoy.class, new MobRenderer(new PlayerModel(0.0F, 0.0F), 0.5F));
        this.renderers.put(Pig.class, new PigRenderer(new PigModel(0.0F), new PigModel(0.5F), 0.7F));
        this.renderers.put(Sheep.class, new SheepRenderer(new SheepModel(), new SheepModel.SheepFur(), 0.7F));
        this.renderers.put(Cow.class, new CowRenderer(new CowModel(), 0.7F));
        this.renderers.put(Chicken.class, new BirdRenderer(new ChickenModel(), 0.3F));
        this.renderers.put(Bunny.class, new MobRenderer(new BunnyModel(), 0.3F));
        this.renderers.put(Fae.class, new BirdRenderer(new CarbuncleModel(), 0.4F));
        this.renderers.put(Ocelot.class, new OcelotRenderer(new OcelotModel(), 0.4F));
        this.renderers.put(Fish.class, new FishRenderer(new FishModel(), 0.25F));
        this.renderers.put(Jellyfish.class, new JellyfishRenderer(new JellyfishModel(), 0.7F));
        this.renderers.put(Horse.class, new HorseRenderer(new HorseModel(0.0F, 64), 0.9F));
        this.renderers.put(Pegasus.class, new HorseRenderer(new PegasusModel(), 0.8F));
        this.renderers.put(Deer.class, new MobRenderer(new DeerModel(), 0.7F));
        this.renderers.put(Bird.class, new BirdRenderer(new BirdModel(), 0.25F));
        this.renderers.put(Dolphin.class, new MobRenderer(new DolphinModel(), 0.7F));
        this.renderers.put(Ostrich.class, new BirdRenderer(new OstrichModel(), 0.5F));
        this.renderers.put(Parrot.class, new BirdRenderer(new ParrotModel(), 0.3F));
        this.renderers.put(Camel.class, new MobRenderer(new CamelModel(), 1.0F));
        this.renderers.put(Turtle.class, new MobRenderer(new TurtleModel(), 0.3F));
        this.renderers.put(Alligator.class, new AlligatorRenderer(new AlligatorModel(), 1.0F));
        this.renderers.put(Meerkat.class, new MobRenderer(new MeerkatModel(), 0.4F));
        this.renderers.put(Seal.class, new MobRenderer(new SealModel(), 0.6F));
        this.renderers.put(Crab.class, new MobRenderer(new CrabModel(), 0.3F));
        this.renderers.put(Spider.class, new SpiderRenderer());
        this.renderers.put(Creeper.class, new CreeperRenderer());
        this.renderers.put(Skeleton.class, new SkeletonRenderer(new SkeletonModel(), 0.5F));
        this.renderers.put(Lich.class, new LichRenderer(new SkeletonModel(), 0.5F));
        this.renderers.put(Zombie.class, new ZombieRenderer(new ZombieModel(), 0.5F));
        this.renderers.put(Slime.class, new SlimeRenderer(new SlimeModel(16, 0.0F), new SlimeModel(0, 0.0F), 0.25F));
        this.renderers.put(MagmaCube.class, new SlimeRenderer(new SlimeModel(16, 0.0F), new SlimeModel(0, 0.0F), 0.25F));
        this.renderers.put(Wisp.class, new WispRenderer(new SlimeModel(0, 0.0F), 0.0F));
        this.renderers.put(Phantom.class, new MobRenderer(new BipedalModel(), 0.5F));
        this.renderers.put(Antlion.class, new AntlionRenderer());
        this.renderers.put(Shark.class, new MobRenderer(new SharkModel(), 0.7F));
        this.renderers.put(Vulture.class, new BirdRenderer(new VultureModel(), 0.3F));
        this.renderers.put(Bear.class, new MobRenderer(new BearModel(), 0.7F));
        this.renderers.put(Silverfish.class, new MobRenderer(new SilverfishModel(), 0.3F));
        this.renderers.put(Termite.class, new MobRenderer(new SilverfishModel(), 0.3F));
        this.renderers.put(Ogre.class, new MobRenderer(new OgreModel(), 0.7F));
        this.renderers.put(Flood.class, new ElementalRenderer());
        this.renderers.put(Treant.class, new MobRenderer(new TreantModel(), 1.0F));
        this.renderers.put(Ghast.class, new GhastRenderer());
        this.renderers.put(Blaze.class, new MobRenderer(new ElementalModel(), 0.6F));
        this.renderers.put(Imp.class, new MobRenderer(new ImpModel(), 0.5F));
        this.renderers.put(Golem.class, new GolemRenderer(new GolemModel(), 0.8F, 0.5F));
        this.renderers.put(Breeze.class, new MobRenderer(new ElementalModel(), 0.6F));
        this.renderers.put(Harpy.class, new MobRenderer(new HarpyModel(), 0.5F));
        this.renderers.put(Wolf.class, new WolfRenderer(new WolfModel(0.0F), 0.5F));
        this.renderers.put(Fox.class, new FoxRenderer(new FoxModel(), 0.5F));
        this.renderers.put(Goat.class, new MobRenderer(new GoatModel(), 0.7F));
        this.renderers.put(Monkey.class, new MonkeyRenderer(new MonkeyModel(), 0.3F));
        this.renderers.put(Penguin.class, new BirdRenderer(new PenguinModel(), 0.4F));
        this.renderers.put(Snowman.class, new MobRenderer(new SnowmanModel(), 0.5F));
        this.renderers.put(Pigman.class, new PigmanRenderer(new BipedalModel(), 0.5F));
        this.renderers.put(Moose.class, new MooseRenderer(new MooseModel(), 1.0F));
        this.renderers.put(PigmanZombie.class, new MobRenderer(new ZombieModel(), 0.5F));
        this.renderers.put(Stalker.class, new EndermanRenderer());
        this.renderers.put(Frog.class, new MobRenderer(new FrogModel(), 0.3F));
        this.renderers.put(ZombieHorse.class, new HorseRenderer(new HorseModel(0.0F, 64), 0.9F));
        this.renderers.put(SkeletonHorse.class, new HorseRenderer(new HorseModel(0.0F, 64), 0.9F));
        this.renderers.put(Lama.class, new LamaRenderer(new LamaModel(0.0F), 0.8F));
        this.renderers.put(Bat.class, new BatRenderer());
        this.renderers.put(Priest.class, new PriestRenderer(new BipedalModel(), 0.5F));
        this.renderers.put(Seraphim.class, new SeraphimRenderer());
        this.renderers.put(HarpyQueen.class, new MobRenderer(new HarpyModel(), 0.8F));
        this.renderers.put(Witch.class, new MobRenderer(new WitchModel(0.0F), 0.5F));
        this.renderers.put(Inferno.class, new MobRenderer(new InfernoModel(), 0.8F));
        this.renderers.put(Guardian.class, new MobRenderer(new StatueModel(), 0.8F));
        this.renderers.put(Pharaoh.class, new MobRenderer(new ZombieModel(), 0.5F));
        this.renderers.put(LichKing.class, new LichRenderer(new SkeletonModel(), 0.5F));
        this.renderers.put(Minecart.class, new MinecartRenderer());
        this.renderers.put(Boat.class, new BoatRenderer("boat"));
        this.renderers.put(LavaBoat.class, new BoatRenderer("boatObsidian"));
        this.renderers.put(Raft.class, new RaftRenderer());
        this.renderers.put(Arrow.class, new ArrowRenderer());
        this.renderers.put(Trident.class, new SpearRenderer());
        this.renderers.put(Bobber.class, new BobberRenderer());
        this.renderers.put(Hook.class, new HookRenderer());
        this.renderers.put(Glowstick.class, new GlowstickRenderer());
        this.renderers.put(Projectile.class, new ProjectileRenderer());
        this.renderers.put(DroppedItem.class, new DroppedItemRenderer());
        this.renderers.put(Painting.class, new PaintingRenderer());
        this.renderers.put(Frame.class, new FrameRenderer());
        this.renderers.put(PrimedTNT.class, new PrimedTNTRenderer());
        this.renderers.put(FallingBlock.class, new FallingBlockRenderer());
        this.renderers.put(LightningBolt.class, new LightningBoltRenderer());
        this.renderers.put(VoidCrystal.class, new CrystalRenderer());

        for (EntityRenderer<? extends Entity> renderer : this.renderers.values()) {
            renderer.setRenderManager(this);
        }
    }

    public EntityRenderer<? extends Entity> getRenderer(Class<? extends Entity> entityClass) {
        EntityRenderer<? extends Entity> renderer = this.renderers.get(entityClass);
        if (renderer == null && entityClass != Entity.class) {
            Class<? extends Entity> shit = (Class<? extends Entity>)entityClass.getSuperclass();
            renderer = this.getRenderer(shit);
            this.renderers.put(entityClass, renderer);
        }

        return renderer;
    }

    public EntityRenderer<? extends Entity> getRenderer(Entity entity) {
        return this.getRenderer((Class<? extends Entity>)entity.getClass());
    }

    public void cacheInfo(World world, TextureLoader texLoader, Font font, Mob cameraMob, OptionsList options, float ticks) {
        this.world = world;
        this.texLoader = texLoader;
        this.options = options;
        this.player = cameraMob;
        this.fontRenderer = font;
        this.yaw = cameraMob.prevYaw + (cameraMob.yaw - cameraMob.prevYaw) * ticks;
        this.pitch = cameraMob.prevPitch + (cameraMob.pitch - cameraMob.prevPitch) * ticks;
        this.viewerPosX = cameraMob.lastTickPosX + (cameraMob.posX - cameraMob.lastTickPosX) * ticks;
        this.viewerPosY = cameraMob.lastTickPosY + (cameraMob.posY - cameraMob.lastTickPosY) * ticks;
        this.viewerPosZ = cameraMob.lastTickPosZ + (cameraMob.posZ - cameraMob.lastTickPosZ) * ticks;
    }

    public void renderEntity(Entity entity, float tick) {
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * tick;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * tick;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * tick;
        float yaw = entity.prevYaw + (entity.yaw - entity.prevYaw) * tick;
        int brightness = entity.getBrightnessInt(tick);
        int lightU = brightness % 65536;
        int lightV = brightness / 65536;
        if (!entity.canGlow && entity.fire <= 0) {
            GL13.glMultiTexCoord2f(33985, lightU / 1.0F, lightV / 1.0F);
        } else {
            GL13.glMultiTexCoord2f(33985, 240.0F, 240.0F);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.renderEntityAt(entity, x - renderPosX, y - renderPosY, z - renderPosZ, yaw, tick);
    }

    public void renderEntityAt(Entity entity, double x, double y, double z, float angle, float tick) {
        EntityRenderer<? super Entity> renderer = (EntityRenderer<? super Entity>)this.getRenderer(entity);
        if (renderer != null) {
            renderer.render(entity, x, y, z, angle, tick);
            renderer.renderDebugHitboxes(entity, x, y, z);
            renderer.renderOverlays(entity, x, y, z, angle, tick);
        }
    }

    public double distToCam(double x, double y, double z) {
        double finalX = x - this.viewerPosX;
        double finalY = y - this.viewerPosY;
        double finalZ = z - this.viewerPosZ;
        return finalX * finalX + finalY * finalY + finalZ * finalZ;
    }

    public Font getFont() {
        return this.fontRenderer;
    }
}
