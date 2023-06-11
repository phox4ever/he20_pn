package atl;

class Canvas {
    private final int x;
    private final int y;
    private int xMin;
    private int yMin;
    private final int xMax;
    private final int yMax;
    private int generation;
    private double timePerGeneration;
    private double averageTimePerGeneration;
    private long timeTotal;
    private int alive;
    private boolean dead;
    private int taskCount;
    final public static int ZOOM_NEAREST = 3;
    final public static int ZOOM_DEFAULT = 2;
    final public static int ZOOM_FARTHEST = 1;
    private int zoomLevel = ZOOM_DEFAULT;

    public Canvas(int x, int y, int xMin, int yMin, int xMax, int yMax) {
        this.x = x;
        this.y = y;
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;
        this.generation = 0;
        this.timePerGeneration = 0;
        this.averageTimePerGeneration = 0;
        this.timeTotal = 0;
        this.alive = 0;
        this.dead = false;
        this.taskCount = 1;
    }

    public int getXMin() {
        return xMin;
    }

    public int getYMin() {
        return yMin;
    }

    public int getxMax() {
        if (zoomLevel == ZOOM_FARTHEST) {
            return xMax;
        } else if (zoomLevel == ZOOM_DEFAULT) {
            return xMax;
        } else {
            return xMax / 2 + 1;
        }
    }

    public int getyMax() {
        if (zoomLevel == ZOOM_DEFAULT) {
            return yMax;
        } else if (zoomLevel == ZOOM_FARTHEST) {
            return yMax * 2;
        } else {
            return yMax / 2 - 4;
        }
    }

    public int getGeneration() {
        return generation;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }

    public double getTimePerGeneration() {
        return timePerGeneration;
    }

    public double getAverageTimePerGeneration() {
        return averageTimePerGeneration;
    }

    public void setTimePerGeneration(double timePerGeneration, int range) {
        int delta = Math.min(generation, range);
        // Calculate average time per generation, but only after 5 generations to avoid skewing the average.
        if (generation > 5) {
            this.averageTimePerGeneration = (averageTimePerGeneration * delta + timePerGeneration) / (delta + 1);
        }
        else {
            this.averageTimePerGeneration = timePerGeneration;
        }
        this.timePerGeneration = timePerGeneration;
    }

    public long getTimeTotal() {
        return timeTotal;
    }

    public String getTimeTotalString() {
        if (timeTotal > 10000) {
            return timeTotal / 1000 + "s";
        }
        return timeTotal + "ms";
    }

    public void setTimeTotal(long timeTotal) {
        this.timeTotal = timeTotal;
    }

    public int getAlive() {
        return alive;
    }

    public void setAlive(int alive) {
        this.alive = alive;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public int getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(int taskCount) {
        this.taskCount = taskCount;
    }

    public int getZoomLevel() {
        return zoomLevel;
    }
    public void moveUp() {
        if (xMin >= 10) {
            xMin = xMin - 10;
        }
        else {
            xMin = 0;
        }
    }

    public void moveDown() {
        if (xMin <= x - xMax - 11) {
            xMin = xMin + 10;
        }
        else {
            xMin = x - xMax + 1;
        }
    }

    public void moveLeft() {
        if (yMin >= 10) {
            yMin = yMin - 10;
        } else {
            yMin = 0;
        }

    }

    public void moveRight() {
        if (yMin <=  y - yMax - 11) {
            yMin = yMin + 10;
        }
        else {
            yMin = y - yMax - 1;
        }
    }

    public void zoomIn() {
        if (zoomLevel < ZOOM_NEAREST) {
            zoomLevel++;
        }
    }

    public void zoomOut() {
        if (zoomLevel > ZOOM_FARTHEST) {
            zoomLevel--;
        }
    }
}
