package net.minecraft.game.item.potion;

public class InstantEffect extends Effect {
    public InstantEffect(int id, boolean z, int col) {
        super(id, z, col);
    }

    @Override
    public boolean isInstant() {
        return true;
    }

    @Override
    public boolean isReady(int dur, int amp) {
        return dur >= 1;
    }
}
