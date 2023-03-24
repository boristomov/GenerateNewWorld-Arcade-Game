package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Engine {
    /* Feel free to change the width and height. */
    public static final int WIDTH = 60;
    public static final int HEIGHT = 40;
    public static final File CWD = new File(System.getProperty("user.dir"));
    static String avatarName = "you";
    static boolean gameStarted = false;
    //public static final File GAME_DIR = Utils.join(CWD, "GAME_DIR");
    static File savedGame = Utils.join(CWD, "saved_game.txt");
    static TETile costumeAvatar = Tileset.AVATAR;
    static boolean isNameChanged = false;
    static boolean isChanged = false;
    TERenderer ter = new TERenderer();

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, both of these calls:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public static TETile[][] interactWithInputString(String input) {
        //tells Quit game not to exit with code 0
        Menu.inputString = true;
        Menu.loadInputGame(input, false);

//        GamePlay.StartGame();
        return GamePlay.world;
    }

    public static TETile[][] interactWithInputString1(String input) {
        //  Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        TERenderer ter = new TERenderer(); //comment out for autograder
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = new TETile[WIDTH][HEIGHT - 3];
        Random rand = new Random(parseSeed(input));
        fillTheVoid(world); // fills with NOTHING tiles.
        generate(world, rand, new Position(3, 3));
        if (RandomUtils.uniform(rand) < 0.5) {
            flipWorld(world);
        }
//        colorBackground(world, rand);
        //cleanPerimeter(world);
        assignCoordinates(world, rand);
        placeAvatar(world, parseSeed(input));
        GamePlay.world = world;
        ter.renderFrame(world); //comment out for autograder
        return world;
    }

    /**
     * The heart of our world generation algorithm!
     * generate creates a room at Position pos and creates hallways as well
     * spanning outwards from this room. generate is then recursively called
     * at the "endpoint" positions for the hallways, and the same process
     * is continued... the logical ordering is as follows:
     * If we can build a room, then we call buildHallways, which randomly
     * decides to build either a north/east/L-shaped hallway.
     * At the end of the buildHallways function, assuming the hallway was
     * successfully constructed, we call generate, which creates a room, etc.
     *
     * @param world
     * @param rand
     * @param pos
     * @return
     */
    public static void generate(TETile[][] world, Random rand, Position pos) {
        int width = RandomUtils.uniform(rand, 4, 8);
        int height = RandomUtils.uniform(rand, 4, 7);
        Room room = new Room(width, height, pos);
        if (room.buildRoom(world, rand)) {
            room.buildHallways(world, rand);
        }
    }

    public static void colorBackground(TETile[][] world, Random rand) { //new start
        double random = rand.nextInt(20);
        for (int x = 0; x < Engine.WIDTH; x++) {
            for (int y = 0; y < Engine.HEIGHT - 3; y++) {
                if (world[x][y] != Tileset.GRASS && world[x][y] != Tileset.WALL
                        && x % 2 != 0 && y % 2 != 0 && world[x][y] != Tileset.FLOWER
                        && world[x][y] != costumeAvatar) {
                    if (random > 15) {
                        world[x][y] = Tileset.WATER;
                    } else if (random > 10) {
                        world[x][y] = Tileset.MOUNTAIN;
                    } else if (random > 5) {
                        world[x][y] = Tileset.FLOOR;
                    } else {
                        world[x][y] = Tileset.SAND;
                    }
                }
            }
        }
    }


    /**
     * Parse the input seed
     *
     * @param input string
     * @return parsed out numbers
     */
    public static long parseSeed(String input) {
        // returns seed as an integer based on input string (e.g. "N123S" returns 123)
        input = input.toLowerCase();
        for (int i = input.length() - 1; i >= 0; i--) {
            if (input.charAt(i) == 'n') {
                String res = "";
                for (int j = i + 1; j < input.length(); j++) {
                    if (input.charAt(j) != 's') {
                        res += input.charAt(j);
                    } else {
                        break;
                    }
                }
                return Long.parseLong(res);
            }
        }
        return 0;
    }

    /**
     * Fills our screen with nothing.
     *
     * @param world 2d array of existing TETiles of world
     */
    public static void fillTheVoid(TETile[][] world) {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT - 3; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    public static String getAvatarName() {
        return avatarName;
    }

    /**
     * Is called in interactWithInputString with 50% chance of flipping world
     * to add more psuedorandomness.
     *
     * @param world 2d array of existing TETiles of world.
     */
    public static void flipWorld(TETile[][] world) {
        for (int x = 0; x < WIDTH / 2; x++) {
            for (int y = 0; y < HEIGHT - 3; y++) {
                TETile temp = world[x][y];
                world[x][y] = world[WIDTH - 1 - x][y];
                world[WIDTH - 1 - x][y] = temp;
            }
        }
    }

    /**
     * Creates a list of all grass tiles and
     * adds corresponding Position identifiers to all elements.
     */
    public static List<TETile> generateGrassList(TETile[][] world) {
        List<TETile> grassTiles = new ArrayList<>();
        for (int x = 0; x < WIDTH - 1; x++) {
            for (int y = 0; y < HEIGHT - 4; y++) {
                world[x][y].coordinates = new Position(x, y);
                if (world[x][y] == Tileset.GRASS) {
                    grassTiles.add(world[x][y]);
                }
            }
        }
        return grassTiles;
    }

    /**
     * Assigns Position identifiers to all tiles.
     */
    public static void assignCoordinates(TETile[][] world, Random rand) {
        List<TETile> grassTiles = new ArrayList<>();
        double random = rand.nextInt(20);
        for (int x = 0; x < WIDTH - 1; x++) {
            for (int y = 0; y < HEIGHT - 4; y++) {
                world[x][y].coordinates = new Position(x, y);
                if (world[x][y] != Tileset.GRASS && world[x][y] != Tileset.WALL && x % 2 != 0
                        && y % 2 != 0 && world[x][y] != Tileset.FLOWER
                        && world[x][y] != costumeAvatar && world[x][y] != Tileset.CROSS) {
                    if (random > 15) {
                        world[x][y] = Tileset.WATER;
                    } else if (random > 10) {
                        world[x][y] = Tileset.MOUNTAIN;
                    } else if (random > 5) {
                        world[x][y] = Tileset.FLOOR;
                    } else {
                        world[x][y] = Tileset.SAND;
                    }
                }
            }
        }
    }

    /**
     * Places an Avatar tile in the world and returns its position.
     */
    public static Position placeAvatar(TETile[][] world, long seed) {
        List<TETile> grassTiles = generateGrassList(world);
        Random rand = new Random(seed);
        int randomIndex = RandomUtils.uniform(rand, 0, grassTiles.size());

        Position avatarPosition = grassTiles.get(randomIndex).coordinates;
        world[avatarPosition.getX()][avatarPosition.getY()] = costumeAvatar;
        GamePlay.avatarPosition = avatarPosition;
        return avatarPosition;
    }

    /**
     * Returns the name of the object under the mouse pointer.
     */
    public static String scanMousePosition(TETile[][] world) {
        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();

        if (x >= WIDTH || y >= HEIGHT - 3) {
            return "nothing";
        }
        TERenderer.mouse = "" + world[x][y].description();
        return world[x][y].description();
    }

    public static void visualizeHUD(TETile[][] world) {


        TERenderer ter = new TERenderer();
        ter.renderFrame(world, true);
        String typed = "";
        while (typed.length() < 0) {
            if (StdDraw.hasNextKeyTyped()) {
                typed += "a";

                break;
            }
        }


    }

    public static String heartHUD() {
        String hearts = "";
        for (int i = 0; i < GamePlay.health; i++) {
            hearts += "❤";
        }
        return hearts;
    }

    public static void main(String[] args) {
        Engine eng = new Engine();
        interactWithInputString("laaaa");
    }

    public static void textFixer() {
        String[] text = new String[9];
        text[0] = "";
        text[1] = "Welcome to Flower Frenzy! ";
        text[2] = "Since the beginning of "
                +
                "civilization humans have been gifting lovers, friends, "
                +
                "and family flowers as a symbol of appreciation and hope!";
        text[3] = " Now, you are tasked with collecting these"
                +
                "flowers in all different climates of the world";
        text[4] = "Each play of the game has a random background theme "
                +
                "and a set amount of flowers that must be collected.";

        text[5] = " Only one person has ever collected enough flowers on every different biome, "
                +
                "his name being Fredrick Flotus.";
        text[6] = "This took Fredrick many attempts as he had to wander "
                +
                "through many of the same biomes before finally";
        text[7] = "the law of averages came to his favor "
                +
                "and he saw each different location.";
        text[8] = "Bumping into the walls will reduce health "
                +
                "and could result in a loss so be precise in your movements!";
        Menu.drawText(text, 15);
    }

    public void cleanPerimeter(TETile[][] world) {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (x == 0 || x == WIDTH - 1
                        || y == 0 || y == HEIGHT - 1) {
                    if (world[x][y] == Tileset.GRASS) {
                        world[x][y] = Tileset.WALL;
                    }
                }
            }
        }
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
//        if(!GAME_DIR.exists()){
//            GAME_DIR.mkdir();
//        }

        String typed = "";
        Menu menuNew = new Menu();
        Menu.initialState(); //Renders the Menu Options before the user selects a command.
        while (typed.length() < 1) {
            if (StdDraw.hasNextKeyTyped()) {
                typed += StdDraw.nextKeyTyped();
            }
        }
        switch (typed.toUpperCase()) {
            case "N":
                Menu.newWorld();
                gameStarted = true;
                GamePlay.startGame();
                break;
            case "L":
                Menu.loadGame(false);
                System.out.println(GamePlay.counter);
                gameStarted = true;
                GamePlay.startGame();
                break;
            case "R":
                Menu.replayGame();
                gameStarted = true;
                GamePlay.startGame();
                break;
            case "C":
                Menu.changeAvatar();
                interactWithKeyboard();
                break;
            case "M":
                Menu.changeName();
                isNameChanged = true;
                interactWithKeyboard();
                break;
            case "T":
                Engine.textFixer();
                StdDraw.pause(10000);
                interactWithKeyboard();
                break;
            case "Q":
                Menu.quitGame();
                break;
            default:
                System.out.println("Please select one of the presented options!");
                System.exit(0);
        }
    }
}
