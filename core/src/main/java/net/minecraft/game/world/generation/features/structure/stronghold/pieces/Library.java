package net.minecraft.game.world.generation.features.structure.stronghold.pieces;

import com.mojang.nbt.tag.special.ListTag;
import com.mojang.nbt.tag.special.StringTag;
import java.util.List;
import java.util.Random;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.tileentity.container.BookcaseEntity;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.ItemStack;
import net.minecraft.game.world.World;
import net.minecraft.game.world.generation.features.structure.LegacyStructureBoundingBox;
import net.minecraft.game.world.generation.features.structure.LegacyStructurePiece;
import net.minecraft.game.world.generation.features.structure.LegacyStructureTreasure;
import net.minecraft.game.world.generation.features.structure.stronghold.DoorType;
import net.minecraft.game.world.generation.features.structure.stronghold.StrongholdPiece;
import net.minecraft.game.world.generation.features.structure.stronghold.StrongholdPieces;

public class Library extends StrongholdPiece {
    private static final LegacyStructureTreasure[] treasureList = new LegacyStructureTreasure[]{
        new LegacyStructureTreasure(ItemList.book.id, 0, 1, 3, 20),
        new LegacyStructureTreasure(ItemList.paper.id, 0, 2, 7, 20),
        new LegacyStructureTreasure(ItemList.bookAndQuill.id, 0, 1, 1, 10),
        new LegacyStructureTreasure(ItemList.mapEmpty.id, 0, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.compass.id, 0, 1, 1, 5),
        new LegacyStructureTreasure(ItemList.scroll.id, 0, 1, 1, 8),
        new LegacyStructureTreasure(ItemList.scroll.id, 1, 1, 1, 8),
        new LegacyStructureTreasure(ItemList.scroll.id, 2, 1, 1, 8),
        new LegacyStructureTreasure(ItemList.scroll.id, 3, 1, 1, 8),
        new LegacyStructureTreasure(ItemList.scroll.id, 4, 1, 1, 8),
        new LegacyStructureTreasure(ItemList.scroll.id, 5, 1, 1, 8)
    };
    String[] bookContents = new String[]{
        "She came from the water, bold and beautiful. Mighty sorceress from beyond.",
        "When night falls, she cloaks the world in impenetrable darkness. A chill rises from the soil and contaminates the air. Suddenly, life has new meaning.",
        "We have not inherited Thule from our ancestors, we have borrowed it from our children.",
        "From the ashes, he arose. Powerful wings of destruction would soon bring upon our demise.",
        "Cast into the void, a slumbering giant. An age of evolution will dawn upon his awakening.",
        "Today is born the seventh one. Born of woman the seventh son. And he in turn of a seventh son. He has the power to heal, He has the gift of the second sight. He is the chosen one.",
        "Other planes lie beyond the reach of normal sense and common roads.",
        "And when she ascended into the heavens, the pact was sealed. The groundlings would soon forget, her memory laid in stone.",
        "One with the blue sky. A child of the stars. A last hope at the edge of yonder is all we can cling to.",
        "To make the Gods' nature one's own, a man must imitate them in every thought, word and deed.",
        "I wonder how winter will be, with a spring that I shall never see. I wonder how night will be, with a day that I shall never see. I wonder how life will be, with a pain that lasts eternally.",
        "I wonder how life will be, with a death that I shall never see. I wonder why life must be, a life that lasts eternally.",
        "As I lie here, the burden is being lifted once and for all. Beware of the light. It may take you away to a land where no evil dwells. Take you away for all eternity.",
        "There stands an ash called Yggdrasil, a mighty tree, showered in white hail. From there come the dews that fall in the valleys, it stands ever green above Earth's well.",
        "Woe be to him that defies the tree, for he shall be cast out into the world through the veils of pain and fire.",
        "Blessed be the tailors, the masks are cut to fit. Blessed be the woodworkers, the crosses and the gallows. Blessed be the forgers of iron, the spikes and the barbwire. Blessed be the stone cutters, it took a quarry to bury their dreams."
    };
    protected final DoorType doorType;
    private final boolean isLargeRoom;

    public Library(int id, Random rand, LegacyStructureBoundingBox bb, int rot) {
        super(id);
        this.rotation = rot;
        this.doorType = this.getRandomDoor(rand);
        this.boundingBox = bb;
        this.isLargeRoom = bb.getYSize() > 6;
    }

    @Override
    public void buildComponent(LegacyStructurePiece piece, List<LegacyStructurePiece> pcs, Random rand) {
    }

    private void placeBookcase(World world, int x, int y, int z) {
        world.setBlockWithNotify(x, y, z, BlockList.bookshelf.id);
        BookcaseEntity bookcase = (BookcaseEntity)world.getTileEntity(x, y, z);
        if (bookcase != null && bookcase.getSize() > 0) {
            int ni = bookcase.getSize();
            int metadata = 0;

            for (int i = 0; i < ni; i++) {
                if (world.rand.nextInt(2) == 0) {
                    ItemStack stack = new ItemStack(ItemList.book, world.rand.nextInt(5) + 1);
                    if (world.rand.nextInt(10) == 0) {
                        stack = new ItemStack(ItemList.writtenBook);
                    }

                    if (stack.getItem() == ItemList.writtenBook) {
                        ListTag<StringTag> bookPages = new ListTag<>();
                        bookPages.add(new StringTag(""));
                        StringTag contents = bookPages.get(0);
                        contents.string = this.bookContents[world.rand.nextInt(this.bookContents.length)];
                        stack.setTagInfo("pages", bookPages);
                    }

                    bookcase.setSlotContents(i, stack);
                    metadata++;
                }
            }

            world.setBlockMetadataWithNotify(x, y, z, metadata);
        }
    }

    protected void fillWithBooks(World world, LegacyStructureBoundingBox bb, int startX, int startY, int startZ, int endX, int endY, int endZ, int blockID) {
        for (int yy0 = startY; yy0 <= endY; yy0++) {
            for (int xx0 = startX; xx0 <= endX; xx0++) {
                for (int zz0 = startZ; zz0 <= endZ; zz0++) {
                    this.placeBlockHere(world, blockID, 0, xx0, yy0, zz0, bb);
                }
            }
        }
    }

    protected void placeBlockHere(World world, int blockid, int metadata, int bbx, int bby, int bbz, LegacyStructureBoundingBox bb) {
        int x = this.getXWithOffset(bbx, bbz);
        int y = this.getYWithOffset(bby);
        int z = this.getZWithOffset(bbx, bbz);
        if (bb.isVecInside(x, y, z)) {
            world.setBlockAndMetadata(x, y, z, blockid, metadata);
            this.placeBookcase(world, x, y, z);
        }
    }

    public static Library findValidPlacement(List<LegacyStructurePiece> pcs, Random rand, int x, int y, int z, int dir, int id) {
        LegacyStructureBoundingBox bb = LegacyStructureBoundingBox.addToBB(x, y, z, -4, -1, 0, 14, 11, 15, dir);
        if (!canGoDeeper(bb) || LegacyStructurePiece.findIntersecting(pcs, bb) != null) {
            bb = LegacyStructureBoundingBox.addToBB(x, y, z, -4, -1, 0, 14, 6, 15, dir);
            if (!canGoDeeper(bb) || LegacyStructurePiece.findIntersecting(pcs, bb) != null) {
                return null;
            }
        }

        return new Library(id, rand, bb, dir);
    }

    @Override
    public boolean addComponentParts(World world, Random rand, LegacyStructureBoundingBox bb) {
        if (this.isFlooded(world, bb)) {
            return false;
        }

        if (this.getAirRatioInBB(world, bb) > 0.2F) {
            return false;
        }

        byte height = 11;
        if (!this.isLargeRoom) {
            height = 6;
        }

        this.fill(world, bb, 0, 0, 0, 13, height - 1, 14, true, rand, StrongholdPieces.getStrongholdStones());
        this.placeDoor(world, rand, bb, this.doorType, 4, 1, 0);
        this.fill(world, bb, rand, 0.07F, 2, 1, 1, 11, 4, 13, BlockList.cobweb.id, BlockList.cobweb.id, false);

        for (int i = 1; i <= 13; i++) {
            if ((i - 1) % 4 == 0) {
                this.fill(world, bb, 1, 1, i, 1, 4, i, BlockList.planks.id, BlockList.planks.id, false);
                this.fill(world, bb, 12, 1, i, 12, 4, i, BlockList.planks.id, BlockList.planks.id, false);
                this.placeBlock(world, BlockList.torch.id, 0, 2, 3, i, bb);
                this.placeBlock(world, BlockList.torch.id, 0, 11, 3, i, bb);
                if (this.isLargeRoom) {
                    this.fill(world, bb, 1, 6, i, 1, 9, i, BlockList.planks.id, BlockList.planks.id, false);
                    this.fill(world, bb, 12, 6, i, 12, 9, i, BlockList.planks.id, BlockList.planks.id, false);
                }
            } else {
                this.fillWithBooks(world, bb, 1, 1, i, 1, 4, i, BlockList.bookshelf.id);
                this.fillWithBooks(world, bb, 12, 1, i, 12, 4, i, BlockList.bookshelf.id);
                if (this.isLargeRoom) {
                    this.fillWithBooks(world, bb, 1, 6, i, 1, 9, i, BlockList.bookshelf.id);
                    this.fillWithBooks(world, bb, 12, 6, i, 12, 9, i, BlockList.bookshelf.id);
                }
            }
        }

        for (int var8 = 3; var8 < 12; var8 += 2) {
            this.fillWithBooks(world, bb, 3, 1, var8, 4, 3, var8, BlockList.bookshelf.id);
            this.fillWithBooks(world, bb, 6, 1, var8, 7, 3, var8, BlockList.bookshelf.id);
            this.fillWithBooks(world, bb, 9, 1, var8, 10, 3, var8, BlockList.bookshelf.id);
        }

        if (this.isLargeRoom) {
            this.fill(world, bb, 1, 5, 1, 3, 5, 13, BlockList.planks.id, BlockList.planks.id, false);
            this.fill(world, bb, 10, 5, 1, 12, 5, 13, BlockList.planks.id, BlockList.planks.id, false);
            this.fill(world, bb, 4, 5, 1, 9, 5, 2, BlockList.planks.id, BlockList.planks.id, false);
            this.fill(world, bb, 4, 5, 12, 9, 5, 13, BlockList.planks.id, BlockList.planks.id, false);
            this.placeBlock(world, BlockList.planks.id, 0, 9, 5, 11, bb);
            this.placeBlock(world, BlockList.planks.id, 0, 8, 5, 11, bb);
            this.placeBlock(world, BlockList.planks.id, 0, 9, 5, 10, bb);
            this.fill(world, bb, 3, 6, 2, 3, 6, 12, BlockList.fence.id, BlockList.fence.id, false);
            this.fill(world, bb, 10, 6, 2, 10, 6, 10, BlockList.fence.id, BlockList.fence.id, false);
            this.fill(world, bb, 4, 6, 2, 9, 6, 2, BlockList.fence.id, BlockList.fence.id, false);
            this.fill(world, bb, 4, 6, 12, 8, 6, 12, BlockList.fence.id, BlockList.fence.id, false);
            this.placeBlock(world, BlockList.fence.id, 0, 9, 6, 11, bb);
            this.placeBlock(world, BlockList.fence.id, 0, 8, 6, 11, bb);
            this.placeBlock(world, BlockList.fence.id, 0, 9, 6, 10, bb);
            int var9 = this.getMetadataWithOffset(BlockList.ladder.id, 3);
            this.placeBlock(world, BlockList.ladder.id, var9, 10, 1, 13, bb);
            this.placeBlock(world, BlockList.ladder.id, var9, 10, 2, 13, bb);
            this.placeBlock(world, BlockList.ladder.id, var9, 10, 3, 13, bb);
            this.placeBlock(world, BlockList.ladder.id, var9, 10, 4, 13, bb);
            this.placeBlock(world, BlockList.ladder.id, var9, 10, 5, 13, bb);
            this.placeBlock(world, BlockList.ladder.id, var9, 10, 6, 13, bb);
            this.placeBlock(world, BlockList.ladder.id, var9, 10, 7, 13, bb);
            byte x = 7;
            byte z = 7;
            this.placeBlock(world, BlockList.fence.id, 0, x - 1, 9, z, bb);
            this.placeBlock(world, BlockList.fence.id, 0, x, 9, z, bb);
            this.placeBlock(world, BlockList.fence.id, 0, x - 1, 8, z, bb);
            this.placeBlock(world, BlockList.fence.id, 0, x, 8, z, bb);
            this.placeBlock(world, BlockList.fence.id, 0, x - 1, 7, z, bb);
            this.placeBlock(world, BlockList.fence.id, 0, x, 7, z, bb);
            this.placeBlock(world, BlockList.fence.id, 0, x - 2, 7, z, bb);
            this.placeBlock(world, BlockList.fence.id, 0, x + 1, 7, z, bb);
            this.placeBlock(world, BlockList.fence.id, 0, x - 1, 7, z - 1, bb);
            this.placeBlock(world, BlockList.fence.id, 0, x - 1, 7, z + 1, bb);
            this.placeBlock(world, BlockList.fence.id, 0, x, 7, z - 1, bb);
            this.placeBlock(world, BlockList.fence.id, 0, x, 7, z + 1, bb);
            this.placeBlock(world, BlockList.torch.id, 0, x - 2, 8, z, bb);
            this.placeBlock(world, BlockList.torch.id, 0, x + 1, 8, z, bb);
            this.placeBlock(world, BlockList.torch.id, 0, x - 1, 8, z - 1, bb);
            this.placeBlock(world, BlockList.torch.id, 0, x - 1, 8, z + 1, bb);
            this.placeBlock(world, BlockList.torch.id, 0, x, 8, z - 1, bb);
            this.placeBlock(world, BlockList.torch.id, 0, x, 8, z + 1, bb);
        }

        this.placeChest(world, bb, rand, 3, 3, 5, treasureList, 1 + rand.nextInt(4));
        if (this.isLargeRoom) {
            this.placeBlock(world, 0, 0, 12, 9, 1, bb);
            this.placeChest(world, bb, rand, 12, 8, 1, treasureList, 1 + rand.nextInt(4));
        }

        return true;
    }
}
