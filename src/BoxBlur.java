import processing.core.PApplet;

public class BoxBlur implements PixelFilter {
    private static final short[][] BOX_BLUR = {{1, 1, 1}, {1, 1, 1}, {1, 1, 1}};
    private static final short[][] SHARPEN = {{0, -1, 0}, {-1, 5, -1}, {0, -1, 0}};
    private static final short[][] GAUSSIAN_BLUR = {{1, 2, 1}, {2, 4, 2}, {1, 2, 1}};
    private static final short[][] GAUSSIAN_BLUR7_BY_7 = {{0, 0, 0, 5, 0, 0, 0}, {0, 5, 18, 32, 18, 5, 0}, {0, 18, 64, 100, 64, 64, 18, 0}, {5, 32, 100, 100, 100, 32, 5}, {0, 18, 64, 100, 64, 18, 0}, {0, 5, 18, 32, 18, 5, 0}, {0, 0, 0, 5, 0, 0, 0}};
    private static final short[][] PREWITT_EDGE = {{-1, -1, -1}, {-1, 8, -1}, {-1, -1, -1}};

    public BoxBlur() {
    }

    public DImage processImage(DImage img) {
        int n = 3;
        int kernelWeight = 0;
        short[][] filter = BOX_BLUR;
        for (int kr = 0; kr < n; kr++) {
            for (int kc = 0; kc < n; kc++) {
                kernelWeight += filter[kr][kc];
            }
        }
        short[][] bwpixels = img.getBWPixelGrid();
        short[][] newPixels = new short[bwpixels.length][bwpixels[0].length];
        for (int r = 0; r < bwpixels.length; r++) {
            for (int c = 0; c < bwpixels[r].length; c++) {
                newPixels[r][c] = bwpixels[r][c];
            }
        }
        for (int r = 0; r < bwpixels.length - n + 1; r++) {
            for (int c = 0; c < bwpixels[r].length - n + 1; c++) {
                int output = 0;
                for (int kr = 0; kr < n; kr++) {
                    for (int kc = 0; kc < n; kc++) {
                        int kernelVal = filter[kr][kc];
                        int pixelVal = bwpixels[r + kr][c + kc];
                        output += kernelVal * pixelVal;
                    }
                }
                if (kernelWeight != 0) output = output / (kernelWeight);

                if (output < 0) output = 0;
                short out = (short) output;
                newPixels[r + n / 2][c + n / 2] = out;
            }
        }
        img.setPixels(newPixels);
        return img;
    }

    @Override
    public void drawOverlay(PApplet window, DImage original, DImage filtered) {

    }
}
