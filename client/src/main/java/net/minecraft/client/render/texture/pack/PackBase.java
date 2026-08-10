package net.minecraft.client.render.texture.pack;

import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.render.texture.TextureLoader;

public interface PackBase {
    void deletePack(TextureLoader var1);

    void bindThumbnail(TextureLoader var1);

    InputStream stream(String var1, boolean var2) throws IOException;

    InputStream getResourceAsStream(String var1) throws IOException;

    String getID();

    String getFileName();

    String firstDescLine();

    String secondDescLine();

    boolean getIOStream(String var1, boolean var2);

    boolean isCompatible();
}
