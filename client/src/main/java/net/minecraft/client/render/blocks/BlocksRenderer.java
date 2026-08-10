package net.minecraft.client.render.blocks;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.options.OptionValues;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.client.render.blocks.model.EmptyModel;
import net.minecraft.client.render.blocks.model.furniture.AltarModel;
import net.minecraft.client.render.blocks.model.furniture.AnvilModel;
import net.minecraft.client.render.blocks.model.furniture.BarrelModel;
import net.minecraft.client.render.blocks.model.furniture.BeaconModel;
import net.minecraft.client.render.blocks.model.furniture.BedModel;
import net.minecraft.client.render.blocks.model.furniture.CakeModel;
import net.minecraft.client.render.blocks.model.furniture.CandleModel;
import net.minecraft.client.render.blocks.model.furniture.CauldronModel;
import net.minecraft.client.render.blocks.model.furniture.ChairModel;
import net.minecraft.client.render.blocks.model.furniture.ChestModel;
import net.minecraft.client.render.blocks.model.furniture.ComposterModel;
import net.minecraft.client.render.blocks.model.furniture.FenceModel;
import net.minecraft.client.render.blocks.model.furniture.FlagModel;
import net.minecraft.client.render.blocks.model.furniture.FlowerPotModel;
import net.minecraft.client.render.blocks.model.furniture.GateModel;
import net.minecraft.client.render.blocks.model.furniture.GravestoneModel;
import net.minecraft.client.render.blocks.model.furniture.GrindstoneModel;
import net.minecraft.client.render.blocks.model.furniture.LadderModel;
import net.minecraft.client.render.blocks.model.furniture.LanternModel;
import net.minecraft.client.render.blocks.model.furniture.LecternModel;
import net.minecraft.client.render.blocks.model.furniture.PaneModel;
import net.minecraft.client.render.blocks.model.furniture.PanelModel;
import net.minecraft.client.render.blocks.model.furniture.PotModel;
import net.minecraft.client.render.blocks.model.furniture.SawmillModel;
import net.minecraft.client.render.blocks.model.furniture.StairModel;
import net.minecraft.client.render.blocks.model.furniture.TableModel;
import net.minecraft.client.render.blocks.model.furniture.TikiTorchModel;
import net.minecraft.client.render.blocks.model.furniture.TorchModel;
import net.minecraft.client.render.blocks.model.furniture.WallModel;
import net.minecraft.client.render.blocks.model.machines.DetectorModel;
import net.minecraft.client.render.blocks.model.machines.GearModel;
import net.minecraft.client.render.blocks.model.machines.PistonHeadModel;
import net.minecraft.client.render.blocks.model.machines.PistonModel;
import net.minecraft.client.render.blocks.model.machines.RailsModel;
import net.minecraft.client.render.blocks.model.machines.RodModel;
import net.minecraft.client.render.blocks.model.misc.CrystalModel;
import net.minecraft.client.render.blocks.model.misc.FireModel;
import net.minecraft.client.render.blocks.model.misc.LiquidModel;
import net.minecraft.client.render.blocks.model.misc.PebbleModel;
import net.minecraft.client.render.blocks.model.misc.SlimeBlockModel;
import net.minecraft.client.render.blocks.model.plants.BranchModel;
import net.minecraft.client.render.blocks.model.plants.CactusModel;
import net.minecraft.client.render.blocks.model.plants.CropsModel;
import net.minecraft.client.render.blocks.model.plants.LilyPadModel;
import net.minecraft.client.render.blocks.model.plants.LogModel;
import net.minecraft.client.render.blocks.model.plants.PlantModel;
import net.minecraft.client.render.blocks.model.plants.StemModel;
import net.minecraft.client.render.blocks.model.plants.VineModel;
import net.minecraft.client.render.blocks.model.plants.WaterLilyModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.hanging.Painting;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.physics.TargetingSystem;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class BlocksRenderer {
    private WorldSource world;
    private static Icon[] destroyBlockIcons;
    public float blockBreakTime;
    public static final BlockModel baseModel = new BlockModel();
    public static final EmptyModel noModel = new EmptyModel();
    public static final PlantModel plantModel = new PlantModel();
    public static final CropsModel cropModel = new CropsModel();
    public static final StemModel stemModel = new StemModel();
    public static final VineModel vineModel = new VineModel();
    public static final LilyPadModel lilyPadModel = new LilyPadModel();
    public static final WaterLilyModel waterlilyModel = new WaterLilyModel();
    public static final BranchModel branchModel = new BranchModel();
    public static final LogModel logModel = new LogModel();
    public static final CactusModel cactusModel = new CactusModel();
    public static final ChestModel chestModel = new ChestModel();
    public static final BarrelModel barrelModel = new BarrelModel();
    public static final CauldronModel cauldronModel = new CauldronModel();
    public static final ComposterModel composterModel = new ComposterModel();
    public static final PotModel potModel = new PotModel();
    public static final StairModel stairModel = new StairModel();
    public static final PanelModel panelModel = new PanelModel();
    public static final CakeModel cakeModel = new CakeModel();
    public static final FenceModel fenceModel = new FenceModel();
    public static final GateModel gateModel = new GateModel();
    public static final WallModel wallModel = new WallModel();
    public static final PaneModel paneModel = new PaneModel();
    public static final TorchModel torchModel = new TorchModel();
    public static final TikiTorchModel tikiTorchModel = new TikiTorchModel();
    public static final CandleModel candleModel = new CandleModel();
    public static final LadderModel ladderModel = new LadderModel();
    public static final RailsModel railModel = new RailsModel();
    public static final AnvilModel anvilModel = new AnvilModel();
    public static final SawmillModel sawmillModel = new SawmillModel();
    public static final GrindstoneModel grindstoneModel = new GrindstoneModel();
    public static final LecternModel lecternModel = new LecternModel();
    public static final BeaconModel beaconModel = new BeaconModel();
    public static final AltarModel altarModel = new AltarModel();
    public static final BedModel bedModel = new BedModel();
    public static final ChairModel chairModel = new ChairModel();
    public static final TableModel tableModel = new TableModel();
    public static final FlowerPotModel flowerPotModel = new FlowerPotModel();
    public static final LanternModel lanternModel = new LanternModel();
    public static final FlagModel flagModel = new FlagModel();
    public static final GravestoneModel graveModel = new GravestoneModel();
    public static final GearModel gearModel = new GearModel();
    public static final DetectorModel detectorModel = new DetectorModel();
    public static final PistonModel pistonModel = new PistonModel();
    public static final PistonHeadModel pistonHeadModel = new PistonHeadModel();
    public static final RodModel rodModel = new RodModel();
    public static final FireModel fireModel = new FireModel();
    public static final LiquidModel liquidModel = new LiquidModel();
    public static final CrystalModel crystalModel = new CrystalModel();
    public static final PebbleModel pebbleModel = new PebbleModel();
    public static final SlimeBlockModel slimeModel = new SlimeBlockModel();
    public static Map<Block.RenderType, BlockModel> models = new HashMap<>();

    public BlocksRenderer(WorldSource world) {
        models.put(Block.RenderType.NONE, noModel);
        models.put(Block.RenderType.NORMAL, baseModel);
        models.put(Block.RenderType.CHEST, chestModel);
        models.put(Block.RenderType.BARREL, barrelModel);
        models.put(Block.RenderType.ANVIL, anvilModel);
        models.put(Block.RenderType.CAULDRON, cauldronModel);
        models.put(Block.RenderType.COMPOSTER, composterModel);
        models.put(Block.RenderType.ALTAR, altarModel);
        models.put(Block.RenderType.LECTERN, lecternModel);
        models.put(Block.RenderType.BEACON, beaconModel);
        models.put(Block.RenderType.SAW, sawmillModel);
        models.put(Block.RenderType.GRINDSTONE, grindstoneModel);
        models.put(Block.RenderType.STAIRS, stairModel);
        models.put(Block.RenderType.PANEL, panelModel);
        models.put(Block.RenderType.CAKE, cakeModel);
        models.put(Block.RenderType.FENCE, fenceModel);
        models.put(Block.RenderType.FENCE_GATE, gateModel);
        models.put(Block.RenderType.WALL, wallModel);
        models.put(Block.RenderType.PANE, paneModel);
        models.put(Block.RenderType.LADDER, ladderModel);
        models.put(Block.RenderType.RAIL, railModel);
        models.put(Block.RenderType.TORCH, torchModel);
        models.put(Block.RenderType.TIKI_TORCH, tikiTorchModel);
        models.put(Block.RenderType.CANDLE, candleModel);
        models.put(Block.RenderType.BED, bedModel);
        models.put(Block.RenderType.CHAIR, chairModel);
        models.put(Block.RenderType.TABLE, tableModel);
        models.put(Block.RenderType.POT, potModel);
        models.put(Block.RenderType.FLOWER_POT, flowerPotModel);
        models.put(Block.RenderType.LANTERN, lanternModel);
        models.put(Block.RenderType.FLAG, flagModel);
        models.put(Block.RenderType.GRAVE, graveModel);
        models.put(Block.RenderType.CROSS, plantModel);
        models.put(Block.RenderType.CROPS, cropModel);
        models.put(Block.RenderType.STEM, stemModel);
        models.put(Block.RenderType.VINE, vineModel);
        models.put(Block.RenderType.LILYPAD, lilyPadModel);
        models.put(Block.RenderType.WATERLILY, waterlilyModel);
        models.put(Block.RenderType.BRANCH, branchModel);
        models.put(Block.RenderType.LOG, logModel);
        models.put(Block.RenderType.CACTUS, cactusModel);
        models.put(Block.RenderType.GEAR, gearModel);
        models.put(Block.RenderType.DETECTOR, detectorModel);
        models.put(Block.RenderType.PISTON_BASE, pistonModel);
        models.put(Block.RenderType.PISTON_EXTENSION, pistonHeadModel);
        models.put(Block.RenderType.ROD, rodModel);
        models.put(Block.RenderType.FIRE, fireModel);
        models.put(Block.RenderType.FLUID, liquidModel);
        models.put(Block.RenderType.SLIME, slimeModel);
        models.put(Block.RenderType.CRYSTAL, crystalModel);
        models.put(Block.RenderType.PEBBLES, pebbleModel);
        this.world = world;

        for (BlockModel value : models.values()) {
            value.setWorldInstance(world);
        }
    }

    public BlocksRenderer() {
        models.put(Block.RenderType.NONE, noModel);
        models.put(Block.RenderType.NORMAL, baseModel);
        models.put(Block.RenderType.CHEST, chestModel);
        models.put(Block.RenderType.BARREL, barrelModel);
        models.put(Block.RenderType.ANVIL, anvilModel);
        models.put(Block.RenderType.CAULDRON, cauldronModel);
        models.put(Block.RenderType.COMPOSTER, composterModel);
        models.put(Block.RenderType.ALTAR, altarModel);
        models.put(Block.RenderType.LECTERN, lecternModel);
        models.put(Block.RenderType.BEACON, beaconModel);
        models.put(Block.RenderType.SAW, sawmillModel);
        models.put(Block.RenderType.GRINDSTONE, grindstoneModel);
        models.put(Block.RenderType.STAIRS, stairModel);
        models.put(Block.RenderType.PANEL, panelModel);
        models.put(Block.RenderType.CAKE, cakeModel);
        models.put(Block.RenderType.FENCE, fenceModel);
        models.put(Block.RenderType.FENCE_GATE, gateModel);
        models.put(Block.RenderType.WALL, wallModel);
        models.put(Block.RenderType.PANE, paneModel);
        models.put(Block.RenderType.LADDER, ladderModel);
        models.put(Block.RenderType.RAIL, railModel);
        models.put(Block.RenderType.TORCH, torchModel);
        models.put(Block.RenderType.TIKI_TORCH, tikiTorchModel);
        models.put(Block.RenderType.CANDLE, candleModel);
        models.put(Block.RenderType.BED, bedModel);
        models.put(Block.RenderType.CHAIR, chairModel);
        models.put(Block.RenderType.TABLE, tableModel);
        models.put(Block.RenderType.POT, potModel);
        models.put(Block.RenderType.FLOWER_POT, flowerPotModel);
        models.put(Block.RenderType.LANTERN, lanternModel);
        models.put(Block.RenderType.FLAG, flagModel);
        models.put(Block.RenderType.GRAVE, graveModel);
        models.put(Block.RenderType.CROSS, plantModel);
        models.put(Block.RenderType.CROPS, cropModel);
        models.put(Block.RenderType.STEM, stemModel);
        models.put(Block.RenderType.VINE, vineModel);
        models.put(Block.RenderType.LILYPAD, lilyPadModel);
        models.put(Block.RenderType.WATERLILY, waterlilyModel);
        models.put(Block.RenderType.BRANCH, branchModel);
        models.put(Block.RenderType.LOG, logModel);
        models.put(Block.RenderType.CACTUS, cactusModel);
        models.put(Block.RenderType.GEAR, gearModel);
        models.put(Block.RenderType.DETECTOR, detectorModel);
        models.put(Block.RenderType.PISTON_BASE, pistonModel);
        models.put(Block.RenderType.PISTON_EXTENSION, pistonHeadModel);
        models.put(Block.RenderType.ROD, rodModel);
        models.put(Block.RenderType.FIRE, fireModel);
        models.put(Block.RenderType.FLUID, liquidModel);
        models.put(Block.RenderType.SLIME, slimeModel);
        models.put(Block.RenderType.CRYSTAL, crystalModel);
        models.put(Block.RenderType.PEBBLES, pebbleModel);
    }

    public void setWorldInstance(WorldSource world) {
        this.world = world;
    }

    public boolean renderBlock(Block block, int x, int y, int z) {
        Block.RenderType renderType = block.getRenderType();
        block.modifyOutline(this.world, x, y, z);
        return models.get(renderType) != null ? models.get(renderType).render(block, x, y, z) : false;
    }

    public void renderBlockGUI(Block block, int itemDmg) {
        Block.RenderType renderType = block.getRenderType();
        if (models.get(renderType) != null) {
            models.get(renderType).renderInGUI(block, itemDmg);
        }
    }

    public boolean isModel3D(Block.RenderType renderType) {
        return models.get(renderType) != null ? models.get(renderType).renderItemIn3d() : true;
    }

    public void applyCracksOverlay(Block.RenderType renderType, Block block, int x, int y, int z, Icon icon) {
        if (models.get(renderType) != null) {
            models.get(renderType).applyTextureOverride(block, x, y, z, icon);
        }
    }

    public void renderMovingBlock(Block target, World world, int x, int y, int z, int mdata) {
        float southFace = 0.5F;
        float northFace = 1.0F;
        float eastFace = 0.8F;
        float westFace = 0.6F;
        Tessellator tess = Tessellator.instance;
        tess.begin();
        tess.setBrightness(target.getBrightness(world, x, y, z));
        tess.setColor(southFace, southFace, southFace);
        baseModel.renderFace(target, -0.5, -0.5, -0.5, target.getIcon(0, mdata), BlockModel.Face.BOTTOM, true);
        tess.setColor(northFace, northFace, northFace);
        baseModel.renderFace(target, -0.5, -0.5, -0.5, target.getIcon(1, mdata), BlockModel.Face.TOP, true);
        tess.setColor(eastFace, eastFace, eastFace);
        baseModel.renderFace(target, -0.5, -0.5, -0.5, target.getIcon(2, mdata), BlockModel.Face.NORTH, true);
        tess.setColor(eastFace, eastFace, eastFace);
        baseModel.renderFace(target, -0.5, -0.5, -0.5, target.getIcon(3, mdata), BlockModel.Face.SOUTH, true);
        tess.setColor(westFace, westFace, westFace);
        baseModel.renderFace(target, -0.5, -0.5, -0.5, target.getIcon(4, mdata), BlockModel.Face.WEST, true);
        tess.setColor(westFace, westFace, westFace);
        baseModel.renderFace(target, -0.5, -0.5, -0.5, target.getIcon(5, mdata), BlockModel.Face.EAST, true);
        tess.draw();
    }

    public void renderBlocksForLogo(Block target, float opacity) {
        Block.RenderType renderer = target.getRenderType();
        Tessellator tess = Tessellator.instance;
        if (renderer == Block.RenderType.NORMAL) {
            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            float southFace = 0.5F;
            float northFace = 1.0F;
            float eastFace = 0.8F;
            float westFace = 0.6F;
            tess.begin();
            tess.setColorAlpha(northFace, northFace, northFace, opacity);
            baseModel.renderFace(target, -0.5, -0.5, -0.5, target.getSideIcon(0), BlockModel.Face.BOTTOM, false);
            tess.setColorAlpha(southFace, southFace, southFace, opacity);
            baseModel.renderFace(target, -0.5, -0.5, -0.5, target.getSideIcon(1), BlockModel.Face.TOP, false);
            tess.setColorAlpha(eastFace, eastFace, eastFace, opacity);
            baseModel.renderFace(target, -0.5, -0.5, -0.5, target.getSideIcon(2), BlockModel.Face.NORTH, false);
            baseModel.renderFace(target, -0.5, -0.5, -0.5, target.getSideIcon(3), BlockModel.Face.SOUTH, false);
            tess.setColorAlpha(westFace, westFace, westFace, opacity);
            baseModel.renderFace(target, -0.5, -0.5, -0.5, target.getSideIcon(4), BlockModel.Face.WEST, false);
            baseModel.renderFace(target, -0.5, -0.5, -0.5, target.getSideIcon(5), BlockModel.Face.EAST, false);
            tess.draw();
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        }
    }

    public void renderShadow(Block target, float opacity) {
        Block.RenderType renderType = target.getRenderType();
        Tessellator tess = Tessellator.instance;
        if (renderType == Block.RenderType.NORMAL) {
            GL11.glTranslatef(-0.4F, -0.4F, -0.4F);
            float brightness = 1.0F;
            tess.begin();
            tess.setColorAlpha(brightness, brightness, brightness, opacity);
            baseModel.renderFace(target, -0.5, -0.5, -0.5, target.getSideIcon(2), BlockModel.Face.NORTH, false);
            tess.draw();
            GL11.glTranslatef(0.4F, 0.4F, 0.4F);
        }
    }

    public void renderBlockCracks(Mob player, TargetingSystem target, float ticks) {
        Tessellator tess = Tessellator.instance;
        GL11.glEnable(3042);
        GL11.glEnable(3008);
        GL11.glBlendFunc(770, 1);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, (MathHelper.sin((float)System.currentTimeMillis() / 100.0F) * 0.2F + 0.4F) * 0.5F);
        if (this.blockBreakTime > 0.0F) {
            GL11.glBlendFunc(774, 768);
            int texId = Minecraft.getMinecraft().textureLoader.getTexture("/terrain.png");
            GL11.glBindTexture(3553, texId);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
            GL11.glPushMatrix();
            int id = this.world.getBlockId(target.blockX, target.blockY, target.blockZ);
            Block block = id > 0 ? BlockList.blocks[id] : null;
            GL11.glDisable(3008);
            GL11.glPolygonOffset(-3.0F, -3.0F);
            GL11.glEnable(32823);
            tess.begin();
            double playerX = player.lastTickPosX + (player.posX - player.lastTickPosX) * ticks;
            double playerY = player.lastTickPosY + (player.posY - player.lastTickPosY) * ticks;
            double playerZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * ticks;
            tess.setTranslation(-playerX, -playerY, -playerZ);
            tess.disableColor();
            if (block == null) {
                block = BlockList.stone;
            }

            this.applyCracksOverlay(
                block.getRenderType(), block, target.blockX, target.blockY, target.blockZ, destroyBlockIcons[(int)(this.blockBreakTime * 10.0F)]
            );
            tess.draw();
            tess.setTranslation(0.0, 0.0, 0.0);
            GL11.glPolygonOffset(0.0F, 0.0F);
            GL11.glDisable(32823);
            GL11.glEnable(3008);
            GL11.glDepthMask(true);
            GL11.glPopMatrix();
        }

        GL11.glDisable(3042);
        GL11.glDisable(3008);
    }

    public void renderBlockOutline(Mob player, TargetingSystem target, float tick) {
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.5F);
        GL11.glLineWidth(2.0F);
        GL11.glDisable(3553);
        GL11.glDepthMask(false);
        double playerX = player.lastTickPosX + (player.posX - player.lastTickPosX) * tick;
        double playerY = player.lastTickPosY + (player.posY - player.lastTickPosY) * tick;
        double playerZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * tick;
        if (target.target == TargetingSystem.HitType.TILE) {
            double offset = 0.002;
            int id = this.world.getBlockId(target.blockX, target.blockY, target.blockZ);
            if (id > 0) {
                BlockList.blocks[id].modifyOutline(this.world, target.blockX, target.blockY, target.blockZ);
                BoundingBox blockBB = BlockList.blocks[id]
                    .getOutline((World)this.world, target.blockX, target.blockY, target.blockZ)
                    .expand(offset, offset, offset)
                    .getOffsetBoundingBox(-playerX, -playerY, -playerZ);
                this.drawOutlinedHitbox(blockBB);
                if (OptionValues.showOutline) {
                    GL11.glEnable(3042);
                    GL11.glEnable(3008);
                    GL11.glBlendFunc(770, 771);
                    GL11.glDepthMask(true);
                    GL11.glColorMask(true, true, true, true);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, ((float)Math.sin(System.currentTimeMillis() / 100.0) * 0.2F + 0.4F) * 0.5F);
                    GL11.glPushMatrix();
                    Tessellator tess = Tessellator.instance;
                    tess.begin();
                    tess.addVertex(blockBB.minX, blockBB.minY, blockBB.minZ);
                    tess.addVertex(blockBB.minX, blockBB.maxY, blockBB.minZ);
                    tess.addVertex(blockBB.maxX, blockBB.maxY, blockBB.minZ);
                    tess.addVertex(blockBB.maxX, blockBB.minY, blockBB.minZ);
                    tess.addVertex(blockBB.maxX, blockBB.minY, blockBB.maxZ);
                    tess.addVertex(blockBB.maxX, blockBB.maxY, blockBB.maxZ);
                    tess.addVertex(blockBB.minX, blockBB.maxY, blockBB.maxZ);
                    tess.addVertex(blockBB.minX, blockBB.minY, blockBB.maxZ);
                    tess.addVertex(blockBB.maxX, blockBB.minY, blockBB.minZ);
                    tess.addVertex(blockBB.maxX, blockBB.maxY, blockBB.minZ);
                    tess.addVertex(blockBB.maxX, blockBB.maxY, blockBB.maxZ);
                    tess.addVertex(blockBB.maxX, blockBB.minY, blockBB.maxZ);
                    tess.addVertex(blockBB.minX, blockBB.minY, blockBB.maxZ);
                    tess.addVertex(blockBB.minX, blockBB.maxY, blockBB.maxZ);
                    tess.addVertex(blockBB.minX, blockBB.maxY, blockBB.minZ);
                    tess.addVertex(blockBB.minX, blockBB.minY, blockBB.minZ);
                    tess.addVertex(blockBB.minX, blockBB.minY, blockBB.minZ);
                    tess.addVertex(blockBB.maxX, blockBB.minY, blockBB.minZ);
                    tess.addVertex(blockBB.maxX, blockBB.minY, blockBB.maxZ);
                    tess.addVertex(blockBB.minX, blockBB.minY, blockBB.maxZ);
                    tess.addVertex(blockBB.minX, blockBB.maxY, blockBB.maxZ);
                    tess.addVertex(blockBB.maxX, blockBB.maxY, blockBB.maxZ);
                    tess.addVertex(blockBB.maxX, blockBB.maxY, blockBB.minZ);
                    tess.addVertex(blockBB.minX, blockBB.maxY, blockBB.minZ);
                    tess.draw();
                    GL11.glPopMatrix();
                }
            }
        }

        if (target != null && target.entityHit instanceof Painting) {
            BoundingBox hitbox = target.entityHit.boundingBox.expand(0.015F, 0.015F, 0.015F).getOffsetBoundingBox(-playerX, -playerY, -playerZ);
            this.drawOutlinedHitbox(hitbox);
        }

        GL11.glDepthMask(true);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
    }

    private void drawOutlinedHitbox(BoundingBox boundingBox) {
        Tessellator tess = Tessellator.instance;
        tess.begin(3);
        tess.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        tess.addVertex(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        tess.addVertex(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
        tess.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
        tess.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        tess.draw();
        tess.begin(3);
        tess.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        tess.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
        tess.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        tess.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        tess.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        tess.draw();
        tess.begin(1);
        tess.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        tess.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        tess.addVertex(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        tess.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
        tess.addVertex(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
        tess.addVertex(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        tess.addVertex(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
        tess.addVertex(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        tess.draw();
    }

    public static void registerDestroyBlockIcons(IconRegister iconRegister) {
        destroyBlockIcons = new Icon[10];

        for (int icon = 0; icon < destroyBlockIcons.length; icon++) {
            destroyBlockIcons[icon] = iconRegister.registerIcon("destroy/destroy_" + icon);
        }
    }
}
