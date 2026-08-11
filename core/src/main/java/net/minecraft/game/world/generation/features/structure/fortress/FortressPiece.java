package net.minecraft.game.world.generation.features.structure.fortress;

import java.util.List;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.generation.features.structure.LegacyStructureBoundingBox;
import net.minecraft.game.world.generation.features.structure.LegacyStructurePiece;
import net.minecraft.game.world.generation.features.structure.LegacyStructureTreasure;
import net.minecraft.game.world.generation.features.structure.fortress.pieces.BridgeEnd;
import net.minecraft.game.world.generation.features.structure.fortress.pieces.EntranceBridge;

public abstract class FortressPiece extends LegacyStructurePiece {
    protected static final LegacyStructureTreasure[] treasureLoot = new LegacyStructureTreasure[]{
        new LegacyStructureTreasure(ItemList.diamond.id, 0, 1, 3, 3),
        new LegacyStructureTreasure(BlockList.lavaSponge.id, 0, 1, 3, 8),
        new LegacyStructureTreasure(ItemList.swordGold.id, 0, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.plateGold.id, 0, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.helmetGold.id, 0, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.legsGold.id, 0, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.bootsGold.id, 0, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.scroll.id, 0, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.scroll.id, 1, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.scroll.id, 2, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.scroll.id, 3, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.scroll.id, 4, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.scroll.id, 5, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.ingotDamascus.id, 0, 1, 3, 1),
        new LegacyStructureTreasure(ItemList.musket.id, 0, 1, 1, 1),
        new LegacyStructureTreasure(ItemList.crossboner.id, 0, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.emerald.id, 0, 3, 5, 8),
        new LegacyStructureTreasure(ItemList.ruby.id, 0, 3, 5, 8),
        new LegacyStructureTreasure(ItemList.sapphire.id, 0, 3, 5, 8),
        new LegacyStructureTreasure(ItemList.amethyst.id, 0, 3, 5, 8),
        new LegacyStructureTreasure(ItemList.musketRound.id, 0, 6, 16, 10),
        new LegacyStructureTreasure(ItemList.saddle.id, 0, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.discFragment.id, 0, 1, 2, 8),
        new LegacyStructureTreasure(ItemList.recordHell.id, 0, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.pearl.id, 0, 1, 1, 5)
    };

    protected FortressPiece(int id) {
        super(id);
    }

    private int getTotalWeight(List<?> daBaby) {
        boolean wght = false;
        int yes = 0;

        for (Object candidate : daBaby) {
            PieceWeight piss = (PieceWeight)candidate;
            if (piss.z > 0 && piss.maxLimit < piss.z) {
                wght = true;
            }

            yes += piss.x;
        }

        return wght ? yes : -1;
    }

    private FortressPiece getNextComponent(
        EntranceBridge start, List<?> list, List<LegacyStructurePiece> pcs, Random rand, int x, int y, int z, int rot, int id
    ) {
        int wght = this.getTotalWeight(list);
        boolean bigEnough = wght > 0 && id <= 30;
        int attempts = 0;

        while (attempts < 5 && bigEnough) {
            attempts++;
            int nextpc = rand.nextInt(wght);

            for (Object candidate : list) {
                PieceWeight pcwght = (PieceWeight)candidate;
                nextpc -= pcwght.x;
                if (nextpc < 0) {
                    if (!pcwght.getMaxLimit() || pcwght == start.pieceWeight && !pcwght.isSubmerged) {
                        break;
                    }

                    FortressPiece pc = FortressPieces.createNextComponent(pcwght, pcs, rand, x, y, z, rot, id);
                    if (pc != null) {
                        pcwght.maxLimit++;
                        start.pieceWeight = pcwght;
                        if (!pcwght.getMaxLimit()) {
                            list.remove(pcwght);
                        }

                        return pc;
                    }
                }
            }
        }

        return BridgeEnd.generateTunnelEnd(pcs, rand, x, y, z, rot, id);
    }

    private LegacyStructurePiece getNextComponent(
        EntranceBridge start, List<LegacyStructurePiece> pcs, Random rand, int x, int y, int z, int rot, int id, boolean idkman
    ) {
        if (Math.abs(x - start.getBoundingBox().minX) <= 112 && Math.abs(z - start.getBoundingBox().minZ) <= 112) {
            List<?> pcscopy = start.pieces;
            if (idkman) {
                pcscopy = start.spawners;
            }

            FortressPiece piss = this.getNextComponent(start, pcscopy, pcs, rand, x, y, z, rot, id + 1);
            if (piss != null) {
                pcs.add(piss);
                start.maxPieces.add(piss);
            }

            return piss;
        } else {
            return BridgeEnd.generateTunnelEnd(pcs, rand, x, y, z, rot, id);
        }
    }

    protected LegacyStructurePiece getNextComponent(EntranceBridge start, List<LegacyStructurePiece> pcs, Random rand, int x, int z, boolean somethingwhatever) {
        switch (this.rotation) {
            case 0:
                return this.getNextComponent(
                    start,
                    pcs,
                    rand,
                    this.boundingBox.minX + x,
                    this.boundingBox.minY + z,
                    this.boundingBox.maxZ + 1,
                    this.rotation,
                    this.getComponentType(),
                    somethingwhatever
                );
            case 1:
                return this.getNextComponent(
                    start,
                    pcs,
                    rand,
                    this.boundingBox.minX - 1,
                    this.boundingBox.minY + z,
                    this.boundingBox.minZ + x,
                    this.rotation,
                    this.getComponentType(),
                    somethingwhatever
                );
            case 2:
                return this.getNextComponent(
                    start,
                    pcs,
                    rand,
                    this.boundingBox.minX + x,
                    this.boundingBox.minY + z,
                    this.boundingBox.minZ - 1,
                    this.rotation,
                    this.getComponentType(),
                    somethingwhatever
                );
            case 3:
                return this.getNextComponent(
                    start,
                    pcs,
                    rand,
                    this.boundingBox.maxX + 1,
                    this.boundingBox.minY + z,
                    this.boundingBox.minZ + x,
                    this.rotation,
                    this.getComponentType(),
                    somethingwhatever
                );
            default:
                return null;
        }
    }

    protected LegacyStructurePiece getNextComponentX(EntranceBridge start, List<LegacyStructurePiece> pcs, Random rand, int x, int z, boolean shit) {
        switch (this.rotation) {
            case 0:
                return this.getNextComponent(
                    start, pcs, rand, this.boundingBox.minX - 1, this.boundingBox.minY + x, this.boundingBox.minZ + z, 1, this.getComponentType(), shit
                );
            case 1:
                return this.getNextComponent(
                    start, pcs, rand, this.boundingBox.minX + z, this.boundingBox.minY + x, this.boundingBox.minZ - 1, 2, this.getComponentType(), shit
                );
            case 2:
                return this.getNextComponent(
                    start, pcs, rand, this.boundingBox.minX - 1, this.boundingBox.minY + x, this.boundingBox.minZ + z, 1, this.getComponentType(), shit
                );
            case 3:
                return this.getNextComponent(
                    start, pcs, rand, this.boundingBox.minX + z, this.boundingBox.minY + x, this.boundingBox.minZ - 1, 2, this.getComponentType(), shit
                );
            default:
                return null;
        }
    }

    protected LegacyStructurePiece getNextComponentZ(EntranceBridge start, List<LegacyStructurePiece> pcs, Random rand, int x, int z, boolean ass) {
        switch (this.rotation) {
            case 0:
                return this.getNextComponent(
                    start, pcs, rand, this.boundingBox.maxX + 1, this.boundingBox.minY + x, this.boundingBox.minZ + z, 3, this.getComponentType(), ass
                );
            case 1:
                return this.getNextComponent(
                    start, pcs, rand, this.boundingBox.minX + z, this.boundingBox.minY + x, this.boundingBox.maxZ + 1, 0, this.getComponentType(), ass
                );
            case 2:
                return this.getNextComponent(
                    start, pcs, rand, this.boundingBox.maxX + 1, this.boundingBox.minY + x, this.boundingBox.minZ + z, 3, this.getComponentType(), ass
                );
            case 3:
                return this.getNextComponent(
                    start, pcs, rand, this.boundingBox.minX + z, this.boundingBox.minY + x, this.boundingBox.maxZ + 1, 0, this.getComponentType(), ass
                );
            default:
                return null;
        }
    }

    protected static boolean isAboveGround(LegacyStructureBoundingBox bb) {
        return bb != null && bb.minY > 10;
    }
}
