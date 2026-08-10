package net.minecraft.client.render.blocks.model.furniture;

import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.blocks.model.BlockModel;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.liquid.LiquidBlock;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.tileentity.other.CauldronEntity;
import net.minecraft.game.block.utility.Cauldron;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.texture.Icon;

public class CauldronModel extends BlockModel {
    private static final double[][] LEG_OFFSETS = new double[][]{{0.0, 0.0}, {0.75, 0.0}, {0.0, 0.75}, {0.75, 0.75}};

    @Override
    public boolean render(Block block, int x, int y, int z) {
        Tessellator tess = Tessellator.instance;
        Cauldron cauldron = (Cauldron)block;
        Icon sideIcon = cauldron.getSideIcon(2);
        float brightness = 0.6F;
        float legWidth = 0.25F;
        float legHeight = 0.1875F;
        boolean render = this.renderStandardBlock(cauldron, x, y, z);
        double offset = 0.125;
        this.applyColorCorrection(block, x, y, z, 0.6F);
        this.renderFace(cauldron, x - 1.0 + offset, y, z, sideIcon, BlockModel.Face.EAST, true);
        this.renderFace(cauldron, x + 1.0 - offset, y, z, sideIcon, BlockModel.Face.WEST, true);
        this.applyColorCorrection(block, x, y, z, 0.8F);
        this.renderFace(cauldron, x, y, z - 1.0 + offset, sideIcon, BlockModel.Face.SOUTH, true);
        this.renderFace(cauldron, x, y, z + 1.0 - offset, sideIcon, BlockModel.Face.NORTH, true);
        Icon inner = Cauldron.getIcon("cauldron_inner");
        this.applyColorCorrection(block, x, y, z, 1.0F);
        this.renderFace(cauldron, x, y - 1.0 + 0.25, z, inner, BlockModel.Face.TOP, true);
        this.renderFace(cauldron, x, y + 1.0 - 0.8125, z, inner, BlockModel.Face.BOTTOM, true);
        TileEntity tileEntity = this.world.getTileEntity(x, y, z);
        if (tileEntity != null && tileEntity instanceof CauldronEntity) {
            CauldronEntity cauldronEntity = (CauldronEntity)tileEntity;
            if (!cauldronEntity.hasLiquid()) {
                Icon liquidIcon = Cauldron.getIcon("potionjuice");
                int liquidLvl = cauldronEntity.getLiquidLevel();
                if (cauldronEntity.justWater()) {
                    this.applyColorCorrection(block, x, y, z, brightness);
                    liquidIcon = LiquidBlock.getIcon_static("water");
                } else {
                    int potionColor = EffectList.effects[cauldronEntity.getPotion() % 20].getLiquidColor();
                    float red = (potionColor >> 16 & 0xFF) / 255.0F;
                    float green = (potionColor >> 8 & 0xFF) / 255.0F;
                    float blue = (potionColor & 0xFF) / 255.0F;
                    tess.setBrightness(this.world.getBrightness(x, y, z, 0));
                    tess.setColor(brightness * red, brightness * green, brightness * blue);
                }

                this.renderFace(cauldron, x, y - 1.0 + (6.0 + liquidLvl * 3.0) / 16.0, z, liquidIcon, BlockModel.Face.TOP, true);
            }
        }

        Icon legs = cauldron.legs;

        for (int i = 0; i < LEG_OFFSETS.length; i++) {
            block.setBounds(
                (float)LEG_OFFSETS[i][0], 0.0, (float)LEG_OFFSETS[i][1], (float)LEG_OFFSETS[i][0] + legWidth, legHeight, (float)LEG_OFFSETS[i][1] + legWidth
            );
            if (!this.hasOverrideTexture()) {
                this.setOverrideTexture(legs);
            }

            this.flipTexture = i != 2;
            if (i != 2) {
                this.rotationEast = 2;
                this.rotationNorth = 2;
            } else {
                this.rotationNorth = 0;
                this.rotationEast = 0;
            }

            this.renderStandardBlock(block, x, y, z);
            this.renderFace(block, x, y, z, legs, BlockModel.Face.BOTTOM, true);
        }

        block.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        this.clearOverrideTexture();
        this.flipTexture = false;
        this.rotationEast = 0;
        this.rotationNorth = 0;
        return render;
    }
}
