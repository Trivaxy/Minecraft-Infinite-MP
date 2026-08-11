package net.minecraft.game.world.generation.structures.oceanruins;

public class BuildingType {
    public Class<?> buildingClass;
    public int probability;

    public BuildingType(Class<?> buildingClass, int probability) {
        this.buildingClass = buildingClass;
        this.probability = probability;
    }
}
