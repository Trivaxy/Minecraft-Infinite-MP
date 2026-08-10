package net.minecraft.game.world.generation.layers;

import net.minecraft.util.math.IntCache;

public class VoronoiZoomLayer extends GenLayer {
    public VoronoiZoomLayer(long seed, GenLayer parent) {
        super(seed);
        super.parent = parent;
    }

    @Override
    public int[] getInts(int chunkX, int chunkZ, int areaWidth, int areaHeight) {
        chunkX -= 2;
        chunkZ -= 2;
        byte passes = 2;
        int zoom = 1 << passes;
        int zoomX = chunkX >> passes;
        int zoomZ = chunkZ >> passes;
        int width = (areaWidth >> passes) + 3;
        int height = (areaHeight >> passes) + 3;
        int[] areas = this.parent.getInts(zoomX, zoomZ, width, height);
        int zoomWidth = width << passes;
        int zoomHeight = height << passes;
        int[] cached = IntCache.getIntCache(zoomWidth * zoomHeight);

        for (int pass = 0; pass < height - 1; pass++) {
            int topLeft = areas[pass * width];
            int bottomLeft = areas[(pass + 1) * width];

            for (int x = 0; x < width - 1; x++) {
                double shrunkenZoom = zoom * 0.9;
                this.initChunkSeed((long)x + zoomX << passes, (long)pass + zoomZ << passes);
                double zoom1 = (this.nextInt(1024) / 1024.0 - 0.5) * shrunkenZoom;
                double zoom2 = (this.nextInt(1024) / 1024.0 - 0.5) * shrunkenZoom;
                this.initChunkSeed((long)x + zoomX + 1L << passes, (long)pass + zoomZ << passes);
                double zoom3 = (this.nextInt(1024) / 1024.0 - 0.5) * shrunkenZoom + zoom;
                double zoom4 = (this.nextInt(1024) / 1024.0 - 0.5) * shrunkenZoom;
                this.initChunkSeed((long)x + zoomX << passes, (long)pass + zoomZ + 1L << passes);
                double zoom5 = (this.nextInt(1024) / 1024.0 - 0.5) * shrunkenZoom;
                double zoom6 = (this.nextInt(1024) / 1024.0 - 0.5) * shrunkenZoom + zoom;
                this.initChunkSeed((long)x + zoomX + 1L << passes, (long)pass + zoomZ + 1L << passes);
                double zoom7 = (this.nextInt(1024) / 1024.0 - 0.5) * shrunkenZoom + zoom;
                double zoom8 = (this.nextInt(1024) / 1024.0 - 0.5) * shrunkenZoom + zoom;
                int topRight = areas[x + 1 + pass * width];
                int bottomRight = areas[x + 1 + (pass + 1) * width];

                for (int z = 0; z < zoom; z++) {
                    int dist = ((pass << passes) + z) * zoomWidth + (x << passes);

                    for (int index = 0; index < zoom; index++) {
                        double dist1 = (z - zoom2) * (z - zoom2) + (index - zoom1) * (index - zoom1);
                        double dist2 = (z - zoom4) * (z - zoom4) + (index - zoom3) * (index - zoom3);
                        double dist3 = (z - zoom6) * (z - zoom6) + (index - zoom5) * (index - zoom5);
                        double dist4 = (z - zoom8) * (z - zoom8) + (index - zoom7) * (index - zoom7);
                        if (dist1 < dist2 && dist1 < dist3 && dist1 < dist4) {
                            cached[dist++] = topLeft;
                        } else if (dist2 < dist1 && dist2 < dist3 && dist2 < dist4) {
                            cached[dist++] = topRight;
                        } else if (dist3 < dist1 && dist3 < dist2 && dist3 < dist4) {
                            cached[dist++] = bottomLeft;
                        } else {
                            cached[dist++] = bottomRight;
                        }
                    }
                }

                topLeft = topRight;
                bottomLeft = bottomRight;
            }
        }

        int[] zoomValue = IntCache.getIntCache(areaWidth * areaHeight);

        for (int index = 0; index < areaHeight; index++) {
            System.arraycopy(cached, (index + (chunkZ & zoom - 1)) * (width << passes) + (chunkX & zoom - 1), zoomValue, index * areaWidth, areaWidth);
        }

        return zoomValue;
    }
}
