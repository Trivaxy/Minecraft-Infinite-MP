package net.minecraft.game.block.stepsound;

public class SlimeSound extends StepSound {
    public SlimeSound(String name, float vol, float pitch) {
        super(name, vol, pitch);
    }

    @Override
    public String getBreakSound() {
        return "mob.slime";
    }

    @Override
    public String getSound() {
        return "mob.slime";
    }
}
