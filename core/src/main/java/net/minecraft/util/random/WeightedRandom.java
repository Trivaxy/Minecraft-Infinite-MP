package net.minecraft.util.random;

import java.util.Collection;
import java.util.Random;

public class WeightedRandom {
    public static <T extends WeightedRandom.Choice> int getTotalWeight(Collection<T> choices) {
        int totalWeight = 0;

        for (T t : choices) {
            totalWeight += t.itemWeight;
        }

        return totalWeight;
    }

    public static <T extends WeightedRandom.Choice> T getRandomItem(Random random, Collection<T> choices, int totalWeight) {
        if (totalWeight <= 0) {
            throw new IllegalArgumentException();
        }

        int nextInt = random.nextInt(totalWeight);

        for (T t : choices) {
            nextInt -= t.itemWeight;
            if (nextInt < 0) {
                return t;
            }
        }

        return null;
    }

    public static <T extends WeightedRandom.Choice> T getRandomItem(Random random, Collection<T> choices) {
        return getRandomItem(random, choices, getTotalWeight(choices));
    }

    public static <T extends WeightedRandom.Choice> int getTotalWeight(T[] choices) {
        int totalWeight = 0;

        for (T t : choices) {
            totalWeight += t.itemWeight;
        }

        return totalWeight;
    }

    public static <T extends WeightedRandom.Choice> T getRandomItem(Random random, T[] choices, int totalWeight) {
        if (totalWeight <= 0) {
            throw new IllegalArgumentException();
        }

        int nextInt = random.nextInt(totalWeight);

        for (T t : choices) {
            nextInt -= t.itemWeight;
            if (nextInt < 0) {
                return t;
            }
        }

        return null;
    }

    public static <T extends WeightedRandom.Choice> T getRandomItem(Random random, T[] choices) {
        return getRandomItem(random, choices, getTotalWeight(choices));
    }

    public static class Choice {
        public int itemWeight;

        public Choice(int weight) {
            this.itemWeight = weight;
        }
    }
}
