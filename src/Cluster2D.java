import java.util.ArrayList;

public class Cluster2D {
    private Point center;
    private ArrayList<Point> points = new ArrayList<>();

    public Cluster2D(Point center) {
        this.center = center;
    }

    public Point getCenter() {
        return center;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Point> points) {
        this.points = points;
    }

    public void addPoint(Point p) {
        this.points.add(p);
    }

    public void clearAllPoints() {
        this.points.clear();
    }

    public double setCenterToAverageOfPoints() {
        double xSum = 0;
        double ySum = 0;
        double distance;
        for (int i = 0; i < points.size(); i++) {
            xSum += points.get(i).getX();
            ySum += points.get(i).getY();
        }
        if (getPoints().size() != 0) {
            double x = xSum / getPoints().size();
            double y = ySum / getPoints().size();
            double diffX = x - getCenter().getX();
            double diffY = y - getCenter().getY();
            distance = diffX * diffX + diffY * diffY;
            setCenter(new Point((int) x, (int) y));
        } else {
            distance = 0;
        }
        return distance;
    }

}
