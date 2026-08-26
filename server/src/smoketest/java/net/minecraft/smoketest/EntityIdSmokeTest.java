package net.minecraft.smoketest;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.game.entity.Entity;
import net.minecraft.game.entity.EntityManager;

/**
 * Entity type-id registry round trip. Unlike the other smoke tests this one needs no server:
 * the registry is a static table in :core, and the wire only ever carries the number it hands
 * out ({@code MobPacket.type}, {@code EntitySpawnPacket.type}).
 *
 * The bug it pins down (B-17): eight ids were registered twice. {@code IdToClass} kept whichever
 * class registered last while {@code classToId} kept both, so the server sent a Monkey as type
 * 120 and the client built a Vulture - and Monkey, Meerkat, Seal, Crab, Turtle, Frog, Alligator
 * and Bird were unreachable by id entirely.
 *
 * For every registered entity this checks name -> class -> id -> class comes back to the same
 * class. Restore the duplicate ids and eight of them come back as the wrong class.
 *
 * Usage: EntityIdSmokeTest [host] [port]   (both ignored; accepted so :server:smokeTest works)
 */
public class EntityIdSmokeTest {

    public static void main(String[] args) throws Exception {
        // BlockList -> ItemList -> StatList, in that order: AchievementList builds ItemStacks
        // during StatList's init and NPEs if ItemList is still mid-initialization. The game gets
        // this order for free from its startup path; a bare main does not.
        Class.forName("net.minecraft.game.block.BlockList");
        Class.forName("net.minecraft.game.item.ItemList");
        Class.forName("net.minecraft.statistics.StatList");

        List<String> names = EntityManager.validEntityIDs();
        System.out.println("[test] " + names.size() + " registered entity types");

        List<String> broken = new ArrayList<String>();
        int checked = 0;

        for (String name : names) {
            Entity entity = EntityManager.createEntity(name, null);
            if (entity == null) {
                broken.add(name + ": createEntity returned null");
                continue;
            }

            int id = EntityManager.getEntityID(entity);
            Entity roundTripped = EntityManager.createEntityFromId(id, null);
            if (roundTripped == null) {
                broken.add(name + ": id " + id + " maps to no class");
            } else if (!roundTripped.getClass().equals(entity.getClass())) {
                broken.add(name + ": sent as id " + id + ", rebuilt as " + roundTripped.getClass().getSimpleName());
            } else {
                checked++;
            }
        }

        if (!broken.isEmpty()) {
            for (String line : broken) {
                System.out.println("[test]   " + line);
            }
            fail(broken.size() + " of " + names.size() + " entity types do not survive the id round trip");
            return;
        }

        System.out.println("[test] PASS: all " + checked + " entity types round trip through their id");
        System.out.println("[test] RESULT: PASS");
    }

    private static void fail(String msg) {
        System.out.println("[test] RESULT: FAIL - " + msg);
        System.exit(1);
    }
}
