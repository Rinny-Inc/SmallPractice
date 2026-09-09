package io.noks.smallpractice.arena;

import io.noks.smallpractice.enums.Ladders;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.util.com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

public class ArenaManager {

    private List<Arena> arenaList = Lists.newLinkedList();

    public ArenaManager() {
        setupArena();
    }

    private void setupArena() {
        if (!arenaList.isEmpty()) {
            return;
        }
        final World world = Bukkit.getWorld("world");
        final Location[] stern = {
            new Location(world, 999.5D, 114.0D, -36.5D, -90.0F, 0.0F),
            new Location(world, 1043.5D, 114.0D, -36.5D, 90.0F, 0.0F),
        };
        final Location[] soupt = {
            new Location(world, 531.5D, 38.0D, 10.5D, 51.0F, 0.0F),
            new Location(world, 495.5D, 38.0D, 43.5D, -132.0F, 0.0F),
        };
        final Location[] spruce = {
            new Location(world, 11424.5D, 44.0D, 16.5D, -81.0F, 0.0F),
            new Location(world, 11487.5D, 38.0D, 28.5D, -258.0F, 0.0F),
        };

        arenaList.add(
            new Arena(
                "Stern",
                stern,
                new ItemStack(Material.DEAD_BUSH, 1),
                false,
                false
            )
        );
        arenaList.add(
            new Arena(
                "SoupT",
                soupt,
                new ItemStack(Material.LOG_2, 1, (short) 1),
                false,
                false
            )
        );
        arenaList.add(
            new Arena(
                "Spruce",
                spruce,
                new ItemStack(Material.STAINED_GLASS, 1, (short) 9),
                false,
                false
            )
        );
    }

    private final Random random = new Random();

    public Arena getRandomArena(Ladders ladder) {
        final List<Arena> arenas = this.arenaList
            .stream()
            .filter(ladder == Ladders.SUMO ? Arena::isSumo : not(Arena::isSumo))
            .filter(
                ladder == Ladders.SPLEEF
                    ? Arena::isSpleef
                    : not(Arena::isSpleef)
            )
            .collect(Collectors.toList());
        return arenas.get(this.random.nextInt(arenas.size()));
    }

    private static <T> Predicate<T> not(Predicate<T> p) {
        return p.negate();
    }

    public Arena getArenaByName(String name) {
        for (Arena arenas : this.arenaList) {
            if (arenas.getName().toLowerCase().equals(name)) {
                return arenas;
            }
        }
        return null;
    }

    public List<Arena> getFullArenaList() {
        return this.arenaList;
    }

    public List<Arena> getArenaList(boolean sumo, boolean spleef) {
        final List<Arena> list = this.arenaList
            .stream()
            .filter(sumo ? Arena::isSumo : not(Arena::isSumo))
            .filter(sumo ? Arena::isSpleef : not(Arena::isSpleef))
            .collect(Collectors.toList());
        return list.isEmpty() ? Collections.emptyList() : list;
    }
}
