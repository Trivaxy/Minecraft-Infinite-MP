package net.minecraft.client.render.blocks.model;

import java.util.Random;
import net.minecraft.client.options.OptionValues;
import net.minecraft.client.render.Tessellator;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.misc.StoneSpike;
import net.minecraft.game.block.plants.Flower;
import net.minecraft.game.block.plants.GlowLychen;
import net.minecraft.game.block.plants.HangingVine;
import net.minecraft.game.block.plants.TallGrass;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.WorldSource;
import net.minecraft.game.world.generation.noise.OctaveNoise;
import org.lwjgl.opengl.GL11;

public class BlockModel {
    protected WorldSource world;
    protected static OctaveNoise noise = new OctaveNoise(new Random(), 2);
    private float[] uvs = new float[4];
    private float[][] uvCorners = new float[4][2];
    private float[][] rotatedUVs = new float[4][2];
    private float[][] colors = new float[3][4];
    private double[][] noiseOffsets = new double[4][3];
    private double[] bounds = new double[6];
    private final int[] brightness = new int[4];
    private final int[] rotations = new int[6];
    protected int rotationNorth = 0;
    protected int rotationSouth = 0;
    protected int rotationEast = 0;
    protected int rotationWest = 0;
    protected int rotationTop = 0;
    protected int rotationBottom = 0;
    private int brightnessUp;
    private int brightnessDown;
    private int brightnessSouth;
    private int brightnessNorth;
    private int brightnessWest;
    private int brightnessEast;
    private int brightnessTop;
    private int brightnessBottom;
    private int brightnessLeft;
    private int brightnessRight;
    private int brightnessNorthEast;
    private int brightnessSouthEast;
    private int brightnessNorthWest;
    private int brightnessSouthWest;
    private int brightnessEastWest;
    private int brightnessWestEast;
    private int brightnessNorthSouth;
    private int brightnessSouthNorth;
    private int brightnessTopWest;
    private int brightnessTopEast;
    private int brightnessTopNorth;
    private int brightnessTopSouth;
    private int brightnessBottomWest;
    private int brightnessBottomEast;
    private int brightnessBottomNorth = 1;
    private float lightValue;
    private float lightValueXNeg;
    private float lightValueYNeg;
    private float lightValueZNeg;
    private float lightValueXPos;
    private float lightValueYPos;
    private float lightValueZPos;
    private float lightValueScratchXYZNNN;
    private float lightValueScratchXYNN;
    private float lightValueScratchXYZNNP;
    private float lightValueScratchYZNN;
    private float lightValueScratchYZNP;
    private float lightValueScratchXYZPNN;
    private float lightValueScratchXYPN;
    private float lightValueScratchXYZPNP;
    private float lightValueScratchXYZNPN;
    private float lightValueScratchXYNP;
    private float lightValueScratchXYZNPP;
    private float lightValueScratchYZPN;
    private float lightValueScratchXYZPPN;
    private float lightValueScratchXYPP;
    private float lightValueScratchYZPP;
    private float lightValueScratchXYZPPP;
    private float lightValueScratchXZNN;
    private float lightValueScratchXZPN;
    private float lightValueScratchXZNP;
    private float lightValueScratchXZPP;
    private float colorRedTopLeft;
    private float colorRedBottomLeft;
    private float colorRedBottomRight;
    private float colorRedTopRight;
    private float colorGreenTopLeft;
    private float colorGreenBottomLeft;
    private float colorGreenBottomRight;
    private float colorGreenTopRight;
    private float colorBlueTopLeft;
    private float colorBlueBottomLeft;
    private float colorBlueBottomRight;
    private float colorBlueTopRight;
    private boolean ambientOcclusion;
    protected boolean flipTexture = false;
    protected Icon textureOverride = null;
    public static Icon destroyBlockIcon;

    public void applyTextureOverride(Block block, int x, int y, int z, Icon icn) {
        this.setOverrideTexture(icn);
        this.render(block, x, y, z);
        this.clearOverrideTexture();
    }

    public static final void registerIcons(IconRegister iconRegister) {
        destroyBlockIcon = iconRegister.registerIcon("destroy/destroy");
    }

    public void setWorldInstance(WorldSource world) {
        this.world = world;
    }

    public void setOverrideTexture(Icon icn) {
        this.textureOverride = icn;
    }

    public void clearOverrideTexture() {
        this.textureOverride = null;
    }

    public boolean hasOverrideTexture() {
        return this.textureOverride != null;
    }

    public boolean render(Block block, int x, int y, int z) {
        return this.renderStandardBlock(block, x, y, z);
    }

    public boolean renderStandardBlock(Block block, int x, int y, int z) {
        int color = block.getRenderColor(this.world, x, y, z);
        float red = (color >> 16 & 0xFF) / 255.0F;
        float blue = (color >> 8 & 0xFF) / 255.0F;
        float green = (color & 0xFF) / 255.0F;
        boolean rotate = OptionValues.textureRotation && block.getRotation(x, y, z, 0) != -1;
        if (rotate) {
            this.rotationBottom = block.getRotation(x, y, z, 0);
            this.rotationTop = block.getRotation(x, y, z, 1);
            this.rotationNorth = block.getRotation(x, y, z, 2);
            this.rotationSouth = block.getRotation(x, y, z, 3);
            this.rotationWest = block.getRotation(x, y, z, 4);
            this.rotationEast = block.getRotation(x, y, z, 5);
        }

        boolean rendered;
        if (OptionValues.smoothLighting) {
            rendered = this.renderWithAO(block, x, y, z, red, blue, green);
        } else {
            rendered = this.renderWithColor(block, x, y, z, red, blue, green);
        }

        if (rotate) {
            this.rotationBottom = 0;
            this.rotationTop = 0;
            this.rotationNorth = 0;
            this.rotationSouth = 0;
            this.rotationWest = 0;
            this.rotationEast = 0;
        }

        return rendered;
    }

    public boolean renderWithColor(Block block, int x, int y, int z, float red, float green, float blue) {
        this.ambientOcclusion = false;
        Tessellator tess = Tessellator.instance;
        boolean renderFace = false;
        float south = 0.5F;
        float north = 1.0F;
        float west = 0.8F;
        float east = 0.6F;
        float southRed = south * red;
        float northRed = north * red;
        float westRed = west * red;
        float eastRed = east * red;
        float southGreen = south * green;
        float northGreen = north * green;
        float westGreen = west * green;
        float eastGreen = east * green;
        float southBlue = south * blue;
        float northBlue = north * blue;
        float westBlue = west * blue;
        float eastBlue = east * blue;
        float brightness = block.getBrightness(this.world, x, y, z);
        if (block.renderFace(this.world, x, y - 1, z, 0)) {
            tess.setBrightness((int)(block.minY > 0.0 ? brightness : block.getBrightness(this.world, x, y - 1, z)));
            tess.setColor(southRed, southGreen, southBlue);
            this.renderFace(block, x, y, z, block.getTexture(this.world, x, y, z, 0), BlockModel.Face.BOTTOM, true);
            renderFace = true;
        }

        if (block.renderFace(this.world, x, y + 1, z, 1)) {
            tess.setBrightness((int)(block.maxY < 1.0 ? brightness : block.getBrightness(this.world, x, y + 1, z)));
            tess.setColor(northRed, northGreen, northBlue);
            this.renderFace(block, x, y, z, block.getTexture(this.world, x, y, z, 1), BlockModel.Face.TOP, true);
            renderFace = true;
        }

        if (block.renderFace(this.world, x, y, z - 1, 2)) {
            tess.setBrightness((int)(block.minZ > 0.0 ? brightness : block.getBrightness(this.world, x, y, z - 1)));
            tess.setColor(westRed, westGreen, westBlue);
            this.renderFace(block, x, y, z, block.getTexture(this.world, x, y, z, 2), BlockModel.Face.NORTH, true);
            renderFace = true;
        }

        if (block.renderFace(this.world, x, y, z + 1, 3)) {
            tess.setBrightness((int)(block.maxZ < 1.0 ? brightness : block.getBrightness(this.world, x, y, z + 1)));
            tess.setColor(westRed, westGreen, westBlue);
            this.renderFace(block, x, y, z, block.getTexture(this.world, x, y, z, 3), BlockModel.Face.SOUTH, true);
            renderFace = true;
        }

        if (block.renderFace(this.world, x - 1, y, z, 4)) {
            tess.setBrightness((int)(block.minX > 0.0 ? brightness : block.getBrightness(this.world, x - 1, y, z)));
            tess.setColor(eastRed, eastGreen, eastBlue);
            this.renderFace(block, x, y, z, block.getTexture(this.world, x, y, z, 4), BlockModel.Face.WEST, true);
            renderFace = true;
        }

        if (block.renderFace(this.world, x + 1, y, z, 5)) {
            tess.setBrightness((int)(block.maxX < 1.0 ? brightness : block.getBrightness(this.world, x + 1, y, z)));
            tess.setColor(eastRed, eastGreen, eastBlue);
            this.renderFace(block, x, y, z, block.getTexture(this.world, x, y, z, 5), BlockModel.Face.EAST, true);
            renderFace = true;
        }

        return renderFace;
    }

    public boolean renderWithAO(Block block, int x, int y, int z, float red, float green, float blue) {
        this.ambientOcclusion = true;
        boolean renderFace = false;
        float lightSouth = this.lightValue;
        float lightNorth = this.lightValue;
        float lightWest = this.lightValue;
        float lightEast = this.lightValue;
        boolean applyBottom = true;
        boolean applyTop = true;
        boolean applyNorth = true;
        boolean applySouth = true;
        boolean applyWest = true;
        boolean applyEast = true;
        this.lightValue = block.getAOSmoothing(this.world, x, y, z);
        this.lightValueXNeg = block.getAOSmoothing(this.world, x - 1, y, z);
        this.lightValueYNeg = block.getAOSmoothing(this.world, x, y - 1, z);
        this.lightValueZNeg = block.getAOSmoothing(this.world, x, y, z - 1);
        this.lightValueXPos = block.getAOSmoothing(this.world, x + 1, y, z);
        this.lightValueYPos = block.getAOSmoothing(this.world, x, y + 1, z);
        this.lightValueZPos = block.getAOSmoothing(this.world, x, y, z + 1);
        int brightSouth = block.getBrightness(this.world, x - 1, y, z);
        int brightBottom = block.getBrightness(this.world, x, y - 1, z);
        int brightWest = block.getBrightness(this.world, x, y, z - 1);
        int brightNorth = block.getBrightness(this.world, x + 1, y, z);
        int brightTop = block.getBrightness(this.world, x, y + 1, z);
        int brightEast = block.getBrightness(this.world, x, y, z + 1);
        if (this.textureOverride != null) {
            applyEast = false;
            applyWest = false;
            applySouth = false;
            applyNorth = false;
            applyBottom = false;
        }

        if (block.renderFace(this.world, x, y - 1, z, 0)) {
            if (this.brightnessBottomNorth <= 0) {
                lightEast = this.lightValueYNeg;
                lightWest = this.lightValueYNeg;
                lightNorth = this.lightValueYNeg;
                lightSouth = this.lightValueYNeg;
                this.brightnessTop = this.brightnessBottom = this.brightnessLeft = this.brightnessRight = this.brightnessDown;
            } else {
                y--;
                this.brightnessDown = block.getBrightness(this.world, x - 1, y, z);
                this.brightnessNorth = block.getBrightness(this.world, x, y, z - 1);
                this.brightnessWest = block.getBrightness(this.world, x, y, z + 1);
                this.brightnessNorthEast = block.getBrightness(this.world, x + 1, y, z);
                this.lightValueScratchXYNN = block.getAOSmoothing(this.world, x - 1, y, z);
                this.lightValueScratchYZNN = block.getAOSmoothing(this.world, x, y, z - 1);
                this.lightValueScratchYZNP = block.getAOSmoothing(this.world, x, y, z + 1);
                this.lightValueScratchXYPN = block.getAOSmoothing(this.world, x + 1, y, z);
                this.lightValueScratchXYZNNN = block.getAOSmoothing(this.world, x - 1, y, z - 1);
                this.brightnessUp = block.getBrightness(this.world, x - 1, y, z - 1);
                this.lightValueScratchXYZNNP = block.getAOSmoothing(this.world, x - 1, y, z + 1);
                this.brightnessSouth = block.getBrightness(this.world, x - 1, y, z + 1);
                this.lightValueScratchXYZPNN = block.getAOSmoothing(this.world, x + 1, y, z - 1);
                this.brightnessEast = block.getBrightness(this.world, x + 1, y, z - 1);
                this.lightValueScratchXYZPNP = block.getAOSmoothing(this.world, x + 1, y, z + 1);
                this.brightnessSouthEast = block.getBrightness(this.world, x + 1, y, z + 1);
                if (this.world.isBlockFull(x, ++y - 1, z)) {
                    brightBottom = block.getBrightness(this.world, x, y, z);
                }

                lightSouth = (this.lightValueScratchXYZNNP + this.lightValueScratchXYNN + this.lightValueScratchYZNP + this.lightValueYNeg) / 4.0F;
                lightEast = (this.lightValueScratchYZNP + this.lightValueYNeg + this.lightValueScratchXYZPNP + this.lightValueScratchXYPN) / 4.0F;
                lightWest = (this.lightValueYNeg + this.lightValueScratchYZNN + this.lightValueScratchXYPN + this.lightValueScratchXYZPNN) / 4.0F;
                lightNorth = (this.lightValueScratchXYNN + this.lightValueScratchXYZNNN + this.lightValueYNeg + this.lightValueScratchYZNN) / 4.0F;
                this.brightnessTop = this.getColors(this.brightnessSouth, this.brightnessDown, this.brightnessWest, brightBottom);
                this.brightnessRight = this.getColors(this.brightnessWest, this.brightnessSouthEast, this.brightnessNorthEast, brightBottom);
                this.brightnessLeft = this.getColors(this.brightnessNorth, this.brightnessNorthEast, this.brightnessEast, brightBottom);
                this.brightnessBottom = this.getColors(this.brightnessDown, this.brightnessUp, this.brightnessNorth, brightBottom);
            }

            this.setColors(lightSouth, lightNorth, lightWest, lightEast, applyBottom, red, green, blue, 0.5F);
            this.renderFace(block, x, y, z, block.getTexture(this.world, x, y, z, 0), BlockModel.Face.BOTTOM, true);
            renderFace = true;
        }

        if (block.renderFace(this.world, x, y + 1, z, 1)) {
            if (this.brightnessBottomNorth <= 0) {
                lightEast = this.lightValueYPos;
                lightWest = this.lightValueYPos;
                lightNorth = this.lightValueYPos;
                lightSouth = this.lightValueYPos;
                this.brightnessTop = this.brightnessBottom = this.brightnessLeft = this.brightnessRight = brightTop;
            } else {
                y++;
                this.brightnessSouthWest = block.getBrightness(this.world, x - 1, y, z);
                this.brightnessSouthNorth = block.getBrightness(this.world, x + 1, y, z);
                this.brightnessWestEast = block.getBrightness(this.world, x, y, z - 1);
                this.brightnessTopWest = block.getBrightness(this.world, x, y, z + 1);
                this.lightValueScratchXYNP = block.getAOSmoothing(this.world, x - 1, y, z);
                this.lightValueScratchXYPP = block.getAOSmoothing(this.world, x + 1, y, z);
                this.lightValueScratchYZPN = block.getAOSmoothing(this.world, x, y, z - 1);
                this.lightValueScratchYZPP = block.getAOSmoothing(this.world, x, y, z + 1);
                this.lightValueScratchXYZNPN = block.getAOSmoothing(this.world, x - 1, y, z - 1);
                this.brightnessNorthWest = block.getBrightness(this.world, x - 1, y, z - 1);
                this.lightValueScratchXYZPPN = block.getAOSmoothing(this.world, x + 1, y, z - 1);
                this.brightnessNorthSouth = block.getBrightness(this.world, x + 1, y, z - 1);
                this.lightValueScratchXYZNPP = block.getAOSmoothing(this.world, x - 1, y, z + 1);
                this.brightnessEastWest = block.getBrightness(this.world, x - 1, y, z + 1);
                this.lightValueScratchXYZPPP = block.getAOSmoothing(this.world, x + 1, y, z + 1);
                this.brightnessTopEast = block.getBrightness(this.world, x + 1, y, z + 1);
                if (this.world.isBlockFull(x, --y + 1, z)) {
                    brightTop = block.getBrightness(this.world, x, y, z);
                }

                lightEast = (this.lightValueScratchXYZNPP + this.lightValueScratchXYNP + this.lightValueScratchYZPP + this.lightValueYPos) / 4.0F;
                lightSouth = (this.lightValueScratchYZPP + this.lightValueYPos + this.lightValueScratchXYZPPP + this.lightValueScratchXYPP) / 4.0F;
                lightNorth = (this.lightValueYPos + this.lightValueScratchYZPN + this.lightValueScratchXYPP + this.lightValueScratchXYZPPN) / 4.0F;
                lightWest = (this.lightValueScratchXYNP + this.lightValueScratchXYZNPN + this.lightValueYPos + this.lightValueScratchYZPN) / 4.0F;
                this.brightnessRight = this.getColors(this.brightnessEastWest, this.brightnessSouthWest, this.brightnessTopWest, brightTop);
                this.brightnessTop = this.getColors(this.brightnessTopWest, this.brightnessTopEast, this.brightnessSouthNorth, brightTop);
                this.brightnessBottom = this.getColors(this.brightnessWestEast, this.brightnessSouthNorth, this.brightnessNorthSouth, brightTop);
                this.brightnessLeft = this.getColors(this.brightnessSouthWest, this.brightnessNorthWest, this.brightnessWestEast, brightTop);
            }

            this.setColors(lightSouth, lightNorth, lightWest, lightEast, applyTop, red, green, blue, 1.0F);
            this.renderFace(block, x, y, z, block.getTexture(this.world, x, y, z, 1), BlockModel.Face.TOP, true);
            renderFace = true;
        }

        if (block.renderFace(this.world, x, y, z - 1, 2)) {
            if (this.brightnessBottomNorth <= 0) {
                lightEast = this.lightValueZNeg;
                lightWest = this.lightValueZNeg;
                lightNorth = this.lightValueZNeg;
                lightSouth = this.lightValueZNeg;
                this.brightnessTop = this.brightnessBottom = this.brightnessLeft = this.brightnessRight = brightWest;
            } else {
                z--;
                this.lightValueScratchXZNN = block.getAOSmoothing(this.world, x - 1, y, z);
                this.lightValueScratchYZNN = block.getAOSmoothing(this.world, x, y - 1, z);
                this.lightValueScratchYZPN = block.getAOSmoothing(this.world, x, y + 1, z);
                this.lightValueScratchXZPN = block.getAOSmoothing(this.world, x + 1, y, z);
                this.brightnessTopNorth = block.getBrightness(this.world, x - 1, y, z);
                this.brightnessNorth = block.getBrightness(this.world, x, y - 1, z);
                this.brightnessWestEast = block.getBrightness(this.world, x, y + 1, z);
                this.brightnessTopSouth = block.getBrightness(this.world, x + 1, y, z);
                this.lightValueScratchXYZNNN = block.getAOSmoothing(this.world, x - 1, y - 1, z);
                this.brightnessUp = block.getBrightness(this.world, x - 1, y - 1, z);
                this.lightValueScratchXYZNPN = block.getAOSmoothing(this.world, x - 1, y + 1, z);
                this.brightnessNorthWest = block.getBrightness(this.world, x - 1, y + 1, z);
                this.lightValueScratchXYZPNN = block.getAOSmoothing(this.world, x + 1, y - 1, z);
                this.brightnessEast = block.getBrightness(this.world, x + 1, y - 1, z);
                this.lightValueScratchXYZPPN = block.getAOSmoothing(this.world, x + 1, y + 1, z);
                this.brightnessNorthSouth = block.getBrightness(this.world, x + 1, y + 1, z);
                if (this.world.isBlockFull(x, y, ++z - 1)) {
                    brightWest = block.getBrightness(this.world, x, y, z);
                }

                lightSouth = (this.lightValueScratchXZNN + this.lightValueScratchXYZNPN + this.lightValueZNeg + this.lightValueScratchYZPN) / 4.0F;
                lightNorth = (this.lightValueZNeg + this.lightValueScratchYZPN + this.lightValueScratchXZPN + this.lightValueScratchXYZPPN) / 4.0F;
                lightWest = (this.lightValueScratchYZNN + this.lightValueZNeg + this.lightValueScratchXYZPNN + this.lightValueScratchXZPN) / 4.0F;
                lightEast = (this.lightValueScratchXYZNNN + this.lightValueScratchXZNN + this.lightValueScratchYZNN + this.lightValueZNeg) / 4.0F;
                this.brightnessTop = this.getColors(this.brightnessTopNorth, this.brightnessNorthWest, this.brightnessWestEast, brightWest);
                this.brightnessBottom = this.getColors(this.brightnessWestEast, this.brightnessTopSouth, this.brightnessNorthSouth, brightWest);
                this.brightnessLeft = this.getColors(this.brightnessNorth, this.brightnessEast, this.brightnessTopSouth, brightWest);
                this.brightnessRight = this.getColors(this.brightnessUp, this.brightnessTopNorth, this.brightnessNorth, brightWest);
            }

            this.setColors(lightSouth, lightNorth, lightWest, lightEast, applyNorth, red, green, blue, 0.8F);
            this.renderFace(block, x, y, z, block.getTexture(this.world, x, y, z, 2), BlockModel.Face.NORTH, true);
            renderFace = true;
        }

        if (block.renderFace(this.world, x, y, z + 1, 3)) {
            if (this.brightnessBottomNorth <= 0) {
                lightEast = this.lightValueZPos;
                lightWest = this.lightValueZPos;
                lightNorth = this.lightValueZPos;
                lightSouth = this.lightValueZPos;
                this.brightnessTop = this.brightnessBottom = this.brightnessLeft = this.brightnessRight = brightEast;
            } else {
                z++;
                this.lightValueScratchXZNP = block.getAOSmoothing(this.world, x - 1, y, z);
                this.lightValueScratchXZPP = block.getAOSmoothing(this.world, x + 1, y, z);
                this.lightValueScratchYZNP = block.getAOSmoothing(this.world, x, y - 1, z);
                this.lightValueScratchYZPP = block.getAOSmoothing(this.world, x, y + 1, z);
                this.brightnessBottomWest = block.getBrightness(this.world, x - 1, y, z);
                this.brightnessBottomEast = block.getBrightness(this.world, x + 1, y, z);
                this.brightnessWest = block.getBrightness(this.world, x, y - 1, z);
                this.brightnessTopWest = block.getBrightness(this.world, x, y + 1, z);
                this.lightValueScratchXYZNNP = block.getAOSmoothing(this.world, x - 1, y - 1, z);
                this.brightnessSouth = block.getBrightness(this.world, x - 1, y - 1, z);
                this.lightValueScratchXYZNPP = block.getAOSmoothing(this.world, x - 1, y + 1, z);
                this.brightnessEastWest = block.getBrightness(this.world, x - 1, y + 1, z);
                this.lightValueScratchXYZPNP = block.getAOSmoothing(this.world, x + 1, y - 1, z);
                this.brightnessSouthEast = block.getBrightness(this.world, x + 1, y - 1, z);
                this.lightValueScratchXYZPPP = block.getAOSmoothing(this.world, x + 1, y + 1, z);
                this.brightnessTopEast = block.getBrightness(this.world, x + 1, y + 1, z);
                if (this.world.isBlockFull(x, y, --z + 1)) {
                    brightEast = block.getBrightness(this.world, x, y, z);
                }

                lightSouth = (this.lightValueScratchXZNP + this.lightValueScratchXYZNPP + this.lightValueZPos + this.lightValueScratchYZPP) / 4.0F;
                lightEast = (this.lightValueZPos + this.lightValueScratchYZPP + this.lightValueScratchXZPP + this.lightValueScratchXYZPPP) / 4.0F;
                lightWest = (this.lightValueScratchYZNP + this.lightValueZPos + this.lightValueScratchXYZPNP + this.lightValueScratchXZPP) / 4.0F;
                lightNorth = (this.lightValueScratchXYZNNP + this.lightValueScratchXZNP + this.lightValueScratchYZNP + this.lightValueZPos) / 4.0F;
                this.brightnessTop = this.getColors(this.brightnessBottomWest, this.brightnessEastWest, this.brightnessTopWest, brightEast);
                this.brightnessRight = this.getColors(this.brightnessTopWest, this.brightnessBottomEast, this.brightnessTopEast, brightEast);
                this.brightnessLeft = this.getColors(this.brightnessWest, this.brightnessSouthEast, this.brightnessBottomEast, brightEast);
                this.brightnessBottom = this.getColors(this.brightnessSouth, this.brightnessBottomWest, this.brightnessWest, brightEast);
            }

            this.setColors(lightSouth, lightNorth, lightWest, lightEast, applySouth, red, green, blue, 0.8F);
            this.renderFace(block, x, y, z, block.getTexture(this.world, x, y, z, 3), BlockModel.Face.SOUTH, true);
            renderFace = true;
        }

        if (block.renderFace(this.world, x - 1, y, z, 4)) {
            if (this.brightnessBottomNorth <= 0) {
                lightEast = this.lightValueXNeg;
                lightWest = this.lightValueXNeg;
                lightNorth = this.lightValueXNeg;
                lightSouth = this.lightValueXNeg;
                this.brightnessTop = this.brightnessBottom = this.brightnessLeft = this.brightnessRight = brightSouth;
            } else {
                this.lightValueScratchXYNN = block.getAOSmoothing(this.world, --x, y - 1, z);
                this.lightValueScratchXZNN = block.getAOSmoothing(this.world, x, y, z - 1);
                this.lightValueScratchXZNP = block.getAOSmoothing(this.world, x, y, z + 1);
                this.lightValueScratchXYNP = block.getAOSmoothing(this.world, x, y + 1, z);
                this.brightnessDown = block.getBrightness(this.world, x, y - 1, z);
                this.brightnessTopNorth = block.getBrightness(this.world, x, y, z - 1);
                this.brightnessBottomWest = block.getBrightness(this.world, x, y, z + 1);
                this.brightnessSouthWest = block.getBrightness(this.world, x, y + 1, z);
                this.lightValueScratchXYZNNN = block.getAOSmoothing(this.world, x, y - 1, z - 1);
                this.brightnessUp = block.getBrightness(this.world, x, y - 1, z - 1);
                this.lightValueScratchXYZNNP = block.getAOSmoothing(this.world, x, y - 1, z + 1);
                this.brightnessSouth = block.getBrightness(this.world, x, y - 1, z + 1);
                this.lightValueScratchXYZNPN = block.getAOSmoothing(this.world, x, y + 1, z - 1);
                this.brightnessNorthWest = block.getBrightness(this.world, x, y + 1, z - 1);
                this.lightValueScratchXYZNPP = block.getAOSmoothing(this.world, x, y + 1, z + 1);
                this.brightnessEastWest = block.getBrightness(this.world, x, y + 1, z + 1);
                if (this.world.isBlockFull(++x - 1, y, z)) {
                    brightSouth = block.getBrightness(this.world, x, y, z);
                }

                lightEast = (this.lightValueScratchXYNN + this.lightValueScratchXYZNNP + this.lightValueXNeg + this.lightValueScratchXZNP) / 4.0F;
                lightSouth = (this.lightValueXNeg + this.lightValueScratchXZNP + this.lightValueScratchXYNP + this.lightValueScratchXYZNPP) / 4.0F;
                lightNorth = (this.lightValueScratchXZNN + this.lightValueXNeg + this.lightValueScratchXYZNPN + this.lightValueScratchXYNP) / 4.0F;
                lightWest = (this.lightValueScratchXYZNNN + this.lightValueScratchXYNN + this.lightValueScratchXZNN + this.lightValueXNeg) / 4.0F;
                this.brightnessRight = this.getColors(this.brightnessDown, this.brightnessSouth, this.brightnessBottomWest, brightSouth);
                this.brightnessTop = this.getColors(this.brightnessBottomWest, this.brightnessSouthWest, this.brightnessEastWest, brightSouth);
                this.brightnessBottom = this.getColors(this.brightnessTopNorth, this.brightnessNorthWest, this.brightnessSouthWest, brightSouth);
                this.brightnessLeft = this.getColors(this.brightnessUp, this.brightnessDown, this.brightnessTopNorth, brightSouth);
            }

            this.setColors(lightSouth, lightNorth, lightWest, lightEast, applyWest, red, green, blue, 0.6F);
            this.renderFace(block, x, y, z, block.getTexture(this.world, x, y, z, 4), BlockModel.Face.WEST, true);
            renderFace = true;
        }

        if (block.renderFace(this.world, x + 1, y, z, 5)) {
            if (this.brightnessBottomNorth <= 0) {
                lightEast = this.lightValueXPos;
                lightWest = this.lightValueXPos;
                lightNorth = this.lightValueXPos;
                lightSouth = this.lightValueXPos;
                this.brightnessTop = this.brightnessBottom = this.brightnessLeft = this.brightnessRight = brightNorth;
            } else {
                this.lightValueScratchXYPN = block.getAOSmoothing(this.world, ++x, y - 1, z);
                this.lightValueScratchXZPN = block.getAOSmoothing(this.world, x, y, z - 1);
                this.lightValueScratchXZPP = block.getAOSmoothing(this.world, x, y, z + 1);
                this.lightValueScratchXYPP = block.getAOSmoothing(this.world, x, y + 1, z);
                this.brightnessNorthEast = block.getBrightness(this.world, x, y - 1, z);
                this.brightnessTopSouth = block.getBrightness(this.world, x, y, z - 1);
                this.brightnessBottomEast = block.getBrightness(this.world, x, y, z + 1);
                this.brightnessSouthNorth = block.getBrightness(this.world, x, y + 1, z);
                this.lightValueScratchXYZPNN = block.getAOSmoothing(this.world, x, y - 1, z - 1);
                this.brightnessEast = block.getBrightness(this.world, x, y - 1, z - 1);
                this.lightValueScratchXYZPNP = block.getAOSmoothing(this.world, x, y - 1, z + 1);
                this.brightnessSouthEast = block.getBrightness(this.world, x, y - 1, z + 1);
                this.lightValueScratchXYZPPN = block.getAOSmoothing(this.world, x, y + 1, z - 1);
                this.brightnessNorthSouth = block.getBrightness(this.world, x, y + 1, z - 1);
                this.lightValueScratchXYZPPP = block.getAOSmoothing(this.world, x, y + 1, z + 1);
                this.brightnessTopEast = block.getBrightness(this.world, x, y + 1, z + 1);
                if (this.world.isBlockFull(--x + 1, y, z)) {
                    brightNorth = block.getBrightness(this.world, x, y, z);
                }

                lightSouth = (this.lightValueScratchXYPN + this.lightValueScratchXYZPNP + this.lightValueXPos + this.lightValueScratchXZPP) / 4.0F;
                lightEast = (this.lightValueXPos + this.lightValueScratchXZPP + this.lightValueScratchXYPP + this.lightValueScratchXYZPPP) / 4.0F;
                lightWest = (this.lightValueScratchXZPN + this.lightValueXPos + this.lightValueScratchXYZPPN + this.lightValueScratchXYPP) / 4.0F;
                lightNorth = (this.lightValueScratchXYZPNN + this.lightValueScratchXYPN + this.lightValueScratchXZPN + this.lightValueXPos) / 4.0F;
                this.brightnessTop = this.getColors(this.brightnessNorthEast, this.brightnessSouthEast, this.brightnessBottomEast, brightNorth);
                this.brightnessRight = this.getColors(this.brightnessBottomEast, this.brightnessSouthNorth, this.brightnessTopEast, brightNorth);
                this.brightnessLeft = this.getColors(this.brightnessTopSouth, this.brightnessNorthSouth, this.brightnessSouthNorth, brightNorth);
                this.brightnessBottom = this.getColors(this.brightnessEast, this.brightnessNorthEast, this.brightnessTopSouth, brightNorth);
            }

            this.setColors(lightSouth, lightNorth, lightWest, lightEast, applyEast, red, green, blue, 0.6F);
            this.renderFace(block, x, y, z, block.getTexture(this.world, x, y, z, 5), BlockModel.Face.EAST, true);
            renderFace = true;
        }

        this.ambientOcclusion = false;
        return renderFace;
    }

    private void setColors(float lightSouth, float lightNorth, float lightWest, float lightEast, boolean apply, float red, float green, float blue, float val) {
        this.colorRedTopLeft = this.colorRedBottomLeft = this.colorRedBottomRight = this.colorRedTopRight = (apply ? red : 1.0F) * val;
        this.colorGreenTopLeft = this.colorGreenBottomLeft = this.colorGreenBottomRight = this.colorGreenTopRight = (apply ? green : 1.0F) * val;
        this.colorBlueTopLeft = this.colorBlueBottomLeft = this.colorBlueBottomRight = this.colorBlueTopRight = (apply ? blue : 1.0F) * val;
        this.colorRedTopLeft *= lightSouth;
        this.colorGreenTopLeft *= lightSouth;
        this.colorBlueTopLeft *= lightSouth;
        this.colorRedBottomLeft *= lightNorth;
        this.colorGreenBottomLeft *= lightNorth;
        this.colorBlueBottomLeft *= lightNorth;
        this.colorRedBottomRight *= lightWest;
        this.colorGreenBottomRight *= lightWest;
        this.colorBlueBottomRight *= lightWest;
        this.colorRedTopRight *= lightEast;
        this.colorGreenTopRight *= lightEast;
        this.colorBlueTopRight *= lightEast;
    }

    private int getColors(int red, int green, int blue, int alpha) {
        if (red == 0) {
            red = alpha;
        }

        if (green == 0) {
            green = alpha;
        }

        if (blue == 0) {
            blue = alpha;
        }

        return red + green + blue + alpha >> 2 & 16711935;
    }

    public void renderFace(Block block, double x, double y, double z, Icon icon, BlockModel.Face face, boolean isWorld) {
        if (icon != null) {
            if (this.hasOverrideTexture()) {
                icon = this.textureOverride;
            }

            Tessellator tess = Tessellator.instance;
            tess.setNormals(face.nx, face.ny, face.nz);
            this.setTextureBoundaries(block, icon, face);
            this.bounds[0] = x + block.minX;
            this.bounds[1] = x + block.maxX;
            this.bounds[2] = y + block.minY;
            this.bounds[3] = y + block.maxY;
            this.bounds[4] = z + block.minZ;
            this.bounds[5] = z + block.maxZ;
            this.uvCorners[0][0] = this.uvs[1];
            this.uvCorners[0][1] = this.uvs[3];
            this.uvCorners[1][0] = this.uvs[0];
            this.uvCorners[1][1] = this.uvs[3];
            this.uvCorners[2][0] = this.uvs[0];
            this.uvCorners[2][1] = this.uvs[2];
            this.uvCorners[3][0] = this.uvs[1];
            this.uvCorners[3][1] = this.uvs[2];
            if (face == BlockModel.Face.TOP || face == BlockModel.Face.BOTTOM) {
                float tmp = this.uvCorners[0][0];
                this.uvCorners[0][0] = this.uvCorners[1][0];
                this.uvCorners[1][0] = tmp;
                tmp = this.uvCorners[2][0];
                this.uvCorners[2][0] = this.uvCorners[3][0];
                this.uvCorners[3][0] = tmp;
            }

            this.refreshBuffers();
            boolean hasRotation = this.rotations[face.id] >= 0;

            for (int i = 0; i < 4; i++) {
                double vx = this.bounds[face.corners[i][0] == 0 ? 0 : 1];
                double vy = this.bounds[face.corners[i][1] == 0 ? 2 : 3];
                double vz = this.bounds[face.corners[i][2] == 0 ? 4 : 5];
                if (isWorld) {
                    double noiseX = noise.generateNoise(vx, vz);
                    double noiseY = noise.generateNoise(vz, vy);
                    double noiseZ = noise.generateNoise(vx, vy);
                    double scale = OptionValues.offsetAmount * 0.2;
                    this.noiseOffsets[i][0] = noiseX * scale;
                    this.noiseOffsets[i][1] = noiseY * scale;
                    this.noiseOffsets[i][2] = noiseZ * scale;
                    vx += this.noiseOffsets[i][0];
                    vy += this.noiseOffsets[i][1];
                    vz += this.noiseOffsets[i][2];
                }

                if (hasRotation) {
                    int rotatedIndex = (i + this.rotations[face.id]) % 4;
                    this.rotatedUVs[i][0] = this.uvCorners[rotatedIndex][0];
                    this.rotatedUVs[i][1] = this.uvCorners[rotatedIndex][1];
                }

                if (this.flipTexture) {
                    this.rotatedUVs[i][0] = this.uvs[1] + this.uvs[0] - this.rotatedUVs[i][0];
                }

                float u = hasRotation ? this.rotatedUVs[i][0] : this.uvCorners[i][0];
                float v = hasRotation ? this.rotatedUVs[i][1] : this.uvCorners[i][1];
                if (this.ambientOcclusion) {
                    int li = face.aoRotation[i];
                    tess.setColor(this.colors[0][li], this.colors[1][li], this.colors[2][li]);
                    tess.setBrightness(this.brightness[li]);
                }

                tess.addTexturedVertex(vx, vy, vz, u, v);
            }
        }
    }

    private void refreshBuffers() {
        this.colors[0][0] = this.colorRedTopLeft;
        this.colors[0][1] = this.colorRedBottomLeft;
        this.colors[0][2] = this.colorRedBottomRight;
        this.colors[0][3] = this.colorRedTopRight;
        this.colors[1][0] = this.colorGreenTopLeft;
        this.colors[1][1] = this.colorGreenBottomLeft;
        this.colors[1][2] = this.colorGreenBottomRight;
        this.colors[1][3] = this.colorGreenTopRight;
        this.colors[2][0] = this.colorBlueTopLeft;
        this.colors[2][1] = this.colorBlueBottomLeft;
        this.colors[2][2] = this.colorBlueBottomRight;
        this.colors[2][3] = this.colorBlueTopRight;
        this.brightness[0] = this.brightnessTop;
        this.brightness[1] = this.brightnessBottom;
        this.brightness[2] = this.brightnessLeft;
        this.brightness[3] = this.brightnessRight;
        this.rotations[0] = this.rotationBottom;
        this.rotations[1] = this.rotationTop;
        this.rotations[2] = this.rotationNorth;
        this.rotations[3] = this.rotationSouth;
        this.rotations[4] = this.rotationWest;
        this.rotations[5] = this.rotationEast;
    }

    private void setTextureBoundaries(Block block, Icon icon, BlockModel.Face face) {
        float uMin;
        float uMax;
        float vMin;
        float vMax;
        switch (face) {
            case BOTTOM:
            case TOP:
                uMin = icon.getInterpolatedU(block.minX * 16.0);
                uMax = icon.getInterpolatedU(block.maxX * 16.0);
                vMin = icon.getInterpolatedV(block.minZ * 16.0);
                vMax = icon.getInterpolatedV(block.maxZ * 16.0);
                if (this.rotationTop == 1 || this.rotationBottom == 1) {
                    uMin = icon.getInterpolatedU(block.minZ * 16.0);
                    vMin = icon.getInterpolatedV(16.0 - block.maxX * 16.0);
                    uMax = icon.getInterpolatedU(block.maxZ * 16.0);
                    vMax = icon.getInterpolatedV(16.0 - block.minX * 16.0);
                }

                if (this.rotationTop == 2 || this.rotationBottom == 2) {
                    uMin = this.calculateU(icon, block.minX, block.maxX, this.rotationTop == 2);
                    vMin = this.calculateV(icon, block.minZ, block.maxZ, this.rotationTop == 2);
                    uMax = this.calculateU(icon, block.maxX, block.minX, this.rotationTop == 2);
                    vMax = this.calculateV(icon, block.maxZ, block.minZ, this.rotationTop == 2);
                }

                if (this.rotationTop == 3 || this.rotationBottom == 3) {
                    uMin = icon.getInterpolatedU(16.0 - block.maxZ * 16.0);
                    vMin = icon.getInterpolatedV(block.minX * 16.0);
                    uMax = icon.getInterpolatedU(16.0 - block.minZ * 16.0);
                    vMax = icon.getInterpolatedV(block.maxX * 16.0);
                }

                if (block.minX < 0.0 || block.maxX > 1.0) {
                    uMin = icon.getMinU();
                    uMax = icon.getMaxU();
                }

                if (block.minZ < 0.0 || block.maxZ > 1.0) {
                    vMin = icon.getMinV();
                    vMax = icon.getMaxV();
                }
                break;
            case NORTH:
            case SOUTH:
                uMin = icon.getInterpolatedU(block.minX * 16.0);
                uMax = icon.getInterpolatedU(block.maxX * 16.0);
                vMin = icon.getInterpolatedV(16.0 - block.minY * 16.0);
                vMax = icon.getInterpolatedV(16.0 - block.maxY * 16.0);
                if (this.rotationNorth == 1 || this.rotationSouth == 1) {
                    uMin = this.calculateU(icon, block.minY, block.maxY, this.rotationNorth == 1);
                    vMin = this.calculateV(icon, block.minX, block.maxX, this.rotationNorth == 1);
                    uMax = this.calculateU(icon, block.maxY, block.minY, this.rotationNorth == 1);
                    vMax = this.calculateV(icon, block.maxX, block.minX, this.rotationNorth == 1);
                }

                if (this.rotationSouth == 2 || this.rotationNorth == 2) {
                    uMin = icon.getInterpolatedU(16.0 - block.maxX * 16.0);
                    vMin = icon.getInterpolatedV(block.maxY * 16.0);
                    uMax = icon.getInterpolatedU(16.0 - block.minX * 16.0);
                    vMax = icon.getInterpolatedV(block.minY * 16.0);
                }

                if (this.rotationSouth == 3 || this.rotationNorth == 3) {
                    uMin = this.calculateU(icon, block.minY, block.maxY, this.rotationSouth == 3);
                    vMin = this.calculateV(icon, block.minX, block.maxX, this.rotationSouth == 3);
                    uMax = this.calculateU(icon, block.maxY, block.minY, this.rotationSouth == 3);
                    vMax = this.calculateV(icon, block.maxX, block.minX, this.rotationSouth == 3);
                }

                if (block.minX < 0.0 || block.maxX > 1.0) {
                    uMin = icon.getMinU();
                    uMax = icon.getMaxU();
                }

                if (block.minY < 0.0 || block.maxY > 1.0) {
                    vMax = icon.getMaxV();
                    vMin = icon.getMinV();
                }
                break;
            case WEST:
            case EAST:
                uMin = icon.getInterpolatedU(block.minZ * 16.0);
                uMax = icon.getInterpolatedU(block.maxZ * 16.0);
                vMin = icon.getInterpolatedV(16.0 - block.minY * 16.0);
                vMax = icon.getInterpolatedV(16.0 - block.maxY * 16.0);
                if (this.rotationWest == 1 || this.rotationEast == 1) {
                    uMin = this.calculateU(icon, block.minY, block.maxY, this.rotationEast == 1);
                    vMin = this.calculateV(icon, block.minZ, block.maxZ, this.rotationEast == 1);
                    uMax = this.calculateU(icon, block.maxY, block.minY, this.rotationEast == 1);
                    vMax = this.calculateV(icon, block.maxZ, block.minZ, this.rotationEast == 1);
                }

                if (this.rotationWest == 2 || this.rotationEast == 2) {
                    uMin = icon.getInterpolatedU(16.0 - block.maxZ * 16.0);
                    vMin = icon.getInterpolatedV(block.maxY * 16.0);
                    uMax = icon.getInterpolatedU(16.0 - block.minZ * 16.0);
                    vMax = icon.getInterpolatedV(block.minY * 16.0);
                }

                if (this.rotationWest == 3 || this.rotationEast == 3) {
                    uMin = this.calculateU(icon, block.minY, block.maxY, this.rotationWest == 3);
                    vMin = this.calculateV(icon, block.minZ, block.maxZ, this.rotationWest == 3);
                    uMax = this.calculateU(icon, block.maxY, block.minY, this.rotationWest == 3);
                    vMax = this.calculateV(icon, block.maxZ, block.minZ, this.rotationWest == 3);
                }

                if (block.minZ < 0.0 || block.maxZ > 1.0) {
                    uMin = icon.getMinU();
                    uMax = icon.getMaxU();
                }

                if (block.minY < 0.0 || block.maxY > 1.0) {
                    vMax = icon.getMaxV();
                    vMin = icon.getMinV();
                }
                break;
            default:
                uMin = icon.getInterpolatedU(0.0);
                uMax = icon.getInterpolatedU(16.0);
                vMin = icon.getInterpolatedV(0.0);
                vMax = icon.getInterpolatedV(16.0);
        }

        this.uvs[0] = uMin;
        this.uvs[1] = uMax;
        this.uvs[2] = vMin;
        this.uvs[3] = vMax;
    }

    private float calculateU(Icon icon, double uMin, double uMax, boolean flip) {
        return flip ? icon.getInterpolatedU(16.0 - uMax * 16.0) : icon.getInterpolatedU(uMin * 16.0);
    }

    private float calculateV(Icon icon, double uMin, double uMax, boolean flip) {
        return flip ? icon.getInterpolatedV(16.0 - uMax * 16.0) : icon.getInterpolatedV(uMin * 16.0);
    }

    public void renderCross(Block block, int meta, double x, double y, double z) {
        Tessellator tess = Tessellator.instance;
        Icon icon = block.getIcon(0, meta);
        if (this.hasOverrideTexture()) {
            icon = this.textureOverride;
        }

        if (block instanceof GlowLychen) {
            long seed = (long)(x * 3129871.0) ^ (long)y * 116129781L ^ (long)z;
            seed = seed * seed * 42317861L + seed * 11L;
            int rotation = (int)(seed >> 16 & 3L);
            switch (rotation) {
                case 0:
                    icon = ((GlowLychen)block).variant1;
                    break;
                case 1:
                    icon = ((GlowLychen)block).variant2;
                    break;
                default:
                    icon = block.getIcon(0, meta);
            }
        }

        if (block instanceof Flower && OptionValues.textureRotation) {
            long seed = (long)(x * 3129871.0) ^ (long)y * 116129781L ^ (long)z;
            seed = seed * seed * 42317861L + seed * 11L;
            double offsetAmount = 0.2;
            if (block instanceof TallGrass) {
                offsetAmount = 0.5;
                y += ((float)(seed >> 20 & 15L) / 15.0F - 1.0) * 0.2;
            }

            if (block instanceof HangingVine) {
                offsetAmount = 0.15;
            }

            x += ((float)(seed >> 16 & 15L) / 15.0F - 0.5) * offsetAmount;
            z += ((float)(seed >> 24 & 15L) / 15.0F - 0.5) * offsetAmount;
        }

        float minU = icon.getMinU();
        float maxU = icon.getMaxU();
        float minV = icon.getMinV();
        float maxV = icon.getMaxV();
        double offUMin = x + 0.5 - 0.45F;
        double offUMax = x + 0.5 + 0.45F;
        double offVMin = z + 0.5 - 0.45F;
        double offVMax = z + 0.5 + 0.45F;
        double offsetTop = y + 1.0;
        double top = y;
        if (block instanceof StoneSpike && (meta == 3 || meta == 4)) {
            top = y + 1.0;
            offsetTop = y;
        }

        tess.addTexturedVertex(offUMin, offsetTop, offVMin, minU, minV);
        tess.addTexturedVertex(offUMin, top, offVMin, minU, maxV);
        tess.addTexturedVertex(offUMax, top, offVMax, maxU, maxV);
        tess.addTexturedVertex(offUMax, offsetTop, offVMax, maxU, minV);
        tess.addTexturedVertex(offUMax, offsetTop, offVMax, minU, minV);
        tess.addTexturedVertex(offUMax, top, offVMax, minU, maxV);
        tess.addTexturedVertex(offUMin, top, offVMin, maxU, maxV);
        tess.addTexturedVertex(offUMin, offsetTop, offVMin, maxU, minV);
        tess.addTexturedVertex(offUMin, offsetTop, offVMax, minU, minV);
        tess.addTexturedVertex(offUMin, top, offVMax, minU, maxV);
        tess.addTexturedVertex(offUMax, top, offVMin, maxU, maxV);
        tess.addTexturedVertex(offUMax, offsetTop, offVMin, maxU, minV);
        tess.addTexturedVertex(offUMax, offsetTop, offVMin, minU, minV);
        tess.addTexturedVertex(offUMax, top, offVMin, minU, maxV);
        tess.addTexturedVertex(offUMin, top, offVMax, maxU, maxV);
        tess.addTexturedVertex(offUMin, offsetTop, offVMax, maxU, minV);
    }

    protected void applyColorCorrection(Block block, int x, int y, int z, float amount) {
        Tessellator tess = Tessellator.instance;
        tess.setBrightness(this.world.getBrightness(x, y, z, 0));
        int fullBright = block.getRenderColor(this.world, x, y, z);
        float red = (fullBright >> 16 & 0xFF) / 255.0F;
        float green = (fullBright >> 8 & 0xFF) / 255.0F;
        float blue = (fullBright & 0xFF) / 255.0F;
        tess.setColor(amount * red, amount * green, amount * blue);
    }

    public void renderInGUI(Block block, int dat) {
        Tessellator tess = Tessellator.instance;
        block.forceBounds(dat);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        tess.begin();
        tess.setNormals(0.0F, -1.0F, 0.0F);
        this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(0, dat), BlockModel.Face.BOTTOM, false);
        tess.setNormals(0.0F, 1.0F, 0.0F);
        this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(1, dat), BlockModel.Face.TOP, false);
        tess.setNormals(0.0F, 0.0F, -1.0F);
        this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(2, dat), BlockModel.Face.NORTH, false);
        tess.setNormals(0.0F, 0.0F, 1.0F);
        this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(3, dat), BlockModel.Face.SOUTH, false);
        tess.setNormals(-1.0F, 0.0F, 0.0F);
        this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(4, dat), BlockModel.Face.WEST, false);
        tess.setNormals(1.0F, 0.0F, 0.0F);
        this.renderFace(block, 0.0, 0.0, 0.0, block.getIcon(5, dat), BlockModel.Face.EAST, false);
        tess.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }

    public boolean renderItemIn3d() {
        return true;
    }

    public enum Face {
        WEST(new int[][]{{0, 1, 1}, {0, 1, 0}, {0, 0, 0}, {0, 0, 1}}, new int[]{0, 1, 2, 3}, -1.0F, 0.0F, 0.0F, 4),
        EAST(new int[][]{{1, 1, 0}, {1, 1, 1}, {1, 0, 1}, {1, 0, 0}}, new int[]{2, 3, 0, 1}, 1.0F, 0.0F, 0.0F, 5),
        NORTH(new int[][]{{0, 1, 0}, {1, 1, 0}, {1, 0, 0}, {0, 0, 0}}, new int[]{0, 1, 2, 3}, 0.0F, 0.0F, -1.0F, 2),
        SOUTH(new int[][]{{1, 1, 1}, {0, 1, 1}, {0, 0, 1}, {1, 0, 1}}, new int[]{3, 0, 1, 2}, 0.0F, 0.0F, 1.0F, 3),
        TOP(new int[][]{{0, 1, 1}, {1, 1, 1}, {1, 1, 0}, {0, 1, 0}}, new int[]{3, 0, 1, 2}, 0.0F, 1.0F, 0.0F, 1),
        BOTTOM(new int[][]{{0, 0, 0}, {1, 0, 0}, {1, 0, 1}, {0, 0, 1}}, new int[]{1, 2, 3, 0}, 0.0F, -1.0F, 0.0F, 0);

        public final int[][] corners;
        public final int[] aoRotation;
        public final float nx;
        public final float ny;
        public final float nz;
        public final int id;

        Face(int[][] corners, int[] aoRot, float nx, float ny, float nz, int id) {
            this.corners = corners;
            this.aoRotation = aoRot;
            this.nx = nx;
            this.ny = ny;
            this.nz = nz;
            this.id = id;
        }
    }
}
