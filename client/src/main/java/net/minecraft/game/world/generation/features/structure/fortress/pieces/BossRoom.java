package net.minecraft.game.world.generation.features.structure.fortress.pieces;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.entity.mob.monster.boss.Inferno;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.structure.LegacyStructureBoundingBox;
import net.minecraft.game.world.generation.features.structure.LegacyStructurePiece;
import net.minecraft.game.world.generation.features.structure.LegacyStructureTreasure;
import net.minecraft.game.world.generation.features.structure.fortress.FortressPiece;

public class BossRoom extends FortressPiece {
    private static final LegacyStructureTreasure[] chestLoot = new LegacyStructureTreasure[]{
        new LegacyStructureTreasure(ItemList.swordMichael.id, 0, 1, 1, 3),
        new LegacyStructureTreasure(ItemList.lifeShard.id, 0, 1, 1, 3),
        new LegacyStructureTreasure(ItemList.swordVampire.id, 0, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.capeAgility.id, 0, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.pickaxePheonix.id, 0, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.axePheonix.id, 0, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.shovelPheonix.id, 0, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.bootsPheonix.id, 0, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.helmetPheonix.id, 0, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.platePheonix.id, 0, 1, 1, 3),
        new LegacyStructureTreasure(ItemList.legsPheonix.id, 0, 1, 1, 4),
        new LegacyStructureTreasure(ItemList.belialScythe.id, 0, 1, 1, 3),
        new LegacyStructureTreasure(ItemList.grapplingHook.id, 0, 1, 1, 3)
    };
    public ArrayList<LegacyStructurePiece> pcs = new ArrayList<>();
    private boolean hasSpawnedBoss;

    public BossRoom(int id, Random rand, LegacyStructureBoundingBox bb, int rot) {
        super(id);
        this.rotation = rot;
        this.boundingBox = bb;
    }

    @Override
    public void buildComponent(LegacyStructurePiece piss, List<LegacyStructurePiece> pcs, Random rand) {
        this.getNextComponent((EntranceBridge)piss, pcs, rand, 5, 3, true);
    }

    public static BossRoom createValidComponent(List<LegacyStructurePiece> pcs, Random rand, int x, int y, int z, int rot, int id) {
        LegacyStructureBoundingBox bb = LegacyStructureBoundingBox.addToBB(x, y, z, -5, -3, 0, 13, 14, 13, rot);
        return isAboveGround(bb) && LegacyStructurePiece.findIntersecting(pcs, bb) == null ? new BossRoom(id, rand, bb, rot) : null;
    }

    @Override
    public boolean addComponentParts(World world, Random rand, LegacyStructureBoundingBox bb) {
        this.fill(world, bb, 0, 3, 0, 12, 4, 12, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 0, 5, 0, 12, 13, 12, 0, 0, false);
        this.fill(world, bb, 0, 5, 0, 1, 12, 12, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 11, 5, 0, 12, 12, 12, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 2, 5, 11, 4, 12, 12, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 8, 5, 11, 10, 12, 12, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 5, 9, 11, 7, 12, 12, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 2, 5, 0, 4, 12, 1, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 8, 5, 0, 10, 12, 1, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 5, 9, 0, 7, 12, 1, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 2, 11, 2, 10, 12, 10, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 5, 8, 0, 7, 8, 0, BlockList.ironBars.id, BlockList.ironBars.id, false);

        for (int x = 1; x <= 11; x += 2) {
            this.fill(world, bb, x, 10, 0, x, 11, 0, BlockList.ironBars.id, BlockList.ironBars.id, false);
            this.fill(world, bb, x, 10, 12, x, 11, 12, BlockList.ironBars.id, BlockList.ironBars.id, false);
            this.fill(world, bb, 0, 10, x, 0, 11, x, BlockList.ironBars.id, BlockList.ironBars.id, false);
            this.fill(world, bb, 12, 10, x, 12, 11, x, BlockList.ironBars.id, BlockList.ironBars.id, false);
            this.placeBlock(world, BlockList.obsidianBrick.id, 0, x, 13, 0, bb);
            this.placeBlock(world, BlockList.obsidianBrick.id, 0, x, 13, 12, bb);
            this.placeBlock(world, BlockList.obsidianBrick.id, 0, 0, 13, x, bb);
            this.placeBlock(world, BlockList.obsidianBrick.id, 0, 12, 13, x, bb);
            this.placeBlock(world, BlockList.ironBars.id, 0, x + 1, 13, 0, bb);
            this.placeBlock(world, BlockList.ironBars.id, 0, x + 1, 13, 12, bb);
            this.placeBlock(world, BlockList.ironBars.id, 0, 0, 13, x + 1, bb);
            this.placeBlock(world, BlockList.ironBars.id, 0, 12, 13, x + 1, bb);
        }

        this.placeBlock(world, BlockList.ironBars.id, 0, 0, 13, 0, bb);
        this.placeBlock(world, BlockList.ironBars.id, 0, 0, 13, 12, bb);
        this.placeBlock(world, BlockList.ironBars.id, 0, 0, 13, 0, bb);
        this.placeBlock(world, BlockList.ironBars.id, 0, 12, 13, 0, bb);

        for (int var8 = 3; var8 <= 9; var8 += 2) {
            this.fill(world, bb, 1, 7, var8, 1, 8, var8, BlockList.ironBars.id, BlockList.ironBars.id, false);
            this.fill(world, bb, 11, 7, var8, 11, 8, var8, BlockList.ironBars.id, BlockList.ironBars.id, false);
        }

        this.fill(world, bb, 4, 2, 0, 8, 2, 12, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 0, 2, 4, 12, 2, 8, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 4, 0, 0, 8, 1, 3, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 4, 0, 9, 8, 1, 12, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 0, 0, 4, 3, 1, 8, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 9, 0, 4, 12, 1, 8, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);

        for (int var9 = 4; var9 <= 8; var9++) {
            for (int z = 0; z <= 2; z++) {
                this.fillDownwards(world, BlockList.obsidianBrick.id, 0, var9, -1, z, bb);
                this.fillDownwards(world, BlockList.obsidianBrick.id, 0, var9, -1, 12 - z, bb);
            }
        }

        for (int var10 = 0; var10 <= 2; var10++) {
            for (int z = 4; z <= 8; z++) {
                this.fillDownwards(world, BlockList.obsidianBrick.id, 0, var10, -1, z, bb);
                this.fillDownwards(world, BlockList.obsidianBrick.id, 0, 12 - var10, -1, z, bb);
            }
        }

        this.fill(world, bb, 5, 5, 5, 7, 5, 7, BlockList.obsidianBrick.id, BlockList.obsidianBrick.id, false);
        this.fill(world, bb, 6, 1, 6, 6, 4, 6, 0, 0, false);
        this.placeBlock(world, BlockList.obsidianBrick.id, 0, 6, 0, 6, bb);
        this.placeBlock(world, BlockList.lavaMoving.id, 0, 6, 5, 6, bb);
        this.placeLockedChest(world, bb, rand, 2, 5, 10, chestLoot, 3 + rand.nextInt(4));
        int var11 = this.getXWithOffset(6, 6);
        int y = this.getYWithOffset(5);
        int z = this.getZWithOffset(6, 6);
        if (bb.isVecInside(var11, y, z)) {
            BlockList.blocks[BlockList.lavaMoving.id].onTick(world, var11, y, z, rand);
        }

        if (!this.hasSpawnedBoss) {
            Inferno entitygolem = new Inferno(world);
            entitygolem.setPosAndRot(this.getXWithOffset(5, 10), this.getYWithOffset(5), this.getZWithOffset(5, 10), world.rand.nextFloat() * 360.0F, 0.0F);
            entitygolem.setPosition(this.getXWithOffset(5, 10), this.getYWithOffset(5), this.getZWithOffset(5, 10));
            world.spawnEntity(entitygolem);
            this.hasSpawnedBoss = true;
        }

        return true;
    }
}
