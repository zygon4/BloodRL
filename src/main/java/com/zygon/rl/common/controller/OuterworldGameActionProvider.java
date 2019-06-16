package com.zygon.rl.common.controller;

import com.badlogic.gdx.Input;
import com.zygon.rl.common.model.CommonAttributes;
import com.zygon.rl.common.model.Entities;
import com.zygon.rl.common.model.Openable;
import com.zygon.rl.common.model.RegionHelper;
import com.zygon.rl.common.system.SimpleAI;
import com.zygon.rl.context.gdx.GDXInputAdapter;
import com.zygon.rl.core.model.Action;
import com.zygon.rl.core.model.Context;
import com.zygon.rl.core.model.Direction;
import com.zygon.rl.core.model.DoubleAttribute;
import com.zygon.rl.core.model.Entity;
import com.zygon.rl.core.model.Game;
import com.zygon.rl.core.model.Location;
import com.zygon.rl.core.model.Pair;
import com.zygon.rl.core.model.Region;
import com.zygon.rl.core.model.Regions;
import com.zygon.rl.core.system.GameSystem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;

/**
 *
 * @author zygon
 */
public class OuterworldGameActionProvider implements BiFunction<Action, Game, Game> {

    private static final String WHICH_DIRECTION = "which direction?";

    private final Set<Integer> gameInputs = new HashSet<>();
    private final GDXInputAdapter directionInputAdapter;
    private final GameSystem simpleAi = new SimpleAI();
    private final GrowthProcessor growthProcessor = new GrowthProcessor();
    private final Map<Direction, Long> growthIdsByDirection = new HashMap<>();

    {
        //TODO: Again, can't have gdx here

        // escape/quit
        gameInputs.add(Input.Keys.Q);
        // movement
        gameInputs.add(Input.Keys.H);
        gameInputs.add(Input.Keys.J);
        gameInputs.add(Input.Keys.K);
        gameInputs.add(Input.Keys.L);

        gameInputs.add(Input.Keys.NUMPAD_7);
        gameInputs.add(Input.Keys.NUMPAD_8);
        gameInputs.add(Input.Keys.NUMPAD_9);
        gameInputs.add(Input.Keys.NUMPAD_4);
        // 5??
        gameInputs.add(Input.Keys.NUMPAD_6);
        gameInputs.add(Input.Keys.NUMPAD_1);
        gameInputs.add(Input.Keys.NUMPAD_2);
        gameInputs.add(Input.Keys.NUMPAD_3);
        directionInputAdapter = new GDXInputAdapter(gameInputs);
    }

    @Override
    public Game apply(Action action, Game game) {
        Game newGame = game;

        switch (action.getName()) {
            case "QUIT":
                System.exit(0);
            case "CLOSE":
                Context closeCtx = Context.builder()
                        .setActionProvider(new DirectionActionProvider())
                        .setGameActionProvider(new CloseDirectionGameActionProvider())
                        .setInputSupplier(directionInputAdapter)
                        .setName("close")
                        .setDisplayName("Close " + WHICH_DIRECTION)
                        .build();
                newGame = newGame.copy()
                        .moveTime(6, TimeUnit.SECONDS)
                        .addContext(closeCtx)
                        .build();
                break;
            case "OPEN":
                Context openCtx = Context.builder()
                        .setActionProvider(new DirectionActionProvider())
                        .setGameActionProvider(new OpenDirectionGameActionProvider())
                        .setInputSupplier(directionInputAdapter)
                        .setName("open")
                        .setDisplayName("Open " + WHICH_DIRECTION)
                        .build();
                newGame = newGame.copy()
                        .moveTime(6, TimeUnit.SECONDS)
                        .addContext(openCtx)
                        .build();
                // TODO: need to set a text window
                break;
            case "MOVE":
                // Can result in bump-to-interact
                Pair<Regions, Integer> timeTakenByRegion = interactPlayer(action, game);
                Regions regions = timeTakenByRegion.getLeft();
                int timeTaken = timeTakenByRegion.getRight();

                // Game region growth: Uses parallel processing
                // Grow needs a "hard stop" and a "soft start" where the player
                // is somewhat nearby, we want to generate and hold the region
                Direction regionGrowthDirection = getRegionGrowthDirection(regions, 1);

                if (regionGrowthDirection != null) {
                    // If this throws an NPE, it's because the soft growth wasn't
                    // started, that's *probably* a bug.
                    long growthId = growthIdsByDirection.get(regionGrowthDirection);
                    Set<Region> growthRegions = growthProcessor.growRegions(growthId);
                    regions = finishGrowth(regions, regionGrowthDirection, growthRegions);
                }

                Direction regionGrowthStartDirection = getRegionGrowthDirection(regions, 4);

                if (regionGrowthStartDirection != null) {
                    // Check if growth is started, if not, start some
                    if (!growthIdsByDirection.containsKey(regionGrowthStartDirection)) {
                        long growthId = growthProcessor.growRegions(regions, regionGrowthStartDirection);
                        growthIdsByDirection.put(regionGrowthStartDirection, growthId);
                    } else {
                        long growthId = growthIdsByDirection.get(regionGrowthStartDirection);
                        if (growthProcessor.isDone(growthId)) {
                            Set<Region> growthRegions = growthProcessor.growRegions(growthId);
                            regions = finishGrowth(regions, regionGrowthStartDirection, growthRegions);
                        }
                    }
                }

                newGame = newGame.copy()
                        .moveTime(timeTaken, TimeUnit.SECONDS)
                        .setRegions(regions)
                        .build();
                break;
            case "EXAMINE":
                Context examineCtx = Context.builder()
                        .setActionProvider(new DirectionActionProvider())
                        .setGameActionProvider(new DirectionGameActionProvider())
                        .setInputSupplier(directionInputAdapter)
                        .setName("examine")
                        .setDisplayName("Examine " + WHICH_DIRECTION)
                        .build();
                newGame = newGame.copy()
                        .moveTime(6, TimeUnit.SECONDS)
                        .addContext(examineCtx)
                        .build();
                break;
            // TODO:
            //  tons of other stuff
        }

        // TBD: where should the systems be plugged in????
        newGame = simpleAi.getGameUpdate().apply(newGame);

        return newGame;
    }

    private Regions finishGrowth(Regions regions, Direction direction, Set<Region> newRegions) {
        for (Region region : newRegions) {
            regions = regions.add(region);
        }
        growthIdsByDirection.remove(direction);
        return regions;
    }

    /**
     * Returns a Direction if the region requires growth depending on the growth
     * factor times the region size.
     *
     * @param regions the regions to check
     * @param growthToleranceFactor the growth factor.
     * @return a Direction where growth is needed.
     */
    private Direction getRegionGrowthDirection(Regions regions, int growthToleranceFactor) {

        Location playerLoc = regions.find(Entities.PLAYER).stream().findAny().get();
        int growRegionTolerance = Regions.REGION_EDGE_SIZE * growthToleranceFactor;
        Direction regionGrowthDirection = null;

        // Logic to grow the region space
        if (regions.getMaxValues().getX() - playerLoc.getX() <= growRegionTolerance - 1) {
            regionGrowthDirection = Direction.EAST;
        }

        if (regions.getMaxValues().getY() - playerLoc.getY() <= growRegionTolerance - 1) {
            regionGrowthDirection = Direction.NORTH;
        }

        if (playerLoc.getX() - regions.getMinValues().getX() <= growRegionTolerance - 1) {
            regionGrowthDirection = Direction.WEST;
        }

        if (playerLoc.getY() - regions.getMinValues().getY() <= growRegionTolerance - 1) {
            regionGrowthDirection = Direction.SOUTH;
        }

        return regionGrowthDirection;
    }

    private boolean canMove(Location destination, Regions regions) {
        Region region = regions.getRegion(destination);

        List<Entity> entities = region.get(destination);
        return !entities.stream()
                .filter(e -> !e.getAttributes(CommonAttributes.IMPASSABLE.name()).isEmpty())
                .findAny().isPresent();
    }

    // Returns single element of Regions -> time taken
    private Pair<Regions, Integer> interactPlayer(Action action, Game game) {
        Regions regions = game.getRegions();
        Location playerLoc = regions.find(Entities.PLAYER).stream()
                .findAny().get();
        int nextX = playerLoc.getX();
        int nextY = playerLoc.getY();
        int nextZ = playerLoc.getZ();

        Direction dir = Direction.valueOf(action.getValue());

        switch (dir) {
            case HERE:
                // do nothing
                break;
            case NORTHEAST:
                nextX++;
                nextY++;
                break;
            case NORTHWEST:
                nextX--;
                nextY++;
                break;
            case SOUTHEAST:
                nextX++;
                nextY--;
                break;
            case SOUTHWEST:
                nextX--;
                nextY--;
                break;
            case WEST:
                nextX--;
                break;
            case EAST:
                nextX++;
                break;
            case NORTH:
                nextY--;
                break;
            case SOUTH:
                nextY++;
                break;
        }

        Location destination = Location.create(nextX, nextY, nextZ);
        int timeToInteract = 6;

        // Could become more robust "getActionsAtDestination"
        if (canMove(destination, regions)) {
            regions = regions.move(Entities.PLAYER, destination);

            // Find the entity with the highest terrain difficultly, if none, then no difficulty
            double terrainDifficulty = regions.get(destination).stream()
                    .map(e -> e.getAttributes(CommonAttributes.TERRAIN_DIFFICULTY.name()))
                    .map(attributes -> attributes.stream().mapToDouble(td -> DoubleAttribute.create(td).getDoubleValue()).max())
                    .mapToDouble(d -> d.orElse(0.0))
                    .max().orElse(0.0);

            timeToInteract += (int) (timeToInteract * terrainDifficulty);
        } else {
            // TODO: need to make this more robust to handle other types of
            // interacts, ie bump to attack
            OpenDirectionGameActionProvider openDirGameActionProv
                    = new OpenDirectionGameActionProvider();

            Entity openable = openDirGameActionProv.getOpenable(regions, destination);
            if (openable != null && new Openable(openable).isClosed()) {
                regions = openDirGameActionProv.handle(game, destination).getRegions();
            }
        }

        return Pair.create(regions, timeToInteract);
    }

    private static int calcSideRegions(Regions regions, Direction direction) {

        int max = -1;
        int min = -1;

        switch (direction) {
            case NORTH:
            case SOUTH:
                min = regions.getMinValues().getX();
                max = regions.getMaxValues().getX();
                break;
            case EAST:
            case WEST:
                min = regions.getMinValues().getY();
                max = regions.getMaxValues().getY();
                break;
        }

        return (max - min + 1) / Regions.REGION_EDGE_SIZE;
    }

    private static final class CloseDirectionGameActionProvider extends DirectionGameActionProvider {

        @Override
        protected Game handle(Game game, Location destination) {

            Regions regions = game.getRegions();
            game = super.handle(game, destination);

            // TBD: handle multiple "closeable" things in the same location?
            Entity closeable = regions.get(destination).stream()
                    .filter(e -> e.getAttributeValue(CommonAttributes.CLOSED.name()) != null)
                    .findAny().orElse(null);

            if (closeable != null) {
                Openable toClose = new Openable(closeable);
                if (!toClose.isClosed()) {
                    regions = regions.remove(closeable, destination);
                    closeable = toClose.close().getEntity();
                    regions = regions.add(closeable, destination);

                    game = game.copy()
                            .setRegions(regions)
                            .build();
                } else {
                    String log = closeable.getDisplayName() + " is already closed";
                    game = game.copy()
                            .addLog(log)
                            .build();
                }
            } else {
                String log = "Nothing to close";
                game = game.copy()
                        .addLog(log)
                        .build();
            }

            return game;
        }
    }

    private static final class OpenDirectionGameActionProvider extends DirectionGameActionProvider {

        @Override
        protected Game handle(Game game, Location destination) {
            Regions regions = game.getRegions();
            game = super.handle(game, destination);

            // TBD: handle multiple "openable" things in the same location?
            Entity openable = getOpenable(regions, destination);

            if (openable != null) {
                Openable toOpen = new Openable(openable);
                if (toOpen.isClosed()) {
                    regions = regions.remove(openable, destination);
                    openable = toOpen.open().getEntity();
                    regions = regions.add(openable, destination);

                    game = game.copy()
                            .setRegions(regions)
                            .build();
                } else {
                    String log = openable.getDisplayName() + " is already open";
                    game = game.copy()
                            .addLog(log)
                            .build();
                }
            } else {
                String log = "Nothing to open";
                game = game.copy()
                        .addLog(log)
                        .build();
            }

            return game;
        }

        Entity getOpenable(Regions regions, Location destination) {
            // TBD: handle multiple "openable" things in the same location?
            Entity openable = regions.get(destination).stream()
                    .filter(e -> e.getAttributeValue(CommonAttributes.CLOSED.name()) != null)
                    .findAny().orElse(null);

            return openable;
        }
    }

    private static final class GrowthProcessor {

        // How deep to pad, region wise
        private static final int PAD_DEPTH = 2;

        private final ExecutorService executor = Executors.newFixedThreadPool(4);
        private final RegionHelper regionHelper = new RegionHelper();
        private final Map<Long, Future<Set<Region>>> regionFuturesByGrowthId = new HashMap<>();
        private final AtomicLong growthIds = new AtomicLong();

        public long growRegions(final Regions regions, Direction direction) {
            Future<Set<Region>> regionGrowthFuture = executor.submit(() -> {
                Set<Region> newRegions = grow(regions, direction);
                return newRegions;
            });

            long growthId = growthIds.getAndIncrement();
            regionFuturesByGrowthId.put(growthId, regionGrowthFuture);
            return growthId;
        }

        // will block until done
        public Set<Region> growRegions(long growthId) {

            Future<Set<Region>> regionsFuture = regionFuturesByGrowthId.get(growthId);
            try {
                Set<Region> regions = regionsFuture.get();
                regionFuturesByGrowthId.remove(growthId);
                return regions;
            } catch (ExecutionException | InterruptedException e) {
                // TBD: log? throw runtime?
                // return null for now..
            }

            return null;
        }

        public boolean isDone(long growthId) {
            Future<Set<Region>> regionsFuture = regionFuturesByGrowthId.get(growthId);
            return regionsFuture.isDone();
        }

        // naive growth - just adds a full row or column on a side
        public synchronized Set<Region> grow(final Regions regions, Direction direction) {
            Set<Region> growthRegions = new HashSet<>();
            int numberOfRegions = calcSideRegions(regions, direction);

            switch (direction) {
                case NORTH: {

                    for (int depth = 0; depth < PAD_DEPTH; depth++) {
                        // Doesn't need to be multiplied by 'depth' - 'newRegions.getMaxValues()'
                        // is recalculated each time.
                        int newLocationY = regions.getMaxValues().getY() + 1;
                        int startingX = regions.getMinValues().getX();

                        // pads the NORTH side with 'numberOfRegions' regions
                        for (int i = 0; i < numberOfRegions; i++) {
                            if (i > 0) {
                                startingX += Regions.REGION_EDGE_SIZE;
                            }

                            Location loc = Location.create(startingX, newLocationY);
                            Region newRegion = regionHelper.generateRegion(loc,
                                    Regions.REGION_EDGE_SIZE, Regions.REGION_EDGE_SIZE, false);
                            growthRegions.add(newRegion);
                        }
                    }
                }
                break;
                case SOUTH: {
                    for (int depth = 0; depth < PAD_DEPTH; depth++) {
                        int newLocationY = regions.getMinValues().getY() - Regions.REGION_EDGE_SIZE;
                        int startingX = regions.getMinValues().getX();

                        // pads the SOUTH side with 'numberOfRegions' regions
                        for (int i = 0; i < numberOfRegions; i++) {
                            if (i > 0) {
                                startingX += Regions.REGION_EDGE_SIZE;
                            }

                            Location loc = Location.create(startingX, newLocationY);
                            Region newRegion = regionHelper.generateRegion(loc,
                                    Regions.REGION_EDGE_SIZE, Regions.REGION_EDGE_SIZE, false);
                            growthRegions.add(newRegion);
                        }
                    }
                }
                break;
                case EAST: {
                    for (int depth = 0; depth < PAD_DEPTH; depth++) {
                        int newLocationX = regions.getMaxValues().getX() + 1;
                        int startingY = regions.getMinValues().getY();

                        // pads the EAST side with 'numberOfRegions' regions
                        for (int i = 0; i < numberOfRegions; i++) {
                            if (i > 0) {
                                startingY += Regions.REGION_EDGE_SIZE;
                            }

                            Location loc = Location.create(newLocationX, startingY);
                            Region newRegion = regionHelper.generateRegion(loc,
                                    Regions.REGION_EDGE_SIZE, Regions.REGION_EDGE_SIZE, false);
                            growthRegions.add(newRegion);
                        }
                    }
                }
                break;
                case WEST: {
                    for (int depth = 0; depth < PAD_DEPTH; depth++) {
                        int newLocationX = regions.getMinValues().getX() - Regions.REGION_EDGE_SIZE;
                        int startingY = regions.getMinValues().getY();

                        // pads the WEST side with 'numberOfRegions' regions
                        for (int i = 0; i < numberOfRegions; i++) {
                            if (i > 0) {
                                startingY += Regions.REGION_EDGE_SIZE;
                            }

                            Location loc = Location.create(newLocationX, startingY);
                            Region newRegion = regionHelper.generateRegion(loc,
                                    Regions.REGION_EDGE_SIZE, Regions.REGION_EDGE_SIZE, false);
                            growthRegions.add(newRegion);
                        }
                    }
                }
                break;
            }

            return growthRegions;
        }
    }
}
