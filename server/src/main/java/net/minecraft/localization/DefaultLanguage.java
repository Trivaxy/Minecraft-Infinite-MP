package net.minecraft.localization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class DefaultLanguage extends Language {
    public static final LanguageBase DEFAULT = new DefaultLanguage("default", null, "English", null);

    public DefaultLanguage(String name, File file, String filename, LanguageBase pack) {
        super(name, file, filename, pack);
    }

    @Override
    protected void loadDescription() {
        this.languageName = "English (US)";
        this.packAuthor = "Mojang, Legacy+ Team";
    }

    @Override
    public boolean doesPackExist(String filename) {
        return DefaultLanguage.class.getResourceAsStream(filename) != null;
    }

    @Override
    protected InputStream checkForPacks(String filename) throws IOException {
        InputStream ioStream = DefaultLanguage.class.getResourceAsStream(filename);
        if (ioStream == null) {
            throw new FileNotFoundException(filename);
        } else {
            return ioStream;
        }
    }

    @Override
    public void deletePack() {
    }
}
