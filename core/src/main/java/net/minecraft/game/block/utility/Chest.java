package net.minecraft.game.block.utility;

import net.minecraft.game.block.Block;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.block.tileentity.TileEntity;
import net.minecraft.game.block.tileentity.container.ChestEntity;
import net.minecraft.game.container.inventory.Inventory;
import net.minecraft.game.container.inventory.LargeChestInventory;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;
import net.minecraft.localization.Translator;
import net.minecraft.util.math.MathHelper;

public class Chest extends ContainerBlock {
    private Icon side;
    private Icon top;
    private Icon front;
    private Icon large_back_1;
    private Icon large_back_2;
    private Icon large_front_1;
    private Icon large_front_2;

    public Chest(int blockid) {
        super(blockid, MaterialList.wood);
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.CHEST;
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public boolean isRotatable(int id) {
        return true;
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        this.unifyAdjacentChests(world, x, y, z);
        int toLeft = world.getBlockId(x, y, z - 1);
        int toRight = world.getBlockId(x, y, z + 1);
        int toFront = world.getBlockId(x - 1, y, z);
        int toBack = world.getBlockId(x + 1, y, z);
        if (toLeft == this.id) {
            this.unifyAdjacentChests(world, x, y, z - 1);
        }

        if (toRight == this.id) {
            this.unifyAdjacentChests(world, x, y, z + 1);
        }

        if (toFront == this.id) {
            this.unifyAdjacentChests(world, x - 1, y, z);
        }

        if (toBack == this.id) {
            this.unifyAdjacentChests(world, x + 1, y, z);
        }
    }

    @Override
    public void onPlaced(World world, Player player, int x, int y, int z, int side) {
        super.onPlaced(world, player, x, y, z, side);
        int toLeft = world.getBlockId(x, y, z - 1);
        int toRight = world.getBlockId(x, y, z + 1);
        int toFront = world.getBlockId(x - 1, y, z);
        int toBack = world.getBlockId(x + 1, y, z);
        byte meta = 0;
        int facing = MathHelper.floor_double(player.yaw * 4.0F / 360.0F + 0.5) & 3;
        if (facing == 0) {
            meta = 2;
        }

        if (facing == 1) {
            meta = 5;
        }

        if (facing == 2) {
            meta = 3;
        }

        if (facing == 3) {
            meta = 4;
        }

        if (toLeft != this.id && toRight != this.id && toFront != this.id && toBack != this.id) {
            world.setBlockMetadataWithNotify(x, y, z, meta);
        } else {
            if ((toLeft == this.id || toRight == this.id) && (meta == 4 || meta == 5)) {
                if (toLeft == this.id) {
                    world.setBlockMetadataWithNotify(x, y, z - 1, meta);
                } else {
                    world.setBlockMetadataWithNotify(x, y, z + 1, meta);
                }

                world.setBlockMetadataWithNotify(x, y, z, meta);
            }

            if ((toFront == this.id || toBack == this.id) && (meta == 2 || meta == 3)) {
                if (toFront == this.id) {
                    world.setBlockMetadataWithNotify(x - 1, y, z, meta);
                } else {
                    world.setBlockMetadataWithNotify(x + 1, y, z, meta);
                }

                world.setBlockMetadataWithNotify(x, y, z, meta);
            }
        }
    }

    @Override
    public Icon getTexture(WorldSource world, int x, int y, int z, int meta) {
        if (meta != 1 && meta != 0) {
            int toLeft = world.getBlockId(x, y, z - 1);
            int toRight = world.getBlockId(x, y, z + 1);
            int toFront = world.getBlockId(x - 1, y, z);
            int toBack = world.getBlockId(x + 1, y, z);
            if (toLeft != this.id && toRight != this.id) {
                if (toFront != this.id && toBack != this.id) {
                    byte b14 = 3;
                    if (Block.isOpaque.get(toLeft) && !Block.isOpaque.get(toRight)) {
                        b14 = 3;
                    }

                    if (Block.isOpaque.get(toRight) && !Block.isOpaque.get(toLeft)) {
                        b14 = 2;
                    }

                    if (Block.isOpaque.get(toFront) && !Block.isOpaque.get(toBack)) {
                        b14 = 5;
                    }

                    if (Block.isOpaque.get(toBack) && !Block.isOpaque.get(toFront)) {
                        b14 = 4;
                    }

                    return meta == b14 ? this.front : this.side;
                }

                if (meta == 4 || meta == 5) {
                    return this.side;
                }

                int offset = 0;
                if (toFront == this.id) {
                    offset = -1;
                }

                int left = world.getBlockId(toFront == this.id ? x - 1 : x + 1, y, z - 1);
                int right = world.getBlockId(toFront == this.id ? x - 1 : x + 1, y, z + 1);
                if (meta == 3) {
                    offset = -1 - offset;
                }

                byte facing = 3;
                if (Block.isOpaque.get(toLeft) || Block.isOpaque.get(left) && !Block.isOpaque.get(toRight) && !Block.isOpaque.get(right)) {
                    facing = 3;
                }

                if (Block.isOpaque.get(toRight) || Block.isOpaque.get(right) && !Block.isOpaque.get(toLeft) && !Block.isOpaque.get(left)) {
                    facing = 2;
                }

                if (meta == facing) {
                    if (offset == -1) {
                        return this.large_front_1;
                    }

                    if (offset == 0) {
                        return this.large_front_2;
                    }
                } else {
                    if (offset == -1) {
                        return this.large_back_1;
                    }

                    if (offset == 0) {
                        return this.large_back_2;
                    }
                }
            } else if (meta != 2 && meta != 3) {
                int offset = 0;
                if (toLeft == this.id) {
                    offset = -1;
                }

                int left = world.getBlockId(x - 1, y, toLeft == this.id ? z - 1 : z + 1);
                int right = world.getBlockId(x + 1, y, toLeft == this.id ? z - 1 : z + 1);
                if (meta == 4) {
                    offset = -1 - offset;
                }

                byte facing = 5;
                if (Block.isOpaque.get(toFront) || Block.isOpaque.get(left) && !Block.isOpaque.get(toBack) && !Block.isOpaque.get(right)) {
                    facing = 5;
                }

                if (Block.isOpaque.get(toBack) || Block.isOpaque.get(right) && !Block.isOpaque.get(toFront) && !Block.isOpaque.get(left)) {
                    facing = 4;
                }

                if (meta == facing) {
                    if (offset == -1) {
                        return this.large_front_1;
                    }

                    if (offset == 0) {
                        return this.large_front_2;
                    }
                } else {
                    if (offset == -1) {
                        return this.large_back_1;
                    }

                    if (offset == 0) {
                        return this.large_back_2;
                    }
                }
            }

            return this.side;
        } else {
            return this.top;
        }
    }

    @Override
    public Icon getSideIcon(int side) {
        return side == 1 ? this.top : (side == 0 ? this.top : (side == 3 ? this.front : this.side));
    }

    @Override
    public Icon getIcon(int sideN, int i) {
        return sideN == 1 ? this.top : (sideN == 0 ? this.top : (sideN == 3 ? this.front : this.side));
    }

    @Override
    public boolean placeableAt(World world, int xCoord, int yCoord, int zCoord) {
        int side = 0;
        if (world.getBlockId(xCoord - 1, yCoord, zCoord) == this.id) {
            side++;
        }

        if (world.getBlockId(xCoord + 1, yCoord, zCoord) == this.id) {
            side++;
        }

        if (world.getBlockId(xCoord, yCoord, zCoord - 1) == this.id) {
            side++;
        }

        if (world.getBlockId(xCoord, yCoord, zCoord + 1) == this.id) {
            side++;
        }

        return side > 1
            ? false
            : (
                this.chestNextTo(world, xCoord - 1, yCoord, zCoord)
                    ? false
                    : (
                        this.chestNextTo(world, xCoord + 1, yCoord, zCoord)
                            ? false
                            : (this.chestNextTo(world, xCoord, yCoord, zCoord - 1) ? false : !this.chestNextTo(world, xCoord, yCoord, zCoord + 1))
                    )
            );
    }

    private boolean chestNextTo(World world, int xCoord, int yCoord, int zCoord) {
        return world.getBlockId(xCoord, yCoord, zCoord) != this.id
            ? false
            : (
                world.getBlockId(xCoord - 1, yCoord, zCoord) == this.id
                    ? true
                    : (
                        world.getBlockId(xCoord + 1, yCoord, zCoord) == this.id
                            ? true
                            : (world.getBlockId(xCoord, yCoord, zCoord - 1) == this.id ? true : world.getBlockId(xCoord, yCoord, zCoord + 1) == this.id)
                    )
            );
    }

    @Override
    public void onBlockRemoval(World world, int xCoord, int yCoord, int zCoord) {
        this.clearInventory(world, xCoord, yCoord, zCoord);
        super.onBlockRemoval(world, xCoord, yCoord, zCoord);
    }

    @Override
    public boolean blockActivated(World world, int xCoord, int yCoord, int zCoord, Player player) {
        Object tileEntity = world.getTileEntity(xCoord, yCoord, zCoord);
        if (world.isBlockFull(xCoord, yCoord + 1, zCoord)) {
            return true;
        }

        if (world.getBlockId(xCoord - 1, yCoord, zCoord) == this.id && world.isBlockFull(xCoord - 1, yCoord + 1, zCoord)) {
            return true;
        }

        if (world.getBlockId(xCoord + 1, yCoord, zCoord) == this.id && world.isBlockFull(xCoord + 1, yCoord + 1, zCoord)) {
            return true;
        }

        if (world.getBlockId(xCoord, yCoord, zCoord - 1) == this.id && world.isBlockFull(xCoord, yCoord + 1, zCoord - 1)) {
            return true;
        }

        if (world.getBlockId(xCoord, yCoord, zCoord + 1) == this.id && world.isBlockFull(xCoord, yCoord + 1, zCoord + 1)) {
            return true;
        }

        String inventoryName = Translator.getInstance().translateKey("container.chestDouble");
        if (world.getBlockId(xCoord - 1, yCoord, zCoord) == this.id) {
            tileEntity = new LargeChestInventory(inventoryName, (ChestEntity)world.getTileEntity(xCoord - 1, yCoord, zCoord), (Inventory)tileEntity);
        }

        if (world.getBlockId(xCoord + 1, yCoord, zCoord) == this.id) {
            tileEntity = new LargeChestInventory(inventoryName, (Inventory)tileEntity, (ChestEntity)world.getTileEntity(xCoord + 1, yCoord, zCoord));
        }

        if (world.getBlockId(xCoord, yCoord, zCoord - 1) == this.id) {
            tileEntity = new LargeChestInventory(inventoryName, (ChestEntity)world.getTileEntity(xCoord, yCoord, zCoord - 1), (Inventory)tileEntity);
        }

        if (world.getBlockId(xCoord, yCoord, zCoord + 1) == this.id) {
            tileEntity = new LargeChestInventory(inventoryName, (Inventory)tileEntity, (ChestEntity)world.getTileEntity(xCoord, yCoord, zCoord + 1));
        }

        if (!world.isOnline) {
            player.displayGUIChest((Inventory)tileEntity, false);
        }

        return true;
    }

    @Override
    public void registerIcons(IconRegister r) {
        this.side = r.registerIcon("workstations/chest_side");
        this.top = r.registerIcon("workstations/chest_top");
        this.front = r.registerIcon("workstations/chest_front");
        this.large_back_1 = r.registerIcon("workstations/largeChest_back_1");
        this.large_back_2 = r.registerIcon("workstations/largeChest_back_2");
        this.large_front_1 = r.registerIcon("workstations/largeChest_front_1");
        this.large_front_2 = r.registerIcon("workstations/largeChest_front_2");
        this.blockIcon = r.registerIcon("workstations/chest_side");
    }

    @Override
    public TileEntity getBlockEntity() {
        return new ChestEntity();
    }

    public void unifyAdjacentChests(World world, int x, int y, int z) {
        if (!world.isOnline) {
            int toLeft = world.getBlockId(x, y, z - 1);
            int toRight = world.getBlockId(x, y, z + 1);
            int toFront = world.getBlockId(x - 1, y, z);
            int toBack = world.getBlockId(x + 1, y, z);
            byte meta;
            if (toLeft == this.id || toRight == this.id) {
                int left = world.getBlockId(x - 1, y, toLeft == this.id ? z - 1 : z + 1);
                int right = world.getBlockId(x + 1, y, toLeft == this.id ? z - 1 : z + 1);
                meta = 5;
                int adjMeta;
                if (toLeft == this.id) {
                    adjMeta = world.getBlockMetadata(x, y, z - 1);
                } else {
                    adjMeta = world.getBlockMetadata(x, y, z + 1);
                }

                if (adjMeta == 4) {
                    meta = 4;
                }

                if ((Block.isOpaque.get(toFront) || Block.isOpaque.get(left)) && !Block.isOpaque.get(toBack) && !Block.isOpaque.get(right)) {
                    meta = 5;
                }

                if ((Block.isOpaque.get(toBack) || Block.isOpaque.get(right)) && !Block.isOpaque.get(toFront) && !Block.isOpaque.get(left)) {
                    meta = 4;
                }
            } else if (toFront != this.id && toBack != this.id) {
                meta = 3;
                if (Block.isOpaque.get(toLeft) && !Block.isOpaque.get(toRight)) {
                    meta = 3;
                }

                if (Block.isOpaque.get(toRight) && !Block.isOpaque.get(toLeft)) {
                    meta = 2;
                }

                if (Block.isOpaque.get(toFront) && !Block.isOpaque.get(toBack)) {
                    meta = 5;
                }

                if (Block.isOpaque.get(toBack) && !Block.isOpaque.get(toFront)) {
                    meta = 4;
                }
            } else {
                int left = world.getBlockId(toFront == this.id ? x - 1 : x + 1, y, z - 1);
                int right = world.getBlockId(toFront == this.id ? x - 1 : x + 1, y, z + 1);
                meta = 3;
                int adjMeta;
                if (toFront == this.id) {
                    adjMeta = world.getBlockMetadata(x - 1, y, z);
                } else {
                    adjMeta = world.getBlockMetadata(x + 1, y, z);
                }

                if (adjMeta == 2) {
                    meta = 2;
                }

                if ((Block.isOpaque.get(toLeft) || Block.isOpaque.get(left)) && !Block.isOpaque.get(toRight) && !Block.isOpaque.get(right)) {
                    meta = 3;
                }

                if ((Block.isOpaque.get(toRight) || Block.isOpaque.get(right)) && !Block.isOpaque.get(toLeft) && !Block.isOpaque.get(left)) {
                    meta = 2;
                }
            }

            world.setBlockMetadataWithNotify(x, y, z, meta);
        }
    }
}
