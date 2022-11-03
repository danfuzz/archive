import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Halftoner {
    private static final int CELL_SIZE = 9;
    
    public static void main(String[] args) throws IOException {
        File readFile = new File(args[0]);
        File writeFile = new File(args[1]);
        doit(readFile, writeFile);
    }

    private static void doit(File readFile, File writeFile)
        throws IOException {
        System.err.println("=== reading");

        BufferedImage image = ImageIO.read(readFile);
        int width = image.getWidth();
        int height = image.getHeight();
        int[] rgb = new int[width * height];

        System.err.println("=== " + width + "x" + height);
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
            try {
                putPatternInCell(patterns[whichPattern], x, y);
            } catch (RuntimeException ex) {
                System.err.println("=== total " + total);
                System.err.println("=== pcount " + patternCount);
                System.err.println("=== max " + maxPossibleTotal);
                throw ex;
            }
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
            for (int p = 1; p < patternCount; p++) {
                int[] pattern = patterns[p];
                System.arraycopy(patterns[p - 1], 0, patterns[p], 0,
                                 pattern.length);
                pattern[p - 1] = 0xffffff;
            }
        }
    }
}
