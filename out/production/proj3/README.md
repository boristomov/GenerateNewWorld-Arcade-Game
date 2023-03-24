# Build Your Own World Design Document

**Partner 1:** 

**Partner 2:**

## Classes and Data Structures
byow 
    -Core
        -Engine (Contains the two methods that allow interacting with your system.)
        -Main (How the user starts the entire system. Reads command line arguments and calls the appropriate function in Engine.java)
        -RandomUtils (Handy utility methods for doing randomness related things.)
        -Position (x,y coordinate)
        -Room (Class that encapsulates the logic of a room/hallway within the game.)
            -boolean isValidRoom()
                -checks to ensure generated room is not overlapping with
                -any other existing rooms, otherwise regenerate room params
                -such as width and height (along with shift too possibly from location)
            -void createRoom(int x, int y, int width, int height)
                -attempts to create a room centered at (x, y) with
                -dimensions width x height. calls isValidRoom to ensure
                -that room is valid based on current world state.
                -calls buildRoomWalls and buildRoomFloor.
            -void createHallway(x, y, width, height)
                -calls createRoom, but with a width apt for a hallway and length as well.
            -void getRandomEdgePoint(Room room)
                -gets a random edge point from the room passed in
                -this is used to create the initial position for outgoing hallways
                -from the current room. randomness rules apply as always.
            -void buildRoomWalls()
                -builds the walls for a room (made of WALL)
            -void buildRoomFloor()
                -builds the floor for a room (made of GRASS)
        -GenerateWorld
            -Random rand = new Random(seed);
                -use the Random object to generate random numbers to pass into
                -the calls to createRoom within recursivelyGenerate.
                -by using a seed, we guarantee that the final state of our
                -generated world is the same every time we run the program.
            -void recursivelyGenerate()
                -uses rand to generate width, height, and pos (x,y) values for rooms.
                -rand passed into various RandomUtils functions (provided for us)
                -and room/hallway sizes can be tweaked based on visual preference.
                -(e.g. width=10, height=10, x and y determined from previous call.)
                -create a room near the center of the tile map.
                -recursively try creating hallways (width of 3) 
                -on all four edges of the room... then, from the
                -endpoints of those hallways, call recursivelyGenerate
                -and repeat the process as long as valid rooms/hallways
                -can be constructed.
            -void fillTheVoid()
                -populates 2D Tile array with NOTHING tiles, since this has
                -to be done initially (our array contains null values at first).
    -Input Demo
    -TileEngine
        -TERenderer (contains rendering-related methods.)
        -TETile (the type used for representing tiles in the world.)
        -Tileset (a library of provided tiles.)
## Algorithms
    -Using the 2D array representing the TileMap, we will recursively generate rooms and hallways
    -beginning from the center of the map. Utilizing randomness for the sizes and existence of hallways
    -(e.g. some rooms may only have 2 outgoing edges



## Persistence
