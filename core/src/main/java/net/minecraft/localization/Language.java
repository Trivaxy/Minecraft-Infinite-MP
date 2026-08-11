package net.minecraft.localization;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public abstract class Language implements LanguageBase {
    private final LanguageBase thePack;
    protected final File packFile;
    private final String packID;
    private final String fileName;
    protected String languageName;
    protected String packAuthor;

    protected Language(String name, File file, String filename, LanguageBase pack) {
        this.packID = name;
        this.fileName = filename;
        this.packFile = file;
        this.thePack = pack;
        this.loadDescription();
    }

    private static String trimString(String string) {
        if (string != null && string.length() > 34) {
            string = string.substring(0, 34);
        }

        return string;
    }

    protected void loadDescription() {
        InputStream io = null;
        BufferedReader reader = null;

        try {
            io = this.checkForPacks("/lang/info.lang");
            reader = new BufferedReader(new InputStreamReader(io, StandardCharsets.UTF_8));
            this.languageName = trimString(reader.readLine());
            this.packAuthor = trimString(reader.readLine());
        } catch (IOException var12) {
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }

                if (io != null) {
                    io.close();
                }
            } catch (IOException var11) {
            }
        }
    }

    @Override
    public InputStream stream(String filename, boolean perform) throws IOException {
        try {
            return this.checkForPacks(filename);
        } catch (IOException ex) {
            if (this.thePack != null && perform) {
                return this.thePack.stream(filename, true);
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
    public boolean getIOStream(String name, boolean check) {
        boolean found = this.doesPackExist(name);
        return !found && check && this.thePack != null ? this.thePack.getIOStream(name, check) : found;
    }

    public abstract boolean doesPackExist(String var1);

    @Override
    public String getPackID() {
        return this.packID;
    }

    @Override
    public String getFileName() {
        return this.fileName;
    }

    @Override
    public String getLanguageName() {
        return this.languageName;
    }

    @Override
    public String getPackAuthor() {
        return this.packAuthor;
    }
}
