package net.minecraft.client.render.texture.pack;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.imageio.ImageIO;
import net.minecraft.client.render.texture.TextureLoader;
import org.lwjgl.opengl.GL11;

public abstract class TexturePack implements PackBase {
    protected final File theFile;
    protected BufferedImage thumbnail;
    private final PackBase thePack;
    private final String id;
    private final String fileName;
    protected String firstDescLine;
    protected String secondDescLine;
    private int thumbnailName = -1;

    protected TexturePack(String id, File file, String fileName, PackBase pack) {
        this.id = id;
        this.fileName = fileName;
        this.theFile = file;
        this.thePack = pack;
        this.loadThumbnail();
        this.loadDescription();
    }

    private static String trimString(String name) {
        if (name != null && name.length() > 34) {
            name = name.substring(0, 34);
        }

        return name;
    }

    private void loadThumbnail() {
        InputStream io = null;

        try {
            io = this.stream("/pack.png", false);
            this.thumbnail = ImageIO.read(io);
        } catch (IOException ex) {
            System.out.println("Error loading pack's thumbnail image!");
        } finally {
            try {
                if (io != null) {
                    io.close();
                }
            } catch (IOException ex) {
                System.out.println("Error loading pack's thumbnail image!");
            }
        }
    }

    protected void loadDescription() {
        InputStream io = null;
        BufferedReader reader = null;

        try {
            io = this.checkForPacks("/pack.txt");
            reader = new BufferedReader(new InputStreamReader(io));
            this.firstDescLine = trimString(reader.readLine());
            this.secondDescLine = trimString(reader.readLine());
        } catch (IOException ex) {
            System.out.println("Error loading the pack's description!");
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }

                if (io != null) {
                    io.close();
                }
            } catch (IOException var11) {
                System.out.println("Error loading the pack's description!");
            }
        }
    }

    @Override
    public InputStream stream(String name, boolean exists) throws IOException {
        try {
            return this.checkForPacks(name);
        } catch (IOException ex) {
            if (this.thePack != null && exists) {
                return this.thePack.stream(name, true);
            } else {
                throw ex;
            }
        }
    }

    @Override
    public InputStream getResourceAsStream(String name) throws IOException {
        return this.stream(name, true);
    }

    protected abstract InputStream checkForPacks(String var1) throws IOException;

    @Override
    public void deletePack(TextureLoader texLoader) {
        if (this.thumbnail != null && this.thumbnailName != -1) {
            texLoader.deleteTexture(this.thumbnailName);
        }
    }

    @Override
    public void bindThumbnail(TextureLoader texLoader) {
        if (this.thumbnail != null) {
            if (this.thumbnailName == -1) {
                this.thumbnailName = texLoader.allocateTexture(this.thumbnail);
            }

            GL11.glBindTexture(3553, this.thumbnailName);
        } else {
            texLoader.bindTexture("/gui/unknown_pack.png");
        }
    }

    @Override
    public boolean getIOStream(String name, boolean loaded) {
        boolean exists = this.doesPackExist(name);
        return !exists && loaded && this.thePack != null ? this.thePack.getIOStream(name, loaded) : exists;
    }

    public abstract boolean doesPackExist(String var1);

    @Override
    public String getID() {
        return this.id;
    }

    @Override
    public String getFileName() {
        return this.fileName;
    }

    @Override
    public String firstDescLine() {
        return this.firstDescLine;
    }

    @Override
    public String secondDescLine() {
        return this.secondDescLine;
    }
}
