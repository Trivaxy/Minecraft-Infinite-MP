package net.minecraft.client.localization;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.localization.CustomLanguage;
import net.minecraft.localization.DefaultLanguage;
import net.minecraft.localization.LanguageBase;
import net.minecraft.localization.LanguageFolder;

public class LanguageList {
    private List<LanguageBase> availableLanguagePacks = new ArrayList<>();
    private LanguageBase defaultLanguagePack = DefaultLanguage.DEFAULT;
    public LanguageBase selectedLanguagePack;
    private Map<String, LanguageBase> languageMap = new HashMap<>();
    private Minecraft mc;
    private File languagePackDir;

    public LanguageList(Minecraft mc, File file) {
        this.mc = mc;
        this.languagePackDir = new File("languagepacks");
        if (!this.languagePackDir.exists()) {
            this.languagePackDir.mkdirs();
        }

        this.updateLanguages();
    }

    public boolean setLanguage(LanguageBase langPack) {
        if (langPack == this.selectedLanguagePack) {
            return false;
        }

        this.selectedLanguagePack = langPack;
        this.mc.options.languagePack = langPack.getFileName();
        this.mc.options.saveOptions();
        return true;
    }

    public void updateLanguages() {
        ArrayList<LanguageBase> langPackList = new ArrayList<>();
        this.selectedLanguagePack = this.defaultLanguagePack;
        langPackList.add(this.defaultLanguagePack);

        for (File file : this.getLanguageDir()) {
            String packId = this.generateLangID(file);
            if (packId != null) {
                LanguageBase langPack = this.languageMap.get(packId);
                if (langPack == null) {
                    langPack = file.isDirectory()
                        ? new LanguageFolder(packId, file, this.defaultLanguagePack)
                        : new CustomLanguage(packId, file, this.defaultLanguagePack);
                    this.languageMap.put(packId, langPack);
                }

                if (langPack.getFileName().equals(this.mc.options.languagePack)) {
                    this.selectedLanguagePack = langPack;
                }

                langPackList.add(langPack);
            }
        }

        this.availableLanguagePacks.removeAll(langPackList);

        for (LanguageBase languagePack : this.availableLanguagePacks) {
            languagePack.deletePack();
            this.languageMap.remove(languagePack.getPackID());
        }

        this.availableLanguagePacks = langPackList;
    }

    private String generateLangID(File file) {
        return file.isFile() && file.getName().toLowerCase().endsWith(".zip")
            ? file.getName() + ":" + file.length() + ":" + file.lastModified()
            : (file.isDirectory() && new File(file, "pack.txt").exists() ? file.getName() + ":folder:" + file.lastModified() : null);
    }

    private List<File> getLanguageDir() {
        return this.languagePackDir.exists() && this.languagePackDir.isDirectory() ? Arrays.asList(this.languagePackDir.listFiles()) : Collections.emptyList();
    }

    public List<LanguageBase> availableLanguages() {
        return Collections.unmodifiableList(this.availableLanguagePacks);
    }
}
