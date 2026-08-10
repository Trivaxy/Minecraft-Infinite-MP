package net.minecraft.client.render.texture.pack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import net.minecraft.client.render.texture.TextureLoader;

public class CustomPack extends TexturePack {
    private ZipFile packFile;

    public CustomPack(String name, File source, PackBase pack) {
        super(name, source, source.getName(), pack);
    }

    @Override
    public void deletePack(TextureLoader texLoader) {
        super.deletePack(texLoader);

        try {
            if (this.packFile != null) {
                this.packFile.close();
            }
        } catch (IOException var3) {
        }

        this.packFile = null;
    }

    @Override
    protected InputStream checkForPacks(String name) throws IOException {
        this.openTexturePackFile();
        ZipEntry zip = this.packFile.getEntry(name.substring(1));
        if (zip == null) {
            throw new FileNotFoundException(name);
        } else {
            return this.packFile.getInputStream(zip);
        }
    }

    @Override
    public boolean doesPackExist(String name1) {
        try {
            this.openTexturePackFile();
            return this.packFile.getEntry(name1.substring(1)) != null;
        } catch (Exception ex) {
            return false;
        }
    }

    private void openTexturePackFile() throws IOException, ZipException {
        if (this.packFile == null) {
            this.packFile = new ZipFile(this.theFile);
        }
    }

    @Override
    public boolean isCompatible() {
        try {
            this.openTexturePackFile();
            Enumeration<? extends ZipEntry> zipFile = this.packFile.entries();

            while (zipFile.hasMoreElements()) {
                ZipEntry file = zipFile.nextElement();
                if (file.getName().startsWith("blocks/") || file.getName().startsWith("items/")) {
                    return true;
                }
            }
        } catch (Exception var3) {
        }

        boolean hasTextures = this.doesPackExist("terrain.png") || this.doesPackExist("gui/items.png");
        return !hasTextures;
    }
}
