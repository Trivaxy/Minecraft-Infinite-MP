package net.minecraft.client.render.texture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.options.OptionValues;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;

public class Texture {
    private final String name;
    private String sourcePath;
    private Texture.Rectangle rect;
    private ByteBuffer data;
    private int glId;
    private int id;
    private final int width;
    private final int height;
    private final int depth;
    private final int format;
    private final int target;
    private boolean autoCreate;
    private boolean notModified;

    private Texture(String name, int type, int width, int height, int depth, int wrap, int format, int minFlt, int magFlt) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.format = format;
        this.rect = new Texture.Rectangle(0, 0, width, height);
        if (height == 1 && depth == 1) {
            this.target = 3552;
        } else if (depth == 1) {
            this.target = 3553;
        } else {
            this.target = 32879;
        }

        if (OptionValues.mipmapping > 0) {
            GL11.glTexParameteri(3553, 33085, OptionValues.mipmapping);
            GL11.glTexParameteri(3553, 10241, 9986);
            GL11.glTexParameteri(3553, 10240, 9728);
            GL11.glTexParameteri(3553, 33169, 1);
            GL30.glGenerateMipmap(3553);
        }

        if (type != 2) {
            this.glId = GL11.glGenTextures();
            GL11.glBindTexture(this.target, this.glId);
            GL11.glTexParameteri(this.target, 10241, minFlt);
            GL11.glTexParameteri(this.target, 10240, magFlt);
            GL11.glTexParameteri(this.target, 10242, wrap);
            GL11.glTexParameteri(this.target, 10243, wrap);
        } else {
            this.glId = -1;
        }

        this.id = TextureManager.instance().getNextId();
    }

    public Texture(String name, int type, int width, int height, int depth, int wrap, int format, int minFlt, int magFlt, BufferedImage image) {
        this(name, type, width, height, depth, wrap, format, minFlt, magFlt);
        if (image == null) {
            if (width != -1 && height != -1) {
                byte[] data = new byte[width * height * depth * 4];

                for (int var12 = 0; var12 < data.length; var12++) {
                    data[var12] = 0;
                }

                this.data = ByteBuffer.allocateDirect(data.length);
                ((Buffer)this.data).clear();
                this.data.put(data);
                ((Buffer)this.data).position(0).limit(data.length);
                if (this.autoCreate) {
                    this.uploadTexture();
                } else {
                    this.notModified = false;
                }
            }
        } else {
            this.transferFromImage(image);
            if (type != 2) {
                this.uploadTexture();
                this.autoCreate = false;
            }
        }
    }

    public final Texture.Rectangle getRect() {
        return this.rect;
    }

    public void fillRect(Texture.Rectangle rect, int i) {
        if (this.target != 32879) {
            Texture.Rectangle newRect = new Texture.Rectangle(0, 0, this.width, this.height);
            newRect.intersect(rect);
            ((Buffer)this.data).position(0);

            for (int height = newRect.getY(); height < newRect.getY() + newRect.getHeight(); height++) {
                int offset = height * this.width * 4;

                for (int width = newRect.getX(); width < newRect.getX() + newRect.getWidth(); width++) {
                    this.data.put(offset + width * 4 + 0, (byte)(i >> 24 & 0xFF));
                    this.data.put(offset + width * 4 + 1, (byte)(i >> 16 & 0xFF));
                    this.data.put(offset + width * 4 + 2, (byte)(i >> 8 & 0xFF));
                    this.data.put(offset + width * 4 + 3, (byte)(i >> 0 & 0xFF));
                }
            }

            if (this.autoCreate) {
                this.uploadTexture();
            } else {
                this.notModified = false;
            }
        }
    }

    public void writeImage(String filename) {
        Minecraft.logger.log(Level.INFO, "Saving atlas image to .minecraft/" + filename);
        BufferedImage image = new BufferedImage(this.width, this.height, 2);
        ByteBuffer buffer = this.getData();
        byte[] data = new byte[this.width * this.height * 4];
        ((Buffer)buffer).position(0);
        buffer.get(data);

        for (int x = 0; x < this.width; x++) {
            for (int y = 0; y < this.height; y++) {
                int offset = y * this.width * 4 + x * 4;
                byte col = 0;
                int rgb = col | (data[offset + 2] & 255) << 0;
                rgb |= (data[offset + 1] & 255) << 8;
                rgb |= (data[offset + 0] & 255) << 16;
                rgb |= (data[offset + 3] & 255) << 24;
                image.setRGB(x, y, rgb);
            }
        }

        ((Buffer)this.data).position(this.width * this.height * 4);

        try {
            ImageIO.write(image, "png", new File(Minecraft.getMinecraftDir(), filename));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void copyFrom(int x, int y, Texture texture, boolean rotated) {
        if (this.target != 32879) {
            ByteBuffer pixels = texture.getData();
            ((Buffer)this.data).position(0);
            ((Buffer)pixels).position(0);

            for (int height = 0; height < texture.getHeight(); height++) {
                int heightOffset = y + height;
                int size = height * texture.getWidth() * 4;
                int sizeOffset = heightOffset * this.width * 4;
                if (rotated) {
                    heightOffset = y + (texture.getHeight() - height);
                }

                for (int width = 0; width < texture.getWidth(); width++) {
                    int offset = sizeOffset + (width + x) * 4;
                    int pixel = size + width * 4;
                    if (rotated) {
                        offset = x + width * this.width * 4 + heightOffset * 4;
                    }

                    this.data.put(offset + 0, pixels.get(pixel + 0));
                    this.data.put(offset + 1, pixels.get(pixel + 1));
                    this.data.put(offset + 2, pixels.get(pixel + 2));
                    this.data.put(offset + 3, pixels.get(pixel + 3));
                }
            }

            ((Buffer)this.data).position(this.width * this.height * 4);
            if (this.autoCreate) {
                this.uploadTexture();
            } else {
                this.notModified = false;
            }
        }
    }

    public void bindImage(int xOffs, int yOffs, Texture tex) {
        GL11.glBindTexture(this.target, this.glId);
        GL11.glTexSubImage2D(this.target, 0, xOffs, yOffs, tex.getWidth(), tex.getHeight(), this.format, 5121, (ByteBuffer)((Buffer)tex.getData()).position(0));
        this.notModified = true;
    }

    public void bindImage(int xOffs, int yOffs, int width, int height, ByteBuffer textureData) {
        GL11.glBindTexture(this.target, this.glId);
        GL11.glTexSubImage2D(this.target, 0, xOffs, yOffs, width, height, this.format, 5121, textureData);
        this.notModified = true;
    }

    public void transferFromImage(BufferedImage image) {
        if (this.target != 32879) {
            int width = image.getWidth();
            int height = image.getHeight();
            if (width <= this.width && height <= this.height) {
                int[] argb = new int[]{3, 0, 1, 2};
                int[] bgra = new int[]{3, 2, 1, 0};
                int[] texFmt = this.format == 32993 ? bgra : argb;
                int[] rgb = new int[this.width * this.height];
                image.getRGB(0, 0, this.width, this.height, rgb, 0, width);
                byte[] data = new byte[this.width * this.height * 4];

                for (int x = 0; x < this.height; x++) {
                    for (int y = 0; y < this.width; y++) {
                        int xo = x * this.width + y;
                        int xxo = xo * 4;
                        data[xxo + texFmt[0]] = (byte)(rgb[xo] >> 24 & 0xFF);
                        data[xxo + texFmt[1]] = (byte)(rgb[xo] >> 16 & 0xFF);
                        data[xxo + texFmt[2]] = (byte)(rgb[xo] >> 8 & 0xFF);
                        data[xxo + texFmt[3]] = (byte)(rgb[xo] >> 0 & 0xFF);
                    }
                }

                this.data = ByteBuffer.allocateDirect(data.length);
                ((Buffer)this.data).clear();
                this.data.put(data);
                ((Buffer)this.data).limit(data.length);
                if (this.autoCreate) {
                    this.uploadTexture();
                } else {
                    this.notModified = false;
                }
            } else {
                Minecraft.logger
                    .warning(
                        "transferFromImage called with a BufferedImage with dimensions ("
                            + width
                            + ", "
                            + height
                            + ") larger than the Texture dimensions ("
                            + this.width
                            + ", "
                            + this.height
                            + "). Ignoring."
                    );
            }
        }
    }

    public void bindTexture(int par1) {
        if (this.depth == 1) {
            GL11.glEnable(3553);
        } else {
            GL11.glEnable(32879);
        }

        GL11.glBindTexture(this.target, this.glId);
        if (!this.notModified) {
            this.uploadTexture();
        }
    }

    public void uploadTexture() {
        ((Buffer)this.data).flip();
        if (this.height != 1 && this.depth != 1) {
            GL12.glTexImage3D(this.target, 0, this.format, this.width, this.height, this.depth, 0, this.format, 5121, this.data);
        } else if (this.height != 1) {
            GL11.glTexImage2D(this.target, 0, this.format, this.width, this.height, 0, this.format, 5121, this.data);
        } else {
            GL11.glTexImage1D(this.target, 0, this.format, this.width, 0, this.format, 5121, this.data);
        }

        this.notModified = true;
    }

    public int getId() {
        return this.id;
    }

    public int getGlId() {
        return this.glId;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public String getName() {
        return this.name;
    }

    public void setSourcePath(String name) {
        this.sourcePath = name;
    }

    public String getSourcePath() {
        return this.sourcePath;
    }

    public ByteBuffer getData() {
        return this.data;
    }

    public class Rectangle {
        private int x;
        private int y;
        private int width;
        private int height;

        public Rectangle(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.width = w;
            this.height = h;
        }

        public Texture.Rectangle intersect(Texture.Rectangle rect) {
            int startX = this.x;
            int startY = this.y;
            int endX = this.x + this.width;
            int endY = this.y + this.height;
            int newStartX = rect.getX();
            int newStartY = rect.getY();
            int newEndX = newStartX + rect.getWidth();
            int newEndY = newStartY + rect.getHeight();
            this.x = Math.max(startX, newStartX);
            this.y = Math.max(startY, newStartY);
            this.width = Math.max(0, Math.min(endX, newEndX) - this.x);
            this.height = Math.max(0, Math.min(endY, newEndY) - this.y);
            return this;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }
    }
}
