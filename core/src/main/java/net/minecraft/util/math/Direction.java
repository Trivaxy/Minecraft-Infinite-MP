package net.minecraft.util.math;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Direction {
    public static final int[] offsetX = new int[]{0, -1, 0, 1, 0, 0, 0, -1, -1, 1, 1};
    public static final int[] offsetZ = new int[]{1, 0, -1, 0, 0, 0, 0, 1, -1, -1, 1};
    public static final int[] offsetY = new int[]{0, 0, 0, 0, 1, -1, 0, 0, 0, 0, 0};
    public static final String[] directions = new String[]{"SOUTH", "WEST", "NORTH", "EAST"};
    public static final int[] directionToFacing = new int[]{3, 4, 2, 5};
    public static final int[] facingToDirection = new int[]{-1, -1, 2, 0, 1, 3};
    public static final int[] rotateOpposite = new int[]{2, 3, 0, 1};
    public static final int[] rotateRight = new int[]{1, 2, 3, 0};
    public static final int[] rotateLeft = new int[]{3, 0, 1, 2};
    public static final int[][] bedDirection = new int[][]{{1, 0, 3, 2, 5, 4}, {1, 0, 5, 4, 2, 3}, {1, 0, 2, 3, 4, 5}, {1, 0, 4, 5, 3, 2}};
    public static final int[] vineGrowth = new int[]{-1, -1, 2, 0, 1, 3};
    public static final int[] oppositeSide = new int[]{1, 0, 3, 2, 5, 4};
    public static final int[] offsetsXForSide = new int[]{0, 0, 0, 0, -1, 1};
    public static final int[] offsetsYForSide = new int[]{-1, 1, 0, 0, 0, 0};
    public static final int[] offsetsZForSide = new int[]{0, 0, -1, 1, 0, 0};
    public static final int[] faceToSide = new int[]{1, 0, 3, 2, 5, 4};
    public static final int SOUTH = 0;
    public static final int WEST = 1;
    public static final int NORTH = 2;
    public static final int EAST = 3;
    public static final int UP = 4;
    public static final int DOWN = 5;
    public static final int NONE = 6;
    public static final int SW = 7;
    public static final int NW = 8;
    public static final int NE = 9;
    public static final int SE = 10;
    public static final int[] HORZ_PLANE = new int[]{0, 1, 2, 3};
    public static final int[] VERT_AXIS = new int[]{4, 6, 5};
    public static final int[] HORZ_PLANE_FULL = new int[]{0, 7, 1, 8, 2, 9, 3, 10};
    public static final int[] HORZ_PLANE_DIAGONALS = new int[]{7, 8, 9, 10};

    public static int getCW(int direction) {
        if (direction < 4) {
            return direction + 1 & 3;
        } else {
            return direction > 6 ? 7 + (direction - 6 & 3) : direction;
        }
    }

    public static int getMovementDirection(double par0, double par2) {
        return MathHelper.abs((float)par0) > MathHelper.abs((float)par2) ? (par0 > 0.0 ? 1 : 3) : (par2 > 0.0 ? 2 : 0);
    }

    public static int rotateY(int direction) {
        switch (direction) {
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                return 3;
            case 3:
                return 0;
            default:
                throw new IllegalStateException("Unable to get Y-rotated facing of " + direction);
        }
    }

    public static int rotateYCCW(int direction) {
        switch (direction) {
            case 0:
                return 3;
            case 1:
                return 0;
            case 2:
                return 1;
            case 3:
                return 2;
            default:
                throw new IllegalStateException("Unable to get CCW facing of " + direction);
        }
    }

    public static int opposite(int direction) {
        switch (direction) {
            case 0:
                return 2;
            case 1:
                return 3;
            case 2:
                return 0;
            case 3:
                return 1;
            case 4:
                return 5;
            case 5:
                return 4;
            default:
                throw new IllegalStateException("Unable to get opposite facing of " + direction);
        }
    }

    public static int getFrontOffsetX(int direction) {
        switch (direction) {
            case 0:
                return 0;
            case 1:
                return -1;
            case 2:
                return 0;
            case 3:
                return 1;
            case 4:
                return 0;
            case 5:
                return 0;
            default:
                return 0;
        }
    }

    public static int getFrontOffsetY(int direction) {
        switch (direction) {
            case 0:
                return 0;
            case 1:
                return 0;
            case 2:
                return 0;
            case 3:
                return 0;
            case 4:
                return 1;
            case 5:
                return -1;
            default:
                return 0;
        }
    }

    public static int getFrontOffsetZ(int direction) {
        switch (direction) {
            case 0:
                return 1;
            case 1:
                return 0;
            case 2:
                return -1;
            case 3:
                return 0;
            case 4:
                return 0;
            case 5:
                return 0;
            default:
                return 0;
        }
    }

    public enum Plane implements Iterable<Integer> {
        HORIZONTAL(new int[]{2, 3, 0, 1}),
        VERTICAL(new int[]{4, 5});

        private final int[] facingValues;

        Plane(int[] facingValues) {
            this.facingValues = facingValues;
        }

        public int random(Random rand) {
            return this.facingValues[rand.nextInt(this.facingValues.length)];
        }

        public IntStream getDirectionValuesAsStream() {
            return Arrays.stream(this.facingValues);
        }

        public int[] getDirectionValues() {
            return this.facingValues;
        }

        @Override
        public Iterator<Integer> iterator() {
            return Arrays.stream(this.facingValues).iterator();
        }

        public List<Integer> randomDirections(int count) {
            if (count > this.facingValues.length) {
                count = this.facingValues.length;
            }

            List<Integer> directions = Arrays.stream(this.facingValues).boxed().collect(Collectors.toList());
            Collections.shuffle(directions);
            return directions.subList(0, count);
        }
    }
}
