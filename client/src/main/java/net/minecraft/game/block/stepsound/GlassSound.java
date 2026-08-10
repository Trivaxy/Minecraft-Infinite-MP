package net.minecraft.game.block.stepsound;

public class GlassSound extends StepSound {
    public GlassSound(String name, float vol, float ptch) {
        super(name, vol, ptch);
    }

    @Override
    public String getBreakSound() {
        return "random.glass";
    }
}
