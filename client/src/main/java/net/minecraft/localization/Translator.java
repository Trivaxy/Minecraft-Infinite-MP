package net.minecraft.localization;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class Translator {
    private static Translator instance = new Translator(DefaultLanguage.DEFAULT);
    private Properties translateTable = new Properties();

    private Translator(LanguageBase langPk) {
        try {
            this.translateTable.load(new InputStreamReader(langPk.getResourceAsStream("/lang/info.lang"), StandardCharsets.UTF_8));
            this.translateTable.load(new InputStreamReader(langPk.getResourceAsStream("/lang/stats.lang"), StandardCharsets.UTF_8));
            this.translateTable.load(new InputStreamReader(langPk.getResourceAsStream("/lang/gui.lang"), StandardCharsets.UTF_8));
            this.translateTable.load(new InputStreamReader(langPk.getResourceAsStream("/lang/multiplayer.lang"), StandardCharsets.UTF_8));
            this.translateTable.load(new InputStreamReader(langPk.getResourceAsStream("/lang/options.lang"), StandardCharsets.UTF_8));
            this.translateTable.load(new InputStreamReader(langPk.getResourceAsStream("/lang/tile.lang"), StandardCharsets.UTF_8));
            this.translateTable.load(new InputStreamReader(langPk.getResourceAsStream("/lang/item.lang"), StandardCharsets.UTF_8));
            this.translateTable.load(new InputStreamReader(langPk.getResourceAsStream("/lang/entity.lang"), StandardCharsets.UTF_8));
            this.translateTable.load(new InputStreamReader(langPk.getResourceAsStream("/lang/desc.lang"), StandardCharsets.UTF_8));
            this.translateTable.load(new InputStreamReader(langPk.getResourceAsStream("/lang/commands.lang"), StandardCharsets.UTF_8));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static Translator getInstance() {
        return instance;
    }

    public static void resetLanguage(LanguageBase pack) {
        instance = new Translator(pack);
    }

    public String translateKey(String key) {
        return this.translateTable.getProperty(key, key);
    }

    public String translateKeyFormat(String format, Object... args) {
        String key = this.translateTable.getProperty(format, format);
        return String.format(key, args);
    }

    public String translateNamedKey(String element) {
        return this.translateTable.getProperty(element + ".name", "");
    }
}
