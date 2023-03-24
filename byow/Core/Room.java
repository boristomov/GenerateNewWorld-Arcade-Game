package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

public class Room {
    private final int width;
    private final int height;
    private final Position bottomLeft;

    /**
     * Constructor for room.
     *
     * @param width      width of room.
     * @param height     height of room.
     * @param bottomLeft starting point of room building process.
     */
    public Room(int width, int height, Position bottomLeft) {
        this.width = width;
        this.height = height;
        this.bottomLeft = bottomLeft;
    }

    /**
     * Checks to make sure the Position for room is within bounds
     * and can be built (without overwriting other rooms/hallways).
     *
     * @param world 2d array of existing TETiles of world
     * @return rand
     */
    public boolean canBuild(TETile[][] world) {
        if (bottomLeft.getX() < 0 || bottomLeft.getY() < 0) {
            return false;
        }
        if (bottomLeft.getX() >= Engine.WIDTH
                || bottomLeft.getY() >= Engine.HEIGHT - 3) {
            return false;
        }
        if (bottomLeft.getX() + width >= Engine.WIDTH
                || bottomLeft.getY() + height >= Engine.HEIGHT - 3) {
            return false;
        }
        // checks to make sure tiles are available
        // and not occupied already by other rooms/hallways.
        for (int x = bottomLeft.getX() + 1; x < bottomLeft.getX() + width; x++) {
            for (int y = bottomLeft.getY() + 1; y < bottomLeft.getY() + height; y++) {
                if (!world[x][y].equals(Tileset.NOTHING)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Returns a random position along a rooms wall to allow us to have a
     * start point for a hallway.
     *
     * @param dir  A passed in direction that has a probability of being either
     *             North or East.
     * @param rand rand object
     * @return Position of edge point.
     */
    public Position getRandomEdgePoint(String dir, Random rand) {
        if (dir.equals("N")) {
            return new Position(bottomLeft.getX() + RandomUtils.uniform(
                    rand, 1, width), bottomLeft.getY() + height);
        } else if (dir.equals("S")) {
            return new Position(bottomLeft.getX() + RandomUtils.uniform(
                    rand, 1, width), bottomLeft.getY());
        } else if (dir.equals("E")) {
            return new Position(bottomLeft.getX() + width, bottomLeft.getY() + RandomUtils.uniform(
                    rand, 1, height));
        } else {
            return new Position(bottomLeft.getX(), bottomLeft.getY()
                    + RandomUtils.uniform(rand, 1, height));
        }
    }

    /**
     * Builds a rectangular room of random dimensions. Does not recursively call
     * back to generate in engine class, but
     *
     * @param world 2d array of existing TETiles of world
     * @param rand  rand
     * @return True if we build the room.
     */
    public boolean buildRoom(TETile[][] world, Random rand) {
        if (canBuild(world)) {
            double random = RandomUtils.uniform(rand);
            int xBL = bottomLeft.getX();
            int yBL = bottomLeft.getY();
            for (int x = xBL; x <= xBL + width; x += 1) {
                for (int y = yBL; y <= yBL + height; y += 1) {
                    if (x == xBL || x == xBL + width || y == yBL
                            || y == yBL + height && world[x][y] != Tileset.GRASS) {
                        world[x][y] = Tileset.WALL;
                    } else {
                        world[x][y] = Tileset.GRASS;

                    }
                    random = rand.nextInt(100); //new start
                    if (world[x][y] == Tileset.GRASS && random > 95) {
                        world[x][y] = Tileset.FLOWER;
                        GamePlay.flowers++;
                    } //new end
                    if (world[x][y] == Tileset.GRASS && random < 10) {
                        world[x][y] = Tileset.CROSS;
                    } //new end

                }
            }
            return true;
        }
        return false;
    }

    /**
     * Builds a Hallway going north. Called from generate with a
     * pre-determined probability. Then recursively calls back to generate from
     * the engine class if we could build hallway.
     *
     * @param world 2d array of existing TETiles of world
     * @param rand  rand
     */
    public void buildNorthHallway(TETile[][] world, Random rand) {
        int offset = RandomUtils.uniform(rand, 4, 9);
        Position north = getRandomEdgePoint("N", rand);
        if (north.getX() - 1 >= 0 && north.getX() + 1 < Engine.WIDTH
                && north.getY() + offset < Engine.HEIGHT - 3 && north.getY() > 0) {
            boolean occupied = false;
            for (int x = north.getX() - 1; x <= north.getX() + 1; x += 1) {
                for (int y = north.getY() + 1; y <= north.getY() + offset; y += 1) {
                    if (world[x][y] != Tileset.NOTHING) {
                        occupied = true;
                    }
                }
            }
            if (!occupied) {
                for (int x = north.getX() - 1; x <= north.getX() + 1; x += 1) {
                    for (int y = north.getY(); y <= north.getY() + offset; y += 1) {
                        if (x == north.getX() - 1 || x == north.getX() + 1) {
                            world[x][y] = Tileset.WALL;
                        } else {
                            world[x][y] = Tileset.GRASS;
                        }
                    }
                }
                Engine.generate(world, rand, new Position(north.getX()
                        - RandomUtils.uniform(rand, 2, 4), north.getY() + offset));
                if (world[north.getX()][north.getY() + offset] == Tileset.WALL) {
                    world[north.getX()][north.getY() + offset] = Tileset.GRASS;

                } else {
                    world[north.getX()][north.getY() + offset] = Tileset.WALL;
                }
            }
        }
    }

    /**
     * Builds a Hallway going east. Called from generate with a
     * pre-determined probability. Then recursively calls back to generate from
     * the engine class if we could build hallway.
     *
     * @param world 2d array of existing TETiles of world
     * @param rand  rand
     */
    public void buildEastHallway(TETile[][] world, Random rand) {
        Position east = getRandomEdgePoint("E", rand);
        int offset = RandomUtils.uniform(rand, 4, 8);
        boolean occupied = false; //checks to see if where about to draw hallway, wether or not
        if (east.getX() >= 0 && east.getX() + offset < Engine.WIDTH
                && east.getY() - 1 >= 0 && east.getY() + 1 < Engine.HEIGHT - 3) {
            for (int x = east.getX() + 1; x <= east.getX() + offset; x += 1) {
                for (int y = east.getY() - 1; y <= east.getY() + 1; y += 1) {
                    if (world[x][y] != Tileset.NOTHING) {
                        occupied = true;
                    }
                }
            }
            if (!occupied) {
                for (int x = east.getX(); x <= east.getX() + offset; x += 1) {
                    for (int y = east.getY() - 1; y <= east.getY() + 1; y += 1) {
                        if (y == east.getY() - 1 || y == east.getY() + 1) {
                            world[x][y] = Tileset.WALL;
                        } else {
                            world[x][y] = Tileset.GRASS;
                        }
                    }
                }
                Engine.generate(world, rand, new Position(east.getX()
                        + offset, east.getY() - RandomUtils.uniform(rand, 2, 4)));
                if (world[east.getX() + offset][east.getY()] == Tileset.WALL) {
                    world[east.getX() + offset][east.getY()] = Tileset.GRASS;
                } else {
                    world[east.getX() + offset][east.getY()] = Tileset.WALL;
                }
            }
        }
    }

    /**
     * Builds a perpendicular hallway. Called from generate with a
     * pre-determined probability. Then recursively calls back to generate from
     * the engine class if we could build hallway.
     *
     * @param world 2d array of existing TETiles of world
     * @param rand  rand
     */
    public void buildPerpendicularHallway(TETile[][] world, Random rand) {
        // first, build north hallway.
        int offset = RandomUtils.uniform(rand, 3, 6);
        Position north = getRandomEdgePoint("N", rand);
        if (north.getX() - 1 >= 0 && north.getX() + 1 < Engine.WIDTH
                && north.getY() + offset < Engine.HEIGHT - 3 && north.getY() > 0) {
            boolean occupied = false;
            for (int x = north.getX() - 1; x <= north.getX() + 1; x += 1) {
                for (int y = north.getY() + 1; y <= north.getY() + offset; y += 1) {
                    if (world[x][y] != Tileset.NOTHING) {
                        occupied = true;
                    }
                }
            }
            if (!occupied) {
                for (int x = north.getX() - 1; x <= north.getX() + 1; x += 1) {
                    for (int y = north.getY(); y <= north.getY() + offset; y += 1) {
                        if (x == north.getX() - 1 || x == north.getX() + 1) {
                            world[x][y] = Tileset.WALL;
                        } else {
                            world[x][y] = Tileset.GRASS;
                        }
                    }
                }
                Position east = new Position(north.getX()
                        + 2, north.getY() + offset);
                buildPHelper(world, rand, north, east, offset);
            }
        }
    }

    /**
     * Helper function to generate east portion of our L-shaped (perpendicular)
     * hallway. This function is created to satisfy the style checker, since
     * our functions cannot be over ~60 lines in length.
     *
     * @param world 2D world
     * @param r     Random obj
     * @param north starting position of north hallway (in L-hallway)
     * @param east  starting position of east hallway (in L-hallway)
     * @param off   original offset for our north hallway (ending point).
     */
    public void buildPHelper(TETile[][] world, Random r, Position north, Position east, int off) {
        int offsetE = RandomUtils.uniform(r, 4, 8);
        boolean occupied = false; //checks to see if where about to draw hallway, wether or not
        if (east.getX() >= 0 && east.getX() + offsetE < Engine.WIDTH
                && east.getY() - 1 >= 0 && east.getY() + 1 < Engine.HEIGHT - 3) {
            for (int x = east.getX() + 1; x <= east.getX() + offsetE; x += 1) {
                for (int y = east.getY() - 1; y <= east.getY() + 1; y += 1) {
                    if (world[x][y] != Tileset.NOTHING) {
                        occupied = true;
                    }
                }
            }
            if (!occupied) {
                for (int x = east.getX(); x <= east.getX() + offsetE; x += 1) {
                    for (int y = east.getY() - 1; y <= east.getY() + 1; y += 1) {
                        if (y == east.getY() - 1 || y == east.getY() + 1) {
                            world[x][y] = Tileset.WALL;
                        } else {
                            world[x][y] = Tileset.GRASS;
                        }

                    }
                }
                world[east.getX() - 1][east.getY()] = Tileset.GRASS;
                world[east.getX() - 1][east.getY() + 1] = Tileset.WALL;
                world[east.getX() - 2][east.getY() + 1] = Tileset.WALL;
                world[east.getX() - 3][east.getY() + 1] = Tileset.WALL;

                Engine.generate(world, r, new Position(east.getX()
                        + offsetE, east.getY() - RandomUtils.uniform(r, 2, 4)));
                if (world[east.getX() + offsetE][east.getY()] == Tileset.WALL) {
                    world[east.getX() + offsetE][east.getY()] = Tileset.GRASS;
                } else {
                    world[east.getX() + offsetE][east.getY()] = Tileset.WALL;
                }
            } else {
                world[north.getX()][north.getY() + off] = Tileset.WALL;
            }
        } else {
            world[north.getX()][north.getY() + off] = Tileset.WALL;
        }
    }

    /**
     * Is called after we build a room and is split into the three possible types
     * hallways. From the hallway endpoints we then call generate to try to build
     * another room, and the recursion ensues.
     *
     * @param world 2d array of existing TETiles of world
     * @param rand  rand
     */
    public void buildHallways(TETile[][] world, Random rand) {
        double random = RandomUtils.uniform(rand);
        if (random < 0.3) {
            buildNorthHallway(world, rand);
            buildEastHallway(world, rand);
        } else if (random < 0.7) {
            buildEastHallway(world, rand);
            buildNorthHallway(world, rand);
        } else {
            buildPerpendicularHallway(world, rand);
        }
    }
}
