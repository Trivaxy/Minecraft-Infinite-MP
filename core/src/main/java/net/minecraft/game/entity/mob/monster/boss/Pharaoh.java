package net.minecraft.game.entity.mob.monster.boss;

import net.minecraft.game.ParticleType;
import net.minecraft.game.entity.mob.CreatureType;
import net.minecraft.game.entity.mob.Mob;
import net.minecraft.game.entity.mob.ai.Action;
import net.minecraft.game.entity.mob.monster.arachnid.Termite;
import net.minecraft.game.entity.mob.monster.zombie.Mummy;
import net.minecraft.game.item.ItemList;
import net.minecraft.game.world.World;

public class Pharaoh extends Mummy {
    private String[] names = new String[]{
        "Nechepso", "Kerpheres", "Khabash", "Sebercheres", "Huischera", "Scheschanq", "Achoris", "Herhut", "Nuru", "Nephthys"
    };

    public Pharaoh(World world) {
        super(world);
        this.setSize(0.8F, 2.34F);
        this.setHealth(250);
        this.heldItem = ItemList.pharaohStaff;
        this.texture = "/mob/zombie/pharaoh.png";
        this.boss = true;
        this.tier = 1;
        this.scoreValue = 1000;
        this.customName = this.names[this.rand.nextInt(this.names.length)];
        this.behaviors.addTask(3, new Pharaoh.SummonAdversaries(this, this.world, 32.0F));
    }

    @Override
    public CreatureType getCreatureType() {
        return CreatureType.UNDEAD;
    }

    @Override
    public int getSpawnID() {
        return 47;
    }

    private class SummonAdversaries extends Action {
        private Mob mob;
        private World world;
        float range;

        private SummonAdversaries(Mob mob, World world, float range) {
            this.mob = mob;
            this.world = world;
            this.range = range;
        }

        @Override
        public boolean canExecute() {
            return this.mob.attackTarget != null
                && this.world.getEntityOfType(Termite.class, this.mob.boundingBox.expand(this.range, 8.0, this.range), this.mob) == null
                && this.mob.rand.nextFloat() <= 0.015F;
        }

        @Override
        public void start() {
            World world = this.world;
            Mob summoner = this.mob;
            int[][] offsets = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
            Termite[] termites = new Termite[offsets.length];

            for (int i = 0; i < offsets.length; i++) {
                Termite termite = new Termite(world);
                termite.setPosAndRot(summoner.posX + offsets[i][0], summoner.posY, summoner.posZ + offsets[i][1], summoner.pitch, summoner.yaw);
                world.spawnEntity(termite);
                termites[i] = termite;
            }

            world.playSoundEffect(summoner.posX, summoner.posY, summoner.posZ, "random.breath", 1.0F, 1.0F / (world.rand.nextFloat() * 0.4F + 0.8F));

            for (int i = 0; i < 20; i++) {
                for (int j = 0; j < termites.length; j++) {
                    Termite termite = termites[j];
                    world.spawnParticle(
                        ParticleType.EXPLODE,
                        termite.posX + world.rand.nextFloat() * termite.width * 2.0F - termite.width,
                        termite.posY + world.rand.nextFloat() * termite.height,
                        termite.posZ + world.rand.nextFloat() * termite.width * 2.0F - termite.width,
                        world.rand.nextGaussian() * 0.02,
                        world.rand.nextGaussian() * 0.02,
                        world.rand.nextGaussian() * 0.02
                    );
                }
            }
        }
    }
}
