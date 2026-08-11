package net.minecraft.game.world.generation.features.structure.stronghold.pieces;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.monster.boss.Guardian;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.structure.LegacyStructureBoundingBox;
import net.minecraft.game.world.generation.features.structure.LegacyStructurePiece;
import net.minecraft.game.world.generation.features.structure.LegacyStructureTreasure;
import net.minecraft.game.world.generation.features.structure.stronghold.DoorType;
import net.minecraft.game.world.generation.features.structure.stronghold.PieceWeight;
import net.minecraft.game.world.generation.features.structure.stronghold.StrongholdPiece;
import net.minecraft.game.world.generation.features.structure.stronghold.StrongholdPieces;

public class TreasureRoom extends StrongholdPiece {
    private static final LegacyStructureTreasure[] chestLoot = new LegacyStructureTreasure[]{
        new LegacyStructureTreasure(ItemList.shield.id, 0, 1, 1, 3),
        new LegacyStructureTreasure(ItemList.swordSpider.id, 0, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.lifeShard.id, 0, 1, 1, 3),
        new LegacyStructureTreasure(ItemList.battleAxe.id, 0, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.gloves.id, 0, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.ringRegen.id, 0, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.magnet.id, 0, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.spectacles.id, 0, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.mirror.id, 0, 1, 1, 3),
        new LegacyStructureTreasure(ItemList.bracelet.id, 0, 1, 1, 4)
    };
    public ArrayList<LegacyStructurePiece> structureList = new ArrayList<>();
    public PieceWeight weight;
    private boolean hasSpawnedBoss;

    public TreasureRoom(int id, Random rand, LegacyStructureBoundingBox bb, int rot) {
        super(id);
        this.rotation = rot;
        this.boundingBox = bb;
        this.hasSpawnedBoss = false;
    }

    @Override
    public void buildComponent(LegacyStructurePiece piss, List<LegacyStructurePiece> pcs, Random rand) {
        if (piss != null) {
            ((EntryStairs)piss).treasureRoom = this;
        }
    }

    public static TreasureRoom findValidPlacement(List<LegacyStructurePiece> pcs, Random rand, int x, int y, int z, int rot, int id) {
        LegacyStructureBoundingBox bb = LegacyStructureBoundingBox.addToBB(x, y, z, -4, -1, 0, 11, 8, 16, rot);
        return LegacyStructurePiece.findIntersecting(pcs, bb) == null ? new TreasureRoom(id, rand, bb, rot) : null;
    }

    @Override
    public boolean addComponentParts(World world, Random rand, LegacyStructureBoundingBox bb) {
        this.fill(world, bb, 0, 0, 0, 10, 7, 15, false, rand, StrongholdPieces.getStrongholdStones());
        this.placeDoor(world, rand, bb, DoorType.GRATES, 4, 1, 0);
        byte offset = 6;
        this.fill(world, bb, 1, offset, 1, 1, offset, 14, false, rand, StrongholdPieces.getStrongholdStones());
        this.fill(world, bb, 9, offset, 1, 9, offset, 14, false, rand, StrongholdPieces.getStrongholdStones());
        this.fill(world, bb, 2, offset, 1, 8, offset, 2, false, rand, StrongholdPieces.getStrongholdStones());
        this.fill(world, bb, 2, offset, 14, 8, offset, 14, false, rand, StrongholdPieces.getStrongholdStones());
        this.fill(world, bb, 1, 1, 1, 2, 1, 4, false, rand, StrongholdPieces.getStrongholdStones());
        this.fill(world, bb, 8, 1, 1, 9, 1, 4, false, rand, StrongholdPieces.getStrongholdStones());
        this.fill(world, bb, 1, 1, 1, 1, 1, 3, BlockList.lavaMoving.id, BlockList.lavaMoving.id, false);
        this.fill(world, bb, 9, 1, 1, 9, 1, 3, BlockList.lavaMoving.id, BlockList.lavaMoving.id, false);
        this.fill(world, bb, 3, 1, 8, 7, 3, 12, false, rand, StrongholdPieces.getStrongholdStones());

        for (int i = 3; i < 14; i += 2) {
            this.fill(world, bb, 0, 3, i, 0, 4, i, BlockList.ironBars.id, BlockList.ironBars.id, false);
            this.fill(world, bb, 10, 3, i, 10, 4, i, BlockList.ironBars.id, BlockList.ironBars.id, false);
        }

        for (int var7 = 2; var7 < 9; var7 += 2) {
            this.fill(world, bb, var7, 3, 15, var7, 4, 15, BlockList.ironBars.id, BlockList.ironBars.id, false);
        }

        int var8 = this.getMetadataWithOffset(BlockList.stairBrick.id, 3);
        this.fill(world, bb, 4, 1, 5, 6, 1, 7, false, rand, StrongholdPieces.getStrongholdStones());
        this.fill(world, bb, 4, 2, 6, 6, 2, 7, false, rand, StrongholdPieces.getStrongholdStones());
        this.fill(world, bb, 4, 3, 7, 6, 3, 7, false, rand, StrongholdPieces.getStrongholdStones());

        for (int var6 = 4; var6 <= 6; var6++) {
            this.placeBlock(world, BlockList.stairBrick.id, var8, var6, 1, 4, bb);
            this.placeBlock(world, BlockList.stairBrick.id, var8, var6, 2, 5, bb);
            this.placeBlock(world, BlockList.stairBrick.id, var8, var6, 3, 6, bb);
        }

        this.placeLockedChest(world, bb, rand, 5, 4, 10, chestLoot, 3 + rand.nextInt(4));
        this.fill(world, bb, 5, 4, 12, 5, 4, 12, false, rand, StrongholdPieces.getStrongholdStones());
        this.fill(world, bb, 3, 4, 10, 3, 4, 10, false, rand, StrongholdPieces.getStrongholdStones());
        this.fill(world, bb, 7, 4, 10, 7, 4, 10, false, rand, StrongholdPieces.getStrongholdStones());
        this.fill(world, bb, 5, 5, 12, 5, 5, 12, BlockList.lantern.id, BlockList.lantern.id, false);
        this.fill(world, bb, 3, 5, 10, 3, 5, 10, BlockList.lantern.id, BlockList.lantern.id, false);
        this.fill(world, bb, 7, 5, 10, 7, 5, 10, BlockList.lantern.id, BlockList.lantern.id, false);
        if (!this.hasSpawnedBoss) {
            Guardian entitygolem = new Guardian(world);
            entitygolem.setPosAndRot(this.getXWithOffset(5, 10), this.getYWithOffset(5), this.getZWithOffset(5, 10), world.rand.nextFloat() * 360.0F, 0.0F);
            entitygolem.setPosition(this.getXWithOffset(5, 10), this.getYWithOffset(5), this.getZWithOffset(5, 10));
            world.spawnEntity(entitygolem);
            this.hasSpawnedBoss = true;
        }

        return true;
    }
}
