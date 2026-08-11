package net.minecraft.localization;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class LanguageFolder extends Language {
    public LanguageFolder(String name, File file, LanguageBase pack) {
        super(name, file, file.getName(), pack);
    }

    @Override
    protected InputStream checkForPacks(String name) throws IOException {
        File file = new File(this.packFile, name.substring(1));
        if (!file.exists()) {
            throw new FileNotFoundException(name);
        } else {
            return new BufferedInputStream(new FileInputStream(file));
        }
    }

    @Override
    public boolean doesPackExist(String filename) {
        File file = new File(this.packFile, filename);
        return file.exists() && file.isFile();
    }

    @Override
    public void deletePack() {
    }
}
