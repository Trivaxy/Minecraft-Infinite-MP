package net.minecraft.game.item;

public enum Rarity {
    COMMON(16777215),
    RARE(5636095),
    EPIC(16733695),
    LEGENDARY(16777045),
    UNCOMMON(1392405);

    int color;

    Rarity(int color) {
        this.color = color;
    }

    public int getColor() {
        return this.color;
    }
}
