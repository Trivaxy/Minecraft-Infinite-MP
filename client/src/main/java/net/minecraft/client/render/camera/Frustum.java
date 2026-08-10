package net.minecraft.client.render.camera;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import net.minecraft.client.render.Renderer;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class Frustum {
    private static Frustum instance = new Frustum();
    private FloatBuffer projectionMatrixBuffer = Renderer.createFloatBuffer(16);
    private FloatBuffer modelviewMatrixBuffer = Renderer.createFloatBuffer(16);
    private FloatBuffer floatBuffer = Renderer.createFloatBuffer(16);
    public float[][] frustum = new float[16][16];
    public float[] projectionMatrix = new float[16];
    public float[] modelviewMatrix = new float[16];
    public float[] clippingMatrix = new float[16];

    public static Frustum getInstance() {
        instance.init();
        return instance;
    }

    public boolean isInFrustum(double startX, double startY, double startZ, double endX, double endY, double endZ) {
        for (int face = 0; face < 6; face++) {
            if (this.frustum[face][0] * startX + this.frustum[face][1] * startY + this.frustum[face][2] * startZ + this.frustum[face][3] <= 0.0
                && this.frustum[face][0] * endX + this.frustum[face][1] * startY + this.frustum[face][2] * startZ + this.frustum[face][3] <= 0.0
                && this.frustum[face][0] * startX + this.frustum[face][1] * endY + this.frustum[face][2] * startZ + this.frustum[face][3] <= 0.0
                && this.frustum[face][0] * endX + this.frustum[face][1] * endY + this.frustum[face][2] * startZ + this.frustum[face][3] <= 0.0
                && this.frustum[face][0] * startX + this.frustum[face][1] * startY + this.frustum[face][2] * endZ + this.frustum[face][3] <= 0.0
                && this.frustum[face][0] * endX + this.frustum[face][1] * startY + this.frustum[face][2] * endZ + this.frustum[face][3] <= 0.0
                && this.frustum[face][0] * startX + this.frustum[face][1] * endY + this.frustum[face][2] * endZ + this.frustum[face][3] <= 0.0
                && this.frustum[face][0] * endX + this.frustum[face][1] * endY + this.frustum[face][2] * endZ + this.frustum[face][3] <= 0.0) {
                return false;
            }
        }

        return true;
    }

    private void normalize(float[][] uv, int index) {
        float offset = MathHelper.sqrt_float(uv[index][0] * uv[index][0] + uv[index][1] * uv[index][1] + uv[index][2] * uv[index][2]);
        uv[index][0] = uv[index][0] / offset;
        uv[index][1] = uv[index][1] / offset;
        uv[index][2] = uv[index][2] / offset;
        uv[index][3] = uv[index][3] / offset;
    }

    private void init() {
        ((Buffer)this.projectionMatrixBuffer).clear();
        ((Buffer)this.modelviewMatrixBuffer).clear();
        ((Buffer)this.floatBuffer).clear();
        GL11.glGetFloat(2983, this.projectionMatrixBuffer);
        GL11.glGetFloat(2982, this.modelviewMatrixBuffer);
        ((Buffer)this.projectionMatrixBuffer).flip().limit(16);
        this.projectionMatrixBuffer.get(this.projectionMatrix);
        ((Buffer)this.modelviewMatrixBuffer).flip().limit(16);
        this.modelviewMatrixBuffer.get(this.modelviewMatrix);
        this.clippingMatrix[0] = this.modelviewMatrix[0] * this.projectionMatrix[0]
            + this.modelviewMatrix[1] * this.projectionMatrix[4]
            + this.modelviewMatrix[2] * this.projectionMatrix[8]
            + this.modelviewMatrix[3] * this.projectionMatrix[12];
        this.clippingMatrix[1] = this.modelviewMatrix[0] * this.projectionMatrix[1]
            + this.modelviewMatrix[1] * this.projectionMatrix[5]
            + this.modelviewMatrix[2] * this.projectionMatrix[9]
            + this.modelviewMatrix[3] * this.projectionMatrix[13];
        this.clippingMatrix[2] = this.modelviewMatrix[0] * this.projectionMatrix[2]
            + this.modelviewMatrix[1] * this.projectionMatrix[6]
            + this.modelviewMatrix[2] * this.projectionMatrix[10]
            + this.modelviewMatrix[3] * this.projectionMatrix[14];
        this.clippingMatrix[3] = this.modelviewMatrix[0] * this.projectionMatrix[3]
            + this.modelviewMatrix[1] * this.projectionMatrix[7]
            + this.modelviewMatrix[2] * this.projectionMatrix[11]
            + this.modelviewMatrix[3] * this.projectionMatrix[15];
        this.clippingMatrix[4] = this.modelviewMatrix[4] * this.projectionMatrix[0]
            + this.modelviewMatrix[5] * this.projectionMatrix[4]
            + this.modelviewMatrix[6] * this.projectionMatrix[8]
            + this.modelviewMatrix[7] * this.projectionMatrix[12];
        this.clippingMatrix[5] = this.modelviewMatrix[4] * this.projectionMatrix[1]
            + this.modelviewMatrix[5] * this.projectionMatrix[5]
            + this.modelviewMatrix[6] * this.projectionMatrix[9]
            + this.modelviewMatrix[7] * this.projectionMatrix[13];
        this.clippingMatrix[6] = this.modelviewMatrix[4] * this.projectionMatrix[2]
            + this.modelviewMatrix[5] * this.projectionMatrix[6]
            + this.modelviewMatrix[6] * this.projectionMatrix[10]
            + this.modelviewMatrix[7] * this.projectionMatrix[14];
        this.clippingMatrix[7] = this.modelviewMatrix[4] * this.projectionMatrix[3]
            + this.modelviewMatrix[5] * this.projectionMatrix[7]
            + this.modelviewMatrix[6] * this.projectionMatrix[11]
            + this.modelviewMatrix[7] * this.projectionMatrix[15];
        this.clippingMatrix[8] = this.modelviewMatrix[8] * this.projectionMatrix[0]
            + this.modelviewMatrix[9] * this.projectionMatrix[4]
            + this.modelviewMatrix[10] * this.projectionMatrix[8]
            + this.modelviewMatrix[11] * this.projectionMatrix[12];
        this.clippingMatrix[9] = this.modelviewMatrix[8] * this.projectionMatrix[1]
            + this.modelviewMatrix[9] * this.projectionMatrix[5]
            + this.modelviewMatrix[10] * this.projectionMatrix[9]
            + this.modelviewMatrix[11] * this.projectionMatrix[13];
        this.clippingMatrix[10] = this.modelviewMatrix[8] * this.projectionMatrix[2]
            + this.modelviewMatrix[9] * this.projectionMatrix[6]
            + this.modelviewMatrix[10] * this.projectionMatrix[10]
            + this.modelviewMatrix[11] * this.projectionMatrix[14];
        this.clippingMatrix[11] = this.modelviewMatrix[8] * this.projectionMatrix[3]
            + this.modelviewMatrix[9] * this.projectionMatrix[7]
            + this.modelviewMatrix[10] * this.projectionMatrix[11]
            + this.modelviewMatrix[11] * this.projectionMatrix[15];
        this.clippingMatrix[12] = this.modelviewMatrix[12] * this.projectionMatrix[0]
            + this.modelviewMatrix[13] * this.projectionMatrix[4]
            + this.modelviewMatrix[14] * this.projectionMatrix[8]
            + this.modelviewMatrix[15] * this.projectionMatrix[12];
        this.clippingMatrix[13] = this.modelviewMatrix[12] * this.projectionMatrix[1]
            + this.modelviewMatrix[13] * this.projectionMatrix[5]
            + this.modelviewMatrix[14] * this.projectionMatrix[9]
            + this.modelviewMatrix[15] * this.projectionMatrix[13];
        this.clippingMatrix[14] = this.modelviewMatrix[12] * this.projectionMatrix[2]
            + this.modelviewMatrix[13] * this.projectionMatrix[6]
            + this.modelviewMatrix[14] * this.projectionMatrix[10]
            + this.modelviewMatrix[15] * this.projectionMatrix[14];
        this.clippingMatrix[15] = this.modelviewMatrix[12] * this.projectionMatrix[3]
            + this.modelviewMatrix[13] * this.projectionMatrix[7]
            + this.modelviewMatrix[14] * this.projectionMatrix[11]
            + this.modelviewMatrix[15] * this.projectionMatrix[15];
        this.frustum[0][0] = this.clippingMatrix[3] - this.clippingMatrix[0];
        this.frustum[0][1] = this.clippingMatrix[7] - this.clippingMatrix[4];
        this.frustum[0][2] = this.clippingMatrix[11] - this.clippingMatrix[8];
        this.frustum[0][3] = this.clippingMatrix[15] - this.clippingMatrix[12];
        this.normalize(this.frustum, 0);
        this.frustum[1][0] = this.clippingMatrix[3] + this.clippingMatrix[0];
        this.frustum[1][1] = this.clippingMatrix[7] + this.clippingMatrix[4];
        this.frustum[1][2] = this.clippingMatrix[11] + this.clippingMatrix[8];
        this.frustum[1][3] = this.clippingMatrix[15] + this.clippingMatrix[12];
        this.normalize(this.frustum, 1);
        this.frustum[2][0] = this.clippingMatrix[3] + this.clippingMatrix[1];
        this.frustum[2][1] = this.clippingMatrix[7] + this.clippingMatrix[5];
        this.frustum[2][2] = this.clippingMatrix[11] + this.clippingMatrix[9];
        this.frustum[2][3] = this.clippingMatrix[15] + this.clippingMatrix[13];
        this.normalize(this.frustum, 2);
        this.frustum[3][0] = this.clippingMatrix[3] - this.clippingMatrix[1];
        this.frustum[3][1] = this.clippingMatrix[7] - this.clippingMatrix[5];
        this.frustum[3][2] = this.clippingMatrix[11] - this.clippingMatrix[9];
        this.frustum[3][3] = this.clippingMatrix[15] - this.clippingMatrix[13];
        this.normalize(this.frustum, 3);
        this.frustum[4][0] = this.clippingMatrix[3] - this.clippingMatrix[2];
        this.frustum[4][1] = this.clippingMatrix[7] - this.clippingMatrix[6];
        this.frustum[4][2] = this.clippingMatrix[11] - this.clippingMatrix[10];
        this.frustum[4][3] = this.clippingMatrix[15] - this.clippingMatrix[14];
        this.normalize(this.frustum, 4);
        this.frustum[5][0] = this.clippingMatrix[3] + this.clippingMatrix[2];
        this.frustum[5][1] = this.clippingMatrix[7] + this.clippingMatrix[6];
        this.frustum[5][2] = this.clippingMatrix[11] + this.clippingMatrix[10];
        this.frustum[5][3] = this.clippingMatrix[15] + this.clippingMatrix[14];
        this.normalize(this.frustum, 5);
    }
}
