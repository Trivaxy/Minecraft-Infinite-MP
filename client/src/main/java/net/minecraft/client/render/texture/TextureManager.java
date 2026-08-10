package net.minecraft.client.render.texture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texture.pack.PackBase;
import net.minecraft.client.render.texture.stitcher.Stitcher;
import org.lwjgl.opengl.GL11;

public class TextureManager {
    private static TextureManager instance;
    private final HashMap<Integer, Texture> texturesMap = new HashMap<>();
    private final HashMap<String, Integer> idMap = new HashMap<>();
    private int nextId = 0;

    public static int getMaxSize() {
        for (int upperLimit = 16384; upperLimit > 0; upperLimit >>= 1) {
            GL11.glTexImage2D(32868, 0, 6408, upperLimit, upperLimit, 0, 6408, 5121, (ByteBuffer)null);
            int id = GL11.glGetTexLevelParameteri(32868, 0, 4096);
            if (id != 0) {
                return upperLimit;
            }
        }

        return -1;
    }

    public static void init() {
        instance = new TextureManager();
    }

    public static TextureManager instance() {
        return instance;
    }

    public int getNextId() {
        return this.nextId++;
    }

    public void registerTexture(String name, Texture tex) {
        this.idMap.put(name, tex.getId());
        if (!this.texturesMap.containsKey(tex.getId())) {
            this.texturesMap.put(tex.getId(), tex);
        }
    }

    public void registerTexture(Texture tex) {
        if (this.texturesMap.containsValue(tex)) {
            Minecraft.logger.warning("TextureManager.registerTexture called, but this texture has already been registered. ignoring.");
        } else {
            this.texturesMap.put(tex.getId(), tex);
        }
    }

    public Stitcher createStitcher(String name) {
        int size = getMaxSize();
        return new Stitcher(name, size, size, true, 0);
    }

    public List<Texture> createTexture(String name) {
        ArrayList<Texture> textures = new ArrayList<>();
        PackBase pack = Minecraft.getMinecraft().texturePackList.selectedPack;

        try {
            BufferedImage img = ImageIO.read(pack.getResourceAsStream("/" + name));
            int height = img.getHeight();
            int width = img.getWidth();
            String baseName = this.getBasename(name);
            if (this.hasAnimation(name, pack)) {
                int size = height / width;

                for (int id = 0; id < size; id++) {
                    Texture tex = this.makeTexture(baseName, 2, width, width, 10496, 6408, 9728, 9728, false, img.getSubimage(0, width * id, width, width));
                    textures.add(tex);
                }
            } else if (width == height) {
                textures.add(this.makeTexture(baseName, 2, width, height, 10496, 6408, 9728, 9728, false, img));
            } else {
                Minecraft.logger.warning("TextureManager.createTexture: Skipping " + name + " because of broken aspect ratio and not animation");
            }

            return textures;
        } catch (FileNotFoundException var18) {
            Minecraft.logger.warning("TextureManager.createTexture called for file " + name + ", but that file does not exist. Ignoring.");
        } catch (IOException var19) {
            Minecraft.logger.warning("TextureManager.createTexture encountered an IOException when trying to read file " + name + ". Ignoring.");
        }

        return textures;
    }

    private String getBasename(String name) {
        File file = new File(name);
        return file.getName().substring(0, file.getName().lastIndexOf(46));
    }

    private boolean hasAnimation(String name, PackBase pack) {
        String fullPath = "/" + name.substring(0, name.lastIndexOf(46)) + ".txt";
        boolean exist = pack.getIOStream("/" + name, false);
        return Minecraft.getMinecraft().texturePackList.selectedPack.getIOStream(fullPath, !exist);
    }

    public Texture makeTexture(String name, int type, int width, int height, int wrap, int format, int min, int max, boolean par9, BufferedImage img) {
        Texture tex = new Texture(name, type, width, height, 1, wrap, format, min, max, img);
        this.registerTexture(tex);
        return tex;
    }

    public Texture createEmptyTexture(String name, int width, int height, int depth, int form) {
        return this.makeTexture(name, width, height, depth, 10496, form, 9728, 9728, false, null);
    }
}
