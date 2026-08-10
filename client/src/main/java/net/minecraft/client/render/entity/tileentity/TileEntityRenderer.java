package net.minecraft.client.render.entity.tileentity;

import net.minecraft.client.render.texture.TextureLoader;
import net.minecraft.game.block.tileentity.TileEntity;

public abstract class TileEntityRenderer<T extends TileEntity> {
    protected TileEntityRenderManager renderManager;

    public abstract void render(T var1, double var2, double var4, double var6, float var8);

    protected void bindTexture(String tex) {
        TextureLoader loader = this.renderManager.texLoader;
        loader.bindTexture(loader.getTexture(tex));
    }

    public void setManager(TileEntityRenderManager TEManager) {
        this.renderManager = TEManager;
    }
}
