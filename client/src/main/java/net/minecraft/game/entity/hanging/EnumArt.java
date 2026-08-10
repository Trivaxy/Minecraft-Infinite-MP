package net.minecraft.game.entity.hanging;

public enum EnumArt {
    Kebab("kebab", 16, 16),
    Aztec("aztec", 16, 16),
    Alban("alban", 16, 16),
    Aztec2("aztec2", 16, 16),
    Bomb("bomb", 16, 16),
    Plant("plant", 16, 16),
    Wasteland("wasteland", 16, 16),
    Dawn("dawn", 16, 16),
    Winds("cloud", 16, 16),
    Seasons("italy", 16, 16),
    Blanket("blanket", 16, 16),
    Pergola("pergola", 16, 16),
    Patio("patio", 16, 16),
    Ruins("ruins", 16, 16),
    Seamtress("seamtress", 16, 16),
    Hummingbird("bird", 16, 16),
    Pool("pool", 32, 16),
    Courbet("courbet", 32, 16),
    Sea("sea", 32, 16),
    Sunset("sunset", 32, 16),
    Creebet("creeper", 32, 16),
    View("view", 32, 16),
    Elderwind("chateau", 32, 16),
    Nix("nix", 32, 16),
    Ducks("ducks", 32, 16),
    Boat("boat", 32, 16),
    Geistlich("mist", 32, 16),
    Dense("dense", 32, 16),
    Wanderer("wanderer", 16, 32),
    Graham("graham", 16, 32),
    Lute("lute", 16, 32),
    Dream("dream", 16, 32),
    Match("match", 32, 32),
    Bust("bust", 32, 32),
    Stage("stage", 32, 32),
    Void("void", 32, 32),
    SkullAndRoses("skull", 32, 32),
    Battle("battle", 32, 32),
    Angel("angel", 32, 32),
    Monologue("monologue", 32, 32),
    Dragon("dragon", 32, 32),
    Wait("wait", 32, 32),
    Grove("grove", 32, 32),
    Dresses("dresses", 32, 32),
    Fighters("fighters", 64, 32),
    Nature("nature", 64, 32),
    Change("change", 64, 32),
    Noekken("noekken", 64, 32),
    Hulder("hulder", 64, 32),
    Mountains("mountains", 64, 32),
    Witch("witch", 32, 48),
    Tomhet("woodpecker", 32, 48),
    Field("cornstacks", 64, 48),
    Kong("kong", 64, 48),
    Coil("coil", 64, 48),
    Pointer("pointer", 64, 64),
    Pigscene("pigscene", 64, 64),
    Filosofem("filosofem", 64, 64);

    public final String title;
    public final int width;
    public final int height;

    EnumArt(String title, int sizeX, int sizeY) {
        this.title = title;
        this.width = sizeX;
        this.height = sizeY;
    }
}
