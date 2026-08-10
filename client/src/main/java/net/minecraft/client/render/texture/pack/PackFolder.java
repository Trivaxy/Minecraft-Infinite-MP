package net.minecraft.client.render.texture.pack;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class PackFolder extends TexturePack {
    public PackFolder(String name, File source, PackBase pack) {
        super(name, source, source.getName(), pack);
    }

    @Override
    protected InputStream checkForPacks(String name) throws IOException {
        File file = new File(this.theFile, name.substring(1));
        if (!file.exists()) {
            throw new FileNotFoundException(name);
        } else {
            return new BufferedInputStream(new FileInputStream(file));
        }
    }

    @Override
    public boolean doesPackExist(String name) {
        File file = new File(this.theFile, name);
        return file.exists() && file.isFile();
    }

    @Override
    public boolean isCompatible() {
        boolean hasAtlasses = this.doesPackExist("terrain.png") || this.doesPackExist("gui/items.png");
        return !hasAtlasses;
    }
}
