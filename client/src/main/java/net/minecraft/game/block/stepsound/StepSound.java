package net.minecraft.game.block.stepsound;

public class StepSound {
    public final String name;
    public final float volume;
    public final float pitch;

    public StepSound(String name, float vol, float pitch) {
        this.name = name;
        this.volume = vol;
        this.pitch = pitch;
    }

    public float getVolume() {
        return this.volume;
    }

    public float getPitch() {
        return this.pitch;
    }

    public String getBreakSound() {
        return "step." + this.name;
    }

    public String getSound() {
        return "step." + this.name;
    }
}
