package net.minecraft.client.sound;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SoundPool {
    private Random rand = new Random();
    private Map<String, List<SoundPool.Sound>> soundPoolMap = new HashMap<>();
    private List<SoundPool.Sound> soundPoolList = new ArrayList<>();
    public int numberOfSounds = 0;

    public SoundPool.Sound addSound(String name, File file) {
        try {
            String backup = name;
            name = name.substring(0, name.indexOf("."));

            while (Character.isDigit(name.charAt(name.length() - 1))) {
                name = name.substring(0, name.length() - 1);
            }

            name = name.replaceAll("/", ".");
            if (!this.soundPoolMap.containsKey(name)) {
                this.soundPoolMap.put(name, new ArrayList<>());
            }

            SoundPool.Sound soundEntry = new SoundPool.Sound(backup, file.toURI().toURL());
            this.soundPoolMap.get(name).add(soundEntry);
            this.soundPoolList.add(soundEntry);
            this.numberOfSounds++;
            return soundEntry;
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    public SoundPool.Sound getRandomSoundFromPool(String snd) {
        List<SoundPool.Sound> sndList = this.soundPoolMap.get(snd);
        return sndList == null ? null : sndList.get(this.rand.nextInt(sndList.size()));
    }

    public SoundPool.Sound getRandomSound() {
        return this.soundPoolList.size() == 0 ? null : this.soundPoolList.get(this.rand.nextInt(this.soundPoolList.size()));
    }

    public class Sound {
        public String soundName;
        public URL soundUrl;

        public Sound(String name, URL url) {
            this.soundName = name;
            this.soundUrl = url;
        }
    }
}
