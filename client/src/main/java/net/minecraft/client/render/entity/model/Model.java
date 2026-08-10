package net.minecraft.client.render.entity.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.Mob;

public abstract class Model {
    public List<ModelPart> modelParts = new ArrayList<>();
    private Map<String, Model.TextureOffset> modelTextures = new HashMap<>();
    public int textureWidth = 64;
    public int textureHeight = 32;
    public float swingMain;
    public float swingOff;
    public boolean isChild = false;

    public void render(Entity entity, float limbSwing, float animSpeed, float angle, float yaw, float pitch, float tick) {
    }

    public void animate(float limbSwing, float animSpeed, float angle, float yaw, float pitch, float tick) {
    }

    public void animateSpecial(Mob entity, float swing, float prevSwing, float ticks) {
    }

    public void setRotation(ModelPart modelRenderer, float x, float y, float z) {
        modelRenderer.rotationX = x;
        modelRenderer.rotationY = y;
        modelRenderer.rotationZ = z;
    }

    protected void setTexture(String texName, int texX, int texY) {
        this.modelTextures.put(texName, new Model.TextureOffset(texX, texY));
    }

    public Model.TextureOffset getTexture(String string1) {
        return this.modelTextures.get(string1);
    }

    public ModelPart getRandomModelPart(Random rand) {
        return this.modelParts.get(rand.nextInt(this.modelParts.size()));
    }

    public class TextureOffset {
        public final int xPos;
        public final int yPos;

        public TextureOffset(int x, int y) {
            this.xPos = x;
            this.yPos = y;
        }
    }
}
