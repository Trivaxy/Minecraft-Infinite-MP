package net.minecraft.client.render.entity.model;

import net.minecraft.client.render.Tessellator;
import net.minecraft.game.physics.Vector;

public class Cuboid {
    private Cuboid.Vertex[] vertexPositions;
    private Cuboid.Quad[] quadList;
    public final float posX1;
    public final float posY1;
    public final float posZ1;
    public final float posX2;
    public final float posY2;
    public final float posZ2;

    public Cuboid(ModelPart model, int texU, int texV, float xOff, float yOff, float zOff, int width, int height, int depth, float scaleFac, boolean mirror) {
        this.posX1 = xOff;
        this.posY1 = yOff;
        this.posZ1 = zOff;
        this.posX2 = xOff + width;
        this.posY2 = yOff + height;
        this.posZ2 = zOff + depth;
        this.vertexPositions = new Cuboid.Vertex[8];
        this.quadList = new Cuboid.Quad[6];
        float xxo = xOff + width;
        float yyo = yOff + height;
        float zzo = zOff + depth;
        xOff -= scaleFac;
        yOff -= scaleFac;
        zOff -= scaleFac;
        xxo += scaleFac;
        yyo += scaleFac;
        zzo += scaleFac;
        if (mirror) {
            float backup = xxo;
            xxo = xOff;
            xOff = backup;
        }

        Cuboid.Vertex vert1 = new Cuboid.Vertex(xOff, yOff, zOff, 0.0F, 0.0F);
        Cuboid.Vertex vert2 = new Cuboid.Vertex(xxo, yOff, zOff, 0.0F, 8.0F);
        Cuboid.Vertex vert3 = new Cuboid.Vertex(xxo, yyo, zOff, 8.0F, 8.0F);
        Cuboid.Vertex vert4 = new Cuboid.Vertex(xOff, yyo, zOff, 8.0F, 0.0F);
        Cuboid.Vertex vert5 = new Cuboid.Vertex(xOff, yOff, zzo, 0.0F, 0.0F);
        Cuboid.Vertex vert6 = new Cuboid.Vertex(xxo, yOff, zzo, 0.0F, 8.0F);
        Cuboid.Vertex vert7 = new Cuboid.Vertex(xxo, yyo, zzo, 8.0F, 8.0F);
        Cuboid.Vertex vert8 = new Cuboid.Vertex(xOff, yyo, zzo, 8.0F, 0.0F);
        this.vertexPositions[0] = vert1;
        this.vertexPositions[1] = vert2;
        this.vertexPositions[2] = vert3;
        this.vertexPositions[3] = vert4;
        this.vertexPositions[4] = vert5;
        this.vertexPositions[5] = vert6;
        this.vertexPositions[6] = vert7;
        this.vertexPositions[7] = vert8;
        this.quadList[0] = new Cuboid.Quad(
            new Cuboid.Vertex[]{vert6, vert2, vert3, vert7},
            texU + depth + width,
            texV + depth,
            texU + depth + width + depth,
            texV + depth + height,
            model.textureWidth,
            model.textureHeight
        );
        this.quadList[1] = new Cuboid.Quad(
            new Cuboid.Vertex[]{vert1, vert5, vert8, vert4}, texU, texV + depth, texU + depth, texV + depth + height, model.textureWidth, model.textureHeight
        );
        this.quadList[2] = new Cuboid.Quad(
            new Cuboid.Vertex[]{vert6, vert5, vert1, vert2}, texU + depth, texV, texU + depth + width, texV + depth, model.textureWidth, model.textureHeight
        );
        this.quadList[3] = new Cuboid.Quad(
            new Cuboid.Vertex[]{vert3, vert4, vert8, vert7},
            texU + depth + width,
            texV + depth,
            texU + depth + width + width,
            texV,
            model.textureWidth,
            model.textureHeight
        );
        this.quadList[4] = new Cuboid.Quad(
            new Cuboid.Vertex[]{vert2, vert1, vert4, vert3},
            texU + depth,
            texV + depth,
            texU + depth + width,
            texV + depth + height,
            model.textureWidth,
            model.textureHeight
        );
        this.quadList[5] = new Cuboid.Quad(
            new Cuboid.Vertex[]{vert5, vert6, vert7, vert8},
            texU + depth + width + depth,
            texV + depth,
            texU + depth + width + depth + width,
            texV + depth + height,
            model.textureWidth,
            model.textureHeight
        );
        if (mirror) {
            for (int i = 0; i < this.quadList.length; i++) {
                this.quadList[i].flipFace();
            }
        }
    }

    public void render(Tessellator t, float deltaTime) {
        for (int i = 0; i < this.quadList.length; i++) {
            this.quadList[i].draw(t, deltaTime);
        }
    }

    public class Quad {
        public Cuboid.Vertex[] vertices;

        public Quad(Cuboid.Vertex[] vertices) {
            this.vertices = vertices;
        }

        public Quad(Cuboid.Vertex[] vertices, int startX, int startY, int endX, int endY, float width, float height) {
            this(vertices);
            vertices[0] = vertices[0].setTexturePosition(endX / width, startY / height);
            vertices[1] = vertices[1].setTexturePosition(startX / width, startY / height);
            vertices[2] = vertices[2].setTexturePosition(startX / width, endY / height);
            vertices[3] = vertices[3].setTexturePosition(endX / width, endY / height);
        }

        public void flipFace() {
            Cuboid.Vertex[] vertices = new Cuboid.Vertex[this.vertices.length];

            for (int vertex = 0; vertex < this.vertices.length; vertex++) {
                vertices[vertex] = this.vertices[this.vertices.length - vertex - 1];
            }

            this.vertices = vertices;
        }

        public void draw(Tessellator tess, float ticks) {
            Vector vec3D3 = this.vertices[1].vector3D.subtract(this.vertices[0].vector3D);
            Vector vec3D4 = this.vertices[1].vector3D.subtract(this.vertices[2].vector3D);
            Vector vec3D5 = vec3D4.crossProduct(vec3D3).normalize();
            tess.begin();
            tess.setNormals((float)vec3D5.xCoord, (float)vec3D5.yCoord, (float)vec3D5.zCoord);

            for (int index = 0; index < 4; index++) {
                Cuboid.Vertex vert = this.vertices[index];
                tess.addTexturedVertex(vert.vector3D.xCoord * ticks, vert.vector3D.yCoord * ticks, vert.vector3D.zCoord * ticks, vert.texU, vert.texV);
            }

            tess.draw();
        }
    }

    private class Vertex {
        public Vector vector3D;
        public float texU;
        public float texV;

        public Vertex(float x, float y, float z, float u, float v) {
            this(Vector.createVector(x, y, z), u, v);
        }

        public Cuboid.Vertex setTexturePosition(float u, float v) {
            return Cuboid.this.new Vertex(this, u, v);
        }

        public Vertex(Cuboid.Vertex vert, float u, float v) {
            this.vector3D = vert.vector3D;
            this.texU = u;
            this.texV = v;
        }

        public Vertex(Vector xyz, float u, float v) {
            this.vector3D = xyz;
            this.texU = u;
            this.texV = v;
        }
    }
}
