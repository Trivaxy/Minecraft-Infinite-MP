package net.minecraft.client.render.texture;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.options.OptionsList;
import net.minecraft.client.render.Renderer;
import net.minecraft.client.render.texture.pack.PackBase;
import net.minecraft.game.texture.Icon;
import org.lwjgl.opengl.GL11;

public class TextureLoader {
    public HashMap<String, Integer> textureMap = new HashMap<>();
    private HashMap<Integer, BufferedImage> textureContentsMap = new HashMap<>();
    private Map<String, TextureLoader.ImageData> dataMap = new HashMap<>();
    private IntBuffer singleIntBuffer = Renderer.createIntBuffer(1);
    private ByteBuffer imageData = Renderer.createByteBuffer(4194304);
    private BufferedImage missingTextureImage = new BufferedImage(64, 64, 2);
    private final TextureMap blocksAtlas;
    private final TextureMap itemsAtlas;
    public PackBase texturePack = Minecraft.getMinecraft().texturePackList.selectedPack;
    private boolean clampTexture = false;

    public TextureLoader(OptionsList options) {
        Graphics graphic = this.missingTextureImage.getGraphics();
        graphic.setColor(Color.MAGENTA);
        graphic.fillRect(0, 0, 32, 32);
        graphic.setColor(Color.BLACK);
        graphic.fillRect(32, 0, 64, 32);
        graphic.setColor(Color.BLACK);
        graphic.fillRect(0, 32, 32, 64);
        graphic.setColor(Color.MAGENTA);
        graphic.fillRect(32, 32, 64, 64);
        graphic.dispose();
        this.blocksAtlas = new TextureMap(0, "terrain", "blocks/", this.missingTextureImage);
        this.itemsAtlas = new TextureMap(1, "items", "items/", this.missingTextureImage);
    }

    public final int getTexture(String atlasPath) {
        if (atlasPath.equals("/terrain.png")) {
            this.blocksAtlas.getTexture().bindTexture(0);
            return this.blocksAtlas.getTexture().getGlId();
        }

        if (atlasPath.equals("/gui/items.png")) {
            this.itemsAtlas.getTexture().bindTexture(0);
            return this.itemsAtlas.getTexture().getGlId();
        }

        if (this.textureMap.get(atlasPath) != null) {
            return this.textureMap.get(atlasPath);
        }

        try {
            ((Buffer)this.singleIntBuffer).clear();
            Renderer.generateDisplayLists(this.singleIntBuffer);
            int index = this.singleIntBuffer.get(0);
            if (atlasPath.startsWith("##")) {
                this.setupTexture(this.unwrapImage(ImageIO.read(this.texturePack.getResourceAsStream(atlasPath.substring(2)))), index);
            } else if (atlasPath.startsWith("%%")) {
                this.clampTexture = true;
                this.setupTexture(ImageIO.read(this.texturePack.getResourceAsStream(atlasPath.substring(2))), index);
                this.clampTexture = false;
            } else if (atlasPath.startsWith("&&")) {
                try {
                    this.setupTexture(ImageIO.read(new File(Minecraft.getMinecraftDir(), atlasPath.substring(2))), index);
                } catch (Exception ex) {
                    this.setupTexture(ImageIO.read(this.texturePack.getResourceAsStream("/gui/unknown_pack.png")), index);
                }
            } else {
                try {
                    this.setupTexture(ImageIO.read(this.texturePack.getResourceAsStream(atlasPath)), index);
                } catch (FileNotFoundException ex) {
                    InputStream stream = TextureLoader.class.getResourceAsStream(atlasPath);
                    if (stream != null) {
                        this.setupTexture(ImageIO.read(stream), index);
                    } else {
                        this.setupTexture(this.missingTextureImage, index);
                    }
                }
            }

            this.textureMap.put(atlasPath, index);
            return index;
        } catch (Exception e) {
            e.printStackTrace();
            int tex = this.allocateTexture(this.missingTextureImage);
            this.textureMap.put(atlasPath, tex);
            return tex;
        }
    }

    private BufferedImage unwrapImage(BufferedImage img) {
        int pixels = img.getWidth() / 16;
        BufferedImage newImg = new BufferedImage(16, img.getHeight() * pixels, 2);
        Graphics graphic = newImg.getGraphics();

        for (int pixel = 0; pixel < pixels; pixel++) {
            graphic.drawImage(img, -pixel * 16, pixel * img.getHeight(), (ImageObserver)null);
        }

        graphic.dispose();
        return newImg;
    }

    public int allocateTexture(BufferedImage img) {
        ((Buffer)this.singleIntBuffer).clear();
        Renderer.generateDisplayLists(this.singleIntBuffer);
        int index = this.singleIntBuffer.get(0);
        this.setupTexture(img, index);
        this.textureContentsMap.put(index, img);
        return index;
    }

    public void setupTexture(BufferedImage img, int id) {
        GL11.glBindTexture(3553, id);
        GL11.glTexParameteri(3553, 10241, 9728);
        GL11.glTexParameteri(3553, 10240, 9728);
        if (this.clampTexture) {
            GL11.glTexParameteri(3553, 10242, 10496);
            GL11.glTexParameteri(3553, 10243, 10496);
        } else {
            GL11.glTexParameteri(3553, 10242, 10497);
            GL11.glTexParameteri(3553, 10243, 10497);
        }

        int width = img.getWidth();
        int height = img.getHeight();
        int[] pixels = new int[width * height];
        byte[] colors = new byte[width * height * 4];
        img.getRGB(0, 0, width, height, pixels, 0, width);

        for (int channel = 0; channel < pixels.length; channel++) {
            int alpha = pixels[channel] >> 24 & 0xFF;
            int blue = pixels[channel] >> 16 & 0xFF;
            int green = pixels[channel] >> 8 & 0xFF;
            int red = pixels[channel] & 0xFF;
            colors[channel * 4 + 0] = (byte)blue;
            colors[channel * 4 + 1] = (byte)green;
            colors[channel * 4 + 2] = (byte)red;
            colors[channel * 4 + 3] = (byte)alpha;
        }

        ((Buffer)this.imageData).clear();
        this.imageData.put(colors);
        ((Buffer)this.imageData).position(0).limit(colors.length);
        GL11.glTexImage2D(3553, 0, 6408, width, height, 0, 6408, 5121, this.imageData);
    }

    public void createTexture(byte[] data, int width, int height, int id) {
        GL11.glBindTexture(3553, id);
        if (this.clampTexture) {
            GL11.glTexParameteri(3553, 10242, 10496);
            GL11.glTexParameteri(3553, 10243, 10496);
        } else {
            GL11.glTexParameteri(3553, 10242, 10497);
            GL11.glTexParameteri(3553, 10243, 10497);
        }

        ((Buffer)this.imageData).clear();
        this.imageData.put(data);
        ((Buffer)this.imageData).position(0).limit(data.length);
        GL11.glTexSubImage2D(3553, 0, 0, 0, width, height, 6408, 5121, this.imageData);
    }

    public void createColoredTexture(int[] data, int width, int height, int id) {
        GL11.glBindTexture(3553, id);
        GL11.glTexParameteri(3553, 10241, 9728);
        GL11.glTexParameteri(3553, 10240, 9728);
        if (this.clampTexture) {
            GL11.glTexParameteri(3553, 10242, 10496);
            GL11.glTexParameteri(3553, 10243, 10496);
        } else {
            GL11.glTexParameteri(3553, 10242, 10497);
            GL11.glTexParameteri(3553, 10243, 10497);
        }

        byte[] colors = new byte[width * height * 4];

        for (int channel = 0; channel < data.length; channel++) {
            int alpha = data[channel] >> 24 & 0xFF;
            int blue = data[channel] >> 16 & 0xFF;
            int green = data[channel] >> 8 & 0xFF;
            int red = data[channel] & 0xFF;
            colors[channel * 4 + 0] = (byte)blue;
            colors[channel * 4 + 1] = (byte)green;
            colors[channel * 4 + 2] = (byte)red;
            colors[channel * 4 + 3] = (byte)alpha;
        }

        ((Buffer)this.imageData).clear();
        this.imageData.put(colors);
        ((Buffer)this.imageData).position(0).limit(colors.length);
        GL11.glTexSubImage2D(3553, 0, 0, 0, width, height, 6408, 5121, this.imageData);
    }

    public void deleteTexture(int id) {
        this.textureContentsMap.remove(id);
        ((Buffer)this.singleIntBuffer).clear();
        this.singleIntBuffer.put(id);
        ((Buffer)this.singleIntBuffer).flip();
        GL11.glDeleteTextures(this.singleIntBuffer);
    }

    public int downloadTexture(String url, String name) {
        TextureLoader.ImageData data = this.dataMap.get(url);
        if (data != null && data.image != null && !data.complete) {
            if (data.name < 0) {
                data.name = this.allocateTexture(data.image);
            } else {
                this.setupTexture(data.image, data.name);
            }

            data.complete = true;
        }

        return data != null && data.name >= 0 ? data.name : this.getTexture(name);
    }

    public TextureLoader.ImageData obtainData(String name, TextureLoader.ImageDownloader downloader, boolean isSkin) {
        TextureLoader.ImageData data = this.dataMap.get(name);
        if (data == null) {
            this.dataMap.put(name, new TextureLoader.ImageData(name, downloader, isSkin));
        } else {
            data.referenceCount++;
        }

        return data;
    }

    public void releaseData(String name) {
        TextureLoader.ImageData data = this.dataMap.get(name);
        if (data != null) {
            data.referenceCount--;
            if (data.referenceCount == 0) {
                if (data.name >= 0) {
                    this.deleteTexture(data.name);
                }

                this.dataMap.remove(name);
            }
        }
    }

    public void updateAnimatedTextures() {
        this.blocksAtlas.updateAnimations();
        this.itemsAtlas.updateAnimations();
    }

    public void refreshTextures() {
        for (int key : this.textureContentsMap.keySet()) {
            BufferedImage img = this.textureContentsMap.get(key);
            this.setupTexture(img, key);
        }

        for (TextureLoader.ImageData data : this.dataMap.values()) {
            data.complete = false;
        }

        for (String type : this.textureMap.keySet()) {
            try {
                BufferedImage img;
                if (type.startsWith("##")) {
                    img = this.unwrapImage(ImageIO.read(this.texturePack.getResourceAsStream(type.substring(2))));
                } else if (type.startsWith("%%")) {
                    this.clampTexture = true;
                    img = ImageIO.read(this.texturePack.getResourceAsStream(type.substring(2)));
                } else {
                    img = ImageIO.read(this.texturePack.getResourceAsStream(type));
                }

                int index = this.textureMap.get(type);
                this.setupTexture(img, index);
                this.clampTexture = false;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void bindTexture(int tex) {
        if (tex >= 0) {
            GL11.glBindTexture(3553, tex);
        }
    }

    public void bindTexture(String name) {
        this.bindTexture(this.getTexture(name));
    }

    public void refreshTextureMaps() {
        this.blocksAtlas.refreshTextures();
        this.itemsAtlas.refreshTextures();
    }

    public Icon getMissingIcon(int atlasId) {
        switch (atlasId) {
            case 0:
                return this.blocksAtlas.getMissingIcon();
            case 1:
            default:
                return this.itemsAtlas.getMissingIcon();
        }
    }

    public class ImageData {
        public BufferedImage image;
        public int referenceCount = 1;
        public int name = -1;
        public boolean complete = false;

        public ImageData(final String url, final TextureLoader.ImageDownloader buffer, final boolean isSkin) {
            (new Thread() {
                {
                    this.setDaemon(true);
                }

                @Override
                public void run() {
                    HttpURLConnection connection = null;

                    try {
                        URL newUrl = new URL(url);
                        connection = (HttpURLConnection)newUrl.openConnection();
                        connection.setConnectTimeout(6000);
                        connection.setDoInput(true);
                        connection.setDoOutput(false);
                        connection.connect();
                        if (connection.getResponseCode() != 404) {
                            if (buffer != null && isSkin) {
                                ImageData.this.image = buffer.parseUserSkin(ImageIO.read(connection.getInputStream()));
                                return;
                            }

                            ImageData.this.image = ImageIO.read(connection.getInputStream());
                            return;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return;
                    } finally {
                        connection.disconnect();
                    }
                }
            }).start();
        }
    }

    public static class ImageDownloader {
        private int[] imageData;
        private int width;
        private int height;

        public BufferedImage parseUserSkin(BufferedImage image) {
            if (image == null) {
                return null;
            }

            this.width = 64;
            this.height = 64;
            BufferedImage buffer = new BufferedImage(this.width, this.height, 2);
            Graphics graphic = buffer.getGraphics();
            graphic.drawImage(image, 0, 0, (ImageObserver)null);
            if (image.getHeight() == 32) {
                graphic.drawImage(buffer, 24, 48, 20, 52, 4, 16, 8, 20, null);
                graphic.drawImage(buffer, 28, 48, 24, 52, 8, 16, 12, 20, null);
                graphic.drawImage(buffer, 20, 52, 16, 64, 8, 20, 12, 32, null);
                graphic.drawImage(buffer, 24, 52, 20, 64, 4, 20, 8, 32, null);
                graphic.drawImage(buffer, 28, 52, 24, 64, 0, 20, 4, 32, null);
                graphic.drawImage(buffer, 32, 52, 28, 64, 12, 20, 16, 32, null);
                graphic.drawImage(buffer, 40, 48, 36, 52, 44, 16, 48, 20, null);
                graphic.drawImage(buffer, 44, 48, 40, 52, 48, 16, 52, 20, null);
                graphic.drawImage(buffer, 36, 52, 32, 64, 48, 20, 52, 32, null);
                graphic.drawImage(buffer, 40, 52, 36, 64, 44, 20, 48, 32, null);
                graphic.drawImage(buffer, 44, 52, 40, 64, 40, 20, 44, 32, null);
                graphic.drawImage(buffer, 48, 52, 44, 64, 52, 20, 56, 32, null);
            }

            graphic.dispose();
            this.imageData = ((DataBufferInt)buffer.getRaster().getDataBuffer()).getData();
            this.setAreaOpaque(0, 0, 32, 16);
            this.setAreaTransparent(32, 0, 64, 32);
            this.setAreaOpaque(0, 16, 64, 32);
            this.setAreaTransparent(0, 32, 16, 48);
            this.setAreaTransparent(16, 32, 40, 48);
            this.setAreaTransparent(40, 32, 56, 48);
            this.setAreaTransparent(0, 48, 16, 64);
            this.setAreaOpaque(16, 48, 48, 64);
            this.setAreaTransparent(48, 48, 64, 64);
            return buffer;
        }

        private void setAreaTransparent(int minX, int minY, int maxX, int maxY) {
            if (!this.hasTransparency(minX, minY, maxX, maxY)) {
                for (int x = minX; x < maxX; x++) {
                    for (int y = minY; y < maxY; y++) {
                        this.imageData[x + y * this.width] = this.imageData[x + y * this.width] & 16777215;
                    }
                }
            }
        }

        private void setAreaOpaque(int minX, int minY, int maxX, int maxY) {
            for (int x = minX; x < maxX; x++) {
                for (int y = minY; y < maxY; y++) {
                    this.imageData[x + y * this.width] = this.imageData[x + y * this.width] | 0xFF000000;
                }
            }
        }

        private boolean hasTransparency(int minX, int minY, int maxX, int maxY) {
            for (int x = minX; x < maxX; x++) {
                for (int y = minY; y < maxY; y++) {
                    int alpha = this.imageData[x + y * this.width];
                    if ((alpha >> 24 & 0xFF) < 128) {
                        return true;
                    }
                }
            }

            return false;
        }
    }
}
