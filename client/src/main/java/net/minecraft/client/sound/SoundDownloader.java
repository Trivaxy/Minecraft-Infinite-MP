package net.minecraft.client.sound;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.title.TitleScreen;

public class SoundDownloader extends Thread {
    public File resourcesFolder;
    private Minecraft mc;
    private boolean closing = false;

    public SoundDownloader(File directory, Minecraft theGame) {
        this.mc = theGame;
        this.setName("Resource download thread");
        this.setDaemon(true);
        this.resourcesFolder = new File(directory, "resources/");
        if (!this.resourcesFolder.exists() && !this.resourcesFolder.mkdirs()) {
            throw new RuntimeException("The working directory could not be created: " + this.resourcesFolder);
        }
    }

    @Override
    public void run() {
        if (Minecraft.NO_DOWNLOAD_RESOURCES_ON_START) {
            this.loadResource(this.resourcesFolder, "");
            Minecraft.logger.info("[DEBUG] Bypassed resource downloading");
        } else {
            try {
                ArrayList<String> soundList = new ArrayList<>();
                URL soundURL = new URL("https://vesuviusvenox.github.io/Classic-Resources/");
                BufferedReader reader = new BufferedReader(new InputStreamReader(soundURL.openStream()));
                String sound = "";

                while ((sound = reader.readLine()) != null) {
                    soundList.add(sound);
                }

                reader.close();

                for (int soundID = 0; soundID < soundList.size(); soundID++) {
                    this.downloadAndInstallResource(soundURL, soundList.get(soundID));
                    if (this.closing) {
                        return;
                    }
                }
            } catch (IOException ex) {
                this.loadResource(this.resourcesFolder, "");
                ex.printStackTrace();
            }
        }
    }

    private void loadResource(File resourceFromFile, String soundName) {
        File[] resourceList = resourceFromFile.listFiles();

        for (int id = 0; id < resourceList.length; id++) {
            if (resourceList[id].isDirectory()) {
                this.loadResource(resourceList[id], soundName + resourceList[id].getName() + "/");
            } else {
                this.mc.installResource(soundName + resourceList[id].getName(), resourceList[id]);
            }
        }
    }

    private void downloadAndInstallResource(URL repository, String sound) {
        try {
            String[] soundList = sound.split(",");
            String beginningSound = soundList[0];
            int soundCount = Integer.parseInt(soundList[1]);
            Long.parseLong(soundList[2]);
            File resourcesDirectory = new File(this.resourcesFolder, beginningSound);
            if (!resourcesDirectory.exists() || resourcesDirectory.length() != soundCount) {
                resourcesDirectory.getParentFile().mkdirs();
                String cleanSoundName = beginningSound.replaceAll(" ", "%20");
                this.downloadResource(new URL(repository, cleanSoundName), resourcesDirectory, soundCount);
                if (this.closing) {
                    return;
                }
            }

            this.mc.installResource(beginningSound, resourcesDirectory);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void downloadResource(URL repository, File soundFile, int soundID) throws IOException {
        TitleScreen.resource = "Downloading resource: " + repository.getFile().replace("/Classic-Resources", "");
        byte[] soundList = new byte[4096];
        DataInputStream inputStream = new DataInputStream(repository.openStream());

        try (DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(soundFile))) {
            do {
                int soundNumber;
                if ((soundNumber = inputStream.read(soundList)) < 0) {
                    inputStream.close();
                    outputStream.close();
                    TitleScreen.resource = "";
                    return;
                }

                outputStream.write(soundList, 0, soundNumber);
            } while (!this.closing);
        }
    }

    public void closeMinecraft() {
        this.closing = true;
    }
}
