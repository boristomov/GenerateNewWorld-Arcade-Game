package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.io.File;

/*
A class that deals with the avatar movement
 */
public class GamePlay {
    public static final File CWD = new File(System.getProperty("user.dir"));
    //    public static final File GAME_DIR = Utils.join(CWD, "GAME_DIR");
    static File savedGame = Utils.join(CWD, "saved_game.txt");

    static Position avatarPosition;

    static String record = "";

    static boolean replayR = false;

    static TETile[][] world;

    static int counter = 0;

    static int health = 5;

    static int flowers = 0;

    public static void W(boolean replay) {
        TERenderer ter = new TERenderer();
        int x = avatarPosition.getX();
        int y = avatarPosition.getY();
        if (world[x][y + 1] != Tileset.WALL) {
            if (world[x][y + 1] == Tileset.CROSS) {
                health--;
            }
            if (world[x][y + 1] == Tileset.FLOWER) {
                counter++;
            }
            world[x][y] = Tileset.GRASS;
            world[x][y + 1] = Engine.costumeAvatar;
            avatarPosition = new Position(x, y + 1);
        } else {
            health--;
        }
        if (replay) {
            ter.renderFrame(world);
            if (replayR) {
                StdDraw.pause(300);
            }
        }
    }

    public static void A(boolean replay) {
        TERenderer ter = new TERenderer();
        int x = avatarPosition.getX();
        int y = avatarPosition.getY();
        if (world[x - 1][y] != Tileset.WALL) {
            if (world[x - 1][y] == Tileset.CROSS) {
                health--;
            }
            if (world[x - 1][y] == Tileset.FLOWER) {
                counter++;
            }
            world[x][y] = Tileset.GRASS;
            world[x - 1][y] = Engine.costumeAvatar;
            avatarPosition = new Position(x - 1, y);
        } else {
            health--;
        }
        if (replay) {
            ter.renderFrame(world);
            if (replayR) {
                StdDraw.pause(300);
            }
        }
    }

    public static void S(boolean replay) {
        TERenderer ter = new TERenderer();
        int x = avatarPosition.getX();
        int y = avatarPosition.getY();
        if (world[x][y - 1] != Tileset.WALL) {
            if (world[x][y - 1] == Tileset.CROSS) {
                health--;
            }
            if (world[x][y - 1] == Tileset.FLOWER) {
                counter++;
            }
            world[x][y] = Tileset.GRASS;
            world[x][y - 1] = Engine.costumeAvatar;
            avatarPosition = new Position(x, y - 1);
        } else {
            health--;
        }
        if (replay) {
            ter.renderFrame(world);
            if (replayR) {
                StdDraw.pause(300);
            }
        }
    }

    public static void D(boolean replay) {
        TERenderer ter = new TERenderer();
        int x = avatarPosition.getX();
        int y = avatarPosition.getY();
        if (world[x + 1][y] != Tileset.WALL) {
            if (world[x + 1][y] == Tileset.CROSS) {
                health--;
            }
            if (world[x + 1][y] == Tileset.FLOWER) {
                counter++;
            }
            world[x][y] = Tileset.GRASS;
            world[x + 1][y] = Engine.costumeAvatar;
            avatarPosition = new Position(x + 1, y);

        } else {
            health--;
        }

        if (replay) {
            ter.renderFrame(world);
            if (replayR) {
                StdDraw.pause(300);
            }
        }
    }

    public static void Q() {
        if (savedGame.exists()) {
            savedGame.delete();
            File newVersion = Utils.join(CWD, "saved_game.txt");
            Utils.writeObject(newVersion, record);
        } else {
            Utils.writeObject(savedGame, record);
        }
        updateAvatar();
        updateName();
        Menu.quitGame();
    }

    public static void updateAvatar() {
        if (Menu.avCostume.exists()) {
            Menu.avCostume.delete();
            File newCostume = Utils.join(CWD, "avatar_costume.txt");
            Utils.writeObject(newCostume, Engine.costumeAvatar);
        } else {
            Utils.writeObject(Menu.avCostume, Engine.costumeAvatar);
        }
    }

    public static void updateName() {
        if (Menu.avName.exists()) {
            Menu.avName.delete();
            File newName = Utils.join(CWD, "avatar_name.txt");
            Utils.writeObject(newName, Engine.avatarName);
        } else {
            Utils.writeObject(Menu.avName, Engine.avatarName);
        }
    }

    public static void updateCount() {
        if (Menu.counterSave.exists()) {
            Menu.counterSave.delete();
            File newCounter = Utils.join(CWD, "counter.txt");
            Utils.writeObject(newCounter, GamePlay.counter);
        } else {
            Utils.writeObject(Menu.counterSave, GamePlay.counter);
        }
    }

    public static void updateHealth() {
        if (Menu.healthSave.exists()) {
            Menu.healthSave.delete();
            File newHealth = Utils.join(CWD, "health.txt");
            Utils.writeObject(newHealth, GamePlay.health);
        } else {
            Utils.writeObject(Menu.healthSave, GamePlay.health);
        }
    }

    public static void startGame() {
        while (true) {
            if (health < 1) {
                Menu.drawFrame("Game Over");
                StdDraw.pause(3000);
                System.exit(0);
            }
            if (counter == flowers) {
                Menu.drawFrame("You won!");
                StdDraw.pause(3000);
                System.exit(0);
            }
            String typed = "";
            while (typed.length() < 1) {
                Engine.visualizeHUD(world);
                if (StdDraw.hasNextKeyTyped()) {
                    typed += StdDraw.nextKeyTyped();
                    if (!typed.equals(":")) {
                        record += typed;
                    }
                }
            }
            switch (typed.toUpperCase()) {
                case "W":
                    W(true);
                    break;
                case "A":
                    A(true);
                    break;
                case "S":
                    S(true);
                    break;
                case "D":
                    D(true);
                    break;
                case ":":
                    String waitingForQ = "";
                    while (waitingForQ.length() < 1) {
                        if (StdDraw.hasNextKeyTyped()) {
                            waitingForQ += StdDraw.nextKeyTyped();
                        }
                    }
                    if (waitingForQ.equalsIgnoreCase("Q")) {
                        Q();
                    } else {
                        break;
                    }
                    break;
                default:
                    break;
            }
        }

    }

    public static String getCounter() {
        return Integer.toString(counter);
    }
}
