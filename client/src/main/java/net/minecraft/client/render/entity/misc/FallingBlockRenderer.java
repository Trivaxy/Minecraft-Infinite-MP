package net.minecraft.client.render.entity.misc;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.BlocksRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.utility.Anvil;
import net.minecraft.game.entity.misc.FallingBlock;
import net.minecraft.game.world.World;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class FallingBlockRenderer extends EntityRenderer<FallingBlock> {
    private BlocksRenderer sandRenderBlocks = new BlocksRenderer();

    public FallingBlockRenderer() {
        this.setShadowSize(0.5F);
    }

    public void render(FallingBlock sand, double x, double y, double z, float yaw, float pitch) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, (float)z);
        this.loadTexture("/terrain.png");
        Block block = BlockList.blocks[sand.blockID];
        World level = sand.world;
        GL11.glDisable(2896);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        if (block instanceof Anvil && block.getRenderType() == Block.RenderType.ANVIL) {
            this.sandRenderBlocks.setWorldInstance(level);
            Tessellator desolator = Tessellator.instance;
            desolator.begin();
            desolator.setTranslation(
                -MathHelper.floor_double(sand.posX) - 0.5, -MathHelper.floor_double(sand.posY) - 0.5, -MathHelper.floor_double(sand.posZ) - 0.5
            );
            BlocksRenderer.anvilModel
                .renderAnvil(
                    (Anvil)block,
                    MathHelper.floor_double(sand.posX),
                    MathHelper.floor_double(sand.posY),
                    MathHelper.floor_double(sand.posZ),
                    sand.metadata,
                    false,
                    true
                );
            desolator.setTranslation(0.0, 0.0, 0.0);
            desolator.draw();
        } else {
            this.sandRenderBlocks
                .renderMovingBlock(
                    block, level, MathHelper.floor_double(sand.posX), MathHelper.floor_double(sand.posY), MathHelper.floor_double(sand.posZ), sand.metadata
                );
        }

        GL11.glEnable(2896);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }
}
