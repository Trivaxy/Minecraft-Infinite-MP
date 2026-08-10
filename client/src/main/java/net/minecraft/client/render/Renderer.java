package net.minecraft.client.render;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL11;

public class Renderer {
    private static IntArrayList displayLists = new IntArrayList();
    private static IntArrayList textureNames = new IntArrayList();

    public static synchronized int generateDisplayLists(int listID) {
        int id = GL11.glGenLists(listID);
        displayLists.add(id);
        displayLists.add(listID);
        return id;
    }

    public static synchronized void generateDisplayLists(IntBuffer buffer) {
        GL11.glGenTextures(buffer);

        for (int id = buffer.position(); id < buffer.limit(); id++) {
            textureNames.add(buffer.get(id));
        }
    }

    public static synchronized void clearDisplayLists() {
        for (int index = 0; index < displayLists.size(); index += 2) {
            GL11.glDeleteLists(displayLists.getInt(index), displayLists.getInt(index + 1));
        }

        IntBuffer texBuffer = createIntBuffer(textureNames.size());
        ((Buffer)texBuffer).flip();
        GL11.glDeleteTextures(texBuffer);

        for (int var2 = 0; var2 < textureNames.size(); var2++) {
            texBuffer.put(textureNames.getInt(var2));
        }

        ((Buffer)texBuffer).flip();
        GL11.glDeleteTextures(texBuffer);
        displayLists.clear();
        textureNames.clear();
    }

    public static synchronized ByteBuffer createByteBuffer(int bufferID) {
        return ByteBuffer.allocateDirect(bufferID).order(ByteOrder.nativeOrder());
    }

    public static IntBuffer createIntBuffer(int index) {
        return createByteBuffer(index << 2).asIntBuffer();
    }

    public static FloatBuffer createFloatBuffer(int index) {
        return createByteBuffer(index << 2).asFloatBuffer();
    }
}
