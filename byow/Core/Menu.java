package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Menu {


    public static final File CWD = new File(System.getProperty("user.dir"));
    static boolean gameStarted = false;
    //    public static final File GAME_DIR = Utils.join(CWD, "GAME_DIR");
    static File savedGame = Utils.join(CWD, "saved_game.txt");
    static File avCostume = Utils.join(CWD, "avatar_costume.txt");
    static File avName = Utils.join(CWD, "avatar_name.txt");
    static File counterSave = Utils.join(CWD, "counter.txt");
    static File healthSave = Utils.join(CWD, "health.txt");

    static boolean inputString = false;


    public Menu() {
        StdDraw.setCanvasSize(Engine.WIDTH * 16, Engine.HEIGHT * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, Engine.WIDTH);
        StdDraw.setYscale(0, Engine.HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

    }

    public static void drawFrame(String s) {
        /* Take the input string S and display it at the center of the screen,
         * with the pen settings given below. */
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(fontBig);
        StdDraw.text(Engine.WIDTH / 2, Engine.HEIGHT / 2, s);
        StdDraw.show();
    }

    public static void drawFrame(String[] s) {
        /** Take the input String array and display every element at
         *  a certain spacing in the center of the screen,
         * with the pen settings given below. */
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(fontBig);
        StdDraw.text(Engine.WIDTH / 2, Engine.HEIGHT / 2 + 5, s[0]);
        Font fontSmall = new Font("Monaco", Font.BOLD, 25);
        StdDraw.setFont(fontSmall);
        for (int i = 1; i < s.length; i++) {
            StdDraw.text(Engine.WIDTH / 2, Engine.HEIGHT / 2 - 2 * i, s[i]);
        }
        StdDraw.show();
    }

    public static void drawText(String[] s, int size) {
        /** Take the input String array and display every element at
         *  a certain spacing in the center of the screen,
         * with the pen settings given below. */
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontSmall = new Font("Monaco", Font.BOLD, size);
        StdDraw.setFont(fontSmall);
        for (int i = 0; i < s.length; i++) {
            StdDraw.text(Engine.WIDTH / 2, Engine.HEIGHT - 3 * i - 10, s[i]);
        }
        StdDraw.show();
    }

    /*
    USER INTERFACE BELOW
     */
    public static void initialState() {
        String[] options = new String[8];
        options[0] = "CS61B: THE GAME";
        options[1] = "New Game (N)";
        options[2] = "Load Game (L)";
        options[3] = "Replay Game (R)";
        options[4] = "Change Avatar (C)";
        options[5] = "Change Avatar Name (M)";
        options[6] = "LORE (T)";
        options[7] = "Quit Game (Q)";
        drawFrame(options);
    }

    public static void newWorld() {

        //// Tracks the seed inputted by the user.
        drawFrame("Enter a seed!");
        String typed = "n";
        String input = "";
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char var = StdDraw.nextKeyTyped();
                typed += var;
                if (var == 's' || var == 'S') {
                    break;
                }
                input += var;
                drawFrame(input);
            }
        }
        GamePlay.record += typed;
        Engine.interactWithInputString1(typed);
    }

    public static void loadGame(boolean replay) {
        if (avCostume.exists() && !Engine.isChanged) {
            Engine.costumeAvatar = Utils.readObject(avCostume, TETile.class);
        }

        String loadSeed;
        TERenderer ter = new TERenderer();
        if (savedGame.exists()) {
            loadSeed = Utils.readObject(savedGame, String.class);
            TETile[][] world = Engine.interactWithInputString1(loadSeed);

            List<Character> commands = commandParser(loadSeed.toUpperCase());
            for (Character i : commands) {
                String cast = "" + i;
                switch (cast.toUpperCase()) {
                    case "W":
                        GamePlay.W(replay);
                        break;
                    case "A":
                        GamePlay.A(replay);
                        break;
                    case "S":
                        GamePlay.S(replay);
                        break;
                    case "D":
                        GamePlay.D(replay);
                        break;
                    default:
                        break;
                }
            }
            GamePlay.record = loadSeed;
            ter.renderFrame(world);
        } else {
            System.exit(0);
        }
    }

    public static TETile[][] loadInputGame(String seed, boolean replay) {

        TERenderer ter = new TERenderer();
        if (seed.charAt(0) == 'l' || seed.charAt(0) == 'L') {
            if (savedGame.exists()) {
                String loadseed = Utils.readObject(savedGame, String.class);
                seed = loadseed + seed.substring(1);
            } else {
                return null;
            }
        }
        if (seed.charAt(seed.length() - 1) != 'Q' || seed.charAt(seed.length() - 1) != 'q') {
            GamePlay.record = seed;
        } else {
            GamePlay.record = seed.substring(0, seed.length() - 2);
        }
        TETile[][] world = Engine.interactWithInputString1(seed);
        List<Character> commands = commandParser(seed.toUpperCase());
        for (int i = 0; i < commands.size(); i++) {
            String cast = "" + commands.get(i);
            switch (cast.toUpperCase()) {
                case "W":
                    GamePlay.W(replay);
                    break;
                case "A":
                    GamePlay.A(replay);
                    break;
                case "D":
                    GamePlay.D(replay);
                    break;
                case "S":
                    GamePlay.S(replay);
                    break;
                case ":":
                    if (commands.get(i + 1) == 'Q') {
                        GamePlay.Q();
                    }
                    break;
                default:
                    break;
            }
        }
        ter.renderFrame(world);
        return GamePlay.world;
    }

    public static void replayGame() {
        GamePlay.replayR = true;
        loadGame(true);
//        quitGame();//for continued replay, comment out.
    }

    public static void costumeMenu() {
        String[] options = new String[5];
        options[0] = "Pick a costume!";
        options[1] = "@  (A)";
        options[2] = "$  (D)";
        options[3] = "♠  (T)";
        options[4] = "?  (Q)";
        drawFrame(options);
    }

    public static void changeAvatar() {
        costumeMenu();
        String costume = "";
        while (costume.length() < 1) {
            if (StdDraw.hasNextKeyTyped()) {
                costume += StdDraw.nextKeyTyped();
            }
        }
        switch (costume.toUpperCase()) {
            case "A":
                Engine.costumeAvatar = Tileset.AVATAR;
                Engine.isChanged = true;
                break;
            case "D":
                Engine.costumeAvatar = Tileset.DOLLAR;
                Engine.isChanged = true;
                break;
            case "T":
                Engine.costumeAvatar = Tileset.TREE;
                Engine.isChanged = true;
                break;
            case "Q":
                Engine.costumeAvatar = Tileset.QUESTION;
                Engine.isChanged = true;
                break;
            default:
                break;
        }
    }

    public static void changeName() {
        drawFrame("Enter avatar name!");
        String input = "";
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char var = StdDraw.nextKeyTyped();
                if (var == 's' || var == 'S') {
                    break;
                }
                input += var;
                drawFrame(input);
            }
        }
        Engine.avatarName = input;
    }

    public static TETile[][] quitGame() {
        if (!inputString) {
            System.exit(0);
        }
        return GamePlay.world;
    }


    /*
    HELPERS
     */
    private static List<Character> commandParser(String seed) {
        String commands = "" + Engine.parseSeed(seed);
        List<Character> commandList = new ArrayList<>();

        for (int i = commands.length() + 2; i < seed.length(); i++) {
            commandList.add(seed.charAt(i));
        }
        return commandList;
    }

}
