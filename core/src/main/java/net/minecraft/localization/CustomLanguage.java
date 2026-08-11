package net.minecraft.localization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class CustomLanguage extends Language {
    private ZipFile packZip;

    public CustomLanguage(String name, File file, LanguageBase pack) {
        super(name, file, file.getName(), pack);
    }

    @Override
    public void deletePack() {
        try {
            if (this.packZip != null) {
                this.packZip.close();
            }
        } catch (IOException var2) {
        }

        this.packZip = null;
    }

    @Override
    protected InputStream checkForPacks(String filename) throws IOException {
        this.openLanguagePackFile();
        ZipEntry zip = this.packZip.getEntry(filename.substring(1));
        if (zip == null) {
            throw new FileNotFoundException(filename);
        } else {
            return this.packZip.getInputStream(zip);
        }
    }

    @Override
    public boolean doesPackExist(String filename) {
        try {
            this.openLanguagePackFile();
            return this.packZip.getEntry(filename.substring(1)) != null;
        } catch (Exception var3) {
            return false;
        }
    }

    private void openLanguagePackFile() throws IOException, ZipException {
        if (this.packZip == null) {
            this.packZip = new ZipFile(this.packFile);
        }
    }
}
