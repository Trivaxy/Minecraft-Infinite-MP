package net.minecraft.game.block.tileentity;

import com.mojang.nbt.tag.special.CompoundTag;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.game.block.Block;
import net.minecraft.game.block.BlockList;
import net.minecraft.game.block.tileentity.container.BarrelEntity;
import net.minecraft.game.block.tileentity.container.BookcaseEntity;
import net.minecraft.game.block.tileentity.container.ChestEntity;
import net.minecraft.game.block.tileentity.container.CrafterEntity;
import net.minecraft.game.block.tileentity.container.DispenserEntity;
import net.minecraft.game.block.tileentity.container.GraveEntity;
import net.minecraft.game.block.tileentity.container.PotEntity;
import net.minecraft.game.block.tileentity.container.VacuumEntity;
import net.minecraft.game.block.tileentity.other.BeaconEntity;
import net.minecraft.game.block.tileentity.other.CauldronEntity;
import net.minecraft.game.block.tileentity.other.CommandBlockEntity;
import net.minecraft.game.block.tileentity.other.JukeboxEntity;
import net.minecraft.game.block.tileentity.other.LecternEntity;
import net.minecraft.game.block.tileentity.other.NoteBlockEntity;
import net.minecraft.game.block.tileentity.other.PistonEntity;
import net.minecraft.game.block.tileentity.other.PortalEntity;
import net.minecraft.game.block.tileentity.other.SignEntity;
import net.minecraft.game.block.tileentity.other.SpawnerEntity;
import net.minecraft.game.block.tileentity.smelters.BlastFurnaceEntity;
import net.minecraft.game.block.tileentity.smelters.FurnaceEntity;
import net.minecraft.game.block.tileentity.smelters.GeneratorEntity;
import net.minecraft.game.block.tileentity.smelters.HellforgeEntity;
import net.minecraft.game.block.tileentity.smelters.SmokerEntity;
import net.minecraft.game.world.World;
import net.minecraft.network.packet.Packet;

public class TileEntity {
    private static Map<String, Class<? extends TileEntity>> nameToClassMap = new HashMap<>();
    private static Map<Class<? extends TileEntity>, String> classToNameMap = new HashMap<>();
    public World world;
    public int x;
    public int y;
    public int z;
    protected boolean invalid;
    public int blockMetadata = -1;
    public Block blockType;

    private static void addMapping(Class<? extends TileEntity> class0, String name) {
        if (classToNameMap.containsKey(name)) {
            throw new IllegalArgumentException("Duplicate id: " + name);
        }

        nameToClassMap.put(name, class0);
        classToNameMap.put(class0, name);
    }

    public Block getBlockType() {
        if (this.blockType == null) {
            this.blockType = BlockList.blocks[this.world.getBlockId(this.x, this.y, this.z)];
        }

        return this.blockType;
    }

    public void readFromNBT(CompoundTag tag) {
        this.x = tag.getInteger("x");
        this.y = tag.getInteger("y");
        this.z = tag.getInteger("z");
    }

    public void writeToNBT(CompoundTag tag) {
        String name = classToNameMap.get(this.getClass());
        if (name == null) {
            throw new Error(this.getClass() + " is missing a mapping! This is a bug!");
        }

        tag.setString("id", name);
        tag.setInteger("x", this.x);
        tag.setInteger("y", this.y);
        tag.setInteger("z", this.z);
    }

    public void tick() {
    }

    public static TileEntity loadFromNBT(CompoundTag tag) {
        TileEntity TE = null;

        try {
            Class<? extends TileEntity> class2 = nameToClassMap.get(tag.getString("id"));
            if (class2 != null) {
                TE = class2.newInstance();
            }
        } catch (Exception exception3) {
            exception3.printStackTrace();
        }

        if (TE != null) {
            TE.readFromNBT(tag);
        } else {
            System.out.println("Skipping TileEntity with unknown id " + tag.getString("id"));
        }

        return TE;
    }

    public int getBlockMetadata() {
        return this.world.getBlockMetadata(this.x, this.y, this.z);
    }

    public void onChanged() {
        this.world.setChunkModified(this.x, this.y, this.z);
    }

    public double getDistanceFrom(double x, double y, double z) {
        double distX = this.x + 0.5 - x;
        double distY = this.y + 0.5 - y;
        double distZ = this.z + 0.5 - z;
        return distX * distX + distY * distY + distZ * distZ;
    }

    public Packet getPacket() {
        return null;
    }

    public boolean isInvalid() {
        return this.invalid;
    }

    public void invalidate() {
        this.invalid = true;
    }

    public void validate() {
        this.invalid = false;
    }

    public void reset() {
        this.blockType = null;
        this.blockMetadata = -1;
    }

    public double getMaxDistance() {
        return 4096.0;
    }

    public boolean dontCull() {
        return false;
    }

    static {
        addMapping(FurnaceEntity.class, "Furnace");
        addMapping(ChestEntity.class, "Chest");
        addMapping(SignEntity.class, "Sign");
        addMapping(SpawnerEntity.class, "MobSpawner");
        addMapping(BarrelEntity.class, "Barrel");
        addMapping(BookcaseEntity.class, "Bookshelf");
        addMapping(JukeboxEntity.class, "Jukebox");
        addMapping(GeneratorEntity.class, "Generator");
        addMapping(PistonEntity.class, "Piston");
        addMapping(NoteBlockEntity.class, "Noteblock");
        addMapping(DispenserEntity.class, "Trap");
        addMapping(VacuumEntity.class, "Vacuum");
        addMapping(BlastFurnaceEntity.class, "BlastFurnace");
        addMapping(CauldronEntity.class, "Cauldron");
        addMapping(PotEntity.class, "Pot");
        addMapping(HellforgeEntity.class, "Hellforge");
        addMapping(SmokerEntity.class, "Smoker");
        addMapping(LecternEntity.class, "Lectern");
        addMapping(CrafterEntity.class, "Crafter");
        addMapping(CommandBlockEntity.class, "Command");
        addMapping(GraveEntity.class, "Grave");
        addMapping(PortalEntity.class, "Portal");
        addMapping(BeaconEntity.class, "Beacon");
    }
}
