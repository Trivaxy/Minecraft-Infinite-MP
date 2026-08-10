package net.minecraft.client.render.texture.stitcher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StitchSlot {
    private List<StitchSlot> subSlots;
    private StitchHolder holder;
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public StitchSlot(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    public StitchHolder getStitchHolder() {
        return this.holder;
    }

    public int getOriginX() {
        return this.x;
    }

    public int getOriginY() {
        return this.y;
    }

    public boolean putHolder(StitchHolder holder) {
        if (this.holder != null) {
            return false;
        }

        int width = holder.getWidth();
        int height = holder.getHeight();
        if (width <= this.width && height <= this.height) {
            if (width == this.width && height == this.height) {
                this.holder = holder;
            } else {
                if (this.subSlots == null) {
                    this.subSlots = new ArrayList<>(1);
                    this.subSlots.add(new StitchSlot(this.x, this.y, width, height));
                    int widthDifference = this.width - width;
                    int heightDifference = this.height - height;
                    if (heightDifference > 0 && widthDifference > 0) {
                        int maxHeight = Math.max(this.height, widthDifference);
                        int maxWidth = Math.max(this.width, heightDifference);
                        if (maxHeight >= maxWidth) {
                            this.subSlots.add(new StitchSlot(this.x, this.y + height, width, heightDifference));
                            this.subSlots.add(new StitchSlot(this.x + width, this.y, widthDifference, this.height));
                        } else {
                            this.subSlots.add(new StitchSlot(this.x + width, this.y, widthDifference, height));
                            this.subSlots.add(new StitchSlot(this.x, this.y + height, this.width, heightDifference));
                        }
                    } else if (widthDifference == 0) {
                        this.subSlots.add(new StitchSlot(this.x, this.y + height, width, heightDifference));
                    } else if (heightDifference == 0) {
                        this.subSlots.add(new StitchSlot(this.x + width, this.y, widthDifference, height));
                    }
                }

                Iterator<StitchSlot> slots = this.subSlots.iterator();

                StitchSlot slot;
                do {
                    if (!slots.hasNext()) {
                        return false;
                    }

                    slot = slots.next();
                } while (!slot.putHolder(holder));
            }

            return true;
        } else {
            return false;
        }
    }

    public void getAllSlots(List<StitchSlot> slots) {
        if (this.holder != null) {
            slots.add(this);
        } else if (this.subSlots != null) {
            for (StitchSlot var3 : this.subSlots) {
                var3.getAllSlots(slots);
            }
        }
    }

    @Override
    public String toString() {
        return "Slot{originX="
            + this.x
            + ", originY="
            + this.y
            + ", width="
            + this.width
            + ", height="
            + this.height
            + ", texture="
            + this.holder
            + ", subSlots="
            + this.subSlots
            + '}';
    }
}
