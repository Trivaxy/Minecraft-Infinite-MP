package net.minecraft.commands.core.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import net.minecraft.commands.Command;
import net.minecraft.commands.CommandAccessor;
import net.minecraft.commands.CommandSource;
import net.minecraft.game.entity.player.Player;
import net.minecraft.game.world.World;
import net.minecraft.game.world.biomes.Biome;
import net.minecraft.game.world.biomes.BiomeList;
import net.minecraft.game.world.chunk.BlockPosition;
import net.minecraft.game.world.generation.structures.Structure;
import net.minecraft.game.world.generation.structures.labyrinth.Labyrinth;
import net.minecraft.game.world.generation.structures.misc.Airship;
import net.minecraft.game.world.generation.structures.misc.Castle;
import net.minecraft.game.world.generation.structures.misc.Cathedral;
import net.minecraft.game.world.generation.structures.misc.Fossil;
import net.minecraft.game.world.generation.structures.misc.OceanMonument;
import net.minecraft.game.world.generation.structures.misc.Pyramid;
import net.minecraft.game.world.generation.structures.misc.Shipwreck;
import net.minecraft.game.world.generation.structures.misc.SlimeLair;
import net.minecraft.game.world.generation.structures.misc.Slipgate;
import net.minecraft.game.world.generation.structures.misc.Tower;
import net.minecraft.game.world.generation.structures.misc.Volcano;
import net.minecraft.game.world.generation.structures.oceanruins.OceanRuins;
import net.minecraft.game.world.generation.structures.palace.Palace;
import net.minecraft.game.world.generation.structures.tree.TreeStructure;
import net.minecraft.game.world.generation.structures.village.AbandonedVillage;
import net.minecraft.game.world.util.Position;
import net.minecraft.localization.Translator;
import net.minecraft.util.math.MathHelper;

public class LocateCommand extends Command {
    private static HashMap<String, Class<? extends Structure>> structures = new HashMap<String, Class<? extends Structure>>() {
        private static final long serialVersionUID = 1L;

        {
            this.put("structure.slipgate", Slipgate.class);
            this.put("structure.labyrinth", Labyrinth.class);
            this.put("structure.village", AbandonedVillage.class);
            this.put("structure.slimelair", SlimeLair.class);
            this.put("structure.shipwreck", Shipwreck.class);
            this.put("structure.oceanruins", OceanRuins.class);
            this.put("structure.volcano", Volcano.class);
            this.put("structure.fossil", Fossil.class);
            this.put("structure.castle", Castle.class);
            this.put("structure.pyramid", Pyramid.class);
            this.put("structure.oceanmonument", OceanMonument.class);
            this.put("structure.palace", Palace.class);
            this.put("structure.cathedral", Cathedral.class);
            this.put("structure.airship", Airship.class);
            this.put("structure.tower", Tower.class);
            this.put("structure.tree", TreeStructure.class);
        }
    };

    @Override
    public void action(String[] args, CommandAccessor ca, CommandSource src) {
        Player player = ca.demandPlayer(src);
        World level = ca.getWorld(player.dimension);
        int x = MathHelper.floor(player.posX);
        int y = MathHelper.floor(player.posY);
        int z = MathHelper.floor(player.posZ);
        int rad = -1;
        String biomname = args[0];
        demandArgCountOf(1, args);

        try {
            rad = parseInt(args[1]);
        } catch (Exception e) {
            rad = 1000;
        }

        if (biomname.equalsIgnoreCase("list")) {
            for (Biome biome : BiomeList.biomes) {
                if (biome != null) {
                    String name = biome.name;
                    if (name != null) {
                        src.print(name);
                    }
                }
            }

            List<String> keysList = new ArrayList<>(structures.keySet());

            for (int i = 0; i < keysList.size(); i++) {
                src.print(keysList.get(i));
            }
        } else {
            if (biomname.contains("biome")) {
                Biome biom = null;

                for (Biome b : BiomeList.biomes) {
                    if (b.name.equalsIgnoreCase(biomname)) {
                        biom = b;
                        break;
                    }
                }

                if (biom != null) {
                    src.printf(Translator.getInstance().translateKey("commands.biome.attempt"), biom.getName(), rad);
                    Position pos = level.getBiomeProvider().findBiomeAt(x, z, rad, biom);
                    if (pos != null) {
                        src.printf(Translator.getInstance().translateKey("commands.biome.find"), biom.getName(), pos.x, pos.z);
                    } else {
                        src.printf(Translator.getInstance().translateKey("commands.biome.none"), biom.getName(), rad);
                    }
                } else {
                    src.print(Translator.getInstance().translateKey("commands.biome.invalid"));
                }
            } else if (biomname.contains("structure")) {
                Class<? extends Structure> feature = null;

                try {
                    feature = structures.get(biomname.toLowerCase());
                } catch (Exception ex) {
                    src.print(Translator.getInstance().translateKey("commands.biome.invalid"));
                }

                if (feature != null) {
                    BlockPosition pos = level.findClosestStructure(feature, x, y, z);
                    if (pos != null) {
                        src.printf(Translator.getInstance().translateKey("commands.biome.find"), biomname, pos.x, pos.z);
                    } else {
                        src.printf(Translator.getInstance().translateKey("commands.biome.none"), biomname, rad);
                    }
                }
            }
        }
    }

    @Override
    public String name() {
        return "locate";
    }

    @Override
    public Command.UserType requiredUserType() {
        return Command.UserType.OPERATOR;
    }

    @Override
    public Command.EnvType envType() {
        return Command.EnvType.BOTH;
    }

    @Override
    public List<String> additionalHelp() {
        return Arrays.asList("<list> " + Translator.getInstance().translateKey("commands.biome.list"));
    }

    @Override
    public String help() {
        return "<name> <radius> " + Translator.getInstance().translateKey("commands.biome");
    }

    @Override
    public List<String[]> suggestions() {
        List<String[]> list = new ArrayList<>();
        List<String> biomes = new ArrayList<>();
        List<String> keysList = new ArrayList<>(structures.keySet());
        biomes.addAll(keysList);

        for (Biome b : BiomeList.biomes) {
            if (b != null && b.name != null) {
                biomes.add(b.name);
            }
        }

        biomes.add("list");
        String[] effects = biomes.toArray(new String[0]);
        list.add(effects);
        return list;
    }
}
