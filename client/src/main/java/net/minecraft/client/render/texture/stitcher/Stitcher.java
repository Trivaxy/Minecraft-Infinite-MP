package net.minecraft.client.render.texture.stitcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.render.texture.Texture;
import net.minecraft.client.render.texture.TextureManager;

public class Stitcher {
    private final Set<StitchHolder> setStitchHolders = new HashSet<>(256);
    private final List<StitchSlot> stitchSlots = new ArrayList<>(256);
    private Texture atlasTexture;
    private final String textureName;
    private int currentWidth = 0;
    private int currentHeight = 0;
    private final int maxWidth;
    private final int maxHeight;
    private final int maxTileDimension;
    private final boolean forcePowerOf2;

    public Stitcher(String name, int width, int height, boolean apathy, int dimensions) {
        this.textureName = name;
        this.maxWidth = width;
        this.maxHeight = height;
        this.forcePowerOf2 = apathy;
        this.maxTileDimension = dimensions;
    }

    public void addStitchHolder(StitchHolder holder) {
        if (this.maxTileDimension > 0) {
            holder.setNewDimension(this.maxTileDimension);
        }

        this.setStitchHolders.add(holder);
    }

    public Texture getTexture() {
        if (this.forcePowerOf2) {
            this.currentWidth = this.getCeilPowerOf2(this.currentWidth);
            this.currentHeight = this.getCeilPowerOf2(this.currentHeight);
        }

        this.atlasTexture = TextureManager.instance().createEmptyTexture(this.textureName, 1, this.currentWidth, this.currentHeight, 6408);
        this.atlasTexture.fillRect(this.atlasTexture.getRect(), -65536);

        for (StitchSlot slot : this.getStichSlots()) {
            StitchHolder holder = slot.getStitchHolder();
            this.atlasTexture.copyFrom(slot.getOriginX(), slot.getOriginY(), holder.getTexture(), holder.isRotated());
        }

        TextureManager.instance().registerTexture(this.textureName, this.atlasTexture);
        return this.atlasTexture;
    }

    public void doStitch() {
        StitchHolder[] holders = this.setStitchHolders.toArray(new StitchHolder[this.setStitchHolders.size()]);
        Arrays.sort(holders);
        this.atlasTexture = null;

        for (StitchHolder holder : holders) {
            if (!this.allocateSlot(holder)) {
                throw new RuntimeException("Cannot create slot!");
            }
        }
    }

    public List<StitchSlot> getStichSlots() {
        ArrayList<StitchSlot> slots = new ArrayList<>();

        for (StitchSlot slot : this.stitchSlots) {
            slot.getAllSlots(slots);
        }

        return slots;
    }

    private int getCeilPowerOf2(int val1) {
        int ceil = val1 - 1;
        ceil |= ceil >> 1;
        ceil |= ceil >> 2;
        ceil |= ceil >> 4;
        ceil |= ceil >> 8;
        ceil |= ceil >> 16;
        return ceil + 1;
    }

    private boolean allocateSlot(StitchHolder holder) {
        for (StitchSlot stitchSlot : this.stitchSlots) {
            if (stitchSlot.putHolder(holder)) {
                return true;
            }

            holder.rotate();
            if (stitchSlot.putHolder(holder)) {
                return true;
            }

            holder.rotate();
        }

        return this.expandAtlas(holder);
    }

    private boolean expandAtlas(StitchHolder holder) {
        int min = Math.min(holder.getHeight(), holder.getWidth());
        boolean empty = this.currentWidth == 0 && this.currentHeight == 0;
        boolean invalid;
        if (this.forcePowerOf2) {
            int startWidth = this.getCeilPowerOf2(this.currentWidth);
            int startHeight = this.getCeilPowerOf2(this.currentHeight);
            int endWidth = this.getCeilPowerOf2(this.currentWidth + min);
            int endHeight = this.getCeilPowerOf2(this.currentHeight + min);
            boolean validWidth = endWidth <= this.maxWidth;
            boolean validHeight = endHeight <= this.maxHeight;
            if (!validWidth && !validHeight) {
                return false;
            }

            int max = Math.max(holder.getHeight(), holder.getWidth());
            if (empty && !validWidth && this.getCeilPowerOf2(this.currentHeight + max) > this.maxHeight) {
                return false;
            }

            boolean rescaleW = startWidth != endWidth;
            boolean rescaleH = startHeight != endHeight;
            if (rescaleW ^ rescaleH) {
                invalid = rescaleW && validWidth;
            } else {
                invalid = validWidth && startWidth <= startHeight;
            }
        } else {
            boolean validWidth = this.currentWidth + min <= this.maxWidth;
            boolean validHeight = this.currentHeight + min <= this.maxHeight;
            if (!validWidth && !validHeight) {
                return false;
            }

            invalid = (empty || this.currentWidth <= this.currentHeight) && validWidth;
        }

        StitchSlot slot;
        if (invalid) {
            if (holder.getWidth() > holder.getHeight()) {
                holder.rotate();
            }

            if (this.currentHeight == 0) {
                this.currentHeight = holder.getHeight();
            }

            slot = new StitchSlot(this.currentWidth, 0, holder.getWidth(), this.currentHeight);
            this.currentWidth = this.currentWidth + holder.getWidth();
        } else {
            slot = new StitchSlot(0, this.currentHeight, this.currentWidth, holder.getHeight());
            this.currentHeight = this.currentHeight + holder.getHeight();
        }

        slot.putHolder(holder);
        this.stitchSlots.add(slot);
        return true;
    }
}
