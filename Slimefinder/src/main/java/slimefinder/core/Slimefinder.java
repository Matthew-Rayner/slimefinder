package slimefinder.core;

import slimefinder.core.image.ImageGenerator;
import slimefinder.core.search.Search;
import slimefinder.io.CLI;
import slimefinder.io.DataLogger;
import slimefinder.io.properties.ImageProperties;
import slimefinder.io.properties.MaskProperties;
import slimefinder.io.properties.SearchProperties;

import java.io.IOException;
import java.util.Random;

public class Slimefinder {

    CLI cli;

    boolean search, images, help;

    public static void main(String[] args) {
        Slimefinder slimefinder = new Slimefinder();
        slimefinder.parseArguments(args);
        slimefinder.execute();
    }

    public Slimefinder() {
        cli = new CLI();
    }

    public void parseArguments(String[] args) {
        for (String arg : args) {
            switch (arg) {
                case "-i":
                    images = true;
                    break;
                case "-s":
                    search = true;
                    break;
                case "-h":
                    help = true;
                    break;
                default:
                    cli.warning("Invalid argument '" + arg + "'");
                    cli.flush();
                    help = true;
                    break;
            }
        }

        if (!search && !images) {
            help = true;
        }
    }

    public void execute() {
        if (help) {
            cli.helpMessage();
            return;
        }

        try {
            MaskProperties pMask = new MaskProperties("mask.properties", cli);
            if (search)  {
                SearchProperties pSearch = new SearchProperties("search.properties", cli);
                DataLogger logger = new DataLogger(pSearch, cli);
                Search searchMasks = new Search(pSearch, pMask, logger);
                runTask(searchMasks);
            }
            if (images) {
                ImageProperties pImage = new ImageProperties("image.properties", cli);
                ImageGenerator generateImgs = new ImageGenerator(pImage, pMask, cli);
                runTask(generateImgs);
            }
        } catch (IOException ex) {
        }
    }

    private void runTask(TrackableTask task) {
        cli.printStartInfo(task);
        Thread taskThread = new Thread(task);
        taskThread.start();
        try {
            do {
                cli.printProgress(task);
                Thread.sleep(50);
            } while (taskThread.isAlive());
        } catch (InterruptedException ie) {
        }
        cli.printEndInfo(task);
    }

    /**
     * This method is copied directly from the source code of Minecraft.
     * It determines which chunks are slime chunks.
     *
     * @param seed
     * @param xChunk - chunk x coordinate
     * @param zChunk - chunk z coordinate
     * @return true if (xChunk, zChunk) is a slime chunk, false otherwise
     */
    public static boolean isSlimeChunk(long seed, int xChunk, int zChunk) {
        Random r = new Random();
        r.setSeed(seed + (long) (xChunk * xChunk * 4987142) + (long) (xChunk * 5947611) + (long) (zChunk * zChunk) * 4392871L + (long) (zChunk * 389711) ^ 987234911L);
        return r.nextInt(10) == 0;
    }
}