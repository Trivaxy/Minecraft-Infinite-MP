package net.minecraft.client.render.texture.pack;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.localization.Font;

public class PackList {
    private Minecraft mc;
    private File packDir;
    private List<PackBase> availablePacks = new ArrayList<>();
    private Map<String, PackBase> textureMap = new HashMap<>();
    private PackBase defaultPack = new DefaultPack();
    public PackBase selectedPack;

    public PackList(Minecraft minecraft, File file) {
        this.mc = minecraft;
        this.packDir = new File("texturepacks");
        if (!this.packDir.exists()) {
            this.packDir.mkdirs();
        }

        this.updateAvaliableTexturePacks();
    }

    public boolean setTexturePack(PackBase pack) {
        if (pack == this.selectedPack) {
            return false;
        }

        this.selectedPack = pack;
        this.mc.options.texturePack = pack.getFileName();
        this.mc.fontRenderer = new Font(this.mc.options, (TexturePack)this.selectedPack, this.mc.textureLoader);
        this.mc.options.saveOptions();
        return true;
    }

    public void updateAvaliableTexturePacks() {
        ArrayList<PackBase> packs = new ArrayList<>();
        this.selectedPack = this.defaultPack;
        packs.add(this.defaultPack);

        for (File file : this.getPackDirContents()) {
            String packID = this.generateTexturePackID(file);
            if (packID != null) {
                PackBase pack = this.textureMap.get(packID);
                if (pack == null) {
                    pack = file.isDirectory() ? new PackFolder(packID, file, this.defaultPack) : new CustomPack(packID, file, this.defaultPack);
                    this.textureMap.put(packID, pack);
                }

                if (pack.getFileName().equals(this.mc.options.texturePack)) {
                    this.selectedPack = pack;
                }

                packs.add(pack);
            }
        }

        this.availablePacks.removeAll(packs);

        for (PackBase pack : this.availablePacks) {
            pack.deletePack(this.mc.textureLoader);
            this.textureMap.remove(pack.getID());
        }

        this.availablePacks = packs;
    }

    private String generateTexturePackID(File file) {
        return file.isFile() && file.getName().toLowerCase().endsWith(".zip")
            ? file.getName() + ":" + file.length() + ":" + file.lastModified()
            : (file.isDirectory() && new File(file, "pack.txt").exists() ? file.getName() + ":folder:" + file.lastModified() : null);
    }

    private List<File> getPackDirContents() {
        return this.packDir.exists() && this.packDir.isDirectory() ? Arrays.asList(this.packDir.listFiles()) : Collections.emptyList();
    }

    public List<PackBase> availablePacks() {
        return Collections.unmodifiableList(this.availablePacks);
    }
}
