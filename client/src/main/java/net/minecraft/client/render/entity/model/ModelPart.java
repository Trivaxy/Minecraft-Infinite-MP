package net.minecraft.client.render.entity.model;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.render.Renderer;
import net.minecraft.client.render.Tessellator;
import org.lwjgl.opengl.GL11;

public class ModelPart {
    public List<Cuboid> cuboids;
    public List<ModelPart> children;
    public float textureWidth;
    public float textureHeight;
    public float pivotX;
    public float pivotY;
    public float pivotZ;
    public float rotationX;
    public float rotationY;
    public float rotationZ;
    private int textureOffsetX;
    private int textureOffsetY;
    public int displayList;
    public boolean render;
    public boolean compiled = false;

    public ModelPart(Model model) {
        this.displayList = 0;
        this.render = true;
        this.cuboids = new ArrayList<>();
        this.setTextureSize(model.textureWidth, model.textureHeight);
        model.modelParts.add(this);
    }

    public ModelPart(Model model, int xOffs, int yOffs) {
        this(model);
        this.setTextureOffset(xOffs, yOffs);
    }

    public void addBox(float offX, float offY, float offZ, int width, int height, int depth, float scale, boolean mirrored) {
        this.cuboids.add(new Cuboid(this, this.textureOffsetX, this.textureOffsetY, offX, offY, offZ, width, height, depth, scale, mirrored));
    }

    public void addChild(ModelPart modelPart) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }

        this.children.add(modelPart);
    }

    public void setTextureOffset(int xOffs, int yOffs) {
        this.textureOffsetX = xOffs;
        this.textureOffsetY = yOffs;
    }

    public void setTextureSize(int textureWidth, int textureHeight) {
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    public void setPivots(float xRot, float yRot, float zRot) {
        this.pivotX = xRot;
        this.pivotY = yRot;
        this.pivotZ = zRot;
    }

    public void render(float deltaTime) {
        if (this.render) {
            if (!this.compiled) {
                this.compileDisplayList(deltaTime);
            }

            if (this.rotationX != 0.0F || this.rotationY != 0.0F || this.rotationZ != 0.0F) {
                GL11.glPushMatrix();
                GL11.glTranslatef(this.pivotX * deltaTime, this.pivotY * deltaTime, this.pivotZ * deltaTime);
                if (this.rotationZ != 0.0F) {
                    GL11.glRotatef(this.rotationZ * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
                }

                if (this.rotationY != 0.0F) {
                    GL11.glRotatef(this.rotationY * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
                }

                if (this.rotationX != 0.0F) {
                    GL11.glRotatef(this.rotationX * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
                }

                GL11.glCallList(this.displayList);
                if (this.children != null) {
                    for (int i = 0; i < this.children.size(); i++) {
                        this.children.get(i).render(deltaTime);
                    }
                }

                GL11.glPopMatrix();
            } else if (this.pivotX == 0.0F && this.pivotY == 0.0F && this.pivotZ == 0.0F) {
                GL11.glCallList(this.displayList);
                if (this.children != null) {
                    for (int i = 0; i < this.children.size(); i++) {
                        this.children.get(i).render(deltaTime);
                    }
                }
            } else {
                GL11.glTranslatef(this.pivotX * deltaTime, this.pivotY * deltaTime, this.pivotZ * deltaTime);
                GL11.glCallList(this.displayList);
                if (this.children != null) {
                    for (int i = 0; i < this.children.size(); i++) {
                        this.children.get(i).render(deltaTime);
                    }
                }

                GL11.glTranslatef(-this.pivotX * deltaTime, -this.pivotY * deltaTime, -this.pivotZ * deltaTime);
            }
        }
    }

    public void applyRotation(float deltaTime) {
        if (this.render) {
            if (!this.compiled) {
                this.compileDisplayList(deltaTime);
            }

            if (this.rotationX != 0.0F || this.rotationY != 0.0F || this.rotationZ != 0.0F) {
                GL11.glTranslatef(this.pivotX * deltaTime, this.pivotY * deltaTime, this.pivotZ * deltaTime);
                if (this.rotationZ != 0.0F) {
                    GL11.glRotatef(this.rotationZ * (180.0F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
                }

                if (this.rotationY != 0.0F) {
                    GL11.glRotatef(this.rotationY * (180.0F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
                }

                if (this.rotationX != 0.0F) {
                    GL11.glRotatef(this.rotationX * (180.0F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
                }
            } else if (this.pivotX != 0.0F || this.pivotY != 0.0F || this.pivotZ != 0.0F) {
                GL11.glTranslatef(this.pivotX * deltaTime, this.pivotY * deltaTime, this.pivotZ * deltaTime);
            }
        }
    }

    public void compileDisplayList(float deltaTime) {
        this.displayList = Renderer.generateDisplayLists(1);
        GL11.glNewList(this.displayList, 4864);
        Tessellator t = Tessellator.instance;

        for (int i = 0; i < this.cuboids.size(); i++) {
            this.cuboids.get(i).render(t, deltaTime);
        }

        GL11.glEndList();
        this.compiled = true;
    }
}
