import processing.core.PApplet;

import javax.swing.*;
import java.util.ArrayList;

public class ObjectTracker implements PixelFilter, Clickable {
    public ArrayList<Short> reds = new ArrayList<>();
    public ArrayList<Short> greens = new ArrayList<>();
    public ArrayList<Short> blues = new ArrayList<>();
    public int k = 0;
    public int distanceThreshold = 40;
    public BoxBlur blur = new BoxBlur();
    private ArrayList<Cluster2D> clusters = new ArrayList<>();
    private ArrayList<Point> whitePoints = new ArrayList<>();

    public ObjectTracker() {
    }

    public void mouseClicked(int mouseX, int mouseY, DImage img) {
        short[][] redChannel = img.getRedChannel();
        short[][] greenChannel = img.getGreenChannel();
        short[][] blueChannel = img.getBlueChannel();
        reds.add(redChannel[mouseY][mouseX]);
        greens.add(greenChannel[mouseY][mouseX]);
        blues.add(blueChannel[mouseY][mouseX]);
        k++;
    }

    public void keyPressed(char key) {
        if (key == '+') {
            distanceThreshold += 5;
        }
        if (key == '-') {
            distanceThreshold -= 5;
        }
        if (key == 'r' && k > 0) {
            k--;
            clusters.remove(clusters.size() - 1);
            reds.remove(reds.size() - 1);
            greens.remove(greens.size() - 1);
            blues.remove(blues.size() - 1);
        }
    }

    public DImage processImage(DImage img) {

        if (k != 0) {
            short[][] bwGrid = img.getBWPixelGrid();
            thresholdObjectsBasedOnColor(img, bwGrid);
            img.setPixels(bwGrid);
            bwGrid = blur.processImage(img).getBWPixelGrid();
            thresholdAt245(bwGrid);


            KMeansClusteringOfWhitePixels(bwGrid);
            short[][] redChannel = img.getRedChannel();
            short[][] greenChannel = img.getGreenChannel();
            short[][] blueChannel = img.getBlueChannel();
            for (int r = 0; r < bwGrid.length; r++) {
                for (int c = 0; c < bwGrid[0].length; c++) {
                    if (bwGrid[r][c] < 100) {
                        redChannel[r][c] = 0;
                        greenChannel[r][c] = 0;
                        blueChannel[r][c] = 0;
                    }
                }
            }
            img.setColorChannels(redChannel, greenChannel, blueChannel);
        }
        return img;
    }

    private void KMeansClusteringOfWhitePixels(short[][] bwGrid) {
        addWhitePixelsToListOfPoints(bwGrid);
        setRandomClusters();
        boolean changed = true;
        do {
            addWhitePointsToTheirClosestCluster();
            for (int i = 0; i < clusters.size(); i++) {
                Cluster2D c = clusters.get(i);
                if (c.setCenterToAverageOfPoints() > 0) {
                    changed = true;
                } else {
                    changed = false;
                }
                c.clearAllPoints();
            }
            whitePoints.clear();
        } while (changed);
    }

    private void addWhitePointsToTheirClosestCluster() {
        for (int i = 0; i < whitePoints.size(); i++) {
            addPointToClosestCluster(whitePoints.get(i));
        }
    }

    private void addPointToClosestCluster(Point point) {
        Cluster2D closestCluster = clusters.get(0);
        for (int i = 1; i < clusters.size(); i++) {
            if (point.distanceToSquared(clusters.get(i).getCenter()) < point.distanceToSquared(closestCluster.getCenter())) {
                closestCluster = clusters.get(i);
            }
        }
        closestCluster.addPoint(point);
    }

    private void setRandomClusters() {
        if (whitePoints.size() > 0) {
            if (clusters.size() != k) {
                for (int i = 0; i < k - clusters.size(); i++) {
                    clusters.add(new Cluster2D(whitePoints.get((int) (Math.random() * whitePoints.size()))));
                }
            }
        }
    }

    public void addWhitePixelsToListOfPoints(short[][] bwGrid) {
        for (int r = 0; r < bwGrid.length; r++) {
            for (int c = 0; c < bwGrid[0].length; c++) {
                if (bwGrid[r][c] == 255) {
                    whitePoints.add(new Point(c, r));
                }
            }
        }
    }

    public void thresholdAt245(short[][] bwGrid) {
        for (int r = 0; r < bwGrid.length; r++) {
            for (int c = 0; c < bwGrid[0].length; c++) {
                if (bwGrid[r][c] < 245) {
                    bwGrid[r][c] = 0;
                } else {
                    bwGrid[r][c] = 255;
                }
            }
        }
    }

    public void thresholdObjectsBasedOnColor(DImage img, short[][] bwGrid) {
        short[][] redChannel = img.getRedChannel();
        short[][] greenChannel = img.getGreenChannel();
        short[][] blueChannel = img.getBlueChannel();
        for (int r = 0; r < redChannel.length; r++) {
            for (int c = 0; c < redChannel[0].length; c++) {
                boolean isChanged = false;
                for (int i = 0; i < reds.size(); i++) {
                    if (getDist(reds.get(i), greens.get(i), blues.get(i), redChannel[r][c], greenChannel[r][c], blueChannel[r][c]) < distanceThreshold) {
                        bwGrid[r][c] = 255;
                        isChanged = true;
                    }
                }
                if (!isChanged) {
                    bwGrid[r][c] = 0;
                }
            }
        }
    }

    public void drawOverlay(PApplet window, DImage original, DImage filtered) {
        for (int i = 0; i < clusters.size(); i++) {
            Point p = clusters.get(i).getCenter();
            window.fill(0, 255, 0);
            window.ellipse(p.getX(), p.getY(), 10, 10);
        }
    }

    public double getDist(int x1, int y1, int z1, int x2, int y2, int z2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (z1 - z2) * (z1 - z2));
    }

}
