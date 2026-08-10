package net.minecraft.game.block.liquid;

import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.building.LayerBlock;
import net.minecraft.game.block.building.Sand;
import net.minecraft.game.block.material.Material;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.world.World;

public class FlowingLiquid extends LiquidBlock {
    int adjacentSources = 0;
    boolean[] optimalFlow = new boolean[4];
    int[] flowCost = new int[4];

    public FlowingLiquid(int id, Material mat) {
        super(id, mat);
    }

    private void updateFlow(World world, int xCoord, int yCoord, int zCoord) {
        int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
        world.setBlockAndMetadata(xCoord, yCoord, zCoord, this.id + 1, meta);
        world.markBlocksDirty(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
    }

    @Override
    public void onTick(World world, int xCoord, int yCoord, int zCoord, Random rand) {
        if (!world.physicsDisabled()) {
            Sand.can_fall = false;
            int decay = this.getFlowDecay(world, xCoord, yCoord, zCoord);
            if (decay > 0) {
                byte cost = -100;
                this.adjacentSources = 0;
                int flow = this.getSmallestFlowDecay(world, xCoord - 1, yCoord, zCoord, cost);
                flow = this.getSmallestFlowDecay(world, xCoord + 1, yCoord, zCoord, flow);
                flow = this.getSmallestFlowDecay(world, xCoord, yCoord, zCoord - 1, flow);
                flow = this.getSmallestFlowDecay(world, xCoord, yCoord, zCoord + 1, flow);
                int state;
                if (this.fluidType == 2 && world.currDim.dimension == 1) {
                    state = flow + 1;
                } else {
                    state = flow + this.fluidType;
                }

                if (state >= 8 || flow < 0) {
                    state = -1;
                }

                if (this.getFlowDecay(world, xCoord, yCoord + 1, zCoord) >= 0) {
                    int dec = this.getFlowDecay(world, xCoord, yCoord + 1, zCoord);
                    if (dec >= 8) {
                        state = dec;
                    } else {
                        state = dec + 8;
                    }
                }

                if (this.adjacentSources >= 2 && this.material == MaterialList.water) {
                    state = 0;
                }

                if (state != decay) {
                    decay = state;
                    if (state < 0) {
                        world.setBlockWithNotify(xCoord, yCoord, zCoord, 0);
                    } else {
                        world.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, state);
                        world.forceTick(xCoord, yCoord, zCoord, this.id);
                        world.updateNeighbors(xCoord, yCoord, zCoord, this.id);
                    }
                } else {
                    this.updateFlow(world, xCoord, yCoord, zCoord);
                }
            } else {
                this.updateFlow(world, xCoord, yCoord, zCoord);
            }

            if (this.canFlowInto(world, xCoord, yCoord - 1, zCoord)) {
                if (this.material == MaterialList.lava && world.getBlockMaterial(xCoord, yCoord - 1, zCoord) == MaterialList.water) {
                    world.setBlockWithNotify(xCoord, yCoord - 1, zCoord, BlockList.stone.id);
                    this.triggerLavaMixEffects(world, xCoord, yCoord - 1, zCoord);
                    Sand.can_fall = true;
                    return;
                }

                if (decay >= 8) {
                    world.setBlockAndMetadataWithNotify(xCoord, yCoord - 1, zCoord, this.id, decay);
                } else {
                    world.setBlockAndMetadataWithNotify(xCoord, yCoord - 1, zCoord, this.id, decay + 8);
                }
            } else if (decay >= 0 && (decay == 0 || this.blocksFlow(world, xCoord, yCoord - 1, zCoord))) {
                boolean[] directions = this.getFlowDirections(world, xCoord, yCoord, zCoord);
                int state = decay + this.fluidType;
                if (decay >= 8) {
                    state = 1;
                }

                if (state >= 8) {
                    Sand.can_fall = true;
                    return;
                }

                if (directions[0]) {
                    this.flowIntoBlock(world, xCoord - 1, yCoord, zCoord, state);
                }

                if (directions[1]) {
                    this.flowIntoBlock(world, xCoord + 1, yCoord, zCoord, state);
                }

                if (directions[2]) {
                    this.flowIntoBlock(world, xCoord, yCoord, zCoord - 1, state);
                }

                if (directions[3]) {
                    this.flowIntoBlock(world, xCoord, yCoord, zCoord + 1, state);
                }
            }

            Sand.can_fall = true;
        }
    }

    private void flowIntoBlock(World world, int xCoord, int yCoord, int zCoord, int metadataValue) {
        if (this.canFlowInto(world, xCoord, yCoord, zCoord)) {
            int id = world.getBlockId(xCoord, yCoord, zCoord);
            int meta = world.getBlockMetadata(xCoord, yCoord, zCoord);
            if (id > 0) {
                if (this.material == MaterialList.lava) {
                    this.triggerLavaMixEffects(world, xCoord, yCoord, zCoord);
                } else {
                    BlockList.blocks[id].dropBlock(world, xCoord, yCoord, zCoord, meta);
                    BlockList.blocks[id].onBroken(world, xCoord, yCoord, zCoord, meta);
                }
            }

            world.setBlockAndMetadataWithNotify(xCoord, yCoord, zCoord, this.id, metadataValue);
        }
    }

    private int calculateFlowCost(World world, int xCoord, int yCoord, int zCoord, int type, int dir) {
        int cost = 1000;

        for (int direction = 0; direction < 4; direction++) {
            if ((direction != 0 || dir != 1) && (direction != 1 || dir != 0) && (direction != 2 || dir != 3) && (direction != 3 || dir != 2)) {
                int newX = xCoord;
                int newZ = zCoord;
                if (direction == 0) {
                    newX = xCoord - 1;
                }

                if (direction == 1) {
                    newX++;
                }

                if (direction == 2) {
                    newZ = zCoord - 1;
                }

                if (direction == 3) {
                    newZ++;
                }

                if (!this.blocksFlow(world, newX, yCoord, newZ)
                    && (world.getBlockMaterial(newX, yCoord, newZ) != this.material || world.getBlockMetadata(newX, yCoord, newZ) != 0)) {
                    if (!this.blocksFlow(world, newX, yCoord - 1, newZ)) {
                        return type;
                    }

                    if (type < 4) {
                        int flowCost = this.calculateFlowCost(world, newX, yCoord, newZ, type + 1, direction);
                        if (flowCost < cost) {
                            cost = flowCost;
                        }
                    }
                }
            }
        }

        return cost;
    }

    private boolean[] getFlowDirections(World world, int xCoord, int yCoord, int zCoord) {
        for (int direction = 0; direction < 4; direction++) {
            this.flowCost[direction] = 1000;
            int newX = xCoord;
            int newZ = zCoord;
            if (direction == 0) {
                newX = xCoord - 1;
            }

            if (direction == 1) {
                newX++;
            }

            if (direction == 2) {
                newZ = zCoord - 1;
            }

            if (direction == 3) {
                newZ++;
            }

            if (!this.blocksFlow(world, newX, yCoord, newZ)
                && (world.getBlockMaterial(newX, yCoord, newZ) != this.material || world.getBlockMetadata(newX, yCoord, newZ) != 0)) {
                if (!this.blocksFlow(world, newX, yCoord - 1, newZ)) {
                    this.flowCost[direction] = 0;
                } else {
                    this.flowCost[direction] = this.calculateFlowCost(world, newX, yCoord, newZ, 1, direction);
                }
            }
        }

        int var8 = this.flowCost[0];

        for (int index = 1; index < 4; index++) {
            if (this.flowCost[index] < var8) {
                var8 = this.flowCost[index];
            }
        }

        for (int var10 = 0; var10 < 4; var10++) {
            this.optimalFlow[var10] = this.flowCost[var10] == var8;
        }

        return this.optimalFlow;
    }

    private boolean blocksFlow(World world, int xCoord, int yCoord, int zCoord) {
        int id = world.getBlockId(xCoord, yCoord, zCoord);
        if (id == BlockList.playerGrave.id
            || id == BlockList.doorWood.id
            || id == BlockList.signStanding.id
            || id == BlockList.signStanding2.id
            || id == BlockList.ladder.id
            || id == BlockList.pot.id
            || id == BlockList.flag.id) {
            return true;
        } else {
            return id != 0 && !(BlockList.blocks[id] instanceof LayerBlock) && id != BlockList.pebble.id ? BlockList.blocks[id].material.isSolid() : false;
        }
    }

    protected int getSmallestFlowDecay(World world, int xCoord, int yCoord, int zCoord, int val) {
        int decay = this.getFlowDecay(world, xCoord, yCoord, zCoord);
        if (decay < 0) {
            return val;
        }

        if (decay == 0) {
            this.adjacentSources++;
        }

        if (decay >= 8) {
            decay = 0;
        }

        return val >= 0 && decay >= val ? val : decay;
    }

    private boolean canFlowInto(World world, int xCoord, int yCoord, int zCoord) {
        Material mater = world.getBlockMaterial(xCoord, yCoord, zCoord);
        return mater == this.material ? false : (mater == MaterialList.lava ? false : !this.blocksFlow(world, xCoord, yCoord, zCoord));
    }

    @Override
    public void onBlockAdded(World world, int xCoord, int yCoord, int zCoord) {
        if (!world.physicsDisabled()) {
            super.onBlockAdded(world, xCoord, yCoord, zCoord);
            if (world.getBlockId(xCoord, yCoord, zCoord) == this.id) {
                world.forceTick(xCoord, yCoord, zCoord, this.id);
            }
        }
    }
}
