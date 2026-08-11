package net.minecraft.game.entity.hanging;

import com.mojang.nbt.tag.special.CompoundTag;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.misc.DroppedItem;
import net.minecraft.game.entity.mob.DamageSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.localization.Translator;
import net.minecraft.util.math.MathHelper;

public class Painting extends Entity {
    public EnumArt art;
    protected int tickCounter = 0;
    public int direction = 0;
    public int xPosition;
    public int yPosition;
    public int zPosition;
    public int meta;

    public Painting(World world) {
        super(world);
        this.yOffset = 0.0F;
        this.setSize(0.5F, 0.5F);
    }

    public Painting(World world, int x, int y, int z, int dir) {
        this(world);
        this.xPosition = x;
        this.yPosition = y;
        this.zPosition = z;
        ArrayList<EnumArt> artsList = new ArrayList<>();
        EnumArt[] arts = EnumArt.values();

        for (int i = 0; i < arts.length; i++) {
            EnumArt art = arts[i];
            this.art = art;
            this.setDirection(dir);
            if (this.onValidSurface()) {
                artsList.add(art);
            }
        }

        if (artsList.size() > 0) {
            this.meta = this.rand.nextInt(artsList.size());
            this.art = artsList.get(this.meta);
        }

        this.setDirection(dir);
    }

    public Painting(World world, int x, int y, int z, int dir, String kebab) {
        this(world);
        this.xPosition = x;
        this.yPosition = y;
        this.zPosition = z;
        EnumArt[] arts = EnumArt.values();

        for (int i = 0; i < arts.length; i++) {
            EnumArt art = arts[i];
            if (art.title.equals(kebab)) {
                this.art = art;
                break;
            }
        }

        this.setDirection(dir);
    }

    public Painting(World world, int xPosition, int yPosition, int zPosition, int dir, int metadata) {
        this(world);
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.zPosition = zPosition;
        ArrayList<EnumArt> artList = new ArrayList<>();
        EnumArt[] arts = EnumArt.values();

        for (int i = 0; i < arts.length; i++) {
            EnumArt art = arts[i];
            this.art = art;
            this.setDirection(dir);
            if (this.onValidSurface() || world.isOnline) {
                artList.add(art);
            }
        }

        if (artList.size() > 0) {
            try {
                if (metadata > 0) {
                    this.art = artList.get(metadata - 1);
                } else {
                    this.art = artList.get(this.rand.nextInt(artList.size()));
                }
            } catch (Exception ex) {
                Player player = world.getClosestPlayer(this, 8.0);
                player.addChatMessage("§c" + Translator.getInstance().translateKey("gui.painting.no"));
            }
        }

        this.setDirection(dir);
    }

    @Override
    protected void init() {
    }

    public void setDirection(int direction) {
        this.direction = direction;
        this.prevYaw = this.yaw = direction * 90;
        double offsetX = this.art.width;
        double offsetY = this.art.height;
        double offsetZ = this.art.width;
        if (direction != 0 && direction != 2) {
            offsetX = 0.5;
        } else {
            offsetZ = 0.5;
        }

        offsetX /= 32.0;
        offsetY /= 32.0;
        offsetZ /= 32.0;
        double x = this.xPosition + 0.5;
        double y = this.yPosition + 0.5;
        double z = this.zPosition + 0.5;
        double offset = 0.5625;
        if (direction == 0) {
            z -= offset;
        }

        if (direction == 1) {
            x -= offset;
        }

        if (direction == 2) {
            z += offset;
        }

        if (direction == 3) {
            x += offset;
        }

        if (direction == 0) {
            x -= this.getArtSize(this.art.width);
        }

        if (direction == 1) {
            z += this.getArtSize(this.art.width);
        }

        if (direction == 2) {
            x += this.getArtSize(this.art.width);
        }

        if (direction == 3) {
            z -= this.getArtSize(this.art.width);
        }

        y += this.getArtSize(this.art.height);
        this.setPosition(x, y, z);
        this.boundingBox.setBounds(x - offsetX, y - offsetY, z - offsetZ, x + offsetX, y + offsetY, z + offsetZ);
    }

    protected float getArtSize(int size) {
        return size == 32 ? 0.5F : (size == 64 ? 0.5F : 0.0F);
    }

    @Override
    public void onTick() {
        if (!this.onValidSurface()) {
            if (this.tickCounter++ >= 1 && !this.world.isOnline) {
                this.setDead();
                this.world.spawnEntity(new DroppedItem(this.world, this.posX, this.posY, this.posZ, new ItemStack(ItemList.painting)));
            }
        } else {
            this.tickCounter = 0;
        }
    }

    public boolean onValidSurface() {
        if (this.world.getBoundingBoxes(this, this.boundingBox).size() > 0) {
            return false;
        }

        int width = this.art.width / 16;
        int height = this.art.height / 16;
        int x = this.xPosition;
        int y = this.yPosition;
        int z = this.zPosition;
        if (this.direction == 0 || this.direction == 2) {
            x = MathHelper.floor_double(this.posX - this.art.width / 32.0);
        }

        if (this.direction == 1 || this.direction == 3) {
            z = MathHelper.floor_double(this.posZ - this.art.width / 32.0);
        }

        y = MathHelper.floor_double(this.posY - this.art.height / 32.0);

        for (int xOffset = 0; xOffset < width; xOffset++) {
            for (int zOffset = 0; zOffset < height; zOffset++) {
                Material material;
                if (this.direction != 0 && this.direction != 2) {
                    material = this.world.getBlockMaterial(this.xPosition, y + zOffset, z + xOffset);
                } else {
                    material = this.world.getBlockMaterial(x + xOffset, y + zOffset, this.zPosition);
                }

                if (!material.isSolid()) {
                    return false;
                }
            }
        }

        List<Entity> entities = this.world.getEntities(this, this.boundingBox);

        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i) instanceof Painting) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    @Override
    public boolean damageEntity(DamageSource src, int amount) {
        if (!this.isDead && !this.world.isOnline) {
            this.setDead();
            this.setAttacked();
            this.world.spawnEntity(new DroppedItem(this.world, this.posX, this.posY, this.posZ, new ItemStack(ItemList.painting)));
        }

        return true;
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        tag.setByte("Dir", (byte)this.direction);
        tag.setByte("Meta", (byte)this.meta);
        tag.setString("Motive", this.art.title);
        tag.setInteger("TileX", this.xPosition);
        tag.setInteger("TileY", this.yPosition);
        tag.setInteger("TileZ", this.zPosition);
    }

    @Override
    public void readFromNBT(CompoundTag tag) {
        this.direction = tag.getByte("Dir");
        this.meta = tag.getByte("Meta");
        this.xPosition = tag.getInteger("TileX");
        this.yPosition = tag.getInteger("TileY");
        this.zPosition = tag.getInteger("TileZ");
        String art = tag.getString("Motive");
        EnumArt[] arts = EnumArt.values();

        for (int i = 0; i < arts.length; i++) {
            EnumArt artE = arts[i];
            if (artE.title.equals(art)) {
                this.art = artE;
            }
        }

        if (this.art == null) {
            this.art = EnumArt.Kebab;
        }

        this.setDirection(this.direction);
    }
}
