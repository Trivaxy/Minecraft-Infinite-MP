package net.minecraft.game.world.generation.features.structure.stronghold.pieces;

import java.util.List;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.structure.LegacyStructureBoundingBox;
import net.minecraft.game.world.generation.features.structure.LegacyStructurePiece;
import net.minecraft.game.world.generation.features.structure.LegacyStructureTreasure;
import net.minecraft.game.world.generation.features.structure.stronghold.DoorType;
import net.minecraft.game.world.generation.features.structure.stronghold.Stronghold;
import net.minecraft.game.world.generation.features.structure.stronghold.StrongholdPiece;
import net.minecraft.game.world.generation.features.structure.stronghold.StrongholdPieces;

public class RoomCrossing extends StrongholdPiece {
    private static final LegacyStructureTreasure[] chestLoot = new LegacyStructureTreasure[]{
        new LegacyStructureTreasure(ItemList.ingotCopper.id, 0, 1, 5, 10),
        new LegacyStructureTreasure(ItemList.ingotIron.id, 0, 1, 3, 5),
        new LegacyStructureTreasure(ItemList.recordCactus.id, 0, 1, 1, 1),
        new LegacyStructureTreasure(ItemList.coal.id, 0, 3, 8, 10),
        new LegacyStructureTreasure(ItemList.bread.id, 0, 1, 3, 15),
        new LegacyStructureTreasure(ItemList.appleRed.id, 0, 1, 3, 15),
        new LegacyStructureTreasure(ItemList.pickaxeSteel.id, 0, 1, 1, 1)
    };
    protected final DoorType doorType;
    protected final int roomType;

    public RoomCrossing(int id, Random rand, LegacyStructureBoundingBox bb, int rot) {
        super(id);
        this.rotation = rot;
        this.doorType = this.getRandomDoor(rand);
        this.boundingBox = bb;
        this.roomType = rand.nextInt(5);
    }

    @Override
    public void buildComponent(LegacyStructurePiece piece, List<LegacyStructurePiece> pcs, Random rand) {
        this.getNextComponent((EntryStairs)piece, pcs, rand, 4, 1);
        this.getNextComponentX((EntryStairs)piece, pcs, rand, 1, 4);
        this.getNextComponentZ((EntryStairs)piece, pcs, rand, 1, 4);
    }

    public static RoomCrossing findValidPlacement(List<LegacyStructurePiece> pcs, Random rand, int x, int y, int z, int direicho, int id) {
        LegacyStructureBoundingBox bb = LegacyStructureBoundingBox.addToBB(x, y, z, -4, -1, 0, 11, 7, 11, direicho);
        return canGoDeeper(bb) && LegacyStructurePiece.findIntersecting(pcs, bb) == null ? new RoomCrossing(id, rand, bb, direicho) : null;
    }

    @Override
    public boolean addComponentParts(World world, Random rand, LegacyStructureBoundingBox bb) {
        if (this.isFlooded(world, bb)) {
            return false;
        }

        if (this.getAirRatioInBB(world, bb) > 0.2F) {
            return false;
        }

        this.fill(world, bb, 0, 0, 0, 10, 6, 10, true, rand, StrongholdPieces.getStrongholdStones());
        this.placeDoor(world, rand, bb, this.doorType, 4, 1, 0);
        this.fill(world, bb, 4, 1, 10, 6, 3, 10, 0, 0, false);
        this.fill(world, bb, 0, 1, 4, 0, 3, 6, 0, 0, false);
        this.fill(world, bb, 10, 1, 4, 10, 3, 6, 0, 0, false);
        switch (this.roomType) {
            case 0:
                this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, 5, 1, 5, bb);
                this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, 5, 2, 5, bb);
                this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, 5, 3, 5, bb);
                this.placeBlock(world, BlockList.torch.id, 0, 4, 3, 5, bb);
                this.placeBlock(world, BlockList.torch.id, 0, 6, 3, 5, bb);
                this.placeBlock(world, BlockList.torch.id, 0, 5, 3, 4, bb);
                this.placeBlock(world, BlockList.torch.id, 0, 5, 3, 6, bb);
                this.placeBlock(world, BlockList.slabStone.id, 0, 4, 1, 4, bb);
                this.placeBlock(world, BlockList.slabStone.id, 0, 4, 1, 5, bb);
                this.placeBlock(world, BlockList.slabStone.id, 0, 4, 1, 6, bb);
                this.placeBlock(world, BlockList.slabStone.id, 0, 6, 1, 4, bb);
                this.placeBlock(world, BlockList.slabStone.id, 0, 6, 1, 5, bb);
                this.placeBlock(world, BlockList.slabStone.id, 0, 6, 1, 6, bb);
                this.placeBlock(world, BlockList.slabStone.id, 0, 5, 1, 4, bb);
                this.placeBlock(world, BlockList.slabStone.id, 0, 5, 1, 6, bb);
                break;
            case 1:
                for (int i = 0; i < 5; i++) {
                    this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, 3, 1, 3 + i, bb);
                    this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, 7, 1, 3 + i, bb);
                    this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, 3 + i, 1, 3, bb);
                    this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, 3 + i, 1, 7, bb);
                }

                this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, 5, 1, 5, bb);
                this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, 5, 2, 5, bb);
                this.placeBlock(world, Stronghold.bricksId, Stronghold.bricksMeta, 5, 3, 5, bb);
                this.placeBlock(world, BlockList.waterMoving.id, 0, 5, 4, 5, bb);
                break;
            case 2:
                for (int i = 1; i <= 9; i++) {
                    this.placeBlock(world, BlockList.cobblestone.id, 0, 1, 3, i, bb);
                    this.placeBlock(world, BlockList.cobblestone.id, 0, 9, 3, i, bb);
                }

                for (int var5 = 1; var5 <= 9; var5++) {
                    this.placeBlock(world, BlockList.cobblestone.id, 0, var5, 3, 1, bb);
                    this.placeBlock(world, BlockList.cobblestone.id, 0, var5, 3, 9, bb);
                }

                this.placeBlock(world, BlockList.cobblestone.id, 0, 5, 1, 4, bb);
                this.placeBlock(world, BlockList.cobblestone.id, 0, 5, 1, 6, bb);
                this.placeBlock(world, BlockList.cobblestone.id, 0, 5, 3, 4, bb);
                this.placeBlock(world, BlockList.cobblestone.id, 0, 5, 3, 6, bb);
                this.placeBlock(world, BlockList.cobblestone.id, 0, 4, 1, 5, bb);
                this.placeBlock(world, BlockList.cobblestone.id, 0, 6, 1, 5, bb);
                this.placeBlock(world, BlockList.cobblestone.id, 0, 4, 3, 5, bb);
                this.placeBlock(world, BlockList.cobblestone.id, 0, 6, 3, 5, bb);

                for (int var6 = 1; var6 <= 3; var6++) {
                    this.placeBlock(world, BlockList.cobblestone.id, 0, 4, var6, 4, bb);
                    this.placeBlock(world, BlockList.cobblestone.id, 0, 6, var6, 4, bb);
                    this.placeBlock(world, BlockList.cobblestone.id, 0, 4, var6, 6, bb);
                    this.placeBlock(world, BlockList.cobblestone.id, 0, 6, var6, 6, bb);
                }

                this.placeBlock(world, BlockList.torch.id, 0, 5, 3, 5, bb);

                for (int var7 = 2; var7 <= 8; var7++) {
                    this.placeBlock(world, BlockList.planks.id, 0, 2, 3, var7, bb);
                    this.placeBlock(world, BlockList.planks.id, 0, 3, 3, var7, bb);
                    if (var7 <= 3 || var7 >= 7) {
                        this.placeBlock(world, BlockList.planks.id, 0, 4, 3, var7, bb);
                        this.placeBlock(world, BlockList.planks.id, 0, 5, 3, var7, bb);
                        this.placeBlock(world, BlockList.planks.id, 0, 6, 3, var7, bb);
                    }

                    this.placeBlock(world, BlockList.planks.id, 0, 7, 3, var7, bb);
                    this.placeBlock(world, BlockList.planks.id, 0, 8, 3, var7, bb);
                }

                this.placeBlock(world, BlockList.ladder.id, this.getMetadataWithOffset(BlockList.ladder.id, 4), 9, 1, 3, bb);
                this.placeBlock(world, BlockList.ladder.id, this.getMetadataWithOffset(BlockList.ladder.id, 4), 9, 2, 3, bb);
                this.placeBlock(world, BlockList.ladder.id, this.getMetadataWithOffset(BlockList.ladder.id, 4), 9, 3, 3, bb);
                this.placeChest(world, bb, rand, 3, 4, 8, chestLoot, 1 + rand.nextInt(4));
        }

        return true;
    }
}
