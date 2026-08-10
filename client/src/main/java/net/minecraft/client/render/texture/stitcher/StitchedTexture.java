package net.minecraft.client.render.texture.stitcher;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texture.Texture;
import net.minecraft.client.render.texture.dynamic.block.FlowingLavaTexture;
import net.minecraft.client.render.texture.dynamic.block.FlowingWaterTexture;
import net.minecraft.client.render.texture.dynamic.block.LavaTexture;
import net.minecraft.client.render.texture.dynamic.block.PotionJuiceTexture;
import net.minecraft.client.render.texture.dynamic.block.WaterTexture;
import net.minecraft.client.render.texture.dynamic.item.ClockTexture;
import net.minecraft.client.render.texture.dynamic.item.CompassTexture;
import net.minecraft.client.render.texture.dynamic.item.RecoveryCompassTexture;
import net.minecraft.game.texture.Icon;

public class StitchedTexture implements Icon {
    protected List<Texture> textures;
    private List<int[]> animData;
    private final String textureName;
    private String sourcePath;
    protected Texture textureSheet;
    protected int x;
    protected int y;
    public int width;
    public int height;
    protected int frames = 0;
    protected int ticks = 0;
    private float minU;
    private float maxU;
    private float minV;
    private float maxV;
    protected boolean rotated;
    public boolean hasAnimation = false;

    public static StitchedTexture makeStitchedTex(String name, boolean dynamic) {
        switch (name) {
            case "water":
                return dynamic ? new WaterTexture() : new StitchedTexture(name);
            case "water_flow":
                return dynamic ? new FlowingWaterTexture() : new StitchedTexture(name);
            case "lava":
                return dynamic ? new LavaTexture() : new StitchedTexture(name);
            case "lava_flow":
                return dynamic ? new FlowingLavaTexture() : new StitchedTexture(name);
            case "compass":
                return new CompassTexture();
            case "compassAmethyst":
                return new RecoveryCompassTexture();
            case "clock":
                return new ClockTexture();
            case "potionjuice":
                return new PotionJuiceTexture();
            default:
                return new StitchedTexture(name);
        }
    }

    protected StitchedTexture(String name) {
        this.textureName = name;
    }

    public void init(Texture atlas, List<Texture> textures, int x, int y, int width, int height, boolean rotated) {
        this.textureSheet = atlas;
        this.textures = textures;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rotated = rotated;
        float offsetX = 0.01F / atlas.getWidth();
        float offsetY = 0.01F / atlas.getHeight();
        this.minU = (float)x / atlas.getWidth() + offsetX;
        this.maxU = (float)(x + width) / atlas.getWidth() - offsetX;
        this.minV = (float)y / atlas.getHeight() + offsetY;
        this.maxV = (float)(y + height) / atlas.getHeight() - offsetY;
    }

    public void copyFrom(StitchedTexture stitchedTex) {
        this.init(stitchedTex.textureSheet, stitchedTex.textures, stitchedTex.x, stitchedTex.y, stitchedTex.width, stitchedTex.height, stitchedTex.rotated);
    }

    @Override
    public int getIconWidth() {
        return this.width;
    }

    @Override
    public int getIconHeight() {
        return this.height;
    }

    @Override
    public int getOriginX() {
        return this.x;
    }

    @Override
    public int getOriginY() {
        return this.y;
    }

    @Override
    public float getMinU() {
        return this.minU;
    }

    @Override
    public float getMaxU() {
        return this.maxU;
    }

    @Override
    public float getInterpolatedU(double offset) {
        float length = this.maxU - this.minU;
        return this.minU + length * ((float)offset / 16.0F);
    }

    @Override
    public float getMinV() {
        return this.minV;
    }

    @Override
    public float getMaxV() {
        return this.maxV;
    }

    @Override
    public float getInterpolatedV(double offset) {
        float length = this.maxV - this.minV;
        return this.minV + length * ((float)offset / 16.0F);
    }

    @Override
    public String getIconName() {
        return this.textureName;
    }

    @Override
    public int getSheetWidth() {
        return this.textureSheet.getWidth();
    }

    @Override
    public int getSheetHeight() {
        return this.textureSheet.getHeight();
    }

    public void updateAnimation() {
        if (this.animData != null) {
            int[] pair = this.animData.get(this.frames);
            this.ticks++;
            if (this.ticks >= pair[1]) {
                int frame = pair[0];
                this.frames = (this.frames + 1) % this.animData.size();
                this.ticks = 0;
                pair = this.animData.get(this.frames);
                int frame2 = pair[0];
                if (frame != frame2 && frame2 >= 0 && frame2 < this.textures.size()) {
                    this.textureSheet.bindImage(this.x, this.y, this.textures.get(frame2));
                }
            }
        } else {
            int frames = this.frames;
            this.frames = (this.frames + 1) % this.textures.size();
            if (frames != this.frames) {
                this.textureSheet.bindImage(this.x, this.y, this.textures.get(this.frames));
            }
        }
    }

    public void readAnimationInfo(BufferedReader reader) {
        ArrayList<int[]> pairs = new ArrayList<>();

        try {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                line = line.trim();
                if (line.length() > 0) {
                    for (String strValue : line.split(",")) {
                        int offset = strValue.indexOf(42);
                        if (offset > 0) {
                            int first = Integer.parseInt(strValue.substring(0, offset));
                            int second = Integer.parseInt(strValue.substring(offset + 1));
                            pairs.add(new int[]{first, second});
                        } else {
                            pairs.add(new int[]{Integer.parseInt(strValue), 1});
                        }
                    }
                }
            }
        } catch (Exception e) {
            Minecraft.logger.warning("Failed to read animation info for " + this.textureName + ": " + e.getMessage());
        }

        if (!pairs.isEmpty() && pairs.size() < 600) {
            this.animData = pairs;
        }
    }

    public void setSourcePath(String name) {
        this.sourcePath = name;
    }

    public String getSourcePath() {
        return this.sourcePath;
    }
}
