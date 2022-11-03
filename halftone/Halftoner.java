import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Halftoner {
    private static final int CELL_SIZE = 16;

    /**
     * Read the file named args[0], process it, and write to the file
     * named args[1]; write a .png file, regardless of what the file
     * name says.
     */
    public static void main(String[] args) throws IOException {
        File readFile = new File(args[0]);
        File writeFile = new File(args[1]);
        doit(readFile, writeFile);
        System.exit(0);
    }

    private static void doit(File readFile, File writeFile)
        throws IOException {
        System.err.println("=== reading");

        BufferedImage image = ImageIO.read(readFile);
        int width = image.getWidth();
        int height = image.getHeight();
        int[] rgb = new int[width * height];

        System.err.println("=== getting");
        image.getRGB(0, 0, width, height, rgb, 0, width);
        System.err.println("=== processing");
        new Mechanism(rgb, width, height).process();
        System.err.println("=== setting");
        image.setRGB(0, 0, width, height, rgb, 0, width);
        System.err.println("=== writing");
        ImageIO.write(image, "png", writeFile);
    }

    private static class Mechanism {
        private final int[] rgb;
        private final int width;
        private final int height;
        private final int widthCells;
        private final int heightCells;
        private final int maxPossibleTotal;
        private final int patternCount;
        private final int[][] patterns;

        public Mechanism(int[] rgb, int width, int height) {
            this.rgb = rgb;
            this.width = width;
            this.height = height;
            widthCells = (width + CELL_SIZE - 1) / CELL_SIZE;
            heightCells = (height + CELL_SIZE - 1) / CELL_SIZE;
            maxPossibleTotal = CELL_SIZE * CELL_SIZE * (255 * 3);
            patternCount = CELL_SIZE * CELL_SIZE + 1;
            patterns = new int[patternCount][CELL_SIZE * CELL_SIZE];

            createPatterns();
        }

        public void process() {
            for (int y = 0; y < heightCells; y++) {
                for (int x = 0; x < widthCells; x++) {
                    processCell(x, y);
                }
            }
        }

        private void processCell(int x, int y) {
            int total = getCellTotal(x, y);
            int whichPattern = (total * patternCount) / (maxPossibleTotal + 1);
            putPatternInCell(patterns[whichPattern], x, y);
        }
        
        private int getCellTotal(int x, int y) {
            int baseX = x * CELL_SIZE;
            int baseY = y * CELL_SIZE;
            int maxX = CELL_SIZE;
            int maxY = CELL_SIZE;
            int total = 0;

            if ((baseX + maxX) > width) {
                maxX = width - baseX;
            }

            if ((baseY + maxY) > height) {
                maxY = height - baseY;
            }

            for (int y1 = 0; y1 < maxY; y1++) {
                int offset = (baseY + y1) * width + baseX;
                for (int x1 = 0; x1 < maxX; x1++) {
                    int one = rgb[offset + x1];
                    int r = one & 0xff;
                    int g = (one >> 8) & 0xff;
                    int b = (one >> 16) & 0xff;
                    total += r + g + b;
                }
            }

            return total;
        }

        private void putPatternInCell(int[] pattern, int x, int y) {
            int baseX = x * CELL_SIZE;
            int baseY = y * CELL_SIZE;
            int maxX = CELL_SIZE;
            int maxY = CELL_SIZE;

            if ((baseX + maxX) > width) {
                maxX = width - baseX;
            }

            if ((baseY + maxY) > height) {
                maxY = height - baseY;
            }

            for (int y1 = 0; y1 < maxY; y1++) {
                int offset = (baseY + y1) * width + baseX;
                int patOffset = y1 * CELL_SIZE;
                for (int x1 = 0; x1 < maxX; x1++) {
                    rgb[offset + x1] = pattern[patOffset + x1];
                }
            }
        }

        private void createPatterns() {
            createPatternsBoxes();
        }

        private void createPatternsBoxes() {
            int x = 0;
            int y = 0;
            int xLimit = CELL_SIZE;
            int mode = 0;

            for (int p = 1; p < patternCount; p++) {
                int[] pattern = patterns[p];
                System.arraycopy(patterns[p - 1], 0, patterns[p], 0,
                                 pattern.length);
                pattern[x + (y * CELL_SIZE)] = 0xffffff;
                if (mode == 0) {
                    x++;
                    if (x == xLimit) {
                        x--;
                        y++;
                        xLimit--;
                        mode = 1;
                    }
                } else {
                    y++;
                    if (y == CELL_SIZE) {
                        y = CELL_SIZE - x;
                        x = 0;
                        mode = 0;
                    }
                }
            }

            int floor = CELL_SIZE * 3;
            for (int p = 1; p < floor; p++) {
                patterns[p] = patterns[floor];
            }

            int ceil = patternCount - floor;
            for (int p = ceil; p < patternCount; p++) {
                patterns[p] = patterns[patternCount - 1];
            }
        }

        private void createPatternsTriangles() {
            int x = 0;
            int y = 0;

            for (int p = 1; p < patternCount; p++) {
                int[] pattern = patterns[p];
                System.arraycopy(patterns[p - 1], 0, patterns[p], 0,
                                 pattern.length);
                pattern[x + (y * CELL_SIZE)] = 0xffffff;
                x--;
                y++;
                if (y >= CELL_SIZE) {
                    y = x + 2;
                    x = CELL_SIZE - 1;
                } else if (x < 0) {
                    x = y;
                    y = 0;
                }
            }
        }

        private void createPatternsHorizontalScanlines() {
            for (int p = 1; p < patternCount; p++) {
                int[] pattern = patterns[p];
                System.arraycopy(patterns[p - 1], 0, patterns[p], 0,
                                 pattern.length);
                pattern[p - 1] = 0xffffff;
            }
        }
    }
}
