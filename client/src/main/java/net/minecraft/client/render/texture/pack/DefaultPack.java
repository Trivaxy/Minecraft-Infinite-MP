package net.minecraft.client.render.texture.pack;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class DefaultPack extends TexturePack {
    public DefaultPack() {
        super("default", null, "Default", null);
    }

    @Override
    protected void loadDescription() {
        this.firstDescLine = "The default look of Minecraft";
    }

    @Override
    public boolean doesPackExist(String name) {
        return DefaultPack.class.getResourceAsStream(name) != null;
    }

    @Override
    public boolean isCompatible() {
        return true;
    }

    @Override
    protected InputStream checkForPacks(String name) throws IOException {
        InputStream ioStream = DefaultPack.class.getResourceAsStream(name);
        if (ioStream == null) {
            throw new FileNotFoundException(name);
        } else {
            return ioStream;
        }
    }
}
