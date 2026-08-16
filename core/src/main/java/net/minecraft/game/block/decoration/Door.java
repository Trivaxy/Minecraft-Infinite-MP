package net.minecraft.game.block.decoration;

import java.util.Random;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconFlipped;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.WorldSource;
import net.minecraft.statistics.StatList;

public class Door extends Block {
    private static final String[] WOOD = new String[]{
        "doorWood_lower",
        "doorWood_upper",
        "doorIron_lower",
        "doorIron_upper",
        "doorGlass_lower",
        "doorGlass_upper",
        "doorBirch_lower",
        "doorBirch_upper",
        "doorSpruce_lower",
        "doorSpruce_upper",
        "doorPalm_lower",
        "doorPalm_upper",
        "doorAcacia_lower",
        "doorAcacia_upper",
        "doorJungle_lower",
        "doorJungle_upper",
        "doorMangrove_lower",
        "doorMangrove_upper",
        "doorBaobab_lower",
        "doorBaobab_upper",
        "doorScorched_lower",
        "doorScorched_upper",
        "doorMaple_lower",
        "doorMaple_upper",
        "doorCherry_lower",
        "doorCherry_upper",
        "doorMahogany_lower",
        "doorMahogany_upper",
        "doorOldenOak_lower",
        "doorOldenOak_upper",
        "doorCrimson_lower",
        "doorCrimson_upper",
        "doorNylium_lower",
        "doorNylium_upper",
        "doorPurpur_lower",
        "doorPurpur_upper",
        "doorWillow_lower",
        "doorWillow_upper",
        "doorLime_lower",
        "doorLime_upper"
    };
    private int doorType = 0;
    private Icon[] icons;
    private int dropID;

    public Door(int blockid, int drop, int icon, Material mat) {
        super(blockid, mat);
        this.doorType = icon;
        this.dropID = drop;
        this.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        this.enableStats = false;
        this.silkTouch = false;
    }

    @Override
    public Icon getIcon(int side, int meta) {
        return this.icons[this.doorType];
    }

    @Override
    public void registerIcons(IconRegister ir) {
        this.icons = new Icon[WOOD.length * 2];

        for (int index = 0; index < WOOD.length; index++) {
            this.icons[index] = ir.registerIcon("doors/" + WOOD[index]);
            this.icons[index + WOOD.length] = new IconFlipped(this.icons[index], true, false);
        }
    }

    @Override
    public boolean renderFace(WorldSource world, int x, int y, int z, int side) {
        int id = world.getBlockId(x, y, z);
        return id != this.id || side != 0 && side != 1 ? super.renderFace(world, x, y, z, side) : false;
    }

    @Override
    public Icon getTexture(WorldSource world, int x, int y, int z, int side) {
        if (side != 1 && side != 0) {
            int meta = world.getBlockMetadata(x, y, z);
            int rotation = meta & 3;
            boolean open = (meta & 4) != 0;
            boolean flip = false;
            boolean closed = (meta & 8) != 0;
            if (open) {
                if (rotation == 0 && side == 2) {
                    flip = !flip;
                } else if (rotation == 1 && side == 5) {
                    flip = !flip;
                } else if (rotation == 2 && side == 3) {
                    flip = !flip;
                } else if (rotation == 3 && side == 4) {
                    flip = !flip;
                }

                if (rotation == 0 && (side == 4 || side == 5) && this == BlockList.doorGlass) {
                    return this.icons[0];
                }

                if (rotation == 3 && (side == 2 || side == 3) && this == BlockList.doorGlass) {
                    return this.icons[0];
                }

                if (rotation == 1 && (side == 3 || side == 2) && this == BlockList.doorGlass) {
                    return this.icons[0];
                }

                if (rotation == 2 && (side == 4 || side == 5) && this == BlockList.doorGlass) {
                    return this.icons[0];
                }
            } else {
                if (rotation == 0 && side == 5) {
                    flip = !flip;
                } else if (rotation == 1 && side == 3) {
                    flip = !flip;
                } else if (rotation == 2 && side == 4) {
                    flip = !flip;
                } else if (rotation == 3 && side == 2) {
                    flip = !flip;
                }

                if (rotation == 0 && (side == 3 || side == 2) && this == BlockList.doorGlass) {
                    return this.icons[0];
                }

                if (rotation == 3 && (side == 4 || side == 5) && this == BlockList.doorGlass) {
                    return this.icons[0];
                }

                if (rotation == 1 && (side == 4 || side == 5) && this == BlockList.doorGlass) {
                    return this.icons[0];
                }

                if (rotation == 2 && (side == 3 || side == 2) && this == BlockList.doorGlass) {
                    return this.icons[0];
                }

                if ((meta & 16) != 0) {
                    flip = !flip;
                }
            }

            return this.icons[this.doorType + (flip ? WOOD.length : 0) + (closed ? 1 : 0)];
        } else {
            return this.icons[this.doorType == 4 ? 0 : this.doorType];
        }
    }

    @Override
    public void dropExact(World world, int xCoord, int yCoord, int zCoord, int id, float dropOffset, int drop, boolean shit) {
        this.dropBlock(world, xCoord, yCoord, zCoord, id);
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public BoundingBox getOutline(World world, int xCoord, int yCoord, int zCoord) {
        this.setStateBounds(world, xCoord, yCoord, zCoord);
        return super.getOutline(world, xCoord, yCoord, zCoord);
    }

    @Override
    public BoundingBox getCollisionBox(World world, int xCoord, int yCoord, int zCoord) {
        this.setStateBounds(world, xCoord, yCoord, zCoord);
        return super.getCollisionBox(world, xCoord, yCoord, zCoord);
    }

    @Override
    public void modifyOutline(WorldSource blockAccess, int xCoord, int yCoord, int zCoord) {
        this.setRotation(this.getState(blockAccess.getBlockMetadata(xCoord, yCoord, zCoord)));
    }

    @Override
    public void setStateBounds(World blockAccess, int xCoord, int yCoord, int zCoord) {
        this.setRotation(this.getState(blockAccess.getBlockMetadata(xCoord, yCoord, zCoord)));
    }

    public void setRotation(int metadataValue) {
        float width = 0.1875F;
        this.setBounds(0.0, 0.0, 0.0, 1.0, 2.0, 1.0);
        if (metadataValue == 0) {
            this.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, width);
        }

        if (metadataValue == 1) {
            this.setBounds(1.0F - width, 0.0, 0.0, 1.0, 1.0, 1.0);
        }

        if (metadataValue == 2) {
            this.setBounds(0.0, 0.0, 1.0F - width, 1.0, 1.0, 1.0);
        }

        if (metadataValue == 3) {
            this.setBounds(0.0, 0.0, 0.0, width, 1.0, 1.0);
        }
    }

    @Override
    public boolean blockActivated(World world, int xCoord, int yCoord, int zCoord, Player player) {
        if (this.id == BlockList.doorSteel.id) {
            return false;
        }

        int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
        if ((meta & 8) != 0) {
            if (world.getBlockId(xCoord, yCoord - 1, zCoord) == this.id) {
                this.blockActivated(world, xCoord, yCoord - 1, zCoord, player);
            }

            player.addStat(StatList.doorUse, 1);
            return true;
        } else {
            if (world.getBlockId(xCoord, yCoord + 1, zCoord) == this.id) {
                world.setBlockMetadataWithNotify(xCoord, yCoord + 1, zCoord, (meta ^ 4) + 8);
            }

            world.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, meta ^ 4);
            world.markBlocksDirty(xCoord, yCoord - 1, zCoord, xCoord, yCoord, zCoord);
            if (meta < 4) {
                world.playBlockSound(player, xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F, "random.door_open", 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
            } else {
                world.playBlockSound(player, xCoord + 0.5F, yCoord + 0.5F, zCoord + 0.5F, "random.door_close", 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
            }

            player.addStat(StatList.doorUse, 1);
            return true;
        }
    }

    @Override
    public void onNeighborChange(World world, int xCoord, int yCoord, int zCoord, int id) {
        int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
        if ((meta & 8) != 0) {
            if (world.getBlockId(xCoord, yCoord - 1, zCoord) != this.id) {
                world.setBlockWithNotify(xCoord, yCoord, zCoord, 0);
                world.playEffect(2001, xCoord, yCoord, zCoord, world.getBlockMetadata(xCoord, yCoord, zCoord) << 12 | this.id);
            }
        } else {
            boolean drop = false;
            if (world.getBlockId(xCoord, yCoord + 1, zCoord) != this.id) {
                world.setBlockWithNotify(xCoord, yCoord, zCoord, 0);
                world.playEffect(2001, xCoord, yCoord, zCoord, world.getBlockMetadata(xCoord, yCoord, zCoord) << 12 | this.id);
                drop = true;
            }

            if (!world.isBlockFull(xCoord, yCoord - 1, zCoord)) {
                world.setBlockWithNotify(xCoord, yCoord, zCoord, 0);
                world.playEffect(2001, xCoord, yCoord, zCoord, world.getBlockMetadata(xCoord, yCoord, zCoord) << 12 | this.id);
                drop = true;
                if (world.getBlockId(xCoord, yCoord + 1, zCoord) == this.id) {
                    world.setBlockWithNotify(xCoord, yCoord + 1, zCoord, 0);
                    world.playEffect(2001, xCoord, yCoord, zCoord, world.getBlockMetadata(xCoord, yCoord, zCoord) << 12 | this.id);
                }
            }

            if (drop) {
                if (!world.isOnline) {
                    this.dropBlock(world, xCoord, yCoord, zCoord, meta);
                }
            } else if (id > 0 && BlockList.blocks[id].providesPower()) {
                boolean powered = world.hasNeighborSignal(xCoord, yCoord, zCoord) || world.hasNeighborSignal(xCoord, yCoord + 1, zCoord);
                this.onPowered(world, xCoord, yCoord, zCoord, powered);
            }
        }
    }

    public void onPowered(World world, int x, int y, int z, boolean power) {
        int meta = world.getBlockMetadata(x, y, z);
        if ((meta & 8) != 0) {
            if (world.getBlockId(x, y - 1, z) == this.id) {
                this.onPowered(world, x, y - 1, z, power);
            }
        } else {
            boolean open = (world.getBlockMetadata(x, y, z) & 4) > 0;
            if (open != power) {
                if (world.getBlockId(x, y + 1, z) == this.id) {
                    world.setBlockMetadataWithNotify(x, y + 1, z, (meta ^ 4) + 8);
                }

                world.setBlockMetadataWithNotify(x, y, z, meta ^ 4);
                if (meta < 4) {
                    world.playBlockSound(x + 0.5F, y + 0.5F, z + 0.5F, "random.door_open", 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
                } else {
                    world.playBlockSound(x + 0.5F, y + 0.5F, z + 0.5F, "random.door_close", 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
                }
            }
        }
    }

    @Override
    public int dropID(int blockid, Random rand) {
        return (blockid & 8) != 0 ? 0 : this.dropID;
    }

    public int getState(int state) {
        return (state & 4) == 0 ? state - 1 & 3 : state & 3;
    }

    @Override
    public boolean placeableAt(World world, int xCoord, int yCoord, int zCoord) {
        return yCoord >= world.getWorldHeight() - 1
            ? false
            : (
                world.getBlockId(xCoord, yCoord + 1, zCoord) != 0
                    ? false
                    : (
                        world.getBlockId(xCoord, yCoord, zCoord) != 0
                            ? false
                            : world.isBlockFull(xCoord, yCoord - 1, zCoord)
                                && super.placeableAt(world, xCoord, yCoord, zCoord)
                                && super.placeableAt(world, xCoord, yCoord + 1, zCoord)
                    )
            );
    }

    @Override
    public boolean particles3D() {
        return this.material != MaterialList.glass;
    }

    @Override
    public int pickID(int meta) {
        return this.dropID;
    }

    @Override
    public int pickMeta(int id) {
        return 0;
    }

    public int getFullMetadata(WorldSource world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        boolean open = (meta & 8) != 0;
        int belowMeta;
        int aboveMeta;
        if (open) {
            belowMeta = world.getBlockMetadata(x, y - 1, z);
            aboveMeta = meta;
        } else {
            belowMeta = meta;
            aboveMeta = world.getBlockMetadata(x, y + 1, z);
        }

        boolean closed = (aboveMeta & 1) != 0;
        return belowMeta & 7 | (open ? 8 : 0) | (closed ? 16 : 0);
    }

    public boolean isOpen(WorldSource world, int x, int y, int z) {
        return (this.getFullMetadata(world, x, y, z) & 4) != 0;
    }

    @Override
    public int getMaxMeta() {
        return 15;
    }
}
