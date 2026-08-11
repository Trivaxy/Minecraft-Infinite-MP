package net.minecraft.game.block.material;

import net.minecraft.game.world.mapdata.MapColor;

public class MaterialList {
    public static final Material air = new TransparentMaterial(MapColor.air);
    public static final Material grass = new Material(MapColor.grass).setBurning();
    public static final Material ground = new Material(MapColor.dirt);
    public static final Material wood = new Material(MapColor.wood).setBurning();
    public static final Material rock = new Material(MapColor.stone);
    public static final Material metal = new Material(MapColor.iron);
    public static final Material water = new LiquidMaterial(MapColor.water);
    public static final Material lava = new LiquidMaterial(MapColor.lava);
    public static final Material leaves = new Material(MapColor.foliage).setBurning();
    public static final Material plants = new TransparentMaterial(MapColor.foliage).setNoPushMobility().setBurning().setGroundCover();
    public static final Material sponge = new Material(MapColor.cloth);
    public static final Material cloth = new Material(MapColor.cloth).setBurning();
    public static final Material fire = new TransparentMaterial(MapColor.air).setNoPushMobility();
    public static final Material sand = new Material(MapColor.sand);
    public static final Material circuits = new TransparentMaterial(MapColor.air).setNoPushMobility();
    public static final Material glass = new Material(MapColor.air);
    public static final Material tnt = new Material(MapColor.lava);
    public static final Material magma = new Material(MapColor.lava);
    public static final Material cake = new Material(MapColor.cloth);
    public static final Material web = new Material(MapColor.air).setNoPushMobility();
    public static final Material rope = new Material(MapColor.air);
    public static final Material slime = new Material(MapColor.foliage);
    public static final Material pulley = new Material(MapColor.air).setImmovableMobility();
    public static final Material vacuum = new Material(MapColor.air);
    public static final Material cactus = new Material(MapColor.foliage).setNoPushMobility();
    public static final Material quicksand = new Material(MapColor.sand);
    public static final Material snow = new Material(MapColor.snow).setNoPushMobility();
    public static final Material ice = new Material(MapColor.ice).setImmovableMobility();
    public static final Material pumpkin = new Material(MapColor.dirt).setNoPushMobility();
    public static final Material ash = new Material(MapColor.stone).setNoPushMobility();
    public static final Material bone = new Material(MapColor.stone);
    public static final Material soulsand = new Material(MapColor.sand);
    public static final Material battery = new Material(MapColor.sand).setImmovableMobility();
    public static final Material cloud = new Material(MapColor.snow);
    public static final Material quicksoil = new Material(MapColor.sand);
    public static final Material portal = new Material(MapColor.stone).setImmovableMobility();
}
