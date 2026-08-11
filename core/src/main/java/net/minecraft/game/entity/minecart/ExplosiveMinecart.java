package net.minecraft.game.entity.minecart;

import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;

public class ExplosiveMinecart extends Minecart {
    public ExplosiveMinecart(World world) {
        super(world);
        this.type = 3;
        this.drop = ItemList.minecartExplosive.id;
    }

    public ExplosiveMinecart(World world, double x, double y, double z) {
        super(world, x, y, z);
        this.type = 3;
        this.drop = ItemList.minecartExplosive.id;
    }

    @Override
    public boolean interact(Player player) {
        return false;
    }

    @Override
    protected void moveCart(double maxOffset) {
        super.moveCart(maxOffset);
        this.explodeOnContact();
    }

    @Override
    protected void moveCartOnTrack(int x, int y, int z, double maxOffset) {
        super.moveCartOnTrack(x, y, z, maxOffset);
        this.explodeOnContact();
    }

    private void explodeOnContact() {
        if (this.collidedHorizontally) {
            this.setDead();
            this.world.createExplosion(null, this.posX, this.posY, this.posZ, 4.0F);
        }
    }
}
