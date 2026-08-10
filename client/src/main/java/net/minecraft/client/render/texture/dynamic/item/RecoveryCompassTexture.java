package net.minecraft.client.render.texture.dynamic.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texture.dynamic.DynamicTexture;
import net.minecraft.client.render.texture.stitcher.StitchedTexture;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.World;
import net.minecraft.game.world.dimension.VoidDimension;
import net.minecraft.game.world.util.Position;
import net.minecraft.util.math.MathHelper;

public class RecoveryCompassTexture extends StitchedTexture implements DynamicTexture {
    public static RecoveryCompassTexture compassTexture;
    public double currentAngle;
    public double angleDelta;

    public RecoveryCompassTexture() {
        super("compassAmethyst");
        compassTexture = this;
    }

    @Override
    public void updateAnimation() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.world != null && mc.player != null) {
            this.updateCompass(mc.world, mc.player, mc.player.posX, mc.player.posZ, mc.player.yaw, false, false);
        } else {
            this.updateCompass(null, null, 0.0, 0.0, 0.0, true, false);
        }
    }

    public void updateCompass(World world, Player player, double x, double z, double facing, boolean update, boolean stop) {
        double offset = 0.0;
        if (world != null && !update) {
            Position deathLoc = player.getDeathCoordinate();
            if (world.currDim instanceof VoidDimension) {
                facing %= 360.0;
                offset = -((facing - 90.0) * Math.PI / 180.0 - Math.atan2(-z, -x));
            } else if (deathLoc != null) {
                double distX = deathLoc.x - x;
                double distZ = deathLoc.z - z;
                facing %= 360.0;
                offset = -((facing - 90.0) * Math.PI / 180.0 - Math.atan2(distZ, distX));
            } else {
                offset = Math.random() * (float) Math.PI * 2.0;
            }
        }

        if (stop) {
            this.currentAngle = offset;
        } else {
            double angle = offset - this.currentAngle;

            while (angle < -Math.PI) {
                angle += Math.PI * 2;
            }

            while (angle >= Math.PI) {
                angle -= Math.PI * 2;
            }

            angle = MathHelper.clamp(angle, -1.0, 1.0);
            this.angleDelta += angle * 0.1;
            this.angleDelta *= 0.8;
            this.currentAngle = this.currentAngle + this.angleDelta;
        }

        int frame = (int)((this.currentAngle / (Math.PI * 2) + 1.0) * this.textures.size()) % this.textures.size();

        while (frame < 0) {
            frame = (frame + this.textures.size()) % this.textures.size();
        }

        if (frame != this.frames) {
            this.frames = frame;
            this.textureSheet.copyFrom(this.x, this.y, this.textures.get(this.frames), this.rotated);
        }
    }
}
