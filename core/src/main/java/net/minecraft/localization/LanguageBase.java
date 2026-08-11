package net.minecraft.localization;

import java.io.IOException;
import java.io.InputStream;

public interface LanguageBase {
    void deletePack();

    InputStream stream(String var1, boolean var2) throws IOException;

    InputStream getResourceAsStream(String var1) throws IOException;

    String getPackID();

    String getFileName();

    String getLanguageName();

    String getPackAuthor();

    boolean getIOStream(String var1, boolean var2);
}
