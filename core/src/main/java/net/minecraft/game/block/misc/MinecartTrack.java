package net.minecraft.game.block.misc;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.physics.BoundingBox;
import net.minecraft.game.physics.TargetingSystem;
import net.minecraft.game.physics.Vector;
import net.minecraft.game.texture.Icon;
import net.minecraft.game.texture.IconRegister;
import net.minecraft.game.world.World;
import net.minecraft.game.world.chunk.BlockPosition;

public class MinecartTrack extends Block {
    private Icon railTurn;
    private Icon railBoosterTurn;
    private Icon railStopperTurn;

    public MinecartTrack(int blockid) {
        super(blockid, MaterialList.circuits);
        this.setBounds(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);
    }

    @Override
    public BoundingBox getCollisionBox(World world, int xCoord, int yCoord, int zCoord) {
        return null;
    }

    @Override
    public boolean fullBlock() {
        return false;
    }

    @Override
    public TargetingSystem rayTrace(World world, int xCoord, int yCoord, int zCoord, Vector start, Vector end) {
        this.setStateBounds(world, xCoord, yCoord, zCoord);
        return super.rayTrace(world, xCoord, yCoord, zCoord, start, end);
    }

    @Override
    public void setStateBounds(World world, int xCoord, int yCoord, int zCoord) {
        int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
        if (meta >= 2 && meta <= 5) {
            this.setBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        } else {
            this.setBounds(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);
        }
    }

    @Override
    public Icon getIcon(int side, int mdata) {
        if (this.id == BlockList.railBooster.id) {
            return mdata >= 6 ? this.railBoosterTurn : this.blockIcon;
        } else if (this.id == BlockList.railStopper.id) {
            return mdata >= 6 ? this.railStopperTurn : this.blockIcon;
        } else {
            return mdata >= 6 ? this.railTurn : this.blockIcon;
        }
    }

    @Override
    public void registerIcons(IconRegister r) {
        this.blockIcon = r.registerIcon("rails/" + this.name);
        this.railTurn = r.registerIcon("rails/rail_turn");
        this.railBoosterTurn = r.registerIcon("rails/railPoweredCurve");
        this.railStopperTurn = r.registerIcon("rails/railStopperCurve");
    }

    @Override
    public Block.RenderType getRenderType() {
        return Block.RenderType.RAIL;
    }

    @Override
    public boolean placeableAt(World world, int xCoord, int yCoord, int zCoord) {
        return world.isBlockFull(xCoord, yCoord - 1, zCoord);
    }

    @Override
    public void onBlockAdded(World world, int xCoord, int yCoord, int zCoord) {
        if (!world.isOnline) {
            world.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 15);
            this.refreshTrackShape(world, xCoord, yCoord, zCoord);
        }
    }

    @Override
    public void onNeighborChange(World world, int xCoord, int yCoord, int zCoord, int id) {
        if (!world.isOnline) {
            int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
            boolean drop = false;
            if (!world.isBlockFull(xCoord, yCoord - 1, zCoord)) {
                drop = true;
            }

            if (meta == 2 && !world.isBlockFull(xCoord + 1, yCoord, zCoord)) {
                drop = true;
            }

            if (meta == 3 && !world.isBlockFull(xCoord - 1, yCoord, zCoord)) {
                drop = true;
            }

            if (meta == 4 && !world.isBlockFull(xCoord, yCoord, zCoord - 1)) {
                drop = true;
            }

            if (meta == 5 && !world.isBlockFull(xCoord, yCoord, zCoord + 1)) {
                drop = true;
            }

            if (drop) {
                this.dropBlock(world, xCoord, yCoord, zCoord, world.getBlockMetadata(xCoord, yCoord, zCoord));
                world.setBlockWithNotify(xCoord, yCoord, zCoord, 0);
            }
        }
    }

    private void refreshTrackShape(World world, int xCoord, int yCoord, int zCoord) {
        if (!world.isOnline) {
            new MinecartTrack.TrackLogic(world, xCoord, yCoord, zCoord).place();
        }
    }

    @Override
    public boolean particles3D() {
        return false;
    }

    private class TrackLogic {
        private World world;
        private int trackX;
        private int trackY;
        private int trackZ;
        private int trackMetadata;
        private List<BlockPosition> connectedTracks = new ArrayList<>();

        public TrackLogic(World world, int x, int y, int z) {
            this.world = world;
            this.trackX = x;
            this.trackY = y;
            this.trackZ = z;
            this.trackMetadata = world.getBlockMetadata(x, y, z);
            this.calculateConnectedTracks();
        }

        private void calculateConnectedTracks() {
            this.connectedTracks.clear();
            if (this.trackMetadata == 0) {
                this.connectedTracks.add(new BlockPosition(this.trackX, this.trackY, this.trackZ - 1));
                this.connectedTracks.add(new BlockPosition(this.trackX, this.trackY, this.trackZ + 1));
            } else if (this.trackMetadata == 1) {
                this.connectedTracks.add(new BlockPosition(this.trackX - 1, this.trackY, this.trackZ));
                this.connectedTracks.add(new BlockPosition(this.trackX + 1, this.trackY, this.trackZ));
            } else if (this.trackMetadata == 2) {
                this.connectedTracks.add(new BlockPosition(this.trackX - 1, this.trackY, this.trackZ));
                this.connectedTracks.add(new BlockPosition(this.trackX + 1, this.trackY + 1, this.trackZ));
            } else if (this.trackMetadata == 3) {
                this.connectedTracks.add(new BlockPosition(this.trackX - 1, this.trackY + 1, this.trackZ));
                this.connectedTracks.add(new BlockPosition(this.trackX + 1, this.trackY, this.trackZ));
            } else if (this.trackMetadata == 4) {
                this.connectedTracks.add(new BlockPosition(this.trackX, this.trackY + 1, this.trackZ - 1));
                this.connectedTracks.add(new BlockPosition(this.trackX, this.trackY, this.trackZ + 1));
            } else if (this.trackMetadata == 5) {
                this.connectedTracks.add(new BlockPosition(this.trackX, this.trackY, this.trackZ - 1));
                this.connectedTracks.add(new BlockPosition(this.trackX, this.trackY + 1, this.trackZ + 1));
            } else if (this.trackMetadata == 6) {
                this.connectedTracks.add(new BlockPosition(this.trackX + 1, this.trackY, this.trackZ));
                this.connectedTracks.add(new BlockPosition(this.trackX, this.trackY, this.trackZ + 1));
            } else if (this.trackMetadata == 7) {
                this.connectedTracks.add(new BlockPosition(this.trackX - 1, this.trackY, this.trackZ));
                this.connectedTracks.add(new BlockPosition(this.trackX, this.trackY, this.trackZ + 1));
            } else if (this.trackMetadata == 8) {
                this.connectedTracks.add(new BlockPosition(this.trackX - 1, this.trackY, this.trackZ));
                this.connectedTracks.add(new BlockPosition(this.trackX, this.trackY, this.trackZ - 1));
            } else if (this.trackMetadata == 9) {
                this.connectedTracks.add(new BlockPosition(this.trackX + 1, this.trackY, this.trackZ));
                this.connectedTracks.add(new BlockPosition(this.trackX, this.trackY, this.trackZ - 1));
            }
        }

        private void refreshConnectedTracks() {
            for (int i = 0; i < this.connectedTracks.size(); i++) {
                MinecartTrack.TrackLogic logic = this.getTrackLogic(this.connectedTracks.get(i));
                if (logic != null && logic.isConnectedTo(this)) {
                    this.connectedTracks.set(i, new BlockPosition(logic.trackX, logic.trackY, logic.trackZ));
                } else {
                    this.connectedTracks.remove(i--);
                }
            }
        }

        private MinecartTrack.TrackLogic getTrackLogic(BlockPosition chunk) {
            Block thisLayer = BlockList.blocks[this.world.getBlockId(chunk.x, chunk.y, chunk.z)];
            Block upperLayer = BlockList.blocks[this.world.getBlockId(chunk.x, chunk.y + 1, chunk.z)];
            Block lowerLayer = BlockList.blocks[this.world.getBlockId(chunk.x, chunk.y - 1, chunk.z)];
            if (thisLayer instanceof MinecartTrack) {
                return MinecartTrack.this.new TrackLogic(this.world, chunk.x, chunk.y, chunk.z);
            } else if (upperLayer instanceof MinecartTrack) {
                return MinecartTrack.this.new TrackLogic(this.world, chunk.x, chunk.y + 1, chunk.z);
            } else {
                return lowerLayer instanceof MinecartTrack ? MinecartTrack.this.new TrackLogic(this.world, chunk.x, chunk.y - 1, chunk.z) : null;
            }
        }

        private boolean isConnectedTo(MinecartTrack.TrackLogic trackLogic) {
            for (int index = 0; index < this.connectedTracks.size(); index++) {
                BlockPosition chunk = this.connectedTracks.get(index);
                if (chunk.x == trackLogic.trackX && chunk.z == trackLogic.trackZ) {
                    return true;
                }
            }

            return false;
        }

        private boolean isInTrack(int x, int y, int z) {
            for (int index = 0; index < this.connectedTracks.size(); index++) {
                BlockPosition chunk = this.connectedTracks.get(index);
                if (chunk.x == x && chunk.z == z) {
                    return true;
                }
            }

            return false;
        }

        private boolean handleKeyPress(MinecartTrack.TrackLogic trackLogic) {
            if (this.isConnectedTo(trackLogic)) {
                return true;
            }

            if (this.connectedTracks.size() == 2) {
                return false;
            }

            if (this.connectedTracks.size() == 0) {
                return true;
            }

            BlockPosition chunk = this.connectedTracks.get(0);
            return trackLogic.trackY == this.trackY && chunk.y == this.trackY ? true : true;
        }

        private void connectToNeighbor(MinecartTrack.TrackLogic logic) {
            this.connectedTracks.add(new BlockPosition(logic.trackX, logic.trackY, logic.trackZ));
            this.world.setBlockMetadataWithNotify(this.trackX, this.trackY, this.trackZ, this.getMeta(false));
        }

        public void place() {
            int meta = this.getMeta(true);
            this.trackMetadata = meta;
            this.calculateConnectedTracks();
            this.world.setBlockMetadataWithNotify(this.trackX, this.trackY, this.trackZ, meta);

            for (int index = 0; index < this.connectedTracks.size(); index++) {
                MinecartTrack.TrackLogic trackLogic = this.getTrackLogic(this.connectedTracks.get(index));
                if (trackLogic != null) {
                    trackLogic.refreshConnectedTracks();
                    if (trackLogic.handleKeyPress(this)) {
                        trackLogic.connectToNeighbor(this);
                    }
                }
            }
        }

        private int getMeta(boolean connect) {
            boolean left = this.isInTrack(this.trackX, this.trackY, this.trackZ - 1);
            boolean right = this.isInTrack(this.trackX, this.trackY, this.trackZ + 1);
            boolean front = this.isInTrack(this.trackX - 1, this.trackY, this.trackZ);
            boolean back = this.isInTrack(this.trackX + 1, this.trackY, this.trackZ);
            if (connect) {
                left = this.canConnectFrom(this.trackX, this.trackY, this.trackZ - 1);
                right = this.canConnectFrom(this.trackX, this.trackY, this.trackZ + 1);
                front = this.canConnectFrom(this.trackX - 1, this.trackY, this.trackZ);
                back = this.canConnectFrom(this.trackX + 1, this.trackY, this.trackZ);
            }

            Block adjacent1 = BlockList.blocks[this.world.getBlockId(this.trackX, this.trackY + 1, this.trackZ - 1)];
            Block adjacent2 = BlockList.blocks[this.world.getBlockId(this.trackX, this.trackY + 1, this.trackZ + 1)];
            Block adjacent3 = BlockList.blocks[this.world.getBlockId(this.trackX + 1, this.trackY + 1, this.trackZ)];
            Block adjacent4 = BlockList.blocks[this.world.getBlockId(this.trackX - 1, this.trackY + 1, this.trackZ)];
            byte meta = -1;
            if (left || right) {
                meta = 0;
            }

            if (front || back) {
                meta = 1;
            }

            if (right && back && !left && !front) {
                meta = 6;
            }

            if (right && front && !left && !back) {
                meta = 7;
            }

            if (left && front && !right && !back) {
                meta = 8;
            }

            if (left && back && !right && !front) {
                meta = 9;
            }

            if (meta == 0) {
                if (adjacent1 instanceof MinecartTrack) {
                    meta = 4;
                }

                if (adjacent2 instanceof MinecartTrack) {
                    meta = 5;
                }
            }

            if (meta == 1) {
                if (adjacent3 instanceof MinecartTrack) {
                    meta = 2;
                }

                if (adjacent4 instanceof MinecartTrack) {
                    meta = 3;
                }
            }

            if (meta < 0) {
                meta = 0;
            }

            return meta;
        }

        private boolean canConnectFrom(int x, int y, int z) {
            MinecartTrack.TrackLogic trackLogic = this.getTrackLogic(new BlockPosition(x, y, z));
            if (trackLogic == null) {
                return false;
            }

            trackLogic.refreshConnectedTracks();
            return trackLogic.handleKeyPress(this);
        }
    }
}
