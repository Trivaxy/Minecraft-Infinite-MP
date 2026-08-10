package net.minecraft.client.render.texture;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import net.minecraft.client.Minecraft;
import net.minecraft.client.options.OptionValues;
import net.minecraft.client.render.blocks.BlocksRenderer;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.client.render.entity.misc.FrameRenderer;
import net.minecraft.client.render.texture.dynamic.DynamicTexture;
import net.minecraft.client.render.texture.pack.PackBase;
import net.minecraft.client.render.texture.stitcher.StitchHolder;
import net.minecraft.client.render.texture.stitcher.StitchSlot;
import net.minecraft.client.render.texture.stitcher.StitchedTexture;
import net.minecraft.client.render.texture.stitcher.Stitcher;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;

public class TextureMap implements IconRegister {
    private final HashMap<String, StitchedTexture> stitchedTextures = new HashMap<>();
    private final Map<String, StitchedTexture> stitchedMap = new HashMap<>();
    private final List<StitchedTexture> textureList = new ArrayList<>();
    private BufferedImage missingImage = new BufferedImage(64, 64, 2);
    private StitchedTexture missingTexture;
    private Texture atlasTexture;
    private final int type;
    private final String name;
    private final String basePath;
    private final String fileExtension;

    public TextureMap(int type, String name, String path, BufferedImage img) {
        this.type = type;
        this.name = name;
        this.basePath = path;
        this.fileExtension = ".png";
        this.missingImage = img;
    }

    public void refreshTextures() {
        this.stitchedMap.clear();
        if (this.type == 0) {
            for (Block block : BlockList.blocks) {
                if (block != null) {
                    block.registerIcons(this);
                }
            }

            BlocksRenderer.registerDestroyBlockIcons(this);
            BlockModel.registerIcons(this);
            FrameRenderer.backdrop = this.registerIcon("frame");
            FrameRenderer.backdropGlowing = this.registerIcon("glowingFrame");
        }

        for (Item item : ItemList.items) {
            if (item != null && item.getAtlas() == this.type) {
                item.registerIcons(this);
            }
        }

        HashMap<StitchHolder, List<Texture>> texMap = new HashMap<>();
        Stitcher stitcher = TextureManager.instance().createStitcher(this.name);
        this.stitchedTextures.clear();
        this.textureList.clear();
        Texture missingTexture = TextureManager.instance()
            .makeTexture("missingno", 2, this.missingImage.getWidth(), this.missingImage.getHeight(), 10496, 6408, 9728, 9728, false, this.missingImage);
        StitchHolder holder = new StitchHolder(missingTexture);
        stitcher.addStitchHolder(holder);
        texMap.put(holder, Arrays.asList(missingTexture));

        for (StitchedTexture tex : this.stitchedMap.values()) {
            String fullPath = this.basePath + tex.getSourcePath() + this.fileExtension;
            List<Texture> textures = TextureManager.instance().createTexture(fullPath);
            if (!textures.isEmpty()) {
                StitchHolder var9 = new StitchHolder(textures.get(0));
                stitcher.addStitchHolder(var9);
                texMap.put(var9, textures);
            }
        }

        try {
            stitcher.doStitch();
        } catch (RuntimeException ex) {
            throw ex;
        }

        this.atlasTexture = stitcher.getTexture();

        for (StitchSlot texSlot : stitcher.getStichSlots()) {
            StitchHolder newHolder = texSlot.getStitchHolder();
            Texture newTex = newHolder.getTexture();
            String texName = newTex.getName();
            List<Texture> texList = texMap.get(newHolder);
            StitchedTexture tex = this.stitchedMap.get(texName);
            boolean created = false;
            if (tex == null) {
                created = true;
                tex = StitchedTexture.makeStitchedTex(texName, false);
                if (!texName.equals("missingno")) {
                    Minecraft.logger.warning("Couldn't find premade icon for " + texName + " doing " + this.name);
                }
            }

            tex.init(
                this.atlasTexture,
                texList,
                texSlot.getOriginX(),
                texSlot.getOriginY(),
                newHolder.getTexture().getWidth(),
                newHolder.getTexture().getHeight(),
                newHolder.isRotated()
            );
            this.stitchedTextures.put(texName, tex);
            if (!created) {
                this.stitchedMap.remove(texName);
            }

            if (texList.size() > 1 && OptionValues.animatedTextures) {
                this.textureList.add(tex);
                String sourcePath = tex.getSourcePath();
                String fullPath = this.basePath + sourcePath + ".txt";
                PackBase pack = Minecraft.getMinecraft().texturePackList.selectedPack;
                boolean exists = !pack.getIOStream("/" + this.basePath + texName + ".png", false);

                try {
                    InputStream ioStream = pack.stream("/" + fullPath, exists);
                    Minecraft.logger.info("Found animation info for: " + fullPath);
                    tex.readAnimationInfo(new BufferedReader(new InputStreamReader(ioStream)));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else if (tex instanceof DynamicTexture) {
                Minecraft.logger.info("Adding dynamic texture " + tex);
                this.textureList.add(tex);
            }
        }

        this.missingTexture = this.stitchedTextures.get("missingno");

        for (StitchedTexture texture : this.stitchedMap.values()) {
            texture.copyFrom(this.missingTexture);
        }

        this.atlasTexture.writeImage("debug.stitched_" + this.name + ".png");
        this.atlasTexture.uploadTexture();
    }

    public void updateAnimations() {
        for (StitchedTexture texture : this.textureList) {
            texture.updateAnimation();
        }
    }

    public Texture getTexture() {
        return this.atlasTexture;
    }

    @Override
    public Icon registerIcon(String name) {
        if (name == null) {
            Minecraft.logger.log(Level.WARNING, "Attempted to register null icon", new RuntimeException());
            return null;
        }

        String[] pathNames = name.split("/");
        String id = pathNames[pathNames.length - 1];
        StitchedTexture texture = this.stitchedMap.get(id);
        if (texture == null) {
            boolean dynamic = false;
            String fullPath = this.basePath + name + ".txt";
            PackBase pack = Minecraft.getMinecraft().texturePackList.selectedPack;
            boolean found = !pack.getIOStream("/" + this.basePath + name + ".png", false);

            try {
                InputStream ioStream = pack.stream("/" + fullPath, found);
                BufferedReader reader = new BufferedReader(new InputStreamReader(ioStream));
                if (reader.ready() && reader.readLine().equals("DYNAMIC") && OptionValues.animatedTextures) {
                    Minecraft.logger.info("Making dynamic texture for: " + fullPath);
                    dynamic = true;
                }
            } catch (IOException var11) {
            }

            texture = StitchedTexture.makeStitchedTex(id, dynamic);
            texture.setSourcePath(name);
            texture.hasAnimation = dynamic;
            this.stitchedMap.put(id, texture);
        }

        return texture;
    }

    public Icon getMissingIcon() {
        return this.missingTexture;
    }
}
