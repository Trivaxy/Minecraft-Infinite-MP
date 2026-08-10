package net.minecraft.client.sound;

import java.io.File;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.options.OptionValues;
import net.minecraft.client.options.OptionsList;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.dimension.HellDimension;
import net.minecraft.util.logging.LoggingUtils;
import net.minecraft.util.math.MathHelper;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

public class SoundManager {
    static Logger logger = Logger.getLogger("Minecraft");
    public SoundSystem sndSystem;
    private OptionsList options;
    public Random rand = new Random();
    private SoundPool soundsPool = new SoundPool();
    private SoundPool musicPool = new SoundPool();
    private SoundPool creativeMusicPool = new SoundPool();
    private SoundPool nightMusicPool = new SoundPool();
    private SoundPool hellMusicPool = new SoundPool();
    private SoundPool skyMusicPool = new SoundPool();
    private SoundPool voidMusicPool = new SoundPool();
    private SoundPool jukeboxMusicPool = new SoundPool();
    public String lastAmbience = "";
    public String ambience = "";
    public String currentTrack = "";
    public String trackArtist;
    public int ticksBeforeMusic;
    private int latestSoundID = 0;
    private float soundFade = 1.0F;
    public boolean loaded = false;
    private boolean loggerSet = false;
    private HashMap<String, String> trackList = new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;

        {
            this.put("calm1.ogg", "Minecraft");
            this.put("calm2.ogg", "Clark");
            this.put("calm3.ogg", "Sweden");
            this.put("piano1.ogg", "Dry Hands");
            this.put("piano2.ogg", "Wet Hands");
            this.put("piano3.ogg", "Mice on Venus");
            this.put("hal1.ogg", "Subwoofer Lullaby");
            this.put("hal2.ogg", "Living Mice");
            this.put("hal3.ogg", "Haggstrom");
            this.put("hal4.ogg", "Danny");
            this.put("nuance1.ogg", "Key");
            this.put("nuance2.ogg", "Oxygene");
            this.put("night1.ogg", "Death");
            this.put("night2.ogg", "Excuse");
            this.put("night3.ogg", "Equinoxe");
            this.put("night4.ogg", "KI");
            this.put("creative1.ogg", "Blind Spots");
            this.put("creative2.ogg", "Biome Fest");
            this.put("creative3.ogg", "Haunt Muskie");
            this.put("creative4.ogg", "Aria Math");
            this.put("creative5.ogg", "Taswell");
            this.put("creative6.ogg", "Dreiton");
            this.put("hell1.ogg", "Eternal Suspense");
            this.put("hell2.ogg", "Lost");
            this.put("hell3.ogg", "Broken Soul");
            this.put("hell4.ogg", "Crimson Eyes");
            this.put("hell5.ogg", "Illuminations");
            this.put("sky1.ogg", "Summer Evening");
            this.put("sky2.ogg", "Highlander");
            this.put("sky3.ogg", "Dreamscape");
            this.put("sky4.ogg", "Angelic Lullaby");
            this.put("sky5.ogg", "Vertigo");
            this.put("void1.ogg", "For Those Who Shall Not Be Forgiven");
        }
    };

    public SoundManager() {
        this.ticksBeforeMusic = 1000;
    }

    public void loadSoundSettings(OptionsList options) {
        this.options = options;
        if (!this.loaded
            && (
                OptionValues.musicVol >= 0.0F
                    || OptionValues.soundVol >= 0.0F
                    || OptionValues.mobVol >= 0.0F
                    || OptionValues.masterVol >= 0.0F
                    || OptionValues.streamingVol >= 0.0F
                    || OptionValues.ambienceVol >= 0.0F
                    || OptionValues.blocksVol >= 0.0F
            )) {
            this.setVolumesAndCodec();
        }
    }

    private void setVolumesAndCodec() {
        try {
            float sound = OptionValues.soundVol;
            float music = OptionValues.musicVol;
            float master = OptionValues.masterVol;
            float ambience = OptionValues.ambienceVol;
            float mobs = OptionValues.mobVol;
            float blocks = OptionValues.blocksVol;
            float streaming = OptionValues.streamingVol;
            OptionValues.soundVol = 0.0F;
            OptionValues.musicVol = 0.0F;
            OptionValues.masterVol = 0.0F;
            OptionValues.ambienceVol = 0.0F;
            OptionValues.mobVol = 0.0F;
            OptionValues.blocksVol = 0.0F;
            OptionValues.streamingVol = 0.0F;
            this.options.saveOptions();
            SoundSystemConfig.addLibrary(LibraryLWJGLOpenAL.class);
            SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
            SoundSystemConfig.setCodec("wav", CodecWav.class);
            if (!this.loggerSet) {
                SoundSystemConfig.setLogger(new SoundLogger());
                this.loggerSet = true;
            }

            this.sndSystem = new SoundSystem();
            OptionValues.soundVol = sound;
            OptionValues.musicVol = music;
            OptionValues.masterVol = master;
            OptionValues.ambienceVol = ambience;
            OptionValues.mobVol = mobs;
            OptionValues.blocksVol = blocks;
            OptionValues.streamingVol = streaming;
            this.options.saveOptions();
        } catch (Throwable throwable) {
            LoggingUtils.warnThrowable(logger, "Error linking with the LibraryJavaSound plug-in", throwable);
        }

        this.loaded = true;
    }

    public void onOptionsChange() {
        if (!this.loaded
            && OptionValues.masterVol >= 0.0F
            && (
                OptionValues.musicVol > 0.0F
                    || OptionValues.blocksVol > 0.0F
                    || OptionValues.soundVol > 0.0F
                    || OptionValues.ambienceVol > 0.0F
                    || OptionValues.mobVol > 0.0F
                    || OptionValues.streamingVol > 0.0F
            )) {
            this.setVolumesAndCodec();
        }

        if (OptionValues.musicVol != 0.0F && OptionValues.masterVol != 0.0F) {
            this.sndSystem.setVolume("BgMusic", OptionValues.musicVol * OptionValues.masterVol);
        } else {
            this.sndSystem.stop("BgMusic");
        }
    }

    public void close() {
        if (this.loaded) {
            this.sndSystem.cleanup();
        }
    }

    public void addSound(String name, File file) {
        this.soundsPool.addSound(name, file);
    }

    public void addMusic(String poolName, String musicTrack, File file) {
        switch (poolName) {
            case "creativemusic":
                this.creativeMusicPool.addSound(musicTrack, file);
                break;
            case "nightmusic":
                this.nightMusicPool.addSound(musicTrack, file);
                break;
            case "nethermusic":
                this.hellMusicPool.addSound(musicTrack, file);
                break;
            case "skymusic":
                this.skyMusicPool.addSound(musicTrack, file);
                break;
            case "voidmusic":
                this.voidMusicPool.addSound(musicTrack, file);
                break;
            case "streaming":
                this.jukeboxMusicPool.addSound(musicTrack, file);
                break;
            default:
                this.musicPool.addSound(musicTrack, file);
        }
    }

    public void addStreaming(String trackName, File file) {
        this.jukeboxMusicPool.addSound(trackName, file);
    }

    public void playRandomMusic(int musicType) {
        if (this.loaded && OptionValues.musicVol != 0.0F && OptionValues.masterVol != 0.0F) {
            if (!this.sndSystem.playing("BgMusic") && !this.sndSystem.playing("streaming")) {
                if (this.ticksBeforeMusic > 0) {
                    this.ticksBeforeMusic--;
                    return;
                }

                SoundPool.Sound sound = this.musicPool.getRandomSound();
                switch (musicType) {
                    case 1:
                        sound = this.hellMusicPool.getRandomSound();
                        this.trackArtist = "Soybean_56";
                        break;
                    case 2:
                        sound = this.skyMusicPool.getRandomSound();
                        this.trackArtist = "Soybean_56";
                        break;
                    case 3:
                        sound = this.voidMusicPool.getRandomSound();
                        this.trackArtist = "Soybean_56";
                        break;
                    case 4:
                        sound = this.creativeMusicPool.getRandomSound();
                        this.trackArtist = "C418";
                        break;
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    default:
                        sound = this.musicPool.getRandomSound();
                        this.trackArtist = "C418";
                        break;
                    case 10:
                        sound = this.nightMusicPool.getRandomSound();
                        this.trackArtist = "C418";
                }

                if (sound != null) {
                    this.ticksBeforeMusic = this.rand.nextInt(12000) + 12000;
                    this.sndSystem.backgroundMusic("BgMusic", sound.soundUrl, sound.soundName, false);
                    this.sndSystem.setVolume("BgMusic", OptionValues.musicVol * OptionValues.masterVol);
                    this.sndSystem.play("BgMusic");
                    this.currentTrack = this.trackList.get(sound.soundName);
                }
            }
        }
    }

    public void setListener(Mob entity, float deltaTime) {
        if (this.loaded && OptionValues.soundVol > 0.0F && OptionValues.masterVol > 0.0F && entity != null) {
            float pitch = entity.prevPitch + (entity.pitch - entity.prevPitch) * deltaTime;
            float yaw = entity.prevYaw + (entity.yaw - entity.prevYaw) * deltaTime;
            float x = (float)(entity.prevX + (entity.posX - entity.prevX) * deltaTime);
            float y = (float)(entity.prevY + (entity.posY - entity.prevY) * deltaTime);
            float z = (float)(entity.prevZ + (entity.posZ - entity.prevZ) * deltaTime);
            float endZ = MathHelper.cos(-yaw * (float) (Math.PI / 180.0) - (float) Math.PI);
            float endY = MathHelper.sin(-yaw * (float) (Math.PI / 180.0) - (float) Math.PI);
            float startZ = MathHelper.cos(-pitch * (float) (Math.PI / 180.0));
            float endX = MathHelper.sin(-pitch * (float) (Math.PI / 180.0));
            float startX = -endY * startZ;
            float startY = -endZ * startZ;
            endY = -endY * endX;
            endZ = -endZ * endX;
            this.sndSystem.setListenerPosition(x, y, z);
            this.sndSystem.setListenerOrientation(startX, endX, startY, endY, startZ, endZ);
        }
    }

    private void playDirectionalSound(String name, float x, float y, float z, float volume, float pitch, float optionVol, String type) {
        if (this.loaded && optionVol > 0.0F && OptionValues.masterVol > 0.0F) {
            SoundPool.Sound sound = this.soundsPool.getRandomSoundFromPool(name);
            if (sound != null && volume > 0.0F) {
                this.latestSoundID = (this.latestSoundID + 1) % 256;
                String soundName = type + this.latestSoundID;
                float maxVolume = 16.0F;
                if (volume > 1.0F) {
                    maxVolume = 16.0F * volume;
                }

                this.sndSystem.newSource(volume > 1.0F, soundName, sound.soundUrl, sound.soundName, false, x, y, z, 2, maxVolume);
                this.sndSystem.setPitch(soundName, pitch);
                if (volume > 1.0F) {
                    volume = 1.0F;
                }

                this.sndSystem.setVolume(soundName, volume * (optionVol * OptionValues.masterVol));
                this.sndSystem.play(soundName);
            }
        }
    }

    private void playSound(String name, float volume, float pitch, float optionVol, String type) {
        if (this.loaded && optionVol > 0.0F && OptionValues.masterVol > 0.0F) {
            SoundPool.Sound sound = this.soundsPool.getRandomSoundFromPool(name);
            if (sound != null) {
                this.latestSoundID = (this.latestSoundID + 1) % 256;
                String soundName = type + this.latestSoundID;
                this.sndSystem.newSource(false, soundName, sound.soundUrl, sound.soundName, false, 0.0F, 0.0F, 0.0F, 0, 0.0F);
                this.sndSystem.setPitch(soundName, volume);
                this.sndSystem.setVolume(soundName, pitch * (optionVol * OptionValues.masterVol));
                this.sndSystem.play(soundName);
            }
        }
    }

    public void playSound(String name, float x, float y, float z, float volume, float pitch) {
        if (!name.equals("random.thunder")
            || Minecraft.getMinecraft().gameRenderer.cameraMob == null
            || !(Minecraft.getMinecraft().gameRenderer.cameraMob.posY < Minecraft.getMinecraft().world.getSeaLevel() - 8)) {
            this.playDirectionalSound(name, x, y, z, volume, pitch, OptionValues.soundVol, "sound_");
        }
    }

    public void playMobSound(String name, int id, float x, float y, float z, float volume, float pitch) {
        this.playDirectionalSound(name, x, y, z, volume, pitch, OptionValues.mobVol, "mob_");
    }

    public void playBlockSound(String name, float x, float y, float z, float volume, float pitch) {
        this.playDirectionalSound(name, x, y, z, volume, pitch, OptionValues.blocksVol, "block_");
    }

    public void playSoundFX(String name, float volume, float pitch) {
        this.playSound(name, volume, pitch, OptionValues.soundVol, "sound_");
    }

    public void playAmbience(String name, float pitch, float volume) {
        this.playSound(name, volume, pitch, OptionValues.ambienceVol, "ambient_");
    }

    public void playStreaming(String name, float x, float y, float z, float volume, float pitch, float dist) {
        if (this.loaded && OptionValues.streamingVol != 0.0F && OptionValues.masterVol != 0.0F) {
            String soundName = "streaming";
            if (this.sndSystem.playing("streaming")) {
                this.sndSystem.stop("streaming");
            }

            if (name != null) {
                SoundPool.Sound sound = this.jukeboxMusicPool.getRandomSoundFromPool(name);
                if (sound != null && volume > 0.0F) {
                    if (this.sndSystem.playing("BgMusic")) {
                        this.sndSystem.stop("BgMusic");
                    }

                    this.sndSystem.newStreamingSource(true, soundName, sound.soundUrl, sound.soundName, false, x, y, z, 2, 16.0F * dist);
                    this.sndSystem.setVolume(soundName, 0.5F * (OptionValues.streamingVol * OptionValues.masterVol));
                    this.sndSystem.play(soundName);
                }
            }
        }
    }

    public void playRegionalAmbience(Mob thePlayer, World theWorld) {
        float distance = 12.0F;
        int x = MathHelper.floor_double(thePlayer.posX);
        int y = MathHelper.floor_double(thePlayer.posY);
        int z = MathHelper.floor_double(thePlayer.posZ);
        BoundingBox boundingBox = thePlayer.boundingBox.expand(distance + 2.0F, 6.0, distance + 2.0F);
        int water = theWorld.countBlocks(boundingBox, BlockList.waterMoving.id);
        water += theWorld.countBlocks(boundingBox, BlockList.waterStill.id);
        int leaves = theWorld.countBlocks(boundingBox, BlockList.leaves.id);
        int grass = theWorld.countBlocks(boundingBox, BlockList.tallGrass.id);
        int crystals = theWorld.countBlocks(boundingBox, BlockList.clusterBlock.id);
        if (thePlayer.isInMaterial(MaterialList.water)) {
            this.ambience = "misc.underwater";
        } else if (!thePlayer.isGliding()
            && (y < theWorld.getWorldHeight() - theWorld.getWorldHeight() / 4 || !theWorld.isBlockExposed(x, y, z) || theWorld.currDim instanceof HellDimension)
            )
         {
            if (water >= 1000 && theWorld.isBlockExposed(x, y, z)) {
                this.ambience = "loops.ocean";
            } else if (leaves >= 1000 && !thePlayer.inWater && theWorld.isDaytime()) {
                if (theWorld.getBiomeAt(x, z).getTemperature() == 1) {
                    this.ambience = "loops.birds screaming loop";
                } else if (theWorld.getBiomeAt(x, z).getTemperature() == 2 && theWorld.getBiomeAt(x, z) != BiomeList.woodlandHills) {
                    this.ambience = "loops.swamp";
                }
            } else if (grass >= 500 && !theWorld.isDaytime() && theWorld.isBlockExposed(x, y, z)) {
                this.ambience = "loops.crickets chirping";
            } else if (crystals >= 100 && !theWorld.isBlockExposed(x, y, z)) {
                this.ambience = "loops.cave chimes";
            } else {
                this.ambience = "";
            }
        } else {
            this.ambience = "loops.treewind";
        }

        if ((this.lastAmbience == this.ambience || !(this.soundFade > 0.0F)) && !this.ambience.isEmpty()) {
            if (!this.ambience.isEmpty()) {
                this.queueAmbience(this.ambience);
                this.soundFade += 0.01F;
                if (this.soundFade > 0.3F) {
                    this.soundFade = 0.3F;
                }
            }
        } else {
            this.soundFade -= 0.01F;
            if (this.soundFade < 0.0F) {
                this.soundFade = 0.0F;
                this.sndSystem.stop("ambienceTrack");
            }
        }

        this.sndSystem.setVolume("ambienceTrack", this.soundFade * (OptionValues.ambienceVol * OptionValues.masterVol));
    }

    private void queueAmbience(String string) {
        if (this.loaded && OptionValues.ambienceVol != 0.0F && OptionValues.masterVol != 0.0F) {
            SoundPool.Sound sound = this.soundsPool.getRandomSoundFromPool(string);
            if (this.lastAmbience != string) {
                this.lastAmbience = string;
                this.sndSystem.stop("ambienceTrack");
            }

            if (sound != null && !this.sndSystem.playing("ambienceTrack")) {
                this.sndSystem.backgroundMusic("ambienceTrack", sound.soundUrl, sound.soundName, true);
                this.sndSystem.setPitch("ambienceTrack", 1.0F);
                this.sndSystem.setVolume("ambienceTrack", 0.1F * (OptionValues.ambienceVol * OptionValues.masterVol));
                this.sndSystem.play("ambienceTrack");
            }
        }
    }
}
