public class Point {
    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double distanceToSquared(Point other) {
        return (other.getY() - getY()) * (other.getY() - getY()) + (other.getX() - getX()) * (other.getX() - getX());
    }

    public void takeRandomStep() {
        int stepSize = 3;
        int rand = (int) (Math.random() * 4);
        if (rand == 0) {
            setX(getX() + stepSize);
        } else if (rand == 1) {
            setY(getY() + stepSize);
        } else if (rand == 2) {
            setX(getX() - stepSize);
        } else if (rand == 3) {
            setY(getY() - stepSize);
        }
    }

}
