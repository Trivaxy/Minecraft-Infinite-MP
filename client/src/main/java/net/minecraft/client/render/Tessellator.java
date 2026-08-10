package net.minecraft.client.render;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Comparator;
import java.util.PriorityQueue;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class Tessellator {
    public static final Tessellator instance = new Tessellator(2097152);
    private ByteBuffer byteBuffer;
    private IntBuffer intBuffer;
    private FloatBuffer floatBuffer;
    private ShortBuffer shortBuffer;
    private int[] rawBuffer;
    private int bufferIndex = 0;
    private int bufferSize;
    private int vertexCount = 0;
    private int brightness;
    private int color;
    private int normal;
    private int drawMode;
    private float textureU;
    private float textureV;
    private double xOffset;
    private double yOffset;
    private double zOffset;
    private boolean hasColor = false;
    private boolean hasTexture = false;
    private boolean hasBrightness = false;
    private boolean hasNormals = false;
    private boolean disableColors = false;
    private boolean isDrawing = false;

    private Tessellator(int size) {
        this.bufferSize = size;
        this.byteBuffer = Renderer.createByteBuffer(size * 4);
        this.intBuffer = this.byteBuffer.asIntBuffer();
        this.floatBuffer = this.byteBuffer.asFloatBuffer();
        this.shortBuffer = this.byteBuffer.asShortBuffer();
        this.rawBuffer = new int[size];
    }

    public void draw() {
        if (!this.isDrawing) {
            throw new IllegalStateException("Not tesselating!");
        }

        this.isDrawing = false;
        if (this.vertexCount > 0) {
            ((Buffer)this.intBuffer).clear();
            this.intBuffer.put(this.rawBuffer, 0, this.bufferIndex);
            ((Buffer)this.byteBuffer).position(0);
            ((Buffer)this.byteBuffer).limit(this.bufferIndex * 4);
            if (this.hasTexture) {
                ((Buffer)this.floatBuffer).position(3);
                GL11.glTexCoordPointer(2, 32, this.floatBuffer);
                GL11.glEnableClientState(32888);
            }

            if (this.hasBrightness) {
                GL13.glClientActiveTexture(33985);
                ((Buffer)this.shortBuffer).position(14);
                GL11.glTexCoordPointer(2, 32, this.shortBuffer);
                GL11.glEnableClientState(32888);
                GL13.glClientActiveTexture(33984);
            }

            if (this.hasColor) {
                ((Buffer)this.byteBuffer).position(20);
                GL11.glColorPointer(4, true, 32, this.byteBuffer);
                GL11.glEnableClientState(32886);
            }

            if (this.hasNormals) {
                ((Buffer)this.byteBuffer).position(24);
                GL11.glNormalPointer(32, this.byteBuffer);
                GL11.glEnableClientState(32885);
            }

            ((Buffer)this.floatBuffer).position(0);
            GL11.glVertexPointer(3, 32, this.floatBuffer);
            GL11.glEnableClientState(32884);
            GL11.glDrawArrays(this.drawMode, 0, this.vertexCount);
            GL11.glDisableClientState(32884);
            if (this.hasTexture) {
                GL11.glDisableClientState(32888);
            }

            if (this.hasBrightness) {
                GL13.glClientActiveTexture(33985);
                GL11.glDisableClientState(32888);
                GL13.glClientActiveTexture(33984);
            }

            if (this.hasColor) {
                GL11.glDisableClientState(32886);
            }

            if (this.hasNormals) {
                GL11.glDisableClientState(32885);
            }
        }

        this.flush();
    }

    private void flush() {
        this.vertexCount = 0;
        ((Buffer)this.byteBuffer).clear();
        this.bufferIndex = 0;
    }

    public void begin() {
        this.begin(7);
    }

    public void begin(int mode) {
        if (this.isDrawing) {
            throw new IllegalStateException("Already tesselating!");
        }

        this.isDrawing = true;
        this.flush();
        this.drawMode = mode;
        this.hasNormals = false;
        this.hasColor = false;
        this.hasTexture = false;
        this.hasBrightness = false;
        this.disableColors = false;
    }

    public void addTexturedVertex(double x, double y, double z, float u, float v) {
        this.setTextureUV(u, v);
        this.addVertex(x, y, z);
    }

    public void addVertex(double x, double y, double z) {
        if (this.hasTexture) {
            this.rawBuffer[this.bufferIndex + 3] = Float.floatToRawIntBits(this.textureU);
            this.rawBuffer[this.bufferIndex + 4] = Float.floatToRawIntBits(this.textureV);
        }

        if (this.hasBrightness) {
            this.rawBuffer[this.bufferIndex + 7] = this.brightness;
        }

        if (this.hasColor) {
            this.rawBuffer[this.bufferIndex + 5] = this.color;
        }

        if (this.hasNormals) {
            this.rawBuffer[this.bufferIndex + 6] = this.normal;
        }

        this.rawBuffer[this.bufferIndex + 0] = Float.floatToRawIntBits((float)(x + this.xOffset));
        this.rawBuffer[this.bufferIndex + 1] = Float.floatToRawIntBits((float)(y + this.yOffset));
        this.rawBuffer[this.bufferIndex + 2] = Float.floatToRawIntBits((float)(z + this.zOffset));
        this.bufferIndex += 8;
        this.vertexCount++;
        if (this.vertexCount % 4 == 0 && this.bufferIndex >= this.bufferSize - 32) {
            this.draw();
            this.isDrawing = true;
        }
    }

    public void addColoredVertex(double x, double y, double z, float red, float green, float blue, float alpha) {
        this.addVertex(x, y, z);
        this.setColorAlpha(red, green, blue, alpha);
    }

    public void setTextureUV(float u, float v) {
        this.hasTexture = true;
        this.textureU = u;
        this.textureV = v;
    }

    public void setBrightness(int brightness) {
        this.hasBrightness = true;
        this.brightness = brightness;
    }

    public void setNormals(float x, float y, float z) {
        this.hasNormals = true;
        byte red = (byte)(x * 127.0F);
        byte green = (byte)(y * 127.0F);
        byte blue = (byte)(z * 127.0F);
        this.normal = red & 255 | (green & 255) << 8 | (blue & 255) << 16;
    }

    public void setTranslation(double x, double y, double z) {
        this.xOffset = x;
        this.yOffset = y;
        this.zOffset = z;
    }

    public void addTranslation(float x, float y, float z) {
        this.xOffset += x;
        this.yOffset += y;
        this.zOffset += z;
    }

    public void setColorAlpha(int red, int green, int blue, int alpha) {
        if (!this.disableColors) {
            red = MathHelper.clamp(red, 0, 255);
            green = MathHelper.clamp(green, 0, 255);
            blue = MathHelper.clamp(blue, 0, 255);
            alpha = MathHelper.clamp(alpha, 0, 255);
            this.hasColor = true;
            this.color = alpha << 24 | blue << 16 | green << 8 | red;
        }
    }

    public void setColor(int red, int green, int blue) {
        this.setColorAlpha(red, green, blue, 255);
    }

    public void setColor(float red, float green, float blue) {
        this.setColor((int)(red * 255.0F), (int)(green * 255.0F), (int)(blue * 255.0F));
    }

    public void setColorAlpha(float red, float green, float blue, float alpha) {
        this.setColorAlpha((int)(red * 255.0F), (int)(green * 255.0F), (int)(blue * 255.0F), (int)(alpha * 255.0F));
    }

    public void setColorAlpha(int color, int opacity) {
        int red = color >> 16 & 0xFF;
        int green = color >> 8 & 0xFF;
        int blue = color & 0xFF;
        this.setColorAlpha(red, green, blue, opacity);
    }

    public void setColor(int color) {
        int red = color >> 16 & 0xFF;
        int green = color >> 8 & 0xFF;
        int blue = color & 0xFF;
        this.setColor(red, green, blue);
    }

    public void disableColor() {
        this.disableColors = true;
    }

    public Tessellator.VertexState getVertexState(float x, float y, float z) {
        if (this.bufferIndex < 1) {
            return null;
        }

        int[] newBuffer = new int[this.bufferIndex];
        PriorityQueue<Integer> queue = new PriorityQueue<>(
            this.bufferIndex, new Tessellator.QuadComparator(this.rawBuffer, x + (float)this.xOffset, y + (float)this.yOffset, z + (float)this.zOffset)
        );
        byte max = 32;

        for (int index = 0; index < this.bufferIndex; index += max) {
            queue.add(index);
        }

        for (int var10 = 0; !queue.isEmpty(); var10 += max) {
            int buffPos = queue.remove();

            for (int offset = 0; offset < max; offset++) {
                newBuffer[var10 + offset] = this.rawBuffer[buffPos + offset];
            }
        }

        System.arraycopy(newBuffer, 0, this.rawBuffer, 0, newBuffer.length);
        return new Tessellator.VertexState(newBuffer, this.bufferIndex, this.vertexCount, this.hasTexture, this.hasBrightness, this.hasNormals, this.hasColor);
    }

    public void setVertexState(Tessellator.VertexState state) {
        System.arraycopy(state.getRawBuffer(), 0, this.rawBuffer, 0, state.getRawBuffer().length);
        this.bufferIndex = state.getRawBufferIndex();
        this.vertexCount = state.getVertexCount();
        this.hasTexture = state.getHasTexture();
        this.hasBrightness = state.getHasBrightness();
        this.hasColor = state.getHasColor();
        this.hasNormals = state.getHasNormals();
    }

    public class QuadComparator implements Comparator<Integer> {
        private float cameraX;
        private float cameraY;
        private float cameraZ;
        private int[] buffer;

        public QuadComparator(int[] buffer, float x, float y, float z) {
            this.buffer = buffer;
            this.cameraX = x;
            this.cameraY = y;
            this.cameraZ = z;
        }

        public int compare(Integer currAngle, Integer newAngle) {
            float x1 = Float.intBitsToFloat(this.buffer[currAngle]) - this.cameraX;
            float y1 = Float.intBitsToFloat(this.buffer[currAngle + 1]) - this.cameraY;
            float z1 = Float.intBitsToFloat(this.buffer[currAngle + 2]) - this.cameraZ;
            float x2 = Float.intBitsToFloat(this.buffer[currAngle + 8]) - this.cameraX;
            float y2 = Float.intBitsToFloat(this.buffer[currAngle + 9]) - this.cameraY;
            float z2 = Float.intBitsToFloat(this.buffer[currAngle + 10]) - this.cameraZ;
            float x3 = Float.intBitsToFloat(this.buffer[currAngle + 16]) - this.cameraX;
            float y3 = Float.intBitsToFloat(this.buffer[currAngle + 17]) - this.cameraY;
            float z3 = Float.intBitsToFloat(this.buffer[currAngle + 18]) - this.cameraZ;
            float x4 = Float.intBitsToFloat(this.buffer[currAngle + 24]) - this.cameraX;
            float y4 = Float.intBitsToFloat(this.buffer[currAngle + 25]) - this.cameraY;
            float z4 = Float.intBitsToFloat(this.buffer[currAngle + 26]) - this.cameraZ;
            float x5 = Float.intBitsToFloat(this.buffer[newAngle]) - this.cameraX;
            float y5 = Float.intBitsToFloat(this.buffer[newAngle + 1]) - this.cameraY;
            float z5 = Float.intBitsToFloat(this.buffer[newAngle + 2]) - this.cameraZ;
            float x6 = Float.intBitsToFloat(this.buffer[newAngle + 8]) - this.cameraX;
            float y6 = Float.intBitsToFloat(this.buffer[newAngle + 9]) - this.cameraY;
            float z6 = Float.intBitsToFloat(this.buffer[newAngle + 10]) - this.cameraZ;
            float x7 = Float.intBitsToFloat(this.buffer[newAngle + 16]) - this.cameraX;
            float y7 = Float.intBitsToFloat(this.buffer[newAngle + 17]) - this.cameraY;
            float z7 = Float.intBitsToFloat(this.buffer[newAngle + 18]) - this.cameraZ;
            float x8 = Float.intBitsToFloat(this.buffer[newAngle + 24]) - this.cameraX;
            float y8 = Float.intBitsToFloat(this.buffer[newAngle + 25]) - this.cameraY;
            float z8 = Float.intBitsToFloat(this.buffer[newAngle + 26]) - this.cameraZ;
            float distX1 = (x1 + x2 + x3 + x4) * 0.25F;
            float distY1 = (y1 + y2 + y3 + y4) * 0.25F;
            float distZ1 = (z1 + z2 + z3 + z4) * 0.25F;
            float distX2 = (x5 + x6 + x7 + x8) * 0.25F;
            float distY2 = (y5 + y6 + y7 + y8) * 0.25F;
            float distZ2 = (z5 + z6 + z7 + z8) * 0.25F;
            float prevDist = distX1 * distX1 + distY1 * distY1 + distZ1 * distZ1;
            float newDist = distX2 * distX2 + distY2 * distY2 + distZ2 * distZ2;
            return Float.compare(newDist, prevDist);
        }
    }

    public class VertexState {
        private int[] buffer;
        private int bufferIndex;
        private int vertexCount;
        private boolean hasTexture;
        private boolean hasBrightness;
        private boolean hasNormals;
        private boolean hasColor;

        public VertexState(int[] buffer, int bufferIndex, int vertCount, boolean texture, boolean brightness, boolean normals, boolean color) {
            this.buffer = buffer;
            this.bufferIndex = bufferIndex;
            this.vertexCount = vertCount;
            this.hasTexture = texture;
            this.hasBrightness = brightness;
            this.hasNormals = normals;
            this.hasColor = color;
        }

        public int[] getRawBuffer() {
            return this.buffer;
        }

        public int getRawBufferIndex() {
            return this.bufferIndex;
        }

        public int getVertexCount() {
            return this.vertexCount;
        }

        public boolean getHasTexture() {
            return this.hasTexture;
        }

        public boolean getHasBrightness() {
            return this.hasBrightness;
        }

        public boolean getHasNormals() {
            return this.hasNormals;
        }

        public boolean getHasColor() {
            return this.hasColor;
        }

        public void addTessellatorVertexState(Tessellator.VertexState state) {
            if (state != null) {
                if (state.hasBrightness != this.hasBrightness
                    || state.hasColor != this.hasColor
                    || state.hasNormals != this.hasNormals
                    || state.hasTexture != this.hasTexture) {
                    throw new IllegalArgumentException("Incompatible vertex states");
                }

                int newIndex = this.bufferIndex + state.bufferIndex;
                int[] newBuffer = new int[newIndex];
                System.arraycopy(this.buffer, 0, newBuffer, 0, this.bufferIndex);
                System.arraycopy(state.buffer, 0, newBuffer, this.bufferIndex, state.bufferIndex);
                this.buffer = newBuffer;
                this.bufferIndex = newIndex;
                this.vertexCount = this.vertexCount + state.vertexCount;
            }
        }
    }
}
