/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.common.system;

import com.zygon.rl.common.model.CommonAttributes;
import com.zygon.rl.common.model.Entities;
import com.zygon.rl.core.model.Entity;
import com.zygon.rl.core.model.Game;
import com.zygon.rl.core.model.Location;
import com.zygon.rl.core.model.Region;
import com.zygon.rl.core.model.Regions;
import com.zygon.rl.core.system.GameSystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Randomly moves monsters around.
 */
public class SimpleAI implements GameSystem {

    private final Random random = new Random();

    @Override
    public String getName() {
        return "Simple AI";
    }

    @Override
    public Function<Game, Game> getGameUpdate() {
        return (game) -> {
            return game.copy()
                    .setRegions(moveAllMonsters(game.getRegions()))
                    .build();
        };
    }

    private Regions moveAllMonsters(Regions regions) {

        Location player = regions.find(Entities.PLAYER).stream()
                .findAny().orElse(null);

        Function<Location, Boolean> distanceFilter = (loc) -> {
            double dist = loc.getDistance(player);
            // reality bubble
            return dist < 50;
        };
        Set<Location> monsterLocations = regions.find(Entities.MONSTER.getName(), distanceFilter);

        for (Location monsterLocation : monsterLocations) {

            // Random move pct
            if (random.nextDouble() > .75) {

                // TBD: moving all monsters exposes lag, which is probably a good thing, but we don't need
                // to ALWAYS move
                List<Location> shuffledNeighbors = new ArrayList<>(monsterLocation.getNeighbors().stream()
                        .collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
                            Collections.shuffle(collected);
                            return collected.stream();
                        }))
                        .collect(Collectors.toList()));

                boolean moved = false;

                for (Location monsterNeighbor : shuffledNeighbors) {
                    if (!moved) {
                        if (canMove(monsterNeighbor, regions)) {
                            Entity monster = regions.get(monsterLocation).stream()
                                    .filter(e -> e.getName().equals("MONSTER"))
                                    .findAny().orElse(null);

                            regions = regions.move(monster, monsterNeighbor);
                            moved = true;
                        }
                    }
                }
            }
        }

        return regions;
    }

    // TBD: this is copy/paste - where does it go?
    private boolean canMove(Location destination, Regions regions) {

        Region region = regions.getRegion(destination);

        // TODO: this is not optimal - this is because monsters on the edge
        if (region == null) {
            return false;
        }

        // TODO: needs detailed terrain/heights to block entities
        Set<Entity> entities = region.get(destination);
        if (entities.stream()
                .filter(e -> !e.getAttributes(CommonAttributes.IMPASSABLE.name()).isEmpty())
                .findAny().isPresent()) {
            return false;
        }

        return true;
    }

}
