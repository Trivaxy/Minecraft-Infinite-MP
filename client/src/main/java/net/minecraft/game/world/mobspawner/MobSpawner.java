package net.minecraft.game.world.mobspawner;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.List;
import net.minecraft.game.block.material.MaterialList;
import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.animal.mounts.horse.SkeletonHorse;
import net.minecraft.game.entity.mob.animal.mounts.horse.ZombieHorse;
import net.minecraft.game.entity.mob.monster.arachnid.Spider;
import net.minecraft.game.entity.mob.monster.creeper.Creeper;
import net.minecraft.game.entity.mob.monster.skeleton.Skeleton;
import net.minecraft.game.entity.mob.monster.zombie.Zombie;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.item.Item;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.item.potion.EffectData;
import net.minecraft.game.item.potion.EffectList;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.chunk.BlockPosition;
import net.minecraft.game.world.chunk.Chunk;
import net.minecraft.game.world.chunk.ChunkUtils;
import net.minecraft.game.world.util.Position;
import net.minecraft.util.math.MathHelper;

public class MobSpawner {
    private static final LongSet eligibleChunks = new LongOpenHashSet();
    private static final Item[] items = new Item[]{ItemList.spearStone, ItemList.spearCopper, ItemList.spearSteel, ItemList.spearGold, ItemList.spearDiamond};

    protected static BlockPosition getRandomPos(World world, int x, int z) {
        Chunk chunk = world.getChunk(x, z);
        int posX = x * 16 + world.rand.nextInt(16);
        int posY = world.rand.nextInt(chunk == null ? world.getWorldHeight() : Math.max(world.getWorldHeight(), chunk.getTopFilledSegment() + 16));
        int posZ = z * 16 + world.rand.nextInt(16);
        return new BlockPosition(posX, posY, posZ);
    }

    public static int performSpawning(World world, boolean spawnHostile, boolean spawnPeaceful) {
        if (!spawnHostile && !spawnPeaceful) {
            return 0;
        }

        eligibleChunks.clear();

        for (int i = 0; i < world.players.size(); i++) {
            Player entityPlayer = world.players.get(i);
            int xChunk = MathHelper.floor_double(entityPlayer.posX / 16.0);
            int zChunk = MathHelper.floor_double(entityPlayer.posZ / 16.0);
            byte radius = 8;

            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    int eligibleChunkX = x + xChunk;
                    int eligibleChunkZ = z + zChunk;
                    if (world.chunkExists(eligibleChunkX, eligibleChunkZ)) {
                        eligibleChunks.add(ChunkUtils.getPackedCoords(eligibleChunkX, eligibleChunkZ));
                    }
                }
            }
        }

        int totalSapwnedMobsCount = 0;
        Position spawnCoordinates = world.getSpawnPoint();

        for (SpawnCategory creatureType : SpawnCategory.values()) {
            if ((!creatureType.animal() || spawnPeaceful)
                && (creatureType.animal() || spawnHostile)
                && countEntities(world, creatureType.getCreatureClass()) <= creatureType.getMaxSpawns() * eligibleChunks.size() / 256) {
                LongIterator lit = eligibleChunks.iterator();

                label139:
                while (lit.hasNext()) {
                    long chunkHash = lit.nextLong();
                    if (chunkHash != 0L) {
                        Chunk currentChunk = world.getChunk(ChunkUtils.getChunkX(chunkHash), ChunkUtils.getChunkZ(chunkHash));
                        Biome biomeGen = currentChunk.getBiomeAt(8, 8, world.getBiomeProvider());
                        int total = 0;
                        List<SpawnListEntry> spawnList = biomeGen.getSpawnList(creatureType);
                        if (spawnList != null && spawnList.size() != 0) {
                            for (SpawnListEntry listEntry : spawnList) {
                                SpawnListEntry mobToSpawn = listEntry;
                                total += mobToSpawn.spawnRate;
                            }

                            int picker = world.rand.nextInt(total);
                            SpawnListEntry mobToSpawn = spawnList.get(0);

                            for (SpawnListEntry spawnListEntry : spawnList) {
                                picker -= spawnListEntry.spawnRate;
                                if (picker < 0) {
                                    mobToSpawn = spawnListEntry;
                                    break;
                                }
                            }

                            BlockPosition blockPosition = getRandomPos(world, ChunkUtils.getChunkX(chunkHash), ChunkUtils.getChunkZ(chunkHash));
                            int x0 = blockPosition.x;
                            int y0 = blockPosition.y;
                            int z0 = blockPosition.z;
                            if (world.chunksExist(x0, y0, z0, x0, y0, z0)
                                && world.isSolid(x0, y0, z0)
                                && world.getBlockMaterial(x0, y0, z0) != creatureType.getSpawnMaterial()) {
                                int spawnedMobsCount = 0;

                                for (int attempt = 0; attempt < 3; attempt++) {
                                    int x = x0;
                                    int y = y0;
                                    int z = z0;
                                    byte step = 6;

                                    for (int i = 0; i < 4; i++) {
                                        x += world.rand.nextInt(step) - world.rand.nextInt(step);
                                        z += world.rand.nextInt(step) - world.rand.nextInt(step);
                                        if (validSpawn(creatureType, world, x, y, z)) {
                                            float xPos = x + 0.5F;
                                            float yPos = y;
                                            float zPos = z + 0.5F;
                                            if (world.getClosestPlayer(xPos, yPos, zPos, 24.0) == null) {
                                                float dx = xPos - spawnCoordinates.x;
                                                float dy = yPos - spawnCoordinates.y;
                                                float dz = zPos - spawnCoordinates.z;
                                                float distanceFromSpawn = dx * dx + dy * dy + dz * dz;
                                                if (distanceFromSpawn >= 576.0F) {
                                                    Mob entityToSpawn;
                                                    try {
                                                        entityToSpawn = mobToSpawn.entityClass.getConstructor(World.class).newInstance(world);
                                                    } catch (Exception ex) {
                                                        ex.printStackTrace();
                                                        return totalSapwnedMobsCount;
                                                    }

                                                    entityToSpawn.setPosAndRot(xPos, yPos, zPos, world.rand.nextFloat() * 360.0F, 0.0F);
                                                    if (entityToSpawn.canSpawnHere()) {
                                                        spawnedMobsCount++;
                                                        world.spawnEntity(entityToSpawn);
                                                        onSpawn(entityToSpawn, world, xPos, yPos, zPos);
                                                        if (spawnedMobsCount >= entityToSpawn.getMaxSpawns()) {
                                                            continue label139;
                                                        }
                                                    }

                                                    totalSapwnedMobsCount += spawnedMobsCount;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return totalSapwnedMobsCount;
    }

    private static void onSpawn(Mob spawnedMob, World world, float x, float y, float z) {
        int chance = 100;
        if (world.bloodMoon) {
            chance = 25;
        }

        if (spawnedMob instanceof Zombie && world.rand.nextInt(chance) == 0) {
            ZombieHorse hons = new ZombieHorse(world);
            hons.setPosAndRot(x, y, z, spawnedMob.yaw, 0.0F);
            world.spawnEntity(hons);
            spawnedMob.mountEntity(hons);
            hons.setHostile();
            ((Zombie)spawnedMob).heldItem = items[world.rand.nextInt(items.length)];
        } else if (spawnedMob instanceof Skeleton && world.rand.nextInt(chance) == 0) {
            SkeletonHorse hons = new SkeletonHorse(world);
            hons.setPosAndRot(x, y, z, spawnedMob.yaw, 0.0F);
            world.spawnEntity(hons);
            spawnedMob.mountEntity(hons);
            hons.setHostile();
        }

        if (world.bloodMoon && world.rand.nextInt(4) == 0) {
            if (spawnedMob instanceof Spider) {
                spawnedMob.addEffect(new EffectData(EffectList.invisibility.id, 1500, 0));
            } else if (spawnedMob instanceof Zombie) {
                spawnedMob.addEffect(new EffectData(EffectList.strength.id, 1500, 0));
            } else if (spawnedMob instanceof Skeleton) {
                spawnedMob.addEffect(new EffectData(EffectList.resistance.id, 1500, 0));
            } else if (spawnedMob instanceof Creeper) {
                ((Creeper)spawnedMob).setPowered();
            }
        }
    }

    private static boolean validSpawn(SpawnCategory type, World level, int x, int y, int z) {
        if (!level.chunksExist(x, y, z, x, y, z)) {
            return false;
        } else {
            return type.getSpawnMaterial() == MaterialList.water
                ? level.getBlockMaterial(x, y, z).getIsLiquid() && !level.isSolid(x, y + 1, z)
                : level.isSolid(x, y - 1, z) && !level.isSolid(x, y, z) && !level.getBlockMaterial(x, y, z).getIsLiquid() && !level.isSolid(x, y + 1, z);
        }
    }

    public static int countEntities(World world, Class<? extends Entity> class1) {
        int count = 0;

        for (Entity entity : world.entities) {
            if (class1.isAssignableFrom(entity.getClass())) {
                count++;
            }
        }

        return count;
    }
}
